package strategie;

import container.Container;
import container.Service;
import enums.ActuatorOrder;
import enums.ScriptNames;
import enums.UnableToMoveReason;
import exceptions.*;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialConnexionException;
import exceptions.serial.SerialFinallyException;
import hook.Hook;
import hook.types.HookFactory;
import robot.Robot;
import robot.RobotReal;
import scripts.*;
import table.Table;
import table.obstacles.ObstacleCircular;
import threads.ThreadTimer;
import utils.Config;
import utils.Log;

import java.util.ArrayList;


/**
 * IA
 * @author discord, CF
 */



public class Strategie implements Service
{
	private Log log;

	private Config config;

	private Table table;

	private GameState<Robot> state;

	private ScriptManager scriptmanager;

	private HookFactory hookFactory;

    private ArrayList<Hook> hooks = new ArrayList<>();

    private ThreadTimer timer;

    /**
     * Si l'adversaire est de haut-niveau
     */
    private boolean dangerousOpponent;

    /**
     * Utile au noyau décisionnel pour lancer le script de sortie
     */
	private boolean start = true;

    /**
     * Si le sable a été pris
     */
	private boolean sandTaken= false;

    /**
     * Si le sable a été déposé
     */
	private boolean castleTaken = false;

    /**
     * Si on a pêché au moins une fois
     */
	private boolean fishedOnce = false;

    /**
     * Mode match anormal, soit on a eu un blocage mécanique, soit l'adversaire est venu nous les briser
     */
    private boolean abnormalMatch = false;

    /**
     * Utile en mode match parfait, permet d'indiquer que l'on a terminé les actions principale, on spam Fishing
     */
    private boolean done = false;

    /**
     * Permet de sauvegarder les coquillages si l'on a dû les supprimer
     */
    private ArrayList<ObstacleCircular> shells;


 /**
 * Crée la strategie, l'IA decisionnelle
 * @param config
 * @param log
 * @param state
 */
	public Strategie(Log log, Config config, Table table, GameState<RobotReal> state, ThreadTimer timer)
	{
		this.log = log;
		this.config = config;
        this.timer = timer;
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
            } catch (ExecuteException|BlockedActuatorException e) {
                log.critical("Je sais pas comment t'as fait Billy, cette exception ne tombe jamais...");
                e.printStackTrace();
            } catch (SerialConnexionException | SerialFinallyException e) {
                log.critical("It was at this moment that the robot knew, that he fucked up.");
                e.printStackTrace();
            } catch (UnableToMoveException e) {
                abnormalMatch = true;
                if(e.reason == UnableToMoveReason.OBSTACLE_DETECTED) //On a vu l'ennemi, c'est anormal
                    dangerousOpponent = true;
                disengage(nextScript);
                e.printStackTrace();
            } catch (BadVersionException e) {
                if(!(nextScript instanceof ShellGetter))
                    log.critical("Un gogol s'est planté de version pour "+nextScript.toString());
                e.printStackTrace();
            } catch (PointInObstacleException | PathNotFoundException e) {
                disengage(nextScript);
                this.shells = state.table.deleteAllTheShells();
                e.printStackTrace();
            }

            if(state.robot.getIsSandInside() && state.robot.getPosition().x < 750)
                sandTaken = true;
            else if(sandTaken && !state.robot.getIsSandInside())
                castleTaken = true;
            else if(abnormalMatch && state.robot.getIsSandInside()) //Si on est trop loin pour déclencher Castle
                castleTaken = true;

            if(state.table.fishesFished > 0)
                fishedOnce = true;
            if(!abnormalMatch && state.table.shellsObtained>0)
                done = true;

            if(!state.robot.getIsSandInside() && !state.robot.shellsOnBoard && !abnormalMatch)
            {
                try {
                    state.robot.useActuator(ActuatorOrder.CLOSE_DOOR, false);
                } catch (SerialConnexionException e) {
                    e.printStackTrace();
                }
            }

            if(state.robot.getAreFishesOnBoard())
            {
                //TODO script de dépose simple des poissons
            }


        }
		//scriptedMatch();
	}

    /**
     * Permet de se dégager en cas d'échec de script
     */
    private void disengage(AbstractScript script)
    {
        try
        {
            if (state.robot.getPosition().x + state.robot.getRobotRadius() >= 1500)
            {
                //TODO sortie
                return;
            }
            else if (state.robot.getPositionFast().x - state.robot.getRobotRadius() <= -1500)
            {
                //TODO sortie
                return;
            }
            else if (state.robot.getPositionFast().y + state.robot.getRobotRadius() >= 2000)
            {
                //TODO sortie
                return;
            }
            else if (state.robot.getPositionFast().y - state.robot.getRobotRadius() <= 0)
            {
                //TODO sortie
                return;
            }


            if (script instanceof Castle) //Dégagement en cas de bloquage en essayant de déposer le sable (trop greedy)
            {
                state.robot.setForceMovement(true);
                if (state.robot.getPositionFast().x > 650)
                {
                    state.robot.moveLengthwise(-100);
                }
                else
                {
                    state.robot.moveLengthwise(100);
                    state.robot.turn(Math.PI);
                    state.robot.moveLengthwise(-400);
                }
                state.robot.setForceMovement(false);
                castleTaken = true;
                return;
            }
            else if(script instanceof ShellGetter) //Si on s'est pris la vitre (shit happens)
            {
                int signe=1;
                if(state.robot.getPositionFast().x < 0)
                {
                    signe = -1;
                }

                if(state.robot.getOrientationFast() > Math.PI/2)
                {
                    state.robot.moveLengthwise(200*signe);
                }
                else if(state.robot.getOrientationFast() > -Math.PI/2)
                {
                    state.robot.moveLengthwise(-200*signe);
                }
                else if(state.robot.getOrientationFast() == Math.PI/2)
                {
                    state.robot.moveLengthwise(-state.robot.getRobotRadius());
                }
                else if(state.robot.getOrientationFast() == -Math.PI/2)
                {
                    state.robot.moveLengthwise(state.robot.getRobotRadius());
                }
                else
                {
                    state.robot.moveLengthwise(signe*200);
                }
            }
        }
        catch (UnableToMoveException e)
        {
            log.critical("CRITICAL : On a pas réussi à se dégager, on a perdu le match");
            e.printStackTrace();
        }
    }

    /**
     * Boîte décisive, sélectionne le prochain script
     * Si un mauvais evenement est arrivé, on prends une décision adaptée et on reprends le match normalement
     */
    public AbstractScript decide()
    {
        if(!abnormalMatch)
        {
            if(start)
            {
                return scriptmanager.getScript(ScriptNames.TECH_THE_SAND);
            }
            else if(done)
            {
                return scriptmanager.getScript(ScriptNames.FISHING);
            }
            else if(fishedOnce)
            {
                return scriptmanager.getScript(ScriptNames.SHELL_GETTER);
            }
            else if(state.table.extDoorClosed && state.table.intDoorClosed)
            {
                return scriptmanager.getScript(ScriptNames.FISHING);
            }
            else if(castleTaken)
            {
                return scriptmanager.getScript(ScriptNames.CLOSE_DOORS);
            }
            else if(sandTaken)
            {
                return scriptmanager.getScript(ScriptNames.CASTLE);
            }
            else
            {
                abnormalMatch = true;
                return decide();
            }
        }
        else
        {
            abnormalMatch = false;

            if(state.robot.getIsSandInside())
                return scriptmanager.getScript(ScriptNames.CASTLE);

            if(state.robot.getIsSandInside() && castleTaken)
                return scriptmanager.getScript(ScriptNames.DROP_THE_SAND);

            if(!sandTaken && !dangerousOpponent)
                return scriptmanager.getScript(ScriptNames.TECH_THE_SAND);
            else if(!sandTaken && state.robot.getPositionFast().x < 850)
                return scriptmanager.getScript(ScriptNames.CASTLE);
            else if(!sandTaken)
            {
                castleTaken = true;
                return scriptmanager.getScript(ScriptNames.CLOSE_DOORS);
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
        else if(script instanceof DropTheSand)
            return 0;
        else if(script instanceof TechTheSand && start)
        {
            start = false;
            return 2;
        }
        else if(script instanceof TechTheSand)
            return 1;
        return 0;
    }

	/**
	 * 	Lance le match scripté de l'IA, suite de scripts
	 */
	private void scriptedMatch()
	{
		//TODO faire un match scripté à lancer si la strategie échoue -> HOW ABOUT NO ?!
	}
}
