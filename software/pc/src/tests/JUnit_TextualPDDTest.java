package tests;

import java.util.ArrayList;

import org.junit.*;
import org.junit.runner.JUnitCore;

import pathDingDing.*;
import smartMath.Vec2;
import table.Table;
import utils.Log;
import enums.*;

import graphics.Window;

public class JUnit_TextualPDDTest extends JUnit_Test
{	
	Window win;
	Table table;
	PathDingDing pf;
	Log log;
	

	public static void main(String[] args) throws Exception
	{                    
	   JUnitCore.main("tests.JUnit_Pathfinding");
	}
	
    @Before
    public void setUp() throws Exception
    {
        super.setUp();
        log = (Log)container.getService(ServiceNames.LOG);
        table = (Table)container.getService(ServiceNames.TABLE);
        pf = (PathDingDing)container.getService(ServiceNames.PATHDINGDING);
    }
    
    @Test
    public void test()
    {
    	ArrayList<Node> path = pf.computePath(new Vec2(300, 1150), new Vec2(-300, 1150));
    	ArrayList<Vec2> pathVec = new ArrayList<Vec2>();
    	for(int i=0 ; i<path.size() ; i++)
    	{
    		pathVec.add(path.get(i).getPosition());
    		log.debug(path.get(i).getPosition());
    	}
    	
    }
    
}