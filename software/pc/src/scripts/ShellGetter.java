package scripts;


import enums.*;
import exceptions.BadVersionException;
import exceptions.BlockedActuatorException;
import exceptions.ExecuteException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialFinallyException;
import hook.Hook;
import hook.types.HookFactory;
import robot.Robot;
import smartMath.Arc;
import smartMath.Circle;
import smartMath.Vec2;
import strategie.GameState;
import table.Shell;
import table.Table;
import table.obstacles.ObstacleRectangular;
import utils.Config;
import utils.Log;

import java.util.ArrayList;

/**
 * Script de récupération unitaire des coquillages
 * La première version est spéciale, elle permet d'en récupérer deux d'un coup
 * @author discord
 */
public class ShellGetter extends AbstractScript
{
    public ShellGetter(HookFactory hookFactory, Config config, Log log)
    {
        super (hookFactory,config,log);
        /**
         * Versions du script
         */
        versions = new Integer[]{0,1,2,3,4};
    }

    @Override
    public void execute(int versionToExecute, GameState<Robot> stateToConsider, ArrayList<Hook> hooksToConsider) throws SerialFinallyException, ExecuteException {

        if(versionToExecute == 0) //Récupération des deux proches du tapis (jamais ennemis)
        {
            Speed speedBeforeScriptWasCalled = stateToConsider.robot.getLocomotionSpeed();
            stateToConsider.robot.setLocomotionSpeed(Speed.MEDIUM_ALL); // TODO A changer quand asserv OK

            try {
                stateToConsider.robot.useActuator(ActuatorOrder.OPEN_DOOR, true);

                // on vérifie si la porte n'est pas bloquée lors de son ouverture
                if(!stateToConsider.robot.getContactSensorValue(ContactSensors.DOOR_OPENED))
                {
                    stateToConsider.robot.useActuator(ActuatorOrder.STOP_DOOR, false);
                    throw new BlockedActuatorException("Porte bloquée !");
                }

                // booléen de vitre ouverte vrai
                stateToConsider.robot.doorIsOpen = true;

                // on étend le rayon du robot avec la vitre ouverte
                stateToConsider.changeRobotRadius(TechTheSand.expandedRobotRadius);
                stateToConsider.table.getObstacleManager().updateObstacles(TechTheSand.expandedRobotRadius);

                //stateToConsider.robot.moveLengthwise(100);
                stateToConsider.robot.turn(-1*Math.PI/2);
                stateToConsider.robot.moveLengthwise(700);

                stateToConsider.robot.setTurningStrategy(TurningStrategy.LEFT_ONLY);

                // Orientation vers pi/2 
                stateToConsider.robot.turn(Math.PI/4);

                stateToConsider.robot.setTurningStrategy(TurningStrategy.FASTEST);


                // on déclare les coquillages comme étant dans le robot
                stateToConsider.robot.shellsOnBoard=true;
                
                // on pousse les coquillages vers notre serviette
                stateToConsider.robot.moveLengthwise(200);

                stateToConsider.robot.turn(Math.PI/2);

                stateToConsider.robot.moveLengthwise(300);

                stateToConsider.robot.turn(Math.PI/4);

                stateToConsider.robot.moveLengthwise(150);

                stateToConsider.robot.turn(Math.PI/2);

                stateToConsider.robot.moveLengthwise(300);

                // on incrémente le nombre de coquillage en notre possession
                stateToConsider.table.shellsObtained+=2;
                
                // on incrémente également le nombre de points
                stateToConsider.obtainedPoints += 4;
                
                // notre serviette devient un obstacle, histoire de ne pas déloger nos coquillages lors d'un déplacement
                stateToConsider.table.getObstacleManager().addObstacle(new ObstacleRectangular(new Vec2(1350,850), 300 + 2*stateToConsider.robot.getRobotRadius(), 500 + 2*stateToConsider.robot.getRobotRadius()));
                
                // on s'éloigne de notre serviette
                stateToConsider.robot.moveLengthwise(-500);
                
                // les coquillages ne sont plus embarqués
                stateToConsider.robot.shellsOnBoard=false;

                // on se tourne vers pi
                stateToConsider.robot.turn(Math.PI);
                
                // on se place pour repartir
                stateToConsider.robot.moveLengthwise(200);
                
                // on reprend la vitesse pre-script
                stateToConsider.robot.setLocomotionSpeed(speedBeforeScriptWasCalled);

                stateToConsider.table.getObstacleManager().freePoint(new Vec2(1300,750));
                stateToConsider.table.getObstacleManager().freePoint(new Vec2(1300,450));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(versionToExecute == -1) //La version spéciale
        {
            try
            {
                Speed speedBeforeScriptWasCalled = stateToConsider.robot.getLocomotionSpeed();
                stateToConsider.robot.setLocomotionSpeed(Speed.MEDIUM_ALL); // TODO A changer quand asserv OK

                stateToConsider.robot.useActuator(ActuatorOrder.OPEN_DOOR, true);

                // on vérifie si la porte n'est pas bloquée lors de son ouverture
                if(!stateToConsider.robot.getContactSensorValue(ContactSensors.DOOR_OPENED))
                {
                    stateToConsider.robot.useActuator(ActuatorOrder.STOP_DOOR, false);
                    throw new BlockedActuatorException("Porte bloquée !");
                }

                // booléen de vitre ouverte vrai
                stateToConsider.robot.doorIsOpen = true;

                // on étend le rayon du robot avec la vitre ouverte
                stateToConsider.changeRobotRadius(TechTheSand.expandedRobotRadius);
                stateToConsider.table.getObstacleManager().updateObstacles(TechTheSand.expandedRobotRadius);

                stateToConsider.robot.turn(-1*Math.PI/2);
                stateToConsider.robot.moveLengthwise(700);

                stateToConsider.robot.setTurningStrategy(TurningStrategy.LEFT_ONLY);

                stateToConsider.robot.setTurningStrategy(TurningStrategy.FASTEST);


                // on déclare les coquillages comme étant dans le robot
                stateToConsider.robot.shellsOnBoard=true;

                // On créé l'arc de récupération
                Arc getter = new Arc(stateToConsider.robot.getPosition(), new Vec2(1300-TechTheSand.expandedRobotRadius/2,750),
                        new Vec2(1300-TechTheSand.expandedRobotRadius/2,1050));

                //On la donne au robot
                stateToConsider.robot.moveArc(getter, hooksToConsider);

                Arc deposit = new Arc(stateToConsider.robot.getPosition(), new Vec2(1400,1200), Math.PI/2, true);

                stateToConsider.robot.moveArc(deposit, hooksToConsider);

                // on incrémente le nombre de coquillage en notre possession
                stateToConsider.table.shellsObtained+=2;

                // on incrémente également le nombre de points
                stateToConsider.obtainedPoints += 4;

                // on s'éloigne de notre serviette
                stateToConsider.robot.moveLengthwise(-500);

                // les coquillages ne sont plus embarqués
                stateToConsider.robot.shellsOnBoard=false;

                // on se tourne vers pi
                stateToConsider.robot.turn(Math.PI);

                // on se place pour repartir
                stateToConsider.robot.moveLengthwise(200);

                // on reprend la vitesse pre-script
                stateToConsider.robot.setLocomotionSpeed(speedBeforeScriptWasCalled);

                stateToConsider.table.getObstacleManager().freePoint(new Vec2(1300,750));
                stateToConsider.table.getObstacleManager().freePoint(new Vec2(1300,450));

            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        if(versionToExecute >= 1 && versionToExecute < 5)
        {
            Shell selected = getTheShell(versionToExecute);
            try {
                //Orientation vers le coquillage
                stateToConsider.robot.turn(Math.atan((selected.getY() - stateToConsider.robot.getPositionFast().y) /
                        (selected.getX() - stateToConsider.robot.getPositionFast().x)));

                stateToConsider.robot.useActuator(ActuatorOrder.OPEN_DOOR, true);

                // on vérifie si la porte n'est pas bloquée lors de son ouverture
                if(!stateToConsider.robot.getContactSensorValue(ContactSensors.DOOR_OPENED))
                {
                    stateToConsider.robot.useActuator(ActuatorOrder.STOP_DOOR, false);
                    throw new BlockedActuatorException("Porte bloquée !");
                }
                
                // booléen de vitre ouverte vrai
                stateToConsider.robot.doorIsOpen = true;

                // on étend le rayon du robot avec la vitre ouverte
                stateToConsider.changeRobotRadius(TechTheSand.expandedRobotRadius);
                stateToConsider.table.getObstacleManager().updateObstacles(TechTheSand.expandedRobotRadius);

                // on oblige le robot à tourner vers la gauche pour ne pas lâcher les coquillages
                stateToConsider.robot.setTurningStrategy(TurningStrategy.LEFT_ONLY);
                
                // on oblige également le robot à ne se déplacer que vers l'avant
                stateToConsider.robot.setDirectionStrategy(DirectionStrategy.FORCE_FORWARD_MOTION);
                
                // on tourne de 180 degrés pour chopper le coquillage
                stateToConsider.robot.turnRelative(Math.PI);
                
                // on indique que les coquillages sont dans le robot
                stateToConsider.robot.shellsOnBoard = true;

                //On supprime l'obstacle de la table
                stateToConsider.table.getObstacleManager().freePoint(selected.getPosition());

            } catch (Exception e) 
            {
                e.printStackTrace();
            }
        }
        else if(versionToExecute == 5) //Version qui prends le coquillage le plus proche
        {
            Shell selected = getClosestShell(stateToConsider.robot.getPositionFast());
            try {
                //Orientation vers le coquillage
                stateToConsider.robot.turn(Math.atan((selected.getY() - stateToConsider.robot.getPositionFast().y) /
                        (selected.getX() - stateToConsider.robot.getPositionFast().x)));

                stateToConsider.robot.useActuator(ActuatorOrder.OPEN_DOOR, true);

                // on vérifie si la porte n'est pas bloquée lors de son ouverture
                if(!stateToConsider.robot.getContactSensorValue(ContactSensors.DOOR_OPENED))
                {
                    stateToConsider.robot.useActuator(ActuatorOrder.STOP_DOOR, false);
                    throw new BlockedActuatorException("Porte bloquée !");
                }

                // booléen de vitre ouverte vrai
                stateToConsider.robot.doorIsOpen = true;

                // on étend le rayon du robot avec la vitre ouverte
                stateToConsider.changeRobotRadius(TechTheSand.expandedRobotRadius);
                stateToConsider.table.getObstacleManager().updateObstacles(TechTheSand.expandedRobotRadius);


                // on oblige le robot à tourner vers la gauche pour ne pas lâcher les coquillages
                stateToConsider.robot.setTurningStrategy(TurningStrategy.LEFT_ONLY);

                // on oblige également le robot à ne se déplacer que vers l'avant
                stateToConsider.robot.setDirectionStrategy(DirectionStrategy.FORCE_FORWARD_MOTION);

                // on tourne de 180 degrés pour chopper le coquillage
                stateToConsider.robot.turnRelative(Math.PI);

                // on indique que les coquillages sont dans le robot
                stateToConsider.robot.shellsOnBoard = true;

                //On supprime l'obstacle de la table
                stateToConsider.table.getObstacleManager().freePoint(selected.getPosition());

                Table.ourShells.remove(selected);
                Table.neutralShells.remove(selected);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int remainingScoreOfVersion(int version, GameState<?> state) {
    	
    	// pour la version spéciale 0, si aucun coquillage n'a été déposé, on renvoie le score maximum 
    	if (version == 0 && state.table.shellsObtained == 0)
    	{
    		return 4;
    	}
    	
    	// pour les autres versions, on a un potentiel de 2 points
    	else if (version>=1 && version<5)
    	{
    		return 2;
    	}
    	
    	// 0 points pour les autres situations
    	else
    	{
    		return 0;
    	}
    }

    @Override
    public Circle entryPosition(int version, int ray, Vec2 robotPosition) throws BadVersionException {
        // pour la version 0, on connait précisément l'endroit de départ du script
    	if (version == 0 || version ==-1 )
        {
            return new Circle(new Vec2(1000,1050));
        }
        
        // pour les autres version, on fait appel à une méthode déterminant l'entrée du scrpit
        else if(version >= 1 && version < 5 )
        {
            Shell selected;
            if((selected = getTheShell(version)) == null)
                throw new BadVersionException(true);

            return selected.entryPosition;

        }
        else if(version==5)
        {
            return getClosestShell(robotPosition).entryPosition;
        }
        else
        {
            //TODO jetter une exception
            log.debug("erreur : mauvaise version de script");
            throw new BadVersionException();
        }
    }

    @Override
    public void finalize(GameState<?> state) throws UnableToMoveException, SerialFinallyException {
    	
    	// on tente de ranger la porte, avec changement de rayon
    	try
    	{
            if (state.robot.shellsOnBoard)
            {
                state.changeRobotRadius(TechTheSand.expandedRobotRadius);
                state.table.getObstacleManager().updateObstacles(TechTheSand.expandedRobotRadius);
            }
            else
            {
                state.robot.useActuator(ActuatorOrder.CLOSE_DOOR, true);
                if(!state.robot.getContactSensorValue(ContactSensors.DOOR_CLOSED)) {
                    state.changeRobotRadius(TechTheSand.retractedRobotRadius);
                    state.table.getObstacleManager().updateObstacles(TechTheSand.retractedRobotRadius);
                    state.robot.setDoor(false);
                }
            }
    	}
    	catch (Exception e)
    	{
    		log.debug("ShellGetter : Impossible de ranger la vitre !");
    		throw new SerialFinallyException();
    	}

    }

    @Override
    public Integer[] getVersion(GameState<?> stateToConsider) {
        return new Integer[0];
    }
    
    // méthode déterminant l'entrée du script des versions autres que 0
    public Shell getTheShell(int version)
    {
    	// liste des coquillages alliés et neutres
        ArrayList<Shell> list = new ArrayList<Shell>();
        list.addAll(Table.ourShells);
        list.addAll(Table.neutralShells);
        
        // on parcourt cette liste
        for(Shell i : list)
        {
        	// on ne regarde que ceux de notre côté
            if(i.getX() > 0)
            {	
            	// si la version vaut 1 on le considère comme étant celui à prendre
                if(version == 1)
                    return i;

                // sinon on décrémente les versions jusqu'à la première
                else
                    version--;
            }
        }
        
        // cas par défaut si on ne trouve rien
        return null;
    }

    /**
     * Récupère le coquillage le plus proche du robot
     * @param robotPos la position du robot
     * @return le coquillage (Shell) en question
     */
    public Shell getClosestShell(Vec2 robotPos)
    {
        ArrayList<Shell> list = new ArrayList<Shell>();
        list.addAll(Table.ourShells);
        list.addAll(Table.neutralShells);
        double distance=list.get(0).getPosition().distance(robotPos);
        Shell selected = list.get(0);

        for(Shell i : list)
        {
            if(i.getPosition().distance(robotPos) < distance)
            {
                selected = i;
            }
        }
        return selected;
    }

}