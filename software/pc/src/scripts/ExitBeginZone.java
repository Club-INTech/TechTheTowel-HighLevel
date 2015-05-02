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
import table.Table;
import utils.Config;
import utils.Log;

/**
 * script pour sortir de la zone de depart. a executer imperativement et uniquement au depart
 * @author paul
 */
public class ExitBeginZone extends AbstractScript
{
	//la distance dont on avance pour sortir de la zone de depart
	int distanceToExit=250;
	
	public ExitBeginZone(HookFactory hookFactory, Config config, Log log) 
	{
		super(hookFactory, config, log);
		versions = new Integer[]{0};
	}

	@Override
	public Circle entryPosition(int id, int ray)
	{
		return new Circle(Table.entryPosition);
	}
	
	@Override
	public void execute (int id_version, GameState<Robot> stateToConsider, ArrayList<Hook> hooksToConsider, boolean shouldRetryIfBlocke) throws UnableToMoveException, SerialConnexionException
	{
		try
		{
			//on met l'ascenseur en haut pour ne pas frotter
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
		
		finalise(stateToConsider);
	}

	@Override
	public int remainingScoreOfVersion(int id_version, GameState<?> state) 
	{		
		// grosse grosse value du script
		return Integer.MAX_VALUE;
	}

	@Override
	protected void finalise(GameState<?> state) 
	{
		//abwa ?
		//en effet, pas d'actionneur a rentrer donc abwa !
	}

	public Integer[] getVersion(GameState<?> stateToConsider)
	{
		return versions;
	}
}
