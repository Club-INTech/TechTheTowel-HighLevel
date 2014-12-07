package scripts;

import smartMath.Vec2;
import strategie.GameState;
import robot.RobotReal;
import utils.Log;
import utils.Config;
import container.Service;
import hook.types.HookFactory;

import java.util.ArrayList;

import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialConnexionException;
import exceptions.UnknownScriptException;

/**
 * Classe abstraite dont héritent les différents scripts.
 * S'occupe le robotvrai et robotchrono de manière à ce que ce soit transparent pour les différents scripts
 * @author pf, marsu
 */
public abstract class Script implements Service 
{

	// Ces services resteront toujours les mêmes, on les factorise avec un static
	protected static HookFactory hookgenerator;
	protected static Config config;
	protected static Log log;

	/*
	 * versions.get(meta_id) donne la liste des versions associées aux meta_id
	 */
	protected ArrayList<ArrayList<Integer>> versions = new ArrayList<ArrayList<Integer>>();	
	
	public Script(HookFactory hookgenerator, Config config, Log log)
	{
		Script.hookgenerator = hookgenerator;
		Script.config = config;
		Script.log = log;
	}
		
	/**
	 * Exécute vraiment un script
	 * @param id_version
	 * @param state
	 * @param retenter_si_blocage
	 * @throws UnknownScriptException
	 */
	public abstract void agit(int id_version, GameState<RobotReal> state, boolean retenter_si_blocage) throws UnknownScriptException;
	
	/**
	 * Calcule le temps d'exécution de ce script (grâce à robotChrono)
	 * @return le temps d'exécution
	 * @throws PathfindingException 
	 */
	public abstract long calcule();

	/**
	 * Retourne la position d'entrée associée à la version id
	 * @param id de la version
	 * @return la position du point d'entrée
	 */
	public abstract Vec2 point_entree(int id);
   
	/**
	 * Renvoie le score que peut fournir une version d'un script
	 * @param id_version
	 * @param state
	 * @return le score
	 */
	public abstract int score(int id_version, final GameState<?> state);
	
	/**
	 * Exécute le script, avec RobotVrai ou RobotChrono
	 * @throws SerialConnexionException 
	 */
	protected abstract void execute() throws UnableToMoveException, SerialConnexionException;

	/**
	 * Méthode toujours appelée à la fin du script (via un finally). Repli des actionneurs.
	 * @param state
	 */
	abstract protected void termine(GameState<?> state);
	
	public void updateConfig()
	{
	}
}
