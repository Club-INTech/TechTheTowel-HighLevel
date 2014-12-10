package scripts;

import enums.ActuatorOrder;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialConnexionException;
import hook.types.HookFactory;
import robot.Robot;
import smartMath.Vec2;
import strategie.GameState;
import utils.Config;
import utils.Log;
/**
 * 
 * @author paul
 * Script pour deposer les tapis sur l'escalier
 */
public class DropCarpet extends AbstractScript 
{
	//TODO spécifier SYSTEMETIQUEMENT les unitées
	
	/** distance de déplacement pour placer les tapis */ //TODO: redondance avec le point d'entrée du script. Il faut fusionner.
	private int distance=200;	// TODO: nom de variable peu explicite
	
	/** le temps d'attente (en ms) entre la commande de dépose du tapis ( le bras se baisse) et la commande qui remonte le bras */
	private int sleepTime = 800; 

	/**
	 * Constructeur (normalement appelé uniquement par le scriptManager) du script déposant les tapis
	 * Le container se charge de renseigner la hookFactory, le système de config et de log.
	 * @param hookFactory La factory a utiliser pour générer les hooks dont pourra avoir besoin le script
	 * @param config le fichier de config a partir duquel le script pourra se configurer
	 * @param log le système de log qu'utilisera le script
	 */
	public DropCarpet(HookFactory hookFactory, Config config, Log log)
	{
		super(hookFactory, config, log);
		
		versions = new int[]{1}; // TODO: il faut penser a le définir (tu l'avais oublié), puis a le garder a jour
	}

	@Override
	public void execute (int versionToExecute, GameState<Robot> stateToConsider, boolean shouldRetryIfBlocke) 
	{
		try 
		{
			
			//on presente ses arrieres a l'escalier
			stateToConsider.robot.turn(Math.PI);
			// on avance vers ces demoiselles (les marches) 
			stateToConsider.robot.moveLengthwiseTowardWall(-distance);
			
			if (!stateToConsider.table.getIsLeftCarpetDropped())
			{
				stateToConsider.robot.useActuator(ActuatorOrder.LEFT_CARPET_DROP, true);
				stateToConsider.table.setIsLeftCarpetDropped(true);
				stateToConsider.robot.useActuator(ActuatorOrder.LEFT_CARPET_FOLDUP, false);
			}
			if (!stateToConsider.table.getIsRightCarpetDropped())
			{
				stateToConsider.robot.useActuator(ActuatorOrder.RIGHT_CARPET_DROP, true);
				stateToConsider.table.setIsRightCarpetDropped(true);
				stateToConsider.robot.useActuator(ActuatorOrder.LEFT_CARPET_FOLDUP, true);
			}
			//on s'eloigne de l'escalier
			stateToConsider.robot.moveLengthwise(distance);
		
		} 
		catch (UnableToMoveException e) 
		{
			log.debug("erreur DropCarpet Script : impossible de bouger", this);	//TODO: remonte cette exeption
		} 
		catch (SerialConnexionException e) 
		{
			log.debug("mauvaise entree serie !",this);//TODO: remonte cette exeption
			e.printStackTrace();
			
		}
	}
	
	@Override
	public Vec2 entryPosition(int id) 
	{
		// le point d'entrée (261,1210) pour les verts, on change comment de couleur si on est jaune ?
		// TODO: réponse (efface quand tu a lu) La table est symétrisée quand on est jaune. Il n'y a rien a faire, les coordonnées restent les mêmes. C'est la magie du bas niveau. (bas niveau java hein)
		return new Vec2(261,1310-distance);
	}

	@Override
	public int remainingScoreOfVersion(int version, GameState<?> stateToConsider) 
	{
		int score = 24;
		if(stateToConsider.table.getIsLeftCarpetDropped())
			score -= 12;
		if(stateToConsider.table.getIsRightCarpetDropped())
			score -= 12;
		
		return score;
	}

	@Override
	protected void finalise(GameState<?> stateToConsider) 
	{
		try 
		{
			stateToConsider.robot.useActuator(ActuatorOrder.LEFT_CARPET_FOLDUP, false);
			stateToConsider.robot.useActuator(ActuatorOrder.RIGHT_CARPET_FOLDUP, true);
		} 
		catch (SerialConnexionException e) 
		{
			log.debug("erreur termine DropCarpet script : impossible de ranger", this); // Remonte cette erreur (celle ci est particulièrement critique !, il faut interrompre le match !)
		}
	}

	
	
	//TODO: pitié, doc moi ces setters/getters, ca prends 20sec
	// D"ailleurs je me demande même si leur existance est utile. 
	
	public int getSleepTime()
	{
		return sleepTime;
	}
	public void setSleepTime(int newSleepTime)
	{
		this.sleepTime = newSleepTime;
	}
	public int getDistance()
	{
		return distance;
	}
	public void setDistance(int newDistance)
	{
		this.distance = newDistance;
	}

}
