
package tests;

import org.junit.Before;
import org.junit.Test;

import enums.ServiceNames;
import enums.Speed;
import robot.RobotReal;
import smartMath.Vec2;

// TODO: Auto-generated Javadoc
/**
 * Vérifie que le robot est bien branché.
 * @author pf, marsu
 *
 */

public class JUnit_javaToRobotLink  extends JUnit_Test 
{

	/** The robotvrai. */
	private RobotReal robotvrai;
	
	/* (non-Javadoc)
	 * @see tests.JUnit_Test#setUp()
	 */
	@Before
	public void setUp() throws Exception 
	{
		super.setUp();
		config.set("couleur", "jaune");
		
<<<<<<< HEAD
		robotvrai = (RobotReal)container.getService("RobotVrai");
		robotvrai.setPosition(new Vec2(1484, 1000));	// TODO : cette position doit être la position de départ du robot 
		//On démarre avec les cales !!!!
=======
		robotvrai = (RobotReal)container.getService(ServiceNames.ROBOT_REAL);
		robotvrai.setPosition(new Vec2(1251, 1695));	// TODO : cette position doit être la position de départ du robot 
		//On démarre avec la cale !!!!
>>>>>>> refs/remotes/origin/refactor
		robotvrai.setOrientation((float)(-Math.PI/2));
		robotvrai.setLocomotionSpeed(Speed.BETWEEN_SCRIPTS);
		container.startInstanciedThreads();
		
	}
	
	
	/**
	 * Test_bidon.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void test_bidon() throws Exception
	{
		robotvrai.moveLengthwise(100);
		robotvrai.moveLengthwise(-100);
	}

}
