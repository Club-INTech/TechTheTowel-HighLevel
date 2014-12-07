package scripts;

import smartMath.Vec2;
import strategie.GameState;
import robot.RobotReal;
import utils.Log;
import utils.Config;
import container.Service;
import hook.types.HookFactory;

import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialConnexionException;
import exceptions.UnknownScriptException;

/**
 * Classe abstraite dont héritent les différents scripts.
 * Sles scripts héritants de cette classe peuvent être indifférement exécutés par un RobotReal ou un RobotChrono.
 * @author pf, marsu
 */
public abstract class AbstractScript implements Service 
{
	/** système de log sur lequel écrire */
	protected static Log log;
	
	/** le fichier de config a partir duquel le script pourra se configurer */
	protected static Config config;
	
	/** Factory de hooks a utiliser par les scripts */
	protected static HookFactory hookFactory;

	/** Liste des versions du script */
	protected int[] versions;	
	
	/**
	 * Constructeur a appeller lorsque un script héritant de la classe AbstractScript est instancié.
	 * Le constructeur se charge de renseigner la hookFactory, le système de config et de log.
	 * @param hookFactory La factory a utiliser pour générer les hooks dont pourra avoir besoin le script
	 * @param config le fichier de config a partir duquel le script pourra se configurer
	 * @param log le système de log qu'utilisera le script
	 */
	public AbstractScript(HookFactory hookFactory, Config config, Log log)
	{
		AbstractScript.hookFactory = hookFactory;
		AbstractScript.config = config;
		AbstractScript.log = log;
	}
		
	/**
	 * Exécute vraiment un script sur la vraie table actuelle.
	 * @param versionToExecute la version du
	 * @param actualState
	 * @param shouldRetryIfBlocked
	 * @throws UnknownScriptException
	 */
	public abstract void actuate(int versionToExecute, GameState<RobotReal> actualState, boolean shouldRetryIfBlocked) throws UnknownScriptException;
	
	/**
	 * Calcule le temps d'exécution de ce script (grâce à robotChrono)
	 * @return le temps d'exécution
	 */
	public abstract long computeExecutionTime();

	/**
	 * Retourne la position d'entrée associée à la version id
	 * @param id de la version
	 * @return la position du point d'entrée
	 */
	public abstract Vec2 entryPosition(int id);
   
	/**
	 * Renvoie le score que peut fournir une version d'un script
	 * @param id_version
	 * @param state
	 * @return le score
	 */
	public abstract int remainingScoreOfVersion(int id_version, final GameState<?> state);
	
	/**
	 * Exécute le script, avec RobotVrai ou RobotChrono
	 * @throws SerialConnexionException 
	 * @throws UnableToMoveException
	 */
	protected abstract void execute() throws UnableToMoveException, SerialConnexionException;

	/**
	 * Méthode toujours appelée à la fin du script (via un finally). Repli des actionneurs.
	 * @param state
	 */
	abstract protected void finalise(GameState<?> state);
	
	public void updateConfig()
	{
	}
}
