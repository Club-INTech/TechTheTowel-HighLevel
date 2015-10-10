package scripts;

import java.util.ArrayList;

import enums.ActuatorOrder;
import enums.Speed;
import exceptions.ExecuteException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialConnexionException;
import exceptions.serial.SerialFinallyException;
import hook.Hook;
import hook.types.HookFactory;
import robot.Robot;
import smartMath.*;
import strategie.GameState;
import table.Table;
import utils.Config;
import utils.Log;
/**
 * @author paul
 * Script pour deposer les tapis sur l'escalier (Exemple de script d'INTech 2015)
 */
public class DropCarpet extends AbstractScript 
{
	
	/**distance de déplacement entre le point de depart et les marches (position pour poser les tapis) en mm
	 * EDIT: n'est plus utilisé pour s'approcher des marches, juste pour en sortir
	 */
	private int distanceBetweenEntryAndStairs=220;
	
	/**
	 * Endroit ou l'on doit s'arreter pour poser les tapis
	 */
	private int carperDropYCoord = 1320;

	/**
	 * Constructeur (normalement appelé uniquement par le scriptManager) du script déposant les tapis
	 * Le container se charge de renseigner la hookFactory, le système de config et de log.
	 * @param hookFactory La factory a utiliser pour générer les hooks dont pourra avoir besoin le script
	 * @param config le fichier de config a partir duquel le script pourra se configurer
	 * @param log le système de log qu'utilisera le script
	 */
	public DropCarpet(HookFactory hookFactory, Config config, Log log)
	{
		super(hookFactory, config, log);
		// ne pas mettre la version 2 (l'IA ne dois pas être au courant)
		versions = new Integer[]{0,1}; 
		//definition du tableau des versions, a modifier a chaque ajout d'une version
	}

	@Override
	public void execute(int versionToExecute, GameState<Robot> stateToConsider,ArrayList<Hook> hooksToConsider) throws SerialFinallyException, ExecuteException
	{
		if (versionToExecute == 1)
			try 
			{	
				//on presente ses arrieres a l'escalier
				stateToConsider.robot.turn(-0.5*Math.PI, hooksToConsider, false);
				// on avance vers ces demoiselles (les marches) (attention impact possible)
				stateToConsider.robot.moveLengthwiseWithoutDetection(-(carperDropYCoord - stateToConsider.robot.getPositionFast().y), hooksToConsider, true);
	
				
				//verification de la position : on n'effectue l'action que si on est assez proche (ie pas d'obstacle)
			//	if(Math.abs((stateToConsider.robot.getPosition().y-1340))<50) // position- position du centre parfait<marge d'erreur
				
				{
					//on depose le tapis gauche (si celui-ci n'est pas deja depose)
//					if (!stateToConsider.table.getIsLeftCarpetDropped())
					{
						stateToConsider.robot.useActuator(ActuatorOrder.STOP, true);
//						stateToConsider.table.setIsLeftCarpetDropped(true);
						stateToConsider.robot.useActuator(ActuatorOrder.STOP, false);
					}
					
					//on depose le tapis droit (si celui-ci n'est pas deja depose)
//					if (!stateToConsider.table.getIsRightCarpetDropped())
					{
						stateToConsider.robot.useActuator(ActuatorOrder.STOP, true);
//						stateToConsider.table.setIsRightCarpetDropped(true);
						stateToConsider.robot.useActuator(ActuatorOrder.STOP, false);
					}
				}
				
				//on s'eloigne de l'escalier
				try 
				{
					stateToConsider.robot.moveLengthwise( distanceBetweenEntryAndStairs, hooksToConsider, false);
					
				}
				catch (UnableToMoveException e) 
				{
					// tant qu'on est pas sorti, et que le pathDingDing ne peut peut reprendre le relais on avance
					while(stateToConsider.robot.getPosition().y > (1400-distanceBetweenEntryAndStairs+20))
					{
						try
						{
							stateToConsider.robot.moveLengthwise(Math.min((stateToConsider.robot.getPosition().y - (1400-distanceBetweenEntryAndStairs)),50), hooksToConsider, false);
						}
						catch (UnableToMoveException e1)
						{
							log.debug("catch dans le script DropCarpet : impossible de s'eloigner de l'escalier");
						}
					}
				}
		
			}
			catch (UnableToMoveException | SerialConnexionException e)
			{
				finalize(stateToConsider);
				throw new ExecuteException(e);
			}
		else if(versionToExecute == 0)
		{
			try
			{
				//mise en place d'un hook pour attraper le gobelet 1.5 secondes après le début du script
//				Hook hookGoblet = hookFactory.newHookTimer(System.currentTimeMillis() + 2000, 500);
//				hookGoblet.addCallback(new Callback(new CloseRightArmExe(log),true, stateToConsider));
//				hooksToConsider.add(hookGoblet);
				
				stateToConsider.robot.useActuator(ActuatorOrder.STOP, false);

				//le 2.98 a ete testé de façon experimentale (ainsi que le 606), a modifier si quelqu'un veut le calculer
				stateToConsider.robot.turn(2.98);

				stateToConsider.robot.moveLengthwise(606, hooksToConsider, false, true, Speed.SLOW);
//				stateToConsider.table.removeGlassX(1);
				
				//on presente ses arrieres a l'escalier
				stateToConsider.robot.turn(-0.5*Math.PI, hooksToConsider, false);
				// on avance vers ces demoiselles (les marches) (attention impact possible)
				stateToConsider.robot.moveLengthwiseTowardWall(-(carperDropYCoord - stateToConsider.robot.getPositionFast().y), hooksToConsider);

				
				//verification de la position : on n'effectue l'action que si on est assez proche (ie pas d'obstacle)
				if(Math.abs((stateToConsider.robot.getPosition().y-1340))<50) // position- position du centre parfait<marge d'erreur
				
					//on depose le tapis gauche (si celui-ci n'est pas deja depose)
//					if (!stateToConsider.table.getIsLeftCarpetDropped())
					{
						stateToConsider.robot.useActuator(ActuatorOrder.STOP, true);
//						stateToConsider.table.setIsLeftCarpetDropped(true);
						stateToConsider.robot.useActuator(ActuatorOrder.STOP, false);
					}
					
					//on depose le tapis droit (si celui-ci n'est pas deja depose)
//					if (!stateToConsider.table.getIsRightCarpetDropped())
					{
						stateToConsider.robot.useActuator(ActuatorOrder.STOP, true);
//						stateToConsider.table.setIsRightCarpetDropped(true);
						stateToConsider.robot.useActuator(ActuatorOrder.STOP, false);
					}
					
				//on s'eloigne de l'escalier
				try 
				{
					stateToConsider.robot.moveLengthwise(distanceBetweenEntryAndStairs, hooksToConsider, false);
				}
				catch (UnableToMoveException e) 
				{
					// tant qu'on est pas sorti, et que le pathDingDing ne peut peut reprendre le relais on avance
					while(stateToConsider.robot.getPosition().y > (1400-distanceBetweenEntryAndStairs+20))
					{
						try
						{
							stateToConsider.robot.moveLengthwise(Math.min((stateToConsider.robot.getPosition().y - (1400-distanceBetweenEntryAndStairs)),50), hooksToConsider, false);
						}
						catch (UnableToMoveException e1)
						{
							log.debug("catch dans le script DropCarpet : impossible de s'eloigner de l'escalier");
						}
					}
				}
			}
			catch(UnableToMoveException | SerialConnexionException e)
			{
				finalize(stateToConsider);
				throw new ExecuteException(e);
			}
		}
		//version 2 du script : débute au point de départ du robot, attrape le gobelet en passant.
		//Attention : ne doit pas être appelé via goToThenExec (car l'executions commence dans la zone de départ)
		else if(versionToExecute == 2)
		{
			try
			{
				// Ralenti le robot pour ce script
				Speed speedBeforeScriptWasCalled = stateToConsider.robot.getLocomotionSpeed();
				stateToConsider.robot.setLocomotionSpeed(Speed.SLOW);
				
				//mise en place d'un hook pour attraper le gobelet 1.75 secondes après le début du script
//				Hook hookGoblet = hookFactory.newHookTimer(System.currentTimeMillis() + 2350,500);
//				hookGoblet.addCallback(new Callback(new CloseRightArmExe(log),true, stateToConsider));
//				hooksToConsider.add(hookGoblet);
				
				stateToConsider.robot.useActuator(ActuatorOrder.STOP, false);

				//le 3.05 a ete testé de façon experimentale (ainsi que le 850), a modifier si quelqu'un veut le calculer
				stateToConsider.robot.turn(3.05);
				stateToConsider.robot.moveLengthwise(850, hooksToConsider, false, true, Speed.SLOW);
//				stateToConsider.table.removeGlassX(1);
				stateToConsider.robot.useActuator(ActuatorOrder.STOP, false);// on ferme dans tous les cas

				//on presente ses arrieres a l'escalier
				stateToConsider.robot.turn(-0.5*Math.PI, hooksToConsider, false);

				// on avance vers ces demoiselles (les marches) (attention impact possible)
				log.debug("Position avant le mur : "+stateToConsider.robot.getPosition().toString());
				stateToConsider.robot.moveLengthwiseTowardWall( -(carperDropYCoord - stateToConsider.robot.getPositionFast().y), hooksToConsider);
				
				stateToConsider.robot.useActuator(ActuatorOrder.STOP, false);
				stateToConsider.robot.useActuator(ActuatorOrder.STOP, false);


				// si l'orientation n'est pas parfaite, on retente
				if(Math.abs(stateToConsider.robot.getOrientation() - Math.PI/2) > 0.1)
					stateToConsider.robot.turn(-0.5*Math.PI, hooksToConsider, false);
				
				//verification de la position : on n'effectue l'action que si on est assez proche (ie pas d'obstacle)
				//if(Math.abs((stateToConsider.robot.getPosition().y-1340))<50) // position- position du centre parfait<marge d'erreur
				
					//on depose le tapis gauche (si celui-ci n'est pas deja depose)
//				if (!stateToConsider.table.getIsLeftCarpetDropped())
				{
					stateToConsider.robot.useActuator(ActuatorOrder.STOP, true);
//					stateToConsider.table.setIsLeftCarpetDropped(true);
					stateToConsider.robot.sleep(200);
					stateToConsider.robot.useActuator(ActuatorOrder.STOP, false);
				}
				
				//on depose le tapis droit (si celui-ci n'est pas deja depose)
//				if (!stateToConsider.table.getIsRightCarpetDropped())
				{
					stateToConsider.robot.useActuator(ActuatorOrder.STOP, true);
//					stateToConsider.table.setIsRightCarpetDropped(true);
					stateToConsider.robot.sleep(200);
					stateToConsider.robot.useActuator(ActuatorOrder.STOP, false);
				}
					
				log.debug("Position après tapis : "+stateToConsider.robot.getPosition().toString());

				// on peut reprendre la vitesse que nous avions avant l'éxécution de ce script puisque les tapis sont largués (si on va trop vite avec les tapis ils masquent les capteurs)
				stateToConsider.robot.setLocomotionSpeed(speedBeforeScriptWasCalled);
					
				//on s'eloigne de l'escalier
				try 
				{
					stateToConsider.robot.moveLengthwise(distanceBetweenEntryAndStairs, hooksToConsider, false);
				}
				catch (UnableToMoveException e) 
				{
					// tant qu'on est pas sorti, et que le pathDingDing ne peut peut reprendre le relais on avance
					while(stateToConsider.robot.getPosition().y > (1400-distanceBetweenEntryAndStairs+20))
					{
						try
						{
							stateToConsider.robot.moveLengthwise(Math.min((stateToConsider.robot.getPosition().y - (1400-distanceBetweenEntryAndStairs)),50), hooksToConsider, false);
						}
						catch (UnableToMoveException e1)
						{
							log.debug("catch dans le script DropCarpet : impossible de s'eloigner de l'escalier");
						}
					}
				}
			}
			catch(UnableToMoveException | SerialConnexionException e)
			{
				finalize(stateToConsider);
				throw new ExecuteException(e);
			}
		}
	}
	
	@Override
	public Circle entryPosition(int id, int radius, Vec2 robotPosition)
	{
		//taille totale des escaliers : 1066 / on divisse par 4; 266.5
		if (id==1)
			return new Circle(266,1400-distanceBetweenEntryAndStairs);//point de depose - distance de deplacement jusqua ce point - rayon robot (distance arriere - roues))
		else if (id == 0)
			return new Circle (1500-320-77-250,1000);
		else if (id == 2)
			return new Circle(Table.entryPosition);
		else
			log.debug("erreur id script :"+id+" attendu 0 ou 1");
			return new Circle(0,1000);
	}

	@Override
	public int remainingScoreOfVersion(int version, GameState<?> stateToConsider) 
	{
		int score = 24;
//		if (version == 0 && !stateToConsider.table.isGlassXTaken(1))
			score += 4;
//		if(stateToConsider.table.getIsLeftCarpetDropped())
			score -= 12;
//		if(stateToConsider.table.getIsRightCarpetDropped())
			score -= 12;
		
		return score;
	}

	@Override
	public void finalize(GameState<?> stateToConsider) throws SerialFinallyException 
	{
		try 
		{
			stateToConsider.robot.useActuator(ActuatorOrder.STOP, false);
			stateToConsider.robot.useActuator(ActuatorOrder.STOP, false);
			stateToConsider.robot.useActuator(ActuatorOrder.STOP, false);
			stateToConsider.robot.useActuator(ActuatorOrder.STOP, false);
		} 
		catch (SerialConnexionException e) 
		{
			log.debug("erreur termine DropCarpet script : impossible de ranger");
			throw new SerialFinallyException ();
		}
	}

	public Integer[] getVersion(GameState<?> stateToConsider)
	{
		//si un des deux tapis n'est pas deposé
//		if (!stateToConsider.table.getIsLeftCarpetDropped() || !stateToConsider.table.getIsRightCarpetDropped())
			//et si le verre 1 n'est pas pris on renvoie toutes les versions
//			if (!stateToConsider.table.isGlassXTaken(1))
				return versions;
			//si le verre 1 est pris on donne seulement a version 1
//			else
//				return new Integer[]{1};

		//si les deux tapis ont été deposés on ne donne aucune version (tant pis pour le gobelet 1, il y a mieux pour le catch)
//		return new Integer[]{};
	}

}
