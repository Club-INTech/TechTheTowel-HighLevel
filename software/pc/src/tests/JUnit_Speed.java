package tests;

import hook.Hook;

import java.util.ArrayList;
import java.util.EnumSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import enums.ObstacleGroups;
import enums.ServiceNames;
import enums.Speed;
import exceptions.InObstacleException;
import exceptions.PathNotFoundException;
import exceptions.Locomotion.UnableToMoveException;
import robot.Robot;
import smartMath.Vec2;
import table.Table;

public class JUnit_Speed extends JUnit_Test {

	Robot robot;
	Table table;
	
	@Before
	public void setUp() throws Exception 
	{
		super.setUp();
		
		robot = (Robot)container.getService(ServiceNames.ROBOT_REAL);
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
	public void test() throws UnableToMoveException, PathNotFoundException, InObstacleException 
	{
		robot.moveLengthwise(250);
		
		for (Speed speed : Speed.values())
		{
			robot.setLocomotionSpeed(speed);
			log.debug("PWM rotation : "+speed.PWMRotation+"\nPWM translation : "+speed.PWMTranslation,this);
			
			robot.moveToLocation(new Vec2(-1000,1000), new ArrayList<Hook>(), table, EnumSet.allOf(ObstacleGroups.class));
			robot.moveToLocation(new Vec2(1000, 1000), new ArrayList<Hook>(), table, EnumSet.allOf(ObstacleGroups.class));
		}
		
	}

}
