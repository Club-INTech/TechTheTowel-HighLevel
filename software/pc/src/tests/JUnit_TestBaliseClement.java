package tests;

import enums.ServiceNames;
import enums.Speed;
import exceptions.ContainerException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.PathNotFoundException;
import exceptions.PointInObstacleException;
import exceptions.serial.SerialManagerException;
import hook.Hook;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import robot.RobotReal;
import smartMath.Vec2;
import strategie.GameState;
import table.Table;
import utils.Log;

import java.util.ArrayList;
import java.util.Random;

public class JUnit_TestBaliseClement extends JUnit_Test
{

	GameState<RobotReal> clement;
	Table table;
	// On utilise le log du robot au lieu de celui de JUnit pour le PDD et pour l'entraînement
	Log log;
	long time;

	public static void main(String[] args) throws Exception
	{
		JUnitCore.main("tests.JUnit_TestBaliseClement");
	}
	
	@SuppressWarnings("unchecked")
    @Before
	public void setUp() throws Exception
	{
		//creation des objets pour le test
		super.setUp();                                                                                                                                
		clement = (GameState<RobotReal>)container.getService(ServiceNames.GAME_STATE);
		
		//position initiale du robot
		clement.robot.setPosition(Table.entryPosition);
		clement.robot.setOrientation(Math.PI);
		clement.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);
		
		table = (Table)container.getService(ServiceNames.TABLE);
		log = (Log)container.getService(ServiceNames.LOG);
		table.deleteAllTheShells();
		this.time = System.currentTimeMillis();
	}	
		
	@Test
	public void test() throws SerialManagerException, ContainerException, UnableToMoveException {
		//container.getService(ServiceNames.THREAD_TIMER);
		//container.getService(ServiceNames.THREAD_INTERFACE);
		//container.startInstanciedThreads();
		clement.robot.moveLengthwise(100, new ArrayList<Hook>(), false);
		while(true || (System.currentTimeMillis()-time)<90000)
		{
			Vec2 point = nextPoint();
			try
			{
				clement.robot.moveToLocation(point, new ArrayList<Hook>(), table);
			}
			catch (PathNotFoundException e)
			{
				log.debug("pas de chemin entre : "+clement.robot.getPosition()+" et : "+point);
			}
			catch (UnableToMoveException e)
			{
				log.debug("robot bloque");
				return;//on arrete le test pour preserver la mecanique du robot
			}
			catch (PointInObstacleException e)
			{
				log.debug("le point : "+point+" est dans un obtacle (normal)");
			}
		}
	}
	
	/**
	 * La m�thode NextPoint renvoie un point g�n�r� al�atoirement
	 * @return un point sur la table ([-1500,1500],[0,2000])
	 */
	private Vec2 nextPoint()
	{
		Random random = new Random();
		Vec2 point = new Vec2(random.nextInt(3001)-1500,random.nextInt(2001));
		log.debug(point);
		return(point);
	}
}