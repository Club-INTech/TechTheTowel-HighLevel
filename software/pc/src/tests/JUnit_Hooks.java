package tests;

import hook.Callback;
import hook.Hook;
import hook.methods.*;
import hook.types.HookFactory;

import java.util.ArrayList;

import scripts.ScriptManager;
import strategie.GameState;
import table.Table;

import org.junit.Before;
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
			matchSetUp(real_state.robot, false);
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
			testHookX.addCallback(	new Callback(new OpenClapLeftHighExe(log),true, real_state)	);
			
			// ajoute le hook a la liste a passer a la locomotion
			testHookList.add(testHookX);
			
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
}
