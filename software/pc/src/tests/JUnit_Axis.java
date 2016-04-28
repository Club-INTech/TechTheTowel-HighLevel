package tests;

import org.junit.Before;
import org.junit.Test;

import enums.ActuatorOrder;
import enums.ServiceNames;
import exceptions.serial.SerialConnexionException;
import robot.Robot;
import strategie.GameState;

public class JUnit_Axis extends JUnit_Test
{
	private GameState<Robot> game;
	
	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		game = (GameState<Robot>)container.getService(ServiceNames.GAME_STATE);
		game.updateConfig();
	}
	
	@Test
	public void turn() throws SerialConnexionException
	{
		game.robot.useActuator(ActuatorOrder.START_AXIS, false);
		game.robot.useActuator(ActuatorOrder.START_AXIS_LEFT, false);
	}
}
