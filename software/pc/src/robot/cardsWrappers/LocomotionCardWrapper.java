package robot.cardsWrappers;


import java.util.Locale;

import container.Service;
import enums.TurningStrategy;
import exceptions.ConfigPropertyNotFoundException;
import exceptions.serial.SerialConnexionException;
import robot.serial.SerialConnexion;
import utils.Config;
import utils.Log;
import utils.Sleep;

import java.util.Locale;

/**
 *  Dialogue avec la carte d'asservissement en position du robot.
 *  Pour les déplacements intelligents, voir Locomotion
 * @author PF, marsu
 */

public class LocomotionCardWrapper implements Service
{

	/**
	 *  pour écrire dans le log en cas de problème
	 */
	private Log log;

	/**
	 * connexion série avec la carte d'asservissement
	 */
	private SerialConnexion locomotionCardSerial;
	
	/**
	 * Le fichier de configuration
	 */
	private Config config;
		
	/**
	 *  nombre de miliseconde de tolérance entre la détection d'un patinage et la levée de l'exeption. Trop basse il y aura des faux positifs, trop haute on va forcer dans les murs pendant longtemps
	 */
	int blockedTolerancy;
	
	/**
	 * Temps d'attente entre deux envois à la serie en ms
	 */
	private int delayBetweenSend = 100; 
	
	public int compteur=1;
	
	/**
	 * Permet de gérer le nombre de tentatives de communication
	 * dans la méthode éponyme
	 */
	public int counterGetCurrentPositionAndOrientation=-1;

	/**
	 * Construit la surchouche de la carte d'asservissement
	 * @param log le système de log ou écrire  
	 * @param serial la connexion série avec la carte d'asservissement
	 * @param config 
	 */
	public LocomotionCardWrapper(Log log, SerialConnexion serial, Config config)
	{
		this.log = log;
		this.locomotionCardSerial = serial;		
		this.config = config;
		
		// comme l'asser n'est pas activé par défaut sur la STM, on l'active ici
        try
        {
    		enableTranslationnalFeedbackLoop();
			enableRotationnalFeedbackLoop();
		} 
        catch (SerialConnexionException e)
        {
			e.printStackTrace();
		}
	}
	
	public void updateConfig()
	{
		try
		{
			blockedTolerancy=(Integer.parseInt(config.getProperty("tolerance_patinage_ms")));
		} 
		catch (ConfigPropertyNotFoundException e)
		{
			log.debug("Code à revoir  : impossible de trouver la propriete "+e.getPropertyNotFound());
		}
	}	
	
	/** 
	 * Regarde si le robot bouge effectivement.
	 * Provoque un appel série pour avoir des information a jour. Cette méthode est demande donc un peu de temps. 
	 * @return truz si le robot bouge
	 * @throws SerialConnexionException en cas de problème de communication avec la carte d'asservissement
	 */

	public boolean isRobotMoving() throws SerialConnexionException
	{
	   return isRobotMovingAndAbnormal()[0];       	
	}
	
	/** 
	 * Fait avancer le robot. Méthode non bloquante
	 * @param distance distance a parcourir par le robot. Une valeur négative fera reculer le robot, une valeur positive le fera avancer.
	 * @throws SerialConnexionException en cas de problème de communication avec la carte d'asservissement
	 */
	public void moveLengthwise(double distance) throws SerialConnexionException
	{
		float distanceTruncated = (float)distance;
		String chaines[] = {"d", String.format(Locale.US, "%.3f", distanceTruncated)};
		locomotionCardSerial.communiquer(chaines, 0);
	}

    /**
     * Fait suivre un arc au robot. Méthode non bloquante
     * @param length la longueur de l'arc à suivre
     * @param radius le rayon de courbure
     * @throws SerialConnexionException pb de comm
     */
	public void moveArc(double length, double radius) throws SerialConnexionException
    {
        String chaines[] = {"dc", Integer.toString((int)length), Integer.toString((int)radius)};
        locomotionCardSerial.communiquer(chaines, 0);
	}

	/**
	 * Fait tourner le robot de maniere absolue. Méthode non bloquante
	 * utilise TurningStrategy.FASTEST
	 * @param angle l'angle de tour
	 * @throws SerialConnexionException en cas de problème de communication avec la carte d'asservissement
	 */
	public void turn(double angle) throws SerialConnexionException
	{
		turn(angle,TurningStrategy.FASTEST);
	}
	/** 
	 * Fait tourner le robot de maniere absolue. Méthode non bloquante
	 * @param angle l'angle de tour
	 * @throws SerialConnexionException en cas de problème de communication avec la carte d'asservissement
	 */
	public void turn(double angle, TurningStrategy turning) throws SerialConnexionException
	{
		// tronque l'angle que l'on envoit a la série pour éviter les overflows
		float angleTruncated = (float)angle;
		if(turning == TurningStrategy.FASTEST) {
			String chaines[] = {"t", String.format(Locale.US, "%.3f", angleTruncated)};
			locomotionCardSerial.communiquer(chaines, 0);
		}
		else if(turning == TurningStrategy.RIGHT_ONLY)
		{
			String chaines[] = {"tor", String.format(Locale.US, "%.3f", angleTruncated)};
			locomotionCardSerial.communiquer(chaines, 0);
		}
		else if(turning == TurningStrategy.LEFT_ONLY)
		{
			String chaines[] = {"tol", String.format(Locale.US, "%.3f", angleTruncated)};
			locomotionCardSerial.communiquer(chaines, 0);
		}
	}
	public void turnRelative(double angle) throws SerialConnexionException
	{
		// tronque l'angle que l'on envoit a la série pour éviter les overflows
		float angleTruncated = (float)angle;
		String chaines[] = {"t3", String.format(Locale.US, "%.3f", angleTruncated)};
		locomotionCardSerial.communiquer(chaines, 0);
	}
	
	/**
	 * Arrête le robot
	 * @throws SerialConnexionException en cas de problème de communication avec la carte d'asservissement
	 */
	public void immobilise() throws SerialConnexionException
	{
		log.warning("Immobilisation du robot");
        
        
        locomotionCardSerial.communiquer("stop", 0);// On s'asservit sur la position actuelle
        while(isRobotMoving())
        {
        	Sleep.sleep(delayBetweenSend); // On attend d'etre arreté
        }
        
	}
	
	/**
	 * Ecrase la position x du robot au niveau de la carte
	 * @param x la nouvelle abscisse que le robot doit considérer avoir sur la table
	 * @throws SerialConnexionException en cas de problème de communication avec la carte d'asservissement
	 */
	public void setX(int x) throws SerialConnexionException
	{
		float floatX=(float)x; //On transtype car la serie veut des Floats <3
		String chaines[] = {"cx", String.format(Locale.US, "%.3f", floatX)};
		locomotionCardSerial.communiquer(chaines, 0);
	}

	/**
	 * Ecrase la position y du robot au niveau de la carte
	 * @param y la nouvelle ordonnée que le robot doit considérer avoir sur la table
	 * @throws SerialConnexionException en cas de problème de communication avec la carte d'asservissement
	 */
	public void setY(int y) throws SerialConnexionException
	{
		float floatY=(float)y;//On transtype car la serie veut des Floats 
		String chaines[] = {"cy", String.format(Locale.US, "%.3f", floatY)};
		locomotionCardSerial.communiquer(chaines, 0);	
	}
	
	/**
	 * Ecrase l'orientation du robot au niveau de la carte
	 * @param orientation la nouvelle orientation que le robot doit considérer avoir sur la table
	 * @param x la nouvelle orientation que le robot doit considérer avoir sur la table
	 * @throws SerialConnexionException en cas de problème de communication avec la carte d'asservissement
	 */
	public void setOrientation(double orientation) throws SerialConnexionException
	{
		//log.debug("setOrientation "+orientation);
		float floatOrientation =(float) orientation; //On transtype car la serie veut des Floats (T_T)
		String chaines[] = {"co", String.format(Locale.US, "%.3f", floatOrientation)};
		locomotionCardSerial.communiquer(chaines, 0);
	}
	
	/**
	 * Active l'asservissement en translation du robot
	 * @throws SerialConnexionException en cas de problème de communication avec la carte d'asservissement
	 */
	public void enableTranslationnalFeedbackLoop() throws SerialConnexionException
	{
		locomotionCardSerial.communiquer("ct1", 0);
	}

	/**
	 * Active l'asservissement en rotation du robot
	 * @throws SerialConnexionException en cas de problème de communication avec la carte d'asservissement
	 */
	public void enableRotationnalFeedbackLoop() throws SerialConnexionException
	{
		locomotionCardSerial.communiquer("cr1", 0);
	}

	/**
	 * Désactive l'asservissement en translation du robot
	 * @throws SerialConnexionException en cas de problème de communication avec la carte d'asservissement
	 */
	public void disableTranslationnalFeedbackLoop() throws SerialConnexionException
	{
		locomotionCardSerial.communiquer("ct0", 0);
	}

	/**
	 * Désactive l'asservissement en vitesse du robot
	 * @throws SerialConnexionException en cas de problème de communication avec la carte d'asservissement
	 */
	public void disableSpeedFeedbackLoop() throws SerialConnexionException
	{
		locomotionCardSerial.communiquer("cv0", 0);
	}

	/**
	 * Désactive l'asservissement en rotation du robot
	 * @throws SerialConnexionException en cas de problème de communication avec la carte d'asservissement
	 */
	public void disableRotationnalFeedbackLoop() throws SerialConnexionException
	{
		locomotionCardSerial.communiquer("cr0", 0);
	}

	/**
	 * Modifie la vitesse en translation du robot sur la table
	 * @param speed la nouvelle valeur maximum que peut prenvent prendre les pwm des moteurs lors d'une translation
	 * @throws SerialConnexionException en cas de problème de communication avec la carte d'asservissement
	 */
	public void setTranslationnalSpeed(float speed) throws SerialConnexionException
	{
		// envoie a la carte d'asservissement le nouveau maximum du pwm
		String chaines[] = {"ctv", String.format(Locale.US, "%.3f", speed)};
		locomotionCardSerial.communiquer(chaines, 0);			
	}

	/**
	 * Modifie la vitesse en rotation du robot sur la table
	 * @param rotationSpeed la nouvelle valeur maximum que peut prenvent prendre les pwm des moteurs lors d'une rotation
	 * @throws SerialConnexionException en cas de problème de communication avec la carte d'asservissement
	 */
	public void setRotationnalSpeed(double rotationSpeed) throws SerialConnexionException
	{
		// envoie a la carte d'asservissement le nouveau maximum du pwm
		String chaines[] = {"crv", String.format(Locale.US, "%.3f", (float)rotationSpeed)};
		locomotionCardSerial.communiquer(chaines, 0);
	}
	


    /**
     * Change le type de mouvement forcé/normal
     * @param choice true pour forcer les mouvements
     */
	public synchronized void setForceMovement(boolean choice) throws SerialConnexionException
	{
		if(choice)
		{
			String chaines[] = {"efm"};
			locomotionCardSerial.communiquer(chaines, 0);
		}
		else
		{
			String chaines[] = {"dfm"};
			locomotionCardSerial.communiquer(chaines, 0);
		}
	}

	/**
	 * Change l'accélération en plus fluide mais plus lente
     */
	public synchronized void setSmoothAcceleration(boolean choice) throws SerialConnexionException
	{
		if(choice)
		{
			String chaines[] = {"ssa"};
			locomotionCardSerial.communiquer(chaines, 0);
		}
		else
		{
			String chaines[] = {"sva"};
			locomotionCardSerial.communiquer(chaines, 0);
		}
	}


	/**
	 * Demande a la carte d'asservissement la position et l'orientation courrante du robot sur la table.
	 * Renvoie x, y et orientation du robot (x en mm, y en mm, et orientation en radiants) 
	 * @return un tableau de 3 cases: [x, y, orientation]
	 * @throws SerialConnexionException en cas de problème de communication avec la carte d'asservissement
	 */
	public float[] getCurrentPositionAndOrientation() throws SerialConnexionException
	{
		// on demande a la carte des information a jour
		// on envois "?xyo" et on lis double (dans l'ordre : abscisse, ordonnée, orientation)
		String[] infosBuffer = locomotionCardSerial.communiquer("?xyo", 3);
		float[] parsedInfos = new float[3];
		for(int i = 0; i < 3; i++)
		{
			try{
			parsedInfos[i] = Float.parseFloat(infosBuffer[i]);
			} catch (NumberFormatException e)
			{
				return null;
			}
		}
		return parsedInfos;
	}

	/**
	 * Ferme la connexion série avec la carte d'asservissements
	 */
	public void closeLocomotion()
	{
		locomotionCardSerial.close();
	}
	

	/**
	 * Eteint la STM
	 * Attention, la STM ne répondra plus jusqu'a ce qu'elle soit manuellement ralummée
	 * @throws SerialConnexionException en cas de problème de communication avec la carte d'asservissement
	 */
	public void shutdownSTM() throws SerialConnexionException
	{
		locomotionCardSerial.communiquer("poweroff", 0);
	}
	
	/**
	 *  Verifie si le robot est arrivé et si c'est anormal
	 *  @return Les informations sous forme d'un tableau de booleens
	 *  lecture : [est ce qu'on bouge][est ce que c'est Anormal]
	 * @throws SerialConnexionException 
	 */
	public boolean[] isRobotMovingAndAbnormal() throws SerialConnexionException
	{
		// on demande a la carte des information a jour
		// on envois "f" et on lis double (dans l'ordre : bouge, est anormal)
		String[] infosBuffer = locomotionCardSerial.communiquer("f", 2);
		boolean[] parsedInfos = new boolean[2];
		for(int i = 0; i < 2; i++)
		{
			if( infosBuffer[i].equals("0") )
			   parsedInfos[i] = false;
			else if ( infosBuffer[i].equals("1") )
				 parsedInfos[i]=true;
			else
				log.debug("Probleme de lecture de f");
		}
		return parsedInfos;
	}

    /**
     * Active l'asservissement en vitesse du robot
     * @throws SerialConnexionException en cas de problème de communication avec la carte d'asservissement
     */
	public void enableSpeedFeedbackLoop() throws SerialConnexionException {
		locomotionCardSerial.communiquer("cv1", 0);
	}
}
