package hook.types;

import hook.Hook;
import robot.RobotReal;
import strategie.GameState;
import utils.Config;
import utils.Log;

/**
 * Hook s'activant quand le robot va en dessous d'une certaine ordonnée
 * @author CF
 */
public class HookYLesser extends Hook
{
	
	private int YValue;
	
	/**
	 * Construit le hook qui se déclenche en deçà de la position en y donnée
	 * @param config    endroit où lire la configuration du robot
     * @param log       Système de log sur lequel écrire
     * @param gameState Etat du jeu sur lequel on vérifie si le hook se déclenche ou non
     * @param YValue la position en y donnée
	 */
	public HookYLesser(Config config, Log log, GameState<RobotReal> gameState, int YValue) 
	{
		super(config, log, gameState);
		this.YValue=YValue;
	}

	@Override
    // condition de déclenchement sur une ordonnée inférieure à celle donnée en argument
	public boolean evaluate() 
	{	
		if (mState.robot.getPosition().x <= YValue)
		{
			return trigger();
		}
		return false;
	}

}
