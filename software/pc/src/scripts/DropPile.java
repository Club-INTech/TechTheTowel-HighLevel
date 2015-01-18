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
 * @author Paul
 *
 *Version 1 on pose la pile sur l'estrade (en (0,0))
 *Version 2 on pose la pile dans notre zone de depart 
 *attention executer le script 1 avant le 2 sinon impossible de recuperer la balle
 */
public class DropPile extends AbstractScript
{
	

	public DropPile(HookFactory hookFactory, Config config, Log log) 
	{
		super(hookFactory, config, log);
		
		
		// TODO: id n'est pas une variable temporaire du constructeur. C'est versions qui est un membre et qu'il faut initialiser ici
		ArrayList<Integer> id = new ArrayList<Integer>();
		id.add(1);
		id.add(2);
	}

	@Override
	public void execute(int version, GameState<Robot> stateToConsider,ArrayList<Hook> hooksToConsider,boolean shouldRetryIfBlocke) throws UnableToMoveException, SerialConnexionException

	{
		if (version==1)
		{
			//TODO: la version actuelle pose la pile devant l'estrade (il faut la modifier pour qu'elle la pose dessus)
			//on se tourne vers la plus belle (l'estrade)
			stateToConsider.robot.turn(Math.PI*-0.5, hooksToConsider, false);
			//on eleve notre membre (l'ascenseur)
			//stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_STAGE, true);
			//on se deplace vers elle
			stateToConsider.robot.moveLengthwise(20, hooksToConsider, true);
			
			//on y place notre membre
			//stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_LOW, true);
			stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_GROUND, true);
			//on ouvre notre coeur (le guide)
			stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_OPEN_JAW, true);
			//stateToConsider.robot.moveLengthwise(50, hooksToConsider, true);
			stateToConsider.robot.useActuator(ActuatorOrder.OPEN_LEFT_GUIDE, true);
			stateToConsider.robot.useActuator(ActuatorOrder.OPEN_RIGHT_GUIDE, true);
			//on se vide (de nos plots)
			stateToConsider.robot.storedPlotCount = 0;
			
			//on sort notre membre
			stateToConsider.robot.moveLengthwise(-200, hooksToConsider, false);
			
			//mais on se referme
			stateToConsider.robot.useActuator(ActuatorOrder.CLOSE_LEFT_GUIDE, false);
			stateToConsider.robot.useActuator(ActuatorOrder.CLOSE_RIGHT_GUIDE, true);
			stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_CLOSE_JAW, true);
			
				
			//on remet notre membre en position de deplacement
			stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_LOW, true);
		}
		else if (version==2)
		{
			// TODO: version 2
		}
		else
		{
			// TODO: version ?
		}
	}
	
	
	@Override
	public Circle entryPosition(int id) 
	{
		if (id==1)
		{
			return new Circle(0,300);
		}
		else if (id==2)
		{
			return new Circle(1300,1000);
		}
		else
		{
			log.debug("erreur DropPile script : out of bound id", this);
			return new Circle(0,1000);
		}
	}

	@Override
	public int remainingScoreOfVersion(int version, GameState<?> stateToConsider)
	{
		if (stateToConsider.robot.isBallStored)
			return 5*(stateToConsider.robot.storedPlotCount);
		else
			return 2*(stateToConsider.robot.storedPlotCount);
	}

	@Override
	protected void finalise(GameState<?> stateToConsider) throws SerialFinallyException, UnableToMoveException 
	{
		try {
			stateToConsider.robot.useActuator(ActuatorOrder.CLOSE_RIGHT_GUIDE, false);
			stateToConsider.robot.useActuator(ActuatorOrder.CLOSE_LEFT_GUIDE, true);	
			stateToConsider.robot.moveLengthwise(-20);
			stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_LOW, true);
		} catch (SerialConnexionException e) {
			throw new SerialFinallyException ();
		}
	}

}

