package threads;

import enums.USsensors;
import exceptions.ConfigPropertyNotFoundException;
import exceptions.serial.SerialConnexionException;
import graphics.Window;
import robot.RobotReal;
import robot.cardsWrappers.SensorsCardWrapper;
import smartMath.Vec2;
import table.Table;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
     * Permet de désactiver les capteurs de la porte que récupère du sable, évite de récupérer des fausses valeurs
     */
    private static boolean modeBorgne = false;

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

    private BufferedWriter out;
    private final boolean debug = false;
	
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
    private final double angleLF = -0.26;
    private final double angleRF = -0.26;
    private final double angleLB = 0.26;
    private final double angleRB = 0.26;


    /**
     * Positions relatives au centre du robot TODO A changer !
     */

    private final Vec2 positionLF = new Vec2(170, -140);
    private final Vec2 positionRFClosed = new Vec2(170, 140);
    private final Vec2 positionLB = new Vec2(-120,-75);
    private final Vec2 positionRB = new Vec2(-120,75);


    /**
     * Valeurs des capteurs US {avant-gauche, avant-droit, arrière gauche, arrière-droit}
     */
    ArrayList<Integer> USvalues = new ArrayList<>(4);

    /**
     * Valeurs de capteurs modifiées pour la suppression d'obstacle
     * Ainsi si l'un des capteurs nous indique 4km, c'est sûrement qu'il n'y a rien devant lui
     * On sépare ce qui sert à détecter de ce qui sert à ne pas détecter (oui c'est trop méta pour toi...)
     * PS : Si il indique 4 km, y'a un pb hein...
     */
    ArrayList<Integer> USvaluesForDeletion = new ArrayList<>();

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
		updateConfig();

        try
        {
            File file = new File("us.txt");
            if (!file.exists()) {
                //file.delete();
                file.createNewFile();
            }
            out = new BufferedWriter(new FileWriter(file));
        } catch (IOException e) {
            e.printStackTrace();
        }

        while(mSensorsCardWrapper.isJumperAbsent())
        {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        while(!mSensorsCardWrapper.isJumperAbsent())
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
				log.debug("Stop du thread capteurs");
				return;
			}
            //long time = System.currentTimeMillis();

			getDistances();


            if( !USvalues.contains(-1)) // si on n'a pas spammé
			{
				// On enleve les obstacles qu'on sait absents de la table : si le robot ennemi a bougé,
				// On l'enleve de notre memoire
                mRobot.getPosition();
                removeObstacle();

                for(int i=0 ; i<USvalues.size(); i++)
                {
                    if(USvalues.get(i) != 0)
                        USvalues.set(i, USvalues.get(i)/*+radius/2*/);
                }

				//ajout d'obstacles mobiles dans l'obstacleManager
				addObstacle();
                //log.debug("Temps de gogolitude du ThreadCapteurs : "+(System.currentTimeMillis()-time)+" ms");
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
        if(USvalues.get(0) != 0 && USvalues.get(1) != 0)
            addFrontObstacleBoth();
        else if((USvalues.get(0) != 0 || USvalues.get(1) != 0))
            addFrontObstacleSingle(USvalues.get(0) != 0);

        if(USvalues.get(2) != 0 && USvalues.get(3) != 0 )
            addBackObstacleBoth();
        else if((USvalues.get(2) != 0 || USvalues.get(3) != 0))
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
            positionSaloperie_1 = changeRef(relativePositionSaloperie_1, mRobot.getPositionFast(), mRobot.getOrientationFast());

            mTable.getObstacleManager().addObstacle(positionSaloperie_1);
        }
        else //Les deux capteurs ne semblent pas détecter le même ennemi, on en ajoute 2 !
        {
            //Position relative du premier ennemi
            relativePositionSaloperie_1.x = (int)(USvalues.get(0)*Math.cos(angleLF)+positionLF.x);
            relativePositionSaloperie_1.y = (int)(-USvalues.get(0)*Math.sin(angleLF)-distanceBetweenSensors/2);
            positionSaloperie_1 = changeRef(relativePositionSaloperie_1, mRobot.getPositionFast(), mRobot.getOrientationFast());

            //Position relative du second ennemi
            relativePositionSaloperie_2.x = (int)(USvalues.get(1)*Math.cos(angleRF)+positionRF().x);
            relativePositionSaloperie_2.y = (int)(USvalues.get(1)*Math.sin(angleRF)+distanceBetweenSensors/2);
            positionSaloperie_2 = changeRef(relativePositionSaloperie_2, mRobot.getPositionFast(), mRobot.getOrientationFast());

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
        return positionRFClosed.plusNewVector(new Vec2(0,mRobot.doorIsOpen ? 110 : 0));
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
            positionSaloperie_1 = changeRef(relativePositionSaloperie_1, mRobot.getPositionFast(), mRobot.getOrientationFast());

            mTable.getObstacleManager().addObstacle(positionSaloperie_1);
        }
        else //Les deux capteurs ne semblent pas détecter le même ennemi, on en ajoute 2 !
        {
            //Position relative du premier ennemi
            relativePositionSaloperie_1.x = (int)(USvalues.get(2)*Math.cos(angleLB)+positionLB.x);
            relativePositionSaloperie_1.y = (int)(-USvalues.get(2)*Math.sin(angleLB)-distanceBetweenSensors/2);
            positionSaloperie_1 = changeRef(relativePositionSaloperie_1, mRobot.getPositionFast(), mRobot.getOrientationFast());

            //Position relative du second ennemi
            relativePositionSaloperie_2.x = (int)(USvalues.get(3)*Math.cos(angleRB)+positionRB.x);
            relativePositionSaloperie_2.y = (int)(USvalues.get(3)*Math.sin(angleRB)+distanceBetweenSensors/2);
            positionSaloperie_2 = changeRef(relativePositionSaloperie_2, mRobot.getPositionFast(), mRobot.getOrientationFast());

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
            positionSaloperie_1 = changeRef(relativePositionSaloperie_1, mRobot.getPositionFast(), mRobot.getOrientationFast());

            mTable.getObstacleManager().addObstacle(positionSaloperie_1);
        }
        else
        {
            relativePositionSaloperie_2.x = (int)(distance*Math.cos(angleRF)+positionRF().x)-100;
            relativePositionSaloperie_2.y = (int)(distance*Math.sin(angleRF)+distanceBetweenSensors/2);
            positionSaloperie_2 = changeRef(relativePositionSaloperie_2, mRobot.getPositionFast(), mRobot.getOrientationFast());

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
            positionSaloperie_1 = changeRef(relativePositionSaloperie_1, mRobot.getPositionFast(), mRobot.getOrientationFast());

            mTable.getObstacleManager().addObstacle(positionSaloperie_1);
        }
        else
        {
            relativePositionSaloperie_2.x = (int)(distance*Math.cos(angleRB)+positionRB.x);
            relativePositionSaloperie_2.y = (int)(distance*Math.sin(angleRB)+distanceBetweenSensors/2);
            positionSaloperie_2 = changeRef(relativePositionSaloperie_2, mRobot.getPositionFast(), mRobot.getOrientationFast());

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

            if(this.debug)
            {
                try {
                    out.write(USvalues.get(0).toString());
                    out.newLine();
                    out.write(USvalues.get(1).toString());
                    out.newLine();
                    out.write(USvalues.get(2).toString());
                    out.newLine();
                    out.write(USvalues.get(3).toString());
                    out.newLine();
                    out.newLine();
                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(symetry) //Inversion gauche/droite pour symétriser
            {
                int temp = USvalues.get(0);
                USvalues.set(0, USvalues.get(1));
                USvalues.set(1, temp);
                temp = USvalues.get(2);
                USvalues.set(2, USvalues.get(3));
                USvalues.set(3, temp);
            }

            mRobot.setUSvalues(USvalues);

            USvaluesForDeletion.clear();
            for(int i=0 ; i<4 ; i++)
            {
                USvaluesForDeletion.add((int)(USvalues.get(i).intValue()*0.8));
            }
            
            for(int i=0 ; i<USvalues.size() ; i++)
            {
                //on met tout les capteurs qui detectent un objet DANS le robot ou à plus de maxSensorRange a 0
                // TO/DO : a passer en traitement de bas niveau ?
                if ( USvalues.get(i) > maxSensorRange)
                {
                    USvalues.set(i, 0);
                    USvaluesForDeletion.set(i, (int)(maxSensorRange*0.9));
                }
                else if (USvalues.get(i) < minSensorRange)
                {
                    USvalues.set(i, 0);
                    USvaluesForDeletion.set(i, 0);
                }
                else if(i == 1 && modeBorgne)
                {
                    USvalues.set(1, 0);
                    USvaluesForDeletion.set(i, 0);
                }
                else
                {
                    USvalues.set(i, USvalues.get(i)+radius);
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
			//Integer.parseInt(config.getProperty("rayon_robot_adverse"));
			
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
		// enlever les obstacles qu'on devrait voir mais qu'on ne detecte plus

        mTable.getObstacleManager().removeNonDetectedObstacles(positionLF, (mRobot.getOrientationFast()+angleLF), USvaluesForDeletion.get(0), detectionAngle);
        mTable.getObstacleManager().removeNonDetectedObstacles(positionRF(), (mRobot.getOrientationFast()-angleRF), USvaluesForDeletion.get(1), detectionAngle);
        mTable.getObstacleManager().removeNonDetectedObstacles(positionLB, (mRobot.getOrientationFast()+angleLB), USvaluesForDeletion.get(2), detectionAngle);
        mTable.getObstacleManager().removeNonDetectedObstacles(positionRB, (mRobot.getOrientationFast()-angleRB), USvaluesForDeletion.get(3), detectionAngle);


	}

    /**
     * Active/desactive le mode borgne
     * @param value oui/non
     */
    public static void modeBorgne(boolean value)
    {
        ThreadSensor.modeBorgne = value;
    }
}
