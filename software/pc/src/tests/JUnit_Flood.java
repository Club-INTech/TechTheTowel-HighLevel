package tests;



import org.junit.Before;
import org.junit.Test;

import enums.ServiceNames;
import robot.Locomotion;
import smartMath.Vec2;

/**
 * Test unitaire pour flooder la serie.
 *
 * @author Théo
 */
public class JUnit_Flood extends JUnit_Test
{
	/** The deplacements. */
	private Locomotion mLocomotion;

	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		log.debug("JUnit_DeplacementsTest.setUp()", this);
		mLocomotion = (Locomotion)container.getService(ServiceNames.LOCOMOTION);
		mLocomotion.updateConfig();
		mLocomotion.setPosition(new Vec2(-123, 456));
		mLocomotion.setOrientation( Math.PI);
	}

	@Test
	public void testFlood() throws Exception
	{
		int compt=0;
		while(true)
		{
			log.debug(compt++, this);
			Vec2 pos=mLocomotion.getPosition();
			log.debug(pos.x+" :/  ^_^  /: "+pos.y, this);
		}
	}	
	
}

