package tests;

import hook.Hook;
import org.junit.*;
import org.junit.runner.JUnitCore;

import pathDingDing.*;
import robot.RobotReal;
import scripts.TechTheSand;
import smartMath.Vec2;
import strategie.GameState;
import table.Table;
import enums.*;

import exceptions.*;
import graphics.Window;
import table.obstacles.ObstacleRectangular;
import utils.Log;

import java.util.ArrayList;


/**
 * Teste le PDD gràce à une inteface graphique ; peut aussi ordonner le robot à se déplacer à un point
 * Commandes :
 *        Clic gauche : pt de départ
 *        Clic droit : pt d'arrivée
 *        Touche O : le prochain clic sera un appel au robot, il se déplacera au dernier point spécifié par le clic droit
 *        Touche S : Le robot peut tourner à gauche et à droite
 *        Touche Q : Le robot ne peut tourner qu'à gauche
 *        Touche D : Le robot ne peut tourner qu'à droite
 */
public class JUnit_Pathfinding extends JUnit_Test
{
    Window win;
    Table table;
    PathDingDing pf;
    Log log;
    GameState<RobotReal> game;


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


        game = (GameState<RobotReal>) container.getService(ServiceNames.GAME_STATE);
        game.robot.setPosition(Table.entryPosition);
        game.robot.setOrientation(Math.PI);
        game.changeRobotRadius(TechTheSand.expandedRobotRadius);

        ArrayList<ObstacleRectangular> mRectangles = game.table.getObstacleManager().getRectangles();

        for (int i=0;i< mRectangles.size();i++)
        {
            if(mRectangles.get(i).isInObstacle(new Vec2(700,1100)))
            {
                game.table.getObstacleManager().removeObstacle(mRectangles.get(i));
            }
        }


        pf = (PathDingDing)container.getService(ServiceNames.PATHDINGDING);
    }



    @Test
    public void testClickedPF() throws Exception
    {
        win.getPanel().drawGraph(pf.getGraph());
        while(true)
        {
            if(win.getKeyboard().isModeActual() && win.getMouse().hasClickedRight())
            {

                //robot.setTurningStrategy(win.getKeyboard().getTurningStrategy());

                try
                {
                    //table.getObstacleManager().setEnnemyRobot1Position(win.getMouse().getMiddleClickPosition());
                    win.getPanel().drawArrayList(pf.computePathVec2(game.robot.getPosition(), win.getMouse().getRightClickPosition()));

                }
                catch(PathNotFoundException e)
                {
                    log.debug("pas de chemin trouve entre "+game.robot.getPosition()+"et"+ win.getMouse().getRightClickPosition());
                }
                catch(PointInObstacleException e)
                {
                    log.debug("point d'arrivée dans un obstacle");
                }
                win.getPanel().repaint();
                win.getKeyboard().resetModeActual();
                win.getMouse().resetHasClicked();
                //robot.moveToLocation(win.getMouse().getRightClickPosition(), new ArrayList<Hook>(), table);
            }
            else if(win.getMouse().hasClicked())
            {
               // robot.setTurningStrategy(win.getKeyboard().getTurningStrategy());
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

