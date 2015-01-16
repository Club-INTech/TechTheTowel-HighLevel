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

import enums.ScriptNames;
import enums.ServiceNames;
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
public class JUnit_getPlot extends JUnit_Test 
{

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
	}
	
	public void waitMatchBegin()
	{

		System.out.println("Robot pret pour le match, attente du retrait du jumper");
		
		// attends que le jumper soit retiré du robot
		
		boolean jumperWasAbsent = mSensorsCardWrapper.isJumperAbsent();
		while(jumperWasAbsent || !mSensorsCardWrapper.isJumperAbsent())
		{
			jumperWasAbsent = mSensorsCardWrapper.isJumperAbsent();
			 Sleep.sleep(100);
		}

		
		// maintenant que le jumper est retiré, le match a commencé
		//ThreadTimer.matchStarted = true;
	}

	@Test
	public void test()
	{
		// on remplis la liste des plots a attraper (dans l'ordre)
			listToGrab.add(2);
			//listToGrab.add(1);
			//listToGrab.add(0);
		
		container.startAllThreads();
		waitMatchBegin();
		//premiere action du match
		
		System.out.println("Le robot commence le match");
		
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
		
		//debut du match
		System.out.println("debut du match");
		
		//premier script
		for (int i=0; i<listToGrab.size();i++)
		{
			try 
			{
				scriptmanager.getScript(ScriptNames.GRAB_PLOT).goToThenExec(listToGrab.get(i), real_state, true, emptyHook );
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
		
		System.out.println("match fini !");


		//Le match s'arrête
		container.destructor();
	}
}
