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
import robot.cardsWrappers.LocomotionCardWrapper;
import smartMath.Vec2;
import utils.Sleep;

/**
 * Tests unitaires pour Deplacements.
 *
 * @author pf
 */
public class JUnit_Locomotion extends JUnit_Test
{

	/** The deplacements. */
	private Locomotion mLocomotion;
	private LocomotionCardWrapper cardWrapper;
	
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
		cardWrapper=(LocomotionCardWrapper)container.getService(ServiceNames.LOCOMOTION_CARD_WRAPPER);
		config.set("couleur", "vert");
		mLocomotion.updateConfig();
		mLocomotion.setPosition(new Vec2 (1381,1000));
		mLocomotion.setOrientation(Math.PI);
	}

	@Test
	public void testMoveLengthwise() 
	{
		try 
		{
			int distance = 210;
			while(true)
			{
				cardWrapper.moveLengthwise(distance);
				while(cardWrapper.isRobotMovingAndAbnormal()[0])
				{
					if(cardWrapper.isRobotMovingAndAbnormal()[1])
						throw new Exception();
				}
				if(cardWrapper.isRobotMovingAndAbnormal()[1])
					throw new Exception();
				
				cardWrapper.moveLengthwise(-distance);
				while(cardWrapper.isRobotMovingAndAbnormal()[0])
				{
					if(cardWrapper.isRobotMovingAndAbnormal()[1])
						throw new Exception();
				}
				if(cardWrapper.isRobotMovingAndAbnormal()[1])
					throw new Exception();


			}
		}
		catch (Exception e)
		{
			log.debug(e, this);
			return;
		}
		
	}	

	/**
	 * Test_tourner.
	 * ATTENTION NE FONCTIONNE QUE DU COTE VERT !
	 * @throws Exception the exception
	 */
	//@Test
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
			mLocomotion.moveLengthwise(2000,null, false);
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
				log.debug("en position : x="+position.x+"; y="+position.y+" après le followpath", this);
			} 
			catch (UnableToMoveException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}
