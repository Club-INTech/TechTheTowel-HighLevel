package scripts;

import java.util.ArrayList;

import enums.Speed;
import exceptions.ExecuteException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialFinallyException;
import hook.Hook;
import hook.types.HookFactory;
import robot.Robot;
import smartMath.Circle;
import smartMath.Vec2;
import strategie.GameState;
import utils.Config;
import utils.Log;

/**
 * Script permettant de positionner le robot avant le match, ou un Junit.
 * Version 0 : On pose le robot près des bacs à poisson, on fait cogner sa diagonale sur le mur qui nous donne la position en y, puis on se dirige vers la cale au niveau du tapis imposant le x
 * @author CF
 */

public class Prequel extends AbstractScript
{
	public Prequel(HookFactory hookFactory, Config config, Log log)
	{
		super(hookFactory, config, log);
		versions = new Integer[] {0};
	}
	
	/**
	 * La position en y du robot
	 */
	private double y;
	

	@Override
	public void execute(int versionToExecute, GameState<Robot> actualState, ArrayList<Hook> hooksToConsider) throws SerialFinallyException, ExecuteException 
	{
		
		if (versionToExecute == 0)
		{
			try
			{
				// On prend une vitesse de rotation lente pour ne pas exploser le coin du robot
				actualState.robot.setLocomotionSpeed(Speed.SLOW);
			
				// Le robot regardant vers pi/2 , on lui demande de tourner vers pi
				actualState.robot.turn(Math.PI/2, hooksToConsider, true);
				
				// Une fois bloqué, on récupère la position en y du robot
				y =  (actualState.robot.robotRay) * Math.asin( actualState.robot.getOrientation() - (Math.PI/2));
				
				// On s'oriente vers pi/2
				actualState.robot.turn(Math.PI/2);
				
				// Vitesse normale 
				actualState.robot.setLocomotionSpeed(Speed.MEDIUM);
				
				// On rejoint la position Y finale que doit avoir le robot
				actualState.robot.moveLengthwise((int) (1150 - y));
				
				// On s'oriente vers pi
				actualState.robot.turn(Math.PI);
				
				// On reprend une vitesse lente
				actualState.robot.setLocomotionSpeed(Speed.SLOW);
				
				// On recule jusqu'à ce que la cale nous bloque en position x finale
				actualState.robot.moveLengthwise(-1000, hooksToConsider, true);
			}
			catch (UnableToMoveException e)
			{
				finalize(actualState);
				throw new ExecuteException(e);
			}
		}
		
	}

	@Override
	public int remainingScoreOfVersion(int version, GameState<?> state) {
		return 0;
	}

	@Override
	public void finalize(GameState<?> state) throws SerialFinallyException 
	{
		state.robot.immobilise();
	}

	@Override
	public Integer[] getVersion(GameState<?> stateToConsider) {
		return null;
	}

	@Override
	public Circle entryPosition(int version, int ray, Vec2 robotPosition) {
		return null;
	}
}
