package tests;

import org.junit.*;

import pathDingDing.PathDingDing;
import table.Table;
import scripts.AbstractScript;
import scripts.ScriptManager;
import smartMath.*;

import java.util.ArrayList;

import enums.ScriptNames;
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

    @Before
    public void setUp() throws Exception
    {
        super.setUp();
        table = (Table)container.getService(ServiceNames.TABLE);
        pf = (PathDingDing)container.getService(ServiceNames.PATHDINGDING);
        win = new Window(table);
    }
    
    
	@Test
    public void test() throws Exception
    {
    	ArrayList<Vec2> path = new ArrayList<Vec2>();
    	
    	int compt=0;//Compteur pour savoir quel point d'entrée ne marche pas

    		
    	
    	win.getPanel().drawGraph(pf.getGraph());
    	
    	try
    	{
    		//Test : computePath(new Vec2 [Point de depart] ,new Vec2[Point d'arrivée] )
	    	win.getPanel().drawArrayList(pf.computePath(new Vec2 (-900,1000),new Vec2(-1045,250)));
	    }
    	catch(PathNotFoundException e)
    	{
    		log.debug("pas de chemin trouve", this);
    		System.out.println("Mauvais chemin");
    	}
    	
    }
}