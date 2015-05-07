//import hook.sortes.HookGenerator;

import java.io.BufferedReader;
import java.io.InputStreamReader;

//import org.junit.runner.JUnitCore;

import strategie.GameState;
import robot.cardsWrappers.SensorsCardWrapper;
import robot.cardsWrappers.LocomotionCardWrapper;
import robot.RobotReal;
import robot.Locomotion;
import scripts.AbstractScript;
import scripts.ScriptManager;
import smartMath.Vec2;
import strategie.GameState;
import strategie.Strategie;
//import tests.JUnit_StrategieThreadTest;
//import sun.rmi.runtime.Log;
//import threads.ThreadTimer;
import utils.Config;
import utils.Log;
import utils.Sleep;
import container.Container;
import enums.ServiceNames;
import hook.Hook;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import scripts.AbstractScript;
import scripts.ScriptManager;
import strategie.GameState;
import table.Table;
import threads.ThreadTimer;

import pathDingDing.PathDingDing;
import enums.ActuatorOrder;
import enums.ScriptNames;
import enums.Speed;
import exceptions.InObstacleException;
import exceptions.PathNotFoundException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialConnexionException;
import exceptions.serial.SerialFinallyException;
import robot.Robot;


		
public class launcher

{
	static Container container;
	static Config config;
	//static GameState<RobotReal> real_state;
	static Strategie strategie;
	static ScriptManager scriptmanager;
	static ArrayList<Hook> emptyHook;
	static GameState<Robot> real_state;
	static SensorsCardWrapper  mSensorsCardWrapper;
	static PathDingDing pathDingDing;
	static SensorsCardWrapper sensors;
	static Log log;
	
	
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception

	{
		
        // si on veut exécuter un test unitaire sur la rapbe, recopier test.nomDeLaClasseDeTest
		//JUnitCore.main(		"tests.JUnit_DeplacementsTest");  
		
		
		container = new Container();
		config = (Config)container.getService(ServiceNames.CONFIG);
		System.out.println("Lanceur lancé");	    	
	    container = new Container();
		config = (Config) container.getService(ServiceNames.CONFIG);
		log = (Log) container.getService(ServiceNames.LOG);
	    real_state = (GameState<Robot>) container.getService(ServiceNames.GAME_STATE);
	    scriptmanager = (ScriptManager) container.getService(ServiceNames.SCRIPT_MANAGER);
	    pathDingDing = (PathDingDing)container.getService(ServiceNames.PATHDINGDING);
	    sensors = (SensorsCardWrapper)container.getService(ServiceNames.SENSORS_CARD_WRAPPER);
	    real_state.robot.updateConfig();
	    sensors.updateConfig();
	    emptyHook = new ArrayList<Hook> ();
	    real_state.robot.setPosition(Table.entryPosition);
	    real_state.robot.setOrientation(Math.PI);
	    		
	    try 
	    {
	    	configColor();
	    	matchSetUp(real_state.robot);
	    	putTennisBall(real_state.robot);
	    	System.out.println("La balle de tennis est mise");
	    	startMatch();
	    } 
	    catch (SerialConnexionException e) 
	    {
	    	e.printStackTrace();
	    }
	}
	/**
	 * Permet de tester et de préparer le robot pour un match
	 */
	    static public void matchSetUp(Robot robot) throws SerialConnexionException
		{
			robot.useActuator(ActuatorOrder.ELEVATOR_GROUND, true);
			
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
			
			robot.setLocomotionSpeed(Speed.BETWEEN_SCRIPTS);
		}
	    /**
	     * Permet la mise en place de la balle de tennis
	     * @param robot
	     * @throws SerialConnexionException
	     */
	    static public void putTennisBall(Robot robot) throws SerialConnexionException
		{
			robot.useActuator(ActuatorOrder.ELEVATOR_GROUND, true);
			robot.useActuator(ActuatorOrder.ELEVATOR_OPEN_JAW, false);
			System.out.println("Veuillez mettre la balle de tennis");
			Sleep.sleep(3000);			
			robot.useActuator(ActuatorOrder.ELEVATOR_CLOSE_JAW, true);
			robot.useActuator(ActuatorOrder.ELEVATOR_LOW, true);
		}
	    /**
	     * Dit au robot qu'il doit démarrer quand on retire le jumper
	     */
	    static public void waitMatchBegin()
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
    		ThreadTimer.matchStarted = true;
    	}
    	/**
    	 * Demande si la couleur est verte au jaune
    	 * @throws Exception
    	 */
    	static void configColor()
    	{
    		String couleur = "";
    		while(!couleur.contains("jaune") && !couleur.contains("vert"))
    		{
    			System.out.println("Rentrez \"vert\" ou \"jaune\" : ");
    			BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in)); 
    			 
    			try 
    			{
    				couleur = keyboard.readLine();
    			}
    			catch (IOException e) 
    			{
    				System.out.println("Erreur IO: le clavier est il bien branché ?");
    			} 
    			if(couleur.contains("jaune"))
    				config.set("couleur", "jaune");
    			else if(couleur.contains("vert"))
    				config.set("couleur", "vert");
    		}
    	}
    	/**
    	 * Coeur du match
    	 */

    	static public void startMatch()
    	{
    		//configColor();
    		container.startAllThreads();
    		waitMatchBegin();
    		//////////////////////////////////////////////////////
    		//	Début du match
    		//////////////////////////////////////////////////////
    		long timeMatchBegin=System.currentTimeMillis();
    		System.out.println("Debut du match");
    		//////////////////////////////////////////////////////
    		//	script drop carpet 2 (sortie de zone de départ + gobelet + tapis)
    		//////////////////////////////////////////////////////
    		try 
	    	{
	    		System.out.println("en position ("+real_state.robot.getPosition().x+", "+real_state.robot.getPosition().y+") avant les tapis");
	    		// ce script sert désormais également à sortir de la zone de départ!!!
	    		scriptmanager.getScript(ScriptNames.DROP_CARPET).execute(2, real_state, emptyHook);
	    		System.out.println("en position ("+real_state.robot.getPosition().x+", "+real_state.robot.getPosition().y+") après les tapis");
	    	}
	    	catch (UnableToMoveException | SerialConnexionException | SerialFinallyException e) 
	    	{
	    		e.printStackTrace();
	    	}
	    	//////////////////////////////////////////////////////
	    	//	script grab plot 2
	    	//////////////////////////////////////////////////////
	    	
	    	try 
	    	{
	    		System.out.println("en position ("+real_state.robot.getPosition().x+", "+real_state.robot.getPosition().y+") avant le plot 2");
	    		// On prend le plot a notre gauche, en sortant de la zone de depart
	    		scriptmanager.getScript(ScriptNames.GRAB_PLOT).goToThenExec(2, real_state, emptyHook );
	    		System.out.println("en position ("+real_state.robot.getPosition().x+", "+real_state.robot.getPosition().y+") après le plot 2");
	    	} 
	    	catch (UnableToMoveException | SerialConnexionException | PathNotFoundException | SerialFinallyException | InObstacleException e1) 
	    	{
	    		e1.printStackTrace();
	    	}
	    	System.out.println("Plot 2 pris");

	    	//////////////////////////////////////////////////////
	    	//	script grab plot 34
	    	//////////////////////////////////////////////////////
	    	
	    	try 
	    	{
	    		System.out.println("en position ("+real_state.robot.getPosition().x+", "+real_state.robot.getPosition().y+") avant les plots 3 et 4, et le verre 0");
	    		// On prend les 2 plots en bas de notre zone de depart, et le verre
	    		scriptmanager.getScript(ScriptNames.GRAB_PLOT).goToThenExec(34, real_state, emptyHook );
	    		System.out.println("en position ("+real_state.robot.getPosition().x+", "+real_state.robot.getPosition().y+") après les plots 3 et 4 et verre 0");
	    	} 
	    	catch (UnableToMoveException | SerialConnexionException | PathNotFoundException | SerialFinallyException | InObstacleException e1) 
	    	{
	    		e1.printStackTrace();
	    	}
	    	
	    	System.out.println("Plot 3, 4 et gobelet pris");
	    		
	    	//////////////////////////////////////////////////////
	    	//	script close clap 12
	    	//////////////////////////////////////////////////////
	    		
	    	try 
	    	{
	    		System.out.println("en position ("+real_state.robot.getPosition().x+", "+real_state.robot.getPosition().y+") avant les claps 1 et 2");
	    		scriptmanager.getScript(ScriptNames.CLOSE_CLAP).goToThenExec(12, real_state, emptyHook );
	    		System.out.println("en position ("+real_state.robot.getPosition().x+", "+real_state.robot.getPosition().y+") après les claps 1 et 2");

	    	}
	    	catch (UnableToMoveException | SerialConnexionException | PathNotFoundException | SerialFinallyException | InObstacleException e) 
	    	{
	    		e.printStackTrace();
	    	}
	    		
	   		System.out.println("Clap 1 et 2 Fermés");

    		//////////////////////////////////////////////////////
    		//	script grab plot 1
    		//////////////////////////////////////////////////////
	    		
    		try 
    		{//TODO PathNotFound Exception
    			System.out.println("en position ("+real_state.robot.getPosition().x+", "+real_state.robot.getPosition().y+") avant le plot 1");
    			// On prend le plot a cote de l'estrade
    			scriptmanager.getScript(ScriptNames.GRAB_PLOT).goToThenExec(1, real_state, emptyHook );
    			System.out.println("en position ("+real_state.robot.getPosition().x+", "+real_state.robot.getPosition().y+") après le plot 1");
    		}
    		catch (UnableToMoveException | SerialConnexionException | PathNotFoundException | SerialFinallyException | InObstacleException e) 
    		{
    			e.printStackTrace();
    		}
	    		
    		System.out.println("Plot 1 pris");
	    		
    		//////////////////////////////////////////////////////
    		//	script free stack 0
    		//////////////////////////////////////////////////////
	    		
    		try 
    		{			
    			System.out.println("en position ("+real_state.robot.getPosition().x+", "+real_state.robot.getPosition().y+") avant de deposé la pile sur l'estrade");
    			// On lache notre pile dans notre zone 
    			scriptmanager.getScript(ScriptNames.FREE_STACK).goToThenExec(0, real_state, emptyHook );
    			System.out.println("en position ("+real_state.robot.getPosition().x+", "+real_state.robot.getPosition().y+") après deposé la pile sur l'estrade");
    		}
    		catch (UnableToMoveException | SerialConnexionException | PathNotFoundException | SerialFinallyException | InObstacleException e) 
    		{
    			e.printStackTrace();
    		}
	    		
    		System.out.println("Pile vidée");
	    		
//	    			/*	
//	    		try 
//	    		{
//	    			System.out.println("en position ("+real_state.robot.getPosition().x+", "+real_state.robot.getPosition().y+") avant de prendre la balle");
//	    			//Le robot n'en est pas encore capable mais ca va venir avec le bas niveau et les fonctions "bras au milieu" etc
//	    			// TODO : resoudre laa PathNotFound Exeption
//	    			scriptmanager.getScript(ScriptNames.TAKE_TENNIS_BALL).goToThenExec(1, real_state, true, emptyHook );
//	    			System.out.println("en position ("+real_state.robot.getPosition().x+", "+real_state.robot.getPosition().y+") après prendre la balle");
//	    		}
//	    		catch (UnableToMoveException | SerialConnexionException | PathNotFoundException | SerialFinallyException e) 
//	    		{
//	    			e.printStackTrace();
//	    		}*/
	    		

    		//////////////////////////////////////////////////////
    		//	script drop gobelet
    		//////////////////////////////////////////////////////
	    		
    		try
    		{
    			System.out.println("en position ("+real_state.robot.getPosition().x+", "+real_state.robot.getPosition().y+") avant de deposer le verre");
    			real_state.robot.moveLengthwise(-50, null, true); // prend un peu de recul pour ne pas shooter la pile que l'on vient de déposer
    			scriptmanager.getScript(ScriptNames.DROP_GLASS).execute(0, real_state, emptyHook );//On depose 1 verre dans notre zone
    			System.out.println("en position ("+real_state.robot.getPosition().x+", "+real_state.robot.getPosition().y+") après deposer le verre");
    		}
    		catch (UnableToMoveException | SerialConnexionException | SerialFinallyException  e) 
    		{
    			e.printStackTrace();
    		}
	    		
    		System.out.println("Verre deposé");
//	    		
//	    		try
//	    		{
//	    			System.out.println("en position ("+real_state.robot.getPosition().x+", "+real_state.robot.getPosition().y+") avant le plot 0");
//	    			scriptmanager.getScript(ScriptNames.GRAB_PLOT).goToThenExec(0, real_state, emptyHook ); // On recupere le plot en face de l'escalier
//	    			System.out.println("en position ("+real_state.robot.getPosition().x+", "+real_state.robot.getPosition().y+") après le plot 0");
	    //
//	    		}
//	    		catch (UnableToMoveException | SerialConnexionException | PathNotFoundException | SerialFinallyException | InObstacleException e) 
//	    		{
//	    			e.printStackTrace();
//	    		}
	    //
//	    		System.out.println("Plot 0 pris");
//	    		
//	    		try 
//	    		{
//	    			System.out.println("en position ("+real_state.robot.getPosition().x+", "+real_state.robot.getPosition().y+") avant le verre 2");
//	    			scriptmanager.getScript(ScriptNames.GRAB_GLASS).goToThenExec(2, real_state, emptyHook ); // ON recupere le verre devant l'estrade
//	    			System.out.println("en position ("+real_state.robot.getPosition().x+", "+real_state.robot.getPosition().y+") après le verre 2");
	    //
//	    		}
//	    		catch (UnableToMoveException | SerialConnexionException | PathNotFoundException | SerialFinallyException | InObstacleException e) 
//	    		{
//	    			e.printStackTrace();
//	    		}
//	    		
//	    		System.out.println("Verre 2 pris");
//	    		
//	    		try 
//	    		{			
//	    			System.out.println("en position ("+real_state.robot.getPosition().x+", "+real_state.robot.getPosition().y+") avant le clap 3");
//	    			scriptmanager.getScript(ScriptNames.CLOSE_CLAP).goToThenExec(3, real_state, emptyHook ); // ON recupere le verre devant l'estrade
//	    			System.out.println("en position ("+real_state.robot.getPosition().x+", "+real_state.robot.getPosition().y+") après le clap 3");
	    //
//	    		}
//	    		catch (UnableToMoveException | SerialConnexionException | PathNotFoundException | SerialFinallyException | InObstacleException e) 
//	    		{
//	    			e.printStackTrace();
//	    		}
//	    		
//	    		System.out.println("Clap 3 fermé");
	    //
//	    		try 
//	    		{
//	    			System.out.println("en position ("+real_state.robot.getPosition().x+", "+real_state.robot.getPosition().y+") avant le deposage en zone basse enemie");
//	    			scriptmanager.getScript(ScriptNames.DROP_GLASS).goToThenExec(1, real_state, emptyHook ); // On depose le verre chez les ennemis, en bas.
//	    			System.out.println("en position ("+real_state.robot.getPosition().x+", "+real_state.robot.getPosition().y+") après le deposage en zone basse enemie");
	    //
//	    		}
//	    		catch (UnableToMoveException | SerialConnexionException | PathNotFoundException | SerialFinallyException | InObstacleException e) 
//	    		{
//	    			e.printStackTrace();
//	    		}
//	    		
//	    		try 
//	    		{
//	    			System.out.println("en position ("+real_state.robot.getPosition().x+", "+real_state.robot.getPosition().y+") avant le deposage en zone haute enemie");	
//	    			scriptmanager.getScript(ScriptNames.DROP_GLASS).goToThenExec(2, real_state, emptyHook ); // On depose le verre chez les ennemis, en haut.
//	    			System.out.println("en position ("+real_state.robot.getPosition().x+", "+real_state.robot.getPosition().y+") après le deposage en zone haute enemie");	
	    //
//	    		}
//	    		catch (UnableToMoveException | SerialConnexionException | PathNotFoundException | SerialFinallyException | InObstacleException e) 
//	    		{
//	    			e.printStackTrace();
//	    		}
//	    		
//	    		try 
//	    		{
//	    			System.out.println("en position ("+real_state.robot.getPosition().x+", "+real_state.robot.getPosition().y+") avant le plot 5 et 6");
//	    			scriptmanager.getScript(ScriptNames.GRAB_PLOT).goToThenExec(56, real_state, emptyHook );//On recupere les 2 plots a droite de l'escalier
//	    			System.out.println("en position ("+real_state.robot.getPosition().x+", "+real_state.robot.getPosition().y+") après le plot 5 et 6");
	    //
//	    		}
//	    		catch (UnableToMoveException | SerialConnexionException | PathNotFoundException | SerialFinallyException | InObstacleException e) 
//	    		{
//	    			e.printStackTrace();
//	    		}
//	    				
//	    		try 
//	    		{
//	    			real_state.robot.turn(0);	
//	    			System.out.println("en position ("+real_state.robot.getPosition().x+", "+real_state.robot.getPosition().y+") avant le plot 7");
//	    			scriptmanager.getScript(ScriptNames.GRAB_PLOT).goToThenExec(7, real_state, emptyHook );//On recupere le dernier plot
//	    			System.out.println("en position ("+real_state.robot.getPosition().x+", "+real_state.robot.getPosition().y+") après le plot 7");
	    //
//	    		}
//	    		catch (UnableToMoveException | SerialConnexionException | PathNotFoundException | SerialFinallyException | InObstacleException e) 
//	    		{
//	    			e.printStackTrace();
//	    		}
//	    		
//	    		try 
//	    		{
//	    			
//	    			System.out.println("en position ("+real_state.robot.getPosition().x+", "+real_state.robot.getPosition().y+") avant le deposage de la pile dans notre zone");
//	    			scriptmanager.getScript(ScriptNames.FREE_STACK).goToThenExec(2, real_state, emptyHook ); // On libere la pile ans notre zone
//	    			System.out.println("en position ("+real_state.robot.getPosition().x+", "+real_state.robot.getPosition().y+") après le deposage de la pile dans notre zone");
	    //type filter text
//	    		}
//	    		catch (UnableToMoveException | SerialConnexionException | PathNotFoundException | SerialFinallyException | InObstacleException e) 
//	    		{
//	    			e.printStackTrace();
//	    		}
	    		
    		//////////////////////////////////////////////////////
    		//	Fin du match
    		//////////////////////////////////////////////////////
	    		
    		System.out.println("match fini !");

    		//Le match s'arrête
    		container.destructor();
	    		
    		System.out.println(System.currentTimeMillis()-timeMatchBegin+" ms depuis le debut : < 90.000 ?");
    	}
}
	

	