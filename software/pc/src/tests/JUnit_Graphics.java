package tests;

import graphics.Window;
import org.junit.Before;
import org.junit.Test;
import smartMath.Vec2;
import utils.Sleep;

import java.util.ArrayList;

public class JUnit_Graphics extends JUnit_Test
{
	Window win;
	
    @Before
    public void setUp() throws Exception
    {
        super.setUp();
    	//
    	//win = new Window((Table)container.getService(ServiceNames.TABLE)/*, (Robot)container.getService(ServiceNames.ROBOT)*/);
    }
    
    //test de l'intersection de deux segments
    //@Test
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
    
    @Test
    public void testSensorPanel() throws InterruptedException
    {
    	win = new Window();
    	for(int i = 10; i < 10000; i += 10)
    	{
    		win.drawInt((int)(10*Math.cos((double)i/10)), (int)(20*Math.cos((double)i/20)), (int)(30*Math.cos((double)i/40)), (int)(40*Math.cos((double)i/80)));
    		Sleep.sleep(200);
    	}
    }
}