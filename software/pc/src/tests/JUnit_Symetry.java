package tests;

import enums.ServiceNames;
import hook.Hook;
import org.junit.Before;
import org.junit.Test;
import robot.serial.SerialWrapper;
import scripts.ScriptManager;
import smartMath.Vec2;
import strategie.GameState;

import java.util.ArrayList;

public class JUnit_Symetry extends JUnit_Test
{
	
	ArrayList<Hook> emptyHook;
	GameState real_state;
	ScriptManager scriptmanager;
	SerialWrapper serialWrapper;
	
	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		real_state = (GameState) container.getService(ServiceNames.GAME_STATE);
		scriptmanager = (ScriptManager) container.getService(ServiceNames.SCRIPT_MANAGER);
		serialWrapper = (SerialWrapper) container.getService(ServiceNames.SERIAL_WRAPPER);
		emptyHook = new ArrayList<Hook> ();  

		// La position est setée qu'on soit jaune ou vert
		real_state.robot.setPosition(new Vec2 (1103,1000));
		real_state.robot.setOrientation(Math.PI); 
		
		real_state.robot.updateConfig();	
	}
	

	//@Test
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
			log.critical(e);
		}
		
		
	}
	
	/**
	 * Verifie si le robot en a quelque chose à faire des plots ennemis 
	 */
	@Test 
	public void testObstaclesColor()
	{
		try 
		{
			real_state.robot.moveLengthwise(500);
			real_state.robot.moveToLocation(new Vec2(-500,800), emptyHook, real_state.table);
			real_state.robot.moveToLocation(new Vec2(-500,400), emptyHook, real_state.table);
		}
		catch (Exception e)
		{
			log.critical(e);
		}
	}
}
