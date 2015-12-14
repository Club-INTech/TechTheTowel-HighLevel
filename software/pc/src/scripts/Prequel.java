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
 * Version 0 : On pose le robot près des portes avec une cale, ce qui nous donne la position en y. Puis on se dirige vers la cale au niveau du tapis imposant le x
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
	 * Distance entre l'arrière du robot et son centre
	 */
	private int back_length = 150;
	
	/**
	 * Longueur de la cale pour le y
	 */
	private int wedge_length = 100;

	@Override
	public void execute(int versionToExecute, GameState<Robot> actualState, ArrayList<Hook> hooksToConsider) throws SerialFinallyException, ExecuteException 
	{
		
		if (versionToExecute == 0)
		{
			try
			{
				// On prend une vitesse de translation lente pour ne pas exploser le robot
				actualState.robot.setLocomotionSpeed(Speed.SLOW);
			
				// Le robot regardant vers -pi/2 , on lui demande de reculer
				actualState.robot.moveLengthwise(0, hooksToConsider, true);
				
				// Vitesse normale 
				actualState.robot.setLocomotionSpeed(Speed.SLOW);
				
				// On rejoint la position Y finale que doit avoir le robot
				actualState.robot.moveLengthwise(850 - back_length - wedge_length);
				
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
		
		else
		{
			log.debug("Mauvaise version du script");
		}
		
	}

	@Override
	public int remainingScoreOfVersion(int version, GameState<?> state) {
		return 0;
	}

	@Override
	public void finalize(GameState<?> state) throws SerialFinallyException 
	{
		// Immobilisation du robot en fin de script
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
