package tests;

import enums.ActuatorOrder;
import enums.ServiceNames;
import exceptions.serial.SerialConnexionException;
import hook.Hook;
import org.junit.Before;
import org.junit.Test;
import robot.Robot;
import strategie.GameState;

import java.util.ArrayList;

public class JUnit_ActuatorTest extends JUnit_Test
{
    private GameState<Robot> mRobot;
    private ArrayList<Hook> emptyHook = new ArrayList<Hook>();

    @Before
    public void setUp() throws Exception
    {
        super.setUp();
        mRobot = (GameState<Robot>)container.getService(ServiceNames.GAME_STATE);
        mRobot.updateConfig();
        mRobot.robot.useActuator(ActuatorOrder.ARM_INIT, true);

    }

    @Test
    public void go() throws SerialConnexionException {
        mRobot.robot.useActuator(ActuatorOrder.FISHING_POSITION_LOW, true);
        mRobot.robot.useActuator(ActuatorOrder.FISHING_POSITION_RIGHT_LOW, true);
        mRobot.robot.useActuator(ActuatorOrder.MIDDLE_POSITION, true);
        mRobot.robot.useActuator(ActuatorOrder.MIDDLE_POSITION_RIGHT, true);
        
		mRobot.robot.useActuator(ActuatorOrder.MAGNET_DOWN, true);
		mRobot.robot.useActuator(ActuatorOrder.FINGER_DOWN, true);
		mRobot.robot.useActuator(ActuatorOrder.MAGNET_UP, true);
		mRobot.robot.useActuator(ActuatorOrder.FINGER_UP, true);
		
		mRobot.robot.useActuator(ActuatorOrder.RIGHT_MAGNET_DOWN, true);
		mRobot.robot.useActuator(ActuatorOrder.RIGHT_FINGER_DOWN, true);
		mRobot.robot.useActuator(ActuatorOrder.RIGHT_MAGNET_UP, true);
		mRobot.robot.useActuator(ActuatorOrder.RIGHT_FINGER_UP, true);

    }
}