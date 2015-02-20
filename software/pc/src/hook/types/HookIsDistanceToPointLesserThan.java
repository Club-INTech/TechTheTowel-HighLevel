package hook.types;

import hook.Hook;
import robot.RobotReal;
import smartMath.Vec2;
import strategie.GameState;
import utils.Config;
import utils.Log;

/**
 * Declenchement si le robot est dans un cercle
 * @author theo
 *
 */

public class HookIsDistanceToPointLesserThan extends Hook
{
	//Rayon du cercle de declenchement du hook
	private float mRay;
	
	//centre du cercle qui nous interesse pour activer le hook
	private Vec2 center;
	
	// Tolerance qu'on accorde au robot pour activer le hook (en mm, le hook sera déclenché si le robot est dans [ray-tolerency/2 , ray+tolerency/2] )
	private float mTolerancy;
	
	
	public HookIsDistanceToPointLesserThan(Config config, Log log, GameState<RobotReal> realState, float ray, Vec2 point, float tolerancy) 
	{
		super(config, log, realState);
		
		this.mRay=ray;
		this.center=point;
		this.mTolerancy=tolerancy;
	}

	
	public boolean evaluate()
	{
		if(Math.abs(  Math.pow(center.x - mState.robot.getPosition().x , 2)  +  
					  Math.pow(center.y - mState.robot.getPosition().y , 2 )   ) <  Math.pow( (mRay+mTolerancy/2) , 2 ) ) // verification de la distance au point
		{
			System.out.println("en position ("+mState.robot.getPosition().x+", "+mState.robot.getPosition().y+") au triger du Hook");
            return trigger();
		}
        return false;
	}

}
