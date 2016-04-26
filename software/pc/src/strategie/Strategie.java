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
import smartMath.Vec2;
import table.Table;
import table.obstacles.Obstacle;
import table.obstacles.ObstacleCircular;
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
            } catch (ExecuteException e) {
                log.critical("Echec de script, on continue");
                abnormalMatch = true;
                shitHappened = true;
                e.printStackTrace();
            }

            state.robot.setBasicDetection(false);

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
                } catch (SerialConnexionException e) {
                    e.printStackTrace();
                }
            }

            if(state.robot.getAreFishesOnBoard())
            {
            	log.debug("Poisson à bord, appel au PDD pour dépose !");
                PathDingDing path = new PathDingDing(table,log);
                try
                {
                	if(isInTable())
                	{
                		path.computePath(state.robot.getPosition(), new Vec2(300,state.robot.getRobotRadius()+1), new ArrayList<Obstacle>());
                	}
                	else
                	{
                		log.debug("Robot trop proche pour l'appel au PDD, tentative de dégagement !");
                		disengage(nextScript);
                		path.computePath(state.robot.getPosition(), new Vec2(300,state.robot.getRobotRadius()+1), new ArrayList<Obstacle>());
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
            if (state.robot.getPosition().x + state.robot.getRobotRadius() >= 1500)
            {
            	disengageXPositive();
            	state.robot.setLocomotionSpeed(speedBeforeDisengage);
            }
            else if (state.robot.getPositionFast().x - state.robot.getRobotRadius() <= -1500)
            {
            	//TODO sortie
            	state.robot.setLocomotionSpeed(speedBeforeDisengage);
            	return;
            }
            else if (state.robot.getPositionFast().y + state.robot.getRobotRadius() >= 2000)
            {
                disengageYSand();
                state.robot.setLocomotionSpeed(speedBeforeDisengage);
            }
            else if (state.robot.getPositionFast().y - state.robot.getRobotRadius() <= 0)
            {
                disengageYSea();
            	state.robot.setLocomotionSpeed(speedBeforeDisengage);
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
            log.critical("CRITICAL : On a pas réussi à se dégager, on a perdu le match");
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
            else if(done || ThreadTimer.remainingTime() <= 30000)
            {
                try {
                    state.robot.useActuator(ActuatorOrder.CLOSE_DOOR, false);
                } catch (SerialConnexionException e) {
                    e.printStackTrace();
                }
                return scriptmanager.getScript(ScriptNames.FISHING);
            }
            else if(gotShells)
            {
                try {
                    state.robot.useActuator(ActuatorOrder.CLOSE_DOOR, false);
                } catch (SerialConnexionException e) {
                    e.printStackTrace();
                }
                return scriptmanager.getScript(ScriptNames.FISHING);
            }
            else if(state.table.extDoorClosed && state.table.intDoorClosed)
            {
                return scriptmanager.getScript(ScriptNames.SHELL_GETTER);
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
        boolean ab = shitHappened;
        abnormalMatch = false;
        if(script instanceof Castle && !ab)
            return 3;
        else if(script instanceof Castle)
            return 4;
        else if(script instanceof CloseDoors && !ab)
            return 0;
        else if(script instanceof CloseDoors)
            return 3;
        else if(script instanceof Fishing && state.robot.getPosition().x < 800)
            return 3;
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
	
	/** Méthode débloquant le robot si hors table x positifs*/
	public void disengageXPositive()
	{
		int radius = state.robot.getRobotRadius();
		// axe x limite pour que le robot puisse tourner
		int zone = 1499-radius;
		
		try
		{
			// cas où l'on est entre pi/2 et -pi/2
			if(state.robot.getOrientation()>Math.PI/2 || state.robot.getOrientationFast()<-Math.PI/2)
			{
				reverse=false;
				hasTurned=true;
				state.robot.turn(Math.PI, hooks, true);
			}
			
			else
			{
				reverse=true;
				hasTurned=true;
				state.robot.turn(0,hooks,true);
			}

			// distance minimale séparant le robot des limites de la table
			int move = Math.abs(zone-state.robot.getPosition().x);

			if(reverse)
			{
				move=-move;
			}
			
			// on sort des limites de la table
			state.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);
			log.debug("Aucun blocage en tournant, mouvement rectiligne classique !");
			hasTurned=false;
			state.robot.moveLengthwise(move);

		}
		catch(UnableToMoveException e)
		{
			// dans le cas où le robot s'est bloqué pendant sa rotation
			if(hasTurned)
			{
				log.debug(("Mur percuté avec pour orientation :" + state.robot.getOrientation()));
				try
				{
					// on tente d'avancer au-delà de la zone limite #trigo
					int safe = Math.abs(zone-state.robot.getPosition().x);
					double theta;
					int deltaY;
					double robotOrientation = state.robot.getOrientationFast();

					// détermination de l'angle formé avec l'axe d'équation y constant
					if(robotOrientation>-Math.PI/2 && robotOrientation<Math.PI/2)
					{
						theta=robotOrientation-Math.PI;
					}
					else
					{
						theta=robotOrientation;
					}


					// dans le cas favorable, on se déplace en ligne droite
					int d = (int) Math.abs((safe/Math.cos(theta)));
					
					// composante de d selon y pour voir si la trajectoire rectiligne est intéressante
					deltaY = (int)(Math.abs(Math.tan(theta)*safe));
					if(state.robot.getPositionFast().y+deltaY>1999-radius || state.robot.getPositionFast().y-deltaY<radius)
					{
						log.debug("Ne peut pas sortir sans arcs !");
						return;
					}
					
					if(reverse)
					{
						d=-d;
					}
					state.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);
					state.robot.moveLengthwise(d);
					state.robot.turn(Math.PI);
				}
				catch(UnableToMoveException ex)
				{
					log.debug("Echec de sortie :" + ex);
				}
			}
			// cas où le robot s'est bloqué pendant la trajectoire rectiligne
			else
			{
				log.debug("Robot bloqué pendant le moveLengthwise !");
			}
		}
        return;
	}
	
	/** Méthode débloquant le robot près du bord côté sable*/
	public void disengageYSand()
	{
		int radius = state.robot.getRobotRadius();
		// axe y limite pour que le robot puisse tourner
		int zone = 1999-radius;
		int move = Math.abs(zone-state.robot.getPosition().y);
		
		try
		{
			// cas orentation négative
			if(state.robot.getOrientation()<0)
			{
				reverse=false;
				hasTurned=true;
				state.robot.turn(-Math.PI/2, hooks, true);
			}

			else
			{
				reverse=true;
				hasTurned=true;
				state.robot.turn(Math.PI/2,hooks,true);
				move=-move;
			}
			
			// on sort des limites de la table
			state.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);
			log.debug("Aucun blocage en tournant, mouvement rectiligne classique !");
			hasTurned=false;
			state.robot.moveLengthwise(move);

		}
		catch(UnableToMoveException e)
		{
			// dans le cas où le robot s'est bloqué pendant sa rotation
			if(hasTurned)
			{
				log.debug(("Mur percuté avec pour orientation :" + state.robot.getOrientation()));
				try
				{
					// on tente d'avancer au-delà de la zone limite #trigo
					int safe = Math.abs(zone-state.robot.getPosition().y);
					double theta;
					int deltaX;
					double robotOrientation = state.robot.getOrientationFast();

					// détermination de l'angle formé avec l'axe d'équation x constant
					if(robotOrientation<0)
					{
						theta=-Math.PI/2-robotOrientation;
					}
					else
					{
						theta=Math.PI/2-robotOrientation;
					}

					// dans le cas favorable, on se déplace en ligne droite
					int d = (int) Math.abs((safe/Math.cos(theta)));
					
					// composante de d pingu was here selon x pour voir si la trajectoire rectiligne est intéressante
					deltaX = (int)(Math.tan(theta)*d);
					if(reverse && (state.robot.getPositionFast().x-deltaX>1499-radius || state.robot.getPositionFast().x-deltaX<-1499+radius))
					{
						log.debug("Ne peut pas sortir sans arcs !");
						return;
					}
					
					if(reverse)
					{
						d=-d;
					}
					state.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);
					state.robot.moveLengthwise(d);
				}
				catch(UnableToMoveException ex)
				{
					log.debug("Echec de sortie : " + e);
				}
			}
			// cas où le robot s'est bloqué pendant la trajectoire rectiligne
			else
			{
				log.debug("Robot bloqué pendant le moveLengthwise !");
			}
		}
        return;
	}
	
	/** Méthode débloquant le robot près de la mer*/
	public void disengageYSea()
	{
		log.debug("Début de test Disengage !");
		// axe y limite pour que le robot puisse tourner
		int zone = state.robot.getRobotRadius();
		int move = Math.abs(zone-state.robot.getPosition().y);
		
		try
		{
			// cas orentation négative
			if(state.robot.getOrientation()<0)
			{
				reverse=true;
				hasTurned=true;
				move=-move;
				state.robot.turn(-Math.PI/2, hooks, true);
			}

			else
			{
				reverse=false;
				hasTurned=true;
				state.robot.turn(Math.PI/2,hooks,true);
			}
			
			// on sort des limites de la table
			state.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);
			log.debug("Aucun blocage en tournant, mouvement rectiligne classique !");
			hasTurned=false;
			state.robot.moveLengthwise(move);

		}
		catch(UnableToMoveException e)
		{
			// dans le cas où le robot s'est bloqué pendant sa rotation
			if(hasTurned)
			{
				log.debug(("Mur percuté avec pour orientation :" + state.robot.getOrientation()));
				try
				{
					// on tente d'avancer au-delà de la zone limite #trigo
					int safe = Math.abs(zone-state.robot.getPosition().y);
					double theta;
					int deltaX;

					// détermination de l'angle formé avec l'axe d'équation y constant
					if(state.robot.getOrientationFast()<0)
					{
						theta=state.robot.getOrientationFast() + Math.PI/2;
					}
					else
					{
						theta=Math.PI/2-state.robot.getOrientationFast();
					}

					// dans le cas favorable, on se déplace en ligne droite
					int d = (int) Math.abs((safe/Math.cos(theta)));
					
					// déplacement selon x pour voir si la trajectoire rectiligne est intéressante
					deltaX = (int)(Math.tan(theta)*safe);
					if(reverse && (state.robot.getPositionFast().x-deltaX>1499-state.robot.getRobotRadius() || state.robot.getPositionFast().x-deltaX<-1499+state.robot.getRobotRadius()))
					{
						log.debug("Ne peut pas sortir sans arcs !");
						return;
					}
					
					if(reverse)
					{
						d=-d;
					}
					state.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);
					state.robot.moveLengthwise(d);
					state.robot.turn(Math.PI/2);
				}
				catch(UnableToMoveException ex)
				{
					log.debug("Echec de sortie");
					state.robot.immobilise();
				}
			}
			// cas où le robot s'est bloqué pendant la trajectoire rectiligne
			else
			{
				log.debug("Robot bloqué pendant le moveLengthwise !");
			}
		}
	}
	
	/** booléen indiquant si le robot est dans les limites habituelles de la table*/
	public boolean isInTable()
	{
		int botX = state.robot.getPosition().x;
		int botY = state.robot.getPosition().y;
		int radius = state.robot.getRobotRadius();
		log.debug(botX);
		log.debug(botY);
		if(botX <= 1499 - radius && botX >= -1499 + radius)
		{
			if(botY<=1999 - radius && botY>= 1+radius )
			{
				return true;
			}
		}
		return false;
	}
}
