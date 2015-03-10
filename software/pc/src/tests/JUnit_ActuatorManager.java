package tests;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import enums.ActuatorOrder;
import enums.ServiceNames;
import robot.RobotReal;
import robot.cardsWrappers.ActuatorCardWrapper;

/**
 * The Class JUnit_ActuatorManager.
 */
public class JUnit_ActuatorManager extends JUnit_Test {

	/** The actionneurs. */
	ActuatorCardWrapper actionneurs;
	
	/** The robot */
	RobotReal robot;
	
	/* (non-Javadoc)
	 * @see tests.JUnit_Test#setUp()
	 */
	@Before
	public void setUp() throws Exception {
		super.setUp();
		log.debug("JUnit_ActionneursTest.setUp()", this);
		
		actionneurs = (ActuatorCardWrapper)container.getService(ServiceNames.ACTUATOR_CARD_WRAPPER);
        robot = (RobotReal) container.getService(ServiceNames.ROBOT_REAL);
	}
	
	/**
	 * Test des machoires du robot
	 *
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	@Test
	public boolean testJawRight() throws Exception
	{
		robot.useActuator(ActuatorOrder.ELEVATOR_OPEN_JAW_RIGHT, true);
		robot.useActuator(ActuatorOrder.ELEVATOR_CLOSE_JAW_RIGHT, true);

		return true;
	}	
	@Test
	public boolean testJawLeft() throws Exception
	{
		robot.useActuator(ActuatorOrder.ELEVATOR_OPEN_JAW_LEFT, true);
		robot.useActuator(ActuatorOrder.ELEVATOR_CLOSE_JAW_LEFT, true);

		return true;
	}
	@Test
	public boolean testJawTogether() throws Exception
	{
		robot.useActuator(ActuatorOrder.ELEVATOR_OPEN_JAW, true);
		robot.useActuator(ActuatorOrder.ELEVATOR_CLOSE_JAW, false);
		return true;
	}
	
	
	/**
	 * Test des claps du robot
	 *
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	@Test
	public boolean testClapRight() throws Exception
	{
		robot.useActuator(ActuatorOrder.HIGH_RIGHT_CLAP, true);
		robot.useActuator(ActuatorOrder.MID_RIGHT_CLAP, true);
		robot.useActuator(ActuatorOrder.LOW_RIGHT_CLAP, false);
		return true;
	}
	@Test
	public boolean testClapLeft() throws Exception
	{

		robot.useActuator(ActuatorOrder.HIGH_LEFT_CLAP, true);
		robot.useActuator(ActuatorOrder.MID_LEFT_CLAP, true);
		robot.useActuator(ActuatorOrder.LOW_LEFT_CLAP, false);
		return true;
	}
	
	
	/**
	 * Test des tapis du robot
	 *
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	@Test
	public boolean testCarpetRight() throws Exception
	{	
		robot.useActuator(ActuatorOrder.RIGHT_CARPET_DROP, true);
		robot.useActuator(ActuatorOrder.RIGHT_CARPET_FOLDUP, false);
		return true;
	}
	
	@Test
	public boolean testCarpetLeft() throws Exception
	{
		robot.useActuator(ActuatorOrder.LEFT_CARPET_DROP, true);
		robot.useActuator(ActuatorOrder.LEFT_CARPET_FOLDUP, false);
		return true;
	}
	
	
	
	/**
	 * Test de l'ascenceur du robot
	 *
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	@Test
	public boolean testElevator() throws Exception
	{	
		robot.useActuator(ActuatorOrder.ELEVATOR_HIGH, true);
		robot.useActuator(ActuatorOrder.ELEVATOR_GROUND, true);
		robot.useActuator(ActuatorOrder.ELEVATOR_STAGE, true);
		robot.useActuator(ActuatorOrder.ELEVATOR_LOW, false);
		return true;
	}
	
	
	/**
	 * Test des bras
	 *
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	@Test
	public boolean testArmRight() throws Exception
	{	
		robot.useActuator(ActuatorOrder.ARM_RIGHT_CLOSE, true);
		robot.useActuator(ActuatorOrder.ARM_RIGHT_OPEN_SLOW, true);
		robot.useActuator(ActuatorOrder.ARM_RIGHT_CLOSE_SLOW, true);
		robot.useActuator(ActuatorOrder.ARM_RIGHT_OPEN, false);

		return true;
	}
	@Test
	public boolean testArmLeft() throws Exception
	{
		robot.useActuator(ActuatorOrder.ARM_LEFT_CLOSE, true);
		robot.useActuator(ActuatorOrder.ARM_LEFT_OPEN_SLOW, true);
		robot.useActuator(ActuatorOrder.ARM_LEFT_CLOSE_SLOW, true);
		robot.useActuator(ActuatorOrder.ARM_LEFT_OPEN, false);
		return true;
	}
	
	
	/**
	 * Test du guide
	 *
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	
	@Test
	public boolean testGuideLeft() throws Exception
	{	

		robot.useActuator(ActuatorOrder.OPEN_LEFT_GUIDE, false);
		robot.useActuator(ActuatorOrder.MID_LEFT_GUIDE, true);
		robot.useActuator(ActuatorOrder.CLOSE_LEFT_GUIDE, false);
		return true;
	}	
	@Test
	public boolean testGuideRight() throws Exception
	{	

		robot.useActuator(ActuatorOrder.OPEN_RIGHT_GUIDE, true);
		robot.useActuator(ActuatorOrder.MID_RIGHT_GUIDE, false);
		robot.useActuator(ActuatorOrder.CLOSE_RIGHT_GUIDE, false);
		return true;
	}
	
}
