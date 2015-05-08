package tests;

import graphics.Window;
import hook.Hook;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Random;

import smartMath.Vec2;
import strategie.GameState;
import table.Table;

import org.junit.Before;
import org.junit.Test;

import enums.ObstacleGroups;
import enums.ServiceNames;
import exceptions.*;
import exceptions.Locomotion.UnableToMoveException;
import robot.*;
import robot.cardsWrappers.ActuatorCardWrapper;
import pathDingDing.PathDingDing;

import threads.ThreadTimer;

public class JUnit_serialPathfinding extends JUnit_Test {

	GameState<Robot> state;
	ActuatorCardWrapper actionneurs;
	ArrayList<Vec2> path = new ArrayList<Vec2>();
	Random rand = new Random();
	ArrayList<Hook> emptyHook = new ArrayList<Hook>();
	Table table;
	PathDingDing pf;
	private Locomotion mLocomotion;
	Window win;
		
	
	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		state = (GameState<Robot>)container.getService(ServiceNames.GAME_STATE);
		
		win = ((ThreadTimer)container.getService(ServiceNames.THREAD_TIMER)).window;
		
		//locomotion
		mLocomotion = (Locomotion)container.getService(ServiceNames.LOCOMOTION);
		config.set("couleur", "vert");
		mLocomotion.updateConfig();
		mLocomotion.setPosition(new Vec2 (1381,1000));
		mLocomotion.setOrientation(Math.PI);
		mLocomotion.setTranslationnalSpeed(170);
		mLocomotion.setRotationnalSpeed(160);
		
		container.startInstanciedThreads();
		
        table = (Table)container.getService(ServiceNames.TABLE);
        pf = (PathDingDing)container.getService(ServiceNames.PATHDINGDING);
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
		System.out.println("en positiWindowon ("+robot.getPosition().x+", "+robot.getPosition().y+")");
		while (true)
		{
			int randX = rand.nextInt(3000)-1500;
			int randY = rand.nextInt(2000);
			try 
			{
				//TOTO : adapter au nouveau pf
				path = pf.computePath(robot.getPosition(), new Vec2(randX,randY), EnumSet.noneOf(ObstacleGroups.class));
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
				if(win.getMouse().hasClicked())
					table.getObstacleManager().addObstacle(win.getMouse().getMiddleClickPosition());
				path = pf.computePath(robot.getPosition(), win.getMouse().getLeftClickPosition(), EnumSet.of(ObstacleGroups.ENNEMY_ROBOTS));
				robot.followPath(path, emptyHook);
			}
			catch (PathNotFoundException e) 
			{
				log.debug("point en dehors de la table : ("+win.getMouse().getLeftClickPosition().x+", "+win.getMouse().getLeftClickPosition().y+")", this);
			}
			catch (InObstacleException e) 
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
