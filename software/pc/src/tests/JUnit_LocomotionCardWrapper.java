package tests;

import enums.ServiceNames;
import enums.TurningStrategy;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import robot.serial.SerialWrapper;
import utils.Sleep;

/**
 * Tests unitaires pour Deplacements.
 *
 * @author pf
 */
public class JUnit_LocomotionCardWrapper extends JUnit_Test
{

	/** The deplacements. */
	private SerialWrapper serialWrapper;
	
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
		log.debug("JUnit_DeplacementsTest.setUp()");
		serialWrapper = (SerialWrapper) container.getService(ServiceNames.SERIAL_WRAPPER);
		serialWrapper.setX(0);
		serialWrapper.setY(1500);
		serialWrapper.setOrientation(0);
		serialWrapper.setTranslationnalSpeed(170);
		serialWrapper.setRotationnalSpeed(160);
	}
	
	/**
	 * Test_infos_xyo.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void test_infos_xyo() throws Exception
	{
		log.debug("JUnit_DeplacementsTest.test_infos_xyo()");
		float[] infos_float = serialWrapper.getCurrentPositionAndOrientation();
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
		log.debug("JUnit_DeplacementsTest.test_avancer()");
		serialWrapper.moveLengthwise(100);
		Thread.sleep(1000);
		float[] infos_float = serialWrapper.getCurrentPositionAndOrientation();
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
	    serialWrapper.enableRotationnalFeedbackLoop();
        serialWrapper.enableTranslationnalFeedbackLoop();
		log.debug("JUnit_DeplacementsTest.test_tourner()");
		System.out.println("Avant tourner");
		serialWrapper.turn((float)1.2, TurningStrategy.FASTEST);
        System.out.println("Après tourner");
		Thread.sleep(2000);
		float[] infos_float = serialWrapper.getCurrentPositionAndOrientation();
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
		log.debug("JUnit_DeplacementsTest.test_set_x()");
		serialWrapper.setX(30);
		float[] infos_float = serialWrapper.getCurrentPositionAndOrientation();
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
		log.debug("JUnit_DeplacementsTest.test_set_y()");
		serialWrapper.setY(330);
		float[] infos_float = serialWrapper.getCurrentPositionAndOrientation();
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
		log.debug("JUnit_DeplacementsTest.test_set_orientation()");
		serialWrapper.setOrientation(1.234f);
		float[] infos_float = serialWrapper.getCurrentPositionAndOrientation();
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
	    serialWrapper.setTranslationnalSpeed(170);
	    serialWrapper.disableRotationnalFeedbackLoop();
	    serialWrapper.moveLengthwise(500);
        serialWrapper.enableRotationnalFeedbackLoop();
	    Sleep.sleep(1000);
	}
	
}
