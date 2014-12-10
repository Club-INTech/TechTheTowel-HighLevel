package scripts;

import java.util.ArrayList;

import enums.ActuatorOrder;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialConnexionException;
import hook.types.HookFactory;
import robot.Robot;
import smartMath.Vec2;
import strategie.GameState;
import utils.Config;
import utils.Log;


//TODO: Doc ( notamment, expliquer ce que sont les différentes versions
/**
 * 
 * @author ???
 *
 */
public class DropPile extends AbstractScript
{
	

	public DropPile(HookFactory hookFactory, Config config, Log log) 
	{
		super(hookFactory, config, log);
		
		
		// TODO: id n'est pas une variable temporaire du constructeur. C'est versions qui est un membre et qu'il faut initialiser ici
		ArrayList<Integer> id = new ArrayList<Integer>();
		id.add(1);
		id.add(2);
	}

	@Override
	protected void execute(int version, GameState<Robot> stateToConsider, boolean shouldRetryIfBlocke) throws UnableToMoveException, SerialConnexionException
	{
		if (version==1)
		{
			stateToConsider.robot.turn(Math.PI/2.0);
			stateToConsider.robot.moveLengthwise(100);
			stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_GROUND, true);
			stateToConsider.robot.useActuator(ActuatorOrder.OPEN_RIGHT_GUIDE, false);
			stateToConsider.robot.useActuator(ActuatorOrder.OPEN_LEFT_GUIDE, true);		
			stateToConsider.robot.moveLengthwise(-20);
			stateToConsider.robot.setStoredPlotCount(0);
			stateToConsider.robot.useActuator(ActuatorOrder.CLOSE_RIGHT_GUIDE, false);
			stateToConsider.robot.useActuator(ActuatorOrder.CLOSE_LEFT_GUIDE, true);	
			stateToConsider.robot.moveLengthwise(-80);
		}
		else if (version==2)
		{
			// TODO: version 2
		}
		else
		{
			// TODO: version ?
		}
	}
	
	
	@Override
	public Vec2 entryPosition(int id) 
	{
		if (id==1)
		{
			return new Vec2(1300,1000);
		}
		else if (id==2)
		{
			return new Vec2(70,40);
		}
		else
		{
			log.debug("erreur DropPile script : out of bound id", this);
			return new Vec2(0,1000);
		}
	}

	@Override
	public int remainingScoreOfVersion(int version, GameState<?> stateToConsider)
	{
		return 5*(8 -stateToConsider.robot.getStoredPlotCount());
	}

	@Override
	protected void finalise(GameState<?> stateToConsider) throws SerialConnexionException, UnableToMoveException
	{

		stateToConsider.robot.useActuator(ActuatorOrder.CLOSE_RIGHT_GUIDE, false);
		stateToConsider.robot.useActuator(ActuatorOrder.CLOSE_LEFT_GUIDE, true);	
		stateToConsider.robot.moveLengthwise(-20);
		stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_LOW, true);
	}

}

