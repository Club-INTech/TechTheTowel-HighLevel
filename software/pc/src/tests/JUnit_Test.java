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
import utils.Sleep;
import container.Container;
import enums.ActuatorOrder;
import enums.ObstacleGroups;
import enums.ServiceNames;
import enums.Speed;
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
		ThreadTimer.matchStarted = true;
	}

	public void matchSetUp(Robot robot, boolean isInitialisationQuick) throws SerialConnexionException
	{
		
		if(isInitialisationQuick)
		{
			// Fermeture de tous  les actionneurs
			
			robot.useActuator(ActuatorOrder.CLOSE_RIGHT_GUIDE, true);
			robot.useActuator(ActuatorOrder.CLOSE_LEFT_GUIDE, false);
			
			robot.useActuator(ActuatorOrder.ARM_LEFT_CLOSE, false);
			robot.useActuator(ActuatorOrder.ARM_RIGHT_CLOSE, false);
			
			robot.useActuator(ActuatorOrder.LEFT_CARPET_FOLDUP, false);
			robot.useActuator(ActuatorOrder.RIGHT_CARPET_FOLDUP, false);
			
			// initialisation normale 
			robot.useActuator(ActuatorOrder.ELEVATOR_GROUND, true);
			
			robot.useActuator(ActuatorOrder.ELEVATOR_OPEN_JAW, false);
	
			robot.useActuator(ActuatorOrder.OPEN_LEFT_GUIDE, false);
			robot.useActuator(ActuatorOrder.OPEN_RIGHT_GUIDE, true);
			
			robot.useActuator(ActuatorOrder.ARM_LEFT_CLOSE, false);
			robot.useActuator(ActuatorOrder.ARM_RIGHT_CLOSE, false);
			
			robot.useActuator(ActuatorOrder.CLOSE_RIGHT_GUIDE, true);
			robot.useActuator(ActuatorOrder.CLOSE_LEFT_GUIDE, true);
	
	
			robot.useActuator(ActuatorOrder.HIGH_LEFT_CLAP, false);
			robot.useActuator(ActuatorOrder.HIGH_RIGHT_CLAP, false);
			
			robot.useActuator(ActuatorOrder.LEFT_CARPET_DROP, false);
			robot.useActuator(ActuatorOrder.RIGHT_CARPET_DROP, true);
			
			robot.useActuator(ActuatorOrder.LEFT_CARPET_FOLDUP, false);
			robot.useActuator(ActuatorOrder.RIGHT_CARPET_FOLDUP, false);
			
			robot.useActuator(ActuatorOrder.LOW_LEFT_CLAP, false);
			robot.useActuator(ActuatorOrder.LOW_RIGHT_CLAP, false);
			
			robot.useActuator(ActuatorOrder.ELEVATOR_CLOSE_JAW, true);
			
			robot.useActuator(ActuatorOrder.ELEVATOR_LOW, true);
			
			robot.setLocomotionSpeed(Speed.SLOW);
		}
		else
		{
			// Fermeture de tous  les actionneurs
			
			robot.useActuator(ActuatorOrder.CLOSE_RIGHT_GUIDE, true);
			robot.useActuator(ActuatorOrder.CLOSE_LEFT_GUIDE, true);
			
			robot.useActuator(ActuatorOrder.ARM_LEFT_CLOSE, true);
			robot.useActuator(ActuatorOrder.ARM_RIGHT_CLOSE, true);
			
			robot.useActuator(ActuatorOrder.LEFT_CARPET_FOLDUP, true);
			robot.useActuator(ActuatorOrder.RIGHT_CARPET_FOLDUP, true);
			
			// initialisation normale 

			robot.useActuator(ActuatorOrder.ARM_LEFT_MIDDLE, true);
			robot.useActuator(ActuatorOrder.ARM_RIGHT_MIDDLE, true);
			
			robot.useActuator(ActuatorOrder.ELEVATOR_GROUND, true);
			
			robot.useActuator(ActuatorOrder.ELEVATOR_OPEN_JAW, true);
	
			robot.useActuator(ActuatorOrder.OPEN_LEFT_GUIDE, true);
			robot.useActuator(ActuatorOrder.OPEN_RIGHT_GUIDE, true);
			
			
			robot.useActuator(ActuatorOrder.CLOSE_RIGHT_GUIDE, true);
			robot.useActuator(ActuatorOrder.CLOSE_LEFT_GUIDE, true);
	
	
			robot.useActuator(ActuatorOrder.HIGH_LEFT_CLAP, true);
			robot.useActuator(ActuatorOrder.HIGH_RIGHT_CLAP, true);
			
			robot.useActuator(ActuatorOrder.LEFT_CARPET_DROP, true);
			robot.useActuator(ActuatorOrder.RIGHT_CARPET_DROP, true);
			
			robot.useActuator(ActuatorOrder.LEFT_CARPET_FOLDUP, true);
			robot.useActuator(ActuatorOrder.RIGHT_CARPET_FOLDUP, true);

			robot.useActuator(ActuatorOrder.ARM_LEFT_CLOSE, true);
			robot.useActuator(ActuatorOrder.ARM_RIGHT_CLOSE, true);
						
			robot.useActuator(ActuatorOrder.LOW_LEFT_CLAP, true);
			robot.useActuator(ActuatorOrder.LOW_RIGHT_CLAP, true);
			
			robot.useActuator(ActuatorOrder.ELEVATOR_CLOSE_JAW, true);
			
			robot.useActuator(ActuatorOrder.ELEVATOR_LOW, true);
			
			robot.setLocomotionSpeed(Speed.SLOW);
		}
	}
	
	public void putTennisBall(Robot robot) throws SerialConnexionException
	{
		robot.useActuator(ActuatorOrder.ELEVATOR_GROUND, true);
		robot.useActuator(ActuatorOrder.ELEVATOR_OPEN_JAW, false);
		
		log.debug("Veuillez mettre la Balle de Tennis", this);
		Sleep.sleep(1500);
		
		robot.useActuator(ActuatorOrder.ELEVATOR_CLOSE_JAW, true);
		robot.useActuator(ActuatorOrder.ELEVATOR_LOW, true);
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
