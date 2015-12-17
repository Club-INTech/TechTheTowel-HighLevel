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

    }

    @Test
    public void go() throws SerialConnexionException {
        mRobot.robot.useActuator(ActuatorOrder.FISHING_POSITION_LEFT, true);
        mRobot.robot.useActuator(ActuatorOrder.FISHING_POSITION_RIGHT, true);
        mRobot.robot.useActuator(ActuatorOrder.MIDDLE_POSITION_LEFT, true);
        mRobot.robot.useActuator(ActuatorOrder.MIDDLE_POSITION_RIGHT, true);
        mRobot.robot.useActuator(ActuatorOrder.FREE_FISHES_LEFT, true);
        mRobot.robot.useActuator(ActuatorOrder.FREE_FISHES_RIGHT, true);

    }
}