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
import utils.Config;
import utils.Log;


/**
 * @author julian
 * Script pour la fermeture des portes des cabines
 * Version 0 : Deplacement de la serviette aux portes puis fermeture ; aucune action prevue hors du deplacement ; aucun pathdingding ; si pb -> arret complet
 */
public class CloseDoors extends AbstractScript
{

	private Boolean doorsClosed = false;

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
		if(versionToExecute == 0)
		{
			try
			{
				Speed speedBeforeScriptWasCalled = stateToConsider.robot.getLocomotionSpeed();
				stateToConsider.robot.setLocomotionSpeed(Speed.BETWEEN_SCRIPTS_SLOW);
				// TODO Deplacement en biais (vive la trigo)
				stateToConsider.robot.moveLengthwise(300, hooksToConsider, false);
				
			
				//On tourne le robot vers les portes
				stateToConsider.robot.turn((Math.PI / 2), hooksToConsider, false);
				
			
				//On deplace le robot vers les portes, sans rentrer dans le tas
				stateToConsider.robot.moveLengthwise(750, hooksToConsider, false);
	
				//On ferme les portes, (20) A CHANGER !!!!!
				stateToConsider.robot.moveLengthwiseTowardWall(20, hooksToConsider);
			
				//On recule
				stateToConsider.robot.moveLengthwiseWithoutDetection(-30, hooksToConsider, false);
				
				//PORTES FERMEES !
				stateToConsider.robot.setLocomotionSpeed(speedBeforeScriptWasCalled);
				
				doorsClosed = true;
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
		// TODO Comptage du score
		return 0;
	}

	@Override
	public Circle entryPosition(int version, int ray, Vec2 robotPosition)
	{
		if (version == 0)
			return new Circle(Table.entryPosition);
		else
		{
			log.debug("erreur : mauvaise version de script");
			return new Circle(Table.entryPosition);
		}
	}

	@Override
	public void finalize(GameState<?> state) throws SerialFinallyException
	{
		
		if(this.doorsClosed)
			state.obtainedPoints += 40;
		state.table.doorsClosed = this.doorsClosed;
	}

	@Override
	public Integer[] getVersion(GameState<?> stateToConsider) 
	{
		return versions;
	}
	
}