package tests;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import robot.Robot;
import scripts.ScriptManager;
import strategie.GameState;
import enums.ScriptNames;
import enums.ServiceNames;
import exceptions.ExecuteException;
import exceptions.PathNotFoundException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialConnexionException;
import exceptions.serial.SerialFinallyException;
import hook.Hook;

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
		mRobot = (GameState<Robot>)container.getService(ServiceNames.ROBOT_REAL);
		//La position de depart est mise dans le updateConfig()
		mRobot.updateConfig();
		scriptManager = (ScriptManager)container.getService(ServiceNames.SCRIPT_MANAGER);
	}
	
	@Test
	public void closeThatDoors() throws UnableToMoveException
	{
		ArrayList<Hook> emptyList = new ArrayList<Hook>();
		try
		{
			//On execute le script
			scriptManager.getScript(ScriptNames.CLOSE_DOORS).goToThenExec(0, mRobot, emptyList);
		}
		catch(ExecuteException | UnableToMoveException | SerialConnexionException | PathNotFoundException | SerialFinallyException e)
		{
			e.printStackTrace();
		}
		
	}
}