package tests;

import hook.Hook;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import robot.Robot;
import robot.cardsWrappers.SensorsCardWrapper;
import scripts.AbstractScript;
import scripts.ScriptManager;
import smartMath.Vec2;
import strategie.GameState;
import enums.ActuatorOrder;
import enums.ScriptNames;
import enums.ServiceNames;
import exceptions.PathNotFoundException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialConnexionException;
import exceptions.serial.SerialFinallyException;

public class JUnit_DropGlass extends JUnit_Test {

	ArrayList<Hook> emptyHook;
	GameState<Robot> real_state;
	ScriptManager scriptmanager;
	SensorsCardWrapper  mSensorsCardWrapper;
	ArrayList<Integer> listToGrab = new ArrayList<Integer>();

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception 
	{
		super.setUp();
		real_state = (GameState<Robot>) container.getService(ServiceNames.GAME_STATE);
		scriptmanager = (ScriptManager) container.getService(ServiceNames.SCRIPT_MANAGER);
		mSensorsCardWrapper = (SensorsCardWrapper) container.getService(ServiceNames.SENSORS_CARD_WRAPPER);
		emptyHook = new ArrayList<Hook> ();
		
		if (config.getProperty("couleur").equals("jaune"))
		{
			real_state.robot.setPosition(new Vec2 (-1381,1000));
			//On est jaune donc on est en 0 
			real_state.robot.setOrientation(0); 
		}
		else
		{
			real_state.robot.setPosition(new Vec2 (1381,1000));
			//On est vert donc on est en PI
			real_state.robot.setOrientation(Math.PI);
		}
		real_state.robot.updateConfig();
	}
	
	@Test
	public void test()
	{
		//on sort de la zone de depart
		try 
		{
			AbstractScript exitScript = scriptmanager.getScript(ScriptNames.EXIT_START_ZONE);
			exitScript.execute(0, real_state, emptyHook, true );
		} 
		catch (SerialConnexionException  e) 
		{
			log.critical("Carte mal branchée. Match termine", this);
			e.printStackTrace();
			return;
		}
		catch (UnableToMoveException e) 
		{
			log.critical("CRITICAL : Chemin bloque, enlevez votre main", this);
			e.printStackTrace();
		}
		
		// libère le verre
		try 
		{
			scriptmanager.getScript(ScriptNames.DROP_GLASS).goToThenExec(1, real_state, true, emptyHook );
			
			scriptmanager.getScript(ScriptNames.DROP_GLASS).goToThenExec(2, real_state, true, emptyHook );
			
			//On se degage, tant qu'il n'y a pas de PDD
			real_state.robot.moveLengthwise(-200, emptyHook, true);
			real_state.robot.turn(Math.PI*(-1/2));
			real_state.robot.moveLengthwise(600, emptyHook, true);
			
			scriptmanager.getScript(ScriptNames.DROP_GLASS).goToThenExec(3, real_state, true, emptyHook );
		}
		catch (UnableToMoveException e) 
		{
			log.critical("CRITICAL : Chemin bloque, enlevez votre main", this);
			e.printStackTrace();
		} 
		catch (SerialConnexionException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (PathNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (SerialFinallyException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
