package tests;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import robot.Locomotion;
import robot.RobotReal;
import smartMath.Vec2;
import enums.ServiceNames;
import enums.Speed;
import exceptions.Locomotion.UnableToMoveException;
import hook.Hook;

/**
 * teste la fermeture des portes par la version 0 du script
 * @author julian
 *
 */
public class JUnit_CloseDoors extends JUnit_Test
{
	private RobotReal mRobot;

	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		log.debug("JUnit_DeplacementsTest.setUp()");
		mRobot = (RobotReal)container.getService(ServiceNames.ROBOT_REAL);
		mRobot.updateConfig();
	}
	
	@Test
	public void closeThatDoors() throws UnableToMoveException
	{
		ArrayList<Hook> emptyList = new ArrayList<Hook>();
		//On ralentit pour eviter de demonter les elements de jeu "Discord-style"
		mRobot.setLocomotionSpeed(Speed.SLOW);
	
		//On tourne le robot vers la position
		mRobot.turn((Math.PI*0.5 + 0.8), emptyList, false);
	
		//On deplace le robot vers les portes
		mRobot.moveLengthwise(380, emptyList, false);
		
		//On s'oriente vers les portes
		mRobot.turn(-(Math.PI / 2), emptyList, false);
		
		//On ferme les portes, (20) A CHANGER !!!!!
		mRobot.moveLengthwise(-600, emptyList, true);
	
		//On recule
		mRobot.moveLengthwise(200, emptyList, false);
	}
}