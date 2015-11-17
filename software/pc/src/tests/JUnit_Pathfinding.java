package tests;

import enums.ServiceNames;
import graphics.Window;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import pathDingDing.PathDingDing;
import table.Table;

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
}