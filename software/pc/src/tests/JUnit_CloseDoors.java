package tests;

import org.junit.Before;
import org.junit.Test;

import robot.Locomotion;
import smartMath.Vec2;
import enums.ServiceNames;

/**
 * teste la fermeture des portes par la version 0 du script
 * @author julian
 *
 */
public class JUnit_CloseDoors extends JUnit_Test
{
	private Locomotion mLocomotion;

	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		log.debug("JUnit_DeplacementsTest.setUp()");
		mLocomotion = (Locomotion)container.getService(ServiceNames.LOCOMOTION);
		mLocomotion.updateConfig();
		mLocomotion.setPosition(new Vec2(1300, 1200));
		mLocomotion.setOrientation( Math.PI);
	}
	
	@Test
	public void closeThatDoors() throws Exception
	{
		mLocomotion.setRotationnalSpeed(10);
		mLocomotion.setTranslationnalSpeed(10);
	
		//On tourne le robot vers la position
		mLocomotion.turn((Math.PI*0.5 - 0.986), null, false);
	
		//On deplace le robot vers les portes
		mLocomotion.moveLengthwise(807, null, false);
		
		//On ralentit pour eviter de demonter les elements de jeu "Discord-style"
		mLocomotion.setTranslationnalSpeed(3);
		
		//On s'oriente vers les portes
		mLocomotion.turn((Math.PI / 2), null, false);
		
		//On ferme les portes, (20) A CHANGER !!!!!
		mLocomotion.moveLengthwise(20, null, true);
	
		//On recule
		mLocomotion.moveLengthwise(-30, null, false);
		
		//PORTES FERMEES !
	}
}