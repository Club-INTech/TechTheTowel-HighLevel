import hook.Hook;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import pathDingDing.PathDingDing;
import container.Container;

import robot.Robot;
import robot.cardsWrappers.SensorsCardWrapper;
import scripts.ScriptManager;
import smartMath.Vec2;
import strategie.GameState;
import strategie.Strategie;
import table.Table;
import threads.ThreadTimer;
import utils.Config;
import utils.Log;
import utils.Sleep;
import enums.ActuatorOrder;
import enums.ServiceNames;
import enums.Speed;
import exceptions.serial.SerialConnexionException;


public class strategy_launcher {

	
	static Strategie strategos;
	
	static Container container;
	static Config config;
	static ScriptManager scriptmanager;
	static ArrayList<Hook> emptyHook;
	static GameState<Robot> real_state;
	static SensorsCardWrapper  mSensorsCardWrapper;
	static PathDingDing pathDingDing;
	static SensorsCardWrapper sensors;
	static Log log;


	
	public static void main(String[] args) throws Exception
	{
	
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
	    strategos = (Strategie) container.getService(ServiceNames.STRATEGIE);
	    
	    real_state.robot.setPosition(Table.entryPosition);
	    real_state.robot.setOrientation(Math.PI);
	    
	    		
	    try 
	    {
	    	
	    	matchSetUp(real_state.robot);
	    	putTennisBall(real_state.robot);
	    	System.out.println("La balle de tennis est mise");
	    	real_state.robot.updateConfig();
	    	startMatchSmartly();
	    	
	    } 
	    catch (SerialConnexionException e) 
	    {
	    	e.printStackTrace();
	    }
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
		
		robot.setLocomotionSpeed(Speed.SLOW);
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
	 * permet de lancer le match paramétré de telle sorte que le robot puisse se débrouille en cas d'erreur
	 */
    static public void startMatchSmartly()
	{
		real_state.robot.setLocomotionSpeed(Speed.SLOW);
		container.startAllThreads();
		waitMatchBegin();
		
		long timeMatchBegin=System.currentTimeMillis();

		
		strategos.updateConfig();
		strategos.IA();
		
		System.out.println(System.currentTimeMillis()-timeMatchBegin+" ms depuis le debut : < 90.000 ?");
	}
}


