package scripts;

import enums.*;
import exceptions.BadVersionException;
import exceptions.BlockedActuatorException;
import exceptions.ConfigPropertyNotFoundException;
import exceptions.ExecuteException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialConnexionException;
import exceptions.serial.SerialFinallyException;
import hook.Callback;
import hook.Hook;
import hook.methods.SetSand;
import hook.types.HookFactory;
import robot.Robot;
import smartMath.Arc;
import smartMath.Circle;
import smartMath.Vec2;
import strategie.GameState;
import threads.ThreadSensor;
import utils.Config;
import utils.Log;

import java.util.ArrayList;

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


    public TechTheSand(HookFactory hookFactory, Config config, Log log)
	{
		super (hookFactory,config,log);
		/**
		 * Versions du script
		 */
		versions = new Integer[]{0,1,2};
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


                stateToConsider.robot.moveLengthwise(995);

				stateToConsider.robot.turnWithoutDetection(2*Math.PI/3, hooksToConsider);

                stateToConsider.robot.setLocomotionSpeed(speedBeforeScriptWasCalled);

			}
		
			if(versionToExecute == 1 || versionToExecute == 2)
            {
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
                
                // Définition de l'arc à suivre, point de départ temporaire
                // 150
                Arc approach = new Arc(stateToConsider.robot.getPosition(), new Vec2(100,2000-(symetry ? 200 : 150)), Math.PI, true);

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

                        log.debug("Impossible de rentrer dans le sable, retry en droite");
                        stateToConsider.robot.setForceMovement(true);
                        stateToConsider.robot.setLocomotionSpeed(Speed.ULTRA_SLOW_ALL);

                        stateToConsider.robot.turnWithoutDetection(Math.PI, hooksToConsider);
                        stateToConsider.robot.moveLengthwise(stateToConsider.robot.getPosition().x - 100);
						stateToConsider.robot.setLocomotionSpeed(Speed.SLOW_ALL);
					}
                    catch (UnableToMoveException e2)
                    {
                        try
                        {
                            if(e.reason == UnableToMoveReason.OBSTACLE_DETECTED)
                                throw new UnableToMoveException(new Vec2(0, 1600), UnableToMoveReason.OBSTACLE_DETECTED);

                            log.debug("Impossible de rentrer dans le sable, retry en droite");
                            stateToConsider.robot.setForceMovement(true);
                            stateToConsider.robot.setLocomotionSpeed(Speed.ULTRA_SLOW_ALL);
                            stateToConsider.robot.turnWithoutDetection(Math.PI, hooksToConsider);
                            stateToConsider.robot.setForceMovement(false);
                            stateToConsider.robot.moveLengthwiseWithoutDetection(-30);
                            stateToConsider.robot.setForceMovement(true);
                            stateToConsider.robot.moveLengthwise(stateToConsider.robot.getPosition().x - 100, hooksToConsider);
							stateToConsider.robot.setLocomotionSpeed(Speed.SLOW_ALL);
						}
                        catch (UnableToMoveException e3) {
                            log.critical("On peut vraiment pas obtenir le sable, on abandonne");
							stateToConsider.robot.setLocomotionSpeed(Speed.SLOW_ALL);
							stateToConsider.robot.turnWithoutDetection(Math.PI, hooksToConsider);
                            stateToConsider.robot.setForceMovement(false);
                            stateToConsider.robot.moveLengthwise(stateToConsider.robot.getPosition().x - 200, hooksToConsider);
                            stateToConsider.robot.moveArc(new Arc(200, -200, stateToConsider.robot.getOrientation(), false), hooksToConsider);
                            stateToConsider.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);
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
					stateToConsider.robot.turnWithoutDetection(Math.PI, hooksToConsider);
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



				double distanceCod = 150;
				// Définition de l'arc à suivre, point de départ temporaire
				Arc approach2 = new Arc(distanceCod, distanceCod*Math.PI/4, Math.PI, false);
                stateToConsider.robot.setLocomotionSpeed(Speed.SLOW_ALL);

                stateToConsider.robot.moveArc(new Arc(distanceCod, -distanceCod*Math.PI/5, Math.PI, false), hooksToConsider);

                //=============================================================================================
				// Bouclage récursif de sortie
				// On tente de sortir 3x en reculant un peu à chaque tentative, si on échoue les 3 tentatives,
				//   on abandonne le sable
				//=============================================================================================
				try
				{
                    stateToConsider.robot.setLocomotionSpeed(Speed.ULTRA_SLOW_ALL);
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
						stateToConsider.robot.moveArc(new Arc(distanceCod, -distanceCod*Math.PI/5, Math.PI, false), hooksToConsider);
						stateToConsider.robot.turnWithoutDetection(Math.PI, hooksToConsider);
						//stateToConsider.robot.moveLengthwise(-80);
                        stateToConsider.robot.setLocomotionSpeed(Speed.ULTRA_SLOW_ALL);
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
							stateToConsider.robot.moveArc(new Arc(distanceCod, -distanceCod*Math.PI/5, Math.PI, false), hooksToConsider);
							stateToConsider.robot.turnWithoutDetection(Math.PI, hooksToConsider);
							//stateToConsider.robot.moveLengthwise(-80);
                            stateToConsider.robot.setLocomotionSpeed(Speed.ULTRA_SLOW_ALL);
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
							stateToConsider.robot.moveLengthwise(stateToConsider.robot.getPosition().x - 150);
							stateToConsider.robot.moveArc(new Arc(distanceCod, -distanceCod*Math.PI/2, Math.PI, false), hooksToConsider);
							stateToConsider.robot.moveLengthwise(-100);
							throw new ExecuteException(e3);
						}
					}
				}
				//==============================================================================================

				try {
					stateToConsider.robot.setLocomotionSpeed(Speed.ULTRA_SLOW_ALL);
					stateToConsider.robot.turnWithoutDetection(-Math.PI/2, hooksToConsider);
				} catch (Exception e)
				{
					e.printStackTrace();
					stateToConsider.robot.moveLengthwiseWithoutDetection(-30);
					stateToConsider.robot.turnWithoutDetection(-Math.PI/2, hooksToConsider);
				}


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
                    stateToConsider.robot.setLocomotionSpeed(Speed.SLOW_ALL);
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
		}
		catch(Exception e)
		{
			finalize(stateToConsider,e);
            if(e instanceof UnableToMoveException && ((UnableToMoveException)e).reason == UnableToMoveReason.OBSTACLE_DETECTED)
            {
                stateToConsider.robot.setLocomotionSpeed(Speed.SLOW_ALL);
                stateToConsider.robot.turnWithoutDetection(Math.PI, hooksToConsider);
                stateToConsider.robot.setForceMovement(false);
                stateToConsider.robot.moveLengthwise(stateToConsider.robot.getPosition().x - 200, hooksToConsider);
                stateToConsider.robot.moveArc(new Arc(200, -200, stateToConsider.robot.getOrientation(), false), hooksToConsider);
                stateToConsider.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);
            }
            stateToConsider.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);
            throw e;
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
            return new Circle(460,2000-390);
        }
        else if(version == 2)
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

	@Override
	public Integer[] getVersion(GameState<?> stateToConsider) 
	{
		return versions;
	}
}
