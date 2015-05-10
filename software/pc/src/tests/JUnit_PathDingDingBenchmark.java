package tests;

import java.util.ArrayList;
import java.util.EnumSet;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;

import pathDingDing.PathDingDing;
import smartMath.Vec2;
import table.Table;
import enums.ObstacleGroups;
import enums.ServiceNames;
import exceptions.InObstacleException;
import exceptions.PathNotFoundException;

public class JUnit_PathDingDingBenchmark extends JUnit_Test
{
	Table table;
	PathDingDing pf;
	

	public static void main(String[] args) throws Exception
	{                    
	   JUnitCore.main("tests.JUnit_PathDingDingBenchmark");
	}
	
    @Before
    public void setUp() throws Exception
    {
        super.setUp();
        table = (Table)container.getService(ServiceNames.TABLE);
        pf = (PathDingDing)container.getService(ServiceNames.PATHDINGDING);
    }
    
    @Test
    public void testClickedPF() throws Exception
    {
    	ArrayList<Vec2> startPoint = new ArrayList<Vec2>();
    	startPoint.add(new Vec2(-600, 500));
    	startPoint.add(new Vec2(-1120, 1580));
    	startPoint.add(new Vec2(-685, 240));
    	
    	ArrayList<Vec2> endPoint = new ArrayList<Vec2>();
    	endPoint.add(new Vec2(-200, 800));
    	endPoint.add(new Vec2(1120, 1580));
    	endPoint.add(new Vec2(770, 240));
    	
    	long temps = 0;
    	
    	for(int j = 0; j < 1000; j++)
	    	for(int i = 0; i < startPoint.size(); i++)
	    	{
		    	try
		    	{
		    		long start = System.nanoTime();
			    	@SuppressWarnings("unused")
					int size = pf.computePath(startPoint.get(i), endPoint.get(i), EnumSet.allOf(ObstacleGroups.class)).size();
			    	long end = System.nanoTime();
			    	temps += (end - start) / 1000;
			    }
		    	catch(PathNotFoundException e)
		    	{
		    		log.debug("pas de chemin trouvé", this);
		    	}
		    	catch(InObstacleException e)
		    	{
		    		log.debug("point d'arrivée dans un obstacle", this);
		    	}
	    	}
    	System.out.println("temps moyen : " + temps / 1000 + "µs");
    }
}
