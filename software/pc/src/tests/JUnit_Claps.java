package tests;

//@author Théo + architecture Paul


import java.util.ArrayList;

import hook.Hook;

import org.junit.Before;
import org.junit.Test;

import enums.ActuatorOrder;
import enums.ScriptNames;
import enums.ServiceNames;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialConnexionException;
import robot.Robot;
import scripts.ScriptManager;
import strategie.GameState;

public class JUnit_Claps extends JUnit_Test {
	
	GameState<Robot> real_state; //c'est bien un robot real a l'interieur ?
	ScriptManager scriptManager;
	
	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception 
	{
		super.setUp();
		real_state = (GameState<Robot>)container.getService(ServiceNames.GAME_STATE);
		scriptManager = (ScriptManager)container.getService(ServiceNames.SCRIPT_MANAGER);
	}
	
	@Test
	public void test()
	{
		try {
			 //le temps d'attente (en ms) entre la commande de dépose du tapis ( le bras se baisse) et la commande qui remonte le bras
			int sleepTime = 2500;
			
			
			real_state.robot.useActuator(ActuatorOrder.LOW_LEFT_CLAP, true);

			real_state.robot.moveLengthwise(500);
			
			real_state.robot.sleep(sleepTime);

			real_state.robot.turn(-Math.PI/2);
			real_state.robot.sleep(sleepTime);                              
			real_state.robot.moveLengthwise(750);       
			real_state.robot.sleep(sleepTime);
			
			real_state.robot.turn(Math.PI);
			real_state.robot.sleep(sleepTime);
			real_state.robot.moveLengthwise(-500);
			real_state.robot.sleep(sleepTime);
			// A partir  de maintenant, le  robot estt au coin de la table
			
			scriptManager.getScript(ScriptNames.CLOSE_CLAP).execute(12, real_state, true);
			
		} catch (UnableToMoveException | SerialConnexionException e) {
			log.debug("BUG !",this);
			e.printStackTrace();
		}
	}
}


