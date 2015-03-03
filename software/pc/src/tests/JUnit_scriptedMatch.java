package tests;

import hook.Hook;

import java.util.ArrayList;

import scripts.AbstractScript;
import scripts.ScriptManager;
import smartMath.Vec2;
import strategie.GameState;

import org.junit.Before;
import org.junit.Test;

import container.Container;
import pathDingDing.PathDingDing;
import enums.ActuatorOrder;
import enums.ScriptNames;
import enums.ServiceNames;
import exceptions.PathNotFoundException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialConnexionException;
import exceptions.serial.SerialFinallyException;
import robot.Robot;
import robot.cardsWrappers.SensorsCardWrapper;

/**
 * classe des matchs scriptes
 */

public class JUnit_scriptedMatch extends JUnit_Test 
{
	ArrayList<Hook> emptyHook;
	GameState<Robot> real_state;
	ScriptManager scriptmanager;
	SensorsCardWrapper  mSensorsCardWrapper;
	PathDingDing pathDingDing;
	
	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		real_state = (GameState<Robot>) container.getService(ServiceNames.GAME_STATE);
		scriptmanager = (ScriptManager) container.getService(ServiceNames.SCRIPT_MANAGER);
		mSensorsCardWrapper = (SensorsCardWrapper) container.getService(ServiceNames.SENSORS_CARD_WRAPPER);
        pathDingDing = (PathDingDing)container.getService(ServiceNames.PATHDINGDING);
		emptyHook = new ArrayList<Hook> ();  

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
		//ThreadTimer.matchStarted = true;
	}
	
	/**
	 * le set up du match en cours (mise en place des actionneurs)
	 * @param robot le robot a setuper
	 * @throws SerialConnexionException si l'ordinateur n'arrive pas a communiquer avec les cartes
	 */
	public void matchSetUp(Robot robot) throws SerialConnexionException
	{
		robot.useActuator(ActuatorOrder.OPEN_LEFT_GUIDE, false);
		robot.useActuator(ActuatorOrder.OPEN_RIGHT_GUIDE, true);
		
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
	public void test() throws PathNotFoundException, SerialFinallyException, SerialConnexionException
	{
		//container.startAllThreads();
		//waitMatchBegin();
		//premiere action du match
		
		System.out.println("Le robot commence le match");
	
		try 
		{
			AbstractScript exitScript = scriptmanager.getScript(ScriptNames.EXIT_START_ZONE); // Sortie de la zone de depart
			exitScript.execute(0, real_state, emptyHook, true );
		} 
		catch (UnableToMoveException | SerialConnexionException e) 
		{
			e.printStackTrace();
		}
		
		//debut du match
		System.out.println("Debut du match");
		System.out.println("en position ("+real_state.robot.getPosition().x+", "+real_state.robot.getPosition().y+") après etre sorti");//On s'attend  (881,1000)

		try 
		{
			scriptmanager.getScript(ScriptNames.GRAB_GLASS).goToThenExec(1, real_state, true, emptyHook );//On prend le verre,  notre droite en sortant
			System.out.println("en position ("+real_state.robot.getPosition().x+", "+real_state.robot.getPosition().y+") après le verre 1");
		} 
		catch (UnableToMoveException | SerialConnexionException | PathNotFoundException | SerialFinallyException e) 
		{
			e.printStackTrace();
		}
				
		try 
		{
			System.out.println("en position ("+real_state.robot.getPosition().x+", "+real_state.robot.getPosition().y+") avant les tapis");
			scriptmanager.getScript(ScriptNames.DROP_CARPET).goToThenExec(1, real_state, true, emptyHook ); // On depose les tapis
			System.out.println("en position ("+real_state.robot.getPosition().x+", "+real_state.robot.getPosition()+" après les tapis");

		}
		catch (UnableToMoveException | SerialConnexionException| PathNotFoundException | SerialFinallyException e) 
		{
			e.printStackTrace();
		}
		
		try 
		{
			System.out.println("en position ("+real_state.robot.getPosition().x+", "+real_state.robot.getPosition().y+") avant le plot 2");
			scriptmanager.getScript(ScriptNames.GRAB_PLOT).goToThenExec(2, real_state, true, emptyHook ); // On prend le plot a notre gauche, en sortant de la zone de depart
			System.out.println("en position ("+real_state.robot.getPosition().x+", "+real_state.robot.getPosition().y+") après le plot 2");
		} 
		catch (UnableToMoveException | SerialConnexionException | PathNotFoundException | SerialFinallyException e1) 
		{
			e1.printStackTrace();
		}
		
		System.out.println("Plot 2 pris");

		try 
		{
			System.out.println("en position ("+real_state.robot.getPosition().x+", "+real_state.robot.getPosition().y+") avant les plots 3 et 4, et le verre 0");
			scriptmanager.getScript(ScriptNames.GRAB_PLOT).goToThenExec(34, real_state, true, emptyHook ); // On prend les 2 plots en bas de notre zonee de depart, et le verre
			System.out.println("en position ("+real_state.robot.getPosition().x+", "+real_state.robot.getPosition().y+") après les plots 3 et 4 et verre 0");
		} 
		catch (UnableToMoveException | SerialConnexionException | PathNotFoundException | SerialFinallyException e1) 
		{
			e1.printStackTrace();
		}
		
		System.out.println("Plot 3, 4 et gobelet pris");
		
		try 
		{
			System.out.println("en position ("+real_state.robot.getPosition().x+", "+real_state.robot.getPosition().y+") avant les claps 1 et 2");
			scriptmanager.getScript(ScriptNames.CLOSE_CLAP).goToThenExec(-12, real_state, true, emptyHook );
		}
		catch (UnableToMoveException | SerialConnexionException | PathNotFoundException | SerialFinallyException e) 
		{
			e.printStackTrace();
		}
		
		System.out.println("Clap 1 et 2 Fermés");

		try 
		{//TODO PathNotFound Exception
			System.out.println("en position ("+real_state.robot.getPosition().x+", "+real_state.robot.getPosition().y+") avant le plot 1");
			scriptmanager.getScript(ScriptNames.GRAB_PLOT).goToThenExec(1, real_state, true, emptyHook ); // On prend le plot a cote de l'estrade
			System.out.println("Plot 1 pris");
		}
		catch (UnableToMoveException | SerialConnexionException | PathNotFoundException | SerialFinallyException e) 
		{
			e.printStackTrace();
		}
		
		System.out.println("Plot 1 pris");
		
		try 
		{			
			System.out.println("en position ("+real_state.robot.getPosition().x+", "+real_state.robot.getPosition().y+") avant de deposer la pile sur l'estrade");
			scriptmanager.getScript(ScriptNames.FREE_STACK).goToThenExec(1, real_state, true, emptyHook ); // On lache notree pile devnt (bientot sur l'estrade
			real_state.robot.moveLengthwise(-300);		//On recule pour ne pas taper (le PF evitera ca)
		}
		catch (UnableToMoveException | SerialConnexionException | PathNotFoundException | SerialFinallyException e) 
		{
			e.printStackTrace();
		}
		
		System.out.println("Pile vidée");
				
		try 
		{	
			/*Le robot n'en est pas encore capable mais ca va venir avec le bas niveau et les fonctions "bras au milieu" etc
			/*	scriptmanager.getScript(ScriptNames.TAKE_TENNIS_BALL).goToThenExec(1, real_state, true, emptyHook );*/
			
			System.out.println("en position ("+real_state.robot.getPosition().x+", "+real_state.robot.getPosition().y+") avant de deposer le verre");
			scriptmanager.getScript(ScriptNames.DROP_GLASS).goToThenExec(1, real_state, true, emptyHook );//On depose 1 verre dans notre zone
			System.out.println("en position ("+real_state.robot.getPosition().x+", "+real_state.robot.getPosition().y+") après avoir deposé le verre");
		}
		catch (UnableToMoveException | SerialConnexionException | PathNotFoundException | SerialFinallyException e) 
		{
			e.printStackTrace();
		}
		
		System.out.println("Verre deposé");
		
		try
		{
			System.out.println("en position ("+real_state.robot.getPosition().x+", "+real_state.robot.getPosition().y+") avant le plot 0");
			scriptmanager.getScript(ScriptNames.GRAB_PLOT).goToThenExec(0, real_state, true, emptyHook ); // On recupere le plot en face de l'escalier
		}
		catch (UnableToMoveException | SerialConnexionException | PathNotFoundException | SerialFinallyException e) 
		{
			e.printStackTrace();
		}

		System.out.println("Plot 0 pris");
		
		try 
		{
			System.out.println("en position ("+real_state.robot.getPosition().x+", "+real_state.robot.getPosition().y+") avant le verre 2");
			scriptmanager.getScript(ScriptNames.GRAB_GLASS).goToThenExec(2, real_state, true, emptyHook ); // ON recupere le verre devant l'estrade
		}
		catch (UnableToMoveException | SerialConnexionException | PathNotFoundException | SerialFinallyException e) 
		{
			e.printStackTrace();
		}
		
		System.out.println("Verre 2 pris");
		
		try 
		{			
			System.out.println("en position ("+real_state.robot.getPosition().x+", "+real_state.robot.getPosition().y+") avant le clap 3");
			scriptmanager.getScript(ScriptNames.CLOSE_CLAP).goToThenExec(3, real_state, true, emptyHook ); // ON recupere le verre devant l'estrade
		}
		catch (UnableToMoveException | SerialConnexionException | PathNotFoundException | SerialFinallyException e) 
		{
			e.printStackTrace();
		}
		
		System.out.println("Clap 3 fermé");

		try 
		{
			System.out.println("en position ("+real_state.robot.getPosition().x+", "+real_state.robot.getPosition().y+") avant le deposage en zone basse enemie");
			scriptmanager.getScript(ScriptNames.DROP_GLASS).goToThenExec(2, real_state, true, emptyHook ); // On depose le verre chez les ennemis, en bas.
		}
		catch (UnableToMoveException | SerialConnexionException | PathNotFoundException | SerialFinallyException e) 
		{
			e.printStackTrace();
		}
		
		try 
		{
			System.out.println("en position ("+real_state.robot.getPosition().x+", "+real_state.robot.getPosition().y+") avant le deposage en zone haute enemie");	
			scriptmanager.getScript(ScriptNames.DROP_GLASS).goToThenExec(3, real_state, true, emptyHook ); // On depose le verre chez les ennemis, en haut.
		}
		catch (UnableToMoveException | SerialConnexionException | PathNotFoundException | SerialFinallyException e) 
		{
			e.printStackTrace();
		}
		
		try 
		{
			System.out.println("en position ("+real_state.robot.getPosition().x+", "+real_state.robot.getPosition().y+") avant le plot 5 et 6");
			scriptmanager.getScript(ScriptNames.GRAB_PLOT).goToThenExec(56, real_state, true, emptyHook );//On recupere les 2 plots a droite de l'escalier
		}
		catch (UnableToMoveException | SerialConnexionException | PathNotFoundException | SerialFinallyException e) 
		{
			e.printStackTrace();
		}
		
		// TODO  Balles de Pop Corn ?
		
		try 
		{
			real_state.robot.turn(0);	
			System.out.println("en position ("+real_state.robot.getPosition().x+", "+real_state.robot.getPosition().y+") avant le plot 7");
			scriptmanager.getScript(ScriptNames.GRAB_PLOT).goToThenExec(7, real_state, true, emptyHook );//On recupere le dernier plot
		}
		catch (UnableToMoveException | SerialConnexionException | PathNotFoundException | SerialFinallyException e) 
		{
			e.printStackTrace();
		}
		
		try 
		{
			
			System.out.println("en position ("+real_state.robot.getPosition().x+", "+real_state.robot.getPosition().y+") avant le deposage de la pile dans notre zone");
			scriptmanager.getScript(ScriptNames.FREE_STACK).goToThenExec(2, real_state, true, emptyHook ); // On libere la pile ans notre zone
		}
		catch (UnableToMoveException | SerialConnexionException | PathNotFoundException | SerialFinallyException e) 
		{
			e.printStackTrace();
		}
		
		System.out.println("match fini !");

		//Le match s'arrête
		container.destructor();
	}
}
