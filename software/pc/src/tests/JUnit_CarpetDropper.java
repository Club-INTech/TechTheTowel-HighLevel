package tests;


import java.util.ArrayList;

import hook.Hook;
import hook.types.HookFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import enums.ActuatorOrder;
import enums.ScriptNames;
import enums.ServiceNames;
import exceptions.ExecuteException;
import exceptions.InObstacleException;
import exceptions.PathNotFoundException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialConnexionException;
import exceptions.serial.SerialFinallyException;
import robot.Robot;
import scripts.ScriptManager;
import strategie.GameState;
import table.Table;
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
	public void test() 
	{
		log.debug("debut du depose tapis", this);
		try 
		{
			scriptManager.getScript(ScriptNames.DROP_CARPET).goToThenExec(0, game, emptyHook);
		} 
			catch (UnableToMoveException | SerialConnexionException | SerialFinallyException | PathNotFoundException | InObstacleException e) 
		{
			e.printStackTrace();
		} catch (ExecuteException e) {
				e.printStackTrace();
			}
		log.debug("fin du depose tapis", this);
		try 
		{
			returnToEntryPosition(game);
		}
		catch (PathNotFoundException | UnableToMoveException
				| InObstacleException e) 
		{
			e.printStackTrace();
		}
	}

}
