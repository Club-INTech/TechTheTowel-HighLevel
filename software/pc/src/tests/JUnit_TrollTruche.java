package tests;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import enums.ActuatorOrder;
import enums.ServiceNames;
import exceptions.serial.SerialConnexionException;
import hook.Hook;
import robot.Robot;
import strategie.GameState;

public class JUnit_TrollTruche extends JUnit_Test
{
	private GameState<Robot> theRobot;
	private ArrayList<Hook> emptyHook = new ArrayList<Hook>();
	
	@SuppressWarnings("unchecked")
	@Before
    public void setUp() throws Exception
    {
        super.setUp();
        theRobot = (GameState<Robot>)container.getService(ServiceNames.GAME_STATE);
        theRobot.robot.useActuator(ActuatorOrder.ARM_INIT, true);
        theRobot.updateConfig();
    }
	
	@Test
    public void go() throws SerialConnexionException 
	{
		theRobot.robot.useActuator(ActuatorOrder.FISHING_POSITION_LOW, true);
		theRobot.robot.sleep(200);
		theRobot.robot.useActuator(ActuatorOrder.MIDDLE_POSITION, true);
		theRobot.robot.useActuator(ActuatorOrder.FISHING_POSITION_LOW, true);
		theRobot.robot.sleep(200);
		theRobot.robot.useActuator(ActuatorOrder.MIDDLE_POSITION, true);
		theRobot.robot.useActuator(ActuatorOrder.FISHING_POSITION_LOW, true);
		theRobot.robot.sleep(200);
		theRobot.robot.useActuator(ActuatorOrder.MIDDLE_POSITION, true);
		theRobot.robot.useActuator(ActuatorOrder.ARM_INIT, true);
    }
}
