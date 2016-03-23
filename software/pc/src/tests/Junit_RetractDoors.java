package tests;

import org.junit.Before;
import org.junit.Test;
import enums.ActuatorOrder;
import enums.ContactSensors;
import enums.ServiceNames;
import exceptions.BlockedActuatorException;
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
	public void retractDoors() throws BlockedActuatorException
	{
		try
		{
			mRobot.robot.useActuator(ActuatorOrder.OPEN_DOOR, true);
			
			if(!mRobot.robot.getContactSensorValue(ContactSensors.DOOR_CLOSED))
			{
				mRobot.robot.useActuator(ActuatorOrder.CLOSE_DOOR, true);
			}
			if(!mRobot.robot.getContactSensorValue(ContactSensors.DOOR_CLOSED))
			{
				mRobot.robot.useActuator(ActuatorOrder.STOP_DOOR, true);
				throw new BlockedActuatorException("Porte droite bloquée !");
			}
			
			if(!mRobot.robot.getContactSensorValue(ContactSensors.DOOR_CLOSED_LEFT))
			{
				mRobot.robot.useActuator(ActuatorOrder.CLOSE_DOOR_LEFT, true);
			}
			if(!mRobot.robot.getContactSensorValue(ContactSensors.DOOR_CLOSED_LEFT))
			{
				mRobot.robot.useActuator(ActuatorOrder.STOP_DOOR, true);
				throw new BlockedActuatorException("Porte gauche bloquée !");
			}
		}
		catch (SerialConnexionException e)
		{
			e.printStackTrace();
		}
			
	}

}
