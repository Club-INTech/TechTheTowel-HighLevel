package tests;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import robot.Robot;
import scripts.ScriptManager;
import strategie.GameState;
import table.Table;
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
		mRobot = (GameState<Robot>)container.getService(ServiceNames.GAME_STATE);
		//La position de depart est mise dans le updateConfig()
		mRobot.updateConfig();
		mRobot.robot.setPosition(Table.entryPosition);
		mRobot.robot.setOrientation(Math.PI);
		scriptManager = (ScriptManager)container.getService(ServiceNames.SCRIPT_MANAGER);
	}
	
	@Test
	public void closeThatDoors() throws UnableToMoveException
	{
		ArrayList<Hook> emptyList = new ArrayList<Hook>();
		try
		{
			//On execute le script
			log.debug("Script lance");
			scriptManager.getScript(ScriptNames.CLOSE_DOORS).execute(0, mRobot, emptyList);
		}
		catch(ExecuteException | SerialFinallyException e)
		{
			e.printStackTrace();
		}
		
	}
}