package tests;

import hook.Callback;
import hook.Hook;
import hook.methods.*;
import hook.types.HookFactory;

import java.util.ArrayList;
import java.util.EnumSet;

import scripts.AbstractScript;
import scripts.ScriptManager;
import smartMath.Vec2;
import strategie.GameState;
import table.Table;

import org.junit.Before;
import org.junit.Test;

import enums.ActuatorOrder;
import enums.ObstacleGroups;
import enums.ScriptNames;
import enums.ServiceNames;
import exceptions.ContainerException;
import exceptions.PathNotFoundException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialConnexionException;
import exceptions.serial.SerialFinallyException;
import exceptions.serial.SerialManagerException;
import robot.Robot;
import robot.cardsWrappers.SensorsCardWrapper;

/**
 *  Classe de test pour les Hooks
 */


public class JUnit_Hooks extends JUnit_Test 
{
	ArrayList<Hook> emptyHook;
	GameState<Robot> real_state;
	ScriptManager scriptmanager;
	SensorsCardWrapper  mSensorsCardWrapper;
	HookFactory hookFactory;
	
	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		real_state = (GameState<Robot>) container.getService(ServiceNames.GAME_STATE);
		scriptmanager = (ScriptManager) container.getService(ServiceNames.SCRIPT_MANAGER);
		mSensorsCardWrapper = (SensorsCardWrapper) container.getService(ServiceNames.SENSORS_CARD_WRAPPER);


		real_state.robot.setPosition(Table.entryPosition);
		real_state.robot.setOrientation(Math.PI);
		
		real_state.robot.updateConfig();
		try 
		{
			matchSetUp(real_state.robot);
		} 
		catch (SerialConnexionException e) 
		{
			e.printStackTrace();
		}		
	}
	
	/**
	 * le set up du match en cours (mise en place des actionneurs)
	 * @param robot le robot a setuper
	 * @throws SerialConnexionException si l'ordinateur n'arrive pas a communiquer avec les cartes
	 */
	public void matchSetUp(Robot robot) throws SerialConnexionException
	{
		robot.useActuator(ActuatorOrder.ARM_LEFT_CLOSE, false);
		robot.useActuator(ActuatorOrder.ARM_RIGHT_CLOSE, false);
		robot.useActuator(ActuatorOrder.CLOSE_LEFT_GUIDE, false);
		robot.useActuator(ActuatorOrder.CLOSE_RIGHT_GUIDE, false);
		robot.useActuator(ActuatorOrder.LEFT_CARPET_FOLDUP, false);
		robot.useActuator(ActuatorOrder.RIGHT_CARPET_FOLDUP, false);
		robot.useActuator(ActuatorOrder.LOW_LEFT_CLAP, false);
		robot.useActuator(ActuatorOrder.LOW_RIGHT_CLAP, false);
		robot.useActuator(ActuatorOrder.ELEVATOR_CLOSE_JAW, false);
		robot.useActuator(ActuatorOrder.ELEVATOR_LOW, true);
	}

	//@Test
	public void test() throws PathNotFoundException, SerialFinallyException, ContainerException, SerialManagerException, SerialConnexionException
	{
		//container.startAllThreads();
		//premiere action du match
		
		System.out.println("Le robot commence le match");
		try 
		{
			//hookfactory qui contient les differents hooks
			hookFactory = (HookFactory) container.getService(ServiceNames.HOOK_FACTORY);

			// liste de hook a passer a la locomotion
			ArrayList<Hook> testHookList = new ArrayList<Hook> ();
			
			Hook testHookX = hookFactory.newHookX(500);
			
			// ajoute un callback au hook de position qui ouvre le bras  bras
			testHookX.addCallback(	new Callback(new OpenClapLeftHighExe(),true, real_state)	);
			
			// ajoute le hook a la liste a passer a la locomotion
			testHookList.add(testHookX);
			
			scriptmanager.getScript(ScriptNames.EXIT_START_ZONE).execute(0, real_state, testHookList );
			
			System.out.println("debut du mouvement");
			real_state.robot.moveLengthwise(1500, testHookList);
			real_state.robot.turn(0, testHookList, false);
			real_state.robot.moveLengthwise(1400, testHookList);
			System.out.println("fin du mouvement");
		}
		catch (UnableToMoveException e) 
		{
			e.printStackTrace();
		}		
		System.out.println("match fini !");

		//Le match s'arrête
		container.destructor();
	}

	//@Test
	public void testTakeGlass() throws PathNotFoundException, SerialFinallyException, ContainerException, SerialManagerException, SerialConnexionException
	{
		emptyHook = new ArrayList<Hook> ();  

		/*
		try
		{
			AbstractScript exitScript = scriptmanager.getScript(ScriptNames.EXIT_START_ZONE); // Sortie de la zone de depart
			exitScript.execute(0, real_state, emptyHook, true );
		} 
		catch (UnableToMoveException | SerialConnexionException e) 
		{
			e.printStackTrace();
		}*/
		
		hookFactory = (HookFactory) container.getService(ServiceNames.HOOK_FACTORY);

		// liste de hook a passer a la locomotion
		ArrayList<Hook> testHookList = new ArrayList<Hook> ();

		
	    Vec2 center = new Vec2(900,1000);
		
	    //TODO : à refaire
	    /*
		// hook pour ouvrir le bras dès que le robot est dans un cercle, à une precision près
		Hook takeGlassHook = hookFactory.newHookIsDistanceToPointLesserThan(100,center, 20);
		
		// ajoute un callback au hook de position qui ouvre le bras  bras
		takeGlassHook.addCallback(	new Callback(new OpenClapLeftHighExe(),true, real_state)	);
		
		// ajoute le hook a la liste a passer a la locomotion
		testHookList.add(takeGlassHook);
		*/
		
		try {
			real_state.robot.moveLengthwise(1500, testHookList);
		} 
		catch (UnableToMoveException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testHookClap() throws PathNotFoundException, SerialFinallyException, ContainerException, SerialManagerException, SerialConnexionException
	{
		//container.startAllThreads();
		//premiere action du match
		
		real_state.robot.setPosition(new Vec2(0, 0));
		
		System.out.println("Le robot commence le match");
		try 
		{
			//hookfactory qui contient les differents hooks
			hookFactory = (HookFactory) container.getService(ServiceNames.HOOK_FACTORY);

			// liste de hook a passer a la locomotion
			ArrayList<Hook> testHookList = new ArrayList<Hook> ();
			
			Hook testHook1 = hookFactory.newHookXisLesser(0, 10);
			Hook testHook2 = hookFactory.newHookXisLesser(-250, 10);
			Hook testHook3 = hookFactory.newHookXisLesser(-360, 10);
			
			// ajoute un callback au hook de position qui ouvre le bras  bras
			testHook1.addCallback(	new Callback(new OpenClapLeftMiddleExe(),true, real_state)	);
			testHook2.addCallback(	new Callback(new OpenClapLeftHighExe(),true, real_state)	);
			testHook3.addCallback(	new Callback(new OpenClapLeftMiddleExe(),true, real_state)	);
			
			// ajoute le hook a la liste a passer a la locomotion
			testHookList.add(testHook1);
			testHookList.add(testHook2);
			testHookList.add(testHook3);
			
			//scriptmanager.getScript(ScriptNames.EXIT_START_ZONE).execute(0, real_state, testHookList, true );
			
			System.out.println("debut du mouvement");
			real_state.robot.moveLengthwise(800, testHookList);
			System.out.println("fin du mouvement");
		}
		catch (UnableToMoveException e) 
		{
			e.printStackTrace();
		}		
		System.out.println("match fini !");

		//Le match s'arrête
		container.destructor();
	}
	
}
