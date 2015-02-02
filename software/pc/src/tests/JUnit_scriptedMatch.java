package tests;

import hook.Hook;

import java.util.ArrayList;

import scripts.AbstractScript;
import scripts.ScriptManager;
import smartMath.Circle;
import smartMath.Vec2;
import strategie.GameState;

import org.junit.Before;
import org.junit.Test;

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
 * classe des matchs scriptes.
 * sert de bases pour nimporte quel test
 */

///FIXME : soucis pour le moment :
/// COTE JAUNE : Le robot part embrasser le mur au lieu de lancer le script "plots 3+4+ verre"
/// COTE JAUNE : Le robot ne prend meme pas la peine de prendre le plot après 
/// L'ascenceur se ferme mal : le systeme d'anti-retour empeche l'ascenceur de bien se fermer, après deposage des plots ET meme au tout debut du match.
/// Les tapis se tordent, mais sont bien posés 2/3 du temps.
/// COTE VERT : Le robot ne ferme pas lees claps

///Reussites à 100% du temps : 
/// Sortir
/// Le verre 1
/// COTE VERT : Les 3 claps
/// Le deposage des plots
/// COTE JAUNE : Les 2 plots+verre dans le coin


public class JUnit_scriptedMatch extends JUnit_Test 
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
		real_state = (GameState<Robot>) container.getService(ServiceNames.GAME_STATE);
		scriptmanager = (ScriptManager) container.getService(ServiceNames.SCRIPT_MANAGER);
		mSensorsCardWrapper = (SensorsCardWrapper) container.getService(ServiceNames.SENSORS_CARD_WRAPPER);
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
		/*
		boolean jumperWasAbsent = mSensorsCardWrapper.isJumperAbsent();
		while(jumperWasAbsent || !mSensorsCardWrapper.isJumperAbsent())
		{
			jumperWasAbsent = mSensorsCardWrapper.isJumperAbsent();
			 Sleep.sleep(100);
		}
*/
		
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
		container.startAllThreads();
		waitMatchBegin();
		//premiere action du match
		
		System.out.println("Le robot commence le match");
	
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
		System.out.println("Debut du match");
		
		
		try 
		{
			scriptmanager.getScript(ScriptNames.GRAB_GLASS).goToThenExec(1, real_state, true, emptyHook );
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
		
		//premier script
		try 
		{
			scriptmanager.getScript(ScriptNames.DROP_CARPET).goToThenExec(1, real_state, true, emptyHook );
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
		
		System.out.println("Tapis deposés");

		//second script

		try 
		{
			scriptmanager.getScript(ScriptNames.GRAB_PLOT).goToThenExec(2, real_state, true, emptyHook );
		} 
		catch (UnableToMoveException | SerialConnexionException
				| PathNotFoundException | SerialFinallyException e1) 
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		System.out.println("PLot 2 pris");

		
		/// TODO COTE JAUNE : Le robot part embrasser le mur au lieu de lancer le script "plots 3+4+ verre"
		try 
		{
			scriptmanager.getScript(ScriptNames.GRAB_PLOT).goToThenExec(34, real_state, true, emptyHook );
		} 
		catch (UnableToMoveException | SerialConnexionException
				| PathNotFoundException | SerialFinallyException e1) 
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		System.out.println("PLot 3, 4 et gobelet pris");
		
		/// TODO COTE VERT : Le robot ne ferme pas lees claps
		try 
		{
			//ferme les 2 claps proches : 
			scriptmanager.getScript(ScriptNames.CLOSE_CLAP).goToThenExec(-12, real_state, true, emptyHook);
		}
		catch (UnableToMoveException e1) 
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
		catch (SerialConnexionException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Clap 1 et 2 Fermés");

		/// TODO COTE JAUNE : Le robot ne prend meme pas la peine de prendre le plot après 
		try 
		{
			scriptmanager.getScript(ScriptNames.GRAB_PLOT).goToThenExec(1, real_state, true, emptyHook );
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
			
		} 
		catch (SerialFinallyException e) 
		{
			e.printStackTrace();
		}
		
		System.out.println("PLot 1 pris");

		try 
		{
			scriptmanager.getScript(ScriptNames.FREE_STACK).goToThenExec(1, real_state, true, emptyHook );
			real_state.robot.moveLengthwise(-300);
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
			
		} 
		catch (SerialFinallyException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*Le robot n'en est pas encore capable mais ca va venir avec le bas niveau et les fonctions "bras au milieu" etc
		try 
		{
		    real_state.robot.moveToCircle(new Circle(800,1000,0), emptyHook, real_state.table); // PDD à appeler / On va vers notre emplacement de depart
			scriptmanager.getScript(ScriptNames.TAKE_TENNIS_BALL).goToThenExec(1, real_state, true, emptyHook );
		}
		catch (UnableToMoveException e1) 
		{
			e1.printStackTrace();
		}*/
		
		try 
		{
			scriptmanager.getScript(ScriptNames.DROP_GLASS).goToThenExec(1, real_state, true, emptyHook );
		    real_state.robot.moveToCircle(new Circle(500,1000,0), emptyHook, real_state.table); // PDD à appeler / On va vers notre emplacement de depart
		}
		catch (UnableToMoveException e1) 
		{
			e1.printStackTrace();
		}
		
		try 
		{
			scriptmanager.getScript(ScriptNames.CLOSE_CLAP).goToThenExec(3, real_state, true, emptyHook );
		}
		catch (UnableToMoveException e1) 
		{
			e1.printStackTrace();
		}
		//GoToThenExec de deposer verres
		
		System.out.println("match fini !");


		//Le match s'arrête
		container.destructor();
	}
}
