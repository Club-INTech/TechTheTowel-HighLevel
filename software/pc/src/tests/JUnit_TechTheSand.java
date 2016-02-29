package tests;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import enums.ScriptNames;
import enums.ServiceNames;
import enums.Speed;
import exceptions.ExecuteException;
import exceptions.PathNotFoundException;
import exceptions.PointInObstacleException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialFinallyException;
import hook.Hook;
import robot.Robot;
import scripts.ScriptManager;
import smartMath.Vec2;
import strategie.GameState;
import table.Table;

/**
 * test des versions 0 et 1 de la prise du tas de sable central, ainsi que de son rapatriement
 * @author CF
 */

public class JUnit_TechTheSand extends JUnit_Test
{
	private GameState<Robot> theRobot;
	private ArrayList<Hook> emptyHook = new ArrayList<Hook>();
	private ScriptManager scriptManager;
	
	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		scriptManager = (ScriptManager)container.getService(ServiceNames.SCRIPT_MANAGER);
		theRobot = (GameState<Robot>)container.getService(ServiceNames.GAME_STATE);
		theRobot.robot.setPosition(Table.entryPosition);
		theRobot.robot.setOrientation(Math.PI);
		theRobot.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);
		//supression de l'obstacle d'arrivé, pour tests
		Vec2 sup = scriptManager.getScript(ScriptNames.TECH_THE_SAND).entryPosition(0, theRobot.robot.getRobotRadius(), theRobot.robot.getPosition()).position;
		theRobot.table.getObstacleManager().freePoint(sup);
		theRobot.table.getObstacleManager().freePoint(new Vec2(900,1150)); // test
		theRobot.robot.moveLengthwise(200);
		container.getService(ServiceNames.THREAD_INTERFACE);
		container.startInstanciedThreads();
	}
	
	
	@Test
	public void TechIt() throws Exception
	{
		try
		{
			log.debug("Début de forage");
			scriptManager.getScript(ScriptNames.TECH_THE_SAND).goToThenExec(0, theRobot, emptyHook);
			scriptManager.getScript(ScriptNames.DROP_THE_SAND).goToThenExec(0, theRobot, emptyHook);
		}
		catch(ExecuteException | SerialFinallyException e)
		{
			e.printStackTrace();
		}
	}
	
	@After
	public void aftermath() throws Exception 
	{
		theRobot.robot.immobilise();
	}

}
