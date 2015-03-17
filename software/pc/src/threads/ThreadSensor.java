package threads;

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
 * @author pf, Krissprolls, marsu, paul
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
	private int sensorFrequency = 5;
	
	/**
	 * distance en mm entre les capteur ultrasond et le guide en plastique, 
	 * on ne peut rien detecter de plus petit que cette distance donc toutes les informations de distance en dessous de cette valeur ne seron pas traités
	 */
	int distanceBetweenGuideAndUltrasound = 20;
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
	 * Distance minimale à laquelle on peut see fier aux capteurs : ne pas detecter notre propre root par exemple
	 */
	double minSensorRange = 70;
	
	/** Les angles des capteurs :
	 * 
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
	 * 		\angle|		 |angle/
	 * 		 \    |		 |	  /
	 * 		  \   |		 |	 /
	 * 		   \  |		 |	/
	 * 			\o|		 |o/
	 * 				Robot		o : capteur
	 * 
	 */
	
	//Angles en degrés, transformés en radians
	double leftFrontSensorAngle=20		*2*(Math.PI) / 360;
	double rightFrontSensorAngle=20 	*2*(Math.PI) / 360;
	double leftBackSensorAngle=0		*2*(Math.PI) / 360;
	double rightBackSensorAngle=0		*2*(Math.PI) / 360;
	
	// position des capteurs relativement au centre du robot, en mm
	Vec2 rightFrontSensorPosition = new Vec2(15, 5);
	Vec2 leftFrontSensorPosition = new Vec2(-15, 5);
	Vec2 rightBackSensorPosition = new Vec2(15, -5);
	Vec2 leftBackSensorPosition = new Vec2(-15, -5);

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

			/* recupere la distance mesurée par l'ultrason
			 * on met la distance detecte, a l'avant et a l'arriere, dans deux variables int[] de taille deux
			 * si la carte ne repond pas on revoie la valeur par default
			 */
			int[] distanceFront = getDistanceFront();
			int[] distanceBack = getDistanceBack();
			
			
			//ajout d'obstacles mobiles dans l'obstacleManager
			// Analyse des capteurs avant, avec gestion dees angles TODO verifier les angles
			addObstacleFront(distanceFront);
			// Analyse des capteurs arrieres, avec gestion des angles
			addObstacleBack(distanceBack);
			
			log.debug("Distance selon ultrasons avant:   "+distanceFront[0]+";"+distanceFront[1], this); 
			log.debug("Distance selon ultrasons arriere: "+distanceBack[0]+";"+distanceBack[1], this);
			
			if (distanceFront[1] > 0 && distanceFront[1] < 70 || distanceFront[0] > 0 && distanceFront[0] < 70)
				log.debug("obstacle detecte a moins de 7 cm !", this);
			
			Sleep.sleep((long)(1000./sensorFrequency));
			
		}
        log.debug("Fin du thread de capteurs", this);
		
	}
	/**
	 * ajoute les obstacles avant a l'obstacleManager TODO a tester
	 * @param distanceFront l'int[] recupere par la serie pour les capteurs avants
	 * a modifier si ajout ou supression de capteurs
	 */
	private void addObstacleFront(int[] distanceFront) 
	{
		/** Zones de detection : 0;1;2 capteurs dans leurs zones respectives
		 * 
		 * 		______ ___
		 * 		|	  |\1/\	
		 * 		|	  |0X2 )
		 *  	|_____|/1\/
		 *  		   ---
		 */
		
	
		 // les deux capteurs detectent, on est dans la zone de double detection et on peut placer precisement l'obstacle
		// Plus precisement : si On a chaque capteur detectant quelquechose dans l'intervalle 
		
		//0 gauche / 1 à droite
		if ((minSensorRange<distanceFront[0] && distanceFront[0]<maxSensorRange) && (minSensorRange<distanceFront[1] && distanceFront[1]<maxSensorRange) )
		{
			//debrouillez vous, faites le calcul (le systeme c'est {x²+y²=distanceBack[0]² ;(L-x)²+y²= distanceBack[1]²})
			

			if(Math.abs(distanceFront[1]-distanceFront[0]) > distanceBetweenFrontSensors)
			{// Si on voit 2 ennemis distincts
				
				// droite
				positionEnnemi_1.x = (int) ((float)Math.sin(20*Math.PI/180+mRobot.getOrientation())*distanceFront[1]+distanceBetweenFrontSensors/2+rightFrontSensorPosition.x);
				positionEnnemi_1.y = (int) ((float)Math.cos(20*Math.PI/180+mRobot.getOrientation())*distanceFront[1]+rightFrontSensorPosition.y);
				
				//gauche
				positionEnnemi_2.x = (int)Math.sin(20*Math.PI/180+mRobot.getOrientation())*distanceFront[0]-distanceBetweenFrontSensors/2+leftFrontSensorPosition.x;
				positionEnnemi_2.y = (int)Math.cos(20*Math.PI/180+mRobot.getOrientation())*distanceFront[0]+leftFrontSensorPosition.y;
			
				System.out.println("position ennemi gauche = ("+positionEnnemi_2.x+","+positionEnnemi_2.y+")");
				System.out.println("position ennemi droit  = ("+positionEnnemi_1.x+","+positionEnnemi_1.y+")");

			}
			else  // sinon, on voit un seul et meme ennemi
			{			
				positionEnnemi_1.x = (int) ( mRobot.getPosition().x + (distanceBetweenFrontSensors/2+(Math.pow(distanceFront[0],2)-Math.pow(distanceFront[1],2))/(2 * distanceBetweenFrontSensors)));
				positionEnnemi_1.y = (int) (rightFrontSensorPosition.y + Math.sqrt(Math.pow(distanceFront[0],2)-Math.pow(positionEnnemi_1.x, 2)));
			
				positionEnnemi_2.x=0;
				positionEnnemi_2.y=0;
				
				System.out.println("position ennemi = ("+positionEnnemi_1.x+","+positionEnnemi_1.y+")");
			}			
			
			
			mTable.getObstacleManager().addObstacle(positionEnnemi_1);
			if(! positionEnnemi_2.equals(new Vec2 (0,0) ) )
				mTable.getObstacleManager().addObstacle(positionEnnemi_2);;

		}
		
		// Sinon, on est dans les zones de simple detection
		else if (minSensorRange<distanceFront[0] && distanceFront[0]<maxSensorRange)// Capteur du cote gauche
		{
			mTable.getObstacleManager().addObstacle(new Vec2(mRobot.getPosition().x + (int)(rightFrontSensorPosition.x*Math.cos(mRobot.getOrientation()) - rightFrontSensorPosition.y*Math.sin(mRobot.getOrientation())) + (int)((distanceFront[0]+radius)*Math.cos(mRobot.getOrientation() + rightFrontSensorAngle)), 
															 mRobot.getPosition().y + (int)(rightFrontSensorPosition.x*Math.sin(mRobot.getOrientation()) + rightFrontSensorPosition.y*Math.cos(mRobot.getOrientation())) + (int)((distanceFront[0]+radius)*Math.sin(mRobot.getOrientation() + rightFrontSensorAngle))));
		}
		else if (minSensorRange<distanceFront[1] && distanceFront[1]<maxSensorRange)// Capteur de coté droit
		{
			mTable.getObstacleManager().addObstacle(new Vec2(mRobot.getPosition().x + (int)(leftFrontSensorPosition.x*Math.cos(mRobot.getOrientation()) - leftFrontSensorPosition.y*Math.sin(mRobot.getOrientation())) + (int)((distanceFront[1]+radius)*Math.cos(mRobot.getOrientation() - leftFrontSensorAngle)), 
															 mRobot.getPosition().y + (int)(leftFrontSensorPosition.x*Math.sin(mRobot.getOrientation()) + leftFrontSensorPosition.y*Math.cos(mRobot.getOrientation())) + (int)((distanceFront[1]+radius)*Math.sin(mRobot.getOrientation() - leftFrontSensorAngle))));
		}
	}

	/**
	 * ajoute les obstacles arrieres a l'obstacleManager FIXME debug primordial
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
				if (distanceBack[i]<distanceBetweenGuideAndUltrasound || distanceBack[i] > maxSensorRange) 
					distanceBack[i]=0;
		}
		catch (SerialConnexionException e)
		{
			log.critical("La carte capteurs ne répond pas !", this);
			e.printStackTrace();
			distanceBack = (int[]) SensorNames.ULTRASOUND_BACK_SENSOR.getDefaultValue();
		}
		return distanceBack;
	}

	/**
	 * 
	 * @return la distance selon les ultrasonsavants, [gauche, droite]
	 */
	private int[] getDistanceFront() 
	{
		int[] distanceFront;
		try 
		{
			distanceFront = (int[]) mSensorsCardWrapper.getSensorValue(SensorNames.ULTRASOUND_FRONT_SENSOR);
			
			//on met tout les capteurs qui detectent un objet DANS le robot ou à plus de maxSensorRange a 0
			for (int i=0; i<distanceFront.length; i++)
				if (distanceFront[i]<distanceBetweenGuideAndUltrasound || distanceFront[i] > maxSensorRange) 
					distanceFront[i]=0;
			
		}
		catch(SerialConnexionException e)
		{
			log.critical("La carte capteurs ne répond pas !", this);
			e.printStackTrace();
			distanceFront = (int[]) SensorNames.ULTRASOUND_FRONT_SENSOR.getDefaultValue();
		}
		return distanceFront;
	}

	/* (non-Javadoc)
	 * @see threads.AbstractThread#updateConfig()
	 */
	public void updateConfig()
	{
			sensorFrequency = Integer.parseInt(config.getProperty("capteurs_frequence"));
			radius = Integer.parseInt(config.getProperty("rayon_robot_adverse"));
			//plus que cette distance (environ 50cm) on est beaucoup moins precis sur la position adverse (donc on ne l'ecrit pas !)
			maxSensorRange = Integer.parseInt(config.getProperty("largeur_robot")) / Math.sin(Float.parseFloat(config.getProperty("angle_capteur")));
			robotWidth = Integer.parseInt(config.getProperty("largeur_robot"));
			robotLenght = Integer.parseInt(config.getProperty("longueur_robot"));
	}
	
}
