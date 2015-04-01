package tests;

import hook.Hook;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

import enums.SensorNames;
import enums.ServiceNames;
import exceptions.ContainerException;
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
 * TODO : comprendre l'utilité du test desactivation_capteur et faux_test.
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
		config.set("couleur", "vert");
		mLocomotion.updateConfig();
		mLocomotion.setPosition(new Vec2 (1381,1000));
		mLocomotion.setOrientation(Math.PI);
		mLocomotion.setTranslationnalSpeed(170);
		mLocomotion.setRotationnalSpeed(160);
		
		container.startInstanciedThreads();

	}

	/**
	 * Desactivation_capteur.
	 *
	 * @throws Exception the exception
	 */
//	@Test
	public void desactivation_capteur() throws Exception
	{
		log.debug("JUnit_CapteursTest.desactivation_capteur()", this);

		// Avec capteurs
		log.debug(((int[])capteurs.getSensorValue(SensorNames.ULTRASOUND_FRONT_SENSOR))[1], this);
	//	Assert.assertTrue(capteurs.mesurer_infrarouge() != 3000);
		Assert.assertTrue(((int[])capteurs.getSensorValue(SensorNames.ULTRASOUND_FRONT_SENSOR))[1] != 3000);

		// Sans capteurs
		config.set("capteurs_on", "false");
		capteurs.updateConfig();
		log.debug(((int[])capteurs.getSensorValue(SensorNames.ULTRASOUND_FRONT_SENSOR))[1], this);
	//	Assert.assertTrue(capteurs.mesurer_infrarouge() == 3000);
		Assert.assertTrue(((int[])capteurs.getSensorValue(SensorNames.ULTRASOUND_FRONT_SENSOR))[1] == 3000);

		// Et re avec
		config.set("capteurs_on", "true");
		capteurs.updateConfig();
		Assert.assertTrue(((int[])capteurs.getSensorValue(SensorNames.ULTRASOUND_FRONT_SENSOR))[1] != 3000);
	//	Assert.assertTrue(capteurs.mesurer_infrarouge() != 3000);
		Assert.assertTrue(((int[])capteurs.getSensorValue(SensorNames.ULTRASOUND_FRONT_SENSOR))[1] != 3000);

	}
	
	@Test
	public void testEvitement()
	{
		log.debug("Test d'évitement", this);

		try 
		{	
			state.robot.moveLengthwiseWithoutDetection(state.robot.getPosition().x-800);
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
				state.robot.moveToCircle(new Circle(new Vec2(-600, 1000),0),  new ArrayList<Hook>(), (Table)container.getService(ServiceNames.TABLE));
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
		} 
		catch (UnableToMoveException e) 
		{
			log.critical("!!!!!! Catch de"+e+" dans testWithoutDetecting !!!!!!" , this);;
		}
	}
	
	//@Test
	public void testDetectionTournante()
	{
		log.debug("Test d'évitement", this);
		
		while(true)
		{
			try 
			{
				state.robot.turn(Math.PI);
				Sleep.sleep(500);
				state.robot.turn(- Math.PI/2);
				Sleep.sleep(500);  
				state.robot.turn(Math.PI);
				Sleep.sleep(500);
				state.robot.turn(  Math.PI/2);
				Sleep.sleep(500);
			} 
			catch (UnableToMoveException e1)
			{
				log.critical("!!!!! Catch de"+e1+" dans testDetectionTournante !!!!!" , this);
			}
		}
	}
	
	//@Test
	public void testvide()
	{
		while (true)
		{
			
		}
	}
	

		
	//@Test
	public void testCapteurFixe()
	{
		log.debug("Test d'évitement fixe", this);
		while(true)
		{
			try
			{
				mLocomotion.detectEnemy(true, false, state.robot.getPosition());
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
            			mLocomotion.detectEnemy(true, false, state.robot.getPosition());
            			break;
            		}
            		catch(UnexpectedObstacleOnPathException e2)
            		{
                        log.critical("Catch de "+e2+" dans moveToPointException", this);
            		}
            	}
			}
		}	
	}
	
	
    //@Test
	public void testCapteurDeplacement()
	{
		log.debug("Test d'évitement", this);
		try 
		{
			state.robot.moveLengthwise(300);
		} 
		catch (UnableToMoveException e1)
		{
			log.critical("!!!!! Catch de"+e1+" dans testEvitement !!!!!" , this);
		}
	}

	
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
