package strategie;

import hook.Hook;

import java.util.ArrayList;

import pathDingDing.PathDingDing;
import container.Container;
import container.Service;
import enums.ScriptNames;
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
			scriptmanager.getScript(ScriptNames.EXIT_START_ZONE).execute(0, gameState, hookRobot, true);
		} 
		catch (UnableToMoveException | SerialConnexionException | SerialFinallyException e1) 
		{
			log.critical("impossible de sortir de la zone de depart", this);
			Sleep.sleep(500);
			IA();
			return;
		}
		
		//TODO mettre le script du match en entier et en cas d'exeption lancer takeDecision
		
		
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
				nextScript.goToThenExec(nextScriptVersion, gameState, true, hookRobot);
			} 
			catch (UnableToMoveException | SerialConnexionException
					| PathNotFoundException | SerialFinallyException | InObstacleException e) 
			{
				// FIXME choix de l'IA face a un imprevu
				e.printStackTrace();
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
		//on replace le robotChrono pour le calcul du temps et la table (pour ne pas modifier la table actuelle)
		robotReal.copy(robotChrono);
		Table tableCopy = table.clone();
		GameState<Robot> chronoState = new GameState<Robot>(config, log, tableCopy, robotChrono);
		
		
		//calcul de la duree du script
		long durationScript;
		robotChrono.resetChrono();
		try 
		{
			script.goToThenExec(version, chronoState, true, hookRobot);
			durationScript = robotChrono.getCurrentChrono();
		} 
		catch (UnableToMoveException | SerialConnexionException
				| PathNotFoundException | SerialFinallyException | InObstacleException e) 
		{
			durationScript = Long.MAX_VALUE;
		}
		log.debug("script :"+script.getClass().getName(), this);
		log.debug("version :"+version, this);
		log.debug("temps :"+durationScript, this);
		int points = (int) (script.remainingScoreOfVersion(version, realGameState)*((matchDuration-realGameState.timeEllapsed)-durationScript)/durationScript);
		log.debug("points :"+points, this);
		//points = pointsScript * (tempsRestant - duree)/duree
		return points;
		
	}
}
