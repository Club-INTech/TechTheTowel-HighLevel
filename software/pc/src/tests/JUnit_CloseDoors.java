package tests;

import enums.ScriptNames;
import enums.ServiceNames;
import enums.Speed;
import exceptions.BadVersionException;
import exceptions.ExecuteException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.PathNotFoundException;
import exceptions.PointInObstacleException;
import exceptions.serial.SerialConnexionException;
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

/**
 * teste la fermeture des portes par la version 0 du script
 * @author julian
 *
 */
public class JUnit_CloseDoors extends JUnit_Test
{
	private GameState<Robot> mRobot;
	private ScriptManager scriptManager;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		log.debug("JUnit_DeplacementsTest.setUp()");
		mRobot = (GameState<Robot>)container.getService(ServiceNames.GAME_STATE);
		//La position de depart est mise dans le updateConfig()
		mRobot.updateConfig();
		mRobot.robot.setPosition(Table.entryPosition);
		mRobot.robot.setOrientation(Math.PI);
		scriptManager = (ScriptManager)container.getService(ServiceNames.SCRIPT_MANAGER);
		mRobot.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);
		mRobot.robot.moveLengthwise(100);
		//container.getService(ServiceNames.THREAD_INTERFACE);
		//container.startInstanciedThreads();
	}
	
	@Test
	public void closeThatDoors() throws UnableToMoveException
	{
		ArrayList<Hook> emptyList = new ArrayList<Hook>();
		try
		{
			//On execute le script
			log.debug("Fermeture des portes.");
			scriptManager.getScript(ScriptNames.CLOSE_DOORS).goToThenExec(0, mRobot, emptyList);
		}
		catch(SerialConnexionException | BadVersionException | ExecuteException | SerialFinallyException e)
		{
			e.printStackTrace();
		} catch (PointInObstacleException e) {
			e.printStackTrace();
		} catch (PathNotFoundException e) {
			e.printStackTrace();
		}

	}

	@After
	public void finish() throws UnableToMoveException, PathNotFoundException, PointInObstacleException 
	{
		mRobot.robot.immobilise();
	}
}