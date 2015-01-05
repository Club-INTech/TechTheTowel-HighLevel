package scripts;

import java.util.ArrayList;

import enums.ActuatorOrder;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialConnexionException;
import hook.Hook;
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
 * Script Fermer les claps
 *
 *Table :
 *
 *    ___________________
 *   |					|
 * 	 |					|
 *   |					|Debut du robot ici
 *   |					|
 *   |		N° claps	|
 *    /_3_/________2_/_1     
 *    
 *    1,2,3 nous appartiennent
 *    
 *   
 */

public class CloseClap extends AbstractScript 
{

	/**
	 * Constructeur (normalement appelé uniquement par le scriptManager) du script fermant les Claps
	 * Le container se charge de renseigner la hookFactory, le système de config et de log.
	 * @param hookFactory La factory a utiliser pour générer les hooks dont pourra avoir besoin le script
	 * @param config le fichier de config a partir duquel le script pourra se configurer
	 * @param log le système de log qu'utilisera le script
	 * TODO: seul closeAllClaps fonctionne, il faut modifier les autres
	 */
	
	public CloseClap(HookFactory hookFactory, Config config, Log log)
	{
		super(hookFactory, config, log);
		versions = new int[]{1, 2, 3 ,12 ,123 }; //il faut penser a le définir, puis a le garder a jour
	}
	
	@Override
	public void execute(int versionToExecute, GameState<Robot> stateToConsider, ArrayList<Hook> hooksToConsider, boolean shouldRetryIfBlocked) throws UnableToMoveException, SerialConnexionException
	{
		//Noté ! =X
		
		//FIXME: gestion de la symétrie !
		if (versionToExecute == 123)
			closeAllOurClaps(stateToConsider, hooksToConsider, shouldRetryIfBlocked);
		else if (versionToExecute == 1)
			closeFirstClap(stateToConsider, hooksToConsider, shouldRetryIfBlocked);
		else if (versionToExecute == 2)
			closeSecondClap(stateToConsider, hooksToConsider, shouldRetryIfBlocked);
		else if (versionToExecute == 3)
			closeThirdClap(stateToConsider, hooksToConsider, shouldRetryIfBlocked);
		else if (versionToExecute == 12)
			closeFirstAndSecondClap(stateToConsider, hooksToConsider, shouldRetryIfBlocked);
		else
			log.debug("Souci de version", this);	//TODO: lancer une exception de version inconnue (la créer si besoin)
	}
	
	public void closeFirstClap (GameState<Robot> stateToConsider,  ArrayList<Hook> hooksToConsider, boolean shouldRetryIfBlocked) throws UnableToMoveException, SerialConnexionException
	{
		//on commence en (1290,231), on se tourne dans le bon sens
		stateToConsider.robot.turn(Math.PI, hooksToConsider, false);
				
		//on reculle pour se mettre en (1350,231)
		stateToConsider.robot.moveLengthwise(-60, hooksToConsider, true);
			
		//On ouvre le bras puis on avance de 300mm pour se retrouver en (1050,231)
		stateToConsider.robot.useActuator(ActuatorOrder.MID_LEFT_CLAP, true);
		stateToConsider.robot.moveLengthwise(300, hooksToConsider, false);
		stateToConsider.table.setIsClap1Closed(true);
	
		//On baisse notre bras
		stateToConsider.robot.useActuator(ActuatorOrder.LOW_LEFT_CLAP, true);
	}

	public void closeSecondClap (GameState<Robot> stateToConsider,  ArrayList<Hook> hooksToConsider, boolean shouldRetryIfBlocked) throws UnableToMoveException, SerialConnexionException
	{
		//on commence en (700,231), on se tourne dans le bon sens
		stateToConsider.robot.turn(Math.PI, hooksToConsider, false);
		
		//On ouvre le bras puis on avance de 300mm pour se retrouver en (400,231)
		stateToConsider.robot.useActuator(ActuatorOrder.MID_LEFT_CLAP, true);
		stateToConsider.robot.moveLengthwise(300, hooksToConsider, false);
		stateToConsider.table.setIsClap2Closed(true);	

		//on baisse notre bras
		stateToConsider.robot.useActuator(ActuatorOrder.LOW_LEFT_CLAP, true);	
	}
	
	public void closeThirdClap (GameState<Robot> stateToConsider,  ArrayList<Hook> hooksToConsider, boolean shouldRetryIfBlocked) throws UnableToMoveException, SerialConnexionException  //Ferme le claps de fin
	{
		//on commence en (-1050,231), on se retourne dans le bon sens
		stateToConsider.robot.turn(0, hooksToConsider, false);
		
		//on ouvre notre bras puis on avance de 200mm pour se retrouver en (-850,231) 
		stateToConsider.robot.useActuator(ActuatorOrder.MID_RIGHT_CLAP, true);
		stateToConsider.robot.moveLengthwise(200, hooksToConsider, false);
		stateToConsider.table.setIsClap3Closed(true);
		
		//on baisse notre bras
		stateToConsider.robot.useActuator(ActuatorOrder.LOW_RIGHT_CLAP, true);
	}
	
	public void closeFirstAndSecondClap (GameState<Robot> stateToConsider,  ArrayList<Hook> hooksToConsider, boolean shouldRetryIfBlocked) throws UnableToMoveException, SerialConnexionException
	{
		//on commence en (1290,231), on se tourne dans le bon sens
		stateToConsider.robot.turn(Math.PI, hooksToConsider, false);
		
		//on reculle pour se mettre en (1350,231)
		stateToConsider.robot.moveLengthwise(-60, hooksToConsider, true);
	
		//On ouvre le bras puis on avance de 300mm pour se retrouver en (1050,231)
		stateToConsider.robot.useActuator(ActuatorOrder.MID_LEFT_CLAP, true);
		stateToConsider.robot.moveLengthwise(300, hooksToConsider, false);
		stateToConsider.table.setIsClap1Closed(true);
	
		//On monte notre bras pour passer au dessus du clap ennemi notre bras et on avance de 350mm pour se retrouver en (700,231)
		stateToConsider.robot.useActuator(ActuatorOrder.LOW_LEFT_CLAP, true);
		stateToConsider.robot.moveLengthwise(350, hooksToConsider, false);

		//On ouvre le bras puis on avance de 300mm pour se retrouver en (400,231)
		stateToConsider.robot.useActuator(ActuatorOrder.MID_LEFT_CLAP, true);
		stateToConsider.robot.moveLengthwise(300, hooksToConsider, false);
		stateToConsider.table.setIsClap2Closed(true);	

		//on baisse notre bras
		stateToConsider.robot.useActuator(ActuatorOrder.LOW_LEFT_CLAP, true);	
	}
	
	public void closeAllOurClaps(GameState<Robot> stateToConsider,  ArrayList<Hook> hooksToConsider, boolean shouldRetryIfBlocked) throws UnableToMoveException, SerialConnexionException  //Ferme tous les Claps, depuis le  debut
	{
		//on commence en (1290,231), on se tourne dans le bon sens
		stateToConsider.robot.turn(Math.PI, hooksToConsider, false);
		
		//on reculle pour se mettre en (1360,231)
		stateToConsider.robot.moveLengthwise(-100, hooksToConsider, true);
	
		//On ouvre le bras puis on avance de 250mm pour se retrouver en (1010,231)
		stateToConsider.robot.useActuator(ActuatorOrder.MID_LEFT_CLAP, true);
		stateToConsider.robot.moveLengthwise(250, hooksToConsider, false);
		stateToConsider.table.setIsClap1Closed(true);
	
		//On monte notre bras pour passer au dessus du clap ennemi notre bras et on avance de 250mm pour se retrouver en (660,231)
		stateToConsider.robot.useActuator(ActuatorOrder.HIGH_LEFT_CLAP, true);
		stateToConsider.robot.moveLengthwise(250, hooksToConsider, false);

		//On ouvre le bras puis on avance de 220mm pour se retrouver en (400,231)
		stateToConsider.robot.useActuator(ActuatorOrder.MID_LEFT_CLAP, true);
		stateToConsider.robot.moveLengthwise(220, hooksToConsider, false);
		stateToConsider.table.setIsClap2Closed(true);	

		//on baisse notre bras
		stateToConsider.robot.turn(0.5*Math.PI, hooksToConsider, false);
		stateToConsider.robot.useActuator(ActuatorOrder.LOW_LEFT_CLAP, true);
		
		//on vas au 3eme clap donc en (-1340,231)
		stateToConsider.robot.moveLengthwise(300, hooksToConsider, false);
		stateToConsider.robot.turn(Math.PI, hooksToConsider, false);
		stateToConsider.robot.moveLengthwise(1750, hooksToConsider, false);
		stateToConsider.robot.turn(-0.5*Math.PI, hooksToConsider, false);
		stateToConsider.robot.moveLengthwise(300, hooksToConsider, false);
		
		//on est en (-1340,231), on se retourne dans le bon sens
		stateToConsider.robot.turn(0, hooksToConsider, false);
		
		//on ouvre notre bras puis on avance de 200mm pour se retrouver en (-1140,231) 
		stateToConsider.robot.useActuator(ActuatorOrder.MID_RIGHT_CLAP, true);
		stateToConsider.robot.moveLengthwise(200, hooksToConsider, false);
		stateToConsider.table.setIsClap3Closed(true);
	}
	
	
	@Override
	public Vec2 entryPosition(int version)
	{
		// FIXME: points exacts à entrer
		
		if (version == 1)
			return new Vec2(1290,231); //point d'entrée : bord de la table, robot devant le clap 1
		else if(version == 2)
			return new Vec2(700,231); //point d'entrée : devant le clap 2
		else if(version == 3)
			return new Vec2(-1050,231);//point d'entrée : devant le clap 3
		else if(version == 12)
			return new Vec2(1290,231); //point d'entrée : devant le clap 1
		else if(version == 123)
			return new Vec2(1290,231); //point d'entrée : devant le clap 1
		else
		{
			log.debug("Probleme d'entrée de position", this);
			return null;
		}
	}

	@Override
	public int remainingScoreOfVersion(int version, GameState<?> stateToConsider)
	{
		//On met à jour le nombre de points restants pour la version : à  0, on a tout fait
		int score = 15;
		if(stateToConsider.table.getIsClap1Closed() || version==2 || version==3)
			score -= 5;
		if(stateToConsider.table.getIsClap2Closed() || version == 1 || version == 3)
			score -= 5;
		if(stateToConsider.table.getIsClap3Closed() || version == 1 || version == 2 || version == 12)
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
}



