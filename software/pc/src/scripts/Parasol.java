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
	
	/** Constructeur du Parasol hérité de la classe abstraite AbstractScript */
	public Parasol(HookFactory hookFactory, Config config, Log log)
	{
		super(hookFactory, config, log);
	}
	
	/**
	 * Exécution du script.
	 * @param versionUnused paramètre inutilisé du prototype
	 * @param actualState le robot
	 * @param hooksToConsider hooks nécessaires pour l'exécution du script
	 * @throws SerialConnexionException
	 */
	@Override
	public void execute(int versionUnused, GameState<Robot> actualState, ArrayList<Hook> hooksToConsider) throws SerialFinallyException, ExecuteException
	{
		// le déploiement du parasol n'est possible que pendant la funny action et lorsque celui-ci n'est pas déjà déployé.
		if (actualState.getTimeEllapsed() >= (long)90 && !actualState.robot.stateParasol)
		{
			try
			{
				// envoi du message d'ouverture du parasol au bas niveau
				actualState.robot.useActuator(ActuatorOrder.OPEN_PARASOL, true);
				// actualisation de l'état du parasol maintenant déployé
				actualState.robot.parasolUnfolded();
			
			}
			catch(SerialConnexionException e)
			{
				finalize(actualState);
				throw new ExecuteException(e);
			}
		}
		else
		{
			log.critical("Vous essayez de déployer le parasol ou bien déjà déployé ou bien en dehors de la funny action.");
		}
	}

	
	@Override
	public int remainingScoreOfVersion(int version, GameState<?> actualState)
	{
		if (actualState.getTimeEllapsed() >= 90000 && !actualState.robot.stateParasol)
		{
			// le robot consulte l'action du parasol lors de la funny action
			return 20;
		}
		else
		{
			// le robot consulte l'action du parasol en dehors de la funny action ou lorsque celui-ci est déjà déployé
			return 0;
		}
	}


	@Override
	public Circle entryPosition(int version, int ray, Vec2 robotPosition) 
	{
		// retour de la position actuelle du robot afin que celui-ci ne se déplace pas
		return new Circle(robotPosition);
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