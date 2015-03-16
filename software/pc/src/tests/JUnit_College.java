package tests;

import hook.Hook;

import java.util.ArrayList;
import java.util.Scanner;

import org.junit.Before;
import org.junit.Test;

import robot.Robot;
import scripts.ScriptManager;
import strategie.GameState;
import enums.ActuatorOrder;
import enums.ScriptNames;
import enums.ServiceNames;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialConnexionException;

public class JUnit_College extends JUnit_Test {

	
	int distanceBetweenPlots = 350;

	private GameState<Robot> real_state;
	private Robot robot;
	private ScriptManager scriptmanager;
	private ArrayList<Hook> emptyHook;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception 
	{
		super.setUp();
		real_state = (GameState<Robot>) container.getService(ServiceNames.GAME_STATE);
		scriptmanager = (ScriptManager) container.getService(ServiceNames.SCRIPT_MANAGER);
		emptyHook = new ArrayList<Hook> ();  
		robot = real_state.robot;
		
		config.set("couleur", "vert");
		robot.updateConfig();

	}

	/**
	 * 
	 * le robot avance de distance-50;
	 * tourne a droite et recule de 220 (voir script depose tapis);
	 * depose les tapis, avance de 220;
	 * ouvre les deux bras, tourne en 0, ferme les deux bras;
	 * (avance de distance-50, ouvre les machoires, avance de 50, mange le plot et le remonte) *4;
	 * ouvre le guide et la machoire et recule de distance; 
	 */
	@Test
	public void demonstration() throws UnableToMoveException, SerialConnexionException 
	{
		matchSetUp(robot);
		
		robot.moveLengthwise(distanceBetweenPlots+200);
		scriptmanager.getScript(ScriptNames.DROP_CARPET).execute(0, real_state, emptyHook, true);
		
		robot.useActuator(ActuatorOrder.HIGH_LEFT_CLAP, false);
		robot.useActuator(ActuatorOrder.HIGH_RIGHT_CLAP, true);
		robot.turn(0);
		robot.useActuator(ActuatorOrder.LOW_LEFT_CLAP, false);
		robot.useActuator(ActuatorOrder.LOW_RIGHT_CLAP, true);
		
		robot.moveLengthwise(distanceBetweenPlots-200);
		robot.useActuator(ActuatorOrder.ELEVATOR_GROUND, true);
		robot.useActuator(ActuatorOrder.ELEVATOR_OPEN_JAW, true);
		robot.moveLengthwise(50);
		robot.useActuator(ActuatorOrder.ARM_LEFT_OPEN, true);
		robot.useActuator(ActuatorOrder.ELEVATOR_CLOSE_JAW, true);
		robot.useActuator(ActuatorOrder.ARM_LEFT_CLOSE, false);
		robot.useActuator(ActuatorOrder.ELEVATOR_HIGH, true);
		robot.useActuator(ActuatorOrder.ELEVATOR_GROUND, false);
		
		robot.moveLengthwise(distanceBetweenPlots-50);
		robot.useActuator(ActuatorOrder.ELEVATOR_OPEN_JAW, true);
		robot.moveLengthwise(50);
		robot.useActuator(ActuatorOrder.ARM_RIGHT_OPEN, true);
		robot.useActuator(ActuatorOrder.ELEVATOR_CLOSE_JAW, true);
		robot.useActuator(ActuatorOrder.ARM_RIGHT_CLOSE, false);
		robot.useActuator(ActuatorOrder.ELEVATOR_HIGH, true);
		robot.useActuator(ActuatorOrder.ELEVATOR_GROUND, false);
		
		robot.moveLengthwise(distanceBetweenPlots-50);
		robot.useActuator(ActuatorOrder.ELEVATOR_OPEN_JAW, true);
		robot.moveLengthwise(50);
		robot.useActuator(ActuatorOrder.ARM_LEFT_OPEN, true);
		robot.useActuator(ActuatorOrder.ELEVATOR_CLOSE_JAW, true);
		robot.useActuator(ActuatorOrder.ARM_LEFT_CLOSE, false);
		robot.useActuator(ActuatorOrder.ELEVATOR_HIGH, true);
		robot.useActuator(ActuatorOrder.ELEVATOR_GROUND, false);
		
		robot.moveLengthwise(distanceBetweenPlots-50);
		robot.useActuator(ActuatorOrder.ELEVATOR_OPEN_JAW, true);
		robot.moveLengthwise(50);
		robot.useActuator(ActuatorOrder.ARM_RIGHT_OPEN, true);
		robot.useActuator(ActuatorOrder.ELEVATOR_CLOSE_JAW, true);
		robot.useActuator(ActuatorOrder.ARM_RIGHT_CLOSE, false);
		
		robot.useActuator(ActuatorOrder.ELEVATOR_GROUND, true);
		robot.useActuator(ActuatorOrder.ELEVATOR_OPEN_JAW, true);

		robot.useActuator(ActuatorOrder.MID_LEFT_GUIDE, true);
		robot.useActuator(ActuatorOrder.MID_RIGHT_GUIDE, true);

		robot.useActuator(ActuatorOrder.OPEN_LEFT_GUIDE, true);
		robot.useActuator(ActuatorOrder.OPEN_RIGHT_GUIDE, true);
		robot.useActuator(ActuatorOrder.ELEVATOR_OPEN_JAW, true);
		robot.moveLengthwise(-distanceBetweenPlots);
	}
	
	//@Test
	public void jeuPourGamins() throws UnableToMoveException
	{
		String scanned = "";
		@SuppressWarnings("resource")
		Scanner scan = new Scanner(System.in);
		while (!scanned.contains("exit"))
		{
			scanned = scan.nextLine();
			if (scanned.compareTo("")!=0)
			{
				char[] arrayScanned = scanned.toCharArray();
				//echo local
				System.out.println(scanned);
				for (int i = 0 ; i<arrayScanned.length ; i++)
				{
					char key = arrayScanned[i];
					if (key == 'q')
					{
						System.out.println("on tourne a gauche");
						robot.turnRelative(Math.PI/2);
						
					}
					else if (key == 'd')
					{
						System.out.println("on tourne a droite");
						robot.turnRelative(-Math.PI/2);
					}
					else if (key == 'z')
					{
						System.out.println("on avance");
						robot.moveLengthwise(100);
					}
					else if (key == 's')
					{
						System.out.println("on recule");
						robot.moveLengthwise(-100);
					}
				}
			}
		}
	}
	
	public void matchSetUp(Robot robot) throws SerialConnexionException
	{
		robot.useActuator(ActuatorOrder.ELEVATOR_GROUND, true);


		robot.useActuator(ActuatorOrder.ELEVATOR_OPEN_JAW_LEFT, true);
		robot.useActuator(ActuatorOrder.ELEVATOR_OPEN_JAW_RIGHT, false);
		
		robot.useActuator(ActuatorOrder.OPEN_LEFT_GUIDE, false);
		robot.useActuator(ActuatorOrder.OPEN_RIGHT_GUIDE, false);
		
		robot.useActuator(ActuatorOrder.ARM_LEFT_CLOSE, true);
		robot.useActuator(ActuatorOrder.ARM_RIGHT_CLOSE, true);
		
		robot.useActuator(ActuatorOrder.CLOSE_RIGHT_GUIDE, false);
		robot.useActuator(ActuatorOrder.CLOSE_LEFT_GUIDE, false);
		
		robot.useActuator(ActuatorOrder.LEFT_CARPET_FOLDUP, true);
		robot.useActuator(ActuatorOrder.RIGHT_CARPET_FOLDUP, true);
		
		robot.useActuator(ActuatorOrder.LOW_LEFT_CLAP, true);
		robot.useActuator(ActuatorOrder.LOW_RIGHT_CLAP, true);
		
		robot.useActuator(ActuatorOrder.ELEVATOR_CLOSE_JAW_LEFT, true);
		robot.useActuator(ActuatorOrder.ELEVATOR_CLOSE_JAW_RIGHT, true);

		robot.useActuator(ActuatorOrder.ELEVATOR_LOW, true);
	}

}
