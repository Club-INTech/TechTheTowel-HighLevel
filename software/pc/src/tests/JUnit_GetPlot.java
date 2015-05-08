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
import exceptions.InObstacleException;
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

	/**
	 * les hooks pour le test
	 */
	ArrayList<Hook> emptyHook;
	/**
	 * le gameState reel pur le test
	 */
	GameState<Robot> real_state;
	/**
	 * pour obtenir les scripts
	 */
	ScriptManager scriptmanager;
	SensorsCardWrapper  mSensorsCardWrapper;
	/**
	 * liste des plots a attraper
	 */
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
	
	

	@Test
	public void test() throws InObstacleException
	{
		try 
		{
			AbstractScript exitScript = scriptmanager.getScript(ScriptNames.EXIT_START_ZONE);
			exitScript.execute(0, real_state, emptyHook );
		} 
		catch (SerialConnexionException | SerialFinallyException | UnableToMoveException e) 
		{
			System.out.println("CRITICAL : Carte mal branchée. Match termine");
			e.printStackTrace();
			return;
		}
		
		System.out.println("debut du match");
		try 
		{
			scriptmanager.getScript(ScriptNames.GRAB_PLOT).goToThenExec(56, real_state, emptyHook );
		}
		catch (UnableToMoveException | SerialConnexionException e) 
		{
			// un robot ennemi devant ?
			e.printStackTrace();
		
		} 
		catch (PathNotFoundException e)
		{
			//TODO: le pathfinding ne trouve pas de chemin
			log.critical( e.logStack(), this);
		} 
		catch (SerialFinallyException e) 
		{
			// TODO Auto-generated catch block
			log.critical( e.logStack(), this);
		}
		
		System.out.println("match fini !");


		//Le match s'arrête
		container.destructor();
	}
}
