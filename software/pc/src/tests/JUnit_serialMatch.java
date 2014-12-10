package tests;

import java.util.ArrayList;
import java.util.Random;

import smartMath.Vec2;
import table.Table;

import org.junit.Before;
import org.junit.Test;

import exceptions.Locomotion.BlockedException;
import exceptions.serial.SerialException;
import robot.cards.ActuatorsManager;
import robot.cards.Locomotion;
import utils.Sleep;
import pathDingDing.PathDingDing;

/**
 * classe des matchs scriptes.
 * sert de bases pour nimporte quel test
 */
public class JUnit_serialMatch extends JUnit_Test 
{

	Locomotion locomotion;
	ActuatorsManager actionneurs;
	ArrayList<Vec2> path = new ArrayList<Vec2>();
	Table table;
	PathDingDing pathfinding = new PathDingDing (table);
	Random rand = new Random();
	
		
	
	@Before
	public void setUp() throws Exception 
	{
		super.setUp();
		locomotion = (Locomotion)container.getService("Deplacements");
		locomotion.set_x(1484);
		locomotion.set_y(1000);
		actionneurs = (ActuatorsManager)container.getService("Actionneurs");
		table = (Table)container.getService("Table");
		pathfinding = new PathDingDing (table);
		
		
	}

	@Test
	public void test()
	{
		try 
		{
				for (int i=0 ; i<10 ; i++)
				{
					locomotion.avancer(1000);
					Sleep.sleep(5000);
					locomotion.avancer(-1000);
					Sleep.sleep(5000);
				}
			log.debug("en position ("+locomotion.get_infos_x_y_orientation()[0]+", "+(int)locomotion.get_infos_x_y_orientation()[1]+")", this);
		} 
		catch (SerialException e1) 
		{
			log.debug("mauvaise entree serie, test init", this);
			e1.printStackTrace();
		}
		/*while (true)
		{
			try 
			{
				int randX = rand.nextInt(3000)-1500;
				int randY = rand.nextInt(2000);
				try 
				{
					path = pathfinding.computePath(new Vec2((int)locomotion.get_infos_x_y_orientation()[0],(int)locomotion.get_infos_x_y_orientation()[1]), new Vec2(randX,randY));
					log.debug(path.toString(), this);
					locomotion.followPath(path);
					log.debug("en position ("+locomotion.get_infos_x_y_orientation()[0]+", "+(int)locomotion.get_infos_x_y_orientation()[1]+")", this);
				} 
				catch (BlockedException e) 
				{
					log.debug("pas de chemin("+randX+", "+randY+")", this);
				}
				
				
			} 
			catch (SerialException e) 
			{
				log.debug("erreur dans le texte serie",this);
				e.printStackTrace();
			}
		}
		*/
		
	}

}
