package tests;

import org.junit.*;

import pathDingDing.PathDingDing;
import table.Table;
import robot.RobotReal;
import scripts.ScriptManager;
import smartMath.*;

import java.util.EnumSet;

import enums.ObstacleGroups;
import enums.ServiceNames;
import exceptions.PathNotFoundException;
import graphics.Window;

/** Test verifiant les points d'entrée des differents scripts, via le PathFinding */

public class JUnit_Path extends JUnit_Test
{
	Table table;
	PathDingDing pf;
    ScriptManager scriptmanager;
    Window win;
    RobotReal robot;
    
    @Before
    public void setUp() throws Exception
    {
        super.setUp();
        robot=(RobotReal)container.getService(ServiceNames.ROBOT_REAL);
        table = (Table)container.getService(ServiceNames.TABLE);
        pf = (PathDingDing)container.getService(ServiceNames.PATHDINGDING);
        win = new Window(table, robot);
    }
    
    
	@Test
    public void test() throws Exception
    {  		
    	win.getPanel().drawGraph(pf.getGraph());
    	
    	try
    	{
    		//Test : computePath(new Vec2 [Point de depart] ,new Vec2[Point d'arrivée] )
	    	win.getPanel().drawArrayList(pf.computePath(new Vec2(0, 0),new Vec2(750,1700), EnumSet.noneOf(ObstacleGroups.class)));
	    }
    	catch(PathNotFoundException e)
    	{
    		log.debug("pas de chemin trouve", this);
    	}
    	
    }
}