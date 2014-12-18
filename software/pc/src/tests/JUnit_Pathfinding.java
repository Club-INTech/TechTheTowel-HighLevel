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
import graphics.Window;

public class JUnit_Pathfinding extends JUnit_Test
{
	Window win;
	
    @Before
    public void setUp() throws Exception
    {
        super.setUp();
        win = new Window(new Table(log, config));
    }
    
    //test du pathfinding
    //@Test
    public void testRandPF1() throws Exception
    {
    	ArrayList<Vec2> path = new ArrayList<Vec2>();
    	for(int n = 0; n < 1000; n++)
    	{
	    	try
	    	{
	    		System.out.println("essai :");
				path = PathDingDing.computePath(new Vec2((int)((Math.random() - 0.5) * 1500), (int)(Math.random() * 2000)), new Vec2((int)((Math.random() - 0.5) * 1500), (int)(Math.random() * 2000)), (Table)container.getService(ServiceNames.TABLE));
				for(int i = 0; i < path.size(); i++)
		    	{
		    		System.out.println("-----------------------------" + path.get(i).toString());
		    	}
				win.getPanel().drawArrayList(path);
				Thread.sleep(10);
	    	}
	    	catch(PathNotFoundException e)
	    	{
	    		System.out.println("--------------not on table------------------");
	    	}
    	}
    }
    
    @Test
    public void testClickedPF() throws Exception
    {
    	while(true)
    	{
	    	try
	    	{
		    	win.getPanel().drawArrayList(PathDingDing.computePath(win.getMouse().getLeftClickPosition(), win.getMouse().getRightClickPosition(), (Table)container.getService(ServiceNames.TABLE)));
		    	Thread.sleep(100);
	    	}
	    	catch(PathNotFoundException e)
	    	{
	    		System.out.println("--------------not on table------------------");
	    		Thread.sleep(200);
	    	}
    	}
    }
}