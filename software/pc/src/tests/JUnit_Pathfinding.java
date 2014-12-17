package tests;

import org.junit.*;

import pathDingDing.PathDingDing;
import container.Container;
import table.Table;
import smartMath.Point;
import smartMath.Vec2;
import smartMath.Path;

import java.util.ArrayList;

import enums.ServiceNames;
import exceptions.*;

public class JUnit_Pathfinding extends JUnit_Test
{
    @Before
    public void setUp() throws Exception
    {
        super.setUp();
    }
    
    //test de l'intersection de deux segments
    @Test
    public void testIntersection() throws Exception
    {
    	if( !PathDingDing.intersects(new Point(0, 0), new Point(1, 1), new Point(0, 1), new Point(1, 0)) )
    		Assert.fail();
    }
    
    //test du pathfinding
    @Test
    public void testPF1() throws Exception
    {
    	ArrayList<Vec2> path = new ArrayList<Vec2>();
    	for(int n = 0; n < 100; n++)
    	{
	    	try
	    	{
	    		System.out.println("essai :");
				path = PathDingDing.computePath(new Vec2((int)((Math.random() - 0.5) * 1500), (int)(Math.random() * 2000)), new Vec2((int)((Math.random() - 0.5) * 1500), (int)(Math.random() * 2000)), (Table)container.getService(ServiceNames.TABLE));
				for(int i = 0; i < path.size(); i++)
		    	{
		    		System.out.println("-----------------------------" + path.get(i).toString());
		    	}
	    	}
	    	catch(PathNotFoundException e)
	    	{
	    		System.out.println("--------------not on table------------------");
	    	}
    	}
    }
}