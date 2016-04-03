package tests;

import enums.ActuatorOrder;
import enums.ContactSensors;
import enums.ServiceNames;
import enums.Speed;
import exceptions.BlockedActuatorException;
import exceptions.serial.SerialConnexionException;
import hook.Hook;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import robot.Robot;
import scripts.ScriptManager;
import strategie.GameState;
import table.Table;

import java.util.ArrayList;

/**
 * Sélecteur de script pour la RPI
 * aucun arg ou 0 => ScriptedMatch
 * 1 => Fishing
 * 2 => TechTheSand + Castle
 * 3 => Shells
 */
public class JUnit_Selector extends JUnit_Test {
    private GameState<Robot> theRobot;
    private ScriptManager scriptManager;
    private ArrayList<Hook> emptyHook = new ArrayList<Hook>();

    // Version des scripts à lancer
    private final int techTheSandVersion = 2;
    private final int fishingVersion = 3;
    private final int closeDoorsVersion = 0;
    private final int shellDepositVersion = 0;

    private static int value = 0;


    public static void main(String[] args) throws Exception {
        JUnitCore.main("tests.JUnit_Selector");

        if (args.length != 0) {
            JUnit_Selector.value = Integer.parseInt(args[0]);
        }
    }

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception {
        super.setUp();
        scriptManager = (ScriptManager) container.getService(ServiceNames.SCRIPT_MANAGER);
        theRobot = (GameState<Robot>) container.getService(ServiceNames.GAME_STATE);
        initialize();

        // Lance le thread graphique
        //container.getService(ServiceNames.THREAD_INTERFACE);
        //container.getService(ServiceNames.THREAD_EYES);
        container.startInstanciedThreads();
    }

    private void initialize() throws Exception {
        theRobot.robot.setOrientation(Math.PI);
        theRobot.robot.setPosition(Table.entryPosition);
        theRobot.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);
        //theRobot.robot.moveLengthwise(200, emptyHook, false);

        theRobot.robot.useActuator(ActuatorOrder.ARM_INIT, true);

        try {
            if (!theRobot.robot.getContactSensorValue(ContactSensors.DOOR_CLOSED)) {
                theRobot.robot.useActuator(ActuatorOrder.CLOSE_DOOR, true);
            }
            if (!theRobot.robot.getContactSensorValue(ContactSensors.DOOR_CLOSED)) {
                theRobot.robot.useActuator(ActuatorOrder.STOP_DOOR, true);
                throw new BlockedActuatorException("Porte droite bloquée !");
            }

            // petit temps d'attente pour éviter de faire planter les portes #LeHautNiveauDemandeDeLaMerde
            theRobot.robot.sleep(100);

        } catch (SerialConnexionException e) {
            e.printStackTrace();
        }

    }


    @After
    public void aftermath() throws Exception {
        //on remonte les bras
        try {
            theRobot.robot.useActuator(ActuatorOrder.ARM_INIT, true);
            theRobot.robot.immobilise();
            log.debug("Fin de match !");
        } catch (SerialConnexionException e) {
            e.printStackTrace();
            log.debug("Impossible de ranger les bras !");
        }
    }

    @Test
    public void launch() throws Exception {
        if(value == 0)
        {
            JUnit_ScriptedMatch junit = new JUnit_ScriptedMatch();
            junit.setUp();
            junit.match();
        }
        else if(value == 1)
        {
            JUnit_Fishing junit = new JUnit_Fishing();
            junit.setUp();
            junit.fishThemWithHook();
        }
        else if(value == 2)
        {
            JUnit_TechTheSand junit = new JUnit_TechTheSand();
            junit.setUp();
            junit.TechIt();
        }
        else if(value == 3)
        {
            JUnit_Shells junit = new JUnit_Shells();
            junit.setUp();
            junit.fishThem();
        }
        else
        {
            log.critical("BAD ARG YOU MOTHERFUCKER");
        }
    }
}

