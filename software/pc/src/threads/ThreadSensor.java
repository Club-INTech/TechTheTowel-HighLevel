package threads;

import enums.SensorNames;
import exceptions.serial.SerialConnexionException;
import robot.cardsWrappers.SensorsCardWrapper;
import table.Table;
import utils.Sleep;

/**
 * Thread qui ajoute en continu les obstacles détectés par les capteurs.
 *
 * @author pf, Krissprolls, marsu, paul
 */

class ThreadSensor extends AbstractThread
{

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
	 * Crée un nouveau thread de capteurs
	 *
	 * @param table La table a l'intérieure de laquelle le thread doit croire évoluer
	 * @param sensorsCardWrapper La carte capteurs avec laquelle le thread va parler
	 */
	ThreadSensor (Table table, SensorsCardWrapper sensorsCardWrapper)
	{
		super(config, log);
		this.mSensorsCardWrapper = sensorsCardWrapper;
		Thread.currentThread().setPriority(2);
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

			// affiche la distance mesurée par l'ultrason
			//code precedant, a retirer si le code suivant ne marche pas
			//int distance = mSensorsCardWrapper.getSensedDistance();
			int[] distanceFront;
			try 
			{
				distanceFront = (int[]) mSensorsCardWrapper.getSensorValue(SensorNames.ULTRASOUND_FRONT_SENSOR);
				
				//on met tout les capteurs qui detectent un objet DANS le robot a 3000
				for (int i=0; i<distanceFront.length; i++)
					if (distanceFront[i]<distanceBetweenGuideAndUltrasound) 
						distanceFront[i]=3000;
				
			}
			catch(SerialConnexionException e)
			{
				log.critical("La carte capteurs ne répond pas !", this);
				e.printStackTrace();
				distanceFront = (int[]) SensorNames.ULTRASOUND_FRONT_SENSOR.getDefaultValue(); // valeur considérée comme infinie
			}
			
			int[] distanceBack;
			
			try 
			{
				distanceBack = (int[]) mSensorsCardWrapper.getSensorValue(SensorNames.ULTRASOUND_BACK_SENSOR);
			}
			catch (SerialConnexionException e)
			{
				log.critical("La carte capteurs ne répond pas !", this);
				e.printStackTrace();
				distanceBack = (int[]) SensorNames.ULTRASOUND_BACK_SENSOR.getDefaultValue(); //distance consideree comme infinie
			}
			
			//FIXME: ajouter les obstacles quand l'obstacleManager sera pret
			
			log.debug("Distance selon ultrason avant: "+distanceFront+"; ultrason arriere: "+distanceBack, this);
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
