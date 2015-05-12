package tests;

import hook.Hook;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;

import robot.Robot;
import robot.cardsWrappers.SensorsCardWrapper;
import smartMath.Vec2;
import strategie.GameState;
import strategie.Strategie;
import threads.ThreadTimer;
import enums.ServiceNames;
import enums.Speed;
import exceptions.ConfigPropertyNotFoundException;
import exceptions.serial.SerialConnexionException;

public class JUnit_Strategia extends JUnit_Test 
{
	GameState<Robot> real_state;
	Strategie strategos;
	ArrayList<Hook> emptyHook;
	SensorsCardWrapper  mSensorsCardWrapper;
	

	public static void main(String[] args) throws Exception
	{                    
	   JUnitCore.main("tests.JUnit_Strategia");
	}
	
	
	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		
		configColor();
		
		
		mSensorsCardWrapper = (SensorsCardWrapper) container.getService(ServiceNames.SENSORS_CARD_WRAPPER);
		real_state = (GameState<Robot>) container.getService(ServiceNames.GAME_STATE);
		strategos = (Strategie) container.getService(ServiceNames.STRATEGIE);
		
		emptyHook = new ArrayList<Hook> ();  

		real_state.robot.setPosition(new Vec2 (1099,1000));
		real_state.robot.setOrientation(Math.PI);
		
		real_state.robot.updateConfig();
		
//		boolean isSetUpSpeedQuick = askForSetUpSpeed();
		
		try 
		{
//			matchSetUp(real_state.robot, isSetUpSpeedQuick);
			matchSetUp(real_state.robot, false);
		} 
		catch (SerialConnexionException e) 
		{
			log.debug( e.logStack(), this);
		}		
		

//		

		real_state.robot.updateConfig();
		real_state.robot.setLocomotionSpeed(Speed.SLOW);
		container.startAllThreads();
	}
	
	/**
	 * Demande à l'utilisateur si il veut une config rapide ou lente
	 * @return vrai si la vitesse  est rapide false si elle est lente
	 */
	@SuppressWarnings("unused")
	private boolean askForSetUpSpeed() 
	{
		String speed = "lol";
		while(!speed.contains("lent") && !speed.contains("rapide") && !speed.isEmpty())
		{
			log.debug("Choissez lz vitesse de l'intitialisation. Rentrez \"rapide\" ou \"lent\" : ",this);
			BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in)); 
			 
			try 
			{
				speed = keyboard.readLine();
			}
			catch (IOException e) 
			{
				log.debug("Erreur IO: le clavier est il bien branché ?",this);
			} 
			if(speed.contains("rapide") || speed.isEmpty())
				return true;
			else if(speed.contains("lent"))
				return false;
		}
		return true;
	}


	/**
	 * Demande si la couleur est verte au jaune
	 * @throws Exception
	 */
	void configColor()
	{

		String couleur = "";
		while(!couleur.contains("jaune") && !couleur.contains("vert"))
		{
			log.debug("Rentrez \"vert\" ou \"jaune\" (override de config.ini) : ",this);
			BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in)); 
			 
			try 
			{
				couleur = keyboard.readLine();
			}
			catch (IOException e) 
			{
				log.debug("Erreur IO: le clavier est il bien branché ?",this);
			} 
			if(couleur.contains("jaune"))
				config.set("couleur", "jaune");
			else if(couleur.contains("vert"))
				config.set("couleur", "vert");
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
	public void desisionTest()
	{
		waitMatchBegin();
		
		long timeMatchBegin=System.currentTimeMillis();

		
		strategos.updateConfig();
		strategos.IA();
		
		//////////////////////////////////////////////////////
		//	Fin du match
		//////////////////////////////////////////////////////
		
		log.debug("match fini !",this);

		//Le match s'arrête
		container.destructor();
		
		log.debug(System.currentTimeMillis()-timeMatchBegin+" ms depuis le debut : < 90.000 ?",this);
	}
}
