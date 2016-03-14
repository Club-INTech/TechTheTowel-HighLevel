package tests;

import enums.ActuatorOrder;
import enums.ServiceNames;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialConnexionException;
import hook.Hook;
import javafx.scene.control.Tab;
import org.junit.Before;
import org.junit.Test;
import robot.Locomotion;
import robot.Robot;
import robot.cardsWrappers.SensorsCardWrapper;
import smartMath.Arc;
import smartMath.Vec2;
import strategie.GameState;
import table.Table;

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

        container.getService(ServiceNames.THREAD_SENSOR);
        //container.getService(ServiceNames.THREAD_TIMER);

        //locomotion
        mLocomotion = (Locomotion)container.getService(ServiceNames.LOCOMOTION);
        mLocomotion.updateConfig();

        //mLocomotion.setPosition(new Vec2 (1500-320-77,1000));
        mLocomotion.setPosition(new Vec2(Table.entryPosition.x, Table.entryPosition.y+350));// milieu de table
        mLocomotion.setOrientation(Math.PI);
        container.getService(ServiceNames.THREAD_INTERFACE);
        container.startInstanciedThreads();

    }

    @Test
    public void dansLeTas()
    {
        try {
            state.robot.moveLengthwise(1500);
            state.robot.useActuator(ActuatorOrder.FISHING_POSITION, false);
        } catch (UnableToMoveException | SerialConnexionException e) {
            e.printStackTrace();
        }
    }

    //@Test
    public void moveArc()
    {
        try {
            state.robot.moveArc(new Arc(state.robot.getPosition(), state.robot.getPosition().plusNewVector(new Vec2(0,-500)),
                    state.robot.getPosition().plusNewVector(new Vec2(-500, -250))), new ArrayList<Hook>());
        } catch (UnableToMoveException e) {
            e.printStackTrace();
        }
    }
}
