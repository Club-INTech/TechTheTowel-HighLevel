package hook.types;

import hook.Hook;
import robot.RobotReal;
import smartMath.Vec2;
import strategie.GameState;
import utils.Config;
import utils.Log;

/**
 * déclenchement avec condition sur position ET orientation
 * @author theo
 */

public class HookIsPositionAndOrientationCorrect extends Hook
{

		
	//centre du cercle qui nous interesse pour activer le hook
	private Vec2 mPoint;
	
	//orientation de declenchement du hook 
	private float mOrientation;
		
	// Tolerance qu'on accorde au robot pour activer le hook, en mm 
	private float mTolerancyPoint;
		
	// Tolerance en orientation qu'on accorde au robot pour activer le hook, en miliradians : 
	// attention à comparer miliradians avec miliradians
	private float mTolerancyOrientation;
	
	public HookIsPositionAndOrientationCorrect(Config config, Log log,GameState<RobotReal> realState,
												Vec2 point, float orientation, float tolerancyPoint, float tolerancyOrientation) 
	{
		super(config, log, realState);
		
		this.mPoint=point;
		this.mOrientation =orientation;
		this.mTolerancyPoint=tolerancyPoint;
		this.mTolerancyOrientation=tolerancyOrientation;
		
	}


	@Override
	public boolean evaluate()
	{
		if(  mState.robot.getPosition().distance(mPoint) < mTolerancyPoint ) //verification de la distance au point
		{
			if(Math.abs(mState.robot.getOrientation()-mOrientation) < mTolerancyOrientation)// verification de l'orientation 
			{
				log.debug("en position ("+mState.robot.getPosition().x+", "+mState.robot.getPosition().y+") et orientation "+mState.robot.getOrientation()+" au trigger du Hook de position et d'orientation");
				return trigger();
			}
		}
	return false;
	}

}
