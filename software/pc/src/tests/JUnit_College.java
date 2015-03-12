package tests;

import hook.Hook;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import robot.Robot;
import scripts.ScriptManager;
import strategie.GameState;
import enums.ActuatorOrder;
import enums.ScriptNames;
import enums.ServiceNames;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialConnexionException;

public class JUnit_College extends JUnit_Test {

	
	int distanceBetweenPlots = 300;

	private GameState<Robot> real_state;
	private Robot robot;
	private ScriptManager scriptmanager;
	private ArrayList<Hook> emptyHook;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception 
	{
		super.setUp();
		super.setUp();
		real_state = (GameState<Robot>) container.getService(ServiceNames.GAME_STATE);
		scriptmanager = (ScriptManager) container.getService(ServiceNames.SCRIPT_MANAGER);
		emptyHook = new ArrayList<Hook> ();  
		robot = real_state.robot;
		
		robot.updateConfig();

	}

	@Test
	public void test() throws UnableToMoveException, SerialConnexionException 
	{
		robot.moveLengthwise(distanceBetweenPlots-50);
		scriptmanager.getScript(ScriptNames.DROP_CARPET).execute(0, real_state, emptyHook, true);
		
		robot.useActuator(ActuatorOrder.HIGH_LEFT_CLAP, false);
		robot.useActuator(ActuatorOrder.HIGH_RIGHT_CLAP, true);
		robot.turn(0);
		robot.useActuator(ActuatorOrder.LOW_LEFT_CLAP, false);
		robot.useActuator(ActuatorOrder.LOW_RIGHT_CLAP, true);
		
		robot.moveLengthwise(distanceBetweenPlots-50);
		robot.useActuator(ActuatorOrder.ELEVATOR_OPEN_JAW, true);
		robot.moveLengthwise(50);
		robot.useActuator(ActuatorOrder.ARM_LEFT_OPEN_SLOW, true);
		robot.useActuator(ActuatorOrder.ELEVATOR_CLOSE_JAW, true);
		robot.useActuator(ActuatorOrder.ARM_LEFT_CLOSE, false);
		robot.useActuator(ActuatorOrder.ELEVATOR_HIGH, true);
		robot.useActuator(ActuatorOrder.ELEVATOR_GROUND, false);
		
		robot.moveLengthwise(distanceBetweenPlots-50);
		robot.useActuator(ActuatorOrder.ELEVATOR_OPEN_JAW, true);
		robot.moveLengthwise(50);
		robot.useActuator(ActuatorOrder.ARM_RIGHT_OPEN_SLOW, true);
		robot.useActuator(ActuatorOrder.ELEVATOR_CLOSE_JAW, true);
		robot.useActuator(ActuatorOrder.ARM_RIGHT_CLOSE, false);
		robot.useActuator(ActuatorOrder.ELEVATOR_HIGH, true);
		robot.useActuator(ActuatorOrder.ELEVATOR_GROUND, false);
		
		robot.moveLengthwise(distanceBetweenPlots-50);
		robot.useActuator(ActuatorOrder.ELEVATOR_OPEN_JAW, true);
		robot.moveLengthwise(50);
		robot.useActuator(ActuatorOrder.ARM_LEFT_OPEN_SLOW, true);
		robot.useActuator(ActuatorOrder.ELEVATOR_CLOSE_JAW, true);
		robot.useActuator(ActuatorOrder.ARM_LEFT_CLOSE, false);
		robot.useActuator(ActuatorOrder.ELEVATOR_HIGH, true);
		robot.useActuator(ActuatorOrder.ELEVATOR_GROUND, false);
		
		robot.moveLengthwise(distanceBetweenPlots-50);
		robot.useActuator(ActuatorOrder.ELEVATOR_OPEN_JAW, true);
		robot.moveLengthwise(50);
		robot.useActuator(ActuatorOrder.ARM_RIGHT_OPEN_SLOW, true);
		robot.useActuator(ActuatorOrder.ELEVATOR_CLOSE_JAW, true);
		robot.useActuator(ActuatorOrder.ARM_RIGHT_CLOSE, false);
		robot.useActuator(ActuatorOrder.ELEVATOR_HIGH, true);
		robot.useActuator(ActuatorOrder.ELEVATOR_GROUND, false);
		
		
		robot.useActuator(ActuatorOrder.OPEN_LEFT_GUIDE, false);
		robot.useActuator(ActuatorOrder.OPEN_RIGHT_GUIDE, true);
		robot.useActuator(ActuatorOrder.ELEVATOR_OPEN_JAW, true);
		robot.moveLengthwise(-distanceBetweenPlots);
	}

}
