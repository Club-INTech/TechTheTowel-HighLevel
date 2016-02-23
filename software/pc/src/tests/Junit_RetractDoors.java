package tests;

import org.junit.Before;
import org.junit.Test;
import enums.ActuatorOrder;
import enums.ServiceNames;
import exceptions.serial.SerialConnexionException;
import robot.Robot;
import strategie.GameState;

public class Junit_RetractDoors extends JUnit_Test
{
	private GameState<Robot> mRobot;
	
	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		mRobot = (GameState<Robot>)container.getService(ServiceNames.GAME_STATE);
		mRobot.updateConfig();
	}
	
	@Test
	public void retractDoors()
	{
		try
		{
			mRobot.robot.useActuator(ActuatorOrder.CLOSE_DOOR, true);
		}
		catch (SerialConnexionException e)
		{
			e.printStackTrace();
		}
			
	}

}
