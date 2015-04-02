package strategie;

import hook.Hook;

import java.util.ArrayList;

import container.Container;
import container.Service;
import enums.ScriptNames;
import enums.ServiceNames;
import exceptions.ContainerException;
import exceptions.PathNotFoundException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialConnexionException;
import exceptions.serial.SerialFinallyException;
import exceptions.serial.SerialManagerException;
import robot.*;
import scripts.AbstractScript;
import scripts.ScriptManager;
import table.Table;
import utils.Log;
import utils.Config;


/**
 *	Classe de l'IA
 * @author Théo
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
     * Crée la strategie, l'IA decisionnelle
     */
	public Strategie(Config config, Log log, GameState<RobotReal> state, GameState<Robot> gameState, ScriptManager scriptManager)
	{
		this.realGameState = state;
		this.config = config;
		this.log = log;
        this.table = state.table;
        this.robotReal = state.robot;
        this.gameState = gameState;
        this.scriptmanager = scriptManager;
	}

	public void updateConfig() 
	{
		table.updateConfig();
        robotReal.updateConfig();
	}
	
	public void IA()
	{
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
				// TODO Auto-generated catch block
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
			AbstractScript script = scriptmanager.getScript(scriptName);
			int[] versions = script.getVersion(realGameState);
			
			for(int i=0; i<(versions.length);i++)
			{
				int valueScript = maxValuable(script, versions[i]);
				if (valueScript>nextScriptValue)
				{
					nextScript=script;
					nextScriptValue=valueScript;
					nextScriptVersion=i;
				}
					
			}
		}
	}

	private int maxValuable(AbstractScript script, int i) 
	{
		// TODO trouver la valeure d'un script
		return 0;
	}
}
