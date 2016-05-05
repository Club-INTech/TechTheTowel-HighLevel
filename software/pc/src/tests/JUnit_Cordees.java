package tests;

import enums.ActuatorOrder;
import enums.ScriptNames;
import enums.ServiceNames;
import enums.Speed;
import exceptions.*;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialConnexionException;
import exceptions.serial.SerialFinallyException;
import exceptions.serial.SerialManagerException;
import hook.Hook;
import org.junit.Before;
import org.junit.Test;
import robot.Robot;
import scripts.ScriptManager;
import smartMath.Vec2;
import strategie.GameState;
import table.Table;
import utils.Log;

import java.util.ArrayList;
import java.util.Random;

/**
 * JUnit pour la présentation pour les "Cordées de la réussite" (aussi nommée "Opération Mouillage de Loli")
 *
 * Il ferme les portes, fait les poissons, lève les bras en mode racaille d'Evry-ghetto
 *      et bouge encore pendant 30 sec
 * Il reste sur la demi-table à droite
 * Il faut le placer aux marques sur le tapis au départ
 *
 * @author discord
 */
public class JUnit_Cordees extends JUnit_Test
{

    GameState<Robot> game;
    Table table;
    Log log;
    ScriptManager scriptManager;
    long time;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception
    {
        //creation des objets pour le test
        super.setUp();
        game = (GameState<Robot>)container.getService(ServiceNames.GAME_STATE);

        //position initiale du robot
        game.robot.setPosition(Table.entryPosition);
        game.robot.setOrientation(Math.PI);
        game.robot.setLocomotionSpeed(Speed.SLOW_ALL);

        scriptManager = (ScriptManager)container.getService(ServiceNames.SCRIPT_MANAGER);
        table = (Table)container.getService(ServiceNames.TABLE);
        log = (Log)container.getService(ServiceNames.LOG);
        this.time = System.currentTimeMillis();
    }

    @Test
    public void test() throws SerialManagerException, BadVersionException, ContainerException, UnableToMoveException, PointInObstacleException, SerialConnexionException, ExecuteException, PathNotFoundException, SerialFinallyException, BlockedActuatorException 
    {
        //container.getService(ServiceNames.THREAD_TIMER);
        container.getService(ServiceNames.THREAD_INTERFACE);
        container.startInstanciedThreads();
        game.robot.useActuator(ActuatorOrder.ARM_INIT,true);
        game.robot.moveLengthwise(100, new ArrayList<Hook>(), false);
        scriptManager.getScript(ScriptNames.CASTLE).goToThenExec(0, game, new ArrayList<Hook>());
        scriptManager.getScript(ScriptNames.CLOSE_DOORS).goToThenExec(0, game, new ArrayList<Hook>());
        scriptManager.getScript(ScriptNames.FISHING).goToThenExec(3, game, new ArrayList<Hook>());
//        game.robot.useActuator(ActuatorOrder.FISHING_POSITION_LOW, true);
//        game.robot.useActuator(ActuatorOrder.FISHING_POSITION_RIGHT_LOW, true);
        game.robot.useActuator(ActuatorOrder.ARM_INIT, true);
        log.debug("Temps d'excétution : " + (System.currentTimeMillis() - this.time));
//        while((System.currentTimeMillis()-time)<30000)
//        {
//            Vec2 point = nextPoint();
//            try
//            {
//                game.robot.moveToLocation(point, new ArrayList<Hook>(), table);
//            }
//            catch (PathNotFoundException e)
//            {
//                log.debug("pas de chemin entre : "+ game.robot.getPosition()+" et : "+point);
//            }
//            catch (UnableToMoveException e)
//            {
//                log.debug("robot bloque");
//                return;//on arrete le test pour preserver la mecanique du robot
//            }
//            catch (PointInObstacleException e)
//            {
//                log.debug("le point : "+point+" est dans un obtacle (normal)");
//            }
//        }
    }

    /**
     * La m�thode NextPoint renvoie un point g�n�r� al�atoirement
     * @return un point sur la table ([0,1500],[0,2000])
     */
    private Vec2 nextPoint()
    {
        Random random = new Random();
        Vec2 point = new Vec2(random.nextInt(1501),random.nextInt(2001));
        log.debug(point);
        return(point);
    }
} 