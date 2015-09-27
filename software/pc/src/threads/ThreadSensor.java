package threads;

import enums.USsensors;
import exceptions.ConfigPropertyNotFoundException;
import exceptions.serial.SerialConnexionException;
import graphics.Window;
import robot.cardsWrappers.SensorsCardWrapper;
import table.Table;
import robot.RobotReal;
import smartMath.Vec2;

/**
 * Thread qui ajoute en continu les obstacles détectés par les capteurs,
 * Et enleve ceux qui ont disparu, de meme que verifie les capteurs de contact
 *
 * @author pf, Krissprolls, marsu, paul, pingu
 */

class ThreadSensor extends AbstractThread
{
	/** Le robot */
	private RobotReal mRobot;

	/** La carte capteurs avec laquelle on doit communiquer */
	private SensorsCardWrapper mSensorsCardWrapper;
	
	/** interface graphique */
	public Window window;
	
	// Valeurs par défaut s'il y a un problème de config
	
	/** fréquence de mise a jour des valeurs renvoyés par les capteurs. Valeurs par défaut de 5 fois par seconde s'il y a un problème de config */
	// Overide par la config
	private int sensorFrequency = 15;


	/**
	 * Distance maximale fiable pour les capteurs : au dela, valeurs abberentes
	 * Override par la config
	 */
	double maxSensorRange=500;

	/**
	 * Distance minimale à laquelle on peut se fier aux capteurs : ne pas detecter notre propre root par exemple
	 * TODO : mettre en config
	 */
	double minSensorRange = 20;
	
	/**
	 *  Angle de visibilité qu'a le capteur 
	 * Override par la config
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
	double detectionAngle=40*Math.PI/180;
	
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
		mRobot = robot;
	}
	
	@Override
	public void run()
	{
		log.debug("Lancement du thread de capteurs");
		updateConfig();
		
		// boucle d'attente de début de match
		boolean jumperWasAbsent = mSensorsCardWrapper.isJumperAbsent();
		while(jumperWasAbsent || !mSensorsCardWrapper.isJumperAbsent())
		{
			
			jumperWasAbsent = mSensorsCardWrapper.isJumperAbsent();
			mRobot.sleep(100);
		}

		// maintenant que le jumper est retiré, le match a commencé
		ThreadTimer.matchEnded = false;
		
		// boucle principale, celle qui dure tout le match
		log.debug("Activation des capteurs");
		while(!ThreadTimer.matchEnded)
		{
			// on s'arrete si le ThreadManager le demande
			if(stopThreads)
			{
				log.debug("Stoppage du thread capteurs");
				return;
			}
			
			/* recupere la distance mesurée par l'ultrason
			 * on met la distance detecte dans deux variables int[] de taille deux
			 * si la carte ne repond pas on revoie la valeur par default
			 */
			// distanceFront[0] = US gauche
			int distance = getDistance();
			
			if( distance!= -1 ) // si on n'a pas spammé
			{		
				// On enleve les obstacles qu'on sait absents de la table : si le robot ennemi a bougé,
				// On l'enleve de notre memoire
				removeObstacle(distance);
				
				//ajout d'obstacles mobiles dans l'obstacleManager
				addObstacle(distance);
			}			
			if (distance > 0 && distance < 70)
				log.debug("obstacle detecte a moins de 7 cm  !");
				
				
			try 
			{
				// On ne spamme pas la serie, on attend un peu avant de redemander les valeurs
				// Et on laisse le temps aux autres Threads
				Thread.sleep((long)(1000./sensorFrequency));
			} 
			catch (InterruptedException e)
			{
				break;
			}			
		}
        log.debug("Fin du thread de capteurs");
		
	}
	
	/**
	 * ajoute les obstacles avant a l'obstacleManager 
	 * @param distance l'int recupere par la serie pour les capteurs
	 * a modifier si ajout ou supression de capteurs
	 */
	private boolean addObstacle(int distance) 
	{		
		boolean obstacleAdded=false;
		
		return obstacleAdded;
	}


	/**
	 * Recupere la distance lue par les ultrasons 
	 * @return la distance selon les ultrasons
	 */
	private int getDistance() 
	{
		int distance;
		try 
		{
			distance = mSensorsCardWrapper.getUSSensorValue(USsensors.ULTRASOUND);
			

			//on met tout les capteurs qui detectent un objet DANS le robot ou à plus de maxSensorRange a 0
			// TODO : a passer en traitement de bas niveau ?
			if(distance==0)
			{
				//log.critical("ARRETEZ DE SPAMMER LES CAPTEURS !", this);
				distance=-1;
			}
			if ( distance > maxSensorRange) 
			{
				distance=0;
			}
		}
		catch(SerialConnexionException e)
		{
			log.critical("La carte capteurs ne répond pas !");
			log.critical( e.logStack());
			distance = USsensors.ULTRASOUND.getDefaultValue();
		}
		return distance;
	}

	public void updateConfig()
	{
		try
		{
			sensorFrequency = Integer.parseInt(config.getProperty("capteurs_frequence"));
			Integer.parseInt(config.getProperty("rayon_robot_adverse"));
			
			//plus que cette distance (environ 50cm) on est beaucoup moins precis sur la position adverse (donc on ne l'ecrit pas !)
			maxSensorRange = Integer.parseInt(config.getProperty("largeur_robot"))
							 / Math.sin(Float.parseFloat(config.getProperty("angle_capteur")));
			
			detectionAngle=Float.parseFloat(config.getProperty("angle_capteur"));
			
			robotWidth = Integer.parseInt(config.getProperty("largeur_robot"));
			robotLenght = Integer.parseInt(config.getProperty("longueur_robot"));
		}
		catch (ConfigPropertyNotFoundException e)
		{
    		log.debug("Revoir le code : impossible de trouver la propriété "+e.getPropertyNotFound());;
		}
	}

	
	/**
	 *  On enleve les obstacles qu'on ne voit pas
	 */
	private void removeObstacle(int detectionDistance)
	{
		//TODO la méthode
		// enlever les obstacles qu'on devrait voir mais qu'on ne detecte plus
	}
}
