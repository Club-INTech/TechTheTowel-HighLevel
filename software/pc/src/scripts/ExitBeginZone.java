package scripts;

import hook.Hook;
import hook.types.HookFactory;

import java.util.ArrayList;

import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialConnexionException;
import robot.Robot;
import smartMath.Vec2;
import strategie.GameState;
import utils.Config;
import utils.Log;

/**
 * script pour sortir de la zone de depart. a executer imperativement et uniquement au depart
 * @author paul
 */
public class ExitBeginZone extends AbstractScript
{

	int distanceToExit=500;
	ArrayList<Hook> emptyHook = new ArrayList<Hook>();
	
	public ExitBeginZone(HookFactory hookFactory, Config config, Log log) 
	{
		super(hookFactory, config, log);
	}

	@Override
	public Vec2 entryPosition(int id)
	{
		// point de depart du match a modifier a chaque base roulante
		return new Vec2(1500-71-48,1000);
	}
	
	@Override
	public void execute (int id_version, GameState<Robot> stateToConsider, boolean shouldRetryIfBlocke) throws UnableToMoveException, SerialConnexionException
	{
		try
		{
			stateToConsider.robot.moveLengthwise(distanceToExit);
		}
		catch (UnableToMoveException e)
		{
			if (shouldRetryIfBlocke)
			{
				execute (id_version, stateToConsider,false);
			}
			else
			{
				log.debug("erreur ExitBeginZone script : impossible de sortir de la zone de depart\n", this);
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
}
