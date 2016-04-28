package tests;

import enums.ActuatorOrder;
import enums.ServiceNames;
import enums.Speed;
import exceptions.ContainerException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialConnexionException;
import exceptions.serial.SerialManagerException;
import hook.Callback;
import hook.Hook;
//import hook.methods.SpeedDown;
import hook.types.HookFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import robot.Robot;
import robot.RobotReal;
import scripts.ScriptManager;
import strategie.GameState;
import table.Table;

import java.util.ArrayList;

/**
 *  Classe de test pour les Hooks
 */


public class JUnit_Hooks extends JUnit_Test 
{
	private GameState<Robot> theRobot;
	private ScriptManager scriptManager;
	private HookFactory hookFactory;
	private ArrayList<Hook> emptyHook = new ArrayList<Hook>();
	
	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		scriptManager = (ScriptManager)container.getService(ServiceNames.SCRIPT_MANAGER);
		theRobot = (GameState<Robot>)container.getService(ServiceNames.GAME_STATE);
		hookFactory = (HookFactory) container.getService(ServiceNames.HOOK_FACTORY);
		theRobot.robot.useActuator(ActuatorOrder.ARM_INIT, true);
		theRobot.robot.setOrientation(Math.PI);
		theRobot.robot.setPosition(Table.entryPosition);
		theRobot.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);
	}
	
	@Test
	public void testSpeed()
	{
		Hook speed = hookFactory.newXLesserHook(1000);
		//speed.addCallback(new Callback(new SpeedDown(),true,theRobot));
		emptyHook.add(speed);
		log.debug("Début de test !");
		try 
		{
			theRobot.robot.moveLengthwise(500,emptyHook,false);
		}
		catch (UnableToMoveException e) 
		{
			log.debug("Haha, fat chance !");
		}
	}
	
	@After
	public void after()
	{
		theRobot.robot.immobilise();
		log.debug("Fin de test !");
	}
}
