package tests;

import hook.Hook;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import pathDingDing.PathDingDing;
import robot.Robot;
import robot.cardsWrappers.SensorsCardWrapper;
import scripts.AbstractScript;
import scripts.ScriptManager;
import smartMath.Vec2;
import strategie.GameState;
import table.Table;
import threads.ThreadTimer;
import enums.ActuatorOrder;
import enums.ObstacleGroups;
import enums.ScriptNames;
import enums.ServiceNames;
import exceptions.InObstacleException;
import exceptions.PathNotFoundException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialConnexionException;
import exceptions.serial.SerialFinallyException;

public class JUnit_DropPile extends JUnit_Test {

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


		real_state.robot.setPosition(Table.entryPosition);
		//On est vert donc on est en PI 
		real_state.robot.setOrientation(Math.PI); 
		real_state.robot.updateConfig();
		
		//initialisation en position des AX-12
		matchSetUp(real_state.robot);
		
		real_state.robot.isGlassStoredLeft=true;
		real_state.robot.isGlassStoredRight=true;

		waitMatchBegin(mSensorsCardWrapper, real_state.robot);
	}

	
	@Test
	public void test()
	{
		//on sort de la zone de depart
		try 
		{
			AbstractScript exitScript = scriptmanager.getScript(ScriptNames.EXIT_START_ZONE);
			exitScript.execute(0, real_state, emptyHook );
		} 
		catch (SerialConnexionException  | SerialFinallyException e) 
		{
			e.printStackTrace();
			return;
		}
		catch (UnableToMoveException e) 
		{
			e.printStackTrace();
			return;
		}
		
		// libère la pile de plots
		try 
		{
			scriptmanager.getScript(ScriptNames.FREE_STACK).goToThenExec(0, real_state, emptyHook );
		} 
		catch (InObstacleException e) 
		{
						for (ObstacleGroups obst : e.getObstacleGroup())
						{
							log.critical(obst.name(),this);
						}
		}
		catch (UnableToMoveException | SerialConnexionException e) 
		{
			e.printStackTrace();
		
		} 
		catch (PathNotFoundException e)
		{
			e.printStackTrace();
		} 
		catch (SerialFinallyException e) 
		{
			e.printStackTrace();
		}
	}
	
}
