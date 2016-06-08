package tests;

import enums.ActuatorOrder;
import enums.Speed;
import org.junit.runner.JUnitCore;
import org.junit.Before;
import org.junit.Test;

import robot.Robot;
import robot.RobotReal;
import smartMath.Vec2;
import strategie.GameState;
import table.Table;
import enums.ServiceNames;
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
		
		container.getService(ServiceNames.THREAD_INTERFACE);
		container.getService(ServiceNames.THREAD_SENSOR);
		container.startInstanciedThreads();
        
		//FIXME : bug pour la position en y :(
		real_state.robot.setPosition(new Vec2(0, 500));
		real_state.robot.setOrientation(-0.5*Math.PI);
		real_state.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);
		
		real_state.robot.updateConfig();
		real_state.robot.useActuator(ActuatorOrder.MONTLHERY, false);
	}

	@Test
	public void start()
	{
		while(true);
	}
}
