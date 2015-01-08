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
import robot.Robot;
import robot.cardsWrappers.SensorsCardWrapper;

/**
 * classe des matchs scriptes.
 * sert de bases pour nimporte quel test
 */
public class JUnit_serialMatch extends JUnit_Test 
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
		
		real_state.robot.setPosition(new Vec2 (1381,1000));
		if (config.getProperty("couleur").equals("jaune"))
		{
			real_state.robot.setOrientation(0); 
			//si on est jaune on est en 0 
		}
		else
		{
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
		while(!jumperWasAbsent || mSensorsCardWrapper.isJumperAbsent())
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
		
				//container.startAllThreads();
				//waitMatchBegin();
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
				System.out.println("debut du match");
				
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
				
				//second script
				
				try 
				{
					scriptmanager.getScript(ScriptNames.CLOSE_CLAP).goToThenExec(123, real_state, true, emptyHook );
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
				System.out.println("match fini !");


				//Le match s'arrête
				container.destructor();
				
		
		/*
			try 
			{
				state.robot.moveLengthwise(1000);
				while(true)
				{
					state.robot.moveLengthwise(1000);
					state.robot.turn(0);
					state.robot.moveLengthwise(1000);
					state.robot.turn(Math.PI);
				}
				//state.robot.moveLengthwise(1120);
				//state.robot.turn(-0.5*Math.PI);
				//state.robot.moveLengthwise(-110);
				//scriptManager.getScript(ScriptNames.DROP_CARPET).execute(1, state, emptyHook, true);
				
				//aller en () point d'entree de fermeture du clap 1-2
				//scriptManager.getScript(ScriptNames.CLOSE_CLAP).execute(12, state, emptyHook, true);
				//aller en () point d'entree de fermeture du clap 3
				//scriptManager.getScript(ScriptNames.CLOSE_CLAP).execute(3, state, emptyHook, true);
			} 
			catch (UnableToMoveException e) 
			{
				e.printStackTrace();
			}
	*/
	}
}
