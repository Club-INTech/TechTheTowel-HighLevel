package strategie;

import hook.Hook;

import java.util.ArrayList;

import pathDingDing.PathDingDing;
import container.Container;
import container.Service;
import enums.ScriptNames;
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
	
	/**
	 * Le gameState a donner aux script (mis a jour a chaque robotChrono et a l'execution du script chosit)
	 */
	private GameState<Robot> gameState;
	
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
	
	/**
     * Crée la strategie, l'IA decisionnelle
     */
	public Strategie(Config config, Log log, GameState<RobotReal> state, GameState<Robot> gameState, ScriptManager scriptManager, PathDingDing trouveurDeChemin)
	{
		this.realGameState = state;
		this.config = config;
		this.log = log;
        this.table = state.table;
        this.robotReal = state.robot;
        this.gameState = gameState;
        this.scriptmanager = scriptManager;
        this.pathDingDing = trouveurDeChemin;
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
		try 
		{
			scriptmanager.getScript(ScriptNames.EXIT_START_ZONE).execute(0, gameState, hookRobot, true);
		} 
		catch (UnableToMoveException | SerialConnexionException e1) 
		{
			log.critical("impossible de sortir de la zone de depart", this);
			Sleep.sleep(500);
			IA();
			return;
		}
		//tant que le match n'est pas fini, on prend des decisions :
		while(realGameState.timeEllapsed   <  Integer.parseInt(config.getProperty("temps_match")))
		{
			updateConfig();
			takeDecision(realGameState.timeEllapsed);
			
			try 
			{
				nextScript.goToThenExec(nextScriptVersion, gameState, true, hookRobot);
			} 
			catch (UnableToMoveException | SerialConnexionException
					| PathNotFoundException | SerialFinallyException e) 
			{
				// FIXME choix de l'IA face a un imprevu
				e.printStackTrace();
			}
		}
	}
	
	
	/** Fonction principale : prend une decision en prenant tout en compte */
	private void takeDecision(long timeEllapsed)
	{
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
		//on replace le robotChrono pour le calcul du temps
		robotReal.copy(robotChrono);
		GameState<Robot> chronoState = new GameState<Robot>(config, log, table, robotChrono);
		
		
		//calcul de la duree du script
		long durationScript;
		long initialTime = chronoState.timeEllapsed;
		try 
		{
			script.goToThenExec(version, chronoState, true, hookRobot);
			durationScript = chronoState.timeEllapsed - initialTime;
		} 
		catch (Exception e) 
		{
			durationScript = Long.MAX_VALUE;
		}
		
		
		return (int) (script.remainingScoreOfVersion(version, gameState)/durationScript);
		
	}
}
