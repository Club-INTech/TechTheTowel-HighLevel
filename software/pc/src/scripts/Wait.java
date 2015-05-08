package scripts;

import hook.Hook;
import hook.types.HookFactory;

import java.util.ArrayList;

import robot.Robot;
import smartMath.Circle;
import smartMath.Vec2;
import strategie.GameState;
import utils.Config;
import utils.Log;

public class Wait extends AbstractScript 
{

	public Wait(HookFactory hookFactory, Config config, Log log)
	{
		super(hookFactory, config, log);
		versions = new Integer[]{0};
	}

	@Override
	public void execute(int versionToExecute, GameState<Robot> actualState,ArrayList<Hook> hooksToConsider)
	{
		//on ne fait rien
		log.debug("le robot attend",this);
	}

	@Override
	public int remainingScoreOfVersion(int version, GameState<?> state) {
		//aucun point
		return 0;
	}

	@Override
	public Circle entryPosition(int version, int ray, Vec2 robotPosition) 
	{
		return new Circle(robotPosition);
	}

	@Override
	public void finalize(GameState<?> state)
	{
		//on ne fait rien
	}

	@Override
	public Integer[] getVersion(GameState<?> stateToConsider) 
	{
		return versions;
	}

}
