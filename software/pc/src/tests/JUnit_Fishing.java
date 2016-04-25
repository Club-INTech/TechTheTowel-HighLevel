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
import smartMath.Arc;
import smartMath.Circle;
import smartMath.Vec2;
import strategie.GameState;
import table.Table;
import threads.ThreadManager;

import java.util.ArrayList;

/**
 * teste la récupération des poissons des versions 0,1,2,3 et 4
 * @author CF
 *
 */
public class JUnit_Fishing extends JUnit_Test
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
		theRobot.robot.setLocomotionSpeed(Speed.FAST_ALL);
		theRobot.robot.moveLengthwise(232);
		theRobot.table.deleteAllTheShells();
        Vec2 sup1 = new Vec2(1255,725);
		Vec2 sup2 = new Vec2(1325,510);
		Vec2 sup3 = new Vec2(1380,136);
		theRobot.table.getObstacleManager().freePoint(sup1);
		theRobot.table.getObstacleManager().freePoint(sup2);
		theRobot.table.getObstacleManager().freePoint(sup3);
		// Lance le thread graphique
		//container.getService(ServiceNames.THREAD_INTERFACE);
		//container.startInstanciedThreads();

	}

	/*
	@Test
	public void fishThem() throws Exception
	{
		try
		{
//			theRobot.robot.moveLengthwise(600, emptyHook);
//			theRobot.robot.moveLengthwise(-300, emptyHook);
//			scriptManager.getScript(ScriptNames.CLOSE_DOORS).goToThenExec(0, theRobot, emptyHook);
			log.debug("Début de pêche !");

			scriptManager.getScript(ScriptNames.FISHING).goToThenExec(2, theRobot, emptyHook);
			theRobot.robot.turn(3*Math.PI/4);
			theRobot.robot.moveLengthwise(200);
		}
		catch(ExecuteException | SerialFinallyException e)
		{
			e.printStackTrace();
		}
		
	}
	*/

	@Test
	public void fishThemWithHook()
	{
        try 
        {
			theRobot.table.getObstacleManager().freePoint(scriptManager.getScript(ScriptNames.FISHING).entryPosition(3, theRobot.robot.getRobotRadius(), theRobot.robot.getPosition()).position);
            scriptManager.getScript(ScriptNames.FISHING).goToThenExec(3, theRobot, emptyHook);
        } catch (Exception e) 
        {
            e.printStackTrace();
        }
    }

	@After
	public void after()
	{
		try
		{
			theRobot.robot.moveToCircle(new Circle(new Vec2(1200,1000)), emptyHook, theRobot.table);
		}
		catch(Exception e)
		{
			log.debug("Haha, fat chance !");
		}
		theRobot.robot.immobilise();
	}
}