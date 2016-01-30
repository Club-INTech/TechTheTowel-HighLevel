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
import hook.types.HookFactory;
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
		theRobot.robot.useActuator(ActuatorOrder.ARM_INIT, true);
		theRobot.robot.setOrientation(Math.PI);
		theRobot.robot.setPosition(Table.entryPosition);
		theRobot.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);
		theRobot.robot.moveLengthwise(200, emptyHook, false);
		
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
//			theRobot.robot.moveLengthwise(600, emptyHook);
//			theRobot.robot.moveLengthwise(-300, emptyHook);
//			scriptManager.getScript(ScriptNames.CLOSE_DOORS).goToThenExec(0, theRobot, emptyHook);
			log.debug("Début de pêche !");

			scriptManager.getScript(ScriptNames.FISHING).goToThenExec(0, theRobot, emptyHook);
			theRobot.robot.turn(3*Math.PI/4);
			theRobot.robot.moveLengthwise(200);
		}
		catch(ExecuteException | SerialFinallyException e)
		{
			e.printStackTrace();
		}
		
	}

	//@Test
	public void fishThemWithHook()
	{
		ArrayList<Hook> hooks = new ArrayList<>();
        HookFactory factory = new HookFactory(config, log, theRobot);
		hooks.add(factory.fishingHook);
        try {
            scriptManager.getScript(ScriptNames.FISHING).goToThenExec(3, theRobot, hooks);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}