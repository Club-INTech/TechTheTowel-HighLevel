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
 * @author theo
 */
public class JUnit_Verres extends JUnit_Test 
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
		
		//gestion de la symmetrie pour les points d'entrée, dans les zones de depart
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
		
		// attends que le jumper soit retiré du robot OU qu'il soit mis puis retiré (orang outangs)
		boolean jumperWasAbsent = mSensorsCardWrapper.isJumperAbsent();
		while(jumperWasAbsent || !mSensorsCardWrapper.isJumperAbsent())
		{
			jumperWasAbsent = mSensorsCardWrapper.isJumperAbsent();
			 Sleep.sleep(100);
		}
		// maintenant que le jumper est retiré, le match a commencé
	}
	

	@Test
	public void test() throws InObstacleException
	{
		try 
		{
			//debut : on met tout en "fermé"
			real_state.robot.useActuator(ActuatorOrder.ARM_LEFT_CLOSE, true);
			real_state.robot.useActuator(ActuatorOrder.ARM_RIGHT_CLOSE, true);
		} 
		
		catch (SerialConnexionException e1) 
		{
			log.critical( e1.logStack(), this);
		}
		container.startAllThreads();
		waitMatchBegin();		
		System.out.println("Le robot commence le match");
		
		
		//on sort de la zone de depart, et gestion de ses exceptions
		try 
		{
			AbstractScript exitScript = scriptmanager.getScript(ScriptNames.EXIT_START_ZONE);
			exitScript.execute(0, real_state, emptyHook );
		} 
		catch (UnableToMoveException e){log.debug(e.logStack(), this);}
		catch (SerialConnexionException e){log.debug(e.logStack(), this);}
		catch (SerialFinallyException e){log.debug(e.logStack(), this);}

		//debut du match
		System.out.println("debut du match");
		try 
		{
			AbstractScript exitScript = scriptmanager.getScript(ScriptNames.EXIT_START_ZONE);
			exitScript.execute(0, real_state, emptyHook );
		
			scriptmanager.getScript(ScriptNames.GRAB_GLASS).goToThenExec(0, real_state, emptyHook );
			System.out.println("Verre 0 attrapé");

			scriptmanager.getScript(ScriptNames.GRAB_GLASS).goToThenExec(1, real_state, emptyHook );
			System.out.println("Verre 1 attrapé");

			scriptmanager.getScript(ScriptNames.GRAB_GLASS).goToThenExec(2, real_state, emptyHook );
			System.out.println("Verre 2 attrapé");

			scriptmanager.getScript(ScriptNames.GRAB_GLASS).goToThenExec(3, real_state, emptyHook );
			System.out.println("Verre 3 attrapé");

			scriptmanager.getScript(ScriptNames.GRAB_GLASS).goToThenExec(4, real_state, emptyHook );
			System.out.println("Verre 4 attrapé");

		}
		catch (UnableToMoveException e){log.debug(e.logStack(), this);}
		catch (SerialConnexionException e){log.debug(e.logStack(), this);}
		catch (PathNotFoundException e){log.debug(e.logStack(), this);}
		catch (SerialFinallyException e){log.debug(e.logStack(), this);}
		catch (InObstacleException e){log.debug(e.logStack(), this);}
		
		
		//Le match s'arrête
		System.out.println("match fini !");
		container.destructor();
	}
}
