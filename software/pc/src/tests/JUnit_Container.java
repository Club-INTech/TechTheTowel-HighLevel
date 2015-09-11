package tests;

import org.junit.Test;

import enums.ServiceNames;

/**
 * Tests unitaires pour le container
 * Sert surtout à vérifier l'absence de dépendances circulaires, et d'éventuelles fautes de frappe...
 * @author pf
 */
public class JUnit_Container extends JUnit_Test {

	/**
	 * Test_log.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void test_log() throws Exception
	{
		log.debug("JUnit_ContainerTest.test_log()", this);
		container.getService(ServiceNames.LOG);
	}

	/**
	 * Test_config.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void test_config() throws Exception
	{
		log.debug("JUnit_ContainerTest.test_config()", this);
		container.getService(ServiceNames.CONFIG);
	}

	/**
	 * Test_table.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void test_table() throws Exception
	{
		log.debug("JUnit_ContainerTest.test_table()", this);
		container.getService(ServiceNames.TABLE);
	}

    /**
     * Test_deplacementshautniveau.
     *
     * @throws Exception the exception
     */
    @Test
    public void test_deplacementshautniveau() throws Exception
    {
        log.debug("JUnit_ContainerTest.test_deplacementshautniveau()", this);
        container.getService(ServiceNames.LOCOMOTION);
    }

	/**
	 * Test_deplacements.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void test_deplacements() throws Exception
	{
		log.debug("JUnit_ContainerTest.test_deplacements()", this);
		container.getService(ServiceNames.LOCOMOTION_CARD_WRAPPER);
	}

	/**
	 * Test_capteurs.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void test_capteurs() throws Exception
	{
		log.debug("JUnit_ContainerTest.test_capteurs()", this);
		container.getService(ServiceNames.SENSORS_CARD_WRAPPER);
	}

	/**
	 * Test_actionneurs.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void test_actionneurs() throws Exception
	{
		log.debug("JUnit_ContainerTest.test_actionneurs()", this);
		container.getService(ServiceNames.ACTUATOR_CARD_WRAPPER);
	}

	/**
	 * Test_ hook generator.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void test_HookGenerator() throws Exception
	{
		log.debug("JUnit_ContainerTest.test_HookGenerator()", this);
		container.getService(ServiceNames.HOOK_FACTORY);
	}

	/**
	 * Test_ robot vrai.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void test_RobotVrai() throws Exception
	{
		log.debug("JUnit_ContainerTest.test_RobotVrai()", this);
		container.getService(ServiceNames.ROBOT_REAL);
	}

	/**
	 * Test_ script manager.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void test_ScriptManager() throws Exception
	{
		log.debug("JUnit_ContainerTest.test_ScriptManager()", this);
		container.getService(ServiceNames.SCRIPT_MANAGER);
	}

	/**
	 * Test_pathfinding.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void test_pathfinding() throws Exception
	{
		log.debug("JUnit_ContainerTest.test_pathfinding()", this);
		container.getService(ServiceNames.PATHDINGDING);
	}

	/**
	 * Test_ laser.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void test_Laser() throws Exception
	{
		log.debug("JUnit_ContainerTest.test_Laser()", this);
		container.getService(ServiceNames.LASER);
	}

	/**
	 * Test_ filtrage laser.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void test_FiltrageLaser() throws Exception
	{
		log.debug("JUnit_ContainerTest.test_FiltrageLaser()", this);
		container.getService(ServiceNames.LASER_FILTRATION);
	}

	/**
	 * Test vérifiant que le système de containers se comporte bien si on appelle deux fois  le meme service .
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void test_doublon() throws Exception
	{
		log.debug("JUnit_ContainerTest.test_doublon()", this);
		container.getService(ServiceNames.LASER_FILTRATION);
		container.getService(ServiceNames.LASER_FILTRATION);
	}

	/**
	 * Test_ check up.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void test_CheckUp() throws Exception
	{
		log.debug("JUnit_ContainerTest.test_CheckUp()", this);
		container.getService(ServiceNames.CHECK_UP);
	}

	/**
	 * Test_serie asservissement.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void test_serieAsservissement() throws Exception
	{
		log.debug("JUnit_ContainerTest.test_serieAsservissement()", this);
		container.getService(ServiceNames.STM_CARD);
	}

	/**
	 * Test_thread timer.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void test_threadTimer() throws Exception
	{
		log.debug("JUnit_ContainerTest.test_threadTimer()", this);
		container.getService(ServiceNames.THREAD_TIMER);
	}

	/**
	 * Test_thread laser.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void test_threadLaser() throws Exception
	{
		log.debug("JUnit_ContainerTest.test_threadLaser()", this);
		container.getService(ServiceNames.THREAD_BALISE);
	}

	/**
	 * Test_thread capteurs.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void test_threadCapteurs() throws Exception
	{
		log.debug("JUnit_ContainerTest.test_threadCapteurs()", this);
		container.getService(ServiceNames.THREAD_SENSOR);
	}

}
