package tests;

import java.util.ArrayList;
import java.util.Random;

import scripts.ScriptManager;
import smartMath.Vec2;
import strategie.GameState;

import org.junit.Before;
import org.junit.Test;

import enums.ScriptNames;
import enums.ServiceNames;
import exceptions.Locomotion.UnableToMoveException;
import robot.Robot;

/**
 * classe des matchs scriptes.
 * sert de bases pour nimporte quel test
 */
public class JUnit_serialMatch extends JUnit_Test 
{

	GameState<Robot> state;
	ScriptManager scriptManager;
	ArrayList<Vec2> path = new ArrayList<Vec2>();
	Random rand = new Random();
	
		
	
	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception 
	{
		super.setUp();
		state=(GameState<Robot>) container.getService(ServiceNames.GAME_STATE);
		scriptManager=(ScriptManager)container.getService(ServiceNames.SCRIPT_MANAGER);
		
		state.robot.setPosition(new Vec2 (1381,1000));
		state.robot.setOrientation(Math.PI);
	}

	@Test
	public void test()
	{
			try 
			{
				state.robot.moveLengthwise(1000);
				while(true)
				{
					state.robot.moveLengthwise(1000);
					state.robot.turn(0);
					state.robot.moveLengthwise(1000);
					state.robot.turn(Math.PI);
				}
				//state.robot.moveLengthwise(1120);
				//state.robot.turn(-0.5*Math.PI);
				//state.robot.moveLengthwise(-110);
				//scriptManager.getScript(ScriptNames.DROP_CARPET).execute(1, state, true);
				
				//aller en () point d'entree de fermeture du clap 1-2
				//scriptManager.getScript(ScriptNames.CLOSE_CLAP).execute(12, state, true);
				//aller en () point d'entree de fermeture du clap 3
				//scriptManager.getScript(ScriptNames.CLOSE_CLAP).execute(3, state, true);
			} 
			catch (UnableToMoveException e) 
			{
				e.printStackTrace();
			}
	}
}
