package tests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import enums.ServiceNames;
import robot.Robot;
import strategie.GameState;

public class JUnit_Disengage extends JUnit_Test
{
	private GameState<Robot> state;
	
	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		state = (GameState<Robot>)container.getService(ServiceNames.GAME_STATE);
		state.updateConfig();
	}
	
	@Test
	public void test()
	{
		try
		{
			state.robot.moveLengthwise(100);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	@After
	public void after()
	{
		state.robot.immobilise();
	}
}
