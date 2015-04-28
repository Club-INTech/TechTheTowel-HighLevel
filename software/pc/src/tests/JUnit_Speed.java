package tests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import enums.ServiceNames;
import enums.Speed;
import exceptions.Locomotion.UnableToMoveException;
import robot.Robot;
import table.Table;

public class JUnit_Speed extends JUnit_Test {

	Robot robot;
	
	@Before
	public void setUp() throws Exception 
	{
		super.setUp();
		
		robot = (Robot)container.getService(ServiceNames.ROBOT_REAL);
		
		robot.setPosition(Table.entryPosition);
		robot.setOrientation(Math.PI);
		
		matchSetUp(robot);
	}

	@After
	public void tearDown() throws Exception 
	{
		super.tearDown();
	}

	@Test
	public void test() throws UnableToMoveException 
	{
		for (Speed speed : Speed.values())
		{
			robot.setLocomotionSpeed(speed);
			log.debug("PWM rotation : "+speed.PWMRotation+"\nPWM translation : "+speed.PWMTranslation,this);
			
			robot.moveLengthwise(1000);
			robot.moveLengthwise(-1000);
		}
		
	}

}
