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
 * 4 => sensors
 * 5 => homologation
 */
public class JUnit_Selector extends JUnit_Test {

    private static int value = 0;


    public static void main(String[] args) throws Exception {
        System.out.println(args[0]);

        if (args.length != 0) {
            JUnit_Selector.value = Integer.parseInt(args[0]);
        }

        JUnitCore.main("tests.JUnit_Selector");
    }


    @Test
    public void launch() throws Exception {
        if(value == 0)
        {
            JUnit_ScriptedMatch junit = new JUnit_ScriptedMatch();
            junit.setUp();
            junit.match();
            junit.aftermath();
        }
        else if(value == 1)
        {
            JUnit_Fishing junit = new JUnit_Fishing();
            junit.setUp();
            junit.fishThemWithHook();
            junit.after();
        }
        else if(value == 2)
        {
            JUnit_TechTheSand junit = new JUnit_TechTheSand();
            junit.setUp();
            junit.TechIt();
            junit.aftermath();
        }
        else if(value == 3)
        {
            JUnit_Shells junit = new JUnit_Shells();
            junit.setUp();
            junit.fishThem();
            junit.aftermath();
        }
        else if(value == 4)
        {
            JUnit_Sensors junit = new JUnit_Sensors();
            junit.setUp();
            junit.testSensorEnnemyWithoutMovement();
        }
        else if(value == 5)
        {
            JUnit_Homologation junit = new JUnit_Homologation();
            junit.setUp();
            junit.launch();
        }
        else
        {
            log.critical("BAD ARG YOU MOTHERFUCKER");
        }
    }
}

