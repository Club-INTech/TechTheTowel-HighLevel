package tests;

import hook.Hook;

import java.util.ArrayList;

import scripts.AbstractScript;
import scripts.ScriptManager;
import smartMath.Vec2;
import strategie.GameState;

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
 *  Classe de test pour la prise de balle de tennis
 */
public class JUnit_TennisBall extends JUnit_Test 
{

	ArrayList<Hook> emptyHook;
	GameState<Robot> real_state;
	ScriptManager scriptmanager;
	SensorsCardWrapper  mSensorsCardWrapper;
	
	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		real_state.robot.updateConfig();

		real_state = (GameState<Robot>) container.getService(ServiceNames.GAME_STATE);
		scriptmanager = (ScriptManager) container.getService(ServiceNames.SCRIPT_MANAGER);
		mSensorsCardWrapper = (SensorsCardWrapper) container.getService(ServiceNames.SENSORS_CARD_WRAPPER);
		emptyHook = new ArrayList<Hook> ();
		
		real_state.robot.setPosition(new Vec2 (1132,1000));
		real_state.robot.setOrientation(Math.PI);
		
		try 
		{
			matchSetUp(real_state.robot);
		} 
		catch (SerialConnexionException e) 
		{
			e.printStackTrace();
		}		
	}
	
	public void waitMatchBegin()
	{

		System.out.println("Robot pret pour le match, attente du retrait du jumper");
		/*
		// attends que le jumper soit retiré du robot
		
		boolean jumperWasAbsent = mSensorsCardWrapper.isJumperAbsent();
		while(jumperWasAbsent || !mSensorsCardWrapper.isJumperAbsent())
		{
			jumperWasAbsent = mSensorsCardWrapper.isJumperAbsent();
			 Sleep.sleep(100);
		}*/

		
		// maintenant que le jumper est retiré, le match a commencé
		//ThreadTimer.matchStarted = true;
	}
	
	/**
	 * le set up du match en cours (mise en place des actionneurs)
	 * @param robot le robot a setuper
	 * @throws SerialConnexionException si l'ordinateur n'arrive pas a communiquer avec les cartes
	 */
	public void matchSetUp(Robot robot) throws SerialConnexionException
	{
		robot.useActuator(ActuatorOrder.ELEVATOR_OPEN_JAW, false);

		robot.useActuator(ActuatorOrder.OPEN_LEFT_GUIDE, false);
		robot.useActuator(ActuatorOrder.OPEN_RIGHT_GUIDE, true);
		
		robot.useActuator(ActuatorOrder.ARM_LEFT_CLOSE, false);
		robot.useActuator(ActuatorOrder.ARM_RIGHT_CLOSE, false);
		
		robot.useActuator(ActuatorOrder.CLOSE_RIGHT_GUIDE, true);
		robot.useActuator(ActuatorOrder.CLOSE_LEFT_GUIDE, true);
		
		robot.useActuator(ActuatorOrder.LEFT_CARPET_FOLDUP, false);
		robot.useActuator(ActuatorOrder.RIGHT_CARPET_FOLDUP, false);
		
		robot.useActuator(ActuatorOrder.LOW_LEFT_CLAP, false);
		robot.useActuator(ActuatorOrder.LOW_RIGHT_CLAP, false);
		
		robot.useActuator(ActuatorOrder.ELEVATOR_CLOSE_JAW, true);
		
		robot.useActuator(ActuatorOrder.ELEVATOR_LOW, true);
	}

	//@Test
	public void test() throws PathNotFoundException, SerialFinallyException, InObstacleException
	{
		container.startAllThreads();
		waitMatchBegin();
		//premiere action du match
		
		System.out.println("Le robot commence le match");
	
		try 
		{
			AbstractScript exitScript = scriptmanager.getScript(ScriptNames.EXIT_START_ZONE);
			exitScript.execute(0, real_state, emptyHook );
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
		System.out.println("Debut du match");
		
		//premier script
		try 
		{
			scriptmanager.getScript(ScriptNames.TAKE_TENNIS_BALL).goToThenExec(1, real_state, emptyHook );
		}
		catch (UnableToMoveException | SerialConnexionException e) 
		{
			// TODO Main erreur critique :
			//attention ce sont surement des erreurs dans le finally d'un script donc elle servent a proteger le meca !
			//ou un robot ennemi devant. Donc beaucoup moins critique (ce serai bie de pouvoir differencer les deux)
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
						
		System.out.println("match fini !");

		//Le match s'arrête
		container.destructor();
	}
	
	@Test
	public void testStatic() throws PathNotFoundException, SerialFinallyException
	{
		try
		{
			//real_state.robot.moveLengthwise(-450,emptyHook);
			scriptmanager.getScript(ScriptNames.TAKE_TENNIS_BALL).execute(1, real_state, emptyHook );
		} 
		catch (UnableToMoveException | SerialConnexionException e) 
		{
			e.printStackTrace();
		}

	}

}
