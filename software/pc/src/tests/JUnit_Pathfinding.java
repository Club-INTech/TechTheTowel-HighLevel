package tests;

import org.junit.*;

import pathfinding.Pathfinding;
import container.Container;
import table.Table;
import smartMath.Point;
import smartMath.Vec2;
import smartMath.Path;

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
    	if( !Pathfinding.intersects(new Point(0, 0), new Point(1, 1), new Point(0, 1), new Point(1, 0)) )
    		Assert.fail();
    }
    
    //test du pathfinding
    @Test
    public void testPF1() throws Exception
    {
    	Container cont = new Container();
    	Pathfinding pf = new Pathfinding((Table)cont.getService("Table"));
    	pf.computePath(new Vec2(-1200, 200), new Vec2(1200, 200));
    }
}