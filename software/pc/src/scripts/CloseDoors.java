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
 * Version 0 : Déplacement de la serviette aux portes puis fermeture ; aucune action prévue hors du déplacement ; aucun pathdingding ; si pb -> arrêt complet
 */
public class CloseDoors extends AbstractScript
{
	/**
	 * Distance en x signée entre le centre de la serviette et le centre des portes, a modifier une fois les dimensions du robot connues
	 */
	private int xBetweenEntryAndDoors = -300;
	
	/**
	 * Distance en y signée entre le centre de la serviette et le centre des portes, a modifier une fois les dimensions du robot connues
	 */
	private int yBetweenEntryAndDoors = 850;

	public CloseDoors(HookFactory hookFactory, Config config, Log log) {
		super(hookFactory, config, log);
		/**
		 * Versions du script
		 */
		versions = new Integer[]{0};
		
	}
	
	/**
	 * On lance le script choisi.
	 * @param versionToExecute Version à lancer
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
				stateToConsider.robot.setLocomotionSpeed(Speed.BETWEEN_SCRIPTS_SLOW);
				//On vérifie l'orientation du robot à pi, sinon on le tourne
				if(Math.abs(stateToConsider.robot.getOrientation() - Math.PI) >= (Math.PI / 6))
				{
					stateToConsider.robot.turn(Math.PI);
				}
			
				//On déplace le robot sans chercher à savoir s'il y a un obstacle (début de match, impossible qu'il y ait l'adversaire a moins qu'il ne possède un portail warp)
				stateToConsider.robot.moveLengthwiseWithoutDetection(xBetweenEntryAndDoors, hooksToConsider, false);
			
				//Verification de la position en x et correction
				if(Math.abs(stateToConsider.robot.getPosition().x - 1050) >= 50)
				{
					stateToConsider.robot.moveLengthwiseWithoutDetection((stateToConsider.robot.getPositionFast().x - 1050), hooksToConsider, false);
				}
			
				//On tourne le robot vers les portes
				stateToConsider.robot.turn((Math.PI / 2), hooksToConsider, false);
				
				//Vérification de l'orientation et correction
				if((Math.abs(stateToConsider.robot.getOrientation() - (Math.PI /2)) >= (Math.PI / 6)))
				{
					stateToConsider.robot.turn((Math.PI / 2), hooksToConsider, false);
				}
			
				//On déplace le robot vers les portes, sans rentrer dans le tas
				stateToConsider.robot.moveLengthwise((yBetweenEntryAndDoors-100), hooksToConsider, false);
			
				//Vérification et correction
				if(Math.abs(stateToConsider.robot.getPosition().y) >= 50)
				{
					stateToConsider.robot.moveLengthwise((stateToConsider.robot.getPositionFast().y - (yBetweenEntryAndDoors-100)), hooksToConsider, false);
				}
			
				//On ferme les portes, (20) A CHANGER !!!!!
				stateToConsider.robot.moveLengthwiseTowardWall(20, hooksToConsider);
			
				//On recule
				stateToConsider.robot.moveLengthwiseWithoutDetection(-30, hooksToConsider, false);
				
				//PORTES FERMEES !
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
		
		
	}

	@Override
	public Integer[] getVersion(GameState<?> stateToConsider) {
		return versions;
	}
	
}