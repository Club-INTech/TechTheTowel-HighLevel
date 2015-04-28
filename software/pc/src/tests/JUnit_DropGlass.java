package tests;

import hook.Hook;

import java.util.ArrayList;
import java.util.EnumSet;

import org.junit.Before;
import org.junit.Test;

import robot.Robot;
import robot.cardsWrappers.SensorsCardWrapper;
import scripts.AbstractScript;
import scripts.ScriptManager;
import smartMath.Circle;
import smartMath.Vec2;
import strategie.GameState;
import enums.ObstacleGroups;
import enums.ScriptNames;
import enums.ServiceNames;
import exceptions.InObstacleException;
import exceptions.PathNotFoundException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialConnexionException;
import exceptions.serial.SerialFinallyException;

public class JUnit_DropGlass extends JUnit_Test {

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
			//On est jaune donc on est en 0 
			real_state.robot.setOrientation(0); 
		}
		else
		{
			real_state.robot.setPosition(new Vec2 (1381,1000));
			//On est vert donc on est en PI
			real_state.robot.setOrientation(Math.PI);
		}
		real_state.robot.updateConfig();
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
		
		// libère le verre
		try 
		{
			real_state.robot.moveLengthwise(300, emptyHook, true);// sans ca, le robot va très mal au point d'entrée -> Boucle d'acquitement de TURN ?
			scriptmanager.getScript(ScriptNames.DROP_GLASS).goToThenExec(1, real_state, true, emptyHook );
				
		    real_state.robot.moveToCircle(new Circle(0,400,0), emptyHook, real_state.table, EnumSet.noneOf(ObstacleGroups.class)); // PDD à appeler

			scriptmanager.getScript(ScriptNames.DROP_GLASS).goToThenExec(3, real_state, true, emptyHook );
			
			//On se degage, tant qu'il n'y a pas de PDD
		    real_state.robot.moveToCircle(new Circle(-800,400,0), emptyHook, real_state.table,EnumSet.noneOf(ObstacleGroups.class));
		    real_state.robot.moveToCircle(new Circle(-800,1500,0), emptyHook, real_state.table,EnumSet.noneOf(ObstacleGroups.class));
			
			scriptmanager.getScript(ScriptNames.DROP_GLASS).goToThenExec(2, real_state, true, emptyHook );
		}
		catch (UnableToMoveException e) 
		{
			log.critical("CRITICAL : Chemin bloque, enlevez votre main", this);
			e.printStackTrace();
		} 
		catch (SerialConnexionException e) 
		{
			e.printStackTrace();
		} 
		catch (PathNotFoundException e)
		{
			e.printStackTrace();
		} 
		catch (SerialFinallyException e) 
		{
			e.printStackTrace();
		}
		
	}
	
}
