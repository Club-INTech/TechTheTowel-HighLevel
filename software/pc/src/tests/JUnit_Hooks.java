package tests;

import hook.Callback;
import hook.Hook;
import hook.methods.OpenLeftArmExe;
import hook.types.HookFactory;

import java.util.ArrayList;

import scripts.ScriptManager;
import smartMath.Vec2;
import strategie.GameState;

import org.junit.Before;
import org.junit.Test;

import enums.ActuatorOrder;
import enums.ScriptNames;
import enums.ServiceNames;
import exceptions.ContainerException;
import exceptions.PathNotFoundException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialConnexionException;
import exceptions.serial.SerialFinallyException;
import exceptions.serial.SerialManagerException;
import robot.Robot;
import robot.cardsWrappers.SensorsCardWrapper;

/**
 *  Classe de test pour les Hooks
 */


public class JUnit_Hooks extends JUnit_Test 
{
	GameState<Robot> real_state;
	ScriptManager scriptmanager;
	SensorsCardWrapper  mSensorsCardWrapper;
	HookFactory hookFactory;
	
	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		real_state = (GameState<Robot>) container.getService(ServiceNames.GAME_STATE);
		scriptmanager = (ScriptManager) container.getService(ServiceNames.SCRIPT_MANAGER);
		mSensorsCardWrapper = (SensorsCardWrapper) container.getService(ServiceNames.SENSORS_CARD_WRAPPER);

		if (real_state.robot.getSymmetry())
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
		try 
		{
			matchSetUp(real_state.robot);
		} 
		catch (SerialConnexionException e) 
		{
			e.printStackTrace();
		}		
	}
	
	/**
	 * le set up du match en cours (mise en place des actionneurs)
	 * @param robot le robot a setuper
	 * @throws SerialConnexionException si l'ordinateur n'arrive pas a communiquer avec les cartes
	 */
	public void matchSetUp(Robot robot) throws SerialConnexionException
	{
		robot.useActuator(ActuatorOrder.ARM_LEFT_CLOSE, false);
		robot.useActuator(ActuatorOrder.ARM_RIGHT_CLOSE, false);
		robot.useActuator(ActuatorOrder.CLOSE_LEFT_GUIDE, false);
		robot.useActuator(ActuatorOrder.CLOSE_RIGHT_GUIDE, false);
		robot.useActuator(ActuatorOrder.LEFT_CARPET_FOLDUP, false);
		robot.useActuator(ActuatorOrder.RIGHT_CARPET_FOLDUP, false);
		robot.useActuator(ActuatorOrder.LOW_LEFT_CLAP, false);
		robot.useActuator(ActuatorOrder.LOW_RIGHT_CLAP, false);
		robot.useActuator(ActuatorOrder.ELEVATOR_CLOSE_JAW, false);
		robot.useActuator(ActuatorOrder.ELEVATOR_LOW, true);
	}

	@Test
	public void test() throws PathNotFoundException, SerialFinallyException, ContainerException, SerialManagerException, SerialConnexionException
	{
		container.startAllThreads();
		//premiere action du match
		
		System.out.println("Le robot commence le match");
		try 
		{
			//hookfactory qui contient les differents hooks
			hookFactory = (HookFactory) container.getService(ServiceNames.HOOK_FACTORY);

			// liste de hook a passer a la locomotion
			ArrayList<Hook> testHookList = new ArrayList<Hook> ();

			
		    Vec2 center = new Vec2();
		    center.x=1000; center.y=1000;
			
			// hook pour ouvrir le bras dès que le robot est dans un cercle, à une precision près
			Hook openArmTestHook = hookFactory.newHookIsDistanceToPointLesserThan(100,center, 20);
			
			// ajoute un callback au hook de position qui ouvre le bras  bras
			openArmTestHook.addCallback(	new Callback(new OpenLeftArmExe(),true, real_state)	);
			
			// ajoute le hook a la liste a passer a la locomotion
			testHookList.add(openArmTestHook);
			
			scriptmanager.getScript(ScriptNames.EXIT_START_ZONE).execute(0, real_state, testHookList, true );
			
			System.out.println("en position ("+real_state.robot.getPosition().x+", "+real_state.robot.getPosition().y+")");
			System.out.println("On est sortis !");
		} 
		catch (UnableToMoveException e) 
		{
			e.printStackTrace();
		}		
		System.out.println("match fini !");

		//Le match s'arrête
		container.destructor();
	}
}
