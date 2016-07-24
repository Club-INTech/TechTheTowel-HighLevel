package tests;

import container.Container;
import enums.ServiceNames;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialConnexionException;
import hook.Hook;
import org.junit.After;
import org.junit.Before;
import robot.Robot;
import robot.serial.SerialWrapper;
import smartMath.Vec2;
import strategie.GameState;
import table.Table;
import threads.ThreadTimer;
import utils.Config;
import utils.Log;

import java.util.ArrayList;

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
	

	
	public void waitMatchBegin(SerialWrapper sensorsCard, Robot robot)
	{

		System.out.println("RobotC pret pour le match, attente du retrait du jumper");
		
		// attends que le jumper soit retiré du robot

		while(sensorsCard.isJumperAbsent())
		{
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		while(!sensorsCard.isJumperAbsent())
		{
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// maintenant que le jumper est retiré, le match a commencé
		log.critical("Jumper Retiré ! ");
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
	
	public void returnToEntryPosition(GameState state) throws UnableToMoveException
	{
		state.robot.moveToLocation(new Vec2(Table.entryPosition.x-100, Table.entryPosition.y),new ArrayList<Hook>(), state.table);
		state.robot.turn(Math.PI);
		state.robot.moveLengthwise(-100);
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
