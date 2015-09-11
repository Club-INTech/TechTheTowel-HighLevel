package tests;

import org.junit.*;
import org.junit.runner.JUnitCore;

import pathDingDing.*;
import table.Table;
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
	

	public static void main(String[] args) throws Exception
	{                    
	   JUnitCore.main("tests.JUnit_Pathfinding");
	}
	
    @Before
    public void setUp() throws Exception
    {
        super.setUp();
        table = (Table)container.getService(ServiceNames.TABLE);
        win = new Window(table);
        pf = (PathDingDing)container.getService(ServiceNames.PATHDINGDING);
    }
    
    
    @Test
    public void testClickedPF() throws Exception
    {
    	 // TODO : ON clique et le pathfinding trouve le chemin de A à B
    } 
    
    //@Test
    public void testEnums()
    {
    	table.eatPlotX(5);
    	
    }
}