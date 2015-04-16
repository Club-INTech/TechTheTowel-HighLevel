package tests;
import hook.Hook;

import java.util.ArrayList;

import scripts.AbstractScript;
import scripts.ScriptManager;
import smartMath.Vec2;
import strategie.GameState;
import utils.Sleep;

import org.junit.Before;
import org.junit.Test;

import enums.ActuatorOrder;
import enums.ScriptNames;
import enums.ServiceNames;
import exceptions.PathNotFoundException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialConnexionException;
import exceptions.serial.SerialFinallyException;
import robot.Robot;
import robot.cardsWrappers.SensorsCardWrapper;

/**
 * 
 * @author paul
 *classe de test pour les plots, attrape les plots dans l'orde donne par l'utilisateur
 */
public class JUnit_GetPlot extends JUnit_Test 
{
	// TODO doc
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

		real_state.robot.setPosition(new Vec2 (1132,1000));
		real_state.robot.setOrientation(Math.PI);
		matchSetUp(real_state.robot);

		real_state.robot.updateConfig();
	}
	
	public void matchSetUp(Robot robot) throws SerialConnexionException
	{
		robot.useActuator(ActuatorOrder.ELEVATOR_OPEN_JAW, false);

		robot.useActuator(ActuatorOrder.OPEN_LEFT_GUIDE, false);
		robot.useActuator(ActuatorOrder.OPEN_RIGHT_GUIDE, true);
		
		robot.useActuator(ActuatorOrder.ARM_LEFT_CLOSE, false);
		robot.useActuator(ActuatorOrder.ARM_RIGHT_CLOSE, false);
		
		robot.useActuator(ActuatorOrder.CLOSE_RIGHT_GUIDE, true);
		robot.useActuator(ActuatorOrder.CLOSE_LEFT_GUIDE, true);
		
		robot.useActuator(ActuatorOrder.LEFT_CARPET_FOLDUP, false);
		robot.useActuator(ActuatorOrder.RIGHT_CARPET_FOLDUP, false);
		
		robot.useActuator(ActuatorOrder.LOW_LEFT_CLAP, false);
		robot.useActuator(ActuatorOrder.LOW_RIGHT_CLAP, false);
		
		robot.useActuator(ActuatorOrder.ELEVATOR_CLOSE_JAW, true);
		
		robot.useActuator(ActuatorOrder.ELEVATOR_LOW, true);
	}
	

	@Test
	public void test()
	{
		try 
		{
			AbstractScript exitScript = scriptmanager.getScript(ScriptNames.EXIT_START_ZONE);
			exitScript.execute(0, real_state, emptyHook, true );
		} 
		catch (SerialConnexionException | SerialFinallyException | UnableToMoveException e) 
		{
			System.out.println("CRITICAL : Carte mal branchée. Match termine");
			e.printStackTrace();
			return;
		}
		
		System.out.println("debut du match");
		
		//premier script
		for (int i=0; i<listToGrab.size(); i++)
		{
			try 
			{
				scriptmanager.getScript(ScriptNames.GRAB_PLOT).goToThenExec(1, real_state, true, emptyHook );
			}
			catch (UnableToMoveException | SerialConnexionException e) 
			{
				// un robot ennemi devant ?
				e.printStackTrace();
			
			} 
			catch (PathNotFoundException e)
			{
				//TODO: le pathfinding ne trouve pas de chemin
				e.printStackTrace();
			} 
			catch (SerialFinallyException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		System.out.println("match fini !");


		//Le match s'arrête
		container.destructor();
	}
}
