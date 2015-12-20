package scripts;

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

import enums.Speed;


/**
 * Script pour la fermeture des portes des cabines
 * Version 0 : Deplacement de la serviette aux portes puis fermeture en même temps ; aucune action prevue hors du deplacement ; aucun pathdingding/evitement ; si pb -> arret complet
 * Version 1 : Identique à la version 0, sauf qu'on ferme les portes en marche avant
 * @author Discord, CF
 */
public class CloseDoors extends AbstractScript
{
	public CloseDoors(HookFactory hookFactory, Config config, Log log) {
		super(hookFactory, config, log);
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
	 */
	@Override
	public void execute(int versionToExecute, GameState<Robot> stateToConsider,ArrayList<Hook> hooksToConsider) throws SerialFinallyException, ExecuteException
	{
		//Les parametres de cette version ont ete determines experimentalement, fonctionnel sur robot 2015
		if(versionToExecute == 0)
		{
			try
			{
				//On ralentit pour éviter de démonter les éléments de jeu "Discord-style"
				Speed speedBeforeScriptWasCalled = stateToConsider.robot.getLocomotionSpeed();
				stateToConsider.robot.setLocomotionSpeed(Speed.SLOW_ALL);
				
				//On s'oriente vers les portes
				stateToConsider.robot.turn(-(Math.PI / 2), hooksToConsider, false);
				
				//On ferme les portes
				stateToConsider.robot.moveLengthwise(-600, hooksToConsider, true);
						
				//PORTES FERMEES !
				stateToConsider.obtainedPoints += 20;
				stateToConsider.table.extDoorClosed = true;
				stateToConsider.table.intDoorClosed = true;
			
				//On avance
				stateToConsider.robot.moveLengthwise(100, hooksToConsider, false);
				
				
				stateToConsider.robot.setLocomotionSpeed(speedBeforeScriptWasCalled);
				
				
			}
			catch(UnableToMoveException e)
			{
				finalize(stateToConsider);
				throw new ExecuteException(e);
			}
		}
		
		else if (versionToExecute == 1)
		{
			try
			{
				//On ralentit pour éviter de démonter les éléments de jeu "Discord-style"
				//Speed speedBeforeScriptWasCalled = stateToConsider.robot.getLocomotionSpeed();
				//stateToConsider.robot.setLocomotionSpeed(Speed.SLOW);
				
				//On s'oriente vers les portes
				stateToConsider.robot.turn((Math.PI / 2), hooksToConsider, false);
				
				//On ferme les portes
				stateToConsider.robot.moveLengthwise(600, hooksToConsider, true);
				
				//PORTES FERMEES !
				stateToConsider.obtainedPoints += 20;
				stateToConsider.table.extDoorClosed = true;
				stateToConsider.table.intDoorClosed = true;
				
				//On recule
				stateToConsider.robot.moveLengthwise(-200, hooksToConsider, false);
				
				
				//stateToConsider.robot.setLocomotionSpeed(speedBeforeScriptWasCalled);
			
			}
			catch(UnableToMoveException e)
			{
				finalize(stateToConsider);
				throw new ExecuteException(e);
			}
		}
			
	}

	@Override
	public int remainingScoreOfVersion(int version, GameState<?> state) 
	{
		//TODO
		return 0;
	}

	@Override
	public Circle entryPosition(int version, int ray, Vec2 robotPosition)
	{
		if (version == 0)
		{
			// Point devant les portes côté vert, modification possible selon l'envergure du robot
			return new Circle(new Vec2(1100,1600));
		}
		else if (version ==1)
		{
			// Point devant les portes côté violet
			return new Circle(new Vec2(-1100,1600));
		}
		else
		{
			//TODO jetter une exception
			log.debug("erreur : mauvaise version de script");
			return new Circle(new Vec2(0,0));
		}
	}

	@Override
	public void finalize(GameState<?> state) throws SerialFinallyException
	{
		state.robot.immobilise();
		
	}

	@Override
	public Integer[] getVersion(GameState<?> stateToConsider) 
	{
		return versions;
	}
	
}