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
import threads.ThreadTimer;
import enums.ActuatorOrder;
import enums.ScriptNames;
import enums.ServiceNames;
import exceptions.InObstacleException;
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
		waitMatchBegin();
	}
	public void waitMatchBegin()
	{

		System.out.println("Robot pret pour le match, attente du retrait du jumper");
		
		// attends que le jumper soit retiré du robot
		
		boolean jumperWasAbsent = mSensorsCardWrapper.isJumperAbsent();
		while(jumperWasAbsent || !mSensorsCardWrapper.isJumperAbsent())
		{
			jumperWasAbsent = mSensorsCardWrapper.isJumperAbsent();
			 real_state.robot.sleep(100);
		}

		// maintenant que le jumper est retiré, le match a commencé
		ThreadTimer.matchStarted = true;
	}
	
	@Test
	public void test() throws InObstacleException
	{
		//on sort de la zone de depart
		try 
		{
			AbstractScript exitScript = scriptmanager.getScript(ScriptNames.EXIT_START_ZONE);
			exitScript.execute(0, real_state, emptyHook );
		} 
		catch (SerialConnexionException  | SerialFinallyException e) 
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
