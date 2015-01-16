package scripts;

import java.util.ArrayList;

import enums.ActuatorOrder;
import enums.SensorNames;
import exceptions.UnableToEatPlot;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialConnexionException;
import hook.Hook;
import hook.types.HookFactory;
import robot.Robot;
import smartMath.Circle;
import strategie.GameState;
import utils.Config;
import utils.Log;

/**
 * script de mangeage de plots. 
 * plots numerotes de 0 a 7
 * @author paul
 *
 */
public class GetPlot extends AbstractScript
{
	

	public GetPlot(HookFactory hookFactory, Config config, Log log) 
	{
		super(hookFactory, config, log);
		ArrayList<Integer> versionList = new ArrayList<Integer>();
		
		//pour le plot 0
		versionList.add(0); 
		
		 //pour le plot 1
		versionList.add(1);
		
		//pour le plot 2
		versionList.add(2); 
		
		//pour les plots 3 et 4 et le goblet devant
		versionList.add(34); 
		
		//pour les plots 5 et 6
		versionList.add(56); 
		
		//pour le plot 7
		versionList.add(7); 
		
	}
	
	@Override
	public void execute(int versionToExecute, GameState<Robot> stateToConsider, ArrayList<Hook> hooksToConsider, boolean shouldRetryIfBlocked) throws UnableToMoveException, SerialConnexionException
	{
		
		//version circulaire
		if (versionToExecute == 0 || versionToExecute == 1 || versionToExecute == 2 || versionToExecute == 7)
		{
			//si on a plus de place dans la pile on termine
			if (stateToConsider.robot.storedPlotCount == 4)
			{
				return;
			}
			
			//TODO: trouver le bon bras pour manger
			//on choisi le bras le plus adapte (assez dificile)
			boolean isChoosenArmLeft = true;
			
			//le robot est deja en face du plot puisqu'on a appele goToThenExec (qui met en face du centre du script) si un jour on autorise de lancer exec il faudra remettre ces lignes (et les debugger)
			//stateToConsider.robot.turn(Math.atan2(	entryPosition(versionToExecute).center.y - stateToConsider.robot.getPosition().y,	// position voulue - position actuelle
			//			 							entryPosition(versionToExecute).center.x - stateToConsider.robot.getPosition().x	// de meme
			//			 						 ));
			
			//on mange le plot
			try 
			{
				eatPlot(false, isChoosenArmLeft, stateToConsider);
			} 
			catch (UnableToEatPlot e) 
			{
				//on a pas reussi a manger, on le dit et on termine le script
				log.debug("impossible de manger le plot n°"+versionToExecute+" mangeage echoue", this);
				finalise(stateToConsider);
				return;
			}
			stateToConsider.table.eatPlotX(versionToExecute);
		}
		else if (versionToExecute == 34)
		{
			//debut du script recuperation du goblet
			stateToConsider.robot.turn(0,hooksToConsider, false);
			
			if (!stateToConsider.table.isGlassXTaken(0))
			{
				stateToConsider.robot.useActuator(ActuatorOrder.ARM_LEFT_OPEN, true);
				stateToConsider.robot.moveLengthwise(160, hooksToConsider);
				stateToConsider.robot.useActuator(ActuatorOrder.ARM_LEFT_CLOSE_SLOW, true);
				stateToConsider.robot.moveLengthwise(180, hooksToConsider);
				stateToConsider.robot.isGlassStoredLeft = true;
				stateToConsider.table.takeGlassX(0);
			}
			else
			{
				stateToConsider.robot.moveLengthwise(340, hooksToConsider);
			}
			
			//si on a plus de place dans la pile on termine
			if (stateToConsider.robot.storedPlotCount == 4)
			{
				return;
			}
			
			stateToConsider.robot.turn(0);
			
			//on mange le plot 3
			try 
			{
				eatPlot(false, false, stateToConsider);
			}
			catch (UnableToEatPlot e) 
			{
				stateToConsider.robot.moveLengthwise(-150, hooksToConsider);
				finalise(stateToConsider);
			}
			stateToConsider.table.eatPlotX(3);
			
			//on mange le plot 4
			try 
			{
				eatPlot(false, true, stateToConsider);
			}
			catch (UnableToEatPlot e) 
			{
				try 
				{
					eatPlot(false, false, stateToConsider);
				}
				catch (UnableToEatPlot e1) 
				{
					stateToConsider.robot.moveLengthwise(-150, hooksToConsider);
					finalise(stateToConsider);
				}
			}
			stateToConsider.table.eatPlotX(4);
			
		}
		//TODO derniere version a treter
		else if (versionToExecute == 56)
		{
			stateToConsider.robot.turn(Math.PI*-0.5);
			
			//si le plot 3 a deja ete mangé
			if (stateToConsider.table.isPlotXEaten(5))
			{
				
			}
			else
			{
				
			}
		}
		else
		
		
		try 
		{
			//pas d'actualite si on en a 3 au depart il ne faut pas ramasser le deuxieme plot mais il faut pouvoir recommencer le script a partir du deuxieme (point de depart different)
			//version double
			//on fait monter le potenetiel plot en cours
			stateToConsider.robot.turn(Math.PI*0.5, hooksToConsider, false);
			eatPlot(true, false, stateToConsider);
			//si on a ramasse qqc on incrément le nb de plots
			//on fait monter
			stateToConsider.robot.moveLengthwise(1, hooksToConsider, false);
			eatPlot(true, false, stateToConsider);
			//si on a ramasse qqc on incrément le nb de plots
		} 
		catch (UnableToMoveException e) 
		{
			log.debug("bloque", this);
			e.printStackTrace();	// remonter cette exception
		}
		catch (SerialConnexionException e)
		{
			log.debug("mauvaise entree serie", this);
			e.printStackTrace(); //  remonter cette exception
		} catch (UnableToEatPlot e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
		
		//si on en a 3 au depart il ne faut pas ramasser le deuxieme plot mais il faut pouvoir recommencer le script a partir du deuxieme (point de depart different)
		//version proche de la zone de depart ! ne pas oublier le goblet
		//se placer dans le bon sens
		//manger premier plot (bras gauche) (on essaiera quand meme avec l'autre bras au cas où ?)
		//si on a ramasse qqc on incrément le nb de plots
		//si compteur < 4 on fait monter ?
		//avancer !ne pas reculer sinon on peut perdre un verre
		//manger deuxieme plot (bras gauche) (on essaiera quand meme avec l'autre bras au cas où ?)
		//si on a ramasse qqc on incrément le nb de plots
		//si compteur < 4 on fait monter ?
		
	}

	@Override
	public Circle entryPosition(int id)
	{
		if (id==0)
			return new Circle (200,600,200);
		else if (id==1)
			return new Circle (400,250,200);
		else if (id==2)
			return new Circle (630,645,200);
		else if (id==34)
			return new Circle (900,220,0);
		else if (id==56)
			return new Circle (650,1700,0);
		else if (id==7)
			return new Circle (1410,1800,200);
		else 
			log.debug("out of bound : mauvais numero de script", this);
			return new Circle (0,1000);
	}

	@Override
	public int remainingScoreOfVersion(int id_version, GameState<?> state) 
	{
		return 0;
	}

	@Override
	protected void finalise(GameState<?> stateToConsider) 
	{
		try 
		{
			stateToConsider.robot.useActuator(ActuatorOrder.ARM_LEFT_CLOSE, true);
			stateToConsider.robot.useActuator(ActuatorOrder.ARM_RIGHT_CLOSE, true);
			stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_CLOSE_JAW, false);
			stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_LOW, true);
		} 
		catch (SerialConnexionException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * mangeage de plots, essaie a nouveau si il est impossible de manger (et que isSecondTry est = a false)
	 * ne se deplace pas, bloquante, ne met pas a jour la table mais met a jour le robot
	 * eleve le plot precedemment dans les machoires (si il existe)
	 * 
	 * @param isSecondTry vrai si l'essai de mangeage de plot est le deuxieme ou si on ne veux pas reessayer
	 * @param isArmChosenLeft vrai si on mange avec le bras gauche
	 * @throws UnableToEatPlot si le mangeage echoue
	 * @throws SerialConnexionException si impossible de communiquer avec les cartes
	 * 
	 * @throws SerialException
	 */
	private void eatPlot (boolean isSecondTry, boolean isArmChosenLeft, GameState<Robot> stateToConsider) throws UnableToEatPlot, SerialConnexionException
	{
		
		stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_GROUND, true);
		stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_OPEN_JAW, true);
		if (isArmChosenLeft) 
		{
			stateToConsider.robot.useActuator(ActuatorOrder.ARM_LEFT_OPEN_SLOW, true);
			stateToConsider.robot.sleep(1000); //TODO modifier le temps d'xecution de ARM_LEFT_OPEN_SLOW a la place
			stateToConsider.robot.useActuator(ActuatorOrder.ARM_LEFT_CLOSE, true);
		}
		else
		{
			stateToConsider.robot.useActuator(ActuatorOrder.ARM_RIGHT_OPEN_SLOW, true);
			stateToConsider.robot.sleep(1000); //TODO modifier le temps d'xecution de ARM_RIGHT_OPEN_SLOW a la place
			stateToConsider.robot.useActuator(ActuatorOrder.ARM_RIGHT_CLOSE, true);
		}
		//si on a attrape qqc on termine sinon on essaie avec l'autre bras (si isSecondTry == false)
		//si deuxieme essai ecrire dans le log qu'on a essaye de manger un plot et on jette une exeption impossible de manger
		stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_CLOSE_JAW, true);
		
		boolean sensorAnswer;
		try 
		{
			sensorAnswer = ((Boolean) stateToConsider.robot.getSensorValue(SensorNames.JAW_SENSOR));
		} 
		catch (SerialConnexionException e) 
		{
			//si impossible de communiquer avec le capteur on suppose qu'on a attrape le plot
			sensorAnswer = ((Boolean) SensorNames.JAW_SENSOR.getDefaultValue());
		}

		if (sensorAnswer)
			{
				if (isSecondTry)
				{
					log.debug("impossible d'attraper le plot", this);	
					throw new UnableToEatPlot();
				}
				else
				{
					eatPlot(true,!isArmChosenLeft, stateToConsider);
					return;
				}
			}
		stateToConsider.robot.storedPlotCount++;
		
		//si on a encore de la place dans le guide alors on monte le plot
		if (stateToConsider.robot.storedPlotCount<4)
		{
			stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_HIGH, true);
			stateToConsider.robot.sleep(500);//TODO modifier le temps d'xecution de ELEVATOR_HIGH a la place
		}
		stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_LOW, false);
	}
}
