package tests;

import hook.Hook;

import java.util.ArrayList;

import scripts.ScriptManager;
import strategie.GameState;
import table.Table;
import threads.ThreadTimer;
import utils.Sleep;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;

import pathDingDing.PathDingDing;
import enums.ScriptNames;
import enums.ServiceNames;
import exceptions.InObstacleException;
import exceptions.PathNotFoundException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialConnexionException;
import exceptions.serial.SerialFinallyException;
import robot.Robot;
import robot.RobotReal;
import robot.cardsWrappers.SensorsCardWrapper;

/**
 * classe des matchs scriptes
 */

public class JUnit_Homologation extends JUnit_Test 
{
	ArrayList<Hook> emptyHook;
	GameState<Robot> real_state;
	ScriptManager scriptmanager;
	SensorsCardWrapper  mSensorsCardWrapper;
	PathDingDing pathDingDing;
	SensorsCardWrapper sensors;
	RobotReal robotReal;
	
	public static void main(String[] args) throws Exception
	{                    
	   JUnitCore.main("tests.JUnit_Homologation");
	}
	
	
	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception
	{
		super.setUp();

		real_state = (GameState<Robot>) container.getService(ServiceNames.GAME_STATE);
		scriptmanager = (ScriptManager) container.getService(ServiceNames.SCRIPT_MANAGER);
		mSensorsCardWrapper = (SensorsCardWrapper) container.getService(ServiceNames.SENSORS_CARD_WRAPPER);
        pathDingDing = (PathDingDing)container.getService(ServiceNames.PATHDINGDING);
        sensors = (SensorsCardWrapper)container.getService(ServiceNames.SENSORS_CARD_WRAPPER);
        
        
		real_state.robot.updateConfig();
        sensors.updateConfig();

		emptyHook = new ArrayList<Hook> ();  
		
		real_state.robot.setPosition(Table.entryPosition);
		real_state.robot.setOrientation(Math.PI);
		
		try 
		{
			matchSetUp(real_state.robot);
		} 
		catch (SerialConnexionException e) 
		{
			log.debug(e.logStack(), this);
		}
	}
	
	public void waitMatchBegin()
	{
		log.debug("Robot pret pour le match, attente du retrait du jumper",this);
		
		// attends que le jumper soit retiré du robot
		
		boolean jumperWasAbsent = mSensorsCardWrapper.isJumperAbsent();
		while(jumperWasAbsent || !mSensorsCardWrapper.isJumperAbsent())
		{
			jumperWasAbsent = mSensorsCardWrapper.isJumperAbsent();
			 real_state.robot.sleep(100);
		}

		// maintenant que le jumper est retiré, le match a commencé
		ThreadTimer.matchStarted = true;
	}

	@Test
	public void startMatch()
	{
		container.startAllThreads();
		waitMatchBegin();

		//////////////////////////////////////////////////////
		//	Début du match
		//////////////////////////////////////////////////////
		
		log.debug("Debut du match d'homologation",this);
				
		//////////////////////////////////////////////////////
		//	Sortie
		//////////////////////////////////////////////////////
		
		

		log.debug("en position ("+real_state.robot.getPosition().x+", "+real_state.robot.getPosition().y+") avant la sortie",this);
		
		// tant qu'on est pas sortis
		while (real_state.robot.getPosition().distance(Table.entryPosition)<250)
		{
			try 
			{
				// version d'exitStartZone avec detection, movelenghtwise simple
				real_state.robot.moveLengthwise(250, emptyHook, false, true);
			} 
			catch (UnableToMoveException e) 
			{
				log.critical("impossible de sortir de la zone de depart", this);
				Sleep.sleep(500);
			}
		}
		
		log.debug("en position ("+real_state.robot.getPosition().x+", "+real_state.robot.getPosition().y+") après la sortie",this);

	
		
	
		//////////////////////////////////////////////////////
		//	Verre 1
		//////////////////////////////////////////////////////
		
		
		while(! real_state.table.isGlassXTaken(1))
		{
			try 
			{
				log.debug("en position ("+real_state.robot.getPosition().x+", "+real_state.robot.getPosition().y+") avant le verre 1",this);
				scriptmanager.getScript(ScriptNames.GRAB_GLASS).goToThenExec(1, real_state, emptyHook);
				log.debug("en position ("+real_state.robot.getPosition().x+", "+real_state.robot.getPosition().y+") après le verre 1",this);
			}
			catch (UnableToMoveException e){log.debug(e.logStack(), this);}
			catch (SerialConnexionException e){log.debug(e.logStack(), this);}
			catch (PathNotFoundException e){log.debug(e.logStack(), this);}
			catch (SerialFinallyException e){log.debug(e.logStack(), this);}
			catch (InObstacleException e){log.debug(e.logStack(), this);}
			
			if(! real_state.table.isGlassXTaken(1))
				log.debug("On retente d'attraper le verre",this);
				
		}
		
		//////////////////////////////////////////////////////
		//	Tapis
		//////////////////////////////////////////////////////
		
		while(! real_state.table.getIsLeftCarpetDropped() && ! real_state.table.getIsRightCarpetDropped())
		{
			try 
			{
				log.debug("en position ("+real_state.robot.getPosition().x+", "+real_state.robot.getPosition().y+") avant les tapis",this);
				scriptmanager.getScript(ScriptNames.DROP_CARPET).goToThenExec(1, real_state, emptyHook);
				log.debug("en position ("+real_state.robot.getPosition().x+", "+real_state.robot.getPosition().y+") après les tapis",this);
			}
			catch (UnableToMoveException e){log.debug(e.logStack(), this);}
			catch (SerialConnexionException e){log.debug(e.logStack(), this);}
			catch (PathNotFoundException e){log.debug(e.logStack(), this);}
			catch (SerialFinallyException e){log.debug(e.logStack(), this);}
			catch (InObstacleException e){log.debug(e.logStack(), this);}
			
			if(! real_state.table.getIsLeftCarpetDropped() && ! real_state.table.getIsRightCarpetDropped())
				log.debug("On retente de depose les tapis",this);
		}
		
		//////////////////////////////////////////////////////
		//	Retour Maison
		//////////////////////////////////////////////////////
		
		while(! real_state.table.isAreaXFilled(1))
		{
			try 
			{
				log.debug("en position ("+real_state.robot.getPosition().x+", "+real_state.robot.getPosition().y+") avant le depose verre",this);
				scriptmanager.getScript(ScriptNames.DROP_GLASS).goToThenExec(0, real_state, emptyHook);
				log.debug("en position ("+real_state.robot.getPosition().x+", "+real_state.robot.getPosition().y+") après le depose verre",this);
			}
			catch (UnableToMoveException e){log.debug(e.logStack(), this);}
			catch (SerialConnexionException e){log.debug(e.logStack(), this);}
			catch (PathNotFoundException e){log.debug(e.logStack(), this);}
			catch (SerialFinallyException e){log.debug(e.logStack(), this);}
			catch (InObstacleException e){log.debug(e.logStack(), this);}
			
			if(! real_state.table.isAreaXFilled(1))
				log.debug("On retente de depose le verre",this);
		}
		
		
		//////////////////////////////////////////////////////
		//	Fin du match
		//////////////////////////////////////////////////////
		
		log.debug("match fini !",this);

		//Le match s'arrête
		container.destructor();
	}
}
