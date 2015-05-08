package hook.types;

import enums.SensorNames;
import hook.Hook;
import robot.RobotReal;
import strategie.GameState;
import utils.Log;
import utils.Config;
import exceptions.serial.*;

/**
 * Hook se déclenchant lorsque le capteur machoire est activé
 * @author Etienne
 *
 */

class HookJawSensor extends Hook
{

    /**
     * Instancie le hook si le capteur machoire est activé
     * @param config : sur quel objet lire la configuration du match
     * @param log : la sortie de log à utiliser
     * @param realState : lien avec le robot a surveiller pour le déclenchement du hook
     */
	public HookJawSensor(Config config, Log log, GameState<RobotReal> realState)
	{
		super(config, log, realState);
	}
	

    /**
     * Déclenche le hook si le capteur machoire est activé
     */
	public boolean evaluate()
	{
		try
		{
			if((boolean)mState.robot.getSensorValue(SensorNames.JAW_SENSOR))
					return trigger();
			return false;
		}
		catch(SerialConnexionException e)
		{
			log.debug(e.toString(), this);
			return false;
		}
	}
	
}
