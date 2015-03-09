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
	
	int maxSensorRange;
	
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
		maxSensorRange = Integer.parseInt(config.getProperty("horizon_capteurs"));
		
		
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

			// affiche la distance mesurée par l'ultrason
			//code precedant, a retirer si le code suivant ne marche pas
			//int distance = mSensorsCardWrapper.getSensedDistance();
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
			
			
			//ajout d'obstacles mobiles dans l'obstacleManager
			//TODO : recuperer le rayon du robot adverse via la config
			int radius = 230;
			for (int i=0; i<distanceFront.length; i++)
				if(distanceFront[i]!=0)
					mTable.getObstacleManager().addObstacle(new Vec2(mRobot.getPosition().x + (int)((distanceFront[i]+radius)*Math.cos(mRobot.getOrientation())), mRobot.getPosition().y + (int)((distanceFront[i]+radius)*Math.sin(mRobot.getOrientation()))));
			for (int i=0; i<distanceBack.length; i++)
				if(distanceBack[i]!=0)
					mTable.getObstacleManager().addObstacle(new Vec2(mRobot.getPosition().x - (int)((distanceBack[i]+radius)*Math.cos(mRobot.getOrientation())), mRobot.getPosition().y - (int)((distanceBack[i]+radius)*Math.sin(mRobot.getOrientation()))));
			
			log.debug("Distance selon ultrasons avant: "+distanceFront[0]+";"+distanceFront[1]+" //  ultrason arriere: "+distanceBack[0]+";"+distanceBack[1], this);
			if (distanceFront[1] > 0 && distanceFront[1] < 70 || distanceFront[0] > 0 && distanceFront[0] < 70)
				log.debug("obstacle detecte a moins de 7 cm !", this);
			
			Sleep.sleep((long)(1000./sensorFrequency));
			
		}
        log.debug("Fin du thread de capteurs", this);
		
	}
	
	/* (non-Javadoc)
	 * @see threads.AbstractThread#updateConfig()
	 */
	public void updateConfig()
	{
			sensorFrequency = Integer.parseInt(config.getProperty("capteurs_frequence"));
	}
	
}
