package tests;

import org.junit.runner.JUnitCore;
import org.junit.Before;
import org.junit.Test;

import robot.Robot;
import robot.RobotReal;
import strategie.GameState;
import table.Table;
import enums.ServiceNames;
import exceptions.serial.SerialConnexionException;
import graphics.Window;

public class JUnit_BorneArcade extends JUnit_Test 
{
	GameState<Robot> real_state;
	Window win;
	
	public static void main(String[] args) throws Exception
	{
	   JUnitCore.main("tests.JUnit_BorneArcade");
	}
	
	@Before
	public void setUp() throws Exception
	{
		super.setUp();

		real_state = (GameState<Robot>) container.getService(ServiceNames.GAME_STATE);
        
		win = new Window((Table)container.getService(ServiceNames.TABLE), (RobotReal)real_state.robot);
        
		//real_state.robot.updateConfig();
	}

	@Test
	public void start()
	{
		while(true);
		
	}
}
