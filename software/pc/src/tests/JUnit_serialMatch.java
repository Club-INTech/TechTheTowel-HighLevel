package tests;

import java.util.ArrayList;

import smartMath.Vec2;
import table.Table;

import org.junit.Before;
import org.junit.Test;

import exceptions.serial.SerialException;
import robot.cards.ActuatorsManager;
import robot.cards.Locomotion;
import utils.Sleep;
import pathdinding.Pathfinding;

public class JUnit_serialMatch extends JUnit_Test {

	Locomotion locomotion;
	ActuatorsManager actionneurs;
	ArrayList<Vec2> path = new ArrayList<Vec2>();
	Table table;
	Pathfinding pathfinding = new Pathfinding (table);
	
	
	@Before
	public void setUp() throws Exception {
		super.setUp();
		locomotion = (Locomotion)container.getService("Deplacements");
		locomotion.set_x(1200);
		locomotion.set_y(200);
		actionneurs = (ActuatorsManager)container.getService("Actionneurs");
		table = (Table)container.getService("Table");
		pathfinding = new Pathfinding (table);
		
	}

	@Test
	public void test()
	{
		try 
		{
			path = pathfinding.computePath(new Vec2((int)locomotion.get_infos_x_y_orientation()[0],(int)locomotion.get_infos_x_y_orientation()[1]), new Vec2(-1200,200));
			log.debug(path.toString(), this);
			locomotion.followPath(path);
		} 
		catch (SerialException e) 
		{
			log.debug("erreur dans le texte serie",this);
			e.printStackTrace();
		}
		
	}

}
