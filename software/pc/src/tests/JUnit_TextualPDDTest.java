package tests;

import java.util.ArrayList;

import org.junit.*;
import org.junit.runner.JUnitCore;

import pathDingDing.*;
import robot.RobotReal;
import smartMath.Vec2;
import table.Table;
import utils.Log;
import enums.*;
import exceptions.PathNotFoundException;
import exceptions.Locomotion.UnableToMoveException;
import graphics.Window;
import hook.Hook;

public class JUnit_TextualPDDTest extends JUnit_Test
{	
	Window win;
	Table table;
	PathDingDing pf;
	RobotReal robot;
	Log log;
	long time;
	

	public static void main(String[] args) throws Exception
	{                    
	   JUnitCore.main("tests.JUnit_Pathfinding");
	}
	
    @Before
    public void setUp() throws Exception
    {
        super.setUp();
        log = (Log)container.getService(ServiceNames.LOG);
        table = (Table)container.getService(ServiceNames.TABLE);
        robot = (RobotReal)container.getService(ServiceNames.ROBOT_REAL);
        pf = (PathDingDing)container.getService(ServiceNames.PATHDINGDING);
    }
    
    @Test
    public void test()
    {
    	time = System.currentTimeMillis();
    	ArrayList<Node> path = pf.computePath(new Vec2(0, 1750), new Vec2(-300, 1000));
    	ArrayList<Vec2> pathVec = new ArrayList<Vec2>();
    	for(int i=0 ; i<path.size() ; i++)
    	{
    		pathVec.add(path.get(i).getPosition());
    		log.debug(path.get(i).getPosition());
    	}
    	log.debug(System.currentTimeMillis()-time);
    	/*
    	try {
			robot.moveToLocation(new Vec2(-300, 1000), new ArrayList<Hook>(), table);
		} catch (PathNotFoundException | UnableToMoveException e) {
			e.printStackTrace();
		} */
    	
    }
    
}