
package tests;

import hook.Hook;

import java.util.ArrayList;

import scripts.AbstractScript;
import scripts.ScriptManager;
import smartMath.Vec2;
import strategie.GameState;

import org.junit.Before;
import org.junit.Test;

import pathDingDing.PathDingDing;
import enums.ActuatorOrder;
import enums.ScriptNames;
import enums.ServiceNames;
import exceptions.PathNotFoundException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialConnexionException;
import exceptions.serial.SerialFinallyException;
import robot.Robot;
import robot.cardsWrappers.SensorsCardWrapper;

/**
 * classe des matchs scriptes
 */

public class JUnit_ActuatorManager extends JUnit_Test 
{
	ArrayList<Hook> emptyHook;
	GameState<Robot> real_state;
	ScriptManager scriptmanager;
	SensorsCardWrapper  mSensorsCardWrapper;
	PathDingDing pathDingDing;
	Robot mRobot;
	
	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		real_state = (GameState<Robot>) container.getService(ServiceNames.GAME_STATE);
		scriptmanager = (ScriptManager) container.getService(ServiceNames.SCRIPT_MANAGER);
		mSensorsCardWrapper = (SensorsCardWrapper) container.getService(ServiceNames.SENSORS_CARD_WRAPPER);
        pathDingDing = (PathDingDing)container.getService(ServiceNames.PATHDINGDING);
		emptyHook = new ArrayList<Hook> ();  

		if (real_state.robot.getSymmetry())
		{
			real_state.robot.setPosition(new Vec2 (-1381,1000));
			real_state.robot.setOrientation(0); 
			//si on est jaune on est en 0 
		}
		else
		{
			real_state.robot.setPosition(new Vec2 (1381,1000));
			real_state.robot.setOrientation(Math.PI);
			//sinon on est vert donc on est en PI
		}
		
		real_state.robot.updateConfig();
		mRobot=real_state.robot;	
	}
	
	public void waitMatchBegin()
	{

		System.out.println("Robot pret pour le match, attente du retrait du jumper");
		
		// attends que le jumper soit retiré du robot
		
		boolean jumperWasAbsent = mSensorsCardWrapper.isJumperAbsent();
		while(jumperWasAbsent || !mSensorsCardWrapper.isJumperAbsent())
		{
			jumperWasAbsent = mSensorsCardWrapper.isJumperAbsent();
			 real_state.robot.sleep(100);
		}

		// maintenant que le jumper est retiré, le match a commencé
		//ThreadTimer.matchStarted = true;
	}

	
	/**
	 * Test des machoires du robot
	 *
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	@Test
	public void testJawRight() throws Exception
	{
		mRobot.useActuator(ActuatorOrder.ELEVATOR_OPEN_JAW_RIGHT, true);
		mRobot.useActuator(ActuatorOrder.ELEVATOR_CLOSE_JAW_RIGHT, true);
	}	
	@Test
	public void testJawLeft() throws Exception
	{
		mRobot.useActuator(ActuatorOrder.ELEVATOR_OPEN_JAW_LEFT, true);
		mRobot.useActuator(ActuatorOrder.ELEVATOR_CLOSE_JAW_LEFT, true);
	}
	@Test
	public void testJawTogether() throws Exception
	{
		mRobot.useActuator(ActuatorOrder.ELEVATOR_OPEN_JAW, true);
		mRobot.useActuator(ActuatorOrder.ELEVATOR_CLOSE_JAW, false);
	}
	
	
	/**
	 * Test des claps du robot
	 *
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	@Test
	public void testClapRight() throws Exception
	{
		mRobot.useActuator(ActuatorOrder.HIGH_RIGHT_CLAP, true);
		mRobot.useActuator(ActuatorOrder.MID_RIGHT_CLAP, true);
		mRobot.useActuator(ActuatorOrder.LOW_RIGHT_CLAP, false);
	}
	@Test
	public void testClapLeft() throws Exception
	{
	
		mRobot.useActuator(ActuatorOrder.HIGH_LEFT_CLAP, true);
		mRobot.useActuator(ActuatorOrder.MID_LEFT_CLAP, true);
		mRobot.useActuator(ActuatorOrder.LOW_LEFT_CLAP, false);
	}
	
	
	/**
	 * Test des tapis du robot
	 *
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	@Test
	public void testCarpetRight() throws Exception
	{	
		mRobot.useActuator(ActuatorOrder.RIGHT_CARPET_DROP, true);
		mRobot.useActuator(ActuatorOrder.RIGHT_CARPET_FOLDUP, false);
	}
	
	@Test
	public void testCarpetLeft() throws Exception
	{
		mRobot.useActuator(ActuatorOrder.LEFT_CARPET_DROP, true);
		mRobot.useActuator(ActuatorOrder.LEFT_CARPET_FOLDUP, false);
	}
	
	
	
	/**
	 * Test de l'ascenceur du robot
	 *
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	@Test
	public void testElevator() throws Exception
	{	
		mRobot.useActuator(ActuatorOrder.ELEVATOR_HIGH, true);
		mRobot.useActuator(ActuatorOrder.ELEVATOR_GROUND, true);
		mRobot.useActuator(ActuatorOrder.ELEVATOR_STAGE, true);
		mRobot.useActuator(ActuatorOrder.ELEVATOR_LOW, false);
	}
	
	
	/**
	 * Test des bras
	 *
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	@Test
	public void testArmRight() throws Exception
	{	
		mRobot.useActuator(ActuatorOrder.ARM_RIGHT_CLOSE, true);
		mRobot.useActuator(ActuatorOrder.ARM_RIGHT_OPEN_SLOW, true);
		mRobot.useActuator(ActuatorOrder.ARM_RIGHT_CLOSE_SLOW, true);
		mRobot.useActuator(ActuatorOrder.ARM_RIGHT_OPEN, false);
	}
	@Test
	public void testArmLeft() throws Exception
	{
		mRobot.useActuator(ActuatorOrder.ARM_LEFT_CLOSE, true);
		mRobot.useActuator(ActuatorOrder.ARM_LEFT_OPEN_SLOW, true);
		mRobot.useActuator(ActuatorOrder.ARM_LEFT_CLOSE_SLOW, true);
		mRobot.useActuator(ActuatorOrder.ARM_LEFT_OPEN, false);
	}
	
	
	/**
	 * Test du guide
	 *
	 * @return true, if successful
	 * @throws Exception the exception
	 */
		
	@Test
	public void testGuideLeft() throws Exception
	{	

		mRobot.useActuator(ActuatorOrder.OPEN_LEFT_GUIDE, false);
		mRobot.useActuator(ActuatorOrder.MID_LEFT_GUIDE, true);
		mRobot.useActuator(ActuatorOrder.CLOSE_LEFT_GUIDE, false);
	}	
	@Test
	public void testGuideRight() throws Exception
	{	

		mRobot.useActuator(ActuatorOrder.OPEN_RIGHT_GUIDE, true);
		mRobot.useActuator(ActuatorOrder.MID_RIGHT_GUIDE, false);
		mRobot.useActuator(ActuatorOrder.CLOSE_RIGHT_GUIDE, false);
	}
	
}
