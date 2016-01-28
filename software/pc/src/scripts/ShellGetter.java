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
import smartMath.Circle;
import smartMath.Vec2;
import strategie.GameState;
import table.Shell;
import table.Table;
import table.obstacles.ObstacleCircular;
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
            stateToConsider.robot.setLocomotionSpeed(Speed.SLOW_ALL); // TODO A changer quand asserv OK

            try {
                stateToConsider.robot.moveLengthwise(100);
                
                // Orientation vers pi/2 
                stateToConsider.robot.turn(Math.PI/2);
                
                // on déclare les coquillages comme étant dans le robot
                stateToConsider.robot.shellsOnBoard=true;
                
                // on pousse les coquillages vers notre serviette
                stateToConsider.robot.moveLengthwise(1000);
                
                // on incrémente le nombre de coquillage en notre possession
                stateToConsider.table.shellsObtained+=2;
                
                // on incrémente également le nombre de points
                stateToConsider.obtainedPoints += 4;
                
                // notre serviette devient un obstacle, histoire de ne pas déloger nos coquillages lors d'un déplacement
                stateToConsider.table.getObstacleManager().addObstacle(new ObstacleRectangular(new Vec2(1350,850), 300 + 2*stateToConsider.robot.getRobotRadius(), 500 + 2*stateToConsider.robot.getRobotRadius()));
                
                // on s'éloigne de notre serviette
                stateToConsider.robot.moveLengthwise(-200);
                
                // les coquillages ne sont plus embarqués
                stateToConsider.robot.shellsOnBoard=false;
                
                // on ferme notre porte
                stateToConsider.robot.useActuator(ActuatorOrder.CLOSE_DOOR, true);
                
                // on vérifie si la porte n'est pas bloquée lors de sa fermeture
                if(!stateToConsider.robot.getContactSensorValue(ContactSensors.DOOR_CLOSED))
                {
                    stateToConsider.robot.useActuator(ActuatorOrder.STOP_DOOR, false);
                    throw new BlockedActuatorException("Porte bloquée !");
                }
                
                // on l'indique au robot
                stateToConsider.robot.doorIsOpen = false;
                
                // on reprend le rayon initial du robot
                stateToConsider.robot.setRobotRadius(TechTheSand.retractedRobotRadius);
                stateToConsider.table.getObstacleManager().updateObstacles(TechTheSand.retractedRobotRadius);

                // on se tourne vers pi
                stateToConsider.robot.turn(Math.PI);
                
                // on se place pour repartir
                stateToConsider.robot.moveLengthwise(200);
                
                // on reprend la vitesse pre-script
                stateToConsider.robot.setLocomotionSpeed(speedBeforeScriptWasCalled);

                ArrayList<ObstacleCircular> cir = stateToConsider.table.getObstacleManager().getFixedObstacles();

                //On supprime les obstacles de la table
                for(ObstacleCircular i : cir)
                {
                    if(i.isInObstacle(new Vec2(1300,750)) || i.isInObstacle(new Vec2(1300,450)))
                    {
                        stateToConsider.table.getObstacleManager().removeObstacle(i);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(versionToExecute >= 1 && versionToExecute < 5)
        {
            Shell selected = getTheShell(versionToExecute);
            try {
                //Orientation vers le coquillage
                stateToConsider.robot.turn(Math.atan((selected.getY() - stateToConsider.robot.getPosition().y) /
                        (selected.getX() - stateToConsider.robot.getPosition().x)));

                //TODO ouvrir la porte droite
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
                stateToConsider.robot.setRobotRadius(TechTheSand.expandedRobotRadius);
                stateToConsider.table.getObstacleManager().updateObstacles(TechTheSand.expandedRobotRadius);

                // on oblige le robot à tourner vers la gauche pour ne pas lâcher les coquillages
                stateToConsider.robot.setTurningStrategy(TurningStrategy.LEFT_ONLY);
                
                // on oblige également le robot à ne se déplacer que vers l'avant
                stateToConsider.robot.setDirectionStrategy(DirectionStrategy.FORCE_FORWARD_MOTION);
                
                // on tourne de 180 degrés pour chopper le coquillage
                stateToConsider.robot.turnRelative(Math.PI);
                
                // on indique que les coquillages sont dans le robot
                stateToConsider.robot.shellsOnBoard = true;

                ArrayList<ObstacleCircular> cir = stateToConsider.table.getObstacleManager().getFixedObstacles();

                //On supprime l'obstacle de la table
                for(ObstacleCircular i : cir)
                {
                    if(i.isInObstacle(selected.getPosition()))
                    {
                        stateToConsider.table.getObstacleManager().removeObstacle(i);
                        break;
                    }
                }
            } catch (Exception e) 
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
    	if (version == 0 )
        {
            return new Circle(new Vec2(1050,300));
        }
        
        // pour les autres version, on fait appel à une méthode déterminant l'entrée du scrpit
        else if(version >= 1 && version < 5 )
        {
            Shell selected;
            if((selected = getTheShell(version)) == null)
                throw new BadVersionException(true);

            return selected.entryPosition;

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
    		state.robot.useActuator(ActuatorOrder.CLOSE_DOOR, true);
    		if (state.robot.shellsOnBoard == true)
    		{
    			state.robot.setRobotRadius(TechTheSand.middleRobotRadius);

                state.table.getObstacleManager().updateObstacles(TechTheSand.middleRobotRadius);

            }
    		else
    		{
    			state.robot.setRobotRadius(TechTheSand.retractedRobotRadius);

                state.table.getObstacleManager().updateObstacles(TechTheSand.retractedRobotRadius);

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
    private Shell getTheShell(int version)
    {
    	// liste des coquillages alliés et neutres
        ArrayList<Shell> list = new ArrayList<Shell>();
        list.addAll(Table.ourShells);
        list.addAll(Table.neutralShells);
        
        // on parcourt cette liste
        for(Shell i : list)
        {
        	// on ne regarde que ceux de notre côté
            if(i.getX() >= 0)
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

}