package scripts;

import java.util.ArrayList;

import robot.Robot;
import smartMath.Circle;
import smartMath.Vec2;
import strategie.GameState;
import table.Table;
import enums.Speed;
import exceptions.ExecuteException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialFinallyException;
import hook.Hook;
import hook.types.HookFactory;
import pathDingDing.Node;
import utils.Config;
import utils.Log;


/**
 * @author julian
 * Script pour la fermeture des portes des cabines
 * Version 0 : Deplacement de la serviette aux portes puis fermeture en même temps ; aucune action prevue hors du deplacement ; aucun pathdingding/evitement ; si pb -> arret complet
 */
public class CloseDoors extends AbstractScript
{
	public CloseDoors(HookFactory hookFactory, Config config, Log log) {
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
	 */
	@Override
	public void execute(int versionToExecute, GameState<Robot> stateToConsider,ArrayList<Hook> hooksToConsider) throws SerialFinallyException, ExecuteException
	{
		//Les parametres de cette version ont ete determines experimentalement, fonctionnel sur robot 2015
		if(versionToExecute == 0)
		{
			try
			{
				//On ralentit pour eviter de demonter les elements de jeu "Discord-style"
				Speed speedBeforeScriptWasCalled = stateToConsider.robot.getLocomotionSpeed();
				stateToConsider.robot.setLocomotionSpeed(Speed.SLOW);
				
				//On s'oriente vers les portes
				stateToConsider.robot.turn(-(Math.PI / 2), hooksToConsider, false);
				
				//On ferme les portes
				stateToConsider.robot.moveLengthwise(-600, hooksToConsider, true);
						
				//PORTES FERMEES !
				stateToConsider.obtainedPoints += 20;
				stateToConsider.table.extDoorClosed = true;
				stateToConsider.table.intDoorClosed = true;
			
				//On avance
				stateToConsider.robot.moveLengthwise(200, hooksToConsider, false);
				
				
				stateToConsider.robot.setLocomotionSpeed(speedBeforeScriptWasCalled);
				
				
			}
			catch(UnableToMoveException e)
			{
				finalize(stateToConsider);
				throw new ExecuteException(e);
			}
			//TODO else
			
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
		//TODO eviter la position d'entree du robot, a modifier quand le pathdingding sera fait
		if (version == 0)
			return new Circle(Table.entryPosition);
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
		//TODO
		
	}

	@Override
	public Integer[] getVersion(GameState<?> stateToConsider) 
	{
		return versions;
	}
	
}