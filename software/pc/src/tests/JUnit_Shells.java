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
import org.junit.Before;
import org.junit.Test;
import robot.Robot;
import scripts.ScriptManager;
import scripts.ShellGetter;
import scripts.TechTheSand;
import smartMath.Vec2;
import strategie.GameState;
import table.Table;
import table.obstacles.ObstacleCircular;
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
        theRobot.robot.moveLengthwise(200, emptyHook, false);
        theRobot.changeRobotRadius(TechTheSand.expandedRobotRadius);

        // Lance le thread graphique
        container.getService(ServiceNames.THREAD_INTERFACE);
        container.startInstanciedThreads();
    }

    //@After
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
                if(mRectangles.get(i).isInObstacle(new Vec2(700,1100)))
                {
                    theRobot.table.getObstacleManager().removeObstacle(mRectangles.get(i));
                }
            }

            scriptManager.getScript(ScriptNames.SHELL_GETTER).goToThenExec(0,theRobot, emptyHook);

            Vec2 selected = ((ShellGetter)scriptManager.getScript(ScriptNames.SHELL_GETTER)).entryPosition(1, theRobot.robot.getRobotRadius(), theRobot.robot.getPosition()).position;
            ArrayList<ObstacleCircular> cir = theRobot.table.getObstacleManager().getFixedObstacles();

            for(ObstacleCircular i : cir)
            {
                if(i.isInObstacle(selected))
                {
                    theRobot.table.getObstacleManager().removeObstacle(i);
                }
            }

            scriptManager.getScript(ScriptNames.SHELL_GETTER).goToThenExec(1,theRobot, emptyHook);

            selected = ((ShellGetter)scriptManager.getScript(ScriptNames.SHELL_GETTER)).entryPosition(2, theRobot.robot.getRobotRadius(), theRobot.robot.getPosition()).position;
            cir = theRobot.table.getObstacleManager().getFixedObstacles();

            for(ObstacleCircular i : cir)
            {
                if(i.isInObstacle(selected))
                {
                    theRobot.table.getObstacleManager().removeObstacle(i);
                }
            }

            scriptManager.getScript(ScriptNames.SHELL_GETTER).goToThenExec(2,theRobot, emptyHook);

            selected = ((ShellGetter)scriptManager.getScript(ScriptNames.SHELL_GETTER)).entryPosition(3, theRobot.robot.getRobotRadius(), theRobot.robot.getPosition()).position;
            cir = theRobot.table.getObstacleManager().getFixedObstacles();


            for(ObstacleCircular i : cir)
            {
                if(i.isInObstacle(selected))
                {
                    theRobot.table.getObstacleManager().removeObstacle(i);
                }
            }

            scriptManager.getScript(ScriptNames.SHELL_DEPOSIT).goToThenExec(0,theRobot, emptyHook);

            scriptManager.getScript(ScriptNames.SHELL_GETTER).goToThenExec(3,theRobot, emptyHook);

            selected = ((ShellGetter)scriptManager.getScript(ScriptNames.SHELL_GETTER)).entryPosition(4, theRobot.robot.getRobotRadius(), theRobot.robot.getPosition()).position;
            cir = theRobot.table.getObstacleManager().getFixedObstacles();


            for(ObstacleCircular i : cir)
            {
                if(i.isInObstacle(selected))
                {
                    theRobot.table.getObstacleManager().removeObstacle(i);
                }
            }

            scriptManager.getScript(ScriptNames.SHELL_GETTER).goToThenExec(4,theRobot, emptyHook);

            scriptManager.getScript(ScriptNames.SHELL_DEPOSIT).goToThenExec(0,theRobot, emptyHook);
        }
        catch(ExecuteException | SerialFinallyException e)
        {
            e.printStackTrace();
        }
    }
}