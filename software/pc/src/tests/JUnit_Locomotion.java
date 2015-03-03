package tests;


import hook.Hook;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import enums.ServiceNames;
import exceptions.Locomotion.UnableToMoveException;
import robot.DirectionStrategy;
import robot.Locomotion;
import smartMath.Vec2;

/**
 * Tests unitaires pour Deplacements.
 *
 * @author pf
 */
public class JUnit_Locomotion extends JUnit_Test
{

	/** The deplacements. */
	private Locomotion mLocomotion;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		// appelé avant chaque batterie de test
		/*
		 * tearDownAfterClass
		 * @AfterClass apres chaque batterie de test de la meme classe JUnit
		 */
	}

	
	
	/* (non-Javadoc)
	 * @see tests.JUnit_Test#setUp()
	 */
	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		log.debug("JUnit_DeplacementsTest.setUp()", this);
		mLocomotion = (Locomotion)container.getService(ServiceNames.LOCOMOTION);
		config.set("couleur", "vert");
		mLocomotion.updateConfig();
		mLocomotion.setPosition(new Vec2(0, 1000));
		mLocomotion.setOrientation(Math.PI);
		mLocomotion.setTranslationnalSpeed(170);
		mLocomotion.setRotationnalSpeed(160);
	}

	@Test
	public void testMoveLengthwise() throws Exception
	{
		mLocomotion.moveLengthwise(300,  new ArrayList<Hook>(), false);
		Vec2 position = mLocomotion.getPosition();
		log.debug("en position : x="+position.x+"; y="+position.y, this);
		mLocomotion.moveLengthwise(-300,  new ArrayList<Hook>(), false);
		position = mLocomotion.getPosition();
		log.debug("en position : x="+position.x+"; y="+position.y, this);
	}	

	/**
	 * Test_tourner.
	 * ATTENTION NE FONCTIONNE QUE DU COTE VERT !
	 * @throws Exception the exception
	 */
	/*@Test
	public void test()
	{
		ArrayList<Vec2> path = new ArrayList<Vec2>();
		path.add(new Vec2 (-500,1000));
		path.add(new Vec2 (500,1000));
		log.debug("JUnit_DeplacementsTest", this);
		Vec2 position = mLocomotion.getPosition();
		log.debug("en position : x="+position.x+"; y="+position.y, this);
		try 
		{
			mLocomotion.moveLengthwise(500,null, false);
		} 
		catch (UnableToMoveException e) 
		{
			e.printStackTrace();
		}
		position = mLocomotion.getPosition();
		log.debug("en position : x="+position.x+"; y="+position.y, this);
		log.debug("orientation : "+mLocomotion.getOrientation(), this);
		while (true)
		{
			try 
			{
				mLocomotion.followPath(path, null, DirectionStrategy.FORCE_FORWARD_MOTION);
				position = mLocomotion.getPosition();
				log.debug("en position : x="+position.x+"; y="+position.y, this);
			} 
			catch (UnableToMoveException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}*/
	
}
