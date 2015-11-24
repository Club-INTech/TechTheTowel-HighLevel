package scripts;

import enums.ActuatorOrder;
import enums.ServiceNames;
import enums.Speed;
import enums.Turning;
import exceptions.ExecuteException;
import exceptions.PathNotFoundException;
import exceptions.PointInObstacleException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialConnexionException;
import exceptions.serial.SerialFinallyException;
import hook.Hook;
import hook.types.HookFactory;
import pathDingDing.Node;
import pathDingDing.PathDingDing;
import robot.Robot;
import smartMath.Circle;
import smartMath.Vec2;
import strategie.GameState;
import table.Table;
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
	public TechTheSand(HookFactory hookFactory, Config config, Log log)
	{
		super (hookFactory,config,log);
		/**
		 * Versions du script
		 */
		versions = new Integer[]{0,1};
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
		 * On exécute la version 0 si le robot est dans le terrain vert
		 * et 1 s'il est dans la zone violette
		 */
		if (versionToExecute == 0)
		{
			try
			{
				// On prend une vitesse lente pour que le robot récupère efficacement le sable
				//Speed speedBeforeScriptWasCalled = stateToConsider.robot.getLocomotionSpeed();
				//stateToConsider.robot.setLocomotionSpeed(Speed.SLOW);
				
				// On s'oriente vers le côté ennemi
				stateToConsider.robot.turn((Math.PI), hooksToConsider, false);
				
				// On déploie la vitre droite
				// TODO créer un ordre ou autre pour communiquer avec le bas niveau sue l'utilisation du moteur de vitre
				
				// On active la tige accrochante
				// TODO même tâche que celle au dessus: sur quel type de message communiquer avec le bas niveau ?
				
				// On avance pour récupérer le sable
				// TODO la distance est arbitraire, à modifier avec les phases de test
				stateToConsider.robot.moveLengthwise(400, hooksToConsider, true);
				
				// On indique au robot qu'il transporte du sable
				stateToConsider.robot.setIsSandInside(true);
				
				// On s'oriente vers notre serviette
				stateToConsider.robot.turn(0);
				
				// On reprend notre vitesse habituelle
				//stateToConsider.robot.setLocomotionSpeed(speedBeforeScriptWasCalled);
				
			}
			catch (UnableToMoveException e)
			{
				// TODO gérer cette exception, c'est-à-dire par exemple reprendre l'avancée avec plus de puissance
				finalize(stateToConsider);
				throw new ExecuteException(e);
			}
		}
		
		else if (versionToExecute == 1)
		{
			try
			{
				// On prend une vitesse lente pour que le robot récupère efficacement le sable
				//Speed speedBeforeScriptWasCalled = stateToConsider.robot.getLocomotionSpeed();
				//stateToConsider.robot.setLocomotionSpeed(Speed.SLOW);
				
				// On s'oriente vers le côté ennemi
				stateToConsider.robot.turn(0, hooksToConsider, false);
				
				// On déploie la vitre gauche
				// TODO créer un ordre ou autre pour communiquer avec le bas niveau sue l'utilisation du moteur de vitre
				
				// On active la tige accrochante
				// TODO même tâche que celle au dessus: sur quel type de message communiquer avec le bas niveau ?
				
				// On avance pour récupérer le sable
				// TODO la distance est arbitraire, à modifier avec les phases de test
				stateToConsider.robot.moveLengthwise(400, hooksToConsider, true);
				
				// On indique au robot qu'il transporte du sable
				stateToConsider.robot.setIsSandInside(true);
				
				// On s'oriente vers notre serviette
				stateToConsider.robot.turn((Math.PI));
				
				// On reprend notre vitesse habituelle
				//stateToConsider.robot.setLocomotionSpeed(speedBeforeScriptWasCalled);
			}
			catch (UnableToMoveException e)
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
			return new Circle (new Vec2(400,1800));
		}
		else if (version == 1)
		{
			return new Circle (new Vec2(-400,1800));
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
