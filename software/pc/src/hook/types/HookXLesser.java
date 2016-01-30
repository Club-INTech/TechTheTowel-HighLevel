package hook.types;

import hook.Hook;
import robot.RobotReal;
import strategie.GameState;
import utils.Config;
import utils.Log;

/**
 * Hook s'activant quand le robot va en dessous d'une certaine abscisse
 * @author discord
 */
public class HookXLesser extends Hook
{

    private int XValue;

    /**
     * ce constructeur ne sera appellé que par les constructeurs des classes filles (des hooks bien précis)
     *
     * @param config    endroit ou lire la configuration du robot
     * @param log       Système de log sur lequel écrire
     * @param gameState Etat du jeu sur lequel on vérifie si le hook se déclenche ou non
     */
    public HookXLesser(Config config, Log log, GameState<RobotReal> gameState, int XValue)
    {
        super(config, log, gameState);
        this.XValue = XValue;
    }

    @Override
    public boolean evaluate() {
        if(mState.robot.getPosition().x <= XValue)
            return trigger();
        return false;
    }
}