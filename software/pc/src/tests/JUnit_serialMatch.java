package tests;

import java.util.ArrayList;
import java.util.Random;

import smartMath.Vec2;
import strategie.GameState;

import org.junit.Before;
import org.junit.Test;

import enums.ServiceNames;
import exceptions.Locomotion.UnableToMoveException;
import robot.Robot;

/**
 * classe des matchs scriptes.
 * sert de bases pour nimporte quel test
 */
public class JUnit_serialMatch extends JUnit_Test 
{

	GameState<Robot> state;
	ArrayList<Vec2> path = new ArrayList<Vec2>();
	Random rand = new Random();
	
		
	
	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception 
	{
		super.setUp();
		state=(GameState<Robot>) container.getService(ServiceNames.GAME_STATE);
	}

	@Test
	public void test()
	{
			try 
			{
				state.robot.moveLengthwise(1000);
				while(true)
				{
					state.robot.moveLengthwise(1000);
					state.robot.sleep(1000);
					state.robot.turn(0);
					state.robot.sleep(1000);
					state.robot.moveLengthwise(1000);
					state.robot.sleep(1000);
					state.robot.turn(Math.PI);
					state.robot.sleep(1000);
				}
			} 
			catch (UnableToMoveException e) 
			{
				e.printStackTrace();
			}
	}
}
