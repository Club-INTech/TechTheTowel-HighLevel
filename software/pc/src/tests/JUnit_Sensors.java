package tests;

import enums.ServiceNames;
import enums.USsensors;
import exceptions.ContainerException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.Locomotion.UnexpectedObstacleOnPathException;
import exceptions.PathNotFoundException;
import exceptions.PointInObstacleException;
import exceptions.serial.SerialConnexionException;
import exceptions.serial.SerialManagerException;
import hook.Hook;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import robot.Locomotion;
import robot.Robot;
import robot.cardsWrappers.SensorsCardWrapper;
import smartMath.Circle;
import smartMath.Vec2;
import strategie.GameState;
import table.Table;
import threads.ThreadInterface;
import utils.Sleep;

import java.util.ArrayList;
import java.util.Random;

/**
 * Test des capteurs : les obstacles doivent être détectés
 *
 * @author marsu
 */
public class JUnit_Sensors extends JUnit_Test
{

	/** The capteurs. */
	SensorsCardWrapper capteurs;
	
	private Locomotion mLocomotion;
	
	GameState<Robot> state;
	
	/* (non-Javadoc)
	 * @see tests.JUnit_Test#setUp()
	 */
	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception 
	{
		super.setUp();
		state = (GameState<Robot>)container.getService(ServiceNames.GAME_STATE);
		
		log.debug("JUnit_ActionneursTest.setUp()");
		capteurs = (SensorsCardWrapper)container.getService(ServiceNames.SENSORS_CARD_WRAPPER);
		
		config.set("capteurs_on", "true");
		capteurs.updateConfig();
		
		container.getService(ServiceNames.THREAD_SENSOR);
		//container.getService(ServiceNames.THREAD_TIMER);
		
		//locomotion
		mLocomotion = (Locomotion)container.getService(ServiceNames.LOCOMOTION);
		mLocomotion.updateConfig();

		//mLocomotion.setPosition(new Vec2 (1500-320-77,1000));
		mLocomotion.setPosition(Table.entryPosition);// milieu de table
		mLocomotion.setOrientation(Math.PI);
		container.getService(ServiceNames.THREAD_INTERFACE);
		container.startInstanciedThreads();

	}

	/**
	 * Desactivation_capteur.
	 * verifie que la desactivation des capteurs avants est efficace
	 *
	 * @throws Exception the exception
	 */
/*	@Test
	public void desactivation_capteur() throws Exception
	{
		log.debug("JUnit_CapteursTest.desactivation_capteur()");

		// Avec capteurs
		log.debug((capteurs.getUSSensorValue(USsensors.ULTRASOUND_LEFT_BACK)));
	//	Assert.assertTrue(capteurs.mesurer_infrarouge() != 3000);
		Assert.assertTrue(((int)capteurs.getUSSensorValue(USsensors.ULTRASOUND_LEFT_BACK)) != 3000);

		// Sans capteurs
		config.set("capteurs_on", "false");
		capteurs.updateConfig();
		log.debug(((int)capteurs.getUSSensorValue(USsensors.ULTRASOUND_LEFT_BACK)));
	//	Assert.assertTrue(capteurs.mesurer_infrarouge() == 3000);
		Assert.assertTrue(((int)capteurs.getUSSensorValue(USsensors.ULTRASOUND_LEFT_BACK)) == 3000);

		// Et re avec
		config.set("capteurs_on", "true");
		capteurs.updateConfig();
		Assert.assertTrue(((int)capteurs.getUSSensorValue(USsensors.ULTRASOUND_LEFT_BACK)) != 3000);
	//	Assert.assertTrue(capteurs.mesurer_infrarouge() != 3000);
		Assert.assertTrue(((int)capteurs.getUSSensorValue(USsensors.ULTRASOUND_LEFT_BACK)) != 3000);

	}*/
	
//	@Test
	public void testEvitement() throws PointInObstacleException
	{
		log.debug("Test d'évitement");
		try 
		{	
			state.robot.moveLengthwiseWithoutDetection(250);
		} 
		catch (UnableToMoveException e1)
		{
			;
		}

		log.critical("Fin de moveLengthWise");
		while(true)
		{
			try
			{
				state.robot.moveToCircle(new Circle(new Vec2(-700, 900),0),  new ArrayList<Hook>(), (Table)container.getService(ServiceNames.TABLE));
			}
			catch (UnableToMoveException | PathNotFoundException | ContainerException | SerialManagerException e) 
			{
				log.critical("!!!!!! Catch de"+e+" dans testEvitement !!!!!!");
			}	
		}
	}
	
	//@Test
	public void testDetecting()
	{
		log.debug("Test d'évitement");
		try 
		{	
			state.robot.moveLengthwise(500);
			state.robot.turn(Math.PI/2);
		} 
		catch (UnableToMoveException e) 
		{
			log.critical( e.logStack());
		}
		
		while(true)
			;
	}
	
//	@Test
	public void testDetectionTournante()
	{
		log.debug("Test d'évitement");
		
	/*	try 
		{
			state.robot.moveLengthwise(250);
		} 
		catch (UnableToMoveException e) 
		{
			log.critical( e.logStack(), this);
		}*/
		
		while(true)
		{
			try 
			{
				state.robot.turn(- Math.PI/2);
				state.robot.sleep(500);
				state.robot.turn( Math.PI);
				state.robot.sleep(500);
			} 
			catch (UnableToMoveException e1)
			{
				log.critical( e1.logStack());
			}
		}
	}
	
	//@Test
	public void testMoveThenDetect()
	{
		
		try 
		{
			state.robot.moveLengthwiseWithoutDetection(500);
			state.robot.turn(- Math.PI/2);
		} 
		catch (UnableToMoveException e1)
		{
			log.critical( e1.logStack());
		}
		while (true)
		{
			;
		}
	}
	
	//@Test
	public void testMoveForwardBackward()
	{
		
		try 
		{
			state.robot.moveLengthwiseWithoutDetection(500);
		} 
		catch (UnableToMoveException e1)
		{
			log.critical( e1.logStack());
		}
		while (true)
		{
			try 
			{
				state.robot.moveLengthwiseWithoutDetection(500);
				state.robot.moveLengthwiseWithoutDetection(-500);
			} 
			catch (UnableToMoveException e1)
			{
				log.critical( e1.logStack());
			}
		}
	}
	
	//@Test
	public void testSensorEnnemyInDiscWithoutMovement()
	{
		log.debug("Test d'évitement fixe");
		while(true)
		{
			try
			{
				mLocomotion.detectEnemyInDisk(true, false, state.robot.getPosition());
			}
			catch (UnexpectedObstacleOnPathException unexpectedObstacle)
	        {
                log.critical("Haut: Catch de "+unexpectedObstacle+" dans moveToPointException"); 

            	long detectionTime = System.currentTimeMillis();
                log.critical("Détection d'un ennemi! Abandon du mouvement.");
            	while(System.currentTimeMillis() - detectionTime < 600)
            	{
            		try
            		{
            			mLocomotion.detectEnemyInDisk(true, false, state.robot.getPosition());
            			break;
            		}
            		catch(UnexpectedObstacleOnPathException e2)
            		{
            			log.critical( e2.logStack());
            		}
            	}
			}
		}
	}
	
	@Test
	public void testSensorEnnemyWithoutMovement() throws InterruptedException, SerialConnexionException {
		log.debug("Test des capteurs fixe");
		state.robot.disableFeedbackLoop();
		while(true)
		{
			state.robot.getPosition();
			Thread.sleep(100);
		}
	}
	
	//@Test
	public void testDistaanceToClosestEnnemy()
	{
		while(true)
		{
			state.table.getObstacleManager().distanceToClosestEnemy(state.robot.getPosition(), new Vec2(500,500));
		}
	} 
	
	
	//@Test
	public void testSensorEnnemyWithMovement()
	{
		log.debug("Test des capteurs fixe");
		while(true)
		{
			try 
			{
				state.robot.moveLengthwise(50);
				state.robot.sleep(500);
			} 
			catch (UnableToMoveException e) 
			{
				try {
					state.robot.moveLengthwise(-50);
				} catch (UnableToMoveException e1) {
					e1.printStackTrace();
				}				
			}
			try 
			{
				state.robot.moveLengthwise(-50);
				state.robot.sleep(500);
			} 
			catch (UnableToMoveException e) 
			{
				try {
					state.robot.moveLengthwise(50);
				} catch (UnableToMoveException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	
	
	
   // @Test
	public void testCapteurDeplacement() throws SerialConnexionException, PointInObstacleException
	{
    	matchSetUp(state.robot, false);
    	try 
    	{
			state.robot.moveLengthwise(300);
		} 
    	catch (UnableToMoveException e2) 
    	{
    		log.critical( e2.logStack());
		}
		log.debug("Test d'évitement");
		Random rand = new Random();
    	while(true)
    	{
			int x=0,y=0;
			try 
			{
				x = rand.nextInt(3000)-1500;
				y = rand.nextInt(2000);
				state.robot.moveToLocation(new Vec2 (x,y),new ArrayList<Hook>(), state.table);
			} 
			catch (UnableToMoveException e1)
			{
				log.critical("!!!!! Catch de"+e1+" dans testEvitement !!!!!");
				break;
			} 
			catch (PathNotFoundException e) 
			{
				log.debug("pas de chemin trouvé : ("+x+";"+y+")");
			}
    	}
	}

	/**
	 * le thread principal ne se deplace pas mais les capterus sont ON donc on detecte les ennemis
	 * @throws Exception
	 */
    //@Test
    public void faux_test() throws Exception
    {
        config.set("capteurs_on", "true");
        for(int i = 0; i < 10000; i++)
        {
            Sleep.sleep(100);
        }
    }

}
