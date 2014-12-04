package tests;

import java.util.ArrayList;

import smartMath.Vec2;

import org.junit.Before;
import org.junit.Test;

import exceptions.serial.SerialException;
import robot.cards.ActuatorsManager;
import robot.cards.Locomotion;
import utils.Sleep;

public class JUnit_serialMatch extends JUnit_Test {

	Locomotion locomotion;
	ActuatorsManager actionneurs;
	ArrayList<Vec2> path = new ArrayList<Vec2>();
	
	
	@Before
	public void setUp() throws Exception {
		super.setUp();
		locomotion = (Locomotion)container.getService("Deplacements");
		actionneurs = (ActuatorsManager)container.getService("Actionneurs");
		locomotion.set_x(1484);
		locomotion.set_y(1000);
		path.add(new Vec2 (1084,1000));
		path.add(new Vec2 (500,1000));
		path.add(new Vec2 (100,700));
		path.add(new Vec2 (500,500));
		path.add(new Vec2 (1420,250));
		path.add(new Vec2 (700,250));
	}

	@Test
	public void test()
	{
		try 
		{
			locomotion.followPath(path);
		} 
		catch (SerialException e) 
		{
			log.debug("erreur dans le texte serie",this);
			e.printStackTrace();
		}
		
	}

}
