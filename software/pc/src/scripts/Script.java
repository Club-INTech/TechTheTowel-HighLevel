package scripts;

import smartMath.Vec2;
import strategie.GameState;
import table.Table;
import robot.Robot;
import robot.RobotReal;
import robot.cards.ActuatorsManager;
import utils.Log;
import utils.Config;
import container.Service;
import hook.Hook;
import hook.types.HookGenerator;

import java.util.ArrayList;

import pathdinding.Pathfinding;
import exceptions.Locomotion.BlockedException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialException;
import exceptions.ScriptException;
/**
 * Classe abstraite dont héritent les différents scripts.
 * S'occupe le robotvrai et robotchrono de manière à ce que ce soit transparent pour les différents scripts
 * @author pf, marsu
 */


public abstract class Script implements Service 
{

	// Ces services resteront toujours les mêmes, on les factorise avec un static
	protected static HookGenerator hookgenerator;
	protected static Config config;
	protected static Log log;
	protected static Pathfinding pathfinding;
	protected static Robot robot;
	protected static ActuatorsManager actionneurs;
	protected static Table table;

	/*
	 * versions.get(meta_id) donne la liste des versions associées aux meta_id
	 */
	protected ArrayList<ArrayList<Integer>> versions = new ArrayList<ArrayList<Integer>>();	
	
	public Script(HookGenerator hookgenerator, Config config, Log log, Pathfinding pathfinding, Robot robot, ActuatorsManager move, Table table)
	{
		Script.hookgenerator = hookgenerator;
		Script.config = config;
		Script.log = log;
		Script.pathfinding = pathfinding;
		Script.robot = robot;
		Script.actionneurs = move;
		Script.table = table;
	}
		
	/**
	 * Exécute vraiment un script et fait le deplacement jusqu'au point d'entree
	 * @throws BlockedException 
	 */
	public void goToThenExec(int id_version, Robot robot, boolean retenter_si_blocage) throws ScriptException, BlockedException
	{
		try 
		{
			robot.suit_chemin(pathfinding.computePath(robot.getPosition(),point_entree(id_version)),new ArrayList<Hook>());
		} 
		catch (UnableToMoveException e) 
		{
			log.debug("Script : chemin impossible", this);;
		}
		try 
		{
			execute(id_version);
		} 
		catch (UnableToMoveException | SerialException e) 
		{
			log.debug("script : impossible d'executer le script", this);;
		}
	}
	
	/**
	 * Calcule le temps d'exécution de ce script (grâce à robotChrono)
	 * @return le temps d'exécution
	 * @throws PathfindingException 
	 */
	public long calcule()
	{
		return 42;
	}	

	/**
	 * Retourne la position d'entrée associée à la version id
	 * @param id de la version
	 * @return la position du point d'entrée
	 */
	public abstract Vec2 point_entree(int id);
   
	/**
	 * Renvoie le score que peut fournir une version d'un script
	 * @return le score
	 */
	public abstract int score(int id_version, final GameState<?> state);
	
	/**
	 * Exécute le script, avec RobotVrai ou RobotChrono
	 * @throws SerialException 
	 */
	protected void execute(int id_version) throws UnableToMoveException, SerialException
	{
	}

	/**
	 * Méthode toujours appelée à la fin du script (via un finally). Repli des actionneurs.
	 */
	abstract protected void termine(GameState<?> state);
	
	public void updateConfig()
	{
	}

}
