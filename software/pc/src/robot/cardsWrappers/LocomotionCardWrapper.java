package robot.cardsWrappers;


import robot.serial.SerialConnexion;
import utils.*;
import container.Service;
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
	@SuppressWarnings("unused")
	private Log log;

	/**
	 * connexion série avec la carte d'asservissement
	 */
	private SerialConnexion locomotionCardSerial;
		
	/**
	 *  nombre de miliseconde de tolérance entre la détection d'un patinage et la levée de l'exeption. Trop basse il y aura des faux positifs, trop haute on va forcer dans les murs pendant longtemps
	 */
	int blockedTolerancy = 200;//TODO: mettre dans le fichier de config

	/**
	 * Construit la surchouche de la carte d'asservissement
	 * @param log le système de log ou écrire  
	 * @param serial la connexion série avec la carte d'asservissement
	 */
	public LocomotionCardWrapper(Log log, SerialConnexion serial)
	{
		this.log = log;
		this.locomotionCardSerial = serial;		
	}
	
	public void updateConfig()
	{
	}	
	
	/** 
	 * Regarde si le robot bouge effectivement.
	 * Provoque un appel série pour avoir des information a jour. Cette méthode est demande donc un peu de temps. 
	 * @return vrai si le robot bouge, faux si le robot est immobile
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
		String chaines[] = {"d", Double.toString(distance)};
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
		float anggleTruncated = (float)angle;
		String chaines[] = {"t", Float.toString(anggleTruncated)};
		locomotionCardSerial.communiquer(chaines, 0);		
	}
	
	/**
	 * Arrête le robot
	 * @throws SerialConnexionException en cas de problème de communication avec la carte d'asservissement
	 */
	public void immobilise() throws SerialConnexionException
	{
		// Je bourrine, tu bourrines, il bourrine, ...
        disableTranslationnalFeedbackLoop();
        disableRotationnalFeedbackLoop();
		locomotionCardSerial.communiquer("stop", 0);
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
		String chaines[] = {"cx", Integer.toString(x)};
		locomotionCardSerial.communiquer(chaines, 0);
	}

	/**
	 * Ecrase la position y du robot au niveau de la carte
	 * @param y la nouvelle ordonnée que le robot doit considérer avoir sur la table
	 * @throws SerialConnexionException en cas de problème de communication avec la carte d'asservissement
	 */
	public void setY(int y) throws SerialConnexionException
	{
		String chaines[] = {"cy", Integer.toString(y)};
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
		String chaines[] = {"co", Double.toString(orientation)};
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
		double 	kp,	// valeur du correcteur proportionnel
				kd; // valeur du correcteur dérivé
		
		// échelonne les valeurs des correcteurs en fonction de la nouvelle valeur maximum que peut prenvent prendre les pwm des moteurs
		if(pwmMax >= 195)
		{
			kp = 0.55;
			kd = 27.0;
		}
		else if(pwmMax >= 165)
		{
			kp = 0.52;
			kd = 17.0;
		}
		else if(pwmMax >= 145)
		{
			kp = 0.52;
			kd = 17.0;
		}
		else if(pwmMax >= 115)
		{
			kp = 0.45;
			kd = 12.0;
		}
		else if(pwmMax >= 85)
		{
			kp = 0.45;
			kd = 12.5;
		}
		else if(pwmMax >= 55)
		{
			kp = 0.5;
			kd = 4.0;
		}
		else
		{
			kp = 1.15;
			kd = 3.0;
		}
		
		// envois a la carte d'asservissement les nouvelles valeurs des correcteurs et le nouveau maximum des pwm
		String chaines[] = {"ctv", Double.toString(kp), Double.toString(kd), Integer.toString(pwmMax)};
		locomotionCardSerial.communiquer(chaines, 0);			
	}

	/**
	 * Modifie la vitesse en rotation du robot sur la table
	 * @param pwmMax la nouvelle valeur maximum que peut prenvent prendre les pwm des moteurs lors d'une rotation
	 * @throws SerialConnexionException en cas de problème de communication avec la carte d'asservissement
	 */
	public void setRotationnalSpeed(int pwmMax) throws SerialConnexionException
	{
		double 	kp, // valeur du correcteur proportionnel
				kd; // valeur du correcteur dérivé
	
		// échelonne les valeurs des correcteurs en fonction de la nouvelle valeur maximum que peut prenvent prendre les pwm des moteurs
		if(pwmMax > 155)
		{
			kp = 2.0;
			kd = 50.0;
		}
		else if(pwmMax > 115)
		{
			kp = 0.85;
			kd = 25.0;
		}
		else if(pwmMax > 85)
		{
			kp = 1.0;
			kd = 15.0;
		}
		else
		{
			kp = 2.0;
			kd = 14.0;
		}

		// envois a la carte d'asservissement les nouvelles valeurs des correcteurs et le nouveau maximum des pwm
		String chaines[] = {"crv", Double.toString(kp), Double.toString(kd), Integer.toString(pwmMax)};
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
		String chaines[] = {"ctv", Double.toString(kp), Double.toString(kd), Integer.toString(pwm_max)};
		locomotionCardSerial.communiquer(chaines, 0);
	}

	/**
	 * envois a la carte d'asservissement de nouvelles valeurs pour les correcteurs et un nouveau maximum pour les pwm lors d'une rotation
	 * @param kp nouvelle valeur du correcteur proportionnel
	 * @param kd nouvelle valeur du correcteur dérivé 
	 * @param pwm_max a nouvelle valeur maximum que peut prenvent prendre les pwm des moteurs lors d'une rotation
	 * @throws SerialConnexionException en cas de problème de communication avec la carte d'asservissement
	 */
	public void changeRotationnalFeedbackParameters(double kp, double kd, int pwm_max) throws SerialConnexionException
	{
		String chaines[] = {"crv", Double.toString(kp), Double.toString(kd), Integer.toString(pwm_max)};
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
		// on envois "?infos" et on lis double (dans l'ordre : abscisse, ordonnée, orientation)
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
	 */
	public boolean[] isRobotMovingAndAbnormal() throws SerialConnexionException
	{
		// on demande a la carte des information a jour
		// on envois "f" et on lis double (dans l'ordre : bouge, est anormal)
		String[] infosBuffer = locomotionCardSerial.communiquer("f", 2);
		boolean[] parsedInfos = new boolean[2];
		for(int i = 0; i < 2; i++)
		    parsedInfos[i] = Boolean.parseBoolean(infosBuffer[i]);
		return parsedInfos;
	}
	
}
