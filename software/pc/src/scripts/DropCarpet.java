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
 * @author paul
 * Script pour deposer les tapis sur l'escalier
 */
public class DropCarpet extends AbstractScript 
{
	
	/**distance de déplacement entre le point de depart et les marches (position pour poser les tapis) en mm */
	private int distanceBetweenEntryAndStairs=200;

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
		versions = new int[]{1}; 
		//definition du tableau des versions, a modifier a chaque ajout d'une version (si il n'y en a qu'une je vois pas trop l'interet mais bon)
	}

	@Override
	public void execute (int versionToExecute, GameState<Robot> stateToConsider, boolean shouldRetryIfBlocke) throws UnableToMoveException, SerialConnexionException 
	{
		try 
		{
			
			//on presente ses arrieres a l'escalier
			stateToConsider.robot.turn(Math.PI);
			// on avance vers ces demoiselles (les marches) 
			stateToConsider.robot.moveLengthwiseTowardWall(-distanceBetweenEntryAndStairs);
			
			if (!stateToConsider.table.getIsLeftCarpetDropped())
			{
				stateToConsider.robot.useActuator(ActuatorOrder.LEFT_CARPET_DROP, true);
				stateToConsider.table.setIsLeftCarpetDropped(true);
				stateToConsider.robot.useActuator(ActuatorOrder.LEFT_CARPET_FOLDUP, false);
			}
			if (!stateToConsider.table.getIsRightCarpetDropped())
			{
				stateToConsider.robot.useActuator(ActuatorOrder.RIGHT_CARPET_DROP, true);
				stateToConsider.table.setIsRightCarpetDropped(true);
				stateToConsider.robot.useActuator(ActuatorOrder.LEFT_CARPET_FOLDUP, true);
			}
			//on s'eloigne de l'escalier
			stateToConsider.robot.moveLengthwise(distanceBetweenEntryAndStairs);
		
		} 
		catch (UnableToMoveException e) 
		{
			if (shouldRetryIfBlocke)
			{
				execute (versionToExecute,stateToConsider,false);
			}
			else
			{
				log.debug("erreur DropCarpet Script : impossible de bouger", this);
				throw e;
			}
		} 

	}
	
	@Override
	public Vec2 entryPosition(int id) 
	{
		return new Vec2(261,1310-distanceBetweenEntryAndStairs);
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
	protected void finalise(GameState<?> stateToConsider) throws SerialConnexionException 
	{
		try 
		{
			stateToConsider.robot.useActuator(ActuatorOrder.LEFT_CARPET_FOLDUP, false);
			stateToConsider.robot.useActuator(ActuatorOrder.RIGHT_CARPET_FOLDUP, true);
		} 
		catch (SerialConnexionException e) 
		{
			log.debug("erreur termine DropCarpet script : impossible de ranger", this);
			throw e;
		}
	}

}
