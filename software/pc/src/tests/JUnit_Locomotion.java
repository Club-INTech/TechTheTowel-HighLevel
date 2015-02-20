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
		mLocomotion.setPosition(new Vec2(0, 1000));
		mLocomotion.setOrientation(Math.PI);
		mLocomotion.setTranslationnalSpeed(170);
		mLocomotion.setRotationnalSpeed(160);
		config.set("couleur", "jaune");
		mLocomotion.updateConfig();
	}

	@Test
	public void testMoveLengthwise() throws Exception
	{
		mLocomotion.moveLengthwise(100,  new ArrayList<Hook>(), false);
/*		mLocomotion.moveLengthwise(-100,  new ArrayList<Hook>(), false);
		mLocomotion.turn(Math.PI/2, new ArrayList<Hook>());
		mLocomotion.turn(-Math.PI/2, new ArrayList<Hook>());
		mLocomotion.turn(0, new ArrayList<Hook>());*/
	}

	/**
	 * Test_tourner.
	 * ATTENTION NE FONCTIONNE QUE DU COTE VERT !
	 * @throws Exception the exception
	 */
	@Test
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
			mLocomotion.moveLengthwise(-2000,null, false);
		} 
		catch (UnableToMoveException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		position = mLocomotion.getPosition();
		log.debug("en position : x="+position.x+"; y="+position.y, this);
		log.debug("orientation : "+mLocomotion.getOrientation(), this);
		while (true)
		{
			try 
			{
				mLocomotion.followPath(path, null, DirectionStrategy.FASTEST);
				position = mLocomotion.getPosition();
				log.debug("en position : x="+position.x+"; y="+position.y, this);
			} 
			catch (UnableToMoveException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}
