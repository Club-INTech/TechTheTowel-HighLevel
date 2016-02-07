package tests;

import enums.ServiceNames;
import enums.Speed;
import exceptions.Locomotion.UnableToMoveException;
import hook.Hook;
import org.junit.Before;
import org.junit.Test;
import robot.RobotReal;
import smartMath.Arc;
import smartMath.Vec2;
import strategie.GameState;
import table.Table;
import utils.Log;

import java.util.ArrayList;

public class JUnit_CurveTrajectories extends JUnit_Test
{
    GameState<RobotReal> game;
    Table table;
    Log log;
    ArrayList<Arc> trajectory;

    @Before
    public void setUp() throws Exception
    {
        //creation des objets pour le test
        super.setUp();
        game = (GameState<RobotReal>)container.getService(ServiceNames.GAME_STATE);

        //position initiale du robot
        game.robot.setPosition(Table.entryPosition);
        game.robot.setOrientation(Math.PI);
        game.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);

        table = (Table)container.getService(ServiceNames.TABLE);
        log = (Log)container.getService(ServiceNames.LOG);
        trajectory = new ArrayList<>();

        trajectory.add(new Arc(Table.entryPosition, new Vec2(900, 300), Math.PI, 3*Math.PI/4));
        trajectory.add(new Arc(new Vec2(900, 300), new Vec2(300, 200), 3*Math.PI/4, Math.PI));

    }

    @Test
    public void proveMeWrong()
    {
        for(Arc a : trajectory)
        {
            try {
                game.robot.moveArc(a, new ArrayList<Hook>());
            } catch (UnableToMoveException e) {
                e.printStackTrace();
            }
        }
    }
}