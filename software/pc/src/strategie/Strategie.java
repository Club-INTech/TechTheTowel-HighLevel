package strategie;

import container.Container;
import container.Service;
import enums.ScriptNames;
import exceptions.*;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialConnexionException;
import exceptions.serial.SerialFinallyException;
import hook.Hook;
import hook.methods.CloseDoor;
import hook.types.HookFactory;
import robot.Robot;
import robot.RobotReal;
import scripts.*;
import table.Table;
import utils.Config;
import utils.Log;

import java.util.ArrayList;


/**
 * IA
 * @author discord
 */



public class Strategie implements Service
{
	private Log log;

	private Config config;

	private Table table;

	private GameState<Robot> state;

	private ScriptManager scriptmanager;

	private HookFactory hookFactory;

    private Container container;

    private ArrayList<Hook> hooks = new ArrayList<>();

    private boolean dangerousOpponent;

	private boolean start = true;

	private boolean sandTaken= false;

	private boolean castleTaken = false;

	private boolean fishedOnce = false;

    private boolean abnormalMatch = false;

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
		this.state = new GameState<Robot>(config, log, table, state.robot);
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
		while(true)
        {
            AbstractScript nextScript = decide();

            try
            {
                nextScript.goToThenExec(version(nextScript), state, hooks);
            } catch (BlockedActuatorException e) {
                e.printStackTrace();
            } catch (ExecuteException e) {
                e.printStackTrace();
            } catch (SerialConnexionException | SerialFinallyException e) {
                log.critical("It was at this moment that the robot knew, that he fucked up.");
                e.printStackTrace();
            } catch (UnableToMoveException e) {
                abnormalMatch = true;
                disengage();
                e.printStackTrace();
            } catch (BadVersionException e) {
                if(!(nextScript instanceof ShellGetter))
                    log.critical("Un gogol s'est planté de version pour "+nextScript.toString());
                e.printStackTrace();
            } catch (PointInObstacleException e) {
                e.printStackTrace();
            } catch (PathNotFoundException e) {
                e.printStackTrace();
            }

            if(state.robot.getIsSandInside())
                sandTaken = true;
            if(sandTaken && !state.robot.getIsSandInside())
                castleTaken = true;
            if(state.table.fishesFished > 0)
                fishedOnce = true;
        }
		//scriptedMatch();
	}

    /**
     * Permet de se dégager en cas d'échec de script
     */
    private void disengage()
    {
        if(state.robot.getPosition().x + state.robot.getRobotRadius() >= 1500)
        {
            //TODO sortie
        }
        else if(state.robot.getPositionFast().x - state.robot.getRobotRadius() <= -1500)
        {
            //TODO sortie
        }
        else if(state.robot.getPositionFast().y + state.robot.getRobotRadius() >= 2000)
        {
            //TODO sortie
        }
        else if(state.robot.getPositionFast().y - state.robot.getRobotRadius() <= 0)
        {
            //TODO sortie
        }
    }

    /**
     * Boîte décisive, sélectionne le prochain script
     */
    public AbstractScript decide()
    {
        if(!abnormalMatch)
        {
            if(start)
            {
                start = false;
                return scriptmanager.getScript(ScriptNames.TECH_THE_SAND);
            }
            else if(sandTaken)
            {
                return scriptmanager.getScript(ScriptNames.CASTLE);
            }
            else if(castleTaken)
            {
                return scriptmanager.getScript(ScriptNames.CLOSE_DOORS);
            }
            else if(state.table.extDoorClosed && state.table.intDoorClosed)
            {
                return scriptmanager.getScript(ScriptNames.FISHING);
            }
            else if(fishedOnce)
            {
                return scriptmanager.getScript(ScriptNames.SHELL_GETTER);
            }
            else
            {
                return scriptmanager.getScript(ScriptNames.FISHING);
            }
        }
        return null;
    }

    /**
     * Sélecteur de version, à modifier
     * @param script le script
     * @return la version
     */
    private int version(AbstractScript script)
    {
        if(script instanceof Castle)
            return 2;
        else if(script instanceof CloseDoors)
            return 0;
        else if(script instanceof Fishing)
            return 3;
        else if(script instanceof ShellGetter)
            return -1;
        else if(script instanceof TechTheSand && start)
            return 2;
        else if(script instanceof TechTheSand)
            return 1;
        return 0;
    }

	/**
	 * 	Lance le match scripté de l'IA, suite de scripts
	 */
	private void scriptedMatch()
	{
		//TODO faire un match scripté à lancer si la strategie échoue
	}
}
