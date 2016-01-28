package tests;

import enums.ActuatorOrder;
import enums.ScriptNames;
import enums.ServiceNames;
import enums.Speed;
import exceptions.ExecuteException;
import exceptions.PathNotFoundException;
import exceptions.PointInObstacleException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialFinallyException;
import hook.Hook;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import robot.Robot;
import scripts.ScriptManager;
import scripts.TechTheSand;
import smartMath.Vec2;
import strategie.GameState;
import table.Table;
import table.obstacles.ObstacleRectangular;

import java.util.ArrayList;

//TODO Version du test temporaire jusqu'à meilleure connaissance des exceptions, et du fonctionnement général des JUnit

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
        theRobot.robot.setLocomotionSpeed(Speed.SLOW_ALL);
        theRobot.robot.moveLengthwise(100, emptyHook, false);
        theRobot.robot.setRobotRadius(TechTheSand.expandedRobotRadius);

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
            // TODO
            e.printStackTrace();
        }
    }

    @Test
    public void fishThem() throws Exception
    {
        try
        {
            ArrayList<ObstacleRectangular> mRectangles = theRobot.table.getObstacleManager().getRectangles();

            // et on supprime le tas de sable
            for (int i=0;i< mRectangles.size();i++)
            {
                if (mRectangles.get(i).isInObstacle(new Vec2(580,1100)));
                {
                    mRectangles.remove(i);
                }
            }

            scriptManager.getScript(ScriptNames.SHELL_GETTER).goToThenExec(0,theRobot, emptyHook);
            scriptManager.getScript(ScriptNames.SHELL_GETTER).goToThenExec(1,theRobot, emptyHook);
            //scriptManager.getScript(ScriptNames.SHELL_GETTER).goToThenExec(2,theRobot, emptyHook);
            //scriptManager.getScript(ScriptNames.SHELL_GETTER).goToThenExec(3,theRobot, emptyHook);
            //scriptManager.getScript(ScriptNames.SHELL_GETTER).goToThenExec(4,theRobot, emptyHook);
            scriptManager.getScript(ScriptNames.SHELL_DEPOSIT).goToThenExec(0,theRobot, emptyHook);
        }
        catch(ExecuteException | SerialFinallyException e)
        {
            e.printStackTrace();
        }
    }
}