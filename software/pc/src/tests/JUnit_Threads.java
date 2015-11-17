package tests;

import enums.ServiceNames;
import org.junit.Assert;
import org.junit.Test;
import robot.RobotReal;
import robot.cardsWrappers.LocomotionCardWrapper;
import smartMath.Vec2;
import table.Table;
import threads.ThreadTimer;

/**
 * Tests unitaires des threads.
 *
 * @author pf
 */

public class JUnit_Threads extends JUnit_Test {

	/**
	 * Test_arret.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void test_arret() throws Exception
	{
		LocomotionCardWrapper deplacements = (LocomotionCardWrapper)container.getService(ServiceNames.LOCOMOTION_CARD_WRAPPER);
		deplacements.setX(0);
		deplacements.setY(1500);
		deplacements.setOrientation(0);
		deplacements.setTranslationnalSpeed(80);
		RobotReal robotvrai = (RobotReal) container.getService(ServiceNames.ROBOT_REAL);
		// TODO démarrer thread position
		container.startInstanciedThreads();
		Thread.sleep(100);
		Assert.assertTrue(robotvrai.getPosition().equals(new Vec2(0,1500)));
		container.stopAllThreads();
		deplacements.setX(100);
		deplacements.setY(1400);
		Thread.sleep(100);
		Assert.assertTrue(robotvrai.getPosition().equals(new Vec2(0,1500)));
	}

	/**
	 * Test_detection_obstacle.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void test_detection_obstacle() throws Exception
	{
		RobotReal robotvrai = (RobotReal) container.getService(ServiceNames.ROBOT_REAL);
		robotvrai.setPosition(new Vec2(0, 900));
		robotvrai.setOrientation(0);
		
		Table table = (Table) container.getService(ServiceNames.TABLE);
		Assert.assertTrue(table.getObstacleManager().getMobileObstaclesCount() == 0);
		
		container.getService(ServiceNames.THREAD_SENSOR);
		container.startInstanciedThreads();
		Thread.sleep(300);
		Assert.assertTrue(table.getObstacleManager().getMobileObstaclesCount() >= 1);

	}
	
	/**
	 * Test_fin_match.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void test_fin_match() throws Exception
	{
		config.set("temps_match", "3");
		container.getService(ServiceNames.THREAD_TIMER);
		long t1 = System.currentTimeMillis();
		container.startAllThreads();
		while(!ThreadTimer.matchEnded)
		{
			Thread.sleep(500);
			if(System.currentTimeMillis()-t1 >= 4000)
				break;
		}
		Assert.assertTrue(System.currentTimeMillis()-t1 < 4000);
	}
	
	/**
	 * Test_demarrage_match.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void test_demarrage_match() throws Exception
	{
		container.getService(ServiceNames.THREAD_TIMER);
		System.out.println("Veuillez mettre le jumper");
		Thread.sleep(2000);
		container.startInstanciedThreads();
		Thread.sleep(200);
		Assert.assertTrue(!ThreadTimer.matchStarted);
		System.out.println("Veuillez retirer le jumper");
		Thread.sleep(2000);
		Assert.assertTrue(ThreadTimer.matchStarted);
	}

	/**
	 * Test_serie.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void test_serie() throws Exception
	{
		RobotReal robotvrai = (RobotReal) container.getService(ServiceNames.ROBOT_REAL);
		robotvrai.setPosition(new Vec2(1000, 1400));
		robotvrai.setOrientation((float)Math.PI);
		container.startAllThreads();
		Thread.sleep(200);
		robotvrai.moveLengthwise(1000);
	}

	/**
	 * Test_fin_thread_avant_match.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void test_fin_thread_avant_match() throws Exception
	{
		container.startAllThreads();
		container.stopAllThreads();
	}

	
}
