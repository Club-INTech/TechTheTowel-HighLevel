package strategie;

import container.Container;
import container.Service;
import enums.*;
import exceptions.*;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialConnexionException;
import exceptions.serial.SerialFinallyException;
import hook.Hook;
import hook.types.HookFactory;
import pathDingDing.PathDingDing;
import robot.Robot;
import robot.RobotReal;
import scripts.*;
import smartMath.Arc;
import smartMath.Vec2;
import table.Table;
import table.obstacles.Obstacle;
import table.obstacles.ObstacleCircular;
import threads.ThreadEyes;
import threads.ThreadSensor;
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
     * Si on a récup les 2 shells proches du tapis au moins une fois
     */
	private boolean gotShells = false;

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
    
    /** si le robot a besoin de faire une marche arrière pour se dégager*/
    private boolean reverse;
    
    /** si le robot s'est bloqué lors de sa rotation*/
    private boolean hasTurned;

    /**
     * Un pb est survenu
     */
    private boolean shitHappened = false;


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
        shells = state.table.deleteAllTheShells();
		while(!ThreadTimer.matchEnded)
        {
            AbstractScript nextScript = decide();
            try
            {
                nextScript.goToThenExec(version(nextScript), state, hooks);
                ThreadEyes.forceEvent(EyesEvent.SUCCESS);
            } catch (BlockedActuatorException e) {
                log.critical("Je sais pas comment t'as fait Billy, cette exception ne tombe jamais...");
                e.printStackTrace();
            } catch (SerialConnexionException | SerialFinallyException e) {
                log.critical("It was at this moment that Billy knew, that he fucked up.");
                e.printStackTrace();
            } catch (UnableToMoveException e) {
                abnormalMatch = true;
                shitHappened = true;
                if(e.reason == UnableToMoveReason.OBSTACLE_DETECTED) //On a vu l'ennemi, c'est anormal
                {
                    dangerousOpponent = true;
                    ThreadEyes.forceEvent(EyesEvent.ENNEMY);
                }
                else
                    ThreadEyes.forceEvent(EyesEvent.BLOCKED);
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
            } catch (ExecuteException e) {
                log.critical("Echec de script, on continue");
                abnormalMatch = true;
                shitHappened = true;
                e.printStackTrace();
            }

            state.robot.setBasicDetection(false);
            state.robot.setForceMovement(false);

            if(state.robot.getIsSandInside() && state.robot.getPosition().x < 750)
                sandTaken = true;
            else if(sandTaken)

            if(sandTaken && !state.robot.getIsSandInside())
            {
                castleTaken = true;
                state.robot.setTurningStrategy(TurningStrategy.FASTEST);
                state.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);
            }
            else if(abnormalMatch && state.robot.getIsSandInside()) //Si on est trop loin pour déclencher Castle
            {
                castleTaken = true;
                state.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);
                ThreadSensor.modeBorgne(false);
            }

            if(castleTaken)
                state.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);

            if(state.table.shellsObtained > 0)
                gotShells = true;

            if(!abnormalMatch && state.table.fishesFished>0)
                done = true;

            if(!state.robot.getIsSandInside() && !state.robot.shellsOnBoard && !abnormalMatch && gotShells)
            {
                try {
                    state.robot.useActuator(ActuatorOrder.CLOSE_DOOR, false);
                    state.robot.setTurningStrategy(TurningStrategy.FASTEST);
                    state.changeRobotRadius(TechTheSand.retractedRobotRadius);
                    state.table.getObstacleManager().updateObstacles(TechTheSand.retractedRobotRadius);
                    state.robot.setDoor(false);
                } catch (SerialConnexionException e) {
                    e.printStackTrace();
                }
            }

            if(state.robot.getAreFishesOnBoard())
            {
            	log.debug("Poisson à bord, appel au PDD pour dépose !");
                try
                {
                	if(isInTable())
                	{
                		state.robot.moveToLocation(new Vec2(250,state.robot.getRobotRadius()+10), hooks, table);
                	}
                	else
                	{
                		log.debug("Robot trop proche pour l'appel au PDD, tentative de dégagement !");
                		disengage(nextScript);
                		state.robot.moveToLocation(new Vec2(250,state.robot.getRobotRadius()+10), hooks, table);
                		
                	}
                }
                catch (Exception e)
                {
                	log.debug("Problème d'appel au PathDingDing : " + e);
                }
                try
                {
                	state.robot.turn(Math.PI);
                	freeFishes();
                    state.robot.setAreFishesOnBoard(false);
                }
                catch(Exception e)
                {
                	log.debug("Problème lors de la dépose des poissons : " + e);
                }
            }


        }
		//scriptedMatch();
	}
	
	/** Suites d'ordres pour dépose des poissons */
	public void freeFishes() throws SerialConnexionException
	{
		try
		{
			state.robot.useActuator(ActuatorOrder.MAGNET_DOWN, true);
			state.robot.useActuator(ActuatorOrder.FINGER_DOWN, true);
			state.robot.useActuator(ActuatorOrder.MAGNET_UP, true);
			state.robot.useActuator(ActuatorOrder.FINGER_UP, false);
		}
		catch(Exception e)
		{
			throw e;
		}
	}

    /**
     * Permet de se dégager en cas d'échec de script
     */
    private void disengage(AbstractScript script)
    {
    	Speed speedBeforeDisengage = state.robot.getLocomotionSpeed();
    	
        try
        {
            state.robot.setBasicDetection(true);
            arcDisengage(script);
            state.robot.setLocomotionSpeed(speedBeforeDisengage);

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
                if(state.robot.getPositionFast().x > 0)
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
            state.robot.setBasicDetection(false);
        }
        catch (UnableToMoveException e)
        {
            log.critical("CRITICAL : On a pas réussi à se dégager");
            shitHappened = true;
            abnormalMatch = true;
            e.printStackTrace();
        }
    }

    /**
     * Boîte décisive, sélectionne le prochain script
     * Si un mauvais evenement est arrivé, on prends une décision adaptée et on reprends le match normalement
     */
    public AbstractScript decide(){
        if(!abnormalMatch)
        {
            if(start)
            {
                return scriptmanager.getScript(ScriptNames.TECH_THE_SAND);
            }
            else if((done || ThreadTimer.remainingTime() <= 30000) && !state.robot.getIsSandInside())
            {
                try {
                    state.robot.useActuator(ActuatorOrder.CLOSE_DOOR, false);
                    state.changeRobotRadius(TechTheSand.retractedRobotRadius);
                    state.table.getObstacleManager().updateObstacles(TechTheSand.retractedRobotRadius);
                    log.debug(state.robot.getRobotRadius());
                    state.robot.setDoor(false);
                } catch (SerialConnexionException e) {
                    e.printStackTrace();
                }
                return scriptmanager.getScript(ScriptNames.FISHING);
            }
            else if(gotShells && !state.robot.getIsSandInside())
            {
                try {
                    state.robot.useActuator(ActuatorOrder.CLOSE_DOOR, false);
                    state.changeRobotRadius(TechTheSand.retractedRobotRadius);
                    state.table.getObstacleManager().updateObstacles(TechTheSand.retractedRobotRadius);
                    log.debug(state.robot.getRobotRadius());
                    state.robot.setDoor(false);
                } catch (SerialConnexionException e) {
                    e.printStackTrace();
                }
                return scriptmanager.getScript(ScriptNames.FISHING);
            }
            else if(state.table.extDoorClosed && state.table.intDoorClosed && !state.robot.getIsSandInside())
            {
                //return scriptmanager.getScript(ScriptNames.SHELL_GETTER);
                try {
                    state.robot.useActuator(ActuatorOrder.CLOSE_DOOR, false);
                    state.changeRobotRadius(TechTheSand.retractedRobotRadius);
                    state.table.getObstacleManager().updateObstacles(TechTheSand.retractedRobotRadius);
                    log.debug(state.robot.getRobotRadius());
                    state.robot.setDoor(false);
                } catch (SerialConnexionException e) {
                    e.printStackTrace();
                }
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

            if(state.robot.getIsSandInside() && !castleTaken)
                return scriptmanager.getScript(ScriptNames.CASTLE);

            if(state.robot.getIsSandInside() && castleTaken)
                return scriptmanager.getScript(ScriptNames.DROP_THE_SAND);

           // if(!sandTaken && !dangerousOpponent)
               // return scriptmanager.getScript(ScriptNames.TECH_THE_SAND);
            if(!sandTaken && state.robot.getPositionFast().x < 850)
            {
                sandTaken = true;
                state.robot.setBasicDetection(true);
                return scriptmanager.getScript(ScriptNames.CASTLE);
            }
            else if(!sandTaken)
            {
                castleTaken = true;
                return scriptmanager.getScript(ScriptNames.CLOSE_DOORS);
            }
            else if(!done)
            {
                done = true;
                try {
                    state.robot.useActuator(ActuatorOrder.CLOSE_DOOR, false);
                    state.changeRobotRadius(TechTheSand.retractedRobotRadius);
                    state.table.getObstacleManager().updateObstacles(TechTheSand.retractedRobotRadius);
                    state.robot.setDoor(false);
                } catch (SerialConnexionException e) {
                    e.printStackTrace();
                }
                state.robot.setBasicDetection(true);
                return scriptmanager.getScript(ScriptNames.FISHING);
            }

            return scriptmanager.getScript(ScriptNames.FISHING);


        }
    }

    /**
     * Sélecteur de version, à modifier
     * @param script le script
     * @return la version
     */
    private int version(AbstractScript script)
    {
        boolean an = abnormalMatch;
        boolean ab = shitHappened;
        abnormalMatch = false;
        if(script instanceof Castle && !ab)
            return 3;
        else if(script instanceof Castle)
            return 4;
        else if(script instanceof CloseDoors && !an)
            return 0;
        else if(script instanceof CloseDoors)
            return 3;
      //  else if(script instanceof Fishing && state.robot.getPosition().x < 800)
     //       return 3;
        else if(script instanceof Fishing)
            return 4;
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
	
	
	/* ========================================== */
	/* Méthodes pour se dégager des bords de table */
	/* ========================================== */
	
	/** Méthode de désengagement du bord de table par arc*/
	public void arcDisengage(AbstractScript script)
	{
		// tant qu'on n'est pas sorti
		while(!isInTable())
		{
			// rayon arbitraire
			int radius=600;
			try
			{
				state.robot.getOrientation();
				double rOrient = state.robot.getOrientationFast();
				
				// détermination de la marche avant ou arrière
				if(state.robot.getPosition().x<0)
				{
					if(rOrient>=-Math.PI/2 && rOrient< Math.PI/2)
					{
						reverse = false;
					}
					else
					{
						reverse = true;
					}
				}
				else
				{
					if(rOrient >=-Math.PI/2 && rOrient< Math.PI/2)
					{
						reverse = true;
					}
					else
					{
						reverse = false;
					}
					if((rOrient>=-Math.PI && rOrient<=-Math.PI/2) || (rOrient>=-Math.PI/2 && rOrient<=0))
					{
						radius=-radius;
					}
				}

				log.debug("Marche arrière ? : " + reverse);
				log.debug("Tentative de dégagement par arc !");

				// longueur d'arc arbitraire
				int length = 200;
				if (reverse)
				{
					length=-length;
				}
				
				// distinction selon la symétrie
				if(config.getProperty("couleur").equals("violet"))
				{
					radius=-radius;
				}
				
				// Cas spécifique pour les poissons
				if(script instanceof Fishing && state.robot.getPosition().y<1000)
				{
					radius=-radius;
					length=-length;
				}
				else if(script instanceof CloseDoors)
				{
					length= Math.abs(length);
					radius=-Math.abs(radius);
				}

				log.debug("Rayon : " + radius);
				log.debug("Longueur : " + length);

				Arc test = new Arc(radius,length,state.robot.getOrientation(),false);
				state.robot.moveArc(test, hooks);
			}
			catch(Exception e)
			{
				log.debug("Fail : " + e ); // poney
				log.debug("Tentative de déplacement rectiligne !");
				try
				{
					if(reverse)
					{
						state.robot.moveLengthwise(100, hooks, true);
					}
					else
					{
						state.robot.moveLengthwise(-100,hooks,true);
					}
				}
				catch(Exception ex)
				{
					log.debug("Fail : " + ex);
					log.debug("Nouvelle boucle de dégagement !");
				}
			}
		}
	}

	
	/** booléen indiquant si le robot est dans les limites habituelles de la table*/
	public boolean isInTable()
	{
		int botX = state.robot.getPosition().x;
		int botY = state.robot.getPosition().y;
		int radius = state.robot.getRobotRadius();
		try
		{
			if(botX <= 1489 - radius && botX >= -1489 + radius)
			{
				if(botY<=1989 - radius && botY>= 10+radius)
				{
					if(config.getProperty("couleur").equals("vert"))
					{
						log.debug("Robot replacé dans la table !");
						return true;
					}
					else if(config.getProperty("couleur").equals("violet"))
					{
						log.debug("Robot encore hors de table !");
						return false;
					}
				}
			}
			else
			{
				if(config.getProperty("couleur").equals("violet"))
				{
					log.debug("Robot replacé dans la table !");
					return true;
				}
				else if(config.getProperty("couleur").equals("vert"))
				{
					log.debug("Robot encore hors de table !");
					return false;
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		log.debug("Robot encore hors de table !");
		return false;
	}
}
