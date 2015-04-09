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
		
		config.set("couleur", "vert");
		real_state.robot.setPosition(new Vec2 (1132,1000));
		//On est vert donc on est en PI 
		real_state.robot.setOrientation(Math.PI); 
		real_state.robot.updateConfig();
		
		//initialisation en position des AX-12
		matchSetUp(real_state.robot);
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
		
		// libère la pile de plots
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
