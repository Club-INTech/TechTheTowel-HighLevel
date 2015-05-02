package scripts;

import java.util.ArrayList;
import java.util.EnumSet;

import enums.ActuatorOrder;
import enums.ObstacleGroups;
import exceptions.InObstacleException;
import exceptions.PathNotFoundException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialConnexionException;
import exceptions.serial.SerialFinallyException;
import hook.Hook;
import hook.types.HookFactory;
import robot.Robot;
import smartMath.Circle;
import strategie.GameState;
import utils.Config;
import utils.Log;

/**
 * 
 * @author Theo
 * Script pour recuperer la balle de tennis
 */
public class TakeTennisBall extends AbstractScript
{
		
	private static final int AverageTimeToGetPlot = 5000;

	public TakeTennisBall(HookFactory hookFactory, Config config, Log log)
	{
		super(hookFactory, config, log);
		versions = new Integer[]{1}; //Une seule version disponible car une seule balle, et une seule entrée
	}
	
	@Override
	public void goToThenExec(int versionToExecute,GameState<Robot> actualState, boolean shouldRetryIfBlocked, ArrayList<Hook> hooksToConsider) throws UnableToMoveException, SerialConnexionException, PathNotFoundException, SerialFinallyException, InObstacleException
	{
		EnumSet<ObstacleGroups> obstacleNotConsidered = EnumSet.noneOf(ObstacleGroups.class);
		if (versionToExecute == 1)
		{
			obstacleNotConsidered.add(ObstacleGroups.ENNEMY_ZONE);
		}
		else 
		{
			log.debug("version de Script inconnue de TakeTennisBall :"+versionToExecute, this);
			return;
		}
			
		// va jusqu'au point d'entrée de la version demandée
		actualState.robot.moveToCircle(entryPosition(versionToExecute,actualState.robot.robotRay), hooksToConsider, actualState.table,obstacleNotConsidered);
		
		// exécute la version demandée
		execute(versionToExecute, actualState, hooksToConsider, shouldRetryIfBlocked);
	}
	
	@Override
	public void execute(int versionToExecute, GameState<Robot> stateToConsider,ArrayList<Hook> hooksToConsider,boolean shouldRetryIfBlocke) throws UnableToMoveException, SerialConnexionException, SerialFinallyException
	{
		try
		{
			stateToConsider.robot.turn(Math.PI, hooksToConsider, false);//on se tourne bien
	
			//On initialise l'ascenceur
			stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_CLOSE_JAW, true);
			stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_LOW, true);
			
			//On ferme tous les bras, si ce n'est deja fait (bras vers l'exterieur, vers les gobelets)
			stateToConsider.robot.useActuator(ActuatorOrder.ARM_LEFT_CLOSE, false);
			stateToConsider.robot.useActuator(ActuatorOrder.ARM_RIGHT_CLOSE, false);
	
			//On avance vers la balle
			stateToConsider.robot.moveLengthwise(400,hooksToConsider);
			System.out.println("On avance vers la balle");
	
			stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_GROUND, true);
			stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_OPEN_JAW, true);
	
			//On la recupere
			stateToConsider.robot.useActuator(ActuatorOrder.ARM_LEFT_MIDDLE, true);
			stateToConsider.robot.useActuator(ActuatorOrder.ARM_RIGHT_OPEN_SLOW, true);
			stateToConsider.robot.useActuator(ActuatorOrder.ARM_RIGHT_MIDDLE, true);
			stateToConsider.robot.useActuator(ActuatorOrder.ARM_LEFT_OPEN_SLOW, true);
			stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_CLOSE_JAW, true);
	
			stateToConsider.robot.useActuator(ActuatorOrder.ARM_RIGHT_CLOSE, true);
			stateToConsider.robot.useActuator(ActuatorOrder.ARM_LEFT_CLOSE, true);
	
			//On refereme et on remonte le tout
	
			System.out.println("Balle prise");
			
			stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_HIGH, false);
			stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_LOW, true);
			// On recule pour retourner dans le PDD
			stateToConsider.robot.moveLengthwise(-400,hooksToConsider);
			
		}
		catch(UnableToMoveException | SerialConnexionException e)
		{
			finalise(stateToConsider);
			throw e;
		}
	}
	
	@Override
	public Circle entryPosition(int id, int ray) 
	{
		if(id==1)
				return new Circle(-830,1000,0); //1500-400-270
		// 1500 : bord
		// 400 : le moveLenghtWise
		// 270 : pour etre loin du bord et avoir la place avec les bras
		else
		{
			System.out.println("Probleme de version");
			return new Circle(-830,1000,0);
		}
	}
	
	@Override
	public int remainingScoreOfVersion(int version, GameState<?> stateToConsider) 
	{
		//si on a pas deja pris la balle et que rien n evas empecher la recuperation de cette balle TODO c'est la bonne zone que l'on regarde ?
		if (!stateToConsider.table.isBallTaken() && stateToConsider.table.getPileValue(0)==0 && !stateToConsider.table.isAreaXFilled(0))
		{
			//on revoie le nombre de points possible a fairre avec cette balle
			return (5*Math.min((int)(90000-stateToConsider.timeEllapsed)/AverageTimeToGetPlot,
									stateToConsider.table.numberOfPlotLeft()));
		}
		return 0;
	}
	
	@Override
	protected void finalise(GameState<?> stateToConsider) throws SerialFinallyException 
	{
		try 
		{
			stateToConsider.robot.useActuator(ActuatorOrder.ARM_LEFT_CLOSE, true);
			stateToConsider.robot.useActuator(ActuatorOrder.ARM_RIGHT_CLOSE, true);
			stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_CLOSE_JAW, true);
			stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_LOW, true);
		} 
		catch (SerialConnexionException e) 
		{
			throw new SerialFinallyException ();
		}
	}

	public Integer[] getVersion(GameState<?> stateToConsider)
	{
		if (stateToConsider.table.isBallTaken())
			return new Integer[]{};
		return versions;
	}
	
}
