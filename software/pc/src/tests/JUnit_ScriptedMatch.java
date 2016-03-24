package tests;

import enums.*;
import exceptions.BadVersionException;
import exceptions.BlockedActuatorException;
import exceptions.ExecuteException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.PathNotFoundException;
import exceptions.PointInObstacleException;
import exceptions.serial.SerialConnexionException;
import exceptions.serial.SerialFinallyException;
import hook.Hook;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import robot.Robot;
import scripts.ScriptManager;
import smartMath.Vec2;
import strategie.GameState;
import table.Table;
import utils.Sleep;

import java.util.ArrayList;

public class JUnit_ScriptedMatch extends JUnit_Test
{
    private GameState<Robot> theRobot;
    private ScriptManager scriptManager;
    private ArrayList<Hook> emptyHook = new ArrayList<Hook>();
    
    // Version des scripts à lancer
    private final int techTheSandVersion = 2;
    private final int fishingVersion = 3;
    private final int closeDoorsVersion = 0;
    private final int shellDepositVersion = 0;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception
    {
        super.setUp();
        scriptManager = (ScriptManager)container.getService(ServiceNames.SCRIPT_MANAGER);
        theRobot = (GameState<Robot>)container.getService(ServiceNames.GAME_STATE);
        initialize();

        // Lance le thread graphique
        container.getService(ServiceNames.THREAD_INTERFACE);
        //container.getService(ServiceNames.THREAD_EYES);
        container.startInstanciedThreads();
    }

    private void initialize() throws Exception
    {
        theRobot.robot.setOrientation(Math.PI);
        theRobot.robot.setPosition(Table.entryPosition);
        theRobot.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);
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


    @After
    public void aftermath() throws Exception
    {
        //on remonte les bras
        try
        {
        	theRobot.robot.useActuator(ActuatorOrder.ARM_INIT,true);
            theRobot.robot.immobilise();
            log.debug("Fin de match !");
        }
        catch (SerialConnexionException e)
        {
            e.printStackTrace();
            log.debug("Impossible de ranger les bras !");
        }
    }

    @Test
    public void match() throws Exception
    {
    	log.debug("Début de match scripté !");
    	
        try
        {
            Vec2 sup = scriptManager.getScript(ScriptNames.TECH_THE_SAND).entryPosition(1, theRobot.robot.getRobotRadius(), theRobot.robot.getPosition()).position;
            theRobot.table.getObstacleManager().freePoint(sup);
            theRobot.robot.setLocomotionSpeed(Speed.FAST_ALL);
            scriptManager.getScript(ScriptNames.TECH_THE_SAND).execute(techTheSandVersion,theRobot, emptyHook);
            log.debug("Fin de Tech The Sand !");
        }
        catch(Exception e)
        {
        	log.debug("Problème d'exécution dans Tech the Sand");
            e.printStackTrace();
        }
        try
        {
        	
            scriptManager.getScript(ScriptNames.CASTLE).execute(3, theRobot, emptyHook);
        }
        catch(Exception e)
        {
        	log.debug("Problème d'exécution dans Castle");
            e.printStackTrace();
        }
       /* try
        {
            Vec2 sup = scriptManager.getScript(ScriptNames.CLOSE_DOORS).entryPosition(closeDoorsVersion, theRobot.robot.getRobotRadius(), theRobot.robot.getPosition()).position;
            theRobot.table.getObstacleManager().freePoint(sup);
            scriptManager.getScript(ScriptNames.CLOSE_DOORS).execute(closeDoorsVersion,theRobot, emptyHook);
        }
        catch(Exception e)
        {
        	log.debug("Problème d'exécution dans Close Doors");
            e.printStackTrace();
        }*/
        try
        {
        	// à décommenter si a version 3 de Castle n'est pas utilisée et le close doors non utilisé
        	theRobot.robot.moveLengthwise(200);
            Vec2 sup = scriptManager.getScript(ScriptNames.SHELL_GETTER).entryPosition(-1, theRobot.robot.getRobotRadius(), theRobot.robot.getPosition()).position;
            theRobot.table.getObstacleManager().freePoint(sup);
            scriptManager.getScript(ScriptNames.SHELL_GETTER).goToThenExec(-1,theRobot, emptyHook);
        }
        catch(Exception e)
        {
        	log.debug("Problème d'exécution dans Shell Getter v -1");
            e.printStackTrace();
        }
        try
        {
        	theRobot.robot.setTurningStrategy(TurningStrategy.FASTEST);
            theRobot.robot.useActuator(ActuatorOrder.CLOSE_DOOR, false);
            Vec2 sup = scriptManager.getScript(ScriptNames.FISHING).entryPosition(fishingVersion, theRobot.robot.getRobotRadius(), theRobot.robot.getPosition()).position;
            theRobot.table.getObstacleManager().freePoint(sup);
            scriptManager.getScript(ScriptNames.FISHING).goToThenExec(fishingVersion, theRobot, emptyHook);
        }
        catch(Exception e)
        {
        	log.debug("Problème d'exécution dans Fishing");
            e.printStackTrace();
        }
        try
        {
            scriptManager.getScript(ScriptNames.SHELL_GETTER).goToThenExec(1,theRobot, emptyHook);
        }
        catch(Exception e)
        {
        	log.debug("Problème d'exécution Shell Getter v2");
            e.printStackTrace();
        }
        try
        {
            scriptManager.getScript(ScriptNames.SHELL_GETTER).goToThenExec(2,theRobot, emptyHook);
        }
        catch(Exception e)
        {
        	log.debug("Problème d'exécution dans Shell Getter v2");
            e.printStackTrace();
        }
        try
        {
            scriptManager.getScript(ScriptNames.SHELL_GETTER).goToThenExec(3,theRobot, emptyHook);
        }
        catch(Exception e)
        {
        	log.debug("Problème d'exécution dans Shell Getter v3");
            e.printStackTrace();
        }
        try
        {
            scriptManager.getScript(ScriptNames.SHELL_GETTER).goToThenExec(4,theRobot, emptyHook);
        }
        catch(Exception e)
        {
        	log.debug("Problème d'exécution dans Shell Getter v4");
            e.printStackTrace();
        }
        try
        {
            scriptManager.getScript(ScriptNames.SHELL_DEPOSIT).goToThenExec(shellDepositVersion,theRobot, emptyHook);
        }
        catch(Exception e)
        {	
        	log.debug("Problème d'exécution dans Shell Deposit");
            e.printStackTrace();
        }
    }
}