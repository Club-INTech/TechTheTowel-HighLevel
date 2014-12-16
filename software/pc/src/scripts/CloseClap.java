package scripts;

import enums.ActuatorOrder;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialConnexionException;
import hook.types.HookFactory;
import robot.Robot;
import smartMath.Vec2;
import strategie.GameState;
import utils.Config;
import utils.Log;

/**
 * 
 * @author theo
 * Exemple sur Paul
 *Sript Fermer les claps
 *
 *Table :
 *
 *    ___________________
 *   |					|
 * 	 |					|
 *   |					|Debut du robot ici
 *   |					|
 *   |		N° claps	|
 *    6_5_4________3_2_1     // TODO: numérotation qui n'est pas a jour
 *    
 *    
 *    
 *    
 *    
 *    
 *    
 *    //TODO: l'orientation n'as pas a être rappellée ici, ====> trouver un endroit logique pour la rappeller. Locomotion, Table, ou LocomotionCardWrapper sont de bon candidats
 *    Angles :
 *    
 *    ___________________
 *   |		PI/2		|
 * 	 |					|
 *   |PI			   0|  Debut du robot ici
 *   |					|
 *   |					|
 *   |______-PI/2_______|
 *   
 */

public class CloseClap extends AbstractScript 
{
	//TODO spécifier SYSTEMETIQUEMENT les unitées

	/** distance entre 2 claps (bout identique de claque clap, ex : charnieres) */
	private int distanceBetweenClaps = 300;
	
	/** Longueur clap  */
	private int lenghtClap = 160;
	
	/** L'estradee fait 100, on met 200 */
	private int lenghtStair = 200;
	
	/** distance intiale au script */ // TODO: ce commentaire ne veut rien dire
	private int distanceInit;
	
	/** distance entre les deux triplettes de claps : entre le 3 et le 5 */
	private int distanceRightLeft = 1700;
	
	/** le temps d'attente (en ms) entre la commande de dépose du tapis ( le bras se baisse) et la commande qui remonte le bras */ //TODO: ce n'est pas un tapis
	private int sleepTime = 800;

	/**
	 * Constructeur (normalement appelé uniquement par le scriptManager) du script fermant les Claps
	 * Le container se charge de renseigner la hookFactory, le système de config et de log.
	 * @param hookFactory La factory a utiliser pour générer les hooks dont pourra avoir besoin le script
	 * @param config le fichier de config a partir duquel le script pourra se configurer
	 * @param log le système de log qu'utilisera le script
	 */
	public CloseClap(HookFactory hookFactory, Config config, Log log)
	{
		super(hookFactory, config, log);
		
		versions = new int[]{1, 2, 3 ,12 ,123 }; // TODO: il faut penser a le définir (tu l'avais oublié), puis a le garder a jour
	}
	
	@Override
	public void execute(int versionToExecute, GameState<Robot> stateToConsider, boolean shouldRetryIfBlocked) throws UnableToMoveException, SerialConnexionException
	{
		
		/*
		 * Théo, il y a une maladresse dans la structure de ton code:
		 * 
		 * tu as une duplication de code au niveau de la partie qui renverse chaque clap.
		 * Pour s'en rendre compte, c'est simple: imagine que tu doive par exemple faire une nouvelle chose a chaque fois que le clap n°1 tombe: afficher un log par exemple.
		 * Dans ce cas, tu devras ajouter 3 fois ton log, dans ta fonction _1, ta fonction _12, et dans ta fonction _123. (j'ai vu les bouts copiés/collés)
		 * 
		 * Je te conseilles plutot:
		 * - une méthode pour le premier clap
		 * - une méthode pour le second
		 * - une dernière pour le dernier
		 * 
		 * - une méthode de transition entre le pemier et le second clap
		 * - une méthode de transition entre le second et le dernier clap
		 * 
		 * Ansi, par exemple, pour la version 123 de ton scipt, tu metterais ici (dans la méthode execute directement) 
		 * 1) premier clap
		 * 2) transition
		 * 3) second clap
		 * 4) transition
		 * 5) dernier clap
		 * 
		 * Au fait, tes noms de méthode en _ sont vraiment hideux
		 * Au fait 2, un commentaire se met la plupart du temps sur une nouvelle ligne
		 */
		
		//FIXME: gestion de la symétrie !
		if (versionToExecute == 123)
			closeAllOurClaps(stateToConsider, shouldRetryIfBlocked);
		else if (versionToExecute == 1)
			closeFirstClap(stateToConsider, shouldRetryIfBlocked);
		else if (versionToExecute == 2)
			closeSecondClap(stateToConsider, shouldRetryIfBlocked);
		else if (versionToExecute == 3)
			closeThirdClap(stateToConsider, shouldRetryIfBlocked);
		else if (versionToExecute == 12)
			closeFirstAndSecondClap(stateToConsider, shouldRetryIfBlocked);
		else
			log.debug("Souci de version", this);	//TODO: lancer une exception de version inconnue (la créer si besoin)
	}
	
	public void closeFirstClap (GameState<Robot> stateToConsider, boolean shouldRetryIfBlocked) throws UnableToMoveException, SerialConnexionException
	{
		try 
		{
			//On ouvre le bras puis on avance
			stateToConsider.robot.useActuator(ActuatorOrder.MID_LEFT_CLAP, true);
			
			//On baisse le premier clap, le notre
			stateToConsider.robot.moveLengthwise(lenghtClap);
			stateToConsider.table.setIsClap1Closed(true);	

			stateToConsider.robot.useActuator(ActuatorOrder.MID_LEFT_CLAP, true);
		}
		catch (UnableToMoveException e) 
		{
			log.debug("Probleme avec le deplacement pendant le clap 1", this);	// Viens me parler de cette ligne. Je dois t'expliquer ce que veut dire "remonter une exception"
		} 
		catch (SerialConnexionException e) 
		{
			log.debug("Mauvaise entrée serie !",this);// Viens me parler de cette ligne. Je dois t'expliquer ce que veut dire "remonter une exception"
			e.printStackTrace();	
		}	
	}

	public void closeSecondClap (GameState<Robot> stateToConsider, boolean shouldRetryIfBlocked) throws UnableToMoveException, SerialConnexionException
	{
		try 
		{
			//On ouvre le bras puis on avance
			stateToConsider.robot.useActuator(ActuatorOrder.MID_LEFT_CLAP, true);
			
			// On baisse le premier clap, le notre TODO: tu vois ? copié/collé abusif xD
			stateToConsider.robot.moveLengthwise(lenghtClap);
			stateToConsider.table.setIsClap2Closed(true);	

			stateToConsider.robot.useActuator(ActuatorOrder.MID_LEFT_CLAP, true);
		}
		catch (UnableToMoveException e) 
		{
			log.debug("Probleme avec le deplacement pendant le clap 2", this);// Viens me parler de cette ligne. Je dois t'expliquer ce que veut dire "remonter une exception"
		} 
		catch (SerialConnexionException e) 
		{
			log.debug("Mauvaise entrée serie !",this);// Viens me parler de cette ligne. Je dois t'expliquer ce que veut dire "remonter une exception"
			e.printStackTrace();	
		}	
	}
	
	public void closeThirdClap (GameState<Robot> stateToConsider, boolean shouldRetryIfBlocked) throws UnableToMoveException, SerialConnexionException  //Ferme le claps de fin
	{
		try 
		{
			stateToConsider.robot.useActuator(ActuatorOrder.MID_RIGHT_CLAP, true);
			
			//On ferme notre clap	
			stateToConsider.robot.moveLengthwise(lenghtClap);
			stateToConsider.table.setIsClap3Closed(true);

			//Partie fuite 
			stateToConsider.robot.turn(Math.PI/2);
			stateToConsider.robot.moveLengthwise(lenghtStair);

			stateToConsider.robot.useActuator(ActuatorOrder.LOW_RIGHT_CLAP, true);
		}
		catch (UnableToMoveException e) 
		{
		log.debug("Probleme avec le deplacement pendant le clap 3", this);// Viens me parler de cette ligne. Je dois t'expliquer ce que veut dire "remonter une exception"
		} 
		catch (SerialConnexionException e) 
		{
			log.debug("Mauvaise entrée serie !",this);// Viens me parler de cette ligne. Je dois t'expliquer ce que veut dire "remonter une exception"
			e.printStackTrace();	
		}	
	}
	
	public void closeFirstAndSecondClap (GameState<Robot> stateToConsider, boolean shouldRetryIfBlocked) throws UnableToMoveException, SerialConnexionException
	{
		try 
		{
			stateToConsider.robot.useActuator(ActuatorOrder.MID_LEFT_CLAP, true);
			
			// On baisse le premier clap, le notre
			stateToConsider.robot.moveLengthwise(lenghtClap);
			stateToConsider.table.setIsClap1Closed(true);	
			
			// On evite le clap adverse
			stateToConsider.robot.useActuator(ActuatorOrder.HIGH_LEFT_CLAP, true); 
			
			// On avance entre le 1 et le 2
			stateToConsider.robot.moveLengthwise(2*distanceBetweenClaps-lenghtClap-20);	// TODO: cette formule semble inexacte, réfères-toi plutot a  https://www.youtube.com/watch?v=I2H1-JHx6xk
			// TODO: recopie la formaule en commentaire en l'expliquant, que le néophite puisse la comprendre

			stateToConsider.robot.useActuator(ActuatorOrder.MID_LEFT_CLAP, true);
			
			// On tourne en fermant le clap
			stateToConsider.robot.turn(Math.PI/2);
			stateToConsider.table.setIsClap2Closed(true);

			stateToConsider.robot.useActuator(ActuatorOrder.LOW_LEFT_CLAP, true);
		}
		catch (UnableToMoveException e) 
		{
			log.debug("Probleme avec le deplacement pendant les claps près, 1 et 2", this);// Viens me parler de cette ligne. Je dois t'expliquer ce que veut dire "remonter une exception"
			throw e;
		} 
		catch (SerialConnexionException e) 
		{
			log.debug("Mauvaise entrée serie : script de fermeture des claps",this);// Viens me parler de cette ligne. Je dois t'expliquer ce que veut dire "remonter une exception"
			e.printStackTrace();	
			throw e;
		}	
	}
	
	public void closeAllOurClaps(GameState<Robot> stateToConsider, boolean shouldRetryIfBlocked) throws UnableToMoveException, SerialConnexionException  //Ferme tous les Claps, depuis le  debut
	{
		try 
		{
			stateToConsider.robot.useActuator(ActuatorOrder.MID_LEFT_CLAP, true);

			stateToConsider.robot.moveLengthwise(lenghtClap);//On baisse le premier clap, le notre
			stateToConsider.table.setIsClap1Closed(true);		

			//on evite le clap adverse
			stateToConsider.robot.useActuator(ActuatorOrder.HIGH_LEFT_CLAP, true); 
			
			stateToConsider.robot.moveLengthwise(2*distanceBetweenClaps-lenghtClap-20); // TODO: recopie la formaule en commentaire en l'expliquant, que le néophite puisse la comprendre
			
			stateToConsider.robot.useActuator(ActuatorOrder.MID_LEFT_CLAP, true);
			
			stateToConsider.robot.turn(Math.PI/2);//On tourne en fermant le clap
			stateToConsider.table.setIsClap2Closed(true);//On ferme notre 2eme clap	
			
			//on referme le clap pour eviter de le perdre
			stateToConsider.robot.useActuator(ActuatorOrder.LOW_LEFT_CLAP, true);

			
			// On se tourne, on avance pour eviter l'estrade
			stateToConsider.robot.moveLengthwise(lenghtStair); 

			//On se tourne, on avance vers le clap interessant
			stateToConsider.robot.turn(Math.PI);
			stateToConsider.robot.moveLengthwise(distanceRightLeft); 
			
			//On se tourne pour s'avancer des claps
			stateToConsider.robot.turn(-Math.PI/2);
			stateToConsider.robot.moveLengthwise(lenghtStair);
			
			//On se tourne dans le bon sens
			stateToConsider.robot.turn(0);
			
			//Partie clap de fin
			// A ce stade, on est devant le 3

			stateToConsider.robot.useActuator(ActuatorOrder.MID_RIGHT_CLAP, true);
			
			stateToConsider.robot.moveLengthwise(lenghtClap);
			stateToConsider.table.setIsClap3Closed(true);//On ferme notre clap	

			//Partie fuite 
			
			stateToConsider.robot.turn(Math.PI/2);
			stateToConsider.robot.moveLengthwise(lenghtStair);
			
			stateToConsider.robot.useActuator(ActuatorOrder.LOW_RIGHT_CLAP, true);
		}
		catch (UnableToMoveException e) 
		{
			log.debug("Probleme avec le deplacement pendant tous les claps", this);// Viens me parler de cette ligne. Je dois t'expliquer ce que veut dire "remonter une exception"
		} 
		catch (SerialConnexionException e) 
		{
			log.debug("Mauvaise entrée serie !",this);// Viens me parler de cette ligne. Je dois t'expliquer ce que veut dire "remonter une exception"
			e.printStackTrace();	
		}	
	}
	
	
	@Override
	public Vec2 entryPosition(int version)
	{
		// FIXME: Euh, c'est assez urgent de renseigner les points d'entrée du script !
		return null;
	}

	@Override
	public int remainingScoreOfVersion(int version, GameState<?> stateToConsider)
	{
		
		//TODO: le résultat est faux: si aucun clap n'est fermé, et que je demande la version 1, ne j'obtient que 5 points, pas 15 comme cette méthode le renvoit
		int score = 15;
		if(stateToConsider.table.getIsClap1Closed())
			score -= 5;
		if(stateToConsider.table.getIsClap2Closed())
			score -= 5;
		if(stateToConsider.table.getIsClap3Closed())
			score -= 5;
		return score;
	}

	@Override
	protected void finalise(GameState<?> stateToConsider)
	{	
		try 
		{
			//On ferme le robot à la fin, attention à ne rien cogner ! (rembarde , etc)
			stateToConsider.robot.useActuator(ActuatorOrder.LOW_RIGHT_CLAP, false);
			stateToConsider.robot.useActuator(ActuatorOrder.LOW_LEFT_CLAP, false);
			
			// TODO: voir si on ne peut pas mettre ici une protection anti-cognage en demandant au service de gestion d'obstacle s'il y a un obstacle sur les cotés du robot. (Attention, les bras doivent toujours essayer de se fermer, mais c'est juste pour voir si on ne peut pas le faire 3cm plus loin)
		} 
		catch (SerialConnexionException e) 
		{
			log.debug("Erreur termine : ne peux pas replier claps", this); // Viens me parler de cette ligne. Je dois t'expliquer ce que veut dire "remonter une exception"
			//FIXME: il faut creer une exception de type SerialFinallyException
		}
	}
	
	
	//TODO: pitié, doc moi ces setters/getters, ca prends 20sec
	// D"ailleurs je me demande même si leur existance est utile. 
	
	
	public int getDistanceInit()
	{
		return distanceInit;
	}

	public void setDistanceInit(int distanceInit)
	{
		this.distanceInit = distanceInit;
	}

	public int getSleepTime()
	{
		return sleepTime;
	}

	public void setSleepTime(int sleepTime)
	{
		this.sleepTime = sleepTime;
	}
}
