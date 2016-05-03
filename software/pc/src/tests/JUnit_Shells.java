package tests;

import enums.*;
import exceptions.BlockedActuatorException;
import exceptions.serial.SerialConnexionException;
import hook.Hook;
import org.junit.Before;
import org.junit.Test;
import robot.Robot;
import scripts.ScriptManager;
import scripts.TechTheSand;
import smartMath.Vec2;
import strategie.GameState;
import table.Table;

import java.util.ArrayList;

/**
 * teste la récupération des coquillages
 * @author Discord
 *
 */
public class JUnit_Shells extends JUnit_Test
{
    private GameState<Robot> theRobot;
    private ScriptManager scriptManager;
    private ArrayList<Hook> emptyHook = new ArrayList<Hook>();

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception
    {
        super.setUp();
        scriptManager = (ScriptManager)container.getService(ServiceNames.SCRIPT_MANAGER);
        theRobot = (GameState<Robot>)container.getService(ServiceNames.GAME_STATE);
        theRobot.robot.useActuator(ActuatorOrder.ARM_INIT, true);
        theRobot.robot.setOrientation(Math.PI);
        theRobot.robot.setPosition(Table.entryPosition);
        theRobot.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);
        try
        {
            if(!theRobot.robot.getContactSensorValue(ContactSensors.DOOR_CLOSED))
            {
                theRobot.robot.useActuator(ActuatorOrder.CLOSE_DOOR, true);
            }
            if(!theRobot.robot.getContactSensorValue(ContactSensors.DOOR_CLOSED))
            {
                theRobot.robot.useActuator(ActuatorOrder.STOP_DOOR, true);
                throw new BlockedActuatorException("Porte droite bloquée !");
            }
        }
        catch (SerialConnexionException e)
        {
            e.printStackTrace();
        }
        theRobot.robot.moveLengthwise(200, emptyHook, false);
        theRobot.changeRobotRadius(TechTheSand.expandedRobotRadius);
        theRobot.robot.useActuator(ActuatorOrder.OPEN_DOOR, true);

        // Lance le thread graphique
        //container.getService(ServiceNames.THREAD_INTERFACE);
        //container.startInstanciedThreads();
    }

    //@After
    public void aftermath() throws Exception
    {
        //on remonte les bras
        theRobot.robot.useActuator(ActuatorOrder.ARM_INIT,true);
//        try
//        {
//            returnToEntryPosition(theRobot);
//        }
//        catch (UnableToMoveException | PathNotFoundException | PointInObstacleException e)
//        {
//            e.printStackTrace();
//        }
    }

    @Test
    public void fishThem() throws Exception
    {
        try
        {
            theRobot.table.getObstacleManager().freePoint(new Vec2(700,1100));

            scriptManager.getScript(ScriptNames.SHELL_GETTER).goToThenExec(-1,theRobot, emptyHook);

            Vec2 selected = scriptManager.getScript(ScriptNames.SHELL_GETTER).entryPosition(1, theRobot.robot.getRobotRadius(), theRobot.robot.getPosition()).position;
            theRobot.table.getObstacleManager().freePoint(selected);

            scriptManager.getScript(ScriptNames.SHELL_GETTER).goToThenExec(1,theRobot, emptyHook);

            selected = scriptManager.getScript(ScriptNames.SHELL_GETTER).entryPosition(2, theRobot.robot.getRobotRadius(), theRobot.robot.getPosition()).position;
            theRobot.table.getObstacleManager().freePoint(selected);

            scriptManager.getScript(ScriptNames.SHELL_GETTER).goToThenExec(2,theRobot, emptyHook);

            selected = scriptManager.getScript(ScriptNames.SHELL_GETTER).entryPosition(3, theRobot.robot.getRobotRadius(), theRobot.robot.getPosition()).position;
            theRobot.table.getObstacleManager().freePoint(selected);

            scriptManager.getScript(ScriptNames.SHELL_DEPOSIT).goToThenExec(0,theRobot, emptyHook);

            scriptManager.getScript(ScriptNames.SHELL_GETTER).goToThenExec(3,theRobot, emptyHook);

            selected = scriptManager.getScript(ScriptNames.SHELL_GETTER).entryPosition(4, theRobot.robot.getRobotRadius(), theRobot.robot.getPosition()).position;
            theRobot.table.getObstacleManager().freePoint(selected);

            scriptManager.getScript(ScriptNames.SHELL_GETTER).goToThenExec(4,theRobot, emptyHook);

            scriptManager.getScript(ScriptNames.SHELL_DEPOSIT).goToThenExec(0,theRobot, emptyHook);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}