package scripts;


// WARNING: ne pas supprimer les lignes taggées inutiles
// car des lignes en commentaires eront intégrées dans le code
// lorsque le bas niveau sera opérationnel
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
 * Version 1 : lorsque le robot vient de la mer, tourne pour choper le château puis le déplace dans la zone de construction
 * Version 2 : chope le château alors que le robot revient de la dune et en contient déjà une partie
 * @author CF, Cérézas
 */
public class Castle extends AbstractScript
{
	// epsilon doit permettre au robot d'exécuter la version 2 du script
	// sans procéder à une putain de rotation d'un tour complet.
	// Pour ce faire, nous devons avoir epsilon<Locomotion.maxRotationTurningStrategyIgnore.
	private double epsilon = 2.*Math.PI/17.;
	
	public Castle(HookFactory hookFactory, Config config, Log log) 
	{
		super(hookFactory, config, log);
		
		/**
		 * Versions du script
		 */
		versions = new Integer[]{0,1,2};
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
			
			else if(versionToExecute == 1)
			{
				/*
				// déploiement du bras droit sous réserve qu'il n'est pas déjà ouvert
				if (stateToConsider.robot.getContactSensorValue(ContactSensors.DOOR_CLOSED))
				{
					stateToConsider.robot.useActuator(ActuatorOrder.OPEN_DOOR, true);
				}
				*/
				
				// la rotation ne doit se faire que dans le sens trigo
				// à priori ce sera déjà du fait que le robot vient de la mer mais on s'en assure
				stateToConsider.robot.setTurningStrategy(TurningStrategy.LEFT_ONLY);

				// on se tourne vers pi
				stateToConsider.robot.turn(Math.PI);
				
				// on indique que le sable se trouve dans le robot
				stateToConsider.robot.setIsSandInside(true);
				
				// on pousse le tas de sable dans la zone de contruction
				// TODO tester
				stateToConsider.robot.moveLengthwise(470,hooksToConsider,false);
				
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
                //stateToConsider.robot.useActuator(ActuatorOrder.CLOSE_DOOR, true);

                // puis on s'assure que le bras est fermé
				/*
                if(!stateToConsider.robot.getContactSensorValue(ContactSensors.DOOR_CLOSED))
                {
                    stateToConsider.robot.useActuator(ActuatorOrder.STOP_DOOR, false);
                    throw new BlockedActuatorException("Porte bloquée !");
                }
                */

				// on gagne théoriquement le nombre de points réglé grâce aux tests
				stateToConsider.obtainedPoints+=remainingScoreOfVersion(1, stateToConsider);
				
				// la version 1 force la rotation dans le sens trigo, ce qu'il faut changer
				stateToConsider.robot.setTurningStrategy(TurningStrategy.FASTEST);
			}
			
			else if(versionToExecute == 2)
			{
				// le bras droit est sensé être ouvert (et contenir du sable de la dune)
				/*
				if (stateToConsider.robot.getContactSensorValue(ContactSensors.DOOR_CLOSED))
				{
					log.critical("Mauvais appel du script ou mauvaise version appelée:\n doit succéder à une première récupération de sable de la dune");
					return;
				}
				*/
				
				// La rotation ne doit se faire que dans le sens trigo,
				// surtout que le bras de droite est déjà sensé contenir une partie de la dune.
				// -> modifier Locomotion.maxRotationTurningStrategyIgnore si besoin.
				stateToConsider.robot.setTurningStrategy(TurningStrategy.LEFT_ONLY);

				/*
				
				// on se tourne vers pi avec un léger décalage pour gagner du temps
				// (en effet on vient de la dune et le château est encore un obstacle)
				stateToConsider.robot.turn(-Math.PI+epsilon);
				
				// on indique que le sable se trouve dans le robot
				stateToConsider.robot.setIsSandInside(true);
				
				// on pousse le tas de sable dans la zone de contruction
				stateToConsider.robot.moveLengthwise(805,hooksToConsider,false);
				
				*/
				
				// Version non courbe
				/*
				stateToConsider.robot.turn(-3*Math.PI/4.-Math.PI/50.);
				stateToConsider.robot.moveLengthwise(567,hooksToConsider,false);
				stateToConsider.robot.turn(-3*Math.PI/4.-epsilon);
				stateToConsider.robot.moveLengthwise(157,hooksToConsider,false);
				stateToConsider.robot.setIsSandInside(true);
				stateToConsider.robot.turn(-3*Math.PI/4.-2*epsilon);
				stateToConsider.robot.moveLengthwise(207,hooksToConsider,false);
				*/
				
				// Version trajectoires courbes
				//TODO Tester
				stateToConsider.robot.moveArc(new Arc(entryPosition(2, 0, stateToConsider.robot.getPosition()).position, new Vec2(850, 1050), new Vec2(400, 820)), hooksToConsider);
				
				
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
				stateToConsider.robot.moveLengthwise(-290,hooksToConsider,false);
				
				// on indique qu'on ne transporte plus de sable
				stateToConsider.robot.setIsSandInside(false);
				
                // on ferme le bras de droite
                //stateToConsider.robot.useActuator(ActuatorOrder.CLOSE_DOOR, true);

                // puis on s'assure que le bras est fermé
                /*
                if(!stateToConsider.robot.getContactSensorValue(ContactSensors.DOOR_CLOSED))
                {
                    stateToConsider.robot.useActuator(ActuatorOrder.STOP_DOOR, false);
                    throw new BlockedActuatorException("Porte bloquée !");
                }
                */

				// on gagne théoriquement le nombre de points réglé grâce aux tests
				stateToConsider.obtainedPoints+=remainingScoreOfVersion(2, stateToConsider);
				
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
		if (version == 0 | version == 1)
		{
			return 16;
		}
		
		else if (version == 2)
		{
			//TODO changer selon le caractère destructif de la version ou les pertes constatées
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
			// former: return (new Circle(new Vec2(1200, 980)));
			return (new Circle(new Vec2(1200, 980)));
		}
		else if (version == 1)
		{
			//TODO tester
			return (new Circle(new Vec2(800, 800)));
		}
		else if (version == 2)
		{
			//testé et approuvé
			return (new Circle(new Vec2(1250, 1370)));
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
