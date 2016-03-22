package tests;

import enums.ActuatorOrder;
import enums.ServiceNames;
import enums.Speed;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.Locomotion.UnexpectedObstacleOnPathException;
import exceptions.serial.SerialConnexionException;
import hook.Hook;
import org.junit.Before;
import org.junit.Test;
import robot.Locomotion;
import robot.Robot;
import robot.cardsWrappers.SensorsCardWrapper;
import smartMath.Arc;
import smartMath.Vec2;
import strategie.GameState;
import table.Table;
import threads.ThreadSensor;

import java.util.ArrayList;

/**
 * JUnit pour la prés. aux collègiens
 */
public class JUnit_Montlhery extends JUnit_Test
{
    /** The capteurs. */
    SensorsCardWrapper capteurs;

    private Locomotion mLocomotion;

    GameState<Robot> state;

    private ThreadSensor sensors;

    /* (non-Javadoc)
     * @see tests.JUnit_Test#setUp()
     */
    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception
    {
        super.setUp();
        state = (GameState<Robot>)container.getService(ServiceNames.GAME_STATE);

        log.debug("JUnit_ActionneursTest.setUp()");
        capteurs = (SensorsCardWrapper)container.getService(ServiceNames.SENSORS_CARD_WRAPPER);

        config.set("capteurs_on", "true");
        capteurs.updateConfig();

        sensors = (ThreadSensor) container.getService(ServiceNames.THREAD_SENSOR);
        //container.getService(ServiceNames.THREAD_TIMER);

        //locomotion
        mLocomotion = (Locomotion)container.getService(ServiceNames.LOCOMOTION);
        mLocomotion.updateConfig();

        //mLocomotion.setPosition(new Vec2 (1500-320-77,1000));
        mLocomotion.setPosition(new Vec2(Table.entryPosition.x, Table.entryPosition.y+350));// milieu de table
        mLocomotion.setOrientation(Math.PI);
        state.robot.setLocomotionSpeed(Speed.SLOW_ALL);
        state.table.getObstacleManager().destroyEverything();
        container.getService(ServiceNames.THREAD_INTERFACE);
        container.startInstanciedThreads();

    }

    //@Test
    public void dansLeTas()
    {
        try {
            state.robot.moveLengthwise(4000);
        } catch (UnableToMoveException e) {
            e.printStackTrace();
        }
        try {
            state.robot.useActuator(ActuatorOrder.FISHING_POSITION, false);
        } catch (SerialConnexionException e) {
            e.printStackTrace();
        }

    }

    //@Test
    public void fishIt()
    {
        sensors.stop();
        state.table.getObstacleManager().destroyEverything();
        try {
            state.robot.useActuator(ActuatorOrder.FISHING_POSITION, true);
            state.robot.setLocomotionSpeed(Speed.SLOW_ALL);
            state.robot.moveLengthwise(400);
            state.robot.useActuator(ActuatorOrder.MIDDLE_POSITION, false);
            state.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);
            state.robot.moveLengthwise(500);
            state.robot.useActuator(ActuatorOrder.MAGNET_DOWN, true);
            state.robot.useActuator(ActuatorOrder.FINGER_DOWN, true);
            state.robot.useActuator(ActuatorOrder.MAGNET_UP, true);
            state.robot.useActuator(ActuatorOrder.FINGER_UP, false);
        } catch (SerialConnexionException | UnableToMoveException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void dansTaMere() throws UnableToMoveException {
        state.robot.setForceMovement(true);
        state.robot.moveLengthwise(-100);
    }

    //@Test
    public void moveArc()
    {
        try {
            state.robot.moveArc(new Arc(state.robot.getPosition(), state.robot.getPosition().plusNewVector(new Vec2(-1500,0)),
                    2*Math.PI/3, true), new ArrayList<Hook>());
        } catch (UnableToMoveException e) {
            e.printStackTrace();
        }
    }

    //@Test
    public void esquive()
    {
        try
        {
            state.robot.moveLengthwise(4000);
        }
        catch (UnableToMoveException e)
        {
            try
            {
                sensors.stop();
                state.table.getObstacleManager().destroyEverything();
                //state.robot.setForceMovement(true);
                state.robot.moveLengthwise(-400);
                state.robot.moveArc(new Arc(state.robot.getPosition(), state.robot.getPosition().plusNewVector(new Vec2(-2000,0)),
                        4*Math.PI/3, false), new ArrayList<Hook>());
                state.robot.turn(Math.PI);
                state.robot.moveLengthwise(500);
            }
            catch (UnableToMoveException e1)
            {
                e1.printStackTrace();
            }

        }
    }

    //@Test
    public void testLive()
    {

    }

    //@Test
    public void test()
    {
        try
        {
            //Fait avancer le robot de 10 cm
            state.robot.moveLengthwise(100);
        }
        catch (UnableToMoveException e)
        {
            e.printStackTrace();
        }
    }
}
