package scripts;

import container.Service;
import exceptions.BadVersionException;
import exceptions.BlockedActuatorException;
import exceptions.ExecuteException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialConnexionException;
import exceptions.serial.SerialFinallyException;
import hook.Hook;
import hook.types.HookFactory;
import smartMath.Circle;
import smartMath.Vec2;
import strategie.GameState;
import utils.Config;
import utils.Log;

import java.util.ArrayList;

/**
 * Classe abstraite dont héritent les différents scripts.
 * Sles scripts héritants de cette classe peuvent être indifférement exécutés par un Robot ou un Robothrono.
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
	protected Integer[] versions;	
	
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
	 * Va au point d'entrée du script (en utilisant le Pathfinding), puis l'exécute
	 * En fournissant un GameState<Robothrono>, il est possible de chronométrer le temps que l'on metterait a exécuter ce script sans réellement l'exécuter
	 *
	 * @param versionToExecute la version du script
	 * @param actualState l'état courrant du match.
	 * @param hooksToConsider les hooks a considérer lors des déplacements vers ces scripts
	 * @throws UnableToMoveException losrque le robot veut se déplacer et que quelque chose sur le chemin cloche et que le robot ne peut s'en défaire simplement: bloquage mécanique immobilisant le robot ou obstacle percu par les capteurs
	 * @throws SerialConnexionException s'il y a un problème de communication avec une des cartes électroniques
	 * @throws SerialFinallyException si le finally n'est pas correctement execute (erreur critique)
	 * @throws ExecuteException
	 */
	public void goToThenExec(int versionToExecute, GameState actualState, ArrayList<Hook> hooksToConsider) throws UnableToMoveException, BadVersionException, SerialConnexionException, SerialFinallyException, ExecuteException, BlockedActuatorException
	{
		// va jusqu'au point d'entrée de la version demandée
		log.debug("Lancement de " + this + " version " + versionToExecute);
		try 
		{
			if(!actualState.robot.getPosition().equals(entryPosition(versionToExecute,actualState.robot.getRobotRadius(), actualState.robot.getPosition()).position))
				actualState.robot.moveToCircle(entryPosition(versionToExecute,actualState.robot.getRobotRadius(), actualState.robot.getPositionFast()), hooksToConsider, actualState.table);
		}
		catch (UnableToMoveException e)
		{
			log.debug("Catch de "+e+" Impossible de goToThenExec : abandon d'exec, throw de "+e);
			throw e;
		}
		
		
		// exécute la version demandée
		execute(versionToExecute, actualState, hooksToConsider);
	}

	   
	/**
	 * Exécute le script
	 * @param versionToExecute la version du
	 * @param actualState l'état courrant du match.
	 * @param hooksToConsider les hooks a considérer lors des déplacements vers ces scripts
	 * @throws UnableToMoveException losrque le robot veut se déplacer et que quelque chose sur le chemin cloche et que le robot ne peut s'en défaire simplement: bloquage mécanique immobilisant le robot ou obstacle percu par les capteurs
	 * @throws SerialConnexionException s'il y a un problème de communication avec une des cartes électroniques
	 * @throws SerialFinallyException s'il y a un problème de communication avec une des cartes électroniques lors du finallize
	 * @throws ExecuteException 
	 */
	public abstract void execute(int versionToExecute, GameState actualState, ArrayList<Hook> hooksToConsider) throws UnableToMoveException, SerialFinallyException, ExecuteException, SerialConnexionException, BlockedActuatorException;

	/**
	 * Renvoie le score que peut fournir une version d'un script.
	 * Si l'exécution du script ne rapporterai aucun point étant donné le gamestate fourni, renvois 0.
	 *
	 * @param version version dont on veut le score potentiel
	 * @param state l'état du jeu ou l'on veut évaluer le nombre de point que rapporterait l'execution de la version fournie de ce script.
	 * @return le score demandé
	 */
	public abstract int remainingScoreOfVersion(int version, final GameState state);

	/**
	 * Retourne la position d'entrée associée à la version.
	 *
	 * @param version version dont on veut le point d'entrée
	 * @param ray : rayon du robot
	 * @param robotPosition la position du robot actuelle
	 * @return la position du point d'entrée
	 */
	public abstract Circle entryPosition(int version, int ray, Vec2 robotPosition) throws BadVersionException;
	
	/**
	 * Méthode appelée à la fin du script si une exception a lieu.
	 * Le repli des actionneurs est impératif à demander au sein de cette méthode : si un bras reste déployé en cours de match, il risque de se faire arracher !  
	 * Ainsi, les exceptions lancées par cette méthode sont les plus critiques que l'on puisse imaginer : elles préviennent qu'on peut casser la méca si on ne réagit pas bien !
	 * @param state : état du jeu au sein duquel il faut finaliser le script
	 * @param e : l'exception qui a déclenché le finalize 
	 * @throws UnableToMoveException losrque le robot veut se déplacer et que quelque chose sur le chemin cloche et que le robot ne peut s'en défaire simplement: bloquage mécanique immobilisant le robot ou obstacle percu par les capteurs
	 * @throws SerialFinallyException 
	 * @throws SerialConnexionException s'il y a un problème de communication avec une des cartes électroniques
	 */
	public abstract void finalize(GameState state, Exception e) throws UnableToMoveException, SerialFinallyException;

	/**
	 * Fonction d'attente de débloquage du chemin, pour eviter d'abandonner un script inutilement
	 * @param posRobot la position du robot
	 * @return true si chemin dégagé, false sinon
     */
	public boolean waitForEnnemy(GameState actualState, Vec2 posRobot, boolean forward)
	{
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long time = System.currentTimeMillis();
		int signe = forward ? 1 : -1;
		Vec2 aim = posRobot.plusNewVector(new Vec2((int)(signe*300*Math.cos(actualState.robot.getOrientation())),(int)(signe*300*Math.sin(actualState.robot.getOrientation()))));
		while(actualState.table.getObstacleManager().isDiscObstructed(aim, 100))
		{
			if(System.currentTimeMillis() - time > 4000)
				return false;
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return true;
	}
	
	/* (non-Javadoc)
	 * @see container.Service#updateConfig()
	 */
	public void updateConfig()
	{
	}

	abstract public Integer[] getVersion(GameState stateToConsider);

}
