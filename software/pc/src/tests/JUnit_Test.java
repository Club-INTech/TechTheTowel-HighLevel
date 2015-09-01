package tests;

import hook.Hook;

import java.util.ArrayList;
import java.util.EnumSet;

import org.junit.Before;
import org.junit.After;

import robot.Robot;
import robot.cardsWrappers.SensorsCardWrapper;
import smartMath.Vec2;
import strategie.GameState;
import table.Table;
import threads.ThreadTimer;
import utils.Log;
import utils.Config;
import container.Container;
import enums.ObstacleGroups;
import enums.ServiceNames;
import exceptions.InObstacleException;
import exceptions.PathNotFoundException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialConnexionException;

/**
 * The Class JUnit_Test.
 */
public abstract class JUnit_Test
{

	/** The container. */
	protected Container container;
	
	/** The config. */
	protected Config config;
	
	/** The log. */
	protected Log log;
	
	/**
	 * Sets the up.
	 *
	 * @throws Exception the exception
	 */
	@Before
	public void setUp() throws Exception
	{
		container = new Container();
		config = (Config) container.getService(ServiceNames.CONFIG);
		log = (Log) container.getService(ServiceNames.LOG);
	}
	
	/**
	 * le set up du match en cours (mise en place des actionneurs)
	 * @param sensorsCard 
	 * @param robot le robot a setuper
	 * @throws SerialConnexionException si l'ordinateur n'arrive pas a communiquer avec les cartes
	 */
	

	
	public void waitMatchBegin(SensorsCardWrapper sensorsCard, Robot robot)
	{

		System.out.println("Robot pret pour le match, attente du retrait du jumper");
		
		// attends que le jumper soit retiré du robot
		
		boolean jumperWasAbsent = sensorsCard.isJumperAbsent();
		while(jumperWasAbsent || !sensorsCard.isJumperAbsent())
		{
			jumperWasAbsent = sensorsCard.isJumperAbsent();
			 robot.sleep(100);
		}

		// maintenant que le jumper est retiré, le match a commencé
		log.critical("Jumper Retiré ! ", this);
		ThreadTimer.matchStarted = true;
	}

	public void matchSetUp(Robot robot, boolean isInitialisationQuick) throws SerialConnexionException
	{
		//TODO init du robot
	}

	public void setBeginPosition(Robot robot)
	{
		robot.setPosition(Table.entryPosition);
	}
	
	public void returnToEntryPosition(GameState<Robot> state) throws PathNotFoundException, UnableToMoveException, InObstacleException
	{
		state.robot.moveToLocation(new Vec2(Table.entryPosition.x-250, Table.entryPosition.y),new ArrayList<Hook>(), state.table, EnumSet.noneOf(ObstacleGroups.class));
		state.robot.turn(Math.PI);
		state.robot.moveLengthwise(-250);
	}
	

	/**
	 * Tear down.
	 *
	 * @throws Exception the exception
	 */
	@After
	public void tearDown() throws Exception 
	{
		container.destructor();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
