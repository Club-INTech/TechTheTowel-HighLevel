package hook.types;

import hook.Hook;
import robot.RobotReal;
import smartMath.Vec2;
import strategie.GameState;
import utils.Config;
import utils.Log;

/**
 * Hook s'activant quand le robot se trouve à une position donnée, avec une tolérance donnée
 * @author CF
 */
public class HookPositionCorrect extends Hook
{

	private Vec2 pos;
	
	private float tolerancy;
	
	/**
	 * Construit le hook se déclenchant sur une position donnée, avec une tolérance donnée
	 * @param config    endroit où lire la configuration du robot
     * @param log       Système de log sur lequel écrire
     * @param gameState Etat du jeu sur lequel on vérifie si le hook se déclenche ou non
     * @param pos la position donnée
     * @param tolerancy la tolérance donnée en mm
	 */
	public HookPositionCorrect(Config config, Log log, GameState<RobotReal> gameState, Vec2 pos, float tolerancy) 
	{
		super(config, log, gameState);
		this.pos=pos;
		this.tolerancy=tolerancy;
	}

	@Override
	// condition de déclenchement sur une position égale, à la tolérance près, à celle donnée en argument
	public boolean evaluate() 
	{
		if (Math.abs(mState.robot.getPosition().distance(pos)) <= tolerancy)
		{
			return trigger();
		}
		return false;
	}

}
