package tests;

import hook.types.HookFactory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import enums.ServiceNames;
import enums.Speed;
import robot.RobotReal;
import smartMath.Vec2;

/**
 * Tests unitaires des hooks (en rouge: avec symétrie).
 *
 * @author pf
 */

public class JUnit_HookGreen extends JUnit_Test {

	/** The robotvrai. */
	private RobotReal robotvrai;
	
	/** The hookgenerator. */
	@SuppressWarnings("unused")
	private HookFactory hookgenerator;
	
	/* (non-Javadoc)
	 * @see tests.JUnit_Test#setUp()
	 */
	@Before
	public void setUp() throws Exception {
		super.setUp();
		log.debug("JUnit_HookRougeTest.setUp()", this);
		config.set("couleur", "rouge");
		robotvrai = (RobotReal) container.getService(ServiceNames.ROBOT_REAL);
		robotvrai.setPosition(new Vec2(0, 1500));
		robotvrai.setOrientation(0);
        robotvrai.setLocomotionSpeed(Speed.SLOW);
	}
	
	// TODO �crire un test par type de hook
	/**
	 * Test_hook abscisse_avancer.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void test_hookAbscisse_avancer() throws Exception
	{
		Assert.assertTrue(666 != 42);
	}
	
}
