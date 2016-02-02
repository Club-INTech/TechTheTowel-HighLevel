package hook.types;

import hook.Hook;
import robot.RobotReal;
import strategie.GameState;
import utils.Config;
import utils.Log;

/**
 * Hook s'activant quand le robot se trouve à une orientation donnée, avec une tolérance donnée
 * @author CF
 */
public class HookOrientationCorrect extends Hook
{
	
	private float orientation;
	
	private float tolerancy;
	
	/**
	 * Construit le hook se déclenchant sur une orientation donnée, avec une tolérance donnée
	 * @param config    endroit où lire la configuration du robot
     * @param log       Système de log sur lequel écrire
     * @param gameState Etat du jeu sur lequel on vérifie si le hook se déclenche ou non
     * @param orientation l'orientation donnée en radians
     * @param tolerancy la tolérance donnée en radians
	 */
	public HookOrientationCorrect(Config config, Log log, GameState<RobotReal> gameState, float oriantation, float tolerancy) 
	{
		super(config, log, gameState);
		this.orientation=oriantation;
		this.tolerancy=tolerancy;
	}

	@Override
	// condition de déclenchement sur une orientation égale, à la tolérance près, à celle donnée en argument
	public boolean evaluate() 
	{
		if (Math.abs(mState.robot.getOrientation() - orientation) <= tolerancy)
		{
			return trigger();
		}
		return false;
	}

}
