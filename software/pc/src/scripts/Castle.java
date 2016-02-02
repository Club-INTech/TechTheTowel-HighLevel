package scripts;

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
import table.obstacles.ObstacleRectangular;
import utils.Config;
import utils.Log;

import java.util.ArrayList;

import enums.ActuatorOrder;
import enums.ContactSensors;
import enums.TurningStrategy;

/**
 * Script pour récupérer le tas de sable devant notre tapis
 * Version 0 : pousse le tas de sable en ligne droite jusqu'à la zone de construction
 * Version 1 : chope le château de profil depuis les coquillages en tournant dans le sens trigo
 * @author CF
 */
public class Castle extends AbstractScript
{
	public Castle(HookFactory hookFactory, Config config, Log log) 
	{
		super(hookFactory, config, log);
		
		/**
		 * Versions du script
		 */
		versions = new Integer[]{0,1};
	}

	@Override
	public void execute(int versionToExecute, GameState<Robot> stateToConsider, ArrayList<Hook> hooksToConsider) throws SerialFinallyException, ExecuteException 
	{
		try
		{
			if (versionToExecute == 0)
			{
				// on se tourne vers pi
				stateToConsider.robot.turn(Math.PI);
				
				// on indique que le sable se trouve dans le robot
				stateToConsider.robot.setIsSandInside(true);
				
				// on pousse le tas de sable dans la zone de contruction
				stateToConsider.robot.moveLengthwise(700,hooksToConsider,false);
				
				// on liste les obstacles rectangulaires 
				ArrayList<ObstacleRectangular> mRectangles = stateToConsider.table.getObstacleManager().getRectangles();
				
				// et on supprime le tas de sable
				for (int i=0;i< mRectangles.size();i++)
				{
					if (mRectangles.get(i).isInObstacle(new Vec2(580,1100)))
					{
						mRectangles.remove(i);
					}
				}
				
				// on s'éloigne de la zone de construction 
				stateToConsider.robot.moveLengthwise(-200,hooksToConsider,false);
				
				// on indique qu'on ne transporte plus de sable
				stateToConsider.robot.setIsSandInside(false);
				
				// on gagne théoriquement le nombre de points réglé grâce aux tests
				stateToConsider.obtainedPoints+=remainingScoreOfVersion(0, stateToConsider);
			}
			
			else if(versionToExecute ==1)
			{
				// déploiement du bras droit
				stateToConsider.robot.useActuator(ActuatorOrder.OPEN_DOOR, true);
				
				// la rotation ne doit se faire que dans le sens trigo
				// à priori ce sera déjà du fait que le robot vient de la mer mais on s'en assure
				stateToConsider.robot.setTurningStrategy(TurningStrategy.LEFT_ONLY);

				// on se tourne vers pi
				stateToConsider.robot.turn(Math.PI);
				
				// on indique que le sable se trouve dans le robot
				stateToConsider.robot.setIsSandInside(true);
				
				// on pousse le tas de sable dans la zone de contruction
				stateToConsider.robot.moveLengthwise(400,hooksToConsider,false);
				
				// on liste les obstacles rectangulaires 
				ArrayList<ObstacleRectangular> mRectangles = stateToConsider.table.getObstacleManager().getRectangles();
				
				// et on supprime le tas de sable
				for (int i=0;i< mRectangles.size();i++)
				{
					if (mRectangles.get(i).isInObstacle(new Vec2(580,1100)))
					{
						mRectangles.remove(i);
					}
				}
				
				// on s'éloigne de la zone de construction 
				stateToConsider.robot.moveLengthwise(-200,hooksToConsider,false);
				
				// on indique qu'on ne transporte plus de sable
				stateToConsider.robot.setIsSandInside(false);
				
                // on ferme le bras de droite
                stateToConsider.robot.useActuator(ActuatorOrder.CLOSE_DOOR, true);

                // puis on s'assure que le bras est fermé
                if(!stateToConsider.robot.getContactSensorValue(ContactSensors.DOOR_CLOSED))
                {
                    stateToConsider.robot.useActuator(ActuatorOrder.STOP_DOOR, false);
                    throw new BlockedActuatorException("Porte bloquée !");
                }

				// on gagne théoriquement le nombre de points réglé grâce aux tests
				stateToConsider.obtainedPoints+=remainingScoreOfVersion(1, stateToConsider);
				
				// la version 1 force la rotation dans le sens trigo, ce qu'il faut changer
				stateToConsider.robot.setTurningStrategy(TurningStrategy.FASTEST);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
	}

	@Override
	public int remainingScoreOfVersion(int version, GameState<?> state) 
	{
		// le tas complet rapporte 16 points maximum
		// à savoir 6 éléments (6*2) de sables et une tour (+4)
		if (version == 0)
		{
			return 16;
		}
		else if (version == 1)
		{
			//TODO changer selon le caractère destructif de la version
			return 12;
		}
		
		// 0 points par défaut
		else
		{
			return 0;
		}
	}

	@Override
	public Circle entryPosition(int version, int ray, Vec2 robotPosition)
	{
		if (version == 0)
		{
			return (new Circle(new Vec2(1200, 980)));
		}
		else if (version == 1)
		{
			//TODO tester
			return (new Circle(new Vec2(800, 800)));
		}
		else
		{
			log.debug("erreur : mauvaise version de script");
			return new Circle(new Vec2(0,0));
		}
	}

	@Override
	public void finalize(GameState<?> state) throws UnableToMoveException, SerialFinallyException 
	{
		
	}

	@Override
	public Integer[] getVersion(GameState<?> stateToConsider) 
	{
		return versions;
	}

}
