package threads;

import org.hamcrest.core.IsSame;

import com.sun.org.apache.bcel.internal.generic.IREM;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Single;

import enums.SensorNames;
import exceptions.serial.SerialConnexionException;
import robot.cardsWrappers.SensorsCardWrapper;
import table.Table;
import robot.RobotReal;
import utils.Sleep;
import smartMath.Vec2;

/**
 * Thread qui ajoute en continu les obstacles détectés par les capteurs.
 *
 * @author pf, Krissprolls, marsu, paul, pingu
 */

class ThreadSensor extends AbstractThread
{
	/** La table */
	private Table mTable;
	
	/** Le robot */
	private RobotReal mRobot;

	/** La carte capteurs avec laquelle on doit communiquer */
	private SensorsCardWrapper mSensorsCardWrapper;
	
	// Valeurs par défaut s'il y a un problème de config
	
	/** fréquence de mise a jour des valeurs renvoyés par les capteurs. Valeurs par défaut de 5 fois par seconde s'il y a un problème de config */
	private int sensorFrequency = 17;
	
	/**
	 * distance en mm entre les capteur ultrasond et le guide en plastique, 
	 * on ne peut rien detecter de plus petit que cette distance donc toutes les informations de distance en dessous de cette valeur ne seron pas traités
	 */
	int distanceBetweenGuideAndUltrasound = 80;
	/**
	 * distance en mm entre les deux capteurs avants
	 */
	int distanceBetweenFrontSensors = 260;
	/**
	 * distance en mm entre le deux capterus arrieres
	 */
	int distanceBetweenBackSensors = 170;

	/**
	 * Distance maximale fiable pour les capteurs : au dela, valeurs abberentes
	 */
	double maxSensorRange;
	
	/**
	 *  Angle de visibilité qu'a le capteur 
	 */
	double detectionAngle;
	
	/**
	 * Distance minimale à laquelle on peut se fier aux capteurs : ne pas detecter notre propre root par exemple
	 */
	double minSensorRange = 80;
	
	/**
	 *  Booleen explicitant si addObstacle a en effet ajouté un obstacle
	 */
	boolean obstacleAddedLeft;
	boolean obstacleAddedRight;

	
	/** Les angles des capteurs :
	 * 		
	 * 			
	 * 			1		2
	 * 			\\ ____//
	 * 			|		|
	 * 			|       |
	 * 			|_______|	
	 * 			//	    \\
	 * 			1		2
	 * 
	 * 
	 * Calcul de l'angle :
	 * 
	 *   |angle/ \angle|		 
	 * 	 |	  /	  \    |		 
	 * 	 |	 /	   \   |		
	 * 	 |	/	    \  |		
	 * 	 |o/         \o|		
	 * 		  Robot			o : capteur
	 * 
	 */
	
	/**
	 * Angles en degrés, transformés en radians
	 */
	double leftFrontSensorAngle=10		*2*(Math.PI) / 360;
	double rightFrontSensorAngle=10 	*2*(Math.PI) / 360;
	double leftBackSensorAngle=0		*2*(Math.PI) / 360;
	double rightBackSensorAngle=0		*2*(Math.PI) / 360;
	
	/** 
	 *  position des capteurs relativement au centre du robot, en mm
	 */
	Vec2 rightFrontSensorPosition = new Vec2(5, 15);
	Vec2 leftFrontSensorPosition = new Vec2(5, -15);
	Vec2 rightBackSensorPosition = new Vec2(-5, 15);
	Vec2 leftBackSensorPosition = new Vec2(-5, -15);
	
	/**
	 * Sauvegarde de la position ennemie
	 */
	private Vec2 relativePosEnnemi1 = new Vec2(0,0);
	private Vec2 relativePosEnnemi2 = new Vec2(0,0);

	
	/**
	 * taille du rayon d'un obstacle
	 */
	private int radius;
	
	/**
	 * Largeur du robot recuperée sur la config
	 */
	int robotWidth;
	
	/**
	 * 	Longueur du robot recuperée sur la config
	 */
	int robotLenght;
	
	/**
	 * Positions des robots à ajouter
	 */
	Vec2 positionEnnemi_1=new Vec2 (0,0);
	Vec2 positionEnnemi_2=new Vec2 (0,0);


	
	
	/**
	 * Crée un nouveau thread de capteurs
	 *
	 * @param table La table a l'intérieure de laquelle le thread doit croire évoluer
	 * @param sensorsCardWrapper La carte capteurs avec laquelle le thread va parler
	 */
	ThreadSensor (Table table, RobotReal robot, SensorsCardWrapper sensorsCardWrapper)
	{
		super(config, log);
		this.mSensorsCardWrapper = sensorsCardWrapper;
		Thread.currentThread().setPriority(2);
		mTable = table;
		mRobot = robot;
	}
	
	/* (non-Javadoc)
	 * @see threads.AbstractThread#run()
	 */
	@Override
	public void run()
	{
		log.debug("Lancement du thread de capteurs", this);
		updateConfig();
		
		
		// boucle d'attente de début de match
		//TODO : decommenter
		/*
		while(!ThreadTimer.matchStarted)
		{
			if(stopThreads)
			{
				log.debug("Stoppage du thread capteurs", this);
				return;
			}
			Sleep.sleep(50);
		}
		*/
		
		
		// boucle principale, celle qui dure tout le match
		log.debug("Activation des capteurs", this);
		while(!ThreadTimer.matchEnded)
		{
			// on s'arrete si le ThreadManager le demande
			if(stopThreads)
			{
				log.debug("Stoppage du thread capteurs", this);
				return;
			}
			
			//if(!mRobot.getIsRobotTurning())// on ne detecte pas si on est en train de tourner (les capteurs font des choses etranges sinon.)
			{
				/* recupere la distance mesurée par l'ultrason
				 * on met la distance detecte, a l'avant et a l'arriere, dans deux variables int[] de taille deux
				 * si la carte ne repond pas on revoie la valeur par default
				 */
				int[] distanceFront = getDistanceFront();
				int[] distanceBack = getDistanceBack();
				
				if(! (distanceFront[0]==-1 || 
					  distanceFront[1]==-1)) // si on n'a pas spammé
				{										
					// on enleve les obstacles 
					removeObstacleFront(distanceFront);

					//ajout d'obstacles mobiles dans l'obstacleManager
					// Analyse des capteurs avant, avec gestion des angles
					addObstacleFront(distanceFront);
					// Analyse descapt	eurs arrieres, avec gestion des angles
					addObstacleBack(distanceBack);
					
				}
					
				
				log.debug("Distance selon ultrasons avant : "+distanceFront[0]+";"+distanceFront[1], this); 
				//log.debug("Distance selon ultrasons arriere: "+distanceBack[0]+";"+distanceBack[1], this);
				
				if (distanceFront[1] > 0 && distanceFront[1] < 70 || distanceFront[0] > 0 && distanceFront[0] < 70)
					log.debug("obstacle detecte a moins de 7 cm !", this);
				
			}
			Sleep.sleep((long)(1000./sensorFrequency));
			
		}
        log.debug("Fin du thread de capteurs", this);
		
	}
	
	/**
	 * ajoute les obstacles avant a l'obstacleManager 
	 * @param distanceFront l'int[] recupere par la serie pour les capteurs avants
	 * a modifier si ajout ou supression de capteurs
	 */
	private boolean addObstacleFront(int[] distanceFront) 
	{
		Vec2 positionRobot = mRobot.getPosition();
		double orientation = mRobot.getOrientation();
		obstacleAddedRight=false;
		obstacleAddedLeft=false;

		
		/** Zones de detection : 0;1;2 capteurs dans leurs zones respectives
		 * 
		 * 		______ ___
		 * 		|	  |\1/\	
		 * 		|	  |0X2 )
		 *  	|_____|/1\/
		 * 
		 */
		
		/**
		 * Distances lues par les capteurs PLUSS le rayon d'un robot pour viser le centre
		 */
		int[] distanceObstacleFront={0,0};
		
		if(!(distanceFront[0]==0))
			distanceObstacleFront[0]=distanceFront[0]+radius;
		else
			distanceObstacleFront[0]=0;
		
		if(!(distanceFront[1]==0))
			distanceObstacleFront[1]=distanceFront[1]+radius;
		else
			distanceObstacleFront[1]=0;

		
		//0 gauche / 1 à droite
		// si les 2 capteurs detectent quelque chose
		if ((minSensorRange<distanceFront[0] && distanceFront[0]<maxSensorRange) && (minSensorRange<distanceFront[1] && distanceFront[1]<maxSensorRange) )
		{
			// Si on voit 2 ennemis distincts
			if(Math.abs(distanceFront[1]-distanceFront[0]) > distanceBetweenFrontSensors)
			{
				// Coté gauche : 
				// relatif
				positionEnnemi_1.x= (int) (distanceFront[0]*Math.cos(leftFrontSensorAngle) + leftFrontSensorPosition.x);
				positionEnnemi_1.y= (int) (distanceFront[0]*Math.sin(leftFrontSensorAngle) + distanceBetweenFrontSensors/2);
				
				// sauvegarde de la position relative
				relativePosEnnemi1.x=positionEnnemi_1.x;
				relativePosEnnemi1.y=positionEnnemi_1.y;
			
				// On change de repere 
				positionEnnemi_1=changeReference(relativePosEnnemi1, positionRobot, orientation );
					
				// Coté droit :
				// relatif au robot
				positionEnnemi_2.x=		(int) (distanceFront[1]*Math.cos(rightFrontSensorAngle) +rightFrontSensorPosition.x);
				positionEnnemi_2.y=    -(int) (distanceFront[1]*Math.sin(rightFrontSensorAngle) +distanceBetweenFrontSensors/2);
				
				// sauvegarde de la position relative
				relativePosEnnemi2.x=positionEnnemi_2.x;
				relativePosEnnemi2.y=positionEnnemi_2.y;
				
				// On change de repere 
				positionEnnemi_2=changeReference(relativePosEnnemi2, positionRobot, orientation );
				
				mTable.getObstacleManager().addObstacle(positionEnnemi_1);
				mTable.getObstacleManager().addObstacle(positionEnnemi_2);
				
				obstacleAddedRight=true;
				obstacleAddedLeft=true;

			}
			// sinon, on voit un seul et meme ennemi
			else  
			{			
				positionEnnemi_1.y =  (int)( 	(Math.pow(distanceObstacleFront[0],2)-Math.pow(distanceObstacleFront[1],2))
												/(2 * distanceBetweenFrontSensors));	//position de l'obstacle en fonction du robot
				
				
				
				positionEnnemi_1.x =  (int)(	rightFrontSensorPosition.x/2 + 
												Math.sqrt( Math.pow(distanceObstacleFront[1],2)-
														   Math.pow(positionEnnemi_1.y-distanceBetweenFrontSensors/2, 2)));	//position de l'obstacle en fonction du robot

				relativePosEnnemi1.x=positionEnnemi_1.x;
				relativePosEnnemi1.y=positionEnnemi_1.y;
				
				// Maintenant, on le remet dans le repere du robot
				positionEnnemi_1=changeReference(relativePosEnnemi1, positionRobot, orientation );

				mTable.getObstacleManager().addObstacle(positionEnnemi_1);

				obstacleAddedRight=true;
				obstacleAddedLeft=true;
			}			
		}
		else // Sinon, un seul des deux capteurs detecte quelque chose
		
		// Capteur du cote gauche
		if (minSensorRange<distanceFront[0] && distanceFront[0]<maxSensorRange)
		{			
			// relatif
			positionEnnemi_1.x=  (int) (distanceObstacleFront[0]*Math.cos(leftFrontSensorAngle) +leftFrontSensorPosition.x);
			positionEnnemi_1.y= -(int) (distanceObstacleFront[0]*Math.sin(leftFrontSensorAngle) +distanceBetweenFrontSensors/2);
			
			// sauvegarde de la position relative
			relativePosEnnemi1.x=positionEnnemi_1.x;
			relativePosEnnemi1.y=positionEnnemi_1.y;
			
			// On change de repere 
			positionEnnemi_1=changeReference(relativePosEnnemi1, positionRobot, orientation );
			
			mTable.getObstacleManager().addObstacle(positionEnnemi_1);
			
			obstacleAddedLeft=true;

		}
		// Capteur de coté droit
		else if (minSensorRange<distanceFront[1] && distanceFront[1]<maxSensorRange)
		{			
			// relatif au robot
			positionEnnemi_1.x=  (int) (distanceObstacleFront[1]*Math.cos(rightFrontSensorAngle) +rightFrontSensorPosition.x);
			positionEnnemi_1.y=  (int) (distanceObstacleFront[1]*Math.sin(rightFrontSensorAngle) +distanceBetweenFrontSensors/2);
			
			// sauvegarde de la position relative
			relativePosEnnemi1.x=positionEnnemi_1.x;
			relativePosEnnemi1.y=positionEnnemi_1.y;
			
			// On change de repere 
			positionEnnemi_1=changeReference(relativePosEnnemi1, positionRobot, orientation );
			
			mTable.getObstacleManager().addObstacle(positionEnnemi_1);
			
			obstacleAddedRight=true;
		}
		return (obstacleAddedRight || obstacleAddedLeft);
	}

	/**
	 * ajoute les obstacles arrieres a l'obstacleManager FIXME a changer
	 * @param distanceBack l'int[] recuperé par la serie pour les capteurs arrieres
	 * a modifier si ajout ou supression de capteurs
	 */
	private void addObstacleBack(int[] distanceBack) 
	{
		if ((0<distanceBack[0] && distanceBack[0]<maxSensorRange) && (0<distanceBack[1] && distanceBack[1]<maxSensorRange)) // les deux capteurs detectent, on est dans la zone de double detection et on peut placer precesement l'obstacle
		{
			//debrouillez vous, faites le calcul (le systeme c'est {x²+y²=distanceBack[0]² ;(L-x)²+y²= distanceBack[1]²})
			
			mTable.getObstacleManager().addObstacle(new Vec2((int)(mRobot.getPosition().x + Math.pow(distanceBack[0],2)-Math.pow(distanceBack[1],2))/(2 * distanceBetweenBackSensors),
																   mRobot.getPosition().y + (int)(robotLenght/2 + Math.pow(Math.pow(Math.pow(distanceBetweenBackSensors,2)+Math.pow(distanceBack[0],2)+Math.pow(distanceBack[1],2), 2)/(4 * Math.pow(distanceBetweenBackSensors, 2)), 0.5))));
		}
		else if (0<distanceBack[0] && distanceBack[0]<maxSensorRange)// Capteur du cote gauche
		{
			mTable.getObstacleManager().addObstacle(new Vec2(mRobot.getPosition().x + (int)(rightBackSensorPosition.x*Math.cos(mRobot.getOrientation()) - rightBackSensorPosition.y*Math.sin(mRobot.getOrientation())) - (int)((distanceBack[0]+radius)*Math.cos(mRobot.getOrientation() - rightBackSensorAngle)), 
															 mRobot.getPosition().y + (int)(rightBackSensorPosition.x*Math.sin(mRobot.getOrientation()) + rightBackSensorPosition.y*Math.cos(mRobot.getOrientation())) - (int)((distanceBack[0]+radius)*Math.sin(mRobot.getOrientation() - rightBackSensorAngle))));
		}
		else if (0<distanceBack[1] && distanceBack[1]<maxSensorRange)// Capteur de coté droit
		{
			mTable.getObstacleManager().addObstacle(new Vec2(mRobot.getPosition().x + (int)(leftBackSensorPosition.x*Math.cos(mRobot.getOrientation()) - leftBackSensorPosition.y*Math.sin(mRobot.getOrientation())) - (int)((distanceBack[1]+radius)*Math.cos(mRobot.getOrientation() + leftBackSensorAngle )), 
															 mRobot.getPosition().y + (int)(leftBackSensorPosition.x*Math.sin(mRobot.getOrientation()) + leftBackSensorPosition.y*Math.cos(mRobot.getOrientation())) - (int)((distanceBack[1]+radius)*Math.sin(mRobot.getOrientation() + leftBackSensorAngle ))));
		}
	}


	/**
	 * 
	 * @return la distance selon les ultrasons avants, [gauche, droite]
	 */
	private int[] getDistanceFront() 
	{
		int[] distanceFront;
		try 
		{
			distanceFront = (int[]) mSensorsCardWrapper.getSensorValue(SensorNames.ULTRASOUND_FRONT_SENSOR);
			
			log.debug("Distance selon ultrasons avant traitement : "+distanceFront[0]+";"+distanceFront[1], this); 

			//on met tout les capteurs qui detectent un objet DANS le robot ou à plus de maxSensorRange a 0
			for (int i=0; i<distanceFront.length; i++)
			{
				if(distanceFront[i]==0)
				{
					log.critical("ARRETEZ DE SPAMMER LES CAPTEURS !", this);
					distanceFront[i]=-1;
				}
				if ( distanceFront[i] <distanceBetweenGuideAndUltrasound && 
					 distanceFront[i] !=-1 )
				{
					distanceFront[i]=0;
				}
				if ( distanceFront[i] > maxSensorRange) 
				{
					distanceFront[i]=0;
				}
			}
		}
		catch(SerialConnexionException e)
		{
			log.critical("La carte capteurs ne répond pas !", this);
			e.printStackTrace();
			distanceFront = (int[]) SensorNames.ULTRASOUND_FRONT_SENSOR.getDefaultValue();
		}
		return distanceFront;
	}
	
	/**
	 * 
	 * @return la distance selon les ultrasons arrieres, [gauche, droite]
	 */
	private int[] getDistanceBack() 
	{
		int[] distanceBack;
		try 
		{
			distanceBack = (int[]) mSensorsCardWrapper.getSensorValue(SensorNames.ULTRASOUND_BACK_SENSOR);
			//on met tout les capteurs qui detectent un objet à plus de maxSensorRange a 0
			for (int i=0; i<distanceBack.length; i++)
			{
				if(distanceBack[i]==0)
					log.critical("ARRETEZ DE SPAMMER LES CAPTEURS !", this);
				if (distanceBack[i]<distanceBetweenGuideAndUltrasound || distanceBack[i] > maxSensorRange) 
					distanceBack[i]=0;
			}
		}
		catch (SerialConnexionException e)
		{
			log.critical("La carte capteurs ne répond pas !", this);
			e.printStackTrace();
			distanceBack = (int[]) SensorNames.ULTRASOUND_BACK_SENSOR.getDefaultValue();
		}
		return distanceBack;
	}


	/* (non-Javadoc)
	 * @see threads.AbstractThread#updateConfig()
	 */
	public void updateConfig()
	{
		sensorFrequency = Integer.parseInt(config.getProperty("capteurs_frequence"));
		radius = Integer.parseInt(config.getProperty("rayon_robot_adverse"));
		
		//plus que cette distance (environ 50cm) on est beaucoup moins precis sur la position adverse (donc on ne l'ecrit pas !)
		maxSensorRange = Integer.parseInt(config.getProperty("largeur_robot"))
						 / Math.sin(Float.parseFloat(config.getProperty("angle_capteur")));
		
		detectionAngle=Float.parseFloat(config.getProperty("angle_capteur"));
		
		robotWidth = Integer.parseInt(config.getProperty("largeur_robot"));
		robotLenght = Integer.parseInt(config.getProperty("longueur_robot"));
	}
	
	/**
	 * Changements de reperes :
	 * @param relativePosEnnemi
	 * @param positionRobot
	 * @param orientation
	 * @return la veritable position ennemie sur la table
	 */
	private Vec2 changeReference(Vec2 relativePosEnnemi, Vec2 positionRobot, double orientation )
	{
		Vec2 positionEnnemi=new Vec2 (0,0);
		
		positionEnnemi.x= (int) ( 	(Math.cos(orientation )																	// projection sur l'axe standard
									*( relativePosEnnemi.x ) 		// de la difference de hauteur avec l'obstacle si le robot est droit, en face de l'obstacle
									+ Math.sin(orientation )																	// projection sur l'axe standard
									*( relativePosEnnemi.y ) 		// de la difference de longueur avec l'obstacle si le robot est droit, en face de l'obstacle
									+ positionRobot.x
						
								));	

		positionEnnemi.y = (int) ( 	+(Math.sin(orientation )																	// projection sur l'axe standard
									*( relativePosEnnemi.x ) 		// de la difference de hauteur avec l'obstacle si le robot est droit, en face de l'obstacle
									- Math.cos(orientation )																	// projection sur l'axe standard
									*( relativePosEnnemi.y ) 		// de la difference de longueur avec l'obstacle si le robot est droit, en face de l'obstacle
									+ positionRobot.y
								));
		
		return positionEnnemi;
	}
	
	
	private void removeObstacleFront(int[] distanceFront)
	{
		if(distanceFront[1] != 0)
			removeObstacleRight((int)((distanceFront[1])*0.7));
		else
			removeObstacleRight((int)maxSensorRange);
		if(distanceFront[0] != 0)
			removeObstacleLeft((int)((distanceFront[0])*0.7));
		else
			removeObstacleLeft((int)maxSensorRange);
	}
	
	/**
	 *  On enleve les obstacles qu'on ne voit pas
	 */
	private void removeObstacleLeft(int detectionDistance)
	{
		Vec2 position, sensorPosition;
		double orientation;
		
		orientation=mRobot.getOrientation();
		position=mRobot.getPosition(); // absolu
		
		sensorPosition=changeReference(leftFrontSensorPosition, position, orientation); // passage de la position du capteur en absolu
		if(mTable.getObstacleManager().removeNonDetectedObstacles(sensorPosition, (orientation-leftFrontSensorAngle), detectionDistance, detectionAngle) )
			log.debug("Obstacle enlevé avec le capteur gauche", this);
	}
	
	private void removeObstacleRight(int detectionDistance)
	{
		Vec2 position, sensorPosition;
		double orientation;
		
		orientation=mRobot.getOrientation();
		position=mRobot.getPosition(); // absolu
		
		sensorPosition=changeReference(rightFrontSensorPosition, position, orientation); // passage de la position du capteur en absolu
		if(mTable.getObstacleManager().removeNonDetectedObstacles(sensorPosition, (orientation+rightFrontSensorAngle), detectionDistance, detectionAngle))
			log.debug("Obstacle enlevé avec le capteur droit", this);

	}
}
