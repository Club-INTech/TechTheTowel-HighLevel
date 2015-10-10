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
	/**
	 * Distance en x signee entre le centre de la serviette et le centre des portes, a modifier une fois les dimensions du robot connues
	 */
	private int xBetweenEntryAndDoors = -300;
	
	/**
	 * Distance en y signee entre le centre de la serviette et le centre des portes, a modifier une fois les dimensions du robot connues
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
				//TODO si on modifie la vitesse il faut la remettre à la fin
				stateToConsider.robot.setLocomotionSpeed(Speed.BETWEEN_SCRIPTS_SLOW);
				//TODO rien ne sert de verifier, on tourne
				//On verifie l'orientation du robot a pi, sinon on le tourne
				if(Math.abs(stateToConsider.robot.getOrientation() - Math.PI) >= (Math.PI / 6))
				{
					stateToConsider.robot.turn(Math.PI);
				}
				//TODO on ne suppose jamais que c'est l debut du match, la strategie decidera
				//On deplace le robot sans chercher Ã  savoir s'il y a un obstacle (debut de match, impossible qu'il y ait l'adversaire a moins qu'il ne possede un portail warp)
				stateToConsider.robot.moveLengthwiseWithoutDetection(xBetweenEntryAndDoors, hooksToConsider, false);
				//si tu veux vraiment etre sur fait un do -> while mais sinon si le robot est bloqué écoute le plutot que de lui redemander la meme chose
				//Verification de la position en x et correction
				if(Math.abs(stateToConsider.robot.getPosition().x - 1050) >= 50)
				{
					stateToConsider.robot.moveLengthwiseWithoutDetection((stateToConsider.robot.getPositionFast().x - 1050), hooksToConsider, false);
				}
			
				//On tourne le robot vers les portes
				stateToConsider.robot.turn((Math.PI / 2), hooksToConsider, false);
				
				//TODO toutes ces verification son innutiles, les méthodes sont bloquantes (le code s'arrete tant que tu n'est pas arrivé) et renvoie des exeption en cas de probleme
				//Verification de l'orientation et correction
				if((Math.abs(stateToConsider.robot.getOrientation() - (Math.PI /2)) >= (Math.PI / 6)))
				{
					stateToConsider.robot.turn((Math.PI / 2), hooksToConsider, false);
				}
			
				//On deplace le robot vers les portes, sans rentrer dans le tas
				stateToConsider.robot.moveLengthwise((yBetweenEntryAndDoors-100), hooksToConsider, false);
				//TODO idem
				//Verification et correction
				if(Math.abs(stateToConsider.robot.getPosition().y) >= 50)
				{
					stateToConsider.robot.moveLengthwise((stateToConsider.robot.getPositionFast().y - (yBetweenEntryAndDoors-100)), hooksToConsider, false);
				}
			
				//On ferme les portes, (20) A CHANGER !!!!!
				stateToConsider.robot.moveLengthwiseTowardWall(20, hooksToConsider);
			
				//On recule
				stateToConsider.robot.moveLengthwiseWithoutDetection(-30, hooksToConsider, false);
				
				//PORTES FERMEES !
				//TODO il faut modifier l'état de la table !!!
				//et le nombre de de points réalisées dans GameState
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
		//TODO
		
	}

	@Override
	public Integer[] getVersion(GameState<?> stateToConsider) 
	{
		return versions;
	}
	
}