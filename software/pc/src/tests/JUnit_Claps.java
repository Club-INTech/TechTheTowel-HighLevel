package tests;

//@author Théo + architecture Paul


import java.util.ArrayList;

import hook.Hook;

import org.junit.Before;
import org.junit.Test;

import enums.ActuatorOrder;
import enums.ScriptNames;
import enums.ServiceNames;
import exceptions.InObstacleException;
import exceptions.PathNotFoundException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialConnexionException;
import exceptions.serial.SerialFinallyException;
import robot.Robot;
import scripts.ScriptManager;
import smartMath.Vec2;
import strategie.GameState;

public class JUnit_Claps extends JUnit_Test {
	
	GameState<Robot> real_state;
	ScriptManager scriptManager;
	ArrayList<Hook> emptyHook = new ArrayList<Hook>();
	
	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception 
	{
		super.setUp();
		real_state = (GameState<Robot>)container.getService(ServiceNames.GAME_STATE);
		scriptManager = (ScriptManager)container.getService(ServiceNames.SCRIPT_MANAGER);
		
		real_state.robot.setPosition(new Vec2 (1132,1000));
		real_state.robot.setOrientation(Math.PI);
		
		real_state.robot.updateConfig();
		try 
		{
			matchSetUp(real_state.robot);
		} 
		catch (SerialConnexionException e) 
		{
			e.printStackTrace();
		}		
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
	public void test() throws InObstacleException
	{
		try {
			 //le temps d'attente (en ms) entre la commande de dépose du tapis ( le bras se baisse) et la commande qui remonte le bras
	
			// A partir  de maintenant, le  robot estt au coin de la table
			scriptManager.getScript(ScriptNames.EXIT_START_ZONE).execute(1, real_state, emptyHook);

			try {
				scriptManager.getScript(ScriptNames.CLOSE_CLAP).goToThenExec(2, real_state, true, emptyHook);
			} catch (PathNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} catch (UnableToMoveException | SerialConnexionException | SerialFinallyException e) {
			log.debug("BUG !",this);
			e.printStackTrace();
		}
	}
}


