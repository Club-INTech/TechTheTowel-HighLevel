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
	Table table;
	
    @Before
    public void setUp() throws Exception
    {
        super.setUp();
        table = (Table)container.getService(ServiceNames.TABLE);
        win = new Window(table);
    }
    
    //@Test
    public void testRandPF1() throws Exception
    {
    	int compt = 0;
    	ArrayList<Vec2> path = new ArrayList<Vec2>();
    	for(int n = 0; n < 100000; n++)
    	{
	    	try
	    	{
	    		System.out.println("essai :");
				path = PathDingDing.computePath(new Vec2((int)((Math.random() - 0.5) * 3000), (int)(Math.random() * 2000)), new Vec2((int)((Math.random() - 0.5) * 3000), (int)(Math.random() * 2000)), (Table)container.getService(ServiceNames.TABLE));
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
    	while(true)
    	{
    		if(win.getMouse().hasClicked())
    		{
		    	try
		    	{
		    		table.getObstacleManager().setEnnemyRobotPosition(win.getMouse().getMiddleClickPosition(), 0);
			    	win.getPanel().drawArrayList(PathDingDing.computePath(win.getMouse().getLeftClickPosition(), win.getMouse().getRightClickPosition(), table));
			    }
		    	catch(PathNotFoundException e)
		    	{
		    		System.out.println("--------------not on table------------------");
		    	}
		    	win.getPanel().repaint();
    		}
    		else
    			Thread.sleep(100);
    	}
    }
}