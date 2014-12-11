package robot.cardsWrappers;

import java.util.Hashtable;
import java.util.Random;

import robot.serial.SerialConnexion;
import utils.*;
import container.Service;
import exceptions.Locomotion.BlockedException;
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
	 * Stockage des informations courrantes de l'asservissement. 
	 * Dès la fin du constructeur, les clefs sont: 
	 *  - PWMmoteurGauche
	 *  - PWMmoteurDroit
	 *  - erreur_rotation
	 *  - erreur_translation
	 *  - derivee_erreur_rotation
	 *  - derivee_erreur_translation
	 */
	private Hashtable<String, Integer> feedbackLoopStatistics;
		
	/**
	 *  en cas de bloquage, date a laquelle le blocage a commencé
	 */
	private long blockageStartTimestamp;
	
	/**
	 *  utilisé par raiseExeptionIfBlocked, pour savoir si lors du dernier appel de raiseExeptionIfBlocked, la robot était déja bloqué (auquel cas il ne faut plus considérer que c'est le début du bloquage)
	 */
    private boolean wasBlockedAtPreviousCall = false;
    

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
		
		feedbackLoopStatistics = new Hashtable<String, Integer>();
		feedbackLoopStatistics.put("PWMmoteurGauche", 0);
		feedbackLoopStatistics.put("PWMmoteurDroit", 0);
		feedbackLoopStatistics.put("erreur_rotation", 0);
		feedbackLoopStatistics.put("erreur_translation", 0);
		feedbackLoopStatistics.put("derivee_erreur_rotation", 0);
		feedbackLoopStatistics.put("derivee_erreur_translation", 0);
		feedbackLoopStatistics.put("inverse_erreur_translation_integrale", 100);
		
	}
	
	public void updateConfig()
	{
	}	
	
	/**
	 * lève BlockedException si le robot bloque (c'est-à-dire que les moteurs forcent mais que le robot ne bouge pas).
	 * @throws BlockedException si le robot est mécaniquement bloqué contre un obstacle qui l'empèche d'avancer plus loin
	 */
	public void raiseExeptionIfBlocked() throws BlockedException
	{
		
		// demande des information sur l'asservissement du robot
		int pwmLeftMotor = feedbackLoopStatistics.get("PWMmoteurGauche");
		int pwmRightMotor = feedbackLoopStatistics.get("PWMmoteurDroit");
		int derivatedRotationnalError = feedbackLoopStatistics.get("derivee_erreur_rotation");
		int derivatedTranslationnalError = feedbackLoopStatistics.get("derivee_erreur_translation");
		
		// on décrète que les moteurs forcent si la puissance qu'ils demandent est trop grande
		boolean areMotorsActive = Math.abs(pwmLeftMotor) > 40 || Math.abs(pwmRightMotor) > 40;
		
		// on décrète que le robot est immobile si l'écart entre la position demandée et la position actuelle est (casi) constant
		//TODO: pourquoi ne pas utiliser isRobotMoving() ?
		boolean isRobotImmobile = Math.abs(derivatedRotationnalError) <= 10 && Math.abs(derivatedTranslationnalError) <= 10;

		// si on patine
		if(isRobotImmobile && areMotorsActive)
		{
			// si on patinais déja auparavant, on fait remonter le patinage au code de haut niveau (via BlocageExeption)
			if(wasBlockedAtPreviousCall)
			{
                // la durée de tolérance au patinage est fixée ici (200ms)
				// mais cette fonction n'étant appellée qu'a une fréquance de l'ordre du Hertz ( la faute a une saturation de la série)
				// le robot mettera plus de temps a réagir ( le temps de réaction est égal au temps qui sépare 2 appels successifs de cette fonction)
				if((System.currentTimeMillis() - blockageStartTimestamp) > blockedTolerancy)
				{
					log.warning("raiseExeptionIfBlocked : le robot a dû s'arrêter suite à un patinage. (levage de BlockedException)", this);
					try
					{
						immobilise();
					} 
					catch (SerialConnexionException e)
					{
						log.critical("raiseExeptionIfBlocked : Impossible d'immobiliser le robot: la carte d'asser ne répond plus.", this);
						e.printStackTrace();
					}
					
					throw new BlockedException("l'écart a la consigne ne bouge pas alors que les moteurs sont en marche");
				}
			}

			// si on détecte pour la première fois le patinage, on continue de forcer
			else
			{
				blockageStartTimestamp = System.currentTimeMillis();
				wasBlockedAtPreviousCall  = true;
			}
		}
		// si tout va bien
		else
		{
			wasBlockedAtPreviousCall = false;
			blockageStartTimestamp = System.currentTimeMillis();
		}

	}

	/** 
	 * Regarde si le robot bouge effectivement.
	 * Provoque un appel série pour avoir des information a jour. Cette méthode est demande donc un peu de temps. 
	 * @return vrai si le robot bouge, faux si le robot est immobile
	 * @throws SerialConnexionException en cas de problème de communication avec la carte d'asservissement
	 */
	public boolean isRobotMoving() throws SerialConnexionException
	{
		refreshFeedbackLoopStatistics();
		
		// petits alias sur les infos de l'asservissement
		int rotationnalError = feedbackLoopStatistics.get("erreur_rotation");
		int translationnalError = feedbackLoopStatistics.get("erreur_translation");
		int derivedRotationnalError = feedbackLoopStatistics.get("derivee_erreur_rotation");
		int derivedTranslationnalError = feedbackLoopStatistics.get("derivee_erreur_translation");
		
		// TODO:VALEURS A REVOIR
		// Décide si on considère le robot immobile ou non.
		boolean rotationStopped = Math.abs(rotationnalError) <= 60;
		boolean translationStopped = Math.abs(translationnalError) <= 60;
		boolean isImmobile = Math.abs(derivedRotationnalError) <= 20 && Math.abs(derivedTranslationnalError) <= 20;
		
		
		
		return !(rotationStopped && translationStopped && isImmobile);
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
	 * Fait tourner le robot. Méthode non bloquante
	 * @param angle
	 * @throws SerialConnexionException en cas de problème de communication avec la carte d'asservissement
	 */
	public void turn(double angle) throws SerialConnexionException
	{
		String chaines[] = {"t", Double.toString(angle)};
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
	 * Retourne la valeur mise a jour du facteur de Deboc en fonction des informations de l'asser' 
	 * Ce facteur permet de rajouter une correction intégrale via java a l'asservissement en translation.
	 * Le principe est d'approcher l'intégrale par un rectangle sous la courbe. Ce rectangle étant de largeur la latence de la liaison série.
	 * 
	 * @return la nouvelle valeur du facteur de Deboc pour la translation
	 */
	public float getTranslationnalDebocFactor()
	{
		
		// inverse de l'erreur courante a annuler par la correction intégrale. Cette valeur est fournie périodiquement par la carte d'asser.
		float invertedTranslationnalError = feedbackLoopStatistics.get("inverse_erreur_translation_integrale");
		
		// prise en compte de latence variable de la liaison série: on approxime par une loi uniforme (100ms de latence en moyenne avec la surcouche java).
		// latence exprimée ici en milisecondes, entre -50ms et 150ms
		// Attention, en fonction de la jvm (Openjdk ou Oracle), ces valeurs peuvent changer
		float latency = ((new Random()).nextFloat() * 200) - 50;

		// intégrale: dt / (1/valeur), c'est a dire valeur * dt
		float DebocFactor = latency / invertedTranslationnalError;
		
		// revois le facteur de deboc à l'utilisateur
		return DebocFactor;
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
	 * Met à jour PWMmoteurGauche, PWMmoteurDroit, erreur_rotation, erreur_translation, derivee_erreur_rotation, derivee_erreur_translation
	 * les nouvelles valeurs sont stokées dans feedbackLoopStatistics (feedbackLoopStatistics est une map privée de la classe)
	 * @throws SerialConnexionException en cas de problème de communication avec la carte d'asservissement
	 */
	public void refreshFeedbackLoopStatistics() throws SerialConnexionException
	{
		// on demande a la carte des information a jour
		// on envois "?infos" et on lis 4 int (dans l'ordre : PWM droit, PWM gauche, erreurRotation, erreurTranslation)
		String[] infosBuffer = locomotionCardSerial.communiquer("?infos", 4);
		int[] parsedInfos = new int[4];
		for(int i = 0; i < 4; i++)
			parsedInfos[i] = Integer.parseInt(infosBuffer[i]);
		
		// calcul des dérivées des erreurs en translation et en rotation :
		// on fait la différence entre la valeur actuelle de l'erreur et le valeur précédemment mesurée.
		// on divise par un dt unitaire (non mentionné dans l'expression)
		int derivedRotationnalError = parsedInfos[2] - feedbackLoopStatistics.get("erreur_rotation");
		int derivedTranslationnalError = parsedInfos[3] - feedbackLoopStatistics.get("erreur_translation");
		
		
		// on stocke la puissance consommée par les moteurs
        feedbackLoopStatistics.put("PWMmoteurGauche", parsedInfos[0]);
        feedbackLoopStatistics.put("PWMmoteurDroit", parsedInfos[1]);
        
        // l'erreur de translation mesurée par les codeuses
        feedbackLoopStatistics.put("erreur_rotation", parsedInfos[2]);
        feedbackLoopStatistics.put("erreur_translation", parsedInfos[3]);
        
        // stocke les dérivées des erreurs, calculés 10 lignes plus haut
        feedbackLoopStatistics.put("derivee_erreur_rotation", derivedRotationnalError);
        feedbackLoopStatistics.put("derivee_erreur_translation", derivedTranslationnalError);

        
	}

	/**
	 * Demande a la carte d'asservissement la position et l'orientation courrante du robot sur la table.
	 * Renvoie x, y et orientation du robot
	 * @return un tableau de 3 cases: [x, y, orientation]
	 * @throws SerialConnexionException en cas de problème de communication avec la carte d'asservissement
	 */
	public double[] getCurrentPositionAndOrientation() throws SerialConnexionException
	{
		// on demande a la carte des information a jour
		// on envois "?infos" et on lis double (dans l'ordre : abscisse, ordonnée, orientation)
		String[] infosBuffer = locomotionCardSerial.communiquer("?xyo", 3);
		double[] parsedInfos = new double[3];
		for(int i = 0; i < 3; i++)
		    parsedInfos[i] = Double.parseDouble(infosBuffer[i]);

		return parsedInfos;
	}

	/**
	 * Ferme la connexion série avec la carte d'asservissements
	 */
	public void closeLocomotion()
	{
		locomotionCardSerial.close();
	}
	
}