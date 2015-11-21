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
import utils.Log;

public class JUnit_Pathfinding extends JUnit_Test
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
        table = (Table)container.getService(ServiceNames.TABLE);
        log = (Log)container.getService(ServiceNames.LOG);
        win = new Window(table);
        pf = (PathDingDing)container.getService(ServiceNames.PATHDINGDING);
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
                    long start = System.currentTimeMillis();
                    win.getPanel().drawArrayList(pf.computePathVec2(win.getMouse().getLeftClickPosition(), win.getMouse().getRightClickPosition()));
                    long end = System.currentTimeMillis();
                    System.out.println("time elapsed : " + (end - start));
                }
                catch(PathNotFoundException e)
                {
                    log.debug("pas de chemin trouve entre "+win.getMouse().getLeftClickPosition()+"et"+ win.getMouse().getRightClickPosition());
                }
                catch(PointInObstacleException e)
                {
                    log.debug("point d'arrivée dans un obstacle");
                }
                win.getPanel().repaint();
            }
            else
                Thread.sleep(200);
        }
    }

    }

