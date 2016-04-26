package hook.methods;

import hook.Executable;
import robot.Robot;
import strategie.GameState;

public class Immobilise implements Executable
{

	@Override
	public boolean execute(GameState<Robot> stateToConsider) 
	{
		stateToConsider.robot.immobilise();
		return false;
	}

}
