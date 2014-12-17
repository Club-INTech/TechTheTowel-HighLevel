package tests;


import java.util.ArrayList;
import java.util.Random;

import hook.Hook;
import hook.types.HookFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import enums.ActuatorOrder;
import enums.ScriptNames;
import enums.ServiceNames;
import exceptions.PathNotFoundException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialConnexionException;
import robot.Robot;
import scripts.ScriptManager;
import smartMath.Vec2;
import strategie.GameState;
/**
 * test pour le script du depose tapis 
 * on suppose que le robot est place en (261,1410)
 * @author paul
 *
 */
public class JUnit_CarpetDropper extends JUnit_Test
{
	ScriptManager scriptManager;
	HookFactory hookFactory;
	GameState<Robot> game;
	ArrayList<Hook> emptyHook = new ArrayList<Hook>();

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception 
	{
		//creation des objets pour le test
		super.setUp();                                                                                                                                 
		hookFactory = (HookFactory)container.getService(ServiceNames.HOOK_FACTORY);
		scriptManager = (ScriptManager)container.getService(ServiceNames.SCRIPT_MANAGER);
		game = (GameState<Robot>)container.getService(ServiceNames.GAME_STATE);
		
		//position initiale du robot
		game.robot.setPosition(new Vec2(1381,1000));
		
		//positionnement du robot
		game.robot.useActuator(ActuatorOrder.RIGHT_CARPET_FOLDUP,false);
		game.robot.useActuator(ActuatorOrder.LEFT_CARPET_FOLDUP,true); 
		//on remonte les deux bras a tapis en meme temps
		game.robot.moveLengthwise(1000);
		//on sort de la zone de depart
		Random rand = new Random();
		game.robot.turn(rand.nextDouble());
		
	}

	@After
	public void tearDown() throws Exception 
	{
		game.robot.useActuator(ActuatorOrder.RIGHT_CARPET_FOLDUP,false);
		game.robot.useActuator(ActuatorOrder.LEFT_CARPET_FOLDUP,true);
		//on remonte les deux bras a tapis en meme temps
	}

	@Test
	public void test() 
	{
		log.debug("debut du depose tapis", this);
		try 
		{
			game.robot.moveLengthwise(120);
			game.robot.turn(Math.PI*-0.5);
			game.robot.moveLengthwise(-110);
			scriptManager.getScript(ScriptNames.DROP_CARPET).execute(1, game, false);
		} 
			catch (UnableToMoveException | SerialConnexionException e) 
		{
			e.printStackTrace();
		}
		log.debug("fin du depose tapis", this);
	}

}
