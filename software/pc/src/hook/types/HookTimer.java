package hook.types;

import hook.Hook;
import robot.RobotReal;
import strategie.GameState;
import utils.Log;
import utils.Config;

/**
 * Hook se déclenchant si le temps est proche du timer à une précision près
 * @author Etienne
 *
 */

class HookTimer extends Hook
{
	/** temps du déclenchement du hook: le hook est déclenché si le temps est proche du temps d'expiration à moins du temps de tolérance */
	private long expirationTime;
	
	/** tolérance temporelle de déclenchement du hook. */
	private int tolerancy;
	
	

    /**
     * Instancie le hook au moment spécifié par le temps d'expiration, à une précision près.
     * @param config : sur quel objet lire la configuration du match
     * @param log : la sortie de log à utiliser
     * @param realState : lien avec le robot a surveiller pour le déclenchement du hook
     * @param expirationTime : le temps où le hook sera exécuté, en millisecondes
     * @param tolerancy : imprécision admise sur le temps de déclanchement du hook
     */
	public HookTimer(Config config, Log log, GameState<RobotReal> realState, long expirationTime, int tolerancy)
	{
		super(config, log, realState);
		this.expirationTime = expirationTime;
		this.tolerancy = tolerancy;
	}
	

    /**
     * Déclenche le hook si la différence entre le temps actuel et le temps d'expiration est plus petite que la tolérance
     */
	public boolean evaluate()
	{
		if(Math.abs(System.currentTimeMillis() - expirationTime) <= tolerancy)
			return trigger();
		return false;
	}
	
}
