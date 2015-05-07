package tests;

import hook.Hook;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import robot.Robot;
import robot.cardsWrappers.SensorsCardWrapper;
import smartMath.Vec2;
import strategie.GameState;
import strategie.Strategie;
import threads.ThreadTimer;
import enums.ActuatorOrder;
import enums.ServiceNames;
import enums.Speed;
import exceptions.serial.SerialConnexionException;

public class JUnit_Strategia extends JUnit_Test 
{
	GameState<Robot> real_state;
	Strategie strategos;
	ArrayList<Hook> emptyHook;
	SensorsCardWrapper  mSensorsCardWrapper;
	
	
	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		mSensorsCardWrapper = (SensorsCardWrapper) container.getService(ServiceNames.SENSORS_CARD_WRAPPER);
		real_state = (GameState<Robot>) container.getService(ServiceNames.GAME_STATE);
		strategos = (Strategie) container.getService(ServiceNames.STRATEGIE);
        
		
		emptyHook = new ArrayList<Hook> ();  

		real_state.robot.setPosition(new Vec2 (1099,1000));
		real_state.robot.setOrientation(Math.PI);
		
		real_state.robot.updateConfig();
		try 
		{
			matchSetUp(real_state.robot);
			putTennisBall(real_state.robot);
		} 
		catch (SerialConnexionException e) 
		{
			log.debug( e.logStack(), this);
		}		
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
		ThreadTimer.matchStarted = true;
	}
	
	@Test
	public void desisionTest()
	{
		configColor();
		real_state.robot.setLocomotionSpeed(Speed.SLOW);
		container.startAllThreads();
		waitMatchBegin();
		
		long timeMatchBegin=System.currentTimeMillis();

		
		strategos.updateConfig();
		strategos.IA();
		
		System.out.println(System.currentTimeMillis()-timeMatchBegin+" ms depuis le debut : < 90.000 ?");
	}
}
