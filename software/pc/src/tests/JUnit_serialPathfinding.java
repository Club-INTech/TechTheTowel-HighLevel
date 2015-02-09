package tests;

import graphics.Window;
import hook.Hook;

import java.util.ArrayList;
import java.util.Random;

import smartMath.Vec2;
import strategie.GameState;
import table.Table;

import org.junit.Before;
import org.junit.Test;

import enums.ServiceNames;
import exceptions.PathNotFoundException;
import exceptions.Locomotion.UnableToMoveException;
import robot.Robot;
import robot.RobotReal;
import robot.cardsWrappers.ActuatorCardWrapper;
import pathDingDing.PathDingDing;

public class JUnit_serialPathfinding extends JUnit_Test {

	GameState<Robot> state;
	ActuatorCardWrapper actionneurs;
	ArrayList<Vec2> path = new ArrayList<Vec2>();
	Random rand = new Random();
	ArrayList<Hook> emptyHook = new ArrayList<Hook>();	
	Window win;
	Table table;
	PathDingDing pf;
		
	
	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		state = ((GameState<Robot>)container.getService(ServiceNames.GAME_STATE));
		actionneurs = (ActuatorCardWrapper)container.getService(ServiceNames.ACTUATOR_CARD_WRAPPER);


		if (state.robot.getSymmetry())
		{
			state.robot.setPosition(new Vec2 (-1381,1000));
			state.robot.setOrientation(0); 
			//si on est jaune on est en 0 
		}
		else
		{
			state.robot.setPosition(new Vec2 (1381,1000));
			state.robot.setOrientation(Math.PI);
			//sinon on est vert donc on est en PI
		}
		
        table = (Table)container.getService(ServiceNames.TABLE);
        win = new Window(table/*, (RobotReal)container.getService(ServiceNames.ROBOT_REAL)*/);
        pf = (PathDingDing)container.getService(ServiceNames.PATHDINGDING);
		
		state.robot.updateConfig();
	}

	//@Test
	public void RandomTest()
	{
		Robot robot = state.robot;
		try 
		{
			state.robot.moveLengthwise(1000);
		}
		catch (UnableToMoveException e) 
		{
			log.debug("impossible de bouger", this);
		}
		robot.sleep(3000);
		System.out.println("en position ("+robot.getPosition().x+", "+robot.getPosition().y+")");
		while (true)
		{
			int randX = rand.nextInt(3000)-1500;
			int randY = rand.nextInt(2000);
			try 
			{
				//TOTO : adapter au nouveau pf
				path = pf.computePath(robot.getPosition(), new Vec2(randX,randY));
				log.debug("chemin : "+path.toString(),this);
				path.remove(0);
				robot.followPath(path, emptyHook);
				robot.sleep(5000);
			}
			catch (PathNotFoundException e) 
			{
				log.debug("pas de chemin vers ("+randX+", "+randY+")", this);
			}
			catch (UnableToMoveException e) 
			{
				log.debug("chemin bloque", this);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			log.debug("en position ("+robot.getPosition().x+", "+robot.getPosition().y+")", this);
		}
		
	}
	
	@Test
	public void ClickedTest()
	{
		Robot robot = state.robot;
		try 
		{
			state.robot.moveLengthwise(600);
		}
		catch (UnableToMoveException e) 
		{
			log.debug("impossible de bouger", this);
		}
		System.out.println("en position ("+robot.getPosition().x+", "+robot.getPosition().y+")");
		while (true)
		{
			try
			{
				path = pf.computePath(robot.getPosition(), win.getMouse().getLeftClickPosition());
				log.debug("chemin : "+path.toString(),this);
				path.remove(0);
				System.out.println("----------------Nouveau chemin---------------");
				for(int i = 0; i < path.size(); i++)
					System.out.println("noeud : ("+path.get(i).x+", "+path.get(i).y+")");
				robot.followPath(path, emptyHook);
			}
			catch (PathNotFoundException e) 
			{
				log.debug("point en dehors de la table : ("+win.getMouse().getLeftClickPosition().x+", "+win.getMouse().getLeftClickPosition().y+")", this);
			}
			catch (UnableToMoveException e) 
			{
				log.debug("chemin bloque", this);
			}
			log.debug("en position ("+robot.getPosition().x+", "+robot.getPosition().y+")", this);
		}
		
	}
}
