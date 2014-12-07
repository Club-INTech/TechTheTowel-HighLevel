package tests;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import enums.ServiceNames;
import robot.cardsWrappers.ActuatorCardWrapper;

// TODO: Auto-generated Javadoc
/**
 * The Class JUnit_ActuatorManager.
 */
public class JUnit_ActuatorManager extends JUnit_Test {

	/** The actionneurs. */
	ActuatorCardWrapper actionneurs;
	
	/* (non-Javadoc)
	 * @see tests.JUnit_Test#setUp()
	 */
	@Before
	public void setUp() throws Exception {
		super.setUp();
		log.debug("JUnit_ActionneursTest.setUp()", this);
		actionneurs = (ActuatorCardWrapper)container.getService(ServiceNames.ACTUATOR_CARD_WRAPPER);
	}
	
	// TODO : un test par actionneur
	/**
	 * Exemple test.
	 *
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	@Test
	public boolean exempleTest() throws Exception
	{
		Assert.assertTrue( 42 != 1337 );
		return true;
	}
}
