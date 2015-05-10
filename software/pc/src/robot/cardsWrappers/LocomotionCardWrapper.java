package robot.cardsWrappers;


import robot.serial.SerialConnexion;
import utils.*;
import container.Service;
import exceptions.ConfigPropertyNotFoundException;
import exceptions.serial.SerialConnexionException;

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
			// TODO Auto-generated catch block
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
			log.debug("Code à revoir  : impossible de ttrouver la propriete "+e.getPropertyNotFound(), this);
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
		String chaines[] = {"d", Float.toString(distanceTruncated)};
		locomotionCardSerial.communiquer(chaines, 0);
	}

	/** 
	 * Fait tourner le robot de maniere absolue. Méthode non bloquante
	 * @param angle
	 * @throws SerialConnexionException en cas de problème de communication avec la carte d'asservissement
	 */
	public void turn(double angle) throws SerialConnexionException
	{
		// tronque l'angle que l'on envoit a la série pour éviter les overflows
		float angleTruncated = (float)angle;
		String chaines[] = {"t", Float.toString(angleTruncated)};
		locomotionCardSerial.communiquer(chaines, 0);		
	}
	public void turnRelative(double angle) throws SerialConnexionException
	{
		// tronque l'angle que l'on envoit a la série pour éviter les overflows
		float angleTruncated = (float)angle;
		String chaines[] = {"t3", Float.toString(angleTruncated)};
		locomotionCardSerial.communiquer(chaines, 0);
	}
	
	/**
	 * Arrête le robot
	 * @throws SerialConnexionException en cas de problème de communication avec la carte d'asservissement
	 */
	public void immobilise() throws SerialConnexionException
	{
		log.critical("Immobilisation du robot", this);
		
        disableTranslationnalFeedbackLoop();
        disableRotationnalFeedbackLoop();
        
        while(isRobotMoving())
        {
        	Sleep.sleep(delayBetweenSend); // On attend d'etre arreté
        }
        
        locomotionCardSerial.communiquer("stop", 0);// On s'asservit sur la position actuelle
        
        enableTranslationnalFeedbackLoop();
        enableRotationnalFeedbackLoop();
	}
	
	/**
	 * Ecrase la position x du robot au niveau de la carte
	 * @param x la nouvelle abscisse que le robot doit considérer avoir sur la table
	 * @throws SerialConnexionException en cas de problème de communication avec la carte d'asservissement
	 */
	public void setX(int x) throws SerialConnexionException
	{
		float floatX=(float)x; //On transtype car la serie veut des Floats <3
		String chaines[] = {"cx", Float.toString(floatX)};
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
		String chaines[] = {"cy", Float.toString(floatY)};
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
		float floatOrientation =(float) orientation; //On transtype car la serie veut des Floats (T_T)
		String chaines[] = {"co", Float.toString(floatOrientation)};
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
	 * Désactive l'asservissement en rotation du robot
	 * @throws SerialConnexionException en cas de problème de communication avec la carte d'asservissement
	 */
	public void disableRotationnalFeedbackLoop() throws SerialConnexionException
	{
		locomotionCardSerial.communiquer("cr0", 0);
	}

	/**
	 * Modifie la vitesse en translation du robot sur la table
	 * @param pwmMax la nouvelle valeur maximum que peut prenvent prendre les pwm des moteurs lors d'une translation
	 * @throws SerialConnexionException en cas de problème de communication avec la carte d'asservissement
	 */
	public void setTranslationnalSpeed(int pwmMax) throws SerialConnexionException
	{
		// envoie a la carte d'asservissement le nouveau maximum du pwm
		String chaines[] = {"ctv", Integer.toString(pwmMax)};
		locomotionCardSerial.communiquer(chaines, 0);			
	}

	/**
	 * Modifie la vitesse en rotation du robot sur la table
	 * @param pwmMax la nouvelle valeur maximum que peut prenvent prendre les pwm des moteurs lors d'une rotation
	 * @throws SerialConnexionException en cas de problème de communication avec la carte d'asservissement
	 */
	public void setRotationnalSpeed(int pwmMax) throws SerialConnexionException
	{
		// envoie a la carte d'asservissement le nouveau maximum du pwm
		String chaines[] = {"crv", Integer.toString(pwmMax)};
		locomotionCardSerial.communiquer(chaines, 0);
	}
	
	/**
	 * envois a la carte d'asservissement de nouvelles valeurs pour les correcteurs et un nouveau maximum pour les pwm lors d'une translation
	 * @param kp nouvelle valeur du correcteur proportionnel
	 * @param kd nouvelle valeur du correcteur dérivé 
	 * @param pwm_max a nouvelle valeur maximum que peut prenvent prendre les pwm des moteurs lors d'une translation
	 * @throws SerialConnexionException en cas de problème de communication avec la carte d'asservissement
	 */
	public void changeTranslationnalFeedbackParameters(double kp, double kd, int pwm_max) throws SerialConnexionException
	{
		String chaines[] = {"ctv", Float.toString((float)kp), Float.toString((float)kd), Float.toString((float)pwm_max)};
		locomotionCardSerial.communiquer(chaines, 0);
	}

	/**
	 * envoie a la carte d'asservissement de nouvelles valeurs pour les correcteurs et un nouveau maximum pour les pwm lors d'une rotation
	 * @param kp nouvelle valeur du correcteur proportionnel
	 * @param kd nouvelle valeur du correcteur dérivé 
	 * @param pwm_max a nouvelle valeur maximum que peut prenvent prendre les pwm des moteurs lors d'une rotation
	 * @throws SerialConnexionException en cas de problème de communication avec la carte d'asservissement
	 */
	public void changeRotationnalFeedbackParameters(double kp, double kd, int pwm_max) throws SerialConnexionException
	{
		String chaines[] = {"crv", Float.toString((float)kp), Float.toString((float)kd), Integer.toString(pwm_max)};
		locomotionCardSerial.communiquer(chaines, 0);
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
		    parsedInfos[i] = Float.parseFloat(infosBuffer[i]);

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
				log.debug("Probleme de lecture de f", this);
		}
		return parsedInfos;
	}

}
