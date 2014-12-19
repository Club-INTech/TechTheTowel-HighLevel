package scripts;

import java.util.ArrayList;

import enums.ActuatorOrder;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialConnexionException;
import exceptions.serial.SerialFinallyException;
import hook.Hook;
import hook.types.HookFactory;
import robot.Robot;
import smartMath.Vec2;
import strategie.GameState;
import utils.Config;
import utils.Log;


/**
 * 
 * @author Paul
 *
 *Version 1 on pose la pile sur l'estrade (en (0,0))
 *Version 2 on pose la pile dans notre zone de depart 
 *attention executer le script 1 avant le 2 sinon impossible de recuperer la balle
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
	public void execute(int version, GameState<Robot> stateToConsider,ArrayList<Hook> hooksToConsider,boolean shouldRetryIfBlocke) throws UnableToMoveException, SerialConnexionException

	{
		if (version==1)
		{
			stateToConsider.robot.turn(Math.PI/2.0, hooksToConsider, false);
			stateToConsider.robot.moveLengthwise(100, hooksToConsider, false);
			stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_GROUND, true);
			stateToConsider.robot.useActuator(ActuatorOrder.OPEN_RIGHT_GUIDE, false);
			stateToConsider.robot.useActuator(ActuatorOrder.OPEN_LEFT_GUIDE, true);		
			stateToConsider.robot.moveLengthwise(-20, hooksToConsider, false);
			stateToConsider.robot.setStoredPlotCount(0);
			stateToConsider.robot.useActuator(ActuatorOrder.CLOSE_RIGHT_GUIDE, false);
			stateToConsider.robot.useActuator(ActuatorOrder.CLOSE_LEFT_GUIDE, true);	
			stateToConsider.robot.moveLengthwise(-80, hooksToConsider, false);
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
	protected void finalise(GameState<?> stateToConsider) throws SerialFinallyException, UnableToMoveException 
	{
		try {
			stateToConsider.robot.useActuator(ActuatorOrder.CLOSE_RIGHT_GUIDE, false);
			stateToConsider.robot.useActuator(ActuatorOrder.CLOSE_LEFT_GUIDE, true);	
			stateToConsider.robot.moveLengthwise(-20);
			stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_LOW, true);
		} catch (SerialConnexionException e) {
			throw new SerialFinallyException ();
		}
	}

}

