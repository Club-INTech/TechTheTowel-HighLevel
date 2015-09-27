package tests;

import hook.Hook;
import hook.types.HookFactory;

import java.util.ArrayList;

import scripts.ScriptManager;
import strategie.GameState;
import table.Table;

import org.junit.Before;
import enums.*;
import exceptions.serial.SerialConnexionException;
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
			log.debug( e.logStack());
		}		
	}
}
