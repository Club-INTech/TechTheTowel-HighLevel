
package tests;

import org.junit.Before;
import org.junit.Test;

import enums.Speed;
import robot.RobotReal;
import smartMath.Vec2;

/**
 * Vérifie que le robot est bien branché.
 * @author pf, marsu
 *
 */

public class JUnit_javaToRobotLink  extends JUnit_Test 
{

	private RobotReal robotvrai;
	
	@Before
	public void setUp() throws Exception 
	{
		super.setUp();
		config.set("couleur", "jaune");
		
		robotvrai = (RobotReal)container.getService("RobotVrai");
		robotvrai.setPosition(new Vec2(1484, 1000));	// TODO : cette position doit être la position de départ du robot 
		//On démarre avec les cales !!!!
		robotvrai.setOrientation((float)(-Math.PI/2));
		robotvrai.set_vitesse(Speed.BETWEEN_SCRIPTS);
		container.demarreThreads();
		
	}
	
	
	@Test
	public void test_bidon() throws Exception
	{
		robotvrai.avancer(100);
		robotvrai.avancer(-100);
	}

}
