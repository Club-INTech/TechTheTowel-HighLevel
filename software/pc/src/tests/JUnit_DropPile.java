package tests;

import hook.Hook;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import robot.Robot;
import robot.cardsWrappers.SensorsCardWrapper;
import scripts.AbstractScript;
import scripts.ScriptManager;
import strategie.GameState;
import table.Table;
import enums.ActuatorOrder;
import enums.ObstacleGroups;
import enums.ScriptNames;
import enums.ServiceNames;
import exceptions.ExecuteException;
import exceptions.InObstacleException;
import exceptions.PathNotFoundException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialConnexionException;
import exceptions.serial.SerialFinallyException;

public class JUnit_DropPile extends JUnit_Test {

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


		real_state.robot.setPosition(Table.entryPosition);
		//On est vert donc on est en PI 
		real_state.robot.setOrientation(Math.PI); 
		real_state.robot.updateConfig();
		
		//initialisation en position des AX-12
		matchSetUp(real_state.robot, false);
		
		real_state.robot.isGlassStoredLeft=true;
		real_state.robot.isGlassStoredRight=true;

		waitMatchBegin(mSensorsCardWrapper, real_state.robot);
	}

	@Test
	public void deposepile() throws SerialConnexionException, UnableToMoveException
	{
		GameState<Robot> stateToConsider = real_state;
		stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_GROUND, true);
		//on ouvre le guide un peu
		
		stateToConsider.robot.useActuator(ActuatorOrder.MID_LEFT_GUIDE, false);
		stateToConsider.robot.useActuator(ActuatorOrder.MID_RIGHT_GUIDE, true);
		stateToConsider.robot.sleep(1000);	// attente pour que la pile retrouve 

		stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_OPEN_JAW, true);

		stateToConsider.robot.sleep(1000);	// attente pour que la pile retrouve 
		//puis beaucoup
		stateToConsider.robot.useActuator(ActuatorOrder.OPEN_RIGHT_GUIDE, false);
		stateToConsider.robot.useActuator(ActuatorOrder.OPEN_LEFT_GUIDE, true);
		//on se vide de nos plots et on met a jour les points
		int ball = 0;
		if (stateToConsider.robot.isBallStored)
			ball = 1;
		int valuePoints = (2*ball+3)*stateToConsider.robot.storedPlotCount;
		stateToConsider.obtainedPoints += (2*ball+3)*stateToConsider.robot.storedPlotCount;
		stateToConsider.table.setPileValue(0, valuePoints);
		stateToConsider.robot.storedPlotCount = 0;
		stateToConsider.robot.isBallStored = false;
		stateToConsider.robot.digestPlot();
		
		stateToConsider.robot.moveLengthwiseWithoutDetection(-250, new ArrayList<Hook>(), false);
		//Puis on finit
		stateToConsider.robot.useActuator(ActuatorOrder.CLOSE_RIGHT_GUIDE, true);
		stateToConsider.robot.useActuator(ActuatorOrder.CLOSE_LEFT_GUIDE, false);
		stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_CLOSE_JAW, true);
		
		//on remet l'ascenceur en position de deplacement
		stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_LOW, false);
	}
	
	//@Test
	public void test()
	{
		//on sort de la zone de depart
		try 
		{
			AbstractScript exitScript = scriptmanager.getScript(ScriptNames.EXIT_START_ZONE);
			exitScript.execute(0, real_state, emptyHook );
		} 
		catch ( SerialFinallyException | ExecuteException e) 
		{
			log.critical( ((ExecuteException) e).logStack(), this);
			return;
		}
		
		// libère la pile de plots
		try 
		{
			scriptmanager.getScript(ScriptNames.FREE_STACK).goToThenExec(0, real_state, emptyHook );
		} 
		catch (InObstacleException e) 
		{
				for (ObstacleGroups obst : e.getObstacleGroup())
				{
					log.critical(obst.name(),this);
				}
		}
		catch (UnableToMoveException | SerialConnexionException e) 
		{
			e.printStackTrace();
		
		} 
		catch (PathNotFoundException e)
		{
			log.critical( e.logStack(), this);
		} 
		catch (SerialFinallyException e) 
		{
			log.critical( e.logStack(), this);
		} catch (ExecuteException e) {
			log.critical( e.logStack(), this);
		}
	}
	
}
