package scripts;

import enums.ActuatorOrder;
import enums.Speed;
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
 * Script pour récuperer les poissons
 * Version 0: déplacement le long du bac pour récupérer les poissons, puis déplacement près du filet pour les lâcher. On suppose un seul aller suffisant.
 * @author CF
 */

public class Fishing extends AbstractScript
{
	public Fishing(HookFactory hookFactory, Config config, Log log) 
	{
		super(hookFactory, config, log);
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
		/*
		 * On ex�cute la version 0 si le robot est dans le terrain vert
		 * et 1 s'il est dans la zone violette
		 */
		if (versionToExecute == 0)
		{
			try
			{
				// On prend une vitesse lente pour que les aimants puissent récupérer les poissons
				Speed speedBeforeScriptWasCalled = stateToConsider.robot.getLocomotionSpeed();
				stateToConsider.robot.setLocomotionSpeed(Speed.SLOW);
				
				// On s'oriente vers le côté ennemi
				stateToConsider.robot.turn((Math.PI), hooksToConsider, false);
				
				// On baisse le bras aimanté
				stateToConsider.robot.useActuator(ActuatorOrder.FISHING_POSITION, true);
				
				// On longe le bac
				stateToConsider.robot.moveLengthwise(420, hooksToConsider, false);
				
				// On remonte le bras pour passer au dessus du filet
				stateToConsider.robot.useActuator(ActuatorOrder.MIDLE_POSITION, true);
				
				// On avance jusqu'au niveau du filet, distance à vérifier avec le robot final
				stateToConsider.robot.moveLengthwise(200, hooksToConsider, false);
				
				// On lâche les poissons
				stateToConsider.robot.useActuator(ActuatorOrder.FREE_FISHES, true);	
				
				// Points gagnés max
				stateToConsider.obtainedPoints += 40;
				
				stateToConsider.robot.setLocomotionSpeed(speedBeforeScriptWasCalled);
			}
			catch(UnableToMoveException | SerialConnexionException e)
			{
				finalize(stateToConsider);
				throw new ExecuteException(e);
			}
		}
		else if (versionToExecute == 1)
		{
			try
				{
					// On prend une vitesse lente pour que les aimants puissent récupérer les poissons
					Speed speedBeforeScriptWasCalled = stateToConsider.robot.getLocomotionSpeed();
					stateToConsider.robot.setLocomotionSpeed(Speed.SLOW);
					
					// On s'oriente vers le côté ennemi
					stateToConsider.robot.turn((Math.PI), hooksToConsider, false);
					
					// On baisse le bras aimanté
					stateToConsider.robot.useActuator(ActuatorOrder.FISHING_POSITION, true);
					
					// On longe le bac
					stateToConsider.robot.moveLengthwise(-420, hooksToConsider, false);
					
					// On remonte le bras pour passer au dessus du filet
					stateToConsider.robot.useActuator(ActuatorOrder.MIDLE_POSITION, true);
					
					// On avance jusqu'au niveau du filet, distance à vérifier avec le robot final
					stateToConsider.robot.moveLengthwise(-200, hooksToConsider, false);
					
					// On lâche les poissons
					stateToConsider.robot.useActuator(ActuatorOrder.FREE_FISHES, true);	
					
					// Points gagnés max
					stateToConsider.obtainedPoints += 40;
					
					stateToConsider.robot.setLocomotionSpeed(speedBeforeScriptWasCalled);
				}
				catch(UnableToMoveException | SerialConnexionException e)
				{
					finalize(stateToConsider);
					throw new ExecuteException(e);
				}
			
		}
	}

	@Override
	public int remainingScoreOfVersion(int version, GameState<?> state) {
		if (version == 0)
		{
			return 40;
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
			// TODO a modifier avec les phases de test
			return new Circle(new Vec2(500,50));
		}
		else
		{
			//TODO jetter une exception
			log.debug("erreur : mauvaise version de script");
			return new Circle(new Vec2(0,0));
		}
	}

	@Override
	public void finalize(GameState<?> state) throws SerialFinallyException {
		// TODO 
		
	}

	@Override
	public Integer[] getVersion(GameState<?> stateToConsider) 
	{
		return versions;
	}
	
}
