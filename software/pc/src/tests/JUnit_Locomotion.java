package tests;

import hook.Hook;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import enums.ServiceNames;
import robot.Locomotion;
import smartMath.Vec2;

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
		mLocomotion.setPosition(new Vec2(0, 1500));
		mLocomotion.setOrientation(0);
	}
	
	/**
	 * Test_tourner.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void testTurn() throws Exception
	{
		log.debug("JUnit_DeplacementsTest.test_tourner()", this);
		mLocomotion.turn((float)1.2, new ArrayList<Hook>(), false);
		Thread.sleep(2000);
		Vec2 position = mLocomotion.getPosition();
		Assert.assertEquals(0, position.x, 5);
		Assert.assertEquals(1500, position.y, 5);
		Assert.assertEquals(1200, mLocomotion.getOrientation(), 50);
	}
	
}
