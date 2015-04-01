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
	int sleepAfterSlow=500;


	public GetPlot(HookFactory hookFactory, Config config, Log log) 
	{
		super(hookFactory, config, log);
		versions = new int[]{0,1,2,34,56,7};
		
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
				System.out.println("Trop de plots !");//Why Can't I Hold All These Limes ?
				return;
			}
			
			//TODO: trouver le bon bras pour manger
			//on choisi le bras le plus adapte (assez dificile)
			boolean isChoosenArmLeft = true;
			
			//On change le bras choisi suivant la symetrie : à voir si l'IA s'en occupera, mais pour les tests ca reste là
			if(stateToConsider.robot.getSymmetry())
			{
				isChoosenArmLeft=!isChoosenArmLeft;
			}
						
			//le robot est deja en face du plot puisqu'on a appele goToThenExec (qui met en face du centre du script) si un jour on autorise de lancer exec il faudra remettre ces lignes (et les debugger)
			//stateToConsider.robot.turn(Math.atan2(	entryPosition(versionToExecute).center.y - stateToConsider.robot.getPosition().y,	// position voulue - position actuelle
			//			 							entryPosition(versionToExecute).center.x - stateToConsider.robot.getPosition().x	// de meme
			//			 						 ));
			
			//on mange le plot
			try 
			{
				if(versionToExecute==0 || versionToExecute==1 || versionToExecute==2 )
				{
					eatPlot(false, isChoosenArmLeft, stateToConsider, true);
					stateToConsider.table.eatPlotX(versionToExecute);
				}

				if(versionToExecute==7)
				{
					System.out.println("en position ("+stateToConsider.robot.getPosition().x+", "+stateToConsider.robot.getPosition().y+") avant la rectification du PF");
					stateToConsider.robot.turn(Math.PI);// On se tourne pour sauver le PF
					stateToConsider.robot.moveLengthwise(300);
					stateToConsider.robot.turn(-Math.PI/2);
					stateToConsider.robot.moveLengthwise(300);
					eatPlot(false, isChoosenArmLeft, stateToConsider, true);
					stateToConsider.table.eatPlotX(versionToExecute);
				}
			} 
			catch (UnableToEatPlot e) 
			{
				//on a pas reussi a manger, on le dit et on termine le script
				log.debug("impossible de manger le plot n°"+versionToExecute+" mangeage echoue", this);
				finalise(stateToConsider);
				return;
			}
			
			
		}
		//version 34 on mange en plus un goblet FIXME traiter le cas où on a trois plots stockés et qu'on ne veut pas manger n°4 et ne pas lancer le script si 2 gobl stockés
		else if (versionToExecute == 34)
		{
			//debut du script recuperation du goblet
			stateToConsider.robot.turn(0,hooksToConsider, false);
			
			if (!stateToConsider.table.isGlassXTaken(0))
			{
				if (!stateToConsider.robot.isGlassStoredLeft)
				{
					stateToConsider.robot.useActuator(ActuatorOrder.ARM_LEFT_OPEN, true);					
					stateToConsider.robot.moveLengthwise(180, hooksToConsider);
					stateToConsider.robot.useActuator(ActuatorOrder.ARM_LEFT_CLOSE_SLOW, true);
					stateToConsider.robot.sleep(sleepAfterSlow);
					stateToConsider.robot.moveLengthwise(140, hooksToConsider);
					stateToConsider.robot.isGlassStoredLeft = true;
				}
				else
				{
					stateToConsider.robot.useActuator(ActuatorOrder.ARM_RIGHT_OPEN, true);					
					stateToConsider.robot.moveLengthwise(180, hooksToConsider);
					stateToConsider.robot.useActuator(ActuatorOrder.ARM_RIGHT_CLOSE_SLOW, true);
					stateToConsider.robot.sleep(sleepAfterSlow);
					stateToConsider.robot.moveLengthwise(140, hooksToConsider);
					stateToConsider.robot.isGlassStoredRight = true;
				}
				stateToConsider.table.removeGlassX(0);
			}
			else
			{
				stateToConsider.robot.moveLengthwise(320, hooksToConsider);
			}
			
			//on mange le plot 4
			try 
			{
				eatPlot(false, true, stateToConsider, false);
			}
			catch (UnableToEatPlot e) 
			{
				try 
				{
					eatPlot(false, false, stateToConsider, false);
				}
				catch (UnableToEatPlot e1) 
				{
					stateToConsider.robot.moveLengthwise(-150, hooksToConsider);
					finalise(stateToConsider);
				}
			}
			stateToConsider.table.eatPlotX(4);
			
			//on mange le plot 3
			try 
			{
				eatPlot(false, false, stateToConsider, false);
			}
			catch (UnableToEatPlot e) 
			{
				try 
				{
					eatPlot(false, false, stateToConsider, false);
				}
				catch (UnableToEatPlot e1) 
				{
					stateToConsider.robot.moveLengthwise(-150, hooksToConsider);
					finalise(stateToConsider);
				}
			}
			stateToConsider.table.eatPlotX(3);
			
		}
		//TODO derniere version a traiter FIXME traiter le cas où on a trois plots stockés et qu'on ne veut pas manger n°6
		else if (versionToExecute == 56)
		{
			stateToConsider.robot.turn(Math.PI*0.5);
			
			if (!stateToConsider.table.isPlotXEaten(5))
			{//plot 5 pas mangé
				if(!stateToConsider.table.isPlotXEaten(6))
				{
					//plot 5 et 6 pas mangé, on mange les deux avec notre bras gauche (celui du coté de l'ascenceur)
					try 
					{
						eatPlot(true, false, stateToConsider, false);
					} 
					catch (UnableToEatPlot e1) 
					{
						e1.printStackTrace();
					}
					
					stateToConsider.robot.moveLengthwise(100); // On avance vers le suivant
					
					try 
					{
						eatPlot(true, false, stateToConsider, false);
					} 
					catch (UnableToEatPlot e) 
					{
						e.printStackTrace();
					}
				}
			}
			else
			{	//Plot 5 mangé
				if(!stateToConsider.table.isPlotXEaten(6))
				{
					//plot 6 pas mangé, on ne mange que le 6
					stateToConsider.robot.moveLengthwise(100);
					
					try 
					{
						eatPlot(true, false, stateToConsider, false);
					} 
					catch (UnableToEatPlot e) 
					{
						e.printStackTrace();
					}
				}
			}
		}
	}

	@Override
	public Circle entryPosition(int id, int ray)
	{
		if (id==0)
			return new Circle (200,600,180);
		else if (id==1)
			return new Circle (400+180,250);//pose souci au PF, on avancera
		else if (id==2)
			return new Circle (630,645,180);
		else if (id==34)
			return new Circle (900,220,0);
		else if (id==56)
			return new Circle (850,1700,0); // Position devant le plot 5, on longeant l'escalier
		else if (id==7)
			return new Circle (1410,1800,180);//Point d'entrée dangereux mais (1280,1700) passe (On est à 166 du centre (1410,1800) )
		else 
			log.debug("out of bound : mauvais numero de script", this);
			return new Circle (0,1000);
	}

	@Override
	public int remainingScoreOfVersion(int id_version, GameState<?> state) 
	{
		return 0;//Ce script ne rapporte pas concretement de points tant que les plots ne sont pas posés
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
	 * @param movementAllowed vrai si on autorise le robot a avancer pour manger le plot
	 * @throws UnableToEatPlot si le mangeage echoue
	 * @throws SerialConnexionException si impossible de communiquer avec les cartes
	 * 
	 * @throws SerialException
	 */
	private void eatPlot (boolean isSecondTry, boolean isArmChosenLeft, GameState<Robot> stateToConsider, boolean movementAllowed) throws UnableToEatPlot, SerialConnexionException
	{
		//On change le bras choisi suivant la symetrie : à voir si l'IA s'en occupera, mais pour les tests ca reste là
		if(stateToConsider.robot.getSymmetry())
		{
			isArmChosenLeft=!isArmChosenLeft;
		}
		
		stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_GROUND, true);
		stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_OPEN_JAW, true);
		if (movementAllowed)
			try 
			{
				stateToConsider.robot.moveLengthwise(100);
			} catch (UnableToMoveException e1) 
			{
				log.debug("mouvement impossible, script GetPlot", this);
			}
		if (isArmChosenLeft) 
		{
			stateToConsider.robot.useActuator(ActuatorOrder.ARM_LEFT_OPEN_SLOW, true);
			stateToConsider.robot.sleep(sleepAfterSlow); //TODO modifier le temps d'xecution de ARM_LEFT_OPEN_SLOW a la place
			stateToConsider.robot.useActuator(ActuatorOrder.ARM_LEFT_CLOSE, true);
		}
		else
		{
			stateToConsider.robot.useActuator(ActuatorOrder.ARM_RIGHT_OPEN_SLOW, true);
			stateToConsider.robot.sleep(sleepAfterSlow); //TODO modifier le temps d'xecution de ARM_RIGHT_OPEN_SLOW a la place
			stateToConsider.robot.useActuator(ActuatorOrder.ARM_RIGHT_CLOSE, true);
		}
		//si on a attrape qqc on termine sinon on essaie avec l'autre bras (si isSecondTry == false)
		//si deuxieme essai ecrire dans le log qu'on a essaye de manger un plot et on jette une exeption impossible de manger
		
		
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
				eatPlot(true,!isArmChosenLeft, stateToConsider, false);
				return;
			}
		}
		stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_CLOSE_JAW, true);
		stateToConsider.robot.storedPlotCount++;
		
		//si on a encore de la place dans le guide alors on monte le plot
		if (stateToConsider.robot.storedPlotCount<4)
		{
			stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_HIGH, true);
			stateToConsider.robot.sleep(500);//TODO modifier le temps d'xecution de ELEVATOR_HIGH a la place
		}
		stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_LOW, false);
	}

	@Override
	public double getNoEnemyTime(GameState<?> stateToConsider, int id) {
		// FIXME Auto-generated method stub
		return 0;
	}
}
