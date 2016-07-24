package tests;

import enums.ServiceNames;
import enums.Speed;
import exceptions.Locomotion.UnableToMoveException;
import hook.Hook;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import robot.Robot;
import smartMath.Vec2;
import table.Table;

import java.util.ArrayList;

public class JUnit_Speed extends JUnit_Test {

	Robot robot;
	Table table;
	
	@Before
	public void setUp() throws Exception 
	{
		super.setUp();
		
		robot = (Robot)container.getService(ServiceNames.ROBOT);
		table = (Table)container.getService(ServiceNames.TABLE);
		
		robot.setPosition(Table.entryPosition);
		robot.setOrientation(Math.PI);
		
		matchSetUp(robot, false);
	}

	@After
	public void tearDown() throws Exception 
	{
		super.tearDown();
	}

	@Test
	public void test() throws UnableToMoveException
	{
		robot.moveLengthwise(250);
		
		for (Speed speed : Speed.values())
		{
			robot.setLocomotionSpeed(speed);
			log.debug("PWM rotation : "+speed.rotationSpeed+"\nPWM translation : "+speed.translationSpeed);
			
			robot.moveToLocation(new Vec2(-1000,1000), new ArrayList<Hook>(), table);
			robot.moveToLocation(new Vec2(1000, 1000), new ArrayList<Hook>(), table);
		}
		
	}

}
