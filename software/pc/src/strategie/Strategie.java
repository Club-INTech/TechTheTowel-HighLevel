package strategie;

import container.Service;
import exceptions.ConfigPropertyNotFoundException;
import hook.types.HookFactory;
import robot.RobotReal;
import scripts.AbstractScript;
import scripts.ScriptManager;
import table.Table;
import utils.Config;
import utils.Log;


/**
 * IA
 * @author discord
 */



public class Strategie implements Service
{
	private Log log;

	private Config config;

	private Table table;

	private GameState<RobotReal> state;

	private ScriptManager scriptmanager;

	private HookFactory hookFactory;

    private boolean dangerousOpponent;

 /**
 * Crée la strategie, l'IA decisionnelle
 * @param config
 * @param log
 * @param state
 */
	public Strategie(Log log, Config config, Table table, GameState<RobotReal> state)
	{
		this.log = log;
		this.config = config;
		this.table = table;
		this.state = state;
		this.hookFactory = new HookFactory(config, log, state);
		this.scriptmanager = new ScriptManager(hookFactory, config, log);
	}

	public void updateConfig() 
	{
        table.updateConfig();
        state.robot.updateConfig();
        try
        {
            dangerousOpponent = Boolean.parseBoolean(config.getProperty("cDesFousEnFace"));
        }
        catch (ConfigPropertyNotFoundException e)
        {
            log.debug("Revoir le code : impossible de trouver la propriete "+e.getPropertyNotFound());
        }
	}
	
	/**
	 * on suppose que tout les AX-12 sont initialisés avant de lancer l'IA
	 * et que la position du robot est la position de depart
	 */
	public void IA()
	{
		//TODO IA
		scriptedMatch();
	}

    public AbstractScript decide()
    {
        return null;
    }

	/**
	 * 	Lance le match scripté de l'IA, suite de scripts
	 */
	private void scriptedMatch()
	{
		//TODO faire un match scripté à lancer si la strategie échoue
	}
}
