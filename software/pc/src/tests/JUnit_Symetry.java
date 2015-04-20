package tests;

import hook.Hook;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import pathDingDing.PathDingDing;
import robot.Robot;
import robot.cardsWrappers.SensorsCardWrapper;
import scripts.ScriptManager;
import smartMath.Vec2;
import strategie.GameState;
import enums.ServiceNames;

public class JUnit_Symetry extends JUnit_Test
{
	
	ArrayList<Hook> emptyHook;
	GameState<Robot> real_state;
	ScriptManager scriptmanager;
	SensorsCardWrapper  mSensorsCardWrapper;
	PathDingDing pathDingDing;
	
	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		real_state = (GameState<Robot>) container.getService(ServiceNames.GAME_STATE);
		scriptmanager = (ScriptManager) container.getService(ServiceNames.SCRIPT_MANAGER);
		mSensorsCardWrapper = (SensorsCardWrapper) container.getService(ServiceNames.SENSORS_CARD_WRAPPER);
        pathDingDing = (PathDingDing)container.getService(ServiceNames.PATHDINGDING);
		emptyHook = new ArrayList<Hook> ();  

		// La position est setée qu'on soit jaune ou vert
		real_state.robot.setPosition(new Vec2 (1103,1000));
		real_state.robot.setOrientation(Math.PI); 
		
		real_state.robot.updateConfig();	
	}
	

	@Test
	public void testDeplacement()
	{
		try 
		{
			real_state.robot.moveLengthwise(100);
			real_state.robot.turn(Math.PI);
			real_state.robot.turn(-Math.PI/2);
			real_state.robot.turn(0);
		}
		catch (Exception e)
		{
			log.critical(e, this);
		}
		
		
	}
}
