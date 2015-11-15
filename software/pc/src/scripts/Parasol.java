package scripts;

import java.util.ArrayList;

import enums.ActuatorOrder;
import exceptions.ExecuteException;
import exceptions.serial.SerialConnexionException;
import exceptions.serial.SerialFinallyException;
import hook.Hook;
import hook.types.HookFactory;
import robot.Robot;
import smartMath.*;
import strategie.GameState;
import utils.Config;

import utils.Log;

/*
 * Script pour l'ouverture du parasol
 * Version unique (éléments de la classe AbstractScript non utilisés) ; envoi d'un simple message au bas niveau.
 * @author Cérézas
 */

public class Parasol extends AbstractScript
{
	// Etat du parasol ; overkill
	//TODO passer en documentation + plutot dans robot
	private static boolean opened = false;
	
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
		try
		{
			// envoi du message d'ouverture du parasol au bas niveau
			actualState.robot.useActuator(ActuatorOrder.OPEN_PARASOL, true);
			// actualisation de l'état du parasol
			Parasol.opened = true;
			
		}
		catch(SerialConnexionException e)
		{
			finalize(actualState);
			throw new ExecuteException(e);
		}
	}
	
	//TODO le score varie en foction de la position du parassol et du temps restant dans le match
	@Override
	public int remainingScoreOfVersion(int version, GameState<?> state)
	{
		return 0;
	}

	//TODO retourer la position du robot (comme ca pas de deplacement)
	@Override
	public Circle entryPosition(int version, int ray, Vec2 robotPosition) 
	{
		return new Circle(new Vec2(0,0));
	}

	@Override
	public void finalize(GameState<?> state) throws SerialFinallyException
	{
		// TODO
		
	}

	//TODO
	@Override
	public Integer[] getVersion(GameState<?> stateToConsider) 
	{
		return versions;
	}
	
}