package scripts;

import java.util.ArrayList;

import enums.ActuatorOrder;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialConnexionException;
import exceptions.serial.SerialFinallyException;
import hook.Hook;
import hook.types.HookFactory;
import robot.Robot;
import smartMath.Circle;
import strategie.GameState;
import sun.org.mozilla.javascript.ast.CatchClause;
import utils.Config;
import utils.Log;
/**
 * FIXME ajouter une version pour chopper le goblet en plus des tapis
 * @author paul
 * Script pour deposer les tapis sur l'escalier
 */
public class DropCarpet extends AbstractScript 
{
	
	/**distance de déplacement entre le point de depart et les marches (position pour poser les tapis) en mm */
	private int distanceBetweenEntryAndStairs=220;

	/**
	 * Constructeur (normalement appelé uniquement par le scriptManager) du script déposant les tapis
	 * Le container se charge de renseigner la hookFactory, le système de config et de log.
	 * @param hookFactory La factory a utiliser pour générer les hooks dont pourra avoir besoin le script
	 * @param config le fichier de config a partir duquel le script pourra se configurer
	 * @param log le système de log qu'utilisera le script
	 */
	public DropCarpet(HookFactory hookFactory, Config config, Log log)
	{
		super(hookFactory, config, log);
		versions = new Integer[]{1}; 
		//definition du tableau des versions, a modifier a chaque ajout d'une version (si il n'y en a qu'une je vois pas trop l'interet mais bon)
	}

	@Override
	public void execute(int versionToExecute, GameState<Robot> stateToConsider,ArrayList<Hook> hooksToConsider,boolean shouldRetryIfBlocke) throws UnableToMoveException, SerialConnexionException
	{
		
		//on presente ses arrieres a l'escalier
		stateToConsider.robot.turn(-0.5*Math.PI, hooksToConsider, false);
		// on avance vers ces demoiselles (les marches) (attention impact possible)
		// TODO utiliser moveLengthwiseTorwardWalls
		stateToConsider.robot.moveLengthwiseWithoutDetection(-distanceBetweenEntryAndStairs, hooksToConsider, true);
		
		System.out.println("en position ("+stateToConsider.robot.getPosition().x+", "+stateToConsider.robot.getPosition().y+") avant depose-tapis");

		
		//verification de la position : on n'effectue l'action que si on est assez proche (ie pas d'obstacle)
		//if(Math.abs((stateToConsider.robot.getPosition().y-1340))<50) // position- position du centre parfait<marge d'erreur
		
		{
			//on depose le tapis gauche (si celui-ci n'est pas deja depose)
			if (!stateToConsider.table.getIsLeftCarpetDropped())
			{
				stateToConsider.robot.useActuator(ActuatorOrder.LEFT_CARPET_DROP, true);
				stateToConsider.table.setIsLeftCarpetDropped(true);
				stateToConsider.robot.useActuator(ActuatorOrder.LEFT_CARPET_FOLDUP, false);
			}
			
			//on depose le tapis droit (si celui-ci n'est pas deja depose)
			if (!stateToConsider.table.getIsRightCarpetDropped())
			{
				stateToConsider.robot.useActuator(ActuatorOrder.RIGHT_CARPET_DROP, true);
				stateToConsider.table.setIsRightCarpetDropped(true);
				stateToConsider.robot.useActuator(ActuatorOrder.RIGHT_CARPET_FOLDUP, true);
			}
			System.out.println("En position ("+stateToConsider.robot.getPosition().x+", "+stateToConsider.robot.getPosition().y+") après avoir deposé les tapis");
		}
		
		//on s'eloigne de l'escalier
		try 
		{
			stateToConsider.robot.moveLengthwise(distanceBetweenEntryAndStairs, hooksToConsider, false);

		}
		catch (UnableToMoveException e) 
		{
			// tant qu'on est pas sorti, et que le pathDingDing peut reprendre lee  relais
			while(stateToConsider.robot.getPosition().y > (1400-distanceBetweenEntryAndStairs+20))
			{
				System.out.println("catch dans le script : DropCarpet");
				stateToConsider.robot.moveLengthwise((stateToConsider.robot.getPosition().y - (1400-distanceBetweenEntryAndStairs)), hooksToConsider, false);
			}
		}
		
	}
	
	@Override
	public Circle entryPosition(int id, int ray) 
	{
		return new Circle(310,1400-distanceBetweenEntryAndStairs);//point de depose - distance de deplacement jusqua ce point
	}

	@Override
	public int remainingScoreOfVersion(int version, GameState<?> stateToConsider) 
	{
		int score = 24;
		if(stateToConsider.table.getIsLeftCarpetDropped())
			score -= 12;
		if(stateToConsider.table.getIsRightCarpetDropped())
			score -= 12;
		
		return score;
	}

	@Override
	protected void finalise(GameState<?> stateToConsider) throws SerialFinallyException 
	{
		try 
		{
			stateToConsider.robot.useActuator(ActuatorOrder.LEFT_CARPET_FOLDUP, false);
			stateToConsider.robot.useActuator(ActuatorOrder.RIGHT_CARPET_FOLDUP, true);
		} 
		catch (SerialConnexionException e) 
		{
			log.debug("erreur termine DropCarpet script : impossible de ranger", this);
			throw new SerialFinallyException ();
		}
	}

	public Integer[] getVersion(GameState<?> stateToConsider)
	{
		if (stateToConsider.table.getIsLeftCarpetDropped() && stateToConsider.table.getIsRightCarpetDropped())
			return new Integer[]{};
		return versions;
	}

}
