package hook.methods;

import hook.Executable;
import robot.Robot;
import strategie.GameState;

/** Permet de mettre fishesOnBoard à true*/
public class SetFishesOnBoard implements Executable
{

	@Override
	public boolean execute(GameState<Robot> stateToConsider) 
	{
		stateToConsider.robot.setAreFishesOnBoard(true);
		return false;
	}
	
}
