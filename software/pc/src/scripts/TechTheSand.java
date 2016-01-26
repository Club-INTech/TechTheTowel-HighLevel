package scripts;

import enums.ActuatorOrder;
import enums.DirectionStrategy;
import enums.Speed;
import enums.TurningStrategy;
import exceptions.ExecuteException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialConnexionException;
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
 * Script pour récupérer le tas de sable central, ne s'occupe pas de le ramener dans notre zone de construction
 * Version 0 et 1: En partant de notre côté, on avance vers le côté ennemi, version 0 si l'on est du côté vert, 1 pour le côté violet
 * @author CF
 */
public class TechTheSand extends AbstractScript
{

    /** TEMPORAIRE */
    public static final int expandedRobotRadius = 400; //TODO a changer
    public static final int retractedRobotRadius = 250; //TODO a changer


    public TechTheSand(HookFactory hookFactory, Config config, Log log)
	{
		super (hookFactory,config,log);
		/**
		 * Versions du script
		 */
		versions = new Integer[]{0};
	}
	
	
	/**
	 * On lance le script choisi.
	 * @param versionToExecute Version a lancer
	 * @param stateToConsider Notre bon vieux robot
	 * @param hooksToConsider Les hooks necessaires pour l'execution du script
	 * @throws SerialConnexionException 
	 */
	@Override
	public void execute(int versionToExecute, GameState<Robot> stateToConsider,ArrayList<Hook> hooksToConsider) throws SerialFinallyException, ExecuteException
	{

		if (versionToExecute == 0)
		{
			try
			{
				// On prend une vitesse lente pour que le robot récupère efficacement le sable
				Speed speedBeforeScriptWasCalled = stateToConsider.robot.getLocomotionSpeed();
				stateToConsider.robot.setLocomotionSpeed(Speed.SLOW_ALL);
				
				// On s'oriente vers le côté ennemi
				stateToConsider.robot.turn((Math.PI), hooksToConsider, false);
				
				// On déploie la vitre droite
				stateToConsider.robot.useActuator(ActuatorOrder.OPEN_DOOR, true);

                stateToConsider.robot.setRobotRadius(TechTheSand.expandedRobotRadius);
				
				// On active la tige accrochante
				stateToConsider.robot.useActuator(ActuatorOrder.START_AXIS, false);
				
				// On avance pour récupérer le sable
				// TODO la distance est arbitraire, à modifier avec les phases de test
				stateToConsider.robot.moveLengthwise(400, hooksToConsider, true);
				
				// Demande au robot de ne tourner que vers la gauche pour ses prochains déplacements
				stateToConsider.robot.setTurningStrategy(TurningStrategy.LEFT_ONLY);

				// Demande au robot de conserver une marche avant pour ses prochains déplacements avec le sable
				stateToConsider.robot.setDirectionStrategy(DirectionStrategy.FORCE_FORWARD_MOTION);

				// On indique au robot qu'il transporte du sable
				stateToConsider.robot.setIsSandInside(true);

				// On s'oriente vers notre serviette
				stateToConsider.robot.turn(0);

				stateToConsider.robot.useActuator(ActuatorOrder.STOP_AXIS, false);
				
				// On reprend notre vitesse habituelle
				stateToConsider.robot.setLocomotionSpeed(speedBeforeScriptWasCalled);
				
			}
			catch (UnableToMoveException | SerialConnexionException e)
			{
				// TODO gérer cette exception, c'est-à-dire par exemple reprendre l'avancée avec plus de puissance
				finalize(stateToConsider);
				throw new ExecuteException(e);
			}
		}
	}

	@Override
	public int remainingScoreOfVersion(int version, GameState<?> state) 
	{
		// TODO comment établir le nombre de point rendu par cette action ?
		return 0;
	}

	@Override
	public Circle entryPosition(int version, int ray, Vec2 robotPosition) 
	{
		if (version == 0)
		{
			return new Circle (new Vec2(400,1700));
		}
		else
		{
			//TODO jetter une exception
			log.debug("erreur : mauvaise version de script");
			return new Circle (new Vec2(0,0));
		}
	}

	@Override
	public void finalize(GameState<?> state) throws SerialFinallyException 
	{
		//TODO arrêter la tige et le moteur de vitre
		
	}

	@Override
	public Integer[] getVersion(GameState<?> stateToConsider) 
	{
		return versions;
	}
}
