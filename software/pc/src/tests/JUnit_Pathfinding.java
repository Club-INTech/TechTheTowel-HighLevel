package tests;

import org.junit.*;

import pathDingDing.PathDingDing;
import table.Table;
import robot.RobotReal;
import smartMath.*;
import enums.*;

import java.util.ArrayList;
import java.util.EnumSet;

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
        pf = (PathDingDing)container.getService(ServiceNames.PATHDINGDING);
    }
    
    //@Test
    public void testRandPF1() throws Exception
    {
    	ArrayList<Vec2> path = new ArrayList<Vec2>();
    	for(int n = 0; n < 10000; n++)
    	{
	    	try
	    	{
				path = pf.computePath(new Vec2((int)((Math.random() - 0.5) * 3000), (int)(Math.random() * 2000)), new Vec2((int)((Math.random() - 0.5) * 3000), (int)(Math.random() * 2000)), EnumSet.noneOf(ObstacleGroups.class));
				win.getPanel().drawArrayList(path);
				//Thread.sleep(10);
	    	}
	    	catch(PathNotFoundException e)
	    	{
	    		log.debug("pas de chemin trouve", this);
	    	}
    	}
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
		    		//table.getObstacleManager().setEnnemyRobot1Position(win.getMouse().getMiddleClickPosition());
			    	win.getPanel().drawArrayList(pf.computePath(win.getMouse().getLeftClickPosition(), win.getMouse().getRightClickPosition(), EnumSet.of(ObstacleGroups.GREEN_PLOTS, ObstacleGroups.YELLOW_PLOTS, ObstacleGroups.GOBLETS, ObstacleGroups.ENNEMY_ROBOTS)));
			    }
		    	catch(PathNotFoundException e)
		    	{
		    		log.debug("pas de chemin trouve entre "+win.getMouse().getLeftClickPosition()+"et"+ win.getMouse().getRightClickPosition(), this);
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
		    	//table.getObstacleManager().setEnnemyRobot1Position(win.getMouse().getMiddleClickPosition());
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