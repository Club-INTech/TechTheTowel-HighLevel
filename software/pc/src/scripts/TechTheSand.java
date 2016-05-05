package scripts;

import enums.*;
import exceptions.*;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialConnexionException;
import exceptions.serial.SerialFinallyException;
import hook.Callback;
import hook.Hook;
import hook.methods.CloseDoor;
import hook.methods.OpenDoor;
import hook.methods.SetSand;
import hook.types.HookFactory;
import robot.Robot;
import smartMath.Arc;
import smartMath.Circle;
import smartMath.Vec2;
import strategie.GameState;
import table.Table;
import threads.ThreadSensor;
import utils.Config;
import utils.Log;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Script pour récupérer le tas de sable central, ne s'occupe pas de le ramener dans notre zone de construction
 * Version 0 : En partant de notre côté, on avance vers le côté ennemi
 * Version 1 : Utilisation de trajectoire courbe
 * Version 2 : Départ du tapis en courbe puis execution version 1
 * @author CF, Discord
 */
public class TechTheSand extends AbstractScript
{

    // TEMPORAIRE 
    public static final int expandedRobotRadius = 330; //TODO a changer
	public static final int retractedRobotRadius = 230; //TODO a changer
    double distanceCod = 150;



    public TechTheSand(HookFactory hookFactory, Config config, Log log)
	{
		super (hookFactory,config,log);
		/**
		 * Versions du script
		 */
		versions = new Integer[]{0,1,2,3};
	}
	
	
	/**
	 * On lance le script choisi.
	 * @param versionToExecute Version a lancer
	 * @param stateToConsider Notre bon vieux robot
	 * @param hooksToConsider Les hooks necessaires pour l'execution du script
	 * @throws SerialConnexionException 
	 */
	@Override
	public void execute(int versionToExecute, GameState<Robot> stateToConsider,ArrayList<Hook> hooksToConsider) throws SerialFinallyException, ExecuteException, UnableToMoveException, SerialConnexionException, BlockedActuatorException
	{
		boolean symetry = false;

		try {
			symetry = config.getProperty("couleur").replaceAll(" ","").equals("violet");
		} catch (ConfigPropertyNotFoundException e) {
			e.printStackTrace();
		}

		try
		{
			if (versionToExecute == 0)
			{
				// On prend une vitesse lente pour que le robot récupère efficacement le sable
				Speed speedBeforeScriptWasCalled = stateToConsider.robot.getLocomotionSpeed();
				stateToConsider.robot.setLocomotionSpeed(Speed.SLOW_ALL);
				
				// On s'oriente vers le côté ennemi
				stateToConsider.robot.turn((Math.PI), hooksToConsider, false);
				
				// On déploie la vitre droite
				stateToConsider.robot.useActuator(ActuatorOrder.OPEN_DOOR, true);

				if(!stateToConsider.robot.getContactSensorValue(ContactSensors.DOOR_OPENED))
                {
                    stateToConsider.robot.useActuator(ActuatorOrder.STOP_DOOR, false);
                    throw new BlockedActuatorException("Porte bloquée !");
                } else {
                    stateToConsider.robot.setDoor(true);
                }

				stateToConsider.changeRobotRadius(TechTheSand.expandedRobotRadius);
                stateToConsider.table.getObstacleManager().updateObstacles(TechTheSand.expandedRobotRadius);
                stateToConsider.robot.setDoor(true);

                // On active la tige accrochante
				stateToConsider.robot.useActuator(ActuatorOrder.START_AXIS, false);
				
				// On avance pour récupérer le sable
				// TODO la distance est arbitraire, à modifier avec les phases de test
				stateToConsider.robot.moveLengthwise(300, hooksToConsider, true);
				
				// Demande au robot de ne tourner que vers la gauche pour ses prochains déplacements
				stateToConsider.robot.setTurningStrategy(TurningStrategy.LEFT_ONLY);

				// Demande au robot de conserver une marche avant pour ses prochains déplacements avec le sable
				stateToConsider.robot.setDirectionStrategy(DirectionStrategy.FORCE_FORWARD_MOTION);

				// On indique au robot qu'il transporte du sable
				stateToConsider.robot.setIsSandInside(true);

				Arc out = new Arc(stateToConsider.robot.getPosition(),new Vec2(-400,1300),stateToConsider.robot.getOrientation(),false);
				
				stateToConsider.robot.moveArc(out, hooksToConsider);

				// On s'oriente vers notre serviette
				stateToConsider.robot.turn(0);

				stateToConsider.robot.useActuator(ActuatorOrder.STOP_AXIS, false);
				
				// On reprend notre vitesse habituelle
				stateToConsider.robot.setLocomotionSpeed(speedBeforeScriptWasCalled);

				stateToConsider.table.getObstacleManager().freePoint(stateToConsider.robot.getPosition());

                //TODO Sortie de la zone
				
			}

			else if(versionToExecute == 2)
			{
				Speed speedBeforeScriptWasCalled = stateToConsider.robot.getLocomotionSpeed();
				stateToConsider.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);
                stateToConsider.robot.useActuator(ActuatorOrder.OPEN_DOOR, false);


                stateToConsider.changeRobotRadius(TechTheSand.expandedRobotRadius);
				stateToConsider.table.getObstacleManager().updateObstacles(TechTheSand.expandedRobotRadius);
                stateToConsider.robot.setDoor(true);

                //Arc entry = new Arc(entryPosition(1, 0, stateToConsider.robot.getPositionFast()).position, new Vec2(100,2000-220), Math.PI, true);

				//Arc start = new Arc(stateToConsider.robot.getPosition(), entry.start, entry.startAngle, true);

				Arc start = new Arc(-200, symetry ? 55 : 60, Math.PI, false);

				//stateToConsider.robot.setForceMovement(true);

                //On se déplace en courbe pour se placer en face du château
                stateToConsider.robot.moveArc(start, hooksToConsider);

				//stateToConsider.robot.setForceMovement(false);
                stateToConsider.robot.setBasicDetection(true);

                stateToConsider.robot.useActuator(ActuatorOrder.OPEN_DOOR, false);

                Hook hook = hookFactory.newXLesserHook(1100);

                hook.addCallback(new Callback(new OpenDoor(), true, stateToConsider));

                hooksToConsider.add(hook);

                stateToConsider.robot.moveLengthwise(975, hooksToConsider);

				stateToConsider.robot.turnWithoutDetection(2*Math.PI/3, hooksToConsider);

                stateToConsider.robot.setLocomotionSpeed(speedBeforeScriptWasCalled);

			}
		
			if(versionToExecute == 1 || versionToExecute == 2)
            {
                if(versionToExecute == 1)
                {
                    if(stateToConsider.table.configShell != 1 && stateToConsider.table.configShell != 5)
                    {
                        try {
                            stateToConsider.robot.moveToLocation(stateToConsider.table.entryPosition.plusNewVector(new Vec2(-180,0)), hooksToConsider, stateToConsider.table);
                            stateToConsider.robot.turnWithoutDetection(0,hooksToConsider);
                            stateToConsider.robot.moveLengthwiseWithoutDetection(100);
                            stateToConsider.robot.moveLengthwise(-100);
                            stateToConsider.robot.useActuator(ActuatorOrder.OPEN_DOOR, false);
                            stateToConsider.robot.moveToLocation(new Vec2(410, 1540), hooksToConsider, stateToConsider.table);
                        } catch (PathNotFoundException | PointInObstacleException e) {
                            e.printStackTrace();
                            throw new ExecuteException(e);
                        }
                    }
                    else
                    {
                        try {
                            stateToConsider.robot.moveToLocation(new Vec2(1150,1500), hooksToConsider, stateToConsider.table);
                            stateToConsider.robot.turnWithoutDetection(0,hooksToConsider);
                            stateToConsider.robot.moveLengthwise(-100);
                            stateToConsider.robot.useActuator(ActuatorOrder.OPEN_DOOR, false);
                            stateToConsider.robot.moveToLocation(new Vec2(410, 1540), hooksToConsider, stateToConsider.table);
                        } catch (PathNotFoundException | PointInObstacleException e) {
                            e.printStackTrace();
                            throw new ExecuteException(e);
                        }
                    }
                }

                // On prend une vitesse lente pour que le robot récupère efficacement le sable
                Speed speedBeforeScriptWasCalled = stateToConsider.robot.getLocomotionSpeed();
                stateToConsider.robot.setLocomotionSpeed(Speed.SLOW_ALL);

				// Si l'on voit l'ennemi dans le sable avant de commencer (#RCVA)
				/*if(stateToConsider.table.getObstacleManager().isDiscObstructed(new Vec2(0, 1550), 100))
					throw new UnableToMoveException(new Vec2(0, 1600), UnableToMoveReason.OBSTACLE_DETECTED);*/

                // On déploie la vitre droite
                stateToConsider.robot.useActuator(ActuatorOrder.OPEN_DOOR, false);

             /*   if (!stateToConsider.robot.getContactSensorValue(ContactSensors.DOOR_OPENED)) {
                    stateToConsider.robot.useActuator(ActuatorOrder.STOP_DOOR, false);
                    throw new BlockedActuatorException("Porte bloquée !");
                } else {
                    stateToConsider.robot.setDoor(true);
                }*/

                stateToConsider.changeRobotRadius(TechTheSand.expandedRobotRadius);
                stateToConsider.table.getObstacleManager().updateObstacles(TechTheSand.expandedRobotRadius);
                stateToConsider.robot.setDoor(true);
                ThreadSensor.modeBorgne(true);

                Hook hook = hookFactory.newXLesserHook(260);

                hook.addCallback(new Callback(new SetSand(), true, stateToConsider));

                hooksToConsider.add(hook);

                // On active la tige accrochante
                stateToConsider.robot.useActuator(ActuatorOrder.START_AXIS, false);

                // #BOURRRRRRIIIIIIIIINNNNNNN
                stateToConsider.robot.setLocomotionSpeed(Speed.SLOW_ALL);
                
                // Définition de l'arc à suivre, point de départ temporaire
                // 150
                Arc approach = new Arc(stateToConsider.robot.getPosition(), new Vec2(100,2000-(symetry ? 160 : 150)), Math.PI, true);


				try {
					stateToConsider.robot.setForceMovement(false);
					//On se déplace en courbe pour se placer en face du château
                    stateToConsider.robot.moveArcNoDetectionWhileTurning(approach, hooksToConsider);
                }
                catch (UnableToMoveException e)
                {
                    try
                    {
                        if(e.reason == UnableToMoveReason.OBSTACLE_DETECTED)
                            throw new UnableToMoveException(new Vec2(0, 1600), UnableToMoveReason.OBSTACLE_DETECTED);

                        stateToConsider.robot.setForceMovement(false);
                        //BOURRRRIIIIINNN(stateToConsider, hooksToConsider);

                        log.debug("Impossible de rentrer dans le sable, retry en droite");
                        stateToConsider.robot.setForceMovement(true);
                        stateToConsider.robot.setLocomotionSpeed(Speed.ULTRA_SLOW_ALL);
						stateToConsider.robot.moveLengthwiseWithoutDetection(-50);

                        safeTurn(Math.PI, stateToConsider, hooksToConsider);

                        stateToConsider.robot.moveLengthwise(stateToConsider.robot.getPosition().x - 200);
						stateToConsider.robot.setLocomotionSpeed(Speed.SLOW_ALL);
					}
                    catch (UnableToMoveException e2)
                    {
                        try
                        {
                            if(e2.reason == UnableToMoveReason.OBSTACLE_DETECTED)
                                throw new UnableToMoveException(new Vec2(0, 1600), UnableToMoveReason.OBSTACLE_DETECTED);

                            stateToConsider.robot.setForceMovement(false);
                            BOURRRRIIIIINNNdroit(stateToConsider, hooksToConsider);

                            log.debug("Impossible de rentrer dans le sable, retry en droite");
                            stateToConsider.robot.setForceMovement(true);
                            stateToConsider.robot.setLocomotionSpeed(Speed.SLOW_ALL);
							stateToConsider.robot.moveLengthwiseWithoutDetection(-30);
                            safeTurn(Math.PI, stateToConsider, hooksToConsider);
                           /* stateToConsider.robot.setForceMovement(false);
                            stateToConsider.robot.moveLengthwiseWithoutDetection(-30);
                            stateToConsider.robot.setForceMovement(true);*/
                            stateToConsider.robot.moveLengthwise(stateToConsider.robot.getPosition().x - 200, hooksToConsider);
							stateToConsider.robot.setLocomotionSpeed(Speed.SLOW_ALL);
						}
                        catch (UnableToMoveException e3) {
                       /*     if(e3.reason == UnableToMoveReason.OBSTACLE_DETECTED)
                                throw new UnableToMoveException(new Vec2(0, 1600), UnableToMoveReason.OBSTACLE_DETECTED);
                            log.critical("On peut vraiment pas obtenir le sable, on abandonne");
							stateToConsider.robot.setLocomotionSpeed(Speed.SLOW_ALL);
							stateToConsider.robot.turnWithoutDetection(Math.PI, hooksToConsider);
                            stateToConsider.robot.setForceMovement(false);
                            stateToConsider.robot.moveLengthwise(stateToConsider.robot.getPosition().x - 200, hooksToConsider);
                            stateToConsider.robot.moveArc(new Arc(200, -200, stateToConsider.robot.getOrientation(), false), hooksToConsider);
                            stateToConsider.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);*/
                            throw e3;
                        }
                    }
                }

                try
				{
                    stateToConsider.robot.setForceMovement(false);
                    stateToConsider.robot.moveLengthwiseWithoutDetection(-30);
                    stateToConsider.robot.setForceMovement(true);

                } catch(UnableToMoveException e)
				{
					e.printStackTrace();
				}
				try {
                    safeTurn(Math.PI, stateToConsider, hooksToConsider);
                } catch (UnableToMoveException e)
				{
					e.printStackTrace();
				}

				/*try {

					// On avance pour récupérer le sable
					// TODO la distance est arbitraire, à modifier avec les phases de test
					stateToConsider.robot.moveLengthwise(stateToConsider.robot.getPosition().x - 200, hooksToConsider, true);
				} catch (Exception e)
				{
					e.printStackTrace();
				}*/
                // Demande au robot de ne tourner que vers la gauche pour ses prochains déplacements
                stateToConsider.robot.setTurningStrategy(TurningStrategy.LEFT_ONLY);

                // Demande au robot de conserver une marche avant pour ses prochains déplacements avec le sable
                stateToConsider.robot.setDirectionStrategy(DirectionStrategy.FORCE_FORWARD_MOTION);

                // On indique au robot qu'il transporte du sable
                stateToConsider.robot.setIsSandInside(true);


                // Définition de l'arc à suivre, point de départ temporaire
				Arc approach2 = new Arc(distanceCod, distanceCod*Math.PI/4, Math.PI, false);
                stateToConsider.robot.setLocomotionSpeed(Speed.SLOW_MEDIUM);

                stateToConsider.robot.moveArc(new Arc(distanceCod, -distanceCod*Math.PI/4, Math.PI, false), hooksToConsider);

                //=============================================================================================
				// Bouclage récursif de sortie
				// On tente de sortir 3x en reculant un peu à chaque tentative, si on échoue les 3 tentatives,
				//   on abandonne le sable
				//=============================================================================================
				try
				{
                    //stateToConsider.robot.moveLengthwiseWithoutDetection(-50);
                    stateToConsider.robot.setLocomotionSpeed(Speed.SLOW_MEDIUM);
                    stateToConsider.robot.moveArc(approach2, hooksToConsider);
                    stateToConsider.robot.setLocomotionSpeed(Speed.SLOW_ALL);
				}
				catch (UnableToMoveException e)
				{
					try
					{
						if(e.reason == UnableToMoveReason.OBSTACLE_DETECTED)
							throw new UnableToMoveException(new Vec2(0, 1600), UnableToMoveReason.OBSTACLE_DETECTED);
						e.printStackTrace();
						stateToConsider.robot.moveArc(new Arc(distanceCod, -distanceCod*Math.PI/4, Math.PI, false), hooksToConsider);
						stateToConsider.robot.turnWithoutDetection(Math.PI, hooksToConsider);
						//stateToConsider.robot.moveLengthwise(-80);
                        stateToConsider.robot.setLocomotionSpeed(Speed.SLOW_MEDIUM);
                        stateToConsider.robot.moveArc(approach2, hooksToConsider);
                        stateToConsider.robot.setLocomotionSpeed(Speed.SLOW_ALL);
                    }
					catch (UnableToMoveException e2)
					{
						try
						{
							if(e2.reason == UnableToMoveReason.OBSTACLE_DETECTED)
								throw new UnableToMoveException(new Vec2(0, 1600), UnableToMoveReason.OBSTACLE_DETECTED);
							e2.printStackTrace();
							stateToConsider.robot.moveArc(new Arc(distanceCod, -distanceCod*Math.PI/4, Math.PI, false), hooksToConsider);
							stateToConsider.robot.turnWithoutDetection(Math.PI, hooksToConsider);
							//stateToConsider.robot.moveLengthwise(-80);
                            stateToConsider.robot.setLocomotionSpeed(Speed.SLOW_MEDIUM);
                            stateToConsider.robot.moveArc(approach2, hooksToConsider);
                            stateToConsider.robot.setLocomotionSpeed(Speed.SLOW_ALL);
                        }
						catch (UnableToMoveException e3)
						{
							e3.printStackTrace();
							log.critical("Impossible de se dégager, abandon du sable");
							stateToConsider.robot.setIsSandInside(false);
                            stateToConsider.robot.setForceMovement(false);
							stateToConsider.robot.setTurningStrategy(TurningStrategy.FASTEST);
							stateToConsider.robot.setDirectionStrategy(DirectionStrategy.FASTEST);
							stateToConsider.robot.turnWithoutDetection(Math.PI, hooksToConsider);
							stateToConsider.robot.moveLengthwise(stateToConsider.robot.getPosition().x - 200);
							stateToConsider.robot.moveArc(new Arc(distanceCod, -distanceCod*Math.PI/3, Math.PI, false), hooksToConsider);
							stateToConsider.robot.moveLengthwise(-100);
							throw new ExecuteException(e3);
						}
					}
				}
				//==============================================================================================


					stateToConsider.robot.setLocomotionSpeed(Speed.SLOW_MEDIUM);
					safeTurn(-Math.PI/2, stateToConsider, hooksToConsider);



                try {
                    stateToConsider.robot.setForceMovement(false);
					stateToConsider.robot.moveLengthwiseWithoutDetection(-1600 + stateToConsider.robot.getPosition().y);
                    stateToConsider.robot.setForceMovement(true);
				} catch (Exception e)
				{
					e.printStackTrace();
					stateToConsider.robot.moveLengthwiseWithoutDetection(-30);
                    stateToConsider.robot.setForceMovement(true);
                    stateToConsider.robot.moveLengthwise(-1600 + stateToConsider.robot.getPosition().y);
				}

                //Delete du château
                stateToConsider.table.getObstacleManager().freePoint(new Vec2(0, 1990));
                stateToConsider.table.getObstacleManager().freePoint(new Vec2(860+300, 1100-300));


                try {
                    stateToConsider.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);
                    stateToConsider.robot.setForceMovement(false);
                    stateToConsider.robot.turnWithoutDetection(0, hooksToConsider);
                } catch (Exception e)
                {
                    e.printStackTrace();
                }

                stateToConsider.robot.setForceMovement(false);
				stateToConsider.robot.setSmoothAcceleration(true);
                ThreadSensor.modeBorgne(false);


                try 
                {
                	// version à décommenter pour la version 2 de Castle
                	// stateToConsider.robot.moveLengthwise(1150-stateToConsider.robot.getPosition().x);
                    stateToConsider.robot.moveLengthwiseWithoutDetection(650 - stateToConsider.robot.getPosition().x);
                } catch (Exception e)
                {
                    e.printStackTrace();
                }

                stateToConsider.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);

                stateToConsider.robot.setSmoothAcceleration(false);
                stateToConsider.robot.setBasicDetection(false);


                // On reprend notre vitesse habituelle
                stateToConsider.robot.setLocomotionSpeed(speedBeforeScriptWasCalled);
                stateToConsider.table.getObstacleManager().freePoint(stateToConsider.robot.getPosition());
            }
            else if(versionToExecute == 3)
            {
                if(stateToConsider.table.configShell != 1 && stateToConsider.table.configShell != 5)
                {
                    try {
                        stateToConsider.robot.moveToLocation(Table.entryPosition.plusNewVector(new Vec2(-180,0)), hooksToConsider, stateToConsider.table);
                        stateToConsider.robot.turnWithoutDetection(0,hooksToConsider);
                        stateToConsider.robot.moveLengthwiseWithoutDetection(100);
                        stateToConsider.robot.setBasicDetection(false);
                        stateToConsider.robot.moveLengthwise(-200);
                        stateToConsider.robot.setBasicDetection(true);
                        stateToConsider.robot.useActuator(ActuatorOrder.OPEN_DOOR, false);
                        stateToConsider.robot.moveToLocation(new Vec2(300, 1590), hooksToConsider, stateToConsider.table);
                    } catch (PathNotFoundException | PointInObstacleException e) {
                        e.printStackTrace();
                        throw new ExecuteException(e);
                    }
                }
                else
                {
                    try {
                        stateToConsider.robot.moveToLocation(new Vec2(1150,1500), hooksToConsider, stateToConsider.table);
                        stateToConsider.robot.turnWithoutDetection(0,hooksToConsider);
                        stateToConsider.robot.moveLengthwise(-100);
                        stateToConsider.robot.useActuator(ActuatorOrder.OPEN_DOOR, false);
                        stateToConsider.robot.moveToLocation(new Vec2(300, 1590), hooksToConsider, stateToConsider.table);
                    } catch (PathNotFoundException | PointInObstacleException e) {
                        e.printStackTrace();
                        throw new ExecuteException(e);
                    }
                }

                ThreadSensor.modeBorgne(true);
              //  stateToConsider.robot.useActuator(ActuatorOrder.OPEN_DOOR, true);
                stateToConsider.robot.turnWithoutDetection(Math.PI/2, hooksToConsider);
                stateToConsider.robot.moveLengthwiseWithoutDetection((2000-360) - stateToConsider.robot.getPosition().y);

                stateToConsider.robot.turnWithoutDetection(Math.PI, hooksToConsider);
                stateToConsider.robot.setBasicDetection(true);
                stateToConsider.robot.setForceMovement(false);
                stateToConsider.robot.setLocomotionSpeed(Speed.SLOW_MEDIUM);
                stateToConsider.robot.useActuator(ActuatorOrder.START_AXIS, false);

                try
                {
                    stateToConsider.robot.moveLengthwise(600, hooksToConsider);
                }
                catch (UnableToMoveException e)
                {
                    if(e.reason == UnableToMoveReason.OBSTACLE_DETECTED)
                        stop();
                }

                stateToConsider.robot.moveArc(new Arc(distanceCod, -distanceCod*Math.PI/6, Math.PI, false), hooksToConsider);
                Arc approach2 = new Arc(distanceCod, distanceCod*Math.PI/4, Math.PI, false);

                //=============================================================================================
                // Bouclage récursif de sortie
                // On tente de sortir 3x en reculant un peu à chaque tentative, si on échoue les 3 tentatives,
                //   on abandonne le sable
                //=============================================================================================
                try
                {
                    //stateToConsider.robot.moveLengthwiseWithoutDetection(-50);
                    stateToConsider.robot.setLocomotionSpeed(Speed.SLOW_MEDIUM);
                    stateToConsider.robot.moveArc(approach2, hooksToConsider);
                    stateToConsider.robot.setLocomotionSpeed(Speed.SLOW_ALL);
                }
                catch (UnableToMoveException e)
                {
                    try
                    {
                        if(e.reason == UnableToMoveReason.OBSTACLE_DETECTED)
                            stop();
                        e.printStackTrace();
                        stateToConsider.robot.moveArc(new Arc(distanceCod, -distanceCod*Math.PI/6, Math.PI, false), hooksToConsider);
                        stateToConsider.robot.turnWithoutDetection(Math.PI, hooksToConsider);
                        //stateToConsider.robot.moveLengthwise(-80);
                        stateToConsider.robot.setLocomotionSpeed(Speed.SLOW_MEDIUM);
                        stateToConsider.robot.moveArc(approach2, hooksToConsider);
                        stateToConsider.robot.setLocomotionSpeed(Speed.SLOW_ALL);
                    }
                    catch (UnableToMoveException e2)
                    {
                        try
                        {
                            if(e2.reason == UnableToMoveReason.OBSTACLE_DETECTED)
                                stop();
                            e2.printStackTrace();
                            stateToConsider.robot.moveArc(new Arc(distanceCod, -distanceCod*Math.PI/6, Math.PI, false), hooksToConsider);
                            stateToConsider.robot.turnWithoutDetection(Math.PI, hooksToConsider);
                            //stateToConsider.robot.moveLengthwise(-80);
                            stateToConsider.robot.setLocomotionSpeed(Speed.SLOW_MEDIUM);
                            stateToConsider.robot.moveArc(approach2, hooksToConsider);
                            stateToConsider.robot.setLocomotionSpeed(Speed.SLOW_ALL);
                        }
                        catch (UnableToMoveException e3)
                        {
                            e3.printStackTrace();
                            log.critical("Impossible de se dégager, abandon du sable");
                            stateToConsider.robot.setIsSandInside(false);
                            stateToConsider.robot.setForceMovement(false);
                            stateToConsider.robot.setTurningStrategy(TurningStrategy.FASTEST);
                            stateToConsider.robot.setDirectionStrategy(DirectionStrategy.FASTEST);
                            stateToConsider.robot.turnWithoutDetection(Math.PI, hooksToConsider);
                            stateToConsider.robot.moveLengthwise(stateToConsider.robot.getPosition().x - 200);
                            stateToConsider.robot.moveArc(new Arc(distanceCod, -distanceCod*Math.PI/4, Math.PI, false), hooksToConsider);
                            stateToConsider.robot.moveLengthwise(-100);
                            throw new ExecuteException(e3);
                        }
                    }
                }
                //==============================================================================================

                stateToConsider.robot.setLocomotionSpeed(Speed.SLOW_MEDIUM);
                safeTurn(-Math.PI/2, stateToConsider, hooksToConsider);



                try {
                    stateToConsider.robot.setForceMovement(false);
                    stateToConsider.robot.moveLengthwiseWithoutDetection(-1600 + stateToConsider.robot.getPosition().y);
                    stateToConsider.robot.setForceMovement(true);
                } catch (Exception e)
                {
                    e.printStackTrace();
                    stateToConsider.robot.moveLengthwiseWithoutDetection(-30);
                    stateToConsider.robot.setForceMovement(true);
                    stateToConsider.robot.moveLengthwise(-1600 + stateToConsider.robot.getPosition().y);
                }

                //Delete du château
                stateToConsider.table.getObstacleManager().freePoint(new Vec2(0, 1990));
                stateToConsider.table.getObstacleManager().freePoint(new Vec2(860+300, 1100-300));


                try {
                    stateToConsider.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);
                    stateToConsider.robot.setForceMovement(false);
                    stateToConsider.robot.turnWithoutDetection(0, hooksToConsider);
                } catch (Exception e)
                {
                    e.printStackTrace();
                }

                try
                {
                    // version à décommenter pour la version 2 de Castle
                    // stateToConsider.robot.moveLengthwise(1150-stateToConsider.robot.getPosition().x);
                    stateToConsider.robot.moveLengthwiseWithoutDetection(650 - stateToConsider.robot.getPosition().x);
                } catch (Exception e)
                {
                    e.printStackTrace();
                }

                stateToConsider.robot.setForceMovement(false);
                stateToConsider.robot.setSmoothAcceleration(true);
                ThreadSensor.modeBorgne(false);

            }
		}
		catch(Exception e)
		{
            if(e instanceof UnableToMoveException)
            {
                stateToConsider.robot.setBasicDetection(true);
                stateToConsider.robot.setLocomotionSpeed(Speed.SLOW_ALL);
                stateToConsider.robot.turnWithoutDetection(Math.PI, hooksToConsider);
                stateToConsider.robot.setForceMovement(false);
                stateToConsider.robot.moveLengthwiseWithoutDetection(stateToConsider.robot.getPosition().x - 200);
                stateToConsider.robot.moveArc(new Arc(200, symetry ? -220: -260, stateToConsider.robot.getOrientation(), false), hooksToConsider);
				stateToConsider.robot.useActuator(ActuatorOrder.CLOSE_DOOR, true);
                stateToConsider.changeRobotRadius(TechTheSand.retractedRobotRadius);
                stateToConsider.table.getObstacleManager().updateObstacles(TechTheSand.retractedRobotRadius);
                stateToConsider.robot.setIsSandInside(false);
				stateToConsider.robot.turnWithoutDetection(0, hooksToConsider);
				stateToConsider.robot.moveLengthwiseWithoutDetection(600 - stateToConsider.robot.getPosition().x);
                stateToConsider.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);
            }
            stateToConsider.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);
            finalize(stateToConsider,e);
            throw e;
		}

        if(versionToExecute == 1 || versionToExecute == 3)
        {
            // DEPOS + STOP GENERAL

            stateToConsider.robot.turnWithoutDetection(0, hooksToConsider);
            stateToConsider.robot.setLocomotionSpeed(Speed.FAST_ALL);

            double angle = stateToConsider.robot.getOrientation();
            try
            {
                stateToConsider.robot.moveArc(new Arc(-500, 950, angle, false), hooksToConsider);
            }
            catch (UnableToMoveException e)
            {
                if(e.reason == UnableToMoveReason.OBSTACLE_DETECTED)
                {
                    log.debug("ENNEMI DETECTE : ATTENTE");
                    if(!waitForEnnemy(stateToConsider, stateToConsider.robot.getPosition(), true))
                        throw new UnableToMoveException(e.aim, UnableToMoveReason.OBSTACLE_DETECTED);
                    double done = Math.abs(stateToConsider.robot.getOrientationFast()-angle)/(950./500);
                    stateToConsider.robot.moveArc(new Arc(-500,(int)(950*done), stateToConsider.robot.getOrientationFast(), false), hooksToConsider);
                }
            }

            stateToConsider.robot.setForceMovement(false);

            stateToConsider.robot.setLocomotionSpeed(Speed.FAST_ALL);
            stateToConsider.robot.turn(Math.PI);
            stateToConsider.robot.setBasicDetection(false);


            try {
                stateToConsider.robot.moveLengthwiseWithoutDetection(400);
            } catch (UnableToMoveException e)
            {
                e.printStackTrace();
            }

            stop();
        }
	}

	@Override
	public int remainingScoreOfVersion(int version, GameState<?> state) 
	{
		return 0;
	}

	@Override
	public Circle entryPosition(int version, int ray, Vec2 robotPosition) throws BadVersionException
	{
		if (version == 0)
		{
			return new Circle (new Vec2(340,1990-ray));
		}
        else if(version == 1)
        {
            return new Circle(robotPosition,0);
            //return new Circle(1150,1500);
            //return new Circle(410,1540);
            //(410, 1540)
        }
        else if(version == 2 || version ==3)
        {
            return new Circle(robotPosition,0);
        }
		else
		{
			log.debug("erreur : mauvaise version de script");
			throw new BadVersionException();
		}
	}

	@Override
	public void finalize(GameState<?> state, Exception e) throws SerialFinallyException 
	{
		log.debug("Exception " + e + " dans Tech The Sand : Lancement du Finalize !");
		
		// on tente d'arrêter la tige et de ranger la porte
		try 
		{
            state.robot.setForceMovement(false);
         /*   if(state.robot.getPosition().x <= 300)
            {
                try {
                    state.robot.turn(Math.PI);
                    state.robot.moveLengthwise(-100);
                } catch (UnableToMoveException e1) {
                    e1.printStackTrace();
                }
            }*/
			state.robot.setBasicDetection(false);
			state.robot.useActuator(ActuatorOrder.STOP_AXIS, true);
            if (state.robot.getIsSandInside())
            {
                state.changeRobotRadius(TechTheSand.expandedRobotRadius);
                state.table.getObstacleManager().updateObstacles(TechTheSand.expandedRobotRadius);
            }
            else
            {
                state.robot.useActuator(ActuatorOrder.CLOSE_DOOR, false);
                state.changeRobotRadius(TechTheSand.retractedRobotRadius);
                state.table.getObstacleManager().updateObstacles(TechTheSand.retractedRobotRadius);
                state.robot.setDoor(false);
            }
            
		}
		catch (SerialConnexionException ex)
		{
			log.debug("TechTheSand : Impossible de stopper l'axe ou de ranger la porte !");
			throw new SerialFinallyException();
		}
		
	}

    private void BOURRRRIIIIINNN(GameState<Robot> stateToConsider, ArrayList<Hook> hooksToConsider) throws UnableToMoveException
    {
        boolean ok = false;
        int tries = 0;

        stateToConsider.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);
        stateToConsider.robot.moveLengthwiseWithoutDetection(-10);

        while(!ok && tries < 4)
        {
            try
            {
                Arc backDown = new Arc(-distanceCod, -50, stateToConsider.robot.getOrientation(), false);
                stateToConsider.robot.moveArc(backDown, hooksToConsider);
                Arc smallAttack = new Arc(-distanceCod, 100, stateToConsider.robot.getOrientation(), false);
                stateToConsider.robot.moveArc(smallAttack, hooksToConsider);
                ok = true;
            }
            catch(UnableToMoveException a)
            {
                if(a.reason == UnableToMoveReason.OBSTACLE_DETECTED)
                    throw new UnableToMoveException(new Vec2(0, 1600), UnableToMoveReason.OBSTACLE_DETECTED);
                log.debug("BAM !");
            }
            tries++;
        }

    }

    private void BOURRRRIIIIINNNdroit(GameState<Robot> stateToConsider, ArrayList<Hook> hooksToConsider) throws UnableToMoveException
    {
        boolean ok = false;
        int tries = 0;

        stateToConsider.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);
        stateToConsider.robot.moveLengthwiseWithoutDetection(-10);

        if(stateToConsider.robot.getPosition().x <= 200)
            return;

        while(!ok && tries < 4)
        {
            try
            {
                safeTurn(Math.PI, stateToConsider, hooksToConsider);
                stateToConsider.robot.moveLengthwiseWithoutDetection(-50);
                stateToConsider.robot.moveLengthwise(100);
                ok = true;
            }
            catch(UnableToMoveException a)
            {
                if(a.reason == UnableToMoveReason.OBSTACLE_DETECTED)
                    throw new UnableToMoveException(new Vec2(0, 1600), UnableToMoveReason.OBSTACLE_DETECTED);
                log.debug("BAM !");
            }
            tries++;
        }

    }

    private void safeTurn(double angle, GameState<Robot> stateToConsider, ArrayList<Hook> hooksToConsider) throws UnableToMoveException
    {
        try {
            stateToConsider.robot.turnWithoutDetection(angle, hooksToConsider);
        } catch (UnableToMoveException e)
        {
            stateToConsider.robot.moveLengthwise(-25);
            safeTurn(angle, stateToConsider, hooksToConsider);
        }
    }

    public void stop()
    {
        log.debug("C'est fini, Billy !");
        while(true)
        {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

	@Override
	public Integer[] getVersion(GameState<?> stateToConsider) 
	{
		return versions;
	}
}
