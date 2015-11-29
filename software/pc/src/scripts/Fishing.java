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
 * Version 0 et 1: déplacement le long du bac pour récupérer les poissons, puis déplacement près du filet pour les lâcher. On suppose un seul aller suffisant.
 * @author CF
 */

// TODO Ce script est encore temporaire, surtout concernant le finalize, les exceptions,les versions et le booléen concernant la pose des poissons

public class Fishing extends AbstractScript
{
	public Fishing(HookFactory hookFactory, Config config, Log log) 
	{
		super(hookFactory, config, log);
		/**
		 * Versions du script
		 */
		versions = new Integer[]{0,1};
		
	}
	
	// Définition du booléen AreFishesFished déjà défini dans robot
	private boolean AreFishesFished = false;
	
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
		 * Remarque : dans les deux versions, la première orientation vers pi est valide seulement si on possède des bras des deux côtés !
		 */
		if (versionToExecute == 0)
		{
			try
			{
				// On prend une vitesse lente pour que les aimants puissent récupérer les poissons
				//Speed speedBeforeScriptWasCalled = stateToConsider.robot.getLocomotionSpeed();
				//stateToConsider.robot.setLocomotionSpeed(Speed.SLOW);
				
				// On s'oriente vers le côté ennemi
				stateToConsider.robot.turn((Math.PI), hooksToConsider, false);
				
				// On baisse le bras aimanté
				stateToConsider.robot.useActuator(ActuatorOrder.FISHING_POSITION, true);
				
				// On longe le bac
				stateToConsider.robot.moveLengthwise(420, hooksToConsider, false);
				
				// On indique au robot que les poissons sont sur le bras
				stateToConsider.robot.setAreFishesOnBoard(true);
				
				// On remonte le bras pour passer au dessus du filet
				stateToConsider.robot.useActuator(ActuatorOrder.MIDLE_POSITION, true);
				
				// On avance jusqu'au niveau du filet, distance à vérifier avec le robot final
				stateToConsider.robot.moveLengthwise(200, hooksToConsider, false);
				
				// On lâche les poissons
				stateToConsider.robot.useActuator(ActuatorOrder.FREE_FISHES, true);	
				
				// On indique au robot que les poissons ne sont plus sur le bras
				stateToConsider.robot.setAreFishesOnBoard(false);
				
				// On indique que les poissons sont pris
				stateToConsider.robot.setAreFishesFished(true);
				
				// Points gagnés max
				stateToConsider.obtainedPoints += 40;
				
				//stateToConsider.robot.setLocomotionSpeed(speedBeforeScriptWasCalled);
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
					//Speed speedBeforeScriptWasCalled = stateToConsider.robot.getLocomotionSpeed();
					//stateToConsider.robot.setLocomotionSpeed(Speed.SLOW);
					
					// On s'oriente vers le côté ennemi
					stateToConsider.robot.turn((Math.PI), hooksToConsider, false);
					
					// On baisse le bras aimanté
					stateToConsider.robot.useActuator(ActuatorOrder.FISHING_POSITION, true);
					
					// On longe le bac
					stateToConsider.robot.moveLengthwise(-420, hooksToConsider, false);
					
					// On indique au robot que les poissons sont sur le bras
					stateToConsider.robot.setAreFishesOnBoard(true);
					
					// On remonte le bras pour passer au dessus du filet
					stateToConsider.robot.useActuator(ActuatorOrder.MIDLE_POSITION, true);
					
					// On avance jusqu'au niveau du filet, distance à vérifier avec le robot final
					stateToConsider.robot.moveLengthwise(-200, hooksToConsider, false);
					
					// On lâche les poissons
					stateToConsider.robot.useActuator(ActuatorOrder.FREE_FISHES, true);	
					
					// On indique au robot que les poissons ne sont plus sur le bras
					stateToConsider.robot.setAreFishesOnBoard(false);
					
					// On indique que les poissons sont pris
					stateToConsider.robot.setAreFishesFished(true);
					
					// Points gagnés max
					stateToConsider.obtainedPoints += 40;
					
					//stateToConsider.robot.setLocomotionSpeed(speedBeforeScriptWasCalled);
				}
				catch(UnableToMoveException | SerialConnexionException e)
				{
					finalize(stateToConsider);
					throw new ExecuteException(e);
				}
			
		}
	}

	@Override
	public int remainingScoreOfVersion(int version, GameState<?> state) 
	{
		// Pour les versions 0 et 1, et si les poissons sont pris, ont gagnent les points
		if (version == 0 | version ==1)
		{
			if (AreFishesFished)
			{
				return 40;
			}
		}
		// Dans le cas contraire, aucun points
		return 0;
	}

	@Override
	public Circle entryPosition(int version, int ray, Vec2 robotPosition) 
	{
		// TODO a modifier avec les phases de test
		if (version == 0)
		{
			return new Circle(new Vec2(1000,200));
		}
		
		else if (version == 1)
		{
			return new Circle (new Vec2(-1000,200));
		}
		else
		{
			//TODO jetter une exception
			log.debug("erreur : mauvaise version de script");
			return new Circle(new Vec2(0,0));
		}
	}

	@Override
	public void finalize(GameState<?> stateToConsider) throws SerialFinallyException 
	{
		try
		{
		// On remonte les deux bras simultanément en fin de script
		stateToConsider.robot.useActuator(ActuatorOrder.ARM_INIT, false);
		stateToConsider.robot.useActuator(ActuatorOrder.ARM_INIT, true);
		}
		catch (SerialConnexionException e) 
		{
			log.debug("erreur termine Fishing script : impossible de ranger");
			throw new SerialFinallyException();
		}
	}

	@Override
	public Integer[] getVersion(GameState<?> stateToConsider) 
	{
		// Au vu des deux versions disponibles pour l'instant, sans informations sur le nombre de poissons pris, on retourne les deux versions
		return versions;
	}
	
}
