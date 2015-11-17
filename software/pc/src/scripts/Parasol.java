package scripts;

import enums.ActuatorOrder;
import exceptions.ExecuteException;
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

/*
 * Script pour l'ouverture du parasol
 * Version unique (éléments de la classe AbstractScript non utilisés) ; envoi d'un simple message au bas niveau.
 * @author Cérézas
 */

public class Parasol extends AbstractScript
{
	// TODO passer en documentation + plutot dans robot (concernant l'état du parasol)
	// je n'ai pas très bien compris ce que tu veux dire ;
	// j'ai créé un static parasolUnfolded dans la classe GameState pour connaître l'état du parasol, est-ce bon ?
	
	public Parasol(HookFactory hookFactory, Config config, Log log)
	{
		super(hookFactory, config, log);
	}
	
	/**
	 * Exécution du script.
	 * @param versionUnused paramètre inutilisé du prototype
	 * @param stateToConsider le robot
	 * @param hooksToConsider hooks nécessaires pour l'exécution du script
	 * @throws SerialConnexionException
	 */
	@Override
	public void execute(int versionUnused, GameState<Robot> actualState, ArrayList<Hook> hooksToConsider) throws SerialFinallyException, ExecuteException
	{
		if (actualState.getTimeEllapsed() >= (long)90 && !actualState.getIsParasolUnfolded())
		{
			try
			{
				// envoi du message d'ouverture du parasol au bas niveau
				actualState.robot.useActuator(ActuatorOrder.OPEN_PARASOL, true);
				// actualisation de l'état du parasol maintenant déployé
				actualState.parasolUnfolded();
			
			}
			catch(SerialConnexionException e)
			{
				finalize(actualState);
				throw new ExecuteException(e);
			}
		}
		else
		{
			// TODO throws new exception à définir si la méthode est appelée alors que le temps écoulé n'est pas 90s ?
		}
	}
	
	//TODO le score varie en fonction de la position du parasol et du temps restant dans le match
	// objection : le parasol est la funny action qui ne fait pas partie de la stratégie des 90s
	@Override
	public int remainingScoreOfVersion(int version, GameState<?> state)
	{
		return 20;
	}


	@Override
	public Circle entryPosition(int version, int ray, Vec2 robotPosition) 
	{
		// retour de la position actuelle du robot afin que celui-ci ne se déplace pas
		return new Circle(robotPosition, 10);
	}

	@Override
	public void finalize(GameState<?> state) throws SerialFinallyException
	{
		// TODO
		
	}

	@Override
	public Integer[] getVersion(GameState<?> stateToConsider) 
	{
		return versions;
	}
	
}