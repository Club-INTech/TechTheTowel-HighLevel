package tests;

import enums.ServiceNames;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.PathNotFoundException;
import exceptions.PointInObstacleException;
import hook.Hook;
import org.junit.Before;
import org.junit.Test;
import robot.Robot;
import smartMath.Vec2;
import strategie.GameState;
import table.Table;
import utils.Log;

import java.util.ArrayList;
import java.util.Random;

public class JUnit_TestBaliseClement extends JUnit_Test
{

	GameState<Robot> clement;
	Table table;
	// On utulise le log du robot au lieu de celui de JUnit pour le PDD et pour l'entraînement
	Log log;
	long time;
	
	@SuppressWarnings("unchecked")
    @Before
	public void setUp() throws Exception
	{
		//creation des objets pour le test
		super.setUp();                                                                                                                                
		clement = (GameState<Robot>)container.getService(ServiceNames.GAME_STATE);
		
		//position initiale du robot
		clement.robot.setPosition(Table.entryPosition);
		clement.robot.setOrientation(Math.PI);
		
		table = (Table)container.getService(ServiceNames.TABLE);
		log = (Log)container.getService(ServiceNames.LOG);
		this.time = System.currentTimeMillis();
	}	
		
	@Test
	public void test() {
		while((System.currentTimeMillis()-time)<90000)
		{
			Vec2 point = NextPoint();
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
				log.debug("le point : "+point+" est dans un obtacle");
			}
		}
	}
	
	/**
	 * La m�thode NextPoint renvoie un point g�n�r� al�atoirement
	 * @return un point sur la table ([-1500,1500],[0,2000])
	 */
	private Vec2 NextPoint() 
	{
		Random random = new Random();
		Vec2 point = new Vec2(random.nextInt(3001)-1500,random.nextInt(2001));
		log.debug(point);
		return(point);
	}
}