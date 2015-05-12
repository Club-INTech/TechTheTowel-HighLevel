package tests;

import java.util.ArrayList;

import hook.Hook;

import org.junit.Before;
import org.junit.Test;

import enums.ScriptNames;
import enums.ServiceNames;
import exceptions.ExecuteException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialConnexionException;
import exceptions.serial.SerialFinallyException;
import robot.Robot;
import robot.cardsWrappers.SensorsCardWrapper;
import scripts.AbstractScript;
import scripts.ScriptManager;
import smartMath.Vec2;
import strategie.GameState;


/**
 * Premier test d'un match réel : sert à tester un script, un deplacement, un hook...
 * @author paul, theo
 *
 */
public class JUnit_Match extends JUnit_Test 
{
	/**
	 * La liste de hooks : pour l'instant vides, à remplir
	 */
	ArrayList<Hook> emptyHook;
	
	/**
	 * L'etat de la table
	 */
	GameState<Robot> real_state;
	
	/**
	 * Le gestionnaire des scripts, instanciant et fournissant les scripts
	 */
	ScriptManager scriptmanager;
	
	/**
	 * Le dialoguqe avec la carte Capteurs
	 */
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
		real_state.robot.setOrientation(Math.PI);
		real_state.robot.updateConfig();
	}
	
	@Test
	public void test() 
	{
		try 
		{
			AbstractScript exitScript = scriptmanager.getScript(ScriptNames.EXIT_START_ZONE);
			exitScript.execute(1, real_state, emptyHook );
		} 
		catch ( SerialFinallyException e) 
		{
			log.critical("Carte mal branchée. Match termine",this);
			e.printStackTrace();
			return;
		}
		catch (ExecuteException e){log.debug(e.logStack(), this);}

		
		try 
		{
			real_state.robot.turn(Math.PI*0.5);
		} 
		catch (UnableToMoveException e) 
		{
			log.debug("impossible de tourner",this);
			log.critical( e.logStack(), this);
		}
		
		try 
		{
			real_state.robot.turn(0);
		} 
		catch (UnableToMoveException e) 
		{
			log.debug("impossible de tourner",this);
			log.critical( e.logStack(), this);
		}
		
	}
}

