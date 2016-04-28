package hook.types;

import hook.Hook;
import robot.RobotReal;
import strategie.GameState;
import utils.Config;
import utils.Log;

/**
 * Hook s'activant quand le robot va au dessus d'une certaine abscisse
 * @author CF
 */
public class HookXGreater extends Hook
{

    private int XValue;

    /**
     * Construit le hook qui se déclenche au-delà de la position en x donnée
     * @param config    endroit ou lire la configuration du robot
     * @param log       Système de log sur lequel écrire
     * @param gameState Etat du jeu sur lequel on vérifie si le hook se déclenche ou non
     * @param XValue la position en x donnée
     */
    public HookXGreater(Config config, Log log, GameState<RobotReal> gameState, int XValue)
    {
        super(config, log, gameState);
        this.XValue = XValue;
    }

    @Override
    // La condition de déclenchement repose sur une abscisse supérieure à celle donnée
    public boolean evaluate() 
    {
        if(mState.robot.getPosition().x > XValue)
        {
        	return trigger();
        }
        return false;
    }
}