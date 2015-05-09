package tests;

import hook.Callback;
import hook.Hook;
import hook.methods.*;
import hook.types.HookFactory;

import java.util.ArrayList;

import scripts.ScriptManager;
import smartMath.Vec2;
import strategie.GameState;
import table.Table;

import org.junit.Before;
import org.junit.Test;

import enums.*;
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
	
	@SuppressWarnings("unchecked")
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
			log.debug( e.logStack(), this);
		}		
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
			log.critical( e.logStack(), this);
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
			log.critical( e.logStack(), this);
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
			Hook testHook3 = hookFactory.newHookXisLesser(-400, 10);
			
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
			log.critical( e.logStack(), this);
		}		
		System.out.println("match fini !");

		//Le match s'arrête
		container.destructor();
	}
	
	//@Test
	public void testHookJawSensor() throws PathNotFoundException, SerialFinallyException, ContainerException, SerialManagerException, SerialConnexionException
	{
		//container.startAllThreads();
		//premiere action du match
		
		real_state.robot.setLocomotionSpeed(Speed.SLOW);
		
		real_state.robot.setPosition(new Vec2(0, 0));
		
		System.out.println("Le robot commence le match");
		try 
		{
			//hookfactory qui contient les differents hooks
			hookFactory = (HookFactory) container.getService(ServiceNames.HOOK_FACTORY);

			// liste de hook a passer a la locomotion
			ArrayList<Hook> testHookList = new ArrayList<Hook> ();
			
			Hook testHook = hookFactory.newHookJawSensor();
			
			// ajoute un callback au hook de position qui ouvre le bras  bras
			testHook.addCallback(	new Callback(new OpenRightArmExe(),true, real_state)	);
			
			// ajoute le hook a la liste a passer a la locomotion
			testHookList.add(testHook);
			
			//scriptmanager.getScript(ScriptNames.EXIT_START_ZONE).execute(0, real_state, testHookList, true );
			
			System.out.println("debut du mouvement");
			real_state.robot.useActuator(ActuatorOrder.ELEVATOR_GROUND, true);
			real_state.robot.useActuator(ActuatorOrder.ELEVATOR_OPEN_JAW, true);
			real_state.robot.moveLengthwise(300, testHookList);
			System.out.println("fin du mouvement");
		}
		catch (UnableToMoveException e) 
		{
			log.critical( e.logStack(), this);
		}		
		System.out.println("match fini !");

		//Le match s'arrête
		container.destructor();
	}
	
}
