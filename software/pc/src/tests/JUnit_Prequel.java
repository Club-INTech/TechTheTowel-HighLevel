package tests;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import enums.ScriptNames;
import enums.ServiceNames;
import exceptions.ExecuteException;
import exceptions.serial.SerialFinallyException;
import hook.Hook;
import robot.Robot;
import scripts.ScriptManager;
import smartMath.Vec2;
import strategie.GameState;

/**
 * Test l'initialisation de la position du robot avant le match ou les JUnit
 * @author CF
 */

public class JUnit_Prequel extends JUnit_Test
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
		
		// Initialisation arbitraire du robot
		theRobot.robot.setPosition(new Vec2(0,0));
		
		theRobot.robot.setOrientation(- Math.PI/2);
	}
	
	@After
	public void aftermath() throws Exception 
	{
		//on stop le robot
		theRobot.robot.immobilise();
	}
	
	@Test
	public void loadIt() throws Exception
	{
		try
		{
			log.debug("Début de prise de position");
			scriptManager.getScript(ScriptNames.PREQUEL).execute(0, theRobot, emptyHook);;
		}
		catch(ExecuteException | SerialFinallyException e)
		{
			e.printStackTrace();
		}
	}
}
