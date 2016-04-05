package threads;

import enums.USsensors;
import exceptions.ConfigPropertyNotFoundException;
import exceptions.serial.SerialConnexionException;
import graphics.Window;
import robot.RobotReal;
import robot.cardsWrappers.SensorsCardWrapper;
import smartMath.Vec2;
import table.Table;

import java.util.ArrayList;

/**
 * Thread qui ajoute en continu les obstacles détectés par les capteurs,
 * Et enleve ceux qui ont disparu, de meme que verifie les capteurs de contact
 *
 * @author pf, Krissprolls, marsu, paul, pingu
 */

public class ThreadSensor extends AbstractThread
{
	/** Le robot */
	private RobotReal mRobot;

    /** La table */
    private Table mTable;

	/** La carte capteurs avec laquelle on doit communiquer */
	private SensorsCardWrapper mSensorsCardWrapper;
	
	/** interface graphique */
	public Window window;
	
	// Valeurs par défaut s'il y a un problème de config
	
	/** fréquence de mise a jour des valeurs renvoyés par les capteurs. Valeurs par défaut de 5 fois par seconde s'il y a un problème de config */
	// Overide par la config
	private int sensorFrequency = 15;

    /**
     * Si l'on doit symétriser
     */
    private boolean symetry;

    /**
     * Rayon du robot adverse
     */
    private int radius;

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
     * Angles des capteurs relatifs à l'axe avant-arrière du robot (radians) TODO A changer !
     */
    private final double angleLF = -0.4;
    private final double angleRF = -0.4;
    private final double angleLB = 0;
    private final double angleRB = 0;


    /**
     * Positions relatives au centre du robot TODO A changer !
     */

    private final Vec2 positionLF = new Vec2(170, -150);
    private final Vec2 positionRFClosed = new Vec2(170, 150);
    private final Vec2 positionLB = new Vec2(-120,-75);
    private final Vec2 positionRB = new Vec2(-120,75);


    /**
     * Valeurs des capteurs US {avant-gauche, avant-droit, arrière gauche, arrière-droit}
     */
    ArrayList<Integer> USvalues = new ArrayList<>(4);

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
	Vec2 positionSaloperie_1 = new Vec2 (0,0);
	Vec2 positionSaloperie_2 = new Vec2 (0,0);

    /**
     * Position relative des ennemis par rapport à notre robot
     */
    Vec2 relativePositionSaloperie_1 = new Vec2 (0,0);
    Vec2 relativePositionSaloperie_2 = new Vec2 (0,0);


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
        mTable = table;
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
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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

			getDistances();
			
			if( !USvalues.contains(-1) || true) // si on n'a pas spammé
			{
				// On enleve les obstacles qu'on sait absents de la table : si le robot ennemi a bougé,
				// On l'enleve de notre memoire
				removeObstacle();

                for(int i=0 ; i<USvalues.size(); i++)
                {
                    if(USvalues.get(i) != 0)
                        USvalues.set(i, USvalues.get(i)+radius);
                }

				//ajout d'obstacles mobiles dans l'obstacleManager
				addObstacle();
			}
//			if (distance > 0 && distance < 70)
//				log.debug("obstacle detecte a moins de 7 cm  !");
				
				
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
	 * ajoute les obstacles a l'obstacleManager
	 */
	private void addObstacle()
	{		
        if(USvalues.get(0) != 0 && USvalues.get(1) != 0 && !mRobot.getIsRobotMovingBackward())
            addFrontObstacleBoth();
        else if((USvalues.get(0) != 0 || USvalues.get(1) != 0) && !mRobot.getIsRobotMovingBackward())
            addFrontObstacleSingle(USvalues.get(0) != 0);

        if(USvalues.get(2) != 0 && USvalues.get(3) != 0 && !mRobot.getIsRobotMovingForward())
            addBackObstacleBoth();
        else if((USvalues.get(2) != 0 || USvalues.get(3) != 0) && !mRobot.getIsRobotMovingForward())
            addBackObstacleSingle(USvalues.get(2) != 0);
	}

    /**
     * Ajoute un obstacle en face du robot, avec les deux capteurs ayant détecté quelque chose
     */
    private void addFrontObstacleBoth()
    {
        double distanceBetweenSensors = positionLF.minusNewVector(positionRF()).length();

        if(Math.abs(USvalues.get(1) - USvalues.get(0)) <= distanceBetweenSensors) //Si on semble pointer vers le même ennemi des deux capteurs
        {
            //Position de l'ennemi
            relativePositionSaloperie_1.y = (int) (((USvalues.get(0) * USvalues.get(0)) - (USvalues.get(1) * USvalues.get(1))) / (2 * distanceBetweenSensors));
            relativePositionSaloperie_1.x = (int) (positionRF().x + Math.sqrt((USvalues.get(1) * USvalues.get(1)) - (relativePositionSaloperie_1.y - distanceBetweenSensors / 2) * (relativePositionSaloperie_1.y - distanceBetweenSensors / 2)));
            positionSaloperie_1 = changeRef(relativePositionSaloperie_1, mRobot.getPosition(), mRobot.getOrientation());

            mTable.getObstacleManager().addObstacle(positionSaloperie_1);
        }
        else //Les deux capteurs ne semblent pas détecter le même ennemi, on en ajoute 2 !
        {
            //Position relative du premier ennemi
            relativePositionSaloperie_1.x = (int)(USvalues.get(0)*Math.cos(angleLF)+positionLF.x);
            relativePositionSaloperie_1.y = (int)(-USvalues.get(0)*Math.sin(angleLF)-distanceBetweenSensors/2);
            positionSaloperie_1 = changeRef(relativePositionSaloperie_1, mRobot.getPosition(), mRobot.getOrientation());

            //Position relative du second ennemi
            relativePositionSaloperie_2.x = (int)(USvalues.get(1)*Math.cos(angleRF)+positionRF().x);
            relativePositionSaloperie_2.y = (int)(USvalues.get(1)*Math.sin(angleRF)+distanceBetweenSensors/2);
            positionSaloperie_2 = changeRef(relativePositionSaloperie_2, mRobot.getPosition(), mRobot.getOrientation());

            mTable.getObstacleManager().addObstacle(positionSaloperie_1);
            mTable.getObstacleManager().addObstacle(positionSaloperie_2);

        }

    }

    /**
     * Donne la position du capteur avant-droit, utile car notre année il se déplace avec la porte
     * @return le Vec2 de position
     */
    private Vec2 positionRF()
    {
        //FIXME l'ajout de distance selon l'ouverture de la porte
        return positionRFClosed.plusNewVector(new Vec2(0,mRobot.doorIsOpen ? 20 : 0));
    }

    /**
     * Ajoute un obstacle derrière le robot, avec les deux capteurs ayant détecté quelque chose
     */
    private void addBackObstacleBoth()
    {
        double distanceBetweenSensors = positionLB.minusNewVector(positionRB).length();

        if(Math.abs(USvalues.get(2) - USvalues.get(3)) <= distanceBetweenSensors) //Si on semble pointer vers le même ennemi des deux capteurs
        {
            //Position de l'ennemi
            relativePositionSaloperie_1.y = (int) ((USvalues.get(2) * USvalues.get(2) - USvalues.get(3) * USvalues.get(3)) / (2 * distanceBetweenSensors));
            relativePositionSaloperie_1.x = (int) (positionRF().x - Math.sqrt((USvalues.get(3) * USvalues.get(3)) - (relativePositionSaloperie_1.y + distanceBetweenSensors / 2) * (relativePositionSaloperie_1.y + distanceBetweenSensors / 2)));
            positionSaloperie_1 = changeRef(relativePositionSaloperie_1, mRobot.getPosition(), mRobot.getOrientation());

            mTable.getObstacleManager().addObstacle(positionSaloperie_1);
        }
        else //Les deux capteurs ne semblent pas détecter le même ennemi, on en ajoute 2 !
        {
            //Position relative du premier ennemi
            relativePositionSaloperie_1.x = (int)(USvalues.get(2)*Math.cos(angleLB)+positionLB.x);
            relativePositionSaloperie_1.y = (int)(-USvalues.get(2)*Math.sin(angleLB)-distanceBetweenSensors/2);
            positionSaloperie_1 = changeRef(relativePositionSaloperie_1, mRobot.getPosition(), mRobot.getOrientation());

            //Position relative du second ennemi
            relativePositionSaloperie_2.x = (int)(USvalues.get(3)*Math.cos(angleRB)+positionRB.x);
            relativePositionSaloperie_2.y = (int)(USvalues.get(3)*Math.sin(angleRB)+distanceBetweenSensors/2);
            positionSaloperie_2 = changeRef(relativePositionSaloperie_2, mRobot.getPosition(), mRobot.getOrientation());

            mTable.getObstacleManager().addObstacle(positionSaloperie_1);
            mTable.getObstacleManager().addObstacle(positionSaloperie_2);

        }
    }

    /**
     * Ajoute un obstacle devant le robot, avec un seul capteur ayant détecté quelque chose
     * @param isLeft si c'est le capteur gauche
     */
    private void addFrontObstacleSingle(boolean isLeft)
    {
        int distance = USvalues.get(0) + USvalues.get(1); //La distance mesurée par le capteur
        double distanceBetweenSensors = positionLF.minusNewVector(positionRF()).length();

        if(isLeft)
        {
            relativePositionSaloperie_1.x = (int)(distance*Math.cos(angleLF)+positionLF.x)-100;
            relativePositionSaloperie_1.y = (int)(-distance*Math.sin(angleLF)-distanceBetweenSensors/2);
            positionSaloperie_1 = changeRef(relativePositionSaloperie_1, mRobot.getPosition(), mRobot.getOrientation());

            mTable.getObstacleManager().addObstacle(positionSaloperie_1);
        }
        else
        {
            relativePositionSaloperie_2.x = (int)(distance*Math.cos(angleRF)+positionRF().x)-100;
            relativePositionSaloperie_2.y = (int)(distance*Math.sin(angleRF)+distanceBetweenSensors/2);
            positionSaloperie_2 = changeRef(relativePositionSaloperie_2, mRobot.getPosition(), mRobot.getOrientation());

            mTable.getObstacleManager().addObstacle(positionSaloperie_2);
        }
    }

    /**
     * Ajoute un obstacle derrière le robot, avec un seul capteur ayant détecté quelque chose
     * @param isLeft si c'est le capteur gauche
     */
    private void addBackObstacleSingle(boolean isLeft)
    {
        int distance = USvalues.get(0) + USvalues.get(1); //La distance mesurée par le capteur
        double distanceBetweenSensors = positionLB.minusNewVector(positionRB).length();

        if(isLeft)
        {
            relativePositionSaloperie_1.x = (int)(distance*Math.cos(angleLB)+positionLB.x);
            relativePositionSaloperie_1.y = (int)(-distance*Math.sin(angleLB)-distanceBetweenSensors/2);
            positionSaloperie_1 = changeRef(relativePositionSaloperie_1, mRobot.getPosition(), mRobot.getOrientation());

            mTable.getObstacleManager().addObstacle(positionSaloperie_1);
        }
        else
        {
            relativePositionSaloperie_2.x = (int)(distance*Math.cos(angleRB)+positionRB.x);
            relativePositionSaloperie_2.y = (int)(distance*Math.sin(angleRB)+distanceBetweenSensors/2);
            positionSaloperie_2 = changeRef(relativePositionSaloperie_2, mRobot.getPosition(), mRobot.getOrientation());

            mTable.getObstacleManager().addObstacle(positionSaloperie_2);
        }
    }


    /**
     * Passe du référentiel centré sur le point donné au référentiel terrestre
     * @param posPoint la position relative dont on cherche les coordonées absolues
     * @param posOrigin la position de l'origine du reférentiel
     * @param orientation l'orientation du référentiel par rapport au terrestre
     */
    private Vec2 changeRef(Vec2 posPoint, Vec2 posOrigin, double orientation)
    {
        return new Vec2((int)(posPoint.x*Math.cos(orientation)+posPoint.y*Math.sin(orientation)+posOrigin.x),
                (int)(posPoint.x*Math.sin(orientation)-posPoint.y*Math.cos(orientation)+posOrigin.y));
    }




	/**
	 * Recupere la distance lue par les ultrasons 
	 * @return la distance selon les ultrasons
	 */
	private void getDistances()
	{
		try 
		{
            USvalues = mSensorsCardWrapper.getUSSensorValue(USsensors.ULTRASOUND); //On récupère une liste de valeurs

            if(symetry) //Inversion gauche/droite pour symétriser
            {
                int temp = USvalues.get(0);
                USvalues.set(0, USvalues.get(1));
                USvalues.set(1, temp);
                temp = USvalues.get(2);
                USvalues.set(2, USvalues.get(3));
                USvalues.set(3, temp);
            }

            for(int i=0 ; i<USvalues.size() ; i++)
            {
                //on met tout les capteurs qui detectent un objet DANS le robot ou à plus de maxSensorRange a 0
                // TODO : a passer en traitement de bas niveau ?
                if(USvalues.get(i)==0)
                {
                    //log.critical("ARRETEZ DE SPAMMER LES CAPTEURS !", this);
                    USvalues.set(i, -1);
                }
                if ( USvalues.get(i) > maxSensorRange || USvalues.get(i) < minSensorRange)
                {
                    USvalues.set(i, 0);
                }

            }
		}
		catch(SerialConnexionException e) {
            log.critical("La carte ne répond pas !");
            log.critical(e.logStack());
        }
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
            symetry = config.getProperty("couleur").replaceAll(" ","").equals("violet");
			
			robotWidth = Integer.parseInt(config.getProperty("largeur_robot"));
			robotLenght = Integer.parseInt(config.getProperty("longueur_robot"));
            radius = Integer.parseInt(config.getProperty("rayon_robot_adverse"));
		}
		catch (ConfigPropertyNotFoundException e)
		{
    		log.debug("Revoir le code : impossible de trouver la propriété "+e.getPropertyNotFound());;
		}
	}

	
	/**
	 *  On enleve les obstacles qu'on ne voit pas
	 */
	private void removeObstacle()
	{
		//TODO la méthode
		// enlever les obstacles qu'on devrait voir mais qu'on ne detecte plus

        //On limite la suppression à 80% de la distance détectée
        int distanceLF = (int)(USvalues.get(0)*0.8);
        int distanceRF = (int)(USvalues.get(1)*0.8);
        int distanceLB = (int)(USvalues.get(2)*0.8);
        int distanceRB = (int)(USvalues.get(3)*0.8);

        mTable.getObstacleManager().removeNonDetectedObstacles(positionLF, (mRobot.getOrientation()+angleLF), distanceLF, detectionAngle);
        mTable.getObstacleManager().removeNonDetectedObstacles(positionRF(), (mRobot.getOrientation()+angleRF), distanceRF, detectionAngle);
        mTable.getObstacleManager().removeNonDetectedObstacles(positionLB, (mRobot.getOrientation()+angleLB), distanceLB, detectionAngle);
        mTable.getObstacleManager().removeNonDetectedObstacles(positionRB, (mRobot.getOrientation()+angleRB), distanceRB, detectionAngle);


	}
}
