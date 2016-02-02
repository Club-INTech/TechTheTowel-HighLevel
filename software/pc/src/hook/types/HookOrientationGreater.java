package hook.types;

import hook.Hook;
import robot.RobotReal;
import strategie.GameState;
import utils.Config;
import utils.Log;

/**
 * Hook s'activant quand le robot va au dessus d'une certaine orientation
 * @author CF
 */
public class HookOrientationGreater extends Hook
{

	private float orientation;
	
	/**
	 * Construit le hook se déclenchant au-delà de l'orientation donnée en argument
	 * @param config    endroit où lire la configuration du robot
     * @param log       Système de log sur lequel écrire
     * @param gameState Etat du jeu sur lequel on vérifie si le hook se déclenche ou non
     * @param orientation l'orientation donnée
	 */
	public HookOrientationGreater(Config config, Log log, GameState<RobotReal> gameState, float orientation) 
	{
		super(config, log, gameState);
		this.orientation=orientation;
	}

	@Override
	// condition de déclenchement sur une orientation supérieure à celle donnée en argument
	public boolean evaluate() 
	{
		if (mState.robot.getOrientation() > orientation)
		{
			return trigger();
		}
		return false;
	}

}
