package tests;

import org.junit.*;

import pathDingDing.PathDingDing;
import container.Container;
import table.Table;
import robot.RobotReal;
import smartMath.*;

import java.util.ArrayList;

import enums.ServiceNames;
import exceptions.*;
import graphics.Window;

public class JUnit_Pathfinding extends JUnit_Test
{
	Window win;
	Table table;
	PathDingDing pf;
	
    @Before
    public void setUp() throws Exception
    {
        super.setUp();
        table = (Table)container.getService(ServiceNames.TABLE);
        win = new Window(table, (RobotReal)container.getService(ServiceNames.ROBOT_REAL));
        pf = new PathDingDing(table);
    }
    
    //@Test
    public void testRandPF1() throws Exception
    {
    	int compt = 0;
    	ArrayList<Vec2> path = new ArrayList<Vec2>();
    	for(int n = 0; n < 10000; n++)
    	{
	    	try
	    	{
	    		System.out.println("essai :");
				path = pf.computePath(new Vec2((int)((Math.random() - 0.5) * 3000), (int)(Math.random() * 2000)), new Vec2((int)((Math.random() - 0.5) * 3000), (int)(Math.random() * 2000)));
				for(int i = 0; i < path.size(); i++)
		    	{
		    		System.out.println("-----------------------------" + path.get(i).toString());
		    	}
				win.getPanel().drawArrayList(path);
				compt++;
				//Thread.sleep(10);
	    	}
	    	catch(PathNotFoundException e)
	    	{
	    		System.out.println("--------------not on table------------------");
	    	}
    	}
    	System.out.println("nombre de calculs : " + compt);
    }
    
    @Test
    public void testClickedPF() throws Exception
    {
    	win.getPanel().drawGraph(pf.getGraph());
    	while(true)
    	{
    		if(win.getMouse().hasClicked())
    		{
		    	try
		    	{
		    		table.getObstacleManager().setEnnemyRobotPosition(win.getMouse().getMiddleClickPosition(), 0);
		    		long start = System.nanoTime();
			    	win.getPanel().drawArrayList(pf.computePath(win.getMouse().getLeftClickPosition(), win.getMouse().getRightClickPosition()));
			    	long end = System.nanoTime();
			    	System.out.println("elapsed : " + (end - start) + " ns");
			    }
		    	catch(PathNotFoundException e)
		    	{
		    		System.out.println("--------------not on table------------------");
		    	}
		    	win.getPanel().repaint();
    		}
    		else
    			Thread.sleep(200);
    	}
    }
    
    //@Test
    public void testCircleCollision() throws Exception
    {
    	while(true)
    	{
    		if(win.getMouse().hasClicked())
    		{
		    	table.getObstacleManager().setEnnemyRobotPosition(win.getMouse().getMiddleClickPosition(), 0);
		    	ArrayList<Vec2> path = new ArrayList<Vec2>();
		    	path.add(win.getMouse().getLeftClickPosition());
		    	path.add(win.getMouse().getRightClickPosition());
		    	if(pf.isPathCorrect(path))
			   		win.getPanel().drawArrayList(path);
		    	win.getPanel().repaint();
    		}
    		else
    			Thread.sleep(200);
    	}
    }
}