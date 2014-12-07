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
	
	/**  système de log sur lequel écrire. */
	protected static Log log;
	
	/**  le fichier de config a partir duquel le script pourra se configurer. */
	protected static Config config;
	
	/**  Factory de hooks a utiliser par les scripts. */
	protected static HookFactory hookFactory;

	/**  Liste des versions du script. */
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
	 *
	 * @param versionToExecute la version du
	 * @param actualState l'état courrant du match. Cet état doit référer un RobotReal et non un RobotChrono.
	 * @param shouldRetryIfBlocked vrai si le robot doit renter le script s'il bloque mécaniquement
	 * @throws UnknownScriptException si le script est inconnu
	 */
	public abstract void actuate(int versionToExecute, GameState<RobotReal> actualState, boolean shouldRetryIfBlocked) throws UnknownScriptException;
	
	/**
	 * Calcule le temps d'exécution de ce script (grâce à robotChrono).
	 *
	 * @return le temps d'exécution
	 */
	public abstract long computeExecutionTime();

	/**
	 * Retourne la position d'entrée associée à la version.
	 *
	 * @param version version dont on veut le point d'entrée
	 * @return la position du point d'entrée
	 */
	public abstract Vec2 entryPosition(int version);
   
	/**
	 * Renvoie le score que peut fournir une version d'un script.
	 * Si l'exécution du script ne rapporterai aucun point étant donné le gamestate fourni, renvois 0.
	 *
	 * @param version version dont on veut le score potentiel
	 * @param state l'état du jeu ou l'on veut évaluer le nombre de point que rapporterait l'execution de la version fournie de ce script.
	 * @return le score demandé
	 */
	public abstract int remainingScoreOfVersion(int version, final GameState<?> state);
	
	/**
	 * Exécute le script, avec RobotVrai ou RobotChrono.
	 *
	 * @throws UnableToMoveException losrque le robot veut se déplacer et que quelque chose sur le chemin cloche et que le robot ne peut s'en défaire simplement: bloquage mécanique immobilisant le robot ou obstacle percu par les capteurs
	 * @throws SerialConnexionException s'il y a un problème de communication avec les cartes électroniques
	 */
	protected abstract void execute() throws UnableToMoveException, SerialConnexionException;

	/**
	 * Méthode toujours appelée à la fin du script via un finally. On des donc certain  que son exécution aura lieu.
	 * Le repli des actionneurs lors de la fin du script a sa place ici et pas ailleurs: si un bras reste déployé en cours de match, il risque de se faire arracher !  
	 *
	 * @param state Etat du jeu au sein duquel il faut finaliser le script
	 */
	abstract protected void finalise(GameState<?> state);
	
	/* (non-Javadoc)
	 * @see container.Service#updateConfig()
	 */
	public void updateConfig()
	{
	}
}
