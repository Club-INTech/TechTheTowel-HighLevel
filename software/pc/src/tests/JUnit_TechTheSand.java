package tests;

import java.util.ArrayList;

import enums.*;
import exceptions.BlockedActuatorException;
import exceptions.serial.SerialConnexionException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
		theRobot = (GameState<Robot>)container.getService(ServiceNames.GAME_STATE);
		scriptManager = (ScriptManager)container.getService(ServiceNames.SCRIPT_MANAGER);
		theRobot.robot.setPosition(Table.entryPosition);
		theRobot.robot.setOrientation(Math.PI);
		theRobot.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);
		//supression de l'obstacle d'arrivé, pour tests
		Vec2 sup = scriptManager.getScript(ScriptNames.TECH_THE_SAND).entryPosition(1, theRobot.robot.getRobotRadius(), theRobot.robot.getPosition()).position;
		theRobot.table.getObstacleManager().freePoint(sup);

		theRobot.table.deleteAllTheShells();
		try
		{
			if(!theRobot.robot.getContactSensorValue(ContactSensors.DOOR_CLOSED))
			{
				theRobot.robot.useActuator(ActuatorOrder.CLOSE_DOOR, true);
			}
			if(!theRobot.robot.getContactSensorValue(ContactSensors.DOOR_CLOSED))
			{
				theRobot.robot.useActuator(ActuatorOrder.STOP_DOOR, true);
				throw new BlockedActuatorException("Porte droite bloquée !");
			}
		}
		catch (SerialConnexionException e)
		{
			e.printStackTrace();
		}
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
			scriptManager.getScript(ScriptNames.TECH_THE_SAND).goToThenExec(1, theRobot, emptyHook);
			log.critical("FINI DE FORER");
			scriptManager.getScript(ScriptNames.CASTLE).execute(2, theRobot, emptyHook);
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
