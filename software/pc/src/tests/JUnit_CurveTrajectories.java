package tests;

import enums.ServiceNames;
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
    private double epsilon = 2.*Math.PI/17.;

    @Before
    public void setUp() throws Exception
    {
        //creation des objets pour le test
        super.setUp();
      //  game = (GameState<RobotReal>)container.getService(ServiceNames.GAME_STATE);

        //position initiale du robot
     //   game.robot.setPosition(Table.entryPosition);
      //  game.robot.setOrientation(Math.PI);
      //  game.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);

        table = (Table)container.getService(ServiceNames.TABLE);
        log = (Log)container.getService(ServiceNames.LOG);
        trajectory = new ArrayList<>();
        
        // sortie de serviette
       // game.robot.moveLengthwise(200);
        
        trajectory.add(new Arc(new Vec2(1000,350), new Vec2(1150,1200), Math.PI/2, true));
        //trajectory.add(new Arc(new Vec2(1000,500), new Vec2(1100,500), Math.PI, true));

    }

    @Test
    public void proveMeWrong()
    {
        for(Arc a : trajectory)
        {
            try {
                game.robot.moveArc(a, new ArrayList<Hook>());
                //game.robot.turn(Math.PI);
            } catch (UnableToMoveException e) {
                e.printStackTrace();
            }
        }
        while(true)
        {
            log.debug(game.robot.getPosition());
        }
    }
}