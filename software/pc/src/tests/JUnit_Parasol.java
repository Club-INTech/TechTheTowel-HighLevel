package tests;

import enums.ScriptNames;
import enums.ServiceNames;
import exceptions.ExecuteException;
import exceptions.serial.SerialFinallyException;
import hook.Hook;
import java.util.ArrayList;
import org.junit.Before;
import org.junit.Test;
import robot.Robot;
import scripts.ScriptManager;
import strategie.GameState;

// JUnit du magnifique proto de mon chef de projet préféré. Signé : Delphine

public class JUnit_Parasol extends JUnit_Test
{
	private GameState<Robot> virtualState;
	private ScriptManager scriptManager;
	
	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		log.debug("JUnit_ParasolTest.setUp()");
		virtualState = (GameState<Robot>)container.getService(ServiceNames.GAME_STATE);
		virtualState.updateConfig();
		scriptManager = (ScriptManager)container.getService(ServiceNames.SCRIPT_MANAGER);
	}
	
	@Test
	public void openThatParasol() throws ExecuteException
	{
		ArrayList<Hook> emptyList = new ArrayList<Hook>();
		try
		{
			log.debug("Lancement du script Parasol");
			scriptManager.getScript(ScriptNames.PARASOL).execute(0, virtualState, emptyList);
		}
		catch(ExecuteException | SerialFinallyException e)
		{
			e.printStackTrace();
		}
	}
}
