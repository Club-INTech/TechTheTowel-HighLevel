package scripts;

import java.util.ArrayList;
import java.util.EnumSet;

import smartMath.Circle;
import smartMath.Vec2;
import strategie.GameState;
import robot.Robot;
import utils.Log;
import utils.Config;
import container.Service;
import hook.Hook;
import hook.types.HookFactory;
import enums.ObstacleGroups;
import exceptions.ExecuteException;
import exceptions.InObstacleException;
import exceptions.PathNotFoundException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialConnexionException;
import exceptions.serial.SerialFinallyException;

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
	 * En fournissant un GameState<RobotChrono>, il est possible de chronométrer le temps que l'on metterait a exécuter ce script sans réellement l'exécuter
	 * IMPORTANT : Tout les scripts voulant retirer des obstacles particuliers lors du calcul de chemin doivent reecrire cette methode et retirer ces obstacles dans leur propre calcul du pathfinding
	 * (on suppose qu'on prends en compte tout les obstacles hors plots adverses)
	 *
	 * @param versionToExecute la version du
	 * @param actualState l'état courrant du match.
	 * @param hooksToConsider les hooks a considérer lors des déplacements vers ces scripts
	 * @throws UnableToMoveException losrque le robot veut se déplacer et que quelque chose sur le chemin cloche et que le robot ne peut s'en défaire simplement: bloquage mécanique immobilisant le robot ou obstacle percu par les capteurs
	 * @throws SerialConnexionException s'il y a un problème de communication avec une des cartes électroniques
	 * @throws PathNotFoundException  si le pathfinding ne trouve pas de chemin
	 * @throws SerialFinallyException si le finally n'est pas correctement execute (erreur critique)
	 * @throws InObstacleException lorqsque le robot veut aller dans un obstacle
	 * @throws ExecuteException 
	 */
	public void goToThenExec(int versionToExecute,GameState<Robot> actualState, ArrayList<Hook> hooksToConsider) throws UnableToMoveException, SerialConnexionException, PathNotFoundException, SerialFinallyException, InObstacleException, ExecuteException
	{
		goToThenExec(versionToExecute, actualState, hooksToConsider, EnumSet.noneOf(ObstacleGroups.class));
	}
		
	/**
	 * Va au point d'entrée du script (en utilisant le Pathfinding), puis l'exécute
	 * En fournissant un GameState<RobotChrono>, il est possible de chronométrer le temps que l'on metterait a exécuter ce script sans réellement l'exécuter
	 *
	 * @param versionToExecute la version du
	 * @param actualState l'état courrant du match.
	 * @param hooksToConsider les hooks a considérer lors des déplacements vers ces scripts
	 * @param enumObstacle les obstacles qu'on ne veut pas prendre ne compte dans le pathDingDing
	 * @throws UnableToMoveException losrque le robot veut se déplacer et que quelque chose sur le chemin cloche et que le robot ne peut s'en défaire simplement: bloquage mécanique immobilisant le robot ou obstacle percu par les capteurs
	 * @throws SerialConnexionException s'il y a un problème de communication avec une des cartes électroniques
	 * @throws PathNotFoundException  si le pathfinding ne trouve pas de chemin
	 * @throws SerialFinallyException si le finally n'est pas correctement execute (erreur critique)
	 * @throws InObstacleException lorqsque le robot veut aller dans un obstacle
	 * @throws ExecuteException 
	 */
	public void goToThenExec(int versionToExecute,GameState<Robot> actualState, ArrayList<Hook> hooksToConsider, EnumSet<ObstacleGroups> enumObstacle) throws UnableToMoveException, SerialConnexionException, PathNotFoundException, SerialFinallyException, InObstacleException, ExecuteException
	{
		// va jusqu'au point d'entrée de la version demandée
		try 
		{
			actualState.robot.moveToCircle(entryPosition(versionToExecute,actualState.robot.robotRay, actualState.robot.getPosition()), hooksToConsider, actualState.table, enumObstacle);
		}
		catch (UnableToMoveException | InObstacleException | PathNotFoundException e)
		{
			log.debug("Catch de "+e+" Impossible de goToThenExec : abandon d'exec, throw de "+e, this);
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
	public abstract void execute(int versionToExecute, GameState<Robot> actualState,ArrayList<Hook> hooksToConsider) throws SerialFinallyException, ExecuteException;

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
	 * Retourne la position d'entrée associée à la version.
	 *
	 * @param version version dont on veut le point d'entrée
	 * @param ray 
	 * @param robotPosition la position du robot actuelle
	 * @param la taille du robot
	 * @return la position du point d'entrée
	 */
	public abstract Circle entryPosition(int version, int ray, Vec2 robotPosition);
	
	/**
	 * Méthode toujours appelée à la fin du script via un finally. On des donc certain  que son exécution aura lieu.
	 * Le repli des actionneurs lors de la fin du script a sa place ici et pas ailleurs: si un bras reste déployé en cours de match, il risque de se faire arracher !  
	 * Ainsi, les exceptions lancés par cette méthodes sont les plus critiques que l'on peut imaginer: elles préviennent qu'on peut casser la méca si on ne réagit pas bien !
	 *
	 * @param state Etat du jeu au sein duquel il faut finaliser le script
	 * @throws UnableToMoveException losrque le robot veut se déplacer et que quelque chose sur le chemin cloche et que le robot ne peut s'en défaire simplement: bloquage mécanique immobilisant le robot ou obstacle percu par les capteurs
	 * @throws SerialFinallyException 
	 * @throws SerialConnexionException s'il y a un problème de communication avec une des cartes électroniques
	 */
	public abstract void finalize(GameState<?> state) throws UnableToMoveException, SerialFinallyException;
	
	/* (non-Javadoc)
	 * @see container.Service#updateConfig()
	 */
	public void updateConfig()
	{
	}

	abstract public Integer[] getVersion(GameState<?> stateToConsider);

}
