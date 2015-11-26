package tests;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import enums.ActuatorOrder;
import enums.ScriptNames;
import enums.ServiceNames;
import exceptions.ExecuteException;
import exceptions.PathNotFoundException;
import exceptions.PointInObstacleException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialFinallyException;
import hook.Hook;
import robot.Robot;
import scripts.ScriptManager;
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
	}
	
	@After
	public void aftermath() throws Exception 
	{
		//TODO à faire après avoir réglé la question de la communication avec le bas niveau, cf TechTheSand.java
	}
	
	@Test
	public void TechIt() throws UnableToMoveException
	{
		try
		{
			log.debug("Début de forage");
			scriptManager.getScript(ScriptNames.TECH_THE_SAND).execute(0, theRobot, emptyHook);
			scriptManager.getScript(ScriptNames.DROP_THE_SAND).execute(0, theRobot, emptyHook);
		}
		catch(ExecuteException | SerialFinallyException e)
		{
			e.printStackTrace();
		}
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

}
