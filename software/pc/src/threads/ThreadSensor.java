package threads;

import enums.SensorNames;
import exceptions.ConfigPropertyNotFoundException;
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
	private int sensorFrequency = 16;
	
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
	double maxSensorRange=500;
	
	/**
	 *  Angle de visibilité qu'a le capteur 
	 */
	double detectionAngle=40*Math.PI/180;
	
	/**
	 * Distance minimale à laquelle on peut se fier aux capteurs : ne pas detecter notre propre root par exemple
	 */
	double minSensorRange = 20;
	
	/**
	 *  Booleen explicitant si addObstacle a en effet ajouté un obstacle
	 */
	boolean obstacleAddedLeft;
	boolean obstacleAddedRight;
	
    /**
     * vrai si on est a gauche de la table (x<0 et jaune)
     * faux sinon
     */
    private boolean symetry;

	
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
	double leftFrontSensorAngle= 15		*2*(Math.PI) / 360;
	double rightFrontSensorAngle= 15 	*2*(Math.PI) / 360;
	double leftBackSensorAngle=  180	*2*(Math.PI) / 360;
	double rightBackSensorAngle= 180	*2*(Math.PI) / 360;
	
	/** 
	 *  position des capteurs relativement au centre du robot, en mm
	 */
	Vec2 rightFrontSensorPosition = new Vec2(80, 130);
	Vec2 leftFrontSensorPosition = new Vec2(80, -130);
	Vec2 rightBackSensorPosition = new Vec2(-90, 90);
	Vec2 leftBackSensorPosition = new Vec2(-90, -90);
	
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

	int[] realSensorValuesFront = new int[2];
	int[] realSensorValuesBack = new int[2];
	
	
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
		while(!ThreadTimer.matchStarted)
		{
			if(stopThreads)
			{
				log.debug("Stoppage du thread capteurs", this);
				return;
			}
			Sleep.sleep(50);
		}
		
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
			
			if(! (distanceFront[0]==-1 || 
				  distanceFront[1]==-1 ||
				   distanceBack[0]==-1 ||
				   distanceBack[1]==-1 )	) // si on n'a pas spammé
			{										
				// on enleve les obstacles 
				removeObstacleFront(distanceFront);
				removeObstacleBack(distanceBack);
				mTable.getObstacleManager().removeObstacleInUs(mRobot.getPosition());

				//ajout d'obstacles mobiles dans l'obstacleManager
				// Analyse des capteurs avant, avec gestion des angles
				if(mRobot.getIsRobotMovingForward())
					addObstacleFront(distanceFront);
				// Analyse des capteurs arrieres, avec gestion des angles
				if(mRobot.getIsRobotMovingBackward())
					addObstacleBack(distanceBack);
				
			}
			if (distanceFront[1] > 0 && distanceFront[1] < 70 || distanceFront[0] > 0 && distanceFront[0] < 70)
				log.debug("obstacle detecte a moins de 7 cm en avant !", this);
			if (distanceBack[1] > 0 && distanceBack[1] < 70 || distanceBack[0] > 0 && distanceBack[0] < 70)
				log.debug("obstacle detecte a moins de 7 cm en arriere !", this);
				
			try
			{
				// TODO à tester asap suivant la symetrie
				// Systeme enlevant un gobelet de la memoire du robot si il ne touche plus le contacteur 
				// ( concretement, si il est tombé alors qu'on prenait un plot )
				// Ca evite de tenter de deposer... kedal.
				if(mRobot.isGlassStoredLeft && ! (boolean) mRobot.getSensorValue(SensorNames.LEFT_ZONE_SENSOR))
				{
					log.critical("Verre gauche tombé", this);
					mRobot.isGlassStoredLeft=false;
				}
				if(mRobot.isGlassStoredRight && ! (boolean) mRobot.getSensorValue(SensorNames.RIGHT_ZONE_SENSOR))
				{
					log.critical("Verre droit tombé", this);
					mRobot.isGlassStoredRight=false;
				}
				
				// On verifie aussi le clic  si on a rien et que ca clique, on a quelque chose
				if(!mRobot.isGlassStoredLeft &&  (boolean) mRobot.getSensorValue(SensorNames.LEFT_ZONE_SENSOR))
				{
					log.debug("Verre gauche mis", this);
					mRobot.isGlassStoredLeft=true;
				}
				if(!mRobot.isGlassStoredRight &&  (boolean) mRobot.getSensorValue(SensorNames.RIGHT_ZONE_SENSOR))
				{
					log.debug("Verre droit mis", this);
					mRobot.isGlassStoredRight=true;
				}
				
			
			} 
			catch (SerialConnexionException e) 
			{
				log.critical( e.logStack(), this);
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
	private boolean[] addObstacleFront(int[] distanceFront) 
	{
		Vec2 positionRobot = mRobot.getPosition();
		double orientation = mRobot.getOrientation();
		
		boolean[] obstacleAdded={false,false};
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
		
		if(distanceFront[0]!=0)
			distanceObstacleFront[0]=distanceFront[0]+radius;
		else
			distanceObstacleFront[0]=0;
		
		if(distanceFront[1]!=0)
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
				// relatif
				positionEnnemi_1.x=   (int) (distanceObstacleFront[0]*Math.cos(leftFrontSensorAngle) +leftFrontSensorPosition.x);
				positionEnnemi_1.y=  -(int) (distanceObstacleFront[0]*Math.sin(leftFrontSensorAngle) +distanceBetweenFrontSensors/2);
				
				// sauvegarde de la position relative
				relativePosEnnemi1.x=positionEnnemi_1.x;
				relativePosEnnemi1.y=positionEnnemi_1.y;
				
				// On change de repere 
				positionEnnemi_1=changeReference(relativePosEnnemi1, positionRobot, orientation );
				
				mTable.getObstacleManager().addObstacle(positionEnnemi_1);
	    		log.debug("Valeur des capteurs avant brute : "+realSensorValuesFront[0]+";"+realSensorValuesFront[1], this);
	    		log.debug("Ennemi avant ajouté en "+positionEnnemi_1.x+";"+positionEnnemi_1.y, this);
				
				obstacleAddedLeft=true;
				
				// relatif au robot
				positionEnnemi_2.x=  (int) (distanceObstacleFront[1]*Math.cos(rightFrontSensorAngle) +rightFrontSensorPosition.x);
				positionEnnemi_2.y=  (int) (distanceObstacleFront[1]*Math.sin(rightFrontSensorAngle) +distanceBetweenFrontSensors/2);
				
				// sauvegarde de la position relative
				relativePosEnnemi2.x=positionEnnemi_2.x;
				relativePosEnnemi2.y=positionEnnemi_2.y;
				
				// On change de repere 
				positionEnnemi_2=changeReference(relativePosEnnemi2, positionRobot, orientation );
				
				mTable.getObstacleManager().addObstacle(positionEnnemi_2);
	    		log.debug("Valeur des capteurs avant brute : "+realSensorValuesFront[0]+";"+realSensorValuesFront[1], this);
	    		log.debug("Ennemi avant ajouté en "+positionEnnemi_2.x+";"+positionEnnemi_2.y, this);

				obstacleAddedRight=true;
			}
			// sinon, on voit un seul et meme ennemi
			else  
			{			
				positionEnnemi_1.y =  (int)( 	(Math.pow(distanceObstacleFront[0],2)-Math.pow(distanceObstacleFront[1],2))
												/(2 * distanceBetweenFrontSensors));	//position de l'obstacle en fonction du robot
				
				
				
				positionEnnemi_1.x =  (int)(	rightFrontSensorPosition.x + Math.sqrt( 
												(distanceObstacleFront[1]*distanceObstacleFront[1])-
												(positionEnnemi_1.y-distanceBetweenFrontSensors/2)*(positionEnnemi_1.y-distanceBetweenFrontSensors/2)));	//position de l'obstacle en fonction du robot

				relativePosEnnemi1.x=positionEnnemi_1.x;
				relativePosEnnemi1.y=positionEnnemi_1.y;
				
				// Maintenant, on le remet dans le repere du robot
				positionEnnemi_1=changeReference(relativePosEnnemi1, positionRobot, orientation );

				mTable.getObstacleManager().addObstacle(positionEnnemi_1);
	    		log.debug("Valeur des capteurs avant brute : "+realSensorValuesFront[0]+";"+realSensorValuesFront[1], this);
	    		log.debug("Ennemi avant ajouté en "+positionEnnemi_1.x+";"+positionEnnemi_1.y, this);


				obstacleAddedRight=true;
				obstacleAddedLeft=true;
			}			
		}
		else // Sinon, un seul des deux capteurs detecte quelque chose
		
		// Capteur du cote gauche
		if (minSensorRange<distanceFront[0] && distanceFront[0]<maxSensorRange)
		{			
			// relatif
			positionEnnemi_1.x=   (int) (distanceObstacleFront[0]*Math.cos(leftFrontSensorAngle) +leftFrontSensorPosition.x);
			positionEnnemi_1.y=  -(int) (distanceObstacleFront[0]*Math.sin(leftFrontSensorAngle) +distanceBetweenFrontSensors/2);
			
			// sauvegarde de la position relative
			relativePosEnnemi1.x=positionEnnemi_1.x;
			relativePosEnnemi1.y=positionEnnemi_1.y;
			
			// On change de repere 
			positionEnnemi_1=changeReference(relativePosEnnemi1, positionRobot, orientation );
			
			mTable.getObstacleManager().addObstacle(positionEnnemi_1);
    		log.debug("Valeur des capteurs avant brute : "+realSensorValuesFront[0]+";"+realSensorValuesFront[1], this);
    		log.debug("Ennemi avant ajouté en "+positionEnnemi_1.x+";"+positionEnnemi_1.y, this);

			
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
    		log.debug("Valeur des capteurs avant brute : "+realSensorValuesFront[0]+";"+realSensorValuesFront[1], this);
    		log.debug("Ennemi avant ajouté en "+positionEnnemi_1.x+";"+positionEnnemi_1.y, this);

			obstacleAddedRight=true;
		}
		obstacleAdded[0]=obstacleAddedLeft;
		obstacleAdded[1]=obstacleAddedRight;

		return obstacleAdded;
	}

	/**
	 * ajoute les obstacles arrieres a l'obstacleManager FIXME a changer
	 * @param distanceBack l'int[] recuperé par la serie pour les capteurs arrieres
	 * a modifier si ajout ou supression de capteurs
	 */
	private boolean[] addObstacleBack(int[] distanceBack) 
	{
		Vec2 positionRobot = mRobot.getPosition();
		double orientation = mRobot.getOrientation();
		
		boolean[] obstacleAdded={false,false};
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
		int[] distanceObstacleBack={0,0};
		
		if(distanceBack[0]!=0)
			distanceObstacleBack[0]=distanceBack[0]+radius;
		else
			distanceObstacleBack[0]=0;
		
		if(distanceBack[1]!=0)
			distanceObstacleBack[1]=distanceBack[1]+radius;
		else
			distanceObstacleBack[1]=0;
		
		//0 gauche / 1 à droite
		// si les 2 capteurs detectent quelque chose
		if ((minSensorRange<distanceBack[0] && distanceBack[0]<maxSensorRange) && (minSensorRange<distanceBack[1] && distanceBack[1]<maxSensorRange) )
		{
//			// Si on voit 2 ennemis distincts
//			if(Math.abs(distanceBack[1]-distanceBack[0]) > distanceBetweenFrontSensors)
//			{
//				// relatif
//				positionEnnemi_1.x=   (int) (distanceObstacleBack[0]*Math.cos(leftBackSensorAngle) +leftBackSensorPosition.x);
//				positionEnnemi_1.y=  -(int) (distanceObstacleBack[0]*Math.sin(leftBackSensorAngle) +distanceBetweenBackSensors/2);
//				
//				// sauvegarde de la position relative
//				relativePosEnnemi1.x=positionEnnemi_1.x;
//				relativePosEnnemi1.y=positionEnnemi_1.y;
//				
//				// On change de repere 
//				positionEnnemi_1=changeReference(relativePosEnnemi1, positionRobot, orientation );
//				
//				mTable.getObstacleManager().addObstacle(positionEnnemi_1);
//	    		log.debug("Valeur des capteurs arrieres brute : "+realSensorValuesBack[0]+";"+realSensorValuesBack[1], this);
//	    		log.debug("Ennemi arriere ajouté en "+positionEnnemi_1.x+";"+positionEnnemi_1.y, this);
//
//				
//				obstacleAddedLeft=true;
//				
//				// relatif au robot
//				positionEnnemi_2.x=  (int) (distanceObstacleBack[1]*Math.cos(rightBackSensorAngle) +rightBackSensorPosition.x);
//				positionEnnemi_2.y=  (int) (distanceObstacleBack[1]*Math.sin(rightBackSensorAngle) +distanceBetweenBackSensors/2);
//				
//				// sauvegarde de la position relative
//				relativePosEnnemi2.x=positionEnnemi_2.x;
//				relativePosEnnemi2.y=positionEnnemi_2.y;
//				
//				// On change de repere 
//				positionEnnemi_2=changeReference(relativePosEnnemi2, positionRobot, orientation );
//				
//				mTable.getObstacleManager().addObstacle(positionEnnemi_2);
//	    		log.debug("Valeur des capteurs arrieres brute : "+realSensorValuesBack[0]+";"+realSensorValuesBack[1], this);
//	    		log.debug("Ennemi arriere ajouté en "+positionEnnemi_2.x+";"+positionEnnemi_2.y, this);
//
//				
//				obstacleAddedRight=true;
//			}
//			// sinon, on voit un seul et meme ennemi
//			else  
			{			
				positionEnnemi_1.y =  (int)( 	(Math.pow(distanceObstacleBack[0],2)-Math.pow(distanceObstacleBack[1],2))
												/(2 * distanceBetweenBackSensors));	//position de l'obstacle en fonction du robot
				
				
				
				positionEnnemi_1.x =  -(int)(	-rightBackSensorPosition.x + Math.sqrt( 
												(distanceObstacleBack[1]*distanceObstacleBack[1])-
											    (positionEnnemi_1.y+distanceBetweenBackSensors/2)*(positionEnnemi_1.y+distanceBetweenBackSensors/2)));	//position de l'obstacle en fonction du robot

				relativePosEnnemi1.x=positionEnnemi_1.x;
				relativePosEnnemi1.y=positionEnnemi_1.y;
				
				// Maintenant, on le remet dans le repere du robot
				positionEnnemi_1=changeReference(relativePosEnnemi1, positionRobot, orientation );

				mTable.getObstacleManager().addObstacle(positionEnnemi_1);
	    		log.debug("Valeur des capteurs arrieres brute : "+realSensorValuesBack[0]+";"+realSensorValuesBack[1], this);
	    		log.debug("Ennemi arriere ajouté en "+positionEnnemi_1.x+";"+positionEnnemi_1.y, this);


				obstacleAddedRight=true;
				obstacleAddedLeft=true;
			}			
		}
		else // Sinon, un seul des deux capteurs detecte quelque chose
		
			// Capteur du cote gauche
			if (minSensorRange<distanceBack[0] && distanceBack[0]<maxSensorRange)
			{			
				// relatif
				positionEnnemi_1.x=   (int) (distanceObstacleBack[0]*Math.cos(leftBackSensorAngle) +leftBackSensorPosition.x);
				positionEnnemi_1.y=  -(int) (distanceObstacleBack[0]*Math.sin(leftBackSensorAngle) +distanceBetweenBackSensors/2);
				
				// sauvegarde de la position relative
				relativePosEnnemi1.x=positionEnnemi_1.x;
				relativePosEnnemi1.y=positionEnnemi_1.y;
				
				// On change de repere 
				positionEnnemi_1=changeReference(relativePosEnnemi1, positionRobot, orientation );
				
				mTable.getObstacleManager().addObstacle(positionEnnemi_1);
	    		log.debug("Valeur des capteurs arrieres brute : "+realSensorValuesBack[0]+";"+realSensorValuesBack[1], this);
	    		log.debug("Ennemi arriere ajouté en "+positionEnnemi_1.x+";"+positionEnnemi_1.y, this);
	
				
				obstacleAddedLeft=true;
	
			}
			// Capteur de coté droit
			else if (minSensorRange<distanceBack[1] && distanceBack[1]<maxSensorRange)
			{			
				// relatif au robot
				positionEnnemi_1.x=  (int) (distanceObstacleBack[1]*Math.cos(rightBackSensorAngle) +rightBackSensorPosition.x);
				positionEnnemi_1.y=  (int) (distanceObstacleBack[1]*Math.sin(rightBackSensorAngle) +distanceBetweenBackSensors/2);
				
				// sauvegarde de la position relative
				relativePosEnnemi1.x=positionEnnemi_1.x;
				relativePosEnnemi1.y=positionEnnemi_1.y;
				
				// On change de repere 
				positionEnnemi_1=changeReference(relativePosEnnemi1, positionRobot, orientation );
				
				mTable.getObstacleManager().addObstacle(positionEnnemi_1);
	    		log.debug("Valeur des capteurs arrieres brute : "+realSensorValuesBack[0]+";"+realSensorValuesBack[1], this);
	    		log.debug("Ennemi arriere ajouté en "+positionEnnemi_1.x+";"+positionEnnemi_1.y, this);
	
				obstacleAddedRight=true;
			}
		
		
		obstacleAdded[0]=obstacleAddedLeft;
		obstacleAdded[1]=obstacleAddedRight;

		
		return obstacleAdded;
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
			
			if(symetry) // On inverse capteur droit et gauche : en effet, on traite les obstacles et les capteurs comme si on etait verts
			{
				int svg=distanceFront[0];
				distanceFront[0]=distanceFront[1];
				distanceFront[1]=svg;
			}
			
			realSensorValuesFront[0]=distanceFront[0];
			realSensorValuesFront[1]=distanceFront[1];


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
			log.critical( e.logStack(), this);
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
			if(symetry) // On inverse capteur droit et gauche : en effet, on traite les obstacles et les capteurs comme si on etait verts
			{
				int svg=distanceBack[0];
				distanceBack[0]=distanceBack[1];
				distanceBack[1]=svg;
			}
			
			realSensorValuesBack[0]=distanceBack[0];
			realSensorValuesBack[1]=distanceBack[1];

			//on met tout les capteurs qui detectent un objet à plus de maxSensorRange a 0
			for (int i=0; i<distanceBack.length; i++)
			{
				if(distanceBack[i]==0)
					log.critical("ARRETEZ DE SPAMMER LES CAPTEURS !", this);
				if (distanceBack[i] > maxSensorRange) 
					distanceBack[i]=0;
			}
		}
		catch (SerialConnexionException e)
		{
			log.critical("La carte capteurs ne répond pas !", this);
			log.critical( e.logStack(), this);
			distanceBack = (int[]) SensorNames.ULTRASOUND_BACK_SENSOR.getDefaultValue();
		}
		return distanceBack;
	}


	/* (non-Javadoc)
	 * @see threads.AbstractThread#updateConfig()
	 */
	public void updateConfig()
	{
		try
		{
			sensorFrequency = Integer.parseInt(config.getProperty("capteurs_frequence"));
			radius = Integer.parseInt(config.getProperty("rayon_robot_adverse"));
			
			//plus que cette distance (environ 50cm) on est beaucoup moins precis sur la position adverse (donc on ne l'ecrit pas !)
			maxSensorRange = Integer.parseInt(config.getProperty("largeur_robot"))
							 / Math.sin(Float.parseFloat(config.getProperty("angle_capteur")));
			
			detectionAngle=Float.parseFloat(config.getProperty("angle_capteur"));
			
			robotWidth = Integer.parseInt(config.getProperty("largeur_robot"));
			robotLenght = Integer.parseInt(config.getProperty("longueur_robot"));
			
			symetry = config.getProperty("couleur").replaceAll(" ","").equals("jaune");
		}
		catch (ConfigPropertyNotFoundException e)
		{
    		log.debug("Revoir le code : impossible de trouver la propriété "+e.getPropertyNotFound(), this);;
		}
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
		// On limite les obstacles supprimés, pour eviter les problemes liés aux reflexions
			removeObstacleLeftFront(  Math.min((int)( realSensorValuesFront[0]*0.7),1000));
			removeObstacleRightFront( Math.min((int)( realSensorValuesFront[1]*0.7),1000));

	}
	
	private void removeObstacleBack(int[] distanceFront)
	{
		// On limite les obstacles supprimés, pour eviter les problemes liés aux reflexions
			removeObstacleLeftBack(  Math.min((int)( realSensorValuesBack[0]*0.7),1000));
			removeObstacleRightBack( Math.min((int)( realSensorValuesBack[1]*0.7),1000));

	}
	
	/**
	 *  On enleve les obstacles qu'on ne voit pas
	 */
	private void removeObstacleLeftFront(int detectionDistance)
	{
		Vec2 position, sensorPosition;
		double orientation;
		
		orientation=mRobot.getOrientation();
		position=mRobot.getPosition(); // absolu
		
		sensorPosition=changeReference(leftFrontSensorPosition, position, orientation); // passage de la position du capteur en absolu
		if(mTable.getObstacleManager().removeNonDetectedObstacles(sensorPosition, (orientation+leftFrontSensorAngle), (detectionDistance), detectionAngle) )
			log.debug("Obstacle enlevé avec le capteur gauche", this);
	}
	
	private void removeObstacleRightFront(int detectionDistance)
	{
		Vec2 position, sensorPosition;
		double orientation;
		
		orientation=mRobot.getOrientation();
		position=mRobot.getPosition(); // absolu
		
		sensorPosition=changeReference(rightFrontSensorPosition, position, orientation); // passage de la position du capteur en absolu
		if(mTable.getObstacleManager().removeNonDetectedObstacles(sensorPosition, (orientation-rightFrontSensorAngle),  (detectionDistance), detectionAngle))
			log.debug("Obstacle enlevé avec le capteur droit", this);

	}
	
	/**
	 *  On enleve les obstacles qu'on ne voit pas derriere nous
	 */
	private void removeObstacleLeftBack(int detectionDistance)
	{
		Vec2 position, sensorPosition;
		double orientation;
		
		orientation=mRobot.getOrientation();
		position=mRobot.getPosition(); // absolu
		
		sensorPosition=changeReference(leftBackSensorPosition, position, orientation); // passage de la position du capteur en absolu
		if(mTable.getObstacleManager().removeNonDetectedObstacles(sensorPosition, (orientation+leftBackSensorAngle), (detectionDistance), detectionAngle) )
			log.debug("Obstacle enlevé avec le capteur gauche arriere", this);
	}
	
	private void removeObstacleRightBack(int detectionDistance)
	{
		Vec2 position, sensorPosition;
		double orientation;
		
		orientation=mRobot.getOrientation();
		position=mRobot.getPosition(); // absolu
		
		sensorPosition=changeReference(rightBackSensorPosition, position, orientation); // passage de la position du capteur en absolu
		if(mTable.getObstacleManager().removeNonDetectedObstacles(sensorPosition, (orientation-rightBackSensorAngle),  (detectionDistance), detectionAngle))
			log.debug("Obstacle enlevé avec le capteur droit", this);

	}
}
