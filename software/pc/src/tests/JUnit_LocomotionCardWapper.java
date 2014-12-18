package tests;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import enums.ServiceNames;
import robot.cardsWrappers.*;
import utils.Sleep;

// TODO: Auto-generated Javadoc
/**
 * Tests unitaires pour Deplacements.
 *
 * @author pf
 */
public class JUnit_LocomotionCardWapper extends JUnit_Test
{

	/** The deplacements. */
	private LocomotionCardWrapper mLocomotionCardWrapper;
	
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
		mLocomotionCardWrapper = (LocomotionCardWrapper)container.getService(ServiceNames.LOCOMOTION_CARD_WRAPPER);
		mLocomotionCardWrapper.setX(0);
		mLocomotionCardWrapper.setY(1500);
		mLocomotionCardWrapper.setOrientation(0);
		mLocomotionCardWrapper.setTranslationnalSpeed(80);
	}
	
	/**
	 * Test_infos_xyo.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void test_infos_xyo() throws Exception
	{
		log.debug("JUnit_DeplacementsTest.test_infos_xyo()", this);
		double[] infos_float = mLocomotionCardWrapper.getCurrentPositionAndOrientation();
		Assert.assertTrue(infos_float[0] == 0);
		Assert.assertTrue(infos_float[1] == 1500);
		Assert.assertTrue(infos_float[2] == 0);
	}

	/**
	 * Test_avancer.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void test_avancer() throws Exception
	{
		log.debug("JUnit_DeplacementsTest.test_avancer()", this);
		mLocomotionCardWrapper.moveLengthwise(100);
		Thread.sleep(1000);
		double[] infos_float = mLocomotionCardWrapper.getCurrentPositionAndOrientation();
		Assert.assertEquals(100, infos_float[0], 5);
		Assert.assertEquals(1500, infos_float[1], 5);
		Assert.assertEquals(0, infos_float[2], 50);

	}

	/**
	 * Test_tourner.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void test_tourner() throws Exception
	{
	    mLocomotionCardWrapper.enableRotationnalFeedbackLoop();
        mLocomotionCardWrapper.enableTranslationnalFeedbackLoop();
		log.debug("JUnit_DeplacementsTest.test_tourner()", this);
		System.out.println("Avant tourner");
		mLocomotionCardWrapper.turn((float)1.2);
        System.out.println("Après tourner");
		Thread.sleep(2000);
		double[] infos_float = mLocomotionCardWrapper.getCurrentPositionAndOrientation();
		Assert.assertEquals(0, infos_float[0], 5);
		Assert.assertEquals(1500, infos_float[1], 5);
		Assert.assertEquals(1200, infos_float[2], 50);
	}
	
	/**
	 * Test_set_x.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void test_set_x() throws Exception
	{
		log.debug("JUnit_DeplacementsTest.test_set_x()", this);
		mLocomotionCardWrapper.setX(30);
		double[] infos_float = mLocomotionCardWrapper.getCurrentPositionAndOrientation();
		Assert.assertTrue(infos_float[0] == 30);
		Assert.assertTrue(infos_float[1] == 1500);
		Assert.assertTrue(infos_float[2] == 0);
	}

	/**
	 * Test_set_y.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void test_set_y() throws Exception
	{
		log.debug("JUnit_DeplacementsTest.test_set_y()", this);
		mLocomotionCardWrapper.setY(330);
		double[] infos_float = mLocomotionCardWrapper.getCurrentPositionAndOrientation();
		Assert.assertTrue(infos_float[0] == 0);
		Assert.assertTrue(infos_float[1] == 330);
		Assert.assertTrue(infos_float[2] == 0);
	}
	
	/**
	 * Test_set_orientation.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void test_set_orientation() throws Exception
	{
		log.debug("JUnit_DeplacementsTest.test_set_orientation()", this);
		mLocomotionCardWrapper.setOrientation(1.234f);
		double[] infos_float = mLocomotionCardWrapper.getCurrentPositionAndOrientation();
		Assert.assertTrue(infos_float[0] == 0);
		Assert.assertTrue(infos_float[1] == 1500);
		Assert.assertTrue(infos_float[2] > 1233 && infos_float[2] < 1235);
	}

	/**
	 * Test_equilibrage.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void test_equilibrage() throws Exception
	{
	    mLocomotionCardWrapper.setTranslationnalSpeed(170);
	    mLocomotionCardWrapper.disableRotationnalFeedbackLoop();
	    mLocomotionCardWrapper.moveLengthwise(500);
        mLocomotionCardWrapper.enableRotationnalFeedbackLoop();
	    Sleep.sleep(1000);
	}
	
}