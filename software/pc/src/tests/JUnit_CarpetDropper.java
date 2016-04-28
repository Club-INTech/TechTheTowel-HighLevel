package tests;


import enums.ActuatorOrder;
import enums.ScriptNames;
import enums.ServiceNames;
import exceptions.BadVersionException;
import exceptions.BlockedActuatorException;
import exceptions.ExecuteException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.PathNotFoundException;
import exceptions.PointInObstacleException;
import exceptions.serial.SerialConnexionException;
import exceptions.serial.SerialFinallyException;
import hook.Hook;
import hook.types.HookFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import robot.Robot;
import scripts.ScriptManager;
import strategie.GameState;
import table.Table;

import java.util.ArrayList;
/**
 * test pour le script du depose tapis 
 * @author paul
 *
 */
public class JUnit_CarpetDropper extends JUnit_Test
{
	ScriptManager scriptManager;
	HookFactory hookFactory;
	GameState<Robot> game;
	ArrayList<Hook> emptyHook = new ArrayList<Hook>();

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception 
	{
		//creation des objets pour le test
		super.setUp();                                                                                                                                 
		scriptManager = (ScriptManager)container.getService(ServiceNames.SCRIPT_MANAGER);
		game = (GameState<Robot>)container.getService(ServiceNames.GAME_STATE);
		
		//position initiale du robot
		game.robot.setPosition(Table.entryPosition);
		game.robot.setOrientation(Math.PI);
		
		matchSetUp(game.robot, false);
	}

	@After
	public void tearDown() throws Exception 
	{
		//on remonte les deux bras a tapis en meme temps
		game.robot.useActuator(ActuatorOrder.STOP,false);
		game.robot.useActuator(ActuatorOrder.STOP,true);
	}

	@Test
	public void test() throws PointInObstacleException, BlockedActuatorException
	{
		log.debug("debut du depose tapis");
		try 
		{
			scriptManager.getScript(ScriptNames.DROP_CARPET).goToThenExec(0, game, emptyHook);
		} catch (ExecuteException | BadVersionException | UnableToMoveException | SerialConnexionException | PathNotFoundException | SerialFinallyException e) {
				e.printStackTrace();
			}
		log.debug("fin du depose tapis");
			try {
				returnToEntryPosition(game);
			} catch (PathNotFoundException | UnableToMoveException e) {
				e.printStackTrace();
			}
	}

}
