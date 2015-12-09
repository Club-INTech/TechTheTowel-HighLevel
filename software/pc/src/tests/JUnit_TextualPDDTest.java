package tests;

import enums.ServiceNames;
import enums.TurningStrategy;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.PathNotFoundException;
import exceptions.PointInObstacleException;
import graphics.Window;
import hook.Hook;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import pathDingDing.Node;
import pathDingDing.PathDingDing;
import robot.RobotReal;
import smartMath.Vec2;
import table.Table;
import utils.Log;

import java.util.ArrayList;

public class JUnit_TextualPDDTest extends JUnit_Test
{
	Table table;
	PathDingDing pf;
	RobotReal robot;
	Log log;
	long time;
	int sum=0;

	public static void main(String[] args) throws Exception
	{                    
	   JUnitCore.main("tests.JUnit_TextualPDDTest");
	}
	
    @Before
    public void setUp() throws Exception
    {
        super.setUp();
        
        log = (Log)container.getService(ServiceNames.LOG);
        table = (Table)container.getService(ServiceNames.TABLE);
        //robot = (RobotReal)container.getService(ServiceNames.ROBOT_REAL);
        time = System.currentTimeMillis();
        pf = (PathDingDing)container.getService(ServiceNames.PATHDINGDING);
		//robot.setPosition(Table.entryPosition);
		//robot.setOrientation(Math.PI);
    }
    
   //@Test
    public void test()
    {
    	ArrayList<Node> path;
		try {
			path = pf.computePath(new Vec2(300, 1000), new Vec2(300, 500));

    		ArrayList<Vec2> pathVec = new ArrayList<Vec2>();
    		for(int i=0 ; i<path.size() ; i++)
    		{
    			pathVec.add(path.get(i).getPosition());
    			log.debug(path.get(i).getPosition());
    		}
    		log.debug(System.currentTimeMillis()-time+" milliSecondes");
    	} catch (PointInObstacleException | PathNotFoundException e) {
    		e.printStackTrace();
    	}

    	try {
			//robot.setTurningStrategy(TurningStrategy.RIGHT_ONLY);
			//robot.moveToLocation(new Vec2(-1000, 1500), new ArrayList<Hook>(), table);
			//robot.moveLengthwise(200, new ArrayList<Hook>());
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    }
    
    /**
     * Meme test, mais en plus muet : ne donne que les milliSecondes passées, sur plusieurs tests
     */
    @Test
    public void testMuted()
    {
    	String timeUsed = "";
    	for(int j=0; j<30; j++)
    	{
	    	time = System.currentTimeMillis();
	    	ArrayList<Node> path;
			try 
			{
				path = pf.computePath(new Vec2(300, 1000), new Vec2(-350, 1000));
				
	    		ArrayList<Vec2> pathVec = new ArrayList<Vec2>();
	    		for(int i=0 ; i<path.size() ; i++)
	    		{
	    			pathVec.add(path.get(i).getPosition());
	    		}
	    	} 
			catch (PointInObstacleException | PathNotFoundException e) 
			{
	    		e.printStackTrace();
			}
			long maBite = System.currentTimeMillis()-time;
			sum+=maBite;
			timeUsed+=maBite+"//";
    	}
    	log.debug(timeUsed);
		log.debug("Temps moyen = "+((float)sum/30)+" ms");
    }
    
}