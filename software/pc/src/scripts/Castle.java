package scripts;

import java.util.ArrayList;

import exceptions.ExecuteException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialFinallyException;
import hook.Hook;
import hook.types.HookFactory;
import robot.Robot;
import smartMath.Circle;
import smartMath.Vec2;
import strategie.GameState;
import table.obstacles.ObstacleRectangular;
import utils.Config;
import utils.Log;

/**
 * Script pour récupérer le tas de sable devant notre tapis
 * Version 0 : pousse le tas de sable en ligne droite jusqu'à la zone de construction
 * @author CF
 */
public class Castle extends AbstractScript
{
	public Castle(HookFactory hookFactory, Config config, Log log) 
	{
		super(hookFactory, config, log);
		
		/**
		 * Versions du script
		 */
		versions = new Integer[]{0};
	}

	@Override
	public void execute(int versionToExecute, GameState<Robot> stateToConsider, ArrayList<Hook> hooksToConsider) throws SerialFinallyException, ExecuteException 
	{
		
		if (versionToExecute == 0)
		{
			try
			{
				// on indique que le sable se trouve dans le robot
				stateToConsider.robot.setIsSandInside(true);
				
				// on pousse le tas de sable dans la zone de contruction
				stateToConsider.robot.moveLengthwise(400,hooksToConsider,false);
				
				// on liste les obstacles rectangulaires 
				ArrayList<ObstacleRectangular> mRectangles = stateToConsider.table.getObstacleManager().getRectangles();
				
				// et on supprime le tas de sable
				for (int i=0;i< mRectangles.size();i++)
				{
					if (mRectangles.get(i).isInObstacle(new Vec2(580,1100)));
					{
						mRectangles.remove(i);
					}
				}
				
				// on s'éloigne de la zone de construction 
				stateToConsider.robot.moveLengthwise(-200,hooksToConsider,false);
				
				// on indique qu'on ne transporte plus de sable
				stateToConsider.robot.setIsSandInside(false);
				
				// on gagne 12 points
				stateToConsider.obtainedPoints+=12;
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	public int remainingScoreOfVersion(int version, GameState<?> state) 
	{
		if (version == 0)
		{
			return 12;
		}
		else
		{
			return 0;
		}
	}

	@Override
	public Circle entryPosition(int version, int ray, Vec2 robotPosition)
	{
		if (version == 0)
		{
			return (new Circle(new Vec2(900 + ray, 900)));
		}
		else
		{
			log.debug("erreur : mauvaise version de script");
			return new Circle(new Vec2(0,0));
		}
	}

	@Override
	public void finalize(GameState<?> state) throws UnableToMoveException, SerialFinallyException 
	{
		
	}

	@Override
	public Integer[] getVersion(GameState<?> stateToConsider) 
	{
		return versions;
	}

}
