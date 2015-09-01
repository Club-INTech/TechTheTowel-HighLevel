package tests;

import hook.Hook;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

import enums.ObstacleGroups;
import enums.SensorNames;
import enums.ServiceNames;
import exceptions.ContainerException;
import exceptions.InObstacleException;
import exceptions.PathNotFoundException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.Locomotion.UnexpectedObstacleOnPathException;
import exceptions.serial.SerialConnexionException;
import exceptions.serial.SerialManagerException;
import robot.Locomotion;
import robot.Robot;
import robot.cardsWrappers.SensorsCardWrapper;
import smartMath.Circle;
import smartMath.Vec2;
import strategie.GameState;
import table.Table;
import utils.Sleep;

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
		
		log.debug("JUnit_ActionneursTest.setUp()", this);
		capteurs = (SensorsCardWrapper)container.getService(ServiceNames.SENSORS_CARD_WRAPPER);
		
		config.set("capteurs_on", "true");
		capteurs.updateConfig();
		
		container.getService(ServiceNames.THREAD_SENSOR);
		container.getService(ServiceNames.THREAD_TIMER);
		
		//locomotion
		mLocomotion = (Locomotion)container.getService(ServiceNames.LOCOMOTION);
		mLocomotion.updateConfig();

		//mLocomotion.setPosition(new Vec2 (1500-320-77,1000));
		mLocomotion.setPosition(new Vec2 (300,1000));// milieu de table
		mLocomotion.setOrientation(Math.PI);
		
		container.startInstanciedThreads();

	}

	/**
	 * Desactivation_capteur.
	 * verifie que la desactivation des capteurs avants est efficace
	 *
	 * @throws Exception the exception
	 */
//	@Test
	public void desactivation_capteur() throws Exception
	{
		log.debug("JUnit_CapteursTest.desactivation_capteur()", this);

		// Avec capteurs
		log.debug((capteurs.getSensorValue(SensorNames.ULTRASOUND)), this);
	//	Assert.assertTrue(capteurs.mesurer_infrarouge() != 3000);
		Assert.assertTrue(((int)capteurs.getSensorValue(SensorNames.ULTRASOUND)) != 3000);

		// Sans capteurs
		config.set("capteurs_on", "false");
		capteurs.updateConfig();
		log.debug(((int)capteurs.getSensorValue(SensorNames.ULTRASOUND)), this);
	//	Assert.assertTrue(capteurs.mesurer_infrarouge() == 3000);
		Assert.assertTrue(((int)capteurs.getSensorValue(SensorNames.ULTRASOUND)) == 3000);

		// Et re avec
		config.set("capteurs_on", "true");
		capteurs.updateConfig();
		Assert.assertTrue(((int)capteurs.getSensorValue(SensorNames.ULTRASOUND)) != 3000);
	//	Assert.assertTrue(capteurs.mesurer_infrarouge() != 3000);
		Assert.assertTrue(((int)capteurs.getSensorValue(SensorNames.ULTRASOUND)) != 3000);

	}
	
//	@Test
	public void testEvitement() throws InObstacleException
	{
		log.debug("Test d'évitement", this);
		try 
		{	
			state.robot.moveLengthwiseWithoutDetection(250);
		} 
		catch (UnableToMoveException e1)
		{
			;
		}

		log.critical("Fin de moveLengthWise" , this);
		while(true)
		{
			try
			{
				state.robot.moveToCircle(new Circle(new Vec2(-700, 900),0),  new ArrayList<Hook>(), (Table)container.getService(ServiceNames.TABLE),EnumSet.noneOf(ObstacleGroups.class));
			}
			catch (UnableToMoveException | PathNotFoundException | ContainerException | SerialManagerException e) 
			{
				log.critical("!!!!!! Catch de"+e+" dans testEvitement !!!!!!" , this);
			}	
		}
	}
	
	//@Test
	public void testDetecting()
	{
		log.debug("Test d'évitement", this);
		try 
		{	
			state.robot.moveLengthwise(500);
			state.robot.turn(Math.PI/2);
		} 
		catch (UnableToMoveException e) 
		{
			log.critical( e.logStack(), this);
		}
		
		while(true)
			;
	}
	
//	@Test
	public void testDetectionTournante()
	{
		log.debug("Test d'évitement", this);
		
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
				log.critical( e1.logStack(), this);
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
			log.critical( e1.logStack(), this);
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
			log.critical( e1.logStack(), this);
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
				log.critical( e1.logStack(), this);
			}
		}
	}
	
	@Test
	public void testSensorEnnemyInDiscWithoutMovement()
	{
		log.debug("Test d'évitement fixe", this);
		while(true)
		{
			try
			{
				mLocomotion.detectEnemyInDisk(true, false, state.robot.getPosition());
			}
			catch (UnexpectedObstacleOnPathException unexpectedObstacle)
	        {
                log.critical("Haut: Catch de "+unexpectedObstacle+" dans moveToPointException", this); 

            	long detectionTime = System.currentTimeMillis();
                log.critical("Détection d'un ennemi! Abandon du mouvement.", this);
            	while(System.currentTimeMillis() - detectionTime < 600)
            	{
            		try
            		{
            			mLocomotion.detectEnemyInDisk(true, false, state.robot.getPosition());
            			break;
            		}
            		catch(UnexpectedObstacleOnPathException e2)
            		{
            			log.critical( e2.logStack(), this);
            		}
            	}
			}
		}
	}
	
	//@Test
	public void testSensorEnnemyWithoutMovement()
	{
		log.debug("Test des capteurs fixe", this);
		while(true)
		{
			;	
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
		log.debug("Test des capteurs fixe", this);
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
	public void testCapteurDeplacement() throws SerialConnexionException, InObstacleException
	{
    	matchSetUp(state.robot, false);
    	try 
    	{
			state.robot.moveLengthwise(300);
		} 
    	catch (UnableToMoveException e2) 
    	{
    		log.critical( e2.logStack(), this);
		}
		log.debug("Test d'évitement", this);
		Random rand = new Random();
    	while(true)
    	{
			int x=0,y=0;
			try 
			{
				x = rand.nextInt(3000)-1500;
				y = rand.nextInt(2000);
				state.robot.moveToLocation(new Vec2 (x,y),new ArrayList<Hook>(), state.table, EnumSet.noneOf(ObstacleGroups.class));
			} 
			catch (UnableToMoveException e1)
			{
				log.critical("!!!!! Catch de"+e1+" dans testEvitement !!!!!" , this);
				break;
			} 
			catch (PathNotFoundException e) 
			{
				log.debug("pas de chemin trouvé : ("+x+";"+y+")", this);
			}
			catch (InObstacleException e) 
			{
				log.debug("dans un obstacle!", this);
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
