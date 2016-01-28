package tests;

import enums.ActuatorOrder;
import enums.ScriptNames;
import enums.ServiceNames;
import enums.Speed;
import exceptions.ExecuteException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.PathNotFoundException;
import exceptions.PointInObstacleException;
import exceptions.serial.SerialFinallyException;
import hook.Hook;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import robot.Robot;
import scripts.ScriptManager;
import strategie.GameState;
import table.Table;

import java.util.ArrayList;

public class JUnit_ScriptedMatch extends JUnit_Test
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
        theRobot.robot.moveLengthwise(100, emptyHook, false);

        // Lance le thread graphique
        container.getService(ServiceNames.THREAD_INTERFACE);
        container.startInstanciedThreads();
    }

    @After
    public void aftermath() throws Exception
    {
        //on remonte les bras
        theRobot.robot.useActuator(ActuatorOrder.ARM_INIT,true);
        try
        {
            returnToEntryPosition(theRobot);
        }
        catch (UnableToMoveException | PathNotFoundException | PointInObstacleException e)
        {
            e.printStackTrace();
        }
    }

    @Test
    public void match() throws Exception
    {
        try
        {
            scriptManager.getScript(ScriptNames.TECH_THE_SAND).goToThenExec(0,theRobot, emptyHook);
        }
        catch(ExecuteException | SerialFinallyException e)
        {
            e.printStackTrace();
        }
        try
        {
            scriptManager.getScript(ScriptNames.DROP_THE_SAND).goToThenExec(0, theRobot, emptyHook);
        }
        catch(ExecuteException | SerialFinallyException e)
        {
            e.printStackTrace();
        }
        try
        {
            scriptManager.getScript(ScriptNames.CLOSE_DOORS).goToThenExec(0,theRobot, emptyHook);
        }
        catch(ExecuteException | SerialFinallyException e)
        {
            e.printStackTrace();
        }
        try
        {
            scriptManager.getScript(ScriptNames.FISHING).goToThenExec(1, theRobot, emptyHook);
        }
        catch(ExecuteException | SerialFinallyException e)
        {
            e.printStackTrace();
        }
        try
        {
            scriptManager.getScript(ScriptNames.SHELL_GETTER).goToThenExec(0,theRobot, emptyHook);
        }
        catch(ExecuteException | SerialFinallyException e)
        {
            e.printStackTrace();
        }
        try
        {
            scriptManager.getScript(ScriptNames.SHELL_GETTER).goToThenExec(1,theRobot, emptyHook);
        }
        catch(ExecuteException | SerialFinallyException e)
        {
            e.printStackTrace();
        }
        try
        {
            scriptManager.getScript(ScriptNames.SHELL_GETTER).goToThenExec(2,theRobot, emptyHook);
        }
        catch(ExecuteException | SerialFinallyException e)
        {
            e.printStackTrace();
        }
        try
        {
            scriptManager.getScript(ScriptNames.SHELL_GETTER).goToThenExec(3,theRobot, emptyHook);
        }
        catch(ExecuteException | SerialFinallyException e)
        {
            e.printStackTrace();
        }
        try
        {
            scriptManager.getScript(ScriptNames.SHELL_GETTER).goToThenExec(4,theRobot, emptyHook);
        }
        catch(ExecuteException | SerialFinallyException e)
        {
            e.printStackTrace();
        }
        try
        {
            scriptManager.getScript(ScriptNames.SHELL_DEPOSIT).goToThenExec(0,theRobot, emptyHook);
        }
        catch(ExecuteException | SerialFinallyException e)
        {
            e.printStackTrace();
        }

    }
}