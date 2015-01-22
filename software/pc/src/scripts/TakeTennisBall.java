package scripts;

import java.util.ArrayList;

import enums.ActuatorOrder;
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
		
	public TakeTennisBall(HookFactory hookFactory, Config config, Log log)
	{
		super(hookFactory, config, log);
		versions = new int[]{1}; //Une seule version disponible car une seule balle, et une seule entrée
	}
	
	@Override
	public void execute(int versionToExecute, GameState<Robot> stateToConsider,ArrayList<Hook> hooksToConsider,boolean shouldRetryIfBlocke) throws UnableToMoveException, SerialConnexionException
	{
		stateToConsider.robot.turn(Math.PI, hooksToConsider, false);//on se tourne bien

		//On initialise l'ascenceur
		stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_CLOSE_JAW, false);
		stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_LOW, true);
		
		//On ferme tous les bras, si ce n'est deja fait (bras vers l'exterieur, vers les gobelets)
		stateToConsider.robot.useActuator(ActuatorOrder.ARM_LEFT_CLOSE, true);
		stateToConsider.robot.useActuator(ActuatorOrder.ARM_RIGHT_CLOSE, true);
		stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_OPEN_JAW, false);

		//On avance vers la balle
		stateToConsider.robot.moveLengthwise(500,hooksToConsider);//TODO
		
		//On la recupere
		stateToConsider.robot.useActuator(ActuatorOrder.ARM_LEFT_CLOSE_SLOW, true);
		stateToConsider.robot.useActuator(ActuatorOrder.ARM_RIGHT_CLOSE_SLOW, true);
		
		//On refereme et on remonte le tout
		stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_CLOSE_JAW, false);
		stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_HIGH, true);
		stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_LOW, true);
	}
	
	@Override
	public Circle entryPosition(int id) 
	{
		if(id==1)
				return new Circle(-881,1000,0); //debut-500
		else
		{
			System.out.println("Probleme de version");
			return new Circle(-881,1000,0);
		}
	}
	
	@Override
	public int remainingScoreOfVersion(int version, GameState<?> stateToConsider) 
	{
		return 0;
	}
	
	@Override
	protected void finalise(GameState<?> stateToConsider) throws SerialFinallyException 
	{
		
	}
	
}
