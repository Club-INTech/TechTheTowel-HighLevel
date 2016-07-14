package hook.methods;

import enums.Speed;
import hook.Executable;
import robot.Robot;
import strategie.GameState;

/**
 * Exécutable réduisant la vitesse en cours de mouvement
 * @author CF
 */
public class SpeedDown implements Executable
{

	@Override
	public boolean execute(GameState<Robot> stateToConsider) 
	{
		stateToConsider.robot.setLocomotionSpeed(Speed.SLOW_ALL);
		return false;
	}

}
