package scripts;

import hook.Hook;
import hook.types.HookFactory;

import java.util.ArrayList;

import enums.ActuatorOrder;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialConnexionException;
import robot.Robot;
import smartMath.Circle;
import strategie.GameState;
import utils.Config;
import utils.Log;

/**
 * script pour sortir de la zone de depart. a executer imperativement et uniquement au depart
 * @author paul
 */
public class ExitBeginZone extends AbstractScript
{
	//la distance dont on avance pour sortir de la zone de depart
	int distanceToExit=500;
	
	public ExitBeginZone(HookFactory hookFactory, Config config, Log log) 
	{
		super(hookFactory, config, log);
		versions = new int[]{0};
	}

	@Override
	public Circle entryPosition(int id, int ray)
	{
		// point de depart du match a modifier a chaque base roulante
		return new Circle(1500-71-48,1000);
		//1500 le bout de la table, 71 la taille du carre vert perce et 48 la taille de l'arriere du robot a son centre
	}
	
	@Override
	public void execute (int id_version, GameState<Robot> stateToConsider, ArrayList<Hook> hooksToConsider, boolean shouldRetryIfBlocke) throws UnableToMoveException, SerialConnexionException
	{
		try
		{
			//on met l'ascenseur en haut pour ne pas frotter
			stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_LOW, true);
			stateToConsider.robot.moveLengthwise(distanceToExit, hooksToConsider, false);
		}
		catch (UnableToMoveException e)
		{
			if (shouldRetryIfBlocke)
			{
				execute (id_version, stateToConsider, hooksToConsider,false);
			}
			else
			{
				log.critical("erreur ExitBeginZone script : impossible de sortir de la zone de depart\n", this);
				throw e;
			}
		}
	}

	@Override
	public int remainingScoreOfVersion(int id_version, GameState<?> state) 
	{		
		return 0;
	}

	@Override
	protected void finalise(GameState<?> state) 
	{
		//abwa ?
		//en effet, pas d'actionneur a rentrer donc abwa !
	}

	@Override
	public double getNoEnemyTime(GameState<?> stateToConsider, int id) 
	{
		// FIXME Auto-generated method stub
		return 0;
	}
}
