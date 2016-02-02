package hook.types;

import hook.Hook;
import robot.RobotReal;
import strategie.GameState;
import utils.Config;
import utils.Log;

/**
 * Hook s'activant quand le robot va au dessus d'une certaine ordonnée
 * @author CF
 */
public class HookYGreater extends Hook
{

	private int Yvalue;
	
	/**
	 * Construit le hook se déclenchant au-delà de la position en x donnée
	 * @param config    endroit où lire la configuration du robot
     * @param log       Système de log sur lequel écrire
     * @param gameState Etat du jeu sur lequel on vérifie si le hook se déclenche ou non
     * @param YValue la position en y donnée
	 */
	public HookYGreater(Config config, Log log, GameState<RobotReal> gameState, int Yvalue) 
	{
		super(config, log, gameState);
		this.Yvalue=Yvalue;
	}

	@Override
    // condition de déclenchement sur une ordonnée supérieure à celle donnée en argument
	public boolean evaluate() 
	{
		if (mState.robot.getPosition().y > Yvalue)
		{
			return trigger();
		}
		return false;
	}

}
