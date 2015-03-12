package tests;

import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

import enums.ServiceNames;
import robot.cards.laser.LaserFiltration;
import robot.cards.laser.LaserCardWrapper;
import robot.RobotReal;
import smartMath.Vec2;
import table.Table;
import utils.Sleep;


/**
 * The Class JUnit_Laser.
 */
public class JUnit_Laser extends JUnit_Test {

	/** The laser. */
	LaserCardWrapper laser;
	
	/** The filtragelaser. */
	LaserFiltration filtragelaser;
	
	/** The robotvrai. */
	RobotReal robotvrai;
	
	/** The table. */
	Table table;

	/* (non-Javadoc)
	 * @see tests.JUnit_Test#setUp()
	 */
	@Before
	public void setUp() throws Exception {
		super.setUp();
		log.debug("JUnit_Laser_Test.setUp()", this);
		filtragelaser = (LaserFiltration) container.getService(ServiceNames.LASER_FILTRATION);
		laser = (LaserCardWrapper) container.getService(ServiceNames.LASER);
		robotvrai = (RobotReal) container.getService(ServiceNames.ROBOT_REAL);
		table = (Table) container.getService(ServiceNames.TABLE);
	}

	/**
	 * Test_avant_verification.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void test_avant_verification() throws Exception
	{
		//Ok
		//Aucune balise n'est allumée
		log.debug("JUnit_Laser_Test.test_avant_verification()", this);
		Assert.assertTrue(laser.activeBeacons().size() == 0);
		Assert.assertTrue(laser.ignoredBeacons().size() == 2);		
	}

	/**
	 * Test_apres_verification.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void test_apres_verification() throws Exception
	{
		//Les balises sont censées être toutes les deux allumées
		log.debug("JUnit_Laser_Test.test_apres_verification()", this);
		laser.turnOn();
		Sleep.sleep(3000);
		laser.checkConnectedBeacons();
		Assert.assertTrue(laser.activeBeacons().size() == 2);
		Assert.assertTrue(laser.ignoredBeacons().size() == 0);
		laser.eteindre();

	}

	/**
	 * Test_coherence.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void test_coherence() throws Exception
	{
		log.debug("JUnit_Laser_Test.test_coherence()", this);
		//Assert.assertTrue(laser.verifier_balises_connectes() == 1);
		log.debug("Ca raconte quoi sur la cohérence des mesures?", this);
		laser.checkBeaconConsistency();
	}

	/**
	 * Test_pour_kayou.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void test_pour_kayou() throws Exception
	{
		container.getService(ServiceNames.THREAD_LASER);
		container.startInstanciedThreads();
		laser.turnOn();
		while(true)
		{
			Sleep.sleep(100);
		}

	}

	/**
	 * Test_on_off.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void test_on_off() throws Exception
	{
		//Ok
		log.debug("JUnit_Laser_Test.test_on_off()", this);
		laser.turnOn();
		Sleep.sleep(2000);
		laser.eteindre();
	}

	/**
	 * Test_position_balise.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void test_position_balise() throws Exception
	{
		log.debug("JUnit_Laser_Test.test_position_balise()", this);
		robotvrai.setOrientation(0);
		robotvrai.setPosition(new Vec2(30,100));
		Vec2 pos_balise0 = new Vec2(0,300);
		Vec2 pos_balise1; //position de la baslise enregistrée
		laser.turnOn();
		Sleep.sleep(3000);
		pos_balise1 = laser.position_balise(0);
		//Position incohérente, il faut déjà connaître le sens du laser pour le caler avec le sens du robot
		log.debug("La balise 1 se trouve en ("+pos_balise1+") selon le laser.",this);
		//Il y a réception d'un acquittement, d'où erreur
		float ecart = pos_balise1.distance(pos_balise0);
		log.debug("L'écart est de : "+ecart, this);
		Assert.assertTrue( ecart < 500);
		laser.eteindre();
		//La pile de la seconde balise est vide, il faut la remplacer
		//Assert.assertTrue(laser.position_balise(1).distance(new Vec2(600,)) < 500);
	}

	/**
	 * Test_vitesse.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void test_vitesse() throws Exception
	{
		//Ok
		log.debug("JUnit_Laser_Test.test_vitesse()", this);
		laser.turnOn();
		Sleep.sleep(1000);
		Assert.assertTrue(filtragelaser.speed().squaredLength() < 10);
		Sleep.sleep(1000);
		laser.eteindre();
	}
	
	/**
	 * Test_position_balise_relative.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void test_position_balise_relative() throws Exception
	{
		log.debug("JUnit_Laser_Test.test_position_balise()", this);
		robotvrai.setOrientation(0);
		robotvrai.setPosition(new Vec2(0,300));
		Vec2 pos_balise0 = new Vec2(0,300);
		Vec2 pos_balise1;
		laser.turnOn();
		Sleep.sleep(3000);
		pos_balise1 = laser.position_balise_relative(0);
		//Position incohérente, il faut déjà connaître le sens du laser pour le caler avec le sens du robot
		log.debug("La balise 1 se trouve en ("+pos_balise1+") selon le laser.",this);
		//Il y a réception d'un acquittement, d'où erreur
		float ecart = pos_balise1.distance(pos_balise0);
		log.debug("L'écart est de : "+ecart, this);
		Assert.assertTrue( ecart < 500);
		laser.eteindre();
		//La pile de la seconde balise est vide, il faut la remplacer
		//Assert.assertTrue(laser.position_balise(1).distance(new Vec2(600,)) < 500);
	}

}
