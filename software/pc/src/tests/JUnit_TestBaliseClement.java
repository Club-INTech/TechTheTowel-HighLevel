package tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Random;

import org.junit.Test;
import java.lang.System;
import enums.ServiceNames;
import exceptions.PathNotFoundException;
import exceptions.PointInObstacleException;
import exceptions.Locomotion.UnableToMoveException;
import hook.Hook;
import robot.Robot;
import robot.RobotChrono;
import scripts.ScriptManager;
import smartMath.Vec2;
import strategie.GameState;
import table.Table;

public class JUnit_TestBaliseClement extends JUnit_Test
{

	GameState<Robot> clement;
	Table table;
	@SuppressWarnings("unchecked")
	public void setUp() throws Exception
	{
		//creation des objets pour le test
		super.setUp();                                                                                                                                 
		clement = (GameState<Robot>)container.getService(ServiceNames.GAME_STATE);
		
		//position initiale du robot
		clement.robot.setPosition(Table.entryPosition);
		clement.robot.setOrientation(Math.PI);
		
		table = (Table)container.getService(ServiceNames.TABLE);
	}	
		
	@Test
	private void test() {
		fail("Not yet implemented");
	}
	
	/**
	 * La m�thode NextPoint renvoie un point g�n�r� al�atoirement
	 * @return un point sur la table ([-1500,1500],[0,2000])
	 */
	private Vec2 NextPoint() 
	{
		Random random = new Random();
		return(new Vec2(random.nextInt(3001)-1500,random.nextInt(2001)));
	}
		
	public void run()
	{
		long time = System.currentTimeMillis();
		while((System.currentTimeMillis()-time)<90000)
		{
			try {
				clement.robot.moveToLocation(NextPoint(),new ArrayList<Hook>(), table);
			} 
			catch (PathNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			catch (UnableToMoveException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (PointInObstacleException e) {
			}
		}
	}
}