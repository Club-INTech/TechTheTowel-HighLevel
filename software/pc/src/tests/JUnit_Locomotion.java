package tests;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import enums.ServiceNames;
import robot.Locomotion;
import robot.cardsWrappers.*;
import smartMath.Vec2;
import utils.Sleep;

// TODO: Auto-generated Javadoc
/**
 * Tests unitaires pour Deplacements.
 *
 * @author pf
 */
public class JUnit_Locomotion extends JUnit_Test
{

	/** The deplacements. */
	private Locomotion mLocomotion;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		// appelé avant chaque batterie de test
		/*
		 * tearDownAfterClass
		 * @AfterClass apres chaque batterie de test de la meme classe JUnit
		 */
	}

	/* (non-Javadoc)
	 * @see tests.JUnit_Test#setUp()
	 */
	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		log.debug("JUnit_DeplacementsTest.setUp()", this);
		mLocomotion = (Locomotion)container.getService(ServiceNames.LOCOMOTION);
		mLocomotion.set(new Vec2(0, 1500));
		mLocomotion.setOrientation(0);
		mLocomotion.setTranslationnalSpeed(80);
	}
	
	/**
	 * Test_tourner.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void testTurn() throws Exception
	{
	    mLocomotion.enableRotationnalFeedbackLoop();
        mLocomotion.enableTranslationnalFeedbackLoop();
		log.debug("JUnit_DeplacementsTest.test_tourner()", this);
		mLocomotion.turn((float)1.2);
		Thread.sleep(2000);
		double[] infos_float = mLocomotion.getCurrentPositionAndOrientation();
		Assert.assertEquals(0, infos_float[0], 5);
		Assert.assertEquals(1500, infos_float[1], 5);
		Assert.assertEquals(1200, infos_float[2], 50);
	}
	
}
