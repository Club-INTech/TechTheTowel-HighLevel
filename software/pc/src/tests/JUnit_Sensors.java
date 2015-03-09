package tests;

import hook.Hook;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

import enums.SensorNames;
import enums.ServiceNames;
import robot.Locomotion;
import robot.cardsWrappers.SensorsCardWrapper;
import smartMath.Vec2;

// TODO: Auto-generated Javadoc
/**
 * Test des capteurs : les obstacles doivent être détectés
 * TODO : comprendre l'utilité du test desactivation_capteur et faux_test.
 *
 * @author marsu
 */
public class JUnit_Sensors extends JUnit_Test
{

	/** The capteurs. */
	SensorsCardWrapper capteurs;
	
	private Locomotion mLocomotion;
	
	/* (non-Javadoc)
	 * @see tests.JUnit_Test#setUp()
	 */
	@Before
	public void setUp() throws Exception {
		super.setUp();
		
		log.debug("JUnit_ActionneursTest.setUp()", this);
		capteurs = (SensorsCardWrapper)container.getService(ServiceNames.SENSORS_CARD_WRAPPER);
		
		config.set("capteurs_on", "true");
		capteurs.updateConfig();
		
		container.startAllThreads();
		
		//locomotion
		mLocomotion = (Locomotion)container.getService(ServiceNames.LOCOMOTION);
		config.set("couleur", "vert");
		mLocomotion.updateConfig();
		mLocomotion.setPosition(new Vec2 (1381,1000));
		mLocomotion.setOrientation(Math.PI);
		mLocomotion.setTranslationnalSpeed(170);
		mLocomotion.setRotationnalSpeed(160);
	}

	/**
	 * Desactivation_capteur.
	 *
	 * @throws Exception the exception
	 */
//	@Test
	public void desactivation_capteur() throws Exception
	{
		log.debug("JUnit_CapteursTest.desactivation_capteur()", this);

		// Avec capteurs
		log.debug(((int[])capteurs.getSensorValue(SensorNames.ULTRASOUND_FRONT_SENSOR))[1], this);
	//	Assert.assertTrue(capteurs.mesurer_infrarouge() != 3000);
		Assert.assertTrue(((int[])capteurs.getSensorValue(SensorNames.ULTRASOUND_FRONT_SENSOR))[1] != 3000);

		// Sans capteurs
		config.set("capteurs_on", "false");
		capteurs.updateConfig();
		log.debug(((int[])capteurs.getSensorValue(SensorNames.ULTRASOUND_FRONT_SENSOR))[1], this);
	//	Assert.assertTrue(capteurs.mesurer_infrarouge() == 3000);
		Assert.assertTrue(((int[])capteurs.getSensorValue(SensorNames.ULTRASOUND_FRONT_SENSOR))[1] == 3000);

		// Et re avec
		config.set("capteurs_on", "true");
		capteurs.updateConfig();
		Assert.assertTrue(((int[])capteurs.getSensorValue(SensorNames.ULTRASOUND_FRONT_SENSOR))[1] != 3000);
	//	Assert.assertTrue(capteurs.mesurer_infrarouge() != 3000);
		Assert.assertTrue(((int[])capteurs.getSensorValue(SensorNames.ULTRASOUND_FRONT_SENSOR))[1] != 3000);

	}
	
	@Test
	public void testEvitement() throws Exception
	{
		log.debug("Test d'évitement", this);

		mLocomotion.moveLengthwise(500,  new ArrayList<Hook>(), false);
	}

/*    @Test
    public void faux_test() throws Exception
    {
        config.set("capteurs_on", true);
        for(int i = 0; i < 10000; i++)
        {
            System.out.println(capteurs.mesurer_ultrason());
            Sleep.sleep(100);
        }
    }*/
}
