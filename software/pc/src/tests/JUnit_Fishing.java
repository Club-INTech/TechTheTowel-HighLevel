package tests;

import enums.ActuatorOrder;
import enums.ScriptNames;
import enums.ServiceNames;
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
import strategie.GameState;
import table.Table;
import java.util.ArrayList;

//TODO Version du test temporaire jusqu'à meilleure connaissance des exceptions, et du fonctionnement général des JUnit 

/**
 * teste la récupération des poissons des versions 0 et 1
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
		theRobot.robot.setPosition(Table.entryPosition);
		theRobot.robot.setOrientation(Math.PI);
	}
	
	@After
	public void aftermath() throws Exception 
	{
		//on remonte les deux bras en même temps
		theRobot.robot.useActuator(ActuatorOrder.ARM_INIT,false);
		theRobot.robot.useActuator(ActuatorOrder.ARM_INIT,true);
	}
	
	@Test
	public void fishThem() throws Exception
	{
		try
		{
			log.debug("Début de pêche");
			scriptManager.getScript(ScriptNames.FISHING).goToThenExec(0, theRobot, emptyHook);
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