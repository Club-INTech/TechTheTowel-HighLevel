package tests;

import org.junit.*;

import enums.ServiceNames;
import graphics.*;
import smartMath.Vec2;
import table.Table;
import robot.*;

import java.util.ArrayList;

public class JUnit_Graphics extends JUnit_Test
{
	Window win;
	
    @Before
    public void setUp() throws Exception
    {
        super.setUp();
    	//
    	win = new Window((Table)container.getService(ServiceNames.TABLE)/*, (RobotReal)container.getService(ServiceNames.ROBOT_REAL)*/);
    }
    
    //test de l'intersection de deux segments
    @Test
    public void testPanel() throws InterruptedException
    {
    	ArrayList<Vec2> path = new ArrayList<Vec2>();
    	path.add(new Vec2(0, 100));
    	path.add(new Vec2(0, 1900));
    	path.add(new Vec2(-1400, 1900));
    	path.add(new Vec2(-1400, 100));
    	win.getPanel().drawArrayList(path);
    	Thread.sleep(5000);
    }
}