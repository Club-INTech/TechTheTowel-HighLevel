package tests;

import org.junit.*;

import pathDingDing.PathDingDing;
import container.Container;
import table.Table;
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
        win = new Window(table);
        pf = new PathDingDing(table);
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
    	while(true)
    	{
    		if(win.getMouse().hasClicked())
    		{
		    	try
		    	{
		    		table.getObstacleManager().setEnnemyRobotPosition(win.getMouse().getMiddleClickPosition(), 0);
			    	win.getPanel().drawArrayList(pf.computePath(win.getMouse().getLeftClickPosition(), win.getMouse().getRightClickPosition()));
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
    
    //@Test
    public void testCollision() throws Exception
    {
    	while(true)
    	{
    		if(win.getMouse().hasClicked())
    		{
    			ArrayList<Vec2> path = new ArrayList<Vec2>();
    			path.add(win.getMouse().getLeftClickPosition());
    			path.add(win.getMouse().getRightClickPosition());
			    win.getPanel().drawArrayList(path);
    			ArrayList<Vec2> path2 = new ArrayList<Vec2>();
    			path2.add(new Vec2(-1100, 778));
    			path2.add(new Vec2(-1100, 1222));
			    win.getPanel().drawArrayList(path2);
			    if(pf.intersects(new Segment(win.getMouse().getLeftClickPosition(), win.getMouse().getRightClickPosition()), new Segment(new Vec2(-1100, 778), new Vec2(-1100, 1222))))
			    {
		    		System.out.println("intersection...");
		    		win.getPanel().drawPoint(pf.intersection(new Segment(win.getMouse().getLeftClickPosition(), win.getMouse().getRightClickPosition()), new Segment(new Vec2(-1100, 778), new Vec2(-1100, 1222))));
			    }
			    else
			    	System.out.println("not");
		    	win.getPanel().repaint();
    		}
    		else
    			Thread.sleep(100);
    	}
    }
}