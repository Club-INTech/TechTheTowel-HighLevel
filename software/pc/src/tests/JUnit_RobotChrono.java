package tests;

import enums.ServiceNames;
import hook.Hook;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pathDingDing.PathDingDing;
import robot.RobotChrono;
import smartMath.Vec2;
import table.Table;

import java.util.ArrayList;

/**
 * Tests unitaires pour RobotChrono.
 *
 * @author pf
 */
public class JUnit_RobotChrono extends JUnit_Test {

	/** The robotchrono. */
	private RobotChrono robotchrono;
	
	/** the table */
	private Table table;
	
	/* (non-Javadoc)
	 * @see tests.JUnit_Test#setUp()
	 */
	@Before
	public void setUp() throws Exception {
		super.setUp();
		log.debug("JUnit_RobotChronoTest.setUp()");
		table = (Table)container.getService(ServiceNames.TABLE);
		robotchrono = new RobotChrono(config, log, (PathDingDing)container.getService(ServiceNames.PATHDINGDING));
		robotchrono.setPosition(new Vec2(0, 1500));
		robotchrono.setOrientation(0);
	}

	/**
	 * Test_avancer.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void test_avancer() throws Exception
	{
		log.debug("JUnit_RobotChronoTest.test_avancer()");
		robotchrono.moveLengthwise(10);
		System.out.println("Avant: "+robotchrono.getPosition());
		Assert.assertTrue(robotchrono.getPosition().equals(new Vec2(10,1500)));
        System.out.println("Après: "+robotchrono.getPosition());
	}

	/**
	 * Test_va_au_point_symetrie.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void test_va_au_point_symetrie() throws Exception
	{
		log.debug("JUnit_RobotChronoTest.test_va_au_point_symetrie()");
		config.set("couleur", "jaune");
		robotchrono.updateConfig();
		robotchrono = new RobotChrono(config, log, (PathDingDing)container.getService(ServiceNames.PATHDINGDING));
		robotchrono.setPosition(new Vec2(0, 1500));
		robotchrono.setOrientation(0);
		robotchrono.moveToLocation(new Vec2(10, 1400), new ArrayList<Hook>(), table);
		Assert.assertTrue(robotchrono.getPosition().distance(new Vec2(10,1400)) < 2);

		config.set("couleur", "rouge");
		robotchrono = new RobotChrono(config, log, (PathDingDing)container.getService(ServiceNames.PATHDINGDING));
		robotchrono.setPosition(new Vec2(0, 1500));
		robotchrono.setOrientation(0);
		robotchrono.moveToLocation(new Vec2(10, 1400), new ArrayList<Hook>(), table);
		Assert.assertTrue(robotchrono.getPosition().distance(new Vec2(-10,1400)) < 2);
	}
	
	/**
	 * Test_va_au_point.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void test_va_au_point() throws Exception
	{
		log.debug("JUnit_RobotChronoTest.test_va_au_point()");
		robotchrono.moveToLocation(new Vec2(10, 1400), new ArrayList<Hook>(), table);
		Assert.assertTrue(robotchrono.getPosition().distance(new Vec2(10,1400)) < 2);
	}

	/**
	 * Test_tourner.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void test_tourner() throws Exception
	{
		log.debug("JUnit_RobotChronoTest.test_tourner()");
		robotchrono.turn((float)1.2);
		Assert.assertTrue(robotchrono.getOrientation()==(float)1.2);
	}

	/**
	 * Test_suit_chemin.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void test_suit_chemin() throws Exception
	{
		log.debug("JUnit_RobotChronoTest.test_suit_chemin()");
		ArrayList<Vec2> chemin = new ArrayList<Vec2>();
		chemin.add(new Vec2(20, 1400));
		chemin.add(new Vec2(40, 1500));
		robotchrono.followPath(chemin, null);
		Assert.assertTrue(robotchrono.getPosition().distance(new Vec2(40,1500)) < 2);
		
	}
	
	/**
	 * Test_actionneurs.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void test_actionneurs() throws Exception
	{
		// TODO: tester les actionneurs de robotchrono
	}
		
}
