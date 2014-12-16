package tests;

import hook.Hook;

import java.util.ArrayList;
import java.util.Random;

import smartMath.Vec2;
import strategie.GameState;

import org.junit.Before;
import org.junit.Test;

import enums.ServiceNames;
import exceptions.PathNotFoundException;
import exceptions.Locomotion.UnableToMoveException;
import robot.Robot;
import robot.cardsWrappers.ActuatorCardWrapper;
import pathDingDing.PathDingDing;

public class JUnit_serialPathfinding extends JUnit_Test {

	GameState<Robot> state;
	ActuatorCardWrapper actionneurs;
	ArrayList<Vec2> path = new ArrayList<Vec2>();
	Random rand = new Random();
	ArrayList<Hook> emptyHook = new ArrayList<Hook>();	
		
	
	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		super.setUp();
		state = ((GameState<Robot>)container.getService(ServiceNames.GAME_STATE));
		actionneurs = (ActuatorCardWrapper)container.getService(ServiceNames.ACTUATOR_CARD_WRAPPER);


		
		state.robot.setPosition(new Vec2 (1381,1000));
		state.robot.setOrientation(Math.PI);
		
		
	}

	@Test
	public void test()
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
		int i=0;
		while (i<10)
		{
			int randX = rand.nextInt(3000)-1500;
			int randY = rand.nextInt(2000);
			try 
			{
				path = PathDingDing.computePath(robot.getPosition(), new Vec2(randX,randY), state.table);
				System.out.println(path.toString());
				robot.followPath(path, emptyHook);
				robot.sleep(1000);
			}
			catch (PathNotFoundException e) 
			{
				log.debug("pas de chemin vers ("+randX+", "+randY+")", this);
			}
			catch (UnableToMoveException e) 
			{
				log.debug("chemin bloque", this);
			}
			System.out.println("en position ("+robot.getPosition().x+", "+robot.getPosition().y+")");
			i++;
		}
		
	}

}
