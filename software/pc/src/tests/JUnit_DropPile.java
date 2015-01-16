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
		
		if (config.getProperty("couleur").equals("jaune"))
		{
			real_state.robot.setPosition(new Vec2 (-1381,1000));
			real_state.robot.setOrientation(0); 
			//si on est jaune on est en 0 
		}
		else
		{
			real_state.robot.setPosition(new Vec2 (1381,1000));
			real_state.robot.setOrientation(Math.PI);
			//sinon on est vert donc on est en PI
		}
		real_state.robot.updateConfig();
		
		//initialisation en position des AX-12
		try 
		{
			real_state.robot.useActuator(ActuatorOrder.ELEVATOR_CLOSE_JAW, true);
			real_state.robot.useActuator(ActuatorOrder.CLOSE_LEFT_GUIDE, false);
			real_state.robot.useActuator(ActuatorOrder.CLOSE_RIGHT_GUIDE, false);			
			real_state.robot.useActuator(ActuatorOrder.ELEVATOR_LOW, false);
			
		} 
		catch (SerialConnexionException e1) 
		{
			e1.printStackTrace();
		}
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
					System.out.println("CRITICAL : Carte mal branchée. Match termine");
					e.printStackTrace();
					return;
				}
				catch (UnableToMoveException e) 
				{
					System.out.println("CRITICAL : Chemin bloque, enlevez votre main");
					e.printStackTrace();
				}
				
				try 
				{
					scriptmanager.getScript(ScriptNames.FREE_STACK).goToThenExec(1, real_state, true, emptyHook );
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
	
}
