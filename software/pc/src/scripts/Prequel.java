package scripts;

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

import java.util.ArrayList;

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
	 * Distance en mm entre l'arrière du robot et son centre
	 */
	private int rear_length = 150;
	
	/**
	 * Longueur en mm de la cale pour place le x et le y 
	 */
	private int wedge = 90;
	

	@Override
	public void execute(int versionToExecute, GameState<Robot> actualState, ArrayList<Hook> hooksToConsider) throws SerialFinallyException, ExecuteException 
	{
		
		if (versionToExecute == 0)
		{
			try
			{
				// On prend une vitesse de translation lente pour ne pas exploser le robot
				actualState.robot.setLocomotionSpeed(Speed.SLOW_ALL);
			
				// Le robot regardant vers -pi/2 , on lui demande de reculer
				actualState.robot.moveLengthwise(-1000, hooksToConsider, true);
				
				// On récupère la position en y du robot
				actualState.robot.setPosition(new Vec2(actualState.robot.getPosition().x , 2000 - wedge - rear_length ));
				
				// On fixe l'orientation
				actualState.robot.setOrientation(- Math.PI/2);
				
				//Vitesse normale 
				//actualState.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);
				
				// On rejoint la position Y finale que doit avoir le robot
				actualState.robot.moveLengthwise(850 - rear_length - wedge);
				
				// On s'oriente vers pi
				actualState.robot.turn(Math.PI);
				
				// On reprend une vitesse lente
				//actualState.robot.setLocomotionSpeed(Speed.SLOW_ALL);
				
				// On recule jusqu'à ce que le mur nous bloque
				actualState.robot.moveLengthwise(-1000, hooksToConsider, true);
				
				// On se positionne en x final
				actualState.robot.moveLengthwise(150, hooksToConsider, false);
				
				// Récupération du x
				actualState.robot.setPosition(new Vec2(1500 - rear_length - 150, actualState.robot.getPosition().y));
				
				// Orientation finale
				actualState.robot.setOrientation(Math.PI);
				
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
