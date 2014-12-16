package tests;


import java.util.ArrayList;
import java.util.Random;

import hook.Hook;
import hook.types.HookFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import enums.ActuatorOrder;
import enums.ServiceNames;
import exceptions.PathNotFoundException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialConnexionException;
import robot.Robot;
import robot.RobotReal;
import scripts.DropCarpet;
import smartMath.Vec2;
import strategie.GameState;
import table.Table;
/**
 * test pour le script du depose tapis 
 * on suppose que le robot est place en (261,1410)
 * @author paul
 *
 */
public class JUnit_CarpetDropper extends JUnit_Test
{
	DropCarpet scriptCarpet;
	RobotReal robot;
	Table table;
	HookFactory hookFactory;
	GameState<Robot> game;
	ArrayList<Hook> emptyHook = new ArrayList<Hook>();

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception 
	{
		//creation des objets pour le test
		super.setUp();                                                                                                                                 
		table = (Table)container.getService(ServiceNames.TABLE);
		robot = (RobotReal)container.getService(ServiceNames.ROBOT_REAL);
		hookFactory = (HookFactory)container.getService(ServiceNames.HOOK_FACTORY);
		scriptCarpet = new DropCarpet(hookFactory, config, log);
		game = (GameState<Robot>)container.getService(ServiceNames.GAME_STATE);
		
		//position initiale du robot
		robot.setPosition(new Vec2(1500-71-48,1000));
		
		//positionnement du robot
		robot.useActuator(ActuatorOrder.RIGHT_CARPET_FOLDUP,false);
		robot.useActuator(ActuatorOrder.LEFT_CARPET_FOLDUP,true); 
		//on remonte les deux bras a tapis en meme temps
		robot.moveLengthwise(1000);
		Random rand = new Random();
		robot.turn(rand.nextDouble());
		
	}

	@After
	public void tearDown() throws Exception 
	{
		robot.useActuator(ActuatorOrder.RIGHT_CARPET_FOLDUP,false);
		robot.useActuator(ActuatorOrder.LEFT_CARPET_FOLDUP,true);
		//on remonte les deux bras a tapis en meme temps
	}

	@Test
	public void test() 
	{
		log.debug("debut du depose tapis", this);
		try 
		{
			scriptCarpet.goToThenExec(1, game, false, emptyHook);
		} 
			catch (UnableToMoveException | SerialConnexionException e) 
		{
			e.printStackTrace();
		} catch (PathNotFoundException e) {
				e.printStackTrace();
			}

		log.debug("fin du depose tapis", this);
	}

}
