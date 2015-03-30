package strategie;

import hook.Hook;

import java.util.ArrayList;

import container.Container;
import container.Service;
import enums.ServiceNames;
import exceptions.ContainerException;
import exceptions.serial.SerialManagerException;
import robot.*;
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
	@SuppressWarnings("unused")
	private Log log;
	
	/** Endroit où on peut lire la configuration du robot */
	private Config config;
	
	/** La table sur laquelle le robot se déplace */
	private Table table;
	
	/** Le robot sur lequel on travaille :*/
	
	private RobotReal robotReal;
	
	/** Le gameState de chaque robot : */
	private GameState<RobotReal> gameState;
	
	/** Les scripts Manager des deux robots*/
	ScriptManager scriptmanager;	
	
	/** Le container necessaire pour les services */
	protected Container container;
	
	/** Les hooks des deux robots*/
	ArrayList<Hook> emptyHookRobotReal;
	
	/**
	 * la liste des scripts a effectuer ainsi que les points et le temps qu'ils impliquent
	 */
	ArrayList<StackScript> list;
	
	
	
	/**
     * Crée la strategie, l'IA decisionnelle
     */
	public Strategie(Config config, Log log, GameState<RobotReal> state)
	{
		this.gameState = state;
		this.config=config;
		this.log=log;
        this.table = state.table;
        this.robotReal = state.robot;
        
		try 
		{
			scriptmanager = (ScriptManager)container.getService(ServiceNames.SCRIPT_MANAGER);
			
		}
		catch (ContainerException | SerialManagerException e) 
		{
			//on affiche un message d'erreur
			log.critical("erreur d'instanciation de l'IA", this);
			//TODO on relance l'initialisation du robot depuis le depart
		}
	}

	public void updateConfig() 
	{
		table.updateConfig();
        robotReal.updateConfig();
	}
	
	public void IA()
	{
		//tant que le match n'est pas fini, on prend des decisions :
		while(gameState.timeEllapsed   <  Integer.parseInt(config.getProperty("temps_match")))
		{
			updateConfig();
			takeDecision(gameState.timeEllapsed);
		}
	}
	
	
	/** Fonction principale : prend une decision en prenant tout en compte */
	public void takeDecision(long timeEllapsed)
	{
		/*
		 * TODO
		 * (j'attends la verison definitive des stacks)
		 * creer les stacks
		 * ajouter le depacement jusqu'au script (si necessaire), ajouter le temps et les points du deplacement 
		 * ajouter les scripts a chacunes des stacks et calculer le temps si necessaire
		 * choisir la stacks la plus adaptée (celle que l'on considere la mieux)
		 */
	}
}
