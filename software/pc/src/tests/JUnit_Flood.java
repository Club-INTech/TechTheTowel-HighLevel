package tests;


import enums.ContactSensors;
import enums.ServiceNames;
import org.junit.Before;
import org.junit.Test;
import robot.Locomotion;
import robot.cardsWrappers.SensorsCardWrapper;
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

	private SensorsCardWrapper mSensors;

	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		log.debug("JUnit_DeplacementsTest.setUp()");
		mLocomotion = (Locomotion)container.getService(ServiceNames.LOCOMOTION);
		mSensors = (SensorsCardWrapper)container.getService(ServiceNames.SENSORS_CARD_WRAPPER);
		mLocomotion.updateConfig();
		mLocomotion.setPosition(new Vec2(-123, 456));
		mLocomotion.setOrientation( Math.PI);
		container.getService(ServiceNames.THREAD_SENSOR);
		container.startInstanciedThreads();
	}

	@Test
	public void testFlood() throws Exception
	{
		int compt=0;
		while(true)
		{
			//log.debug(compt++);
			try{
				log.debug(mLocomotion.getOrientation());
				mSensors.getContactSensorValue(ContactSensors.DOOR_CLOSED);

			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			
		}
	}	
	
}

