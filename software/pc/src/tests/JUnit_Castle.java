package tests;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import enums.ScriptNames;
import enums.ServiceNames;
import enums.Speed;
import exceptions.BadVersionException;
import exceptions.ExecuteException;
import exceptions.PathNotFoundException;
import exceptions.PointInObstacleException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialConnexionException;
import exceptions.serial.SerialFinallyException;
import hook.Hook;
import robot.Robot;
import scripts.ScriptManager;
import smartMath.Vec2;
import strategie.GameState;
import table.Table;

public class JUnit_Castle extends JUnit_Test
{
	private GameState<Robot> mRobot;
	
	private ScriptManager scriptManager;
	
	private int versionToExecute = 0;
	
	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		mRobot = (GameState<Robot>)container.getService(ServiceNames.GAME_STATE);
		//La position de depart est mise dans le updateConfig()
		mRobot.updateConfig();

		scriptManager = (ScriptManager)container.getService(ServiceNames.SCRIPT_MANAGER);
		//Supprime la position de départ du robot dans le script des obstacles de la table.
		//A des fins de test uniquement !
		Vec2 entryPositionScriptCastle = scriptManager.getScript(ScriptNames.CASTLE).entryPosition(versionToExecute, mRobot.robot.getRobotRadius(), mRobot.robot.getPosition()).position;
	    mRobot.table.getObstacleManager().freePoint(entryPositionScriptCastle);
        
		mRobot.robot.setPosition(Table.entryPosition);
		mRobot.robot.setOrientation(Math.PI);
		mRobot.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);
		mRobot.robot.moveLengthwise(200);
		container.getService(ServiceNames.THREAD_INTERFACE);
		container.startInstanciedThreads();
	}
	
	@Test
	public void closeThatDoors() throws UnableToMoveException
	{
		ArrayList<Hook> emptyList = new ArrayList<Hook>();
		try
		{
			//On execute le script
			log.debug("Récupération du château !");
			scriptManager.getScript(ScriptNames.CASTLE).goToThenExec(versionToExecute, mRobot, emptyList);
		}
		catch(SerialConnexionException | BadVersionException | ExecuteException | SerialFinallyException e)
		{
			e.printStackTrace();
		} catch (PointInObstacleException e) 
		{
			e.printStackTrace();
		} catch (PathNotFoundException e) 
		{
			e.printStackTrace();
		}
	}

	@After
	public void finish() throws UnableToMoveException, PathNotFoundException, PointInObstacleException 
	{
//		super.returnToEntryPosition(mRobot);
	}

}
