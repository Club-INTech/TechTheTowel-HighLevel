package tests;

import org.junit.Before;
import org.junit.Test;
import exceptions.serial.SerialException;
import robot.cards.ActuatorsManager;
import robot.cards.Locomotion;
import utils.Sleep;

public class JUnit_serialMatch extends JUnit_Test {

	Locomotion locomotion;
	ActuatorsManager actionneurs;
	
	@Before
	public void setUp() throws Exception {
		super.setUp();
		locomotion = (Locomotion)container.getService("Deplacements");
		actionneurs = (ActuatorsManager)container.getService("Actionneurs");
	}

	@Test
	public void test()
	{
		try 
		{
			locomotion.avancer((1340-255));
			Sleep.sleep(2000);
			locomotion.tourner(0);
			Sleep.sleep(1500);
			locomotion.avancer(-340);
			Sleep.sleep(1000);
			actionneurs.lowLeftCarpet();
			Sleep.sleep(800);
			actionneurs.highLeftCarpet();
			actionneurs.lowRightCarpet();
			Sleep.sleep(800);
			actionneurs.highRightCarpet();
		} 
		catch (SerialException e) 
		{
			log.debug("erreur dans le texte serie",this);
			e.printStackTrace();
		}
	}

}
