package strategie;

import hook.Hook;

import java.util.ArrayList;

import pathDingDing.PathDingDing;
import container.Container;
import container.Service;
import enums.ActuatorOrder;
import enums.ScriptNames;
import enums.ObstacleGroups;
import enums.Speed;
import enums.UnableToMoveReason;
import exceptions.ConfigPropertyNotFoundException;
import exceptions.InObstacleException;
import exceptions.PathNotFoundException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialConnexionException;
import exceptions.serial.SerialFinallyException;
import robot.*;
import scripts.AbstractScript;
import scripts.ScriptManager;
import table.Table;
import utils.Log;
import utils.Config;
import utils.Sleep;


/**
 *	Classe de l'IA
 * @author Paul
 */



public class Strategie implements Service
{
	/** système de log sur lequel écrire*/
	private Log log;
	
	/** Endroit où on peut lire la configuration du robot */
	private Config config;
	
	/** La table sur laquelle le robot se déplace */
	private Table table;
	
	/** Le robot sur lequel on travaille :*/
	
	private RobotReal robotReal;
	
	/** Le gameState reel*/
	private GameState<RobotReal> realGameState;
	
	
	/** Les scripts Manager des deux robots*/
	ScriptManager scriptmanager;	
	
	/** Le container necessaire pour les services */
	protected Container container;
	
	/** Les hooks des deux robots*/
	ArrayList<Hook> hookRobot = new ArrayList<Hook>();

	/**
	 * le prochain script que l'IA executera mis a jour dans takeDecision
	 */
	private AbstractScript nextScript;
	/**
	 * la valeure en point du prochain script
	 */
	private int nextScriptValue;
	/**
	 * le numero de version du procahin script a executer
	 */
	private int nextScriptVersion;
	
	/**
	 * le trouveur de chemin de la strategie
	 */
	private PathDingDing pathDingDing;
	/**
	 * le chrono de la strategie
	 */
	private RobotChrono robotChrono;

	private int matchDuration;
	
	/**
	 * la liste des scripts a executer pour le match scripté
	 */
	private ArrayList<AbstractScript> matchScriptArray = new ArrayList<AbstractScript>();
	/**
	 * la liste des versions a executer pour le match scripté
	 */
	private ArrayList<Integer> matchVersionArray = new ArrayList<Integer>();
	
	
/**
 * Crée la strategie, l'IA decisionnelle
 * @param config
 * @param log
 * @param state
 * @param scriptManager
 * @param trouveurDeChemin
 */
	public Strategie(Config config, Log log, GameState<RobotReal> state, ScriptManager scriptManager, PathDingDing trouveurDeChemin)
	{
		this.realGameState = state;
		this.config = config;
		this.log = log;
        this.table = state.table;
        this.robotReal = state.robot;
        this.scriptmanager = scriptManager;
        this.pathDingDing = trouveurDeChemin;
		updateConfig();

        robotChrono = new RobotChrono(config, log, pathDingDing);
	}

	public void updateConfig() 
	{
		table.updateConfig();
        robotReal.updateConfig();
        try
        {
            matchDuration = Integer.parseInt(config.getProperty("temps_match"));
		}
        catch (ConfigPropertyNotFoundException e)
        {
        	log.debug("Revoir le code : impossible de trouver la propriété "+e.getPropertyNotFound(), this);
		}

	}
	
	/**
	 * on suppose que tout les AX-12 sont initialisés avant de lancer l'IA
	 * et que la position du robot est la position de depart
	 */
	public void IA()
	{
		GameState<Robot> gameState = new GameState<Robot>(config, log, table, robotReal);
		
		
		//premier script pour sortir de la zone, on essaye en premier de sortir en deposant les tapis et en recuperant le gobelet
		try 
		{
			scriptmanager.getScript(ScriptNames.DROP_CARPET).execute(2, gameState, hookRobot);
		} 
		catch (UnableToMoveException e)
		{
			if (robotReal.getPosition().distance(Table.entryPosition)<250)
			{
				matchScriptArray.add(scriptmanager.getScript(ScriptNames.DROP_CARPET));
				matchVersionArray.add(0);
			}
			
			while (robotReal.getPosition().distance(Table.entryPosition)<250)
			{
				
				try 
				{
					scriptmanager.getScript(ScriptNames.EXIT_START_ZONE).execute(0, gameState, hookRobot);
				} 
				catch (UnableToMoveException | SerialConnexionException | SerialFinallyException e1) 
				{
					log.critical("impossible de sortir de la zone de depart", this);
					Sleep.sleep(500);
				}
			}
				
		}
		catch (SerialConnexionException e) 
		{		
			initInMatch();
		}
		catch (SerialFinallyException e)
		{
			while (true)
			{
				try 
				{
					scriptmanager.getScript(ScriptNames.DROP_CARPET).finalize(gameState);
					break;
				} 
				catch (UnableToMoveException | SerialFinallyException e1) 
				{
				}
			}
		}
		
		
		// match scripté de l'IA
		scriptedMatch(gameState);
					
		//tant que le match n'est pas fini, on prend des decisions :
		while(realGameState.timeEllapsed   <  matchDuration )
		{
			log.debug("======choix script======", this);
			System.out.println();
			
			updateConfig();
			takeDecision();
			
			log.debug("script choisit :"+nextScript.getClass().getName(), this);
			log.debug("version :"+nextScriptVersion, this);
			
			try 
			{
				nextScript.goToThenExec(nextScriptVersion, gameState, hookRobot);
			} 
			catch (PathNotFoundException e1)
			{
				//un obstacle a été ajouté depuis le calcul de robot chrono donc il faut relancher la prise de decision
			}
			catch (UnableToMoveException e1) 
			{
				//si le robot se cogne sans detecter l'obstacle
				if (e1.reason.compareTo(UnableToMoveReason.PHYSICALLY_BLOCKED)==0)
				{
					//on ajoute cet obstacle
					table.getObstacleManager().addObstacle(robotReal.getPosition());
					//on essaye de se degager
					int numberOfTry = 0;
					while (numberOfTry<5)
					{
						try
						{
							if (robotReal.getIsRobotMovingBackward())
								robotReal.moveLengthwise(250, hookRobot, false, false);
							else // si on tourne ou qu'on avancais on recule pour se degager
								robotReal.moveLengthwise(-250, hookRobot, false, false);
							break;
						} 
						catch (UnableToMoveException e2)
						{
							log.warning("impossible de se degager de l'obstacle : tentative n°"+numberOfTry, this);
							numberOfTry++;
						}
					}
					//qu'on ai reussi ou non a se degager on fait autre chose
				}
				//puis on relance la prise de decision
				
				//sinon on relance la pise de decision
			}
			catch (InObstacleException e1)
			{
				//TODO gerer cette exception en cours de match (pas uniquement pour le debug)
				log.debug("le script "+nextScript.getClass()+"emmet un inObstacleException", this);
			}
			catch (SerialConnexionException e1)
			{
				initInMatch();
			}
			catch (SerialFinallyException e1)
			{
				while (true)
				{
					try 
					{
						nextScript.finalize(gameState);
						break;
					} 
					catch (UnableToMoveException | SerialFinallyException e2) 
					{
						log.critical("multiple finalize exceptions", this);
					}
				}
			}
	}
	
	/**
	 * initialize the real robot during a match (because of a SerialConnexionException)
	 * se relance tant qu'il y a des SerialConnexionException (pour preserver la meca)
	 */
	private void initInMatch() 
	{
		try 
		{
			robotReal.useActuator(ActuatorOrder.ELEVATOR_GROUND, false);
		} 
		catch (SerialConnexionException e) 
		{
			log.warning("elevator ne reponds pas (ground)", this);
			initInMatch();
			return;
		}
		
		try 
		{
			robotReal.useActuator(ActuatorOrder.ARM_LEFT_CLOSE, false);
		} 
		catch (SerialConnexionException e)
		{
			log.warning("le bras gauche ne reponds pas", this);
			initInMatch();
			return;
		}
		
		try 
		{
			robotReal.useActuator(ActuatorOrder.ARM_RIGHT_CLOSE, false);
		} catch (SerialConnexionException e) 
		{
			log.warning("le bras droit ne reponds pas", this);
			initInMatch();
			return;
		}
		
		try 
		{
			robotReal.useActuator(ActuatorOrder.CLOSE_RIGHT_GUIDE, false);
		} 
		catch (SerialConnexionException e) 
		{
			log.warning("le guide droit ne reponds pas", this);
			initInMatch();
			return;
		}
		
		try 
		{
			robotReal.useActuator(ActuatorOrder.CLOSE_LEFT_GUIDE, false);
		} 
		catch (SerialConnexionException e) 
		{
			log.warning("le guide gauche ne reponds pas", this);
			initInMatch();
			return;
		}
		
		try 
		{
			robotReal.useActuator(ActuatorOrder.LEFT_CARPET_FOLDUP, false);
		}
		catch (SerialConnexionException e) 
		{
			log.warning("le tapis gauche ne reponds pas", this);
			initInMatch();
			return;
		}
		
		try 
		{
			robotReal.useActuator(ActuatorOrder.RIGHT_CARPET_FOLDUP, false);
		}
		catch (SerialConnexionException e)
		{
			log.warning("le tapis droit ne reponds pas", this);
			initInMatch();
			return;
		}
		
		try
		{
			robotReal.useActuator(ActuatorOrder.LOW_LEFT_CLAP, false);
		} 
		catch (SerialConnexionException e) 
		{
			log.warning("le clap gauche ne reponds pas", this);
			initInMatch();
			return;
		}
		
		try 
		{
			robotReal.useActuator(ActuatorOrder.LOW_RIGHT_CLAP, false);
		}
		catch (SerialConnexionException e2) 
		{
			log.warning("le clap droit ne reponds pas", this);
			initInMatch();
			return;
		}
		
		try
		{
			robotReal.useActuator(ActuatorOrder.ELEVATOR_CLOSE_JAW, true);
		} 
		catch (SerialConnexionException e) 
		{
			log.warning("les machoires ne repondent pas", this);
			initInMatch();
			return;
		}
		
		try 
		{
			robotReal.useActuator(ActuatorOrder.ELEVATOR_LOW, true);
		}
		catch (SerialConnexionException e) 
		{
			log.warning("elevator ne reponds pas (low)", this);
			initInMatch();
			return;
		}
		
		robotReal.setLocomotionSpeed(Speed.SLOW);
	}

	private void scriptedMatch(GameState<Robot> gameState)
	{
		

		
		matchScriptArray.add(scriptmanager.getScript(ScriptNames.GRAB_PLOT));
		matchVersionArray.add(2);
		
		matchScriptArray.add(scriptmanager.getScript(ScriptNames.GRAB_PLOT));
		matchVersionArray.add(34);
		
		matchScriptArray.add(scriptmanager.getScript(ScriptNames.CLOSE_CLAP));
		matchVersionArray.add(-12);
		
		matchScriptArray.add(scriptmanager.getScript(ScriptNames.GRAB_PLOT));
		matchVersionArray.add(1);
		
		matchScriptArray.add(scriptmanager.getScript(ScriptNames.FREE_STACK));
		matchVersionArray.add(0);
		
		matchScriptArray.add(scriptmanager.getScript(ScriptNames.GRAB_PLOT));
		matchVersionArray.add(56);
		
		matchScriptArray.add(scriptmanager.getScript(ScriptNames.FREE_STACK));
		matchVersionArray.add(2);
		
		
		while(!matchScriptArray.isEmpty())
		{
			try 
			{
				boolean tryAgain = true;
				while (tryAgain)
				{
					try 
					{
						matchScriptArray.get(0).goToThenExec(matchVersionArray.get(0), gameState, hookRobot);
						tryAgain = false;
					}
					catch (UnableToMoveException e) 
					{
						if (e.reason.compareTo(UnableToMoveReason.PHYSICALLY_BLOCKED)==0)
						{
							//FIXME degager (ne pas bouger tryAgain)
						}
					} 
					catch (PathNotFoundException e) 
					{
						//on ajoute le script dans le tableau un peu plus loin
						matchScriptArray.add(Math.max(0,matchScriptArray.size()-4), matchScriptArray.get(0));
						matchVersionArray.add(Math.max(0,matchVersionArray.size()-4), matchVersionArray.get(0));
						//et on abandonne le script pour le moment
						tryAgain = false;
					} 
					catch (InObstacleException e) 
					{
						for (ObstacleGroups obstacle : e.getObstacleGroup())
						{
							log.warning("attention, obstacle : "+obstacle.getClass(),this);
							if(obstacle.compareTo(ObstacleGroups.ENNEMY_ROBOTS)==0)
							{
								//on ajoute le script dans le tableau un peu plus loin
								matchScriptArray.add(Math.max(0,matchScriptArray.size()-4), matchScriptArray.get(0));
								matchVersionArray.add(Math.max(0,matchVersionArray.size()-4), matchVersionArray.get(0));
								//et on abandonne le script pour le moment
								tryAgain = false;
							}
						}
					}
				}
				matchScriptArray.remove(0);
				matchVersionArray.remove(0);
			}
			catch (IndexOutOfBoundsException e)
			{
				log.debug("out of bound, IA's scripted match", this);
				return;
			}
			catch (SerialConnexionException | SerialFinallyException e) 
			{
				while (true)
				{
					//on attends 3 secondes pour (re)tenter un finalise
					gameState.robot.sleep(3000);
					try 
					{
						matchScriptArray.get(0).finalize(gameState);
						break;
					} 
					catch (IndexOutOfBoundsException e1)
					{
						log.debug("out of bound, IA's scripted match", this);
						//on ajoute le script de depart pour lancer son finalize (puisqu'il n'y avait pas de script prevu apres c'est pas grave)
						matchScriptArray.add(scriptmanager.getScript(ScriptNames.EXIT_START_ZONE));
					}
					catch (SerialFinallyException | UnableToMoveException e1)
					{
						log.critical("enchainement de SerialFinallyException", this);
					}
				}
			}
		}
		
	}

	/** Fonction principale : prend une decision en prenant tout en compte */
	private void takeDecision()
	{
		//TODO ajouter un script qui ne fait rien si tout les scripts ont deja étés effectués (qui fait 0 points)
		nextScriptValue=Integer.MIN_VALUE;
		for(ScriptNames scriptName : ScriptNames.values())
		{
			if (scriptName != ScriptNames.EXIT_START_ZONE)
			{
				AbstractScript script = scriptmanager.getScript(scriptName);
				Integer[] versions = script.getVersion(realGameState);
				
				for(int i=0; i<(versions.length);i++)
				{
					int currentScriptValue = scriptValue(script, versions[i]);
					if (currentScriptValue>nextScriptValue)
					{
						nextScript=script;
						nextScriptValue=currentScriptValue;
						nextScriptVersion=versions[i];
					}
						
				}
			}
		}
	}

	/**
	 * donne la valeur d'un script au sens de l'IA
	 * @param script le script dont on veut connaitre la valeur
	 * @param version la version du script a tester
	 * @return un entier qui est la valeur de ce script
	 */
	private int scriptValue(AbstractScript script, int version) 
	{
		int points = 0;
		//on replace le robotChrono pour le calcul du temps et la table (pour ne pas modifier la table actuelle)
		robotReal.copy(robotChrono);
		Table tableCopy = table.clone();
		GameState<Robot> chronoState = new GameState<Robot>(config, log, tableCopy, robotChrono);
		
		
		//calcul de la duree du script
		long durationScript;
		robotChrono.resetChrono();
		try 
		{
			script.goToThenExec(version, chronoState, hookRobot);
			durationScript = robotChrono.getCurrentChrono();
		} 
		catch (UnableToMoveException | SerialConnexionException
				| PathNotFoundException | SerialFinallyException e) 
		{
			durationScript = Long.MAX_VALUE;
		} 
		catch (InObstacleException e) 
		{
			//on enleve les obstacles genants en adaptant les points
			if (!e.getObstacleGroup().isEmpty())
			{
				try 
				{
					robotChrono.resetChrono();
					script.goToThenExec(version, chronoState, hookRobot, e.getObstacleGroup());
					durationScript = robotChrono.getCurrentChrono();
				}
				catch (UnableToMoveException | SerialConnexionException
						| PathNotFoundException | SerialFinallyException
						| InObstacleException e1) 
				//en cas de double erreur on suppose que le robot n'y arrivera pas
				{
					durationScript = Long.MAX_VALUE;
				}
			
				//on retire le nombre de points correspondant a ces obstacles = malus
				//on suppose ces obstacles toulours sur la table (a voir si on peut tester)
				for (ObstacleGroups obstacle : e.getObstacleGroup())
				{
					if 
					(	
						obstacle == ObstacleGroups.GOBLET_0 || 
						obstacle == ObstacleGroups.GOBLET_1 || 
						obstacle == ObstacleGroups.GOBLET_2 || 
						obstacle == ObstacleGroups.GOBLET_3 || 
						obstacle == ObstacleGroups.GOBLET_4
					)
						points -= 4;
					else if 
					(	
						obstacle == ObstacleGroups.YELLOW_PLOT_0 || 
						obstacle == ObstacleGroups.YELLOW_PLOT_1 || 
						obstacle == ObstacleGroups.YELLOW_PLOT_2 || 
						obstacle == ObstacleGroups.YELLOW_PLOT_3 || 
						obstacle == ObstacleGroups.YELLOW_PLOT_4 ||
						obstacle == ObstacleGroups.YELLOW_PLOT_5 || 
						obstacle == ObstacleGroups.YELLOW_PLOT_6 || 
						obstacle == ObstacleGroups.YELLOW_PLOT_7 || 
						obstacle == ObstacleGroups.GREEN_PLOT_0 || 
						obstacle == ObstacleGroups.GREEN_PLOT_1 || 
						obstacle == ObstacleGroups.GREEN_PLOT_2 || 
						obstacle == ObstacleGroups.GREEN_PLOT_3 || 
						obstacle == ObstacleGroups.GREEN_PLOT_4 || 
						obstacle == ObstacleGroups.GREEN_PLOT_5 || 
						obstacle == ObstacleGroups.GREEN_PLOT_6 || 
						obstacle == ObstacleGroups.GREEN_PLOT_7
					)
						points -= 5;
					//si il faut suprimer le zone adverse ou le robot enemi on suprime les points de ce script (puni)
					else if
					(
						obstacle == ObstacleGroups.ENNEMY_ROBOTS ||
						obstacle == ObstacleGroups.ENNEMY_ZONE
						
					)
						points = Integer.MIN_VALUE;
				}
			}
			//si aucun obstacle a enlever alors le point visé est hors de la table engueuler les scripts
			else
				durationScript = Long.MAX_VALUE;
			
			
		}
		//FIXME supr debug
		log.debug("script :"+script.getClass().getName(), this);
		log.debug("version :"+version, this);
		log.debug("temps :"+durationScript, this);
		
		
		points += script.remainingScoreOfVersion(version, realGameState);
		points *= ((matchDuration-realGameState.timeEllapsed)-durationScript)/durationScript;
		log.debug("points :"+points, this);
		//points = (pointsScript+malus) * (tempsRestant - duree)/duree
		return points;
		
	}
}
