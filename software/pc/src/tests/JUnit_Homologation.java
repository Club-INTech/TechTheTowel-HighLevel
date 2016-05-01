package tests;

import enums.*;
import exceptions.BlockedActuatorException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialConnexionException;
import hook.Hook;
import org.junit.Before;
import org.junit.Test;
import robot.Robot;
import robot.cardsWrappers.SensorsCardWrapper;
import scripts.ScriptManager;
import smartMath.Vec2;
import strategie.GameState;
import table.Table;
import threads.ThreadSensor;
import threads.ThreadTimer;
import utils.Sleep;

import java.util.ArrayList;

/**
 * Code d'homologation
 * @author discord
 */
public class JUnit_Homologation extends JUnit_Test
{
    private GameState<Robot> theRobot;
    private ScriptManager scriptManager;
    private SensorsCardWrapper mSensorsCardWrapper;
    private ThreadSensor threadSensor;
    private ArrayList<Hook> emptyHook = new ArrayList<Hook>();

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception
    {
        super.setUp();
        scriptManager = (ScriptManager)container.getService(ServiceNames.SCRIPT_MANAGER);
        theRobot = (GameState<Robot>)container.getService(ServiceNames.GAME_STATE);
        mSensorsCardWrapper = (SensorsCardWrapper) container.getService(ServiceNames.SENSORS_CARD_WRAPPER);
        initialize();

        // Lance le thread graphique
        container.getService(ServiceNames.THREAD_TIMER);
        this.threadSensor = (ThreadSensor) container.getService(ServiceNames.THREAD_SENSOR);
        ThreadSensor.noDelay();
        //container.getService(ServiceNames.THREAD_INTERFACE);
        container.startInstanciedThreads();

        waitMatchBegin();
    }

    private void initialize() throws Exception
    {
        theRobot.robot.setOrientation(Math.PI);
        theRobot.robot.setPosition(Table.entryPosition);
        theRobot.robot.setLocomotionSpeed(Speed.SLOW_ALL);
        theRobot.table.deleteAllTheShells();
        //theRobot.robot.moveLengthwise(200, emptyHook, false);

        theRobot.robot.useActuator(ActuatorOrder.ARM_INIT, true);

        try
        {
            if(!theRobot.robot.getContactSensorValue(ContactSensors.DOOR_CLOSED))
            {
                theRobot.robot.useActuator(ActuatorOrder.CLOSE_DOOR, true);
            }
            if(!theRobot.robot.getContactSensorValue(ContactSensors.DOOR_CLOSED))
            {
                theRobot.robot.useActuator(ActuatorOrder.STOP_DOOR, true);
                throw new BlockedActuatorException("Porte droite bloquée !");
            }

            // petit temps d'attente pour éviter de faire planter les portes #LeHautNiveauDemandeDeLaMerde
            theRobot.robot.sleep(100);

        }
        catch (SerialConnexionException e)
        {
            e.printStackTrace();
        }

    }

    @Test
    public void launch()
    {
        boolean bite = true;
        try
        {
            theRobot.robot.setBasicDetection(true);
            theRobot.robot.moveLengthwise(200);
            //scriptManager.getScript(ScriptNames.CASTLE).goToThenExec(0, theRobot, emptyHook);
        }
        catch(Exception e)
        {
            log.debug("Problème d'exécution dans Castle");
            e.printStackTrace();
        }
        while(bite)
        {
            try {
                Vec2 sup = scriptManager.getScript(ScriptNames.CLOSE_DOORS).entryPosition(3, theRobot.robot.getRobotRadius(), theRobot.robot.getPosition()).position;
                theRobot.table.getObstacleManager().freePoint(sup);
                scriptManager.getScript(ScriptNames.CLOSE_DOORS).goToThenExec(3, theRobot, emptyHook);
                break;
            } catch (Exception e) {
                log.debug("Problème d'exécution dans Close Doors");
                try {
                    if(e instanceof UnableToMoveException && ((UnableToMoveException)e).reason == UnableToMoveReason.OBSTACLE_DETECTED
                            && ((UnableToMoveException) e).aim.minusNewVector(theRobot.robot.getPosition()).dot(theRobot.robot.getPosition().minusNewVector(theRobot.robot.getPosition().plusNewVector(new Vec2((int)(100*Math.cos(theRobot.robot.getOrientation())),(int)(100*Math.sin(theRobot.robot.getOrientation())))))) > 0)
                        theRobot.robot.moveLengthwise(200);
                    else if(e instanceof UnableToMoveException && ((UnableToMoveException)e).reason == UnableToMoveReason.OBSTACLE_DETECTED)
                        theRobot.robot.moveLengthwise(-200);
                } catch (UnableToMoveException e1) {
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }
        }
        while (bite)
        {
            try {
                theRobot.robot.setBasicDetection(true);

                theRobot.robot.setTurningStrategy(TurningStrategy.FASTEST);
                theRobot.robot.useActuator(ActuatorOrder.CLOSE_DOOR, false);
                Vec2 sup = scriptManager.getScript(ScriptNames.FISHING).entryPosition(3, theRobot.robot.getRobotRadius(), theRobot.robot.getPosition()).position;
                theRobot.table.getObstacleManager().freePoint(sup);
                scriptManager.getScript(ScriptNames.FISHING).goToThenExec(3, theRobot, emptyHook);
                bite = false;
            } catch (Exception e) {
                log.debug("Problème d'exécution dans Fishing");
                try {
                    if(e instanceof UnableToMoveException && ((UnableToMoveException)e).reason == UnableToMoveReason.OBSTACLE_DETECTED
                            && ((UnableToMoveException) e).aim.minusNewVector(theRobot.robot.getPosition()).dot(theRobot.robot.getPosition().minusNewVector(theRobot.robot.getPosition().plusNewVector(new Vec2((int)(100*Math.cos(theRobot.robot.getOrientation())),(int)(100*Math.sin(theRobot.robot.getOrientation())))))) > 0)
                        theRobot.robot.moveLengthwise(200);
                    else if(e instanceof UnableToMoveException && ((UnableToMoveException)e).reason == UnableToMoveReason.OBSTACLE_DETECTED)
                        theRobot.robot.moveLengthwise(-200);
                } catch (UnableToMoveException e1) {
                    e1.printStackTrace();
                }
                // threadSensor.stop();

            }
        }
    }

    /**
     * Attends que le match soit lancé
     * cette fonction prend fin quand le match a démarré
     */
    private void waitMatchBegin()
    {

        System.out.println("Robot pret pour le match, attente du retrait du jumper");

        while(mSensorsCardWrapper.isJumperAbsent())
        {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        while(!mSensorsCardWrapper.isJumperAbsent())
        {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // maintenant que le jumper est retiré, le match a commencé
        ThreadTimer.matchStarted = true;
    }
}
