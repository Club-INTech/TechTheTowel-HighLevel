package strategie;

import hook.Hook;

import java.util.ArrayList;

import pathDingDing.PathDingDing;
import container.Container;
import container.Service;
import enums.ScriptNames;
import enums.ObstacleGroups;
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
	ArrayList<Hook> hookRobot;

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
        matchDuration = Integer.parseInt(config.getProperty("temps_match"));
        robotChrono = new RobotChrono(config, log, pathDingDing);
	}

	public void updateConfig() 
	{
		table.updateConfig();
        robotReal.updateConfig();
	}
	
	/**
	 * on suppose que tout les AX-12 sont initialisés avant de lancer l'IA
	 */
	public void IA()
	{
		GameState<Robot> gameState = new GameState<Robot>(config, log, table, robotReal);
		try 
		{
			scriptmanager.getScript(ScriptNames.EXIT_START_ZONE).execute(0, gameState, hookRobot);
		} 
		catch (UnableToMoveException | SerialConnexionException | SerialFinallyException e1) 
		{
			log.critical("impossible de sortir de la zone de depart", this);
			Sleep.sleep(500);
			IA();
			return;
		}
		
		try 
		{
			scriptedMatch(gameState);
		} 
		catch (PathNotFoundException | InObstacleException| UnableToMoveException e1) 
		{
					
			//tant que le match n'est pas fini, on prend des decisions :
			while(realGameState.timeEllapsed   <  Integer.parseInt(config.getProperty("temps_match")))
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
				catch (UnableToMoveException | SerialConnexionException
						| PathNotFoundException | SerialFinallyException | InObstacleException e) 
				{
					// FIXME choix de l'IA face a un imprevu
					e.printStackTrace();
				}
			}
		}
	}
	
	
	private void scriptedMatch(GameState<Robot> gameState) throws PathNotFoundException, InObstacleException, UnableToMoveException 
	{
		//TODO ajouter les scripts ainsi que leur version au match scripté
		ArrayList<AbstractScript> scriptArray = new ArrayList<AbstractScript>();
		ArrayList<Integer> versionArray = new ArrayList<Integer>();

		while(!scriptArray.isEmpty())
		{
			try 
			{
				scriptArray.get(0).goToThenExec(versionArray.get(0), gameState, hookRobot);
			}
			catch (SerialConnexionException | SerialFinallyException e) 
			{
				//on attends 3 secodes pour tenter un finalise
				gameState.robot.sleep(3000);
				try 
				{
					scriptArray.get(0).finalize(gameState);
				} 
				catch (SerialFinallyException e1)
				{
					log.critical("enchainement de SerialFinallyException : arret du robot !", this);
					container.destructor();
					//on attends la fin du match
						try 
						{
							Thread.sleep(1000);
						} 
						catch (InterruptedException e2) 
						{
							e2.printStackTrace();
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
