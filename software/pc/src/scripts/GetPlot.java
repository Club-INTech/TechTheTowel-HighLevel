package scripts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;

import enums.ActuatorOrder;
import enums.ObstacleGroups;
import enums.SensorNames;
import exceptions.ExecuteException;
import exceptions.InObstacleException;
import exceptions.PathNotFoundException;
import exceptions.UnableToEatPlot;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialConnexionException;
import exceptions.serial.SerialFinallyException;
import hook.Hook;
import hook.types.HookFactory;
import robot.Robot;
import smartMath.Circle;
import smartMath.Vec2;
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
	/**
	 * le temps en ms pour faire une pile d'au moins 1 plot
	 */
	private static final int timeToDoPile = 2000;


	public GetPlot(HookFactory hookFactory, Config config, Log log) 
	{
		super(hookFactory, config, log);
		versions = new Integer[]{0,1,2,34,56,7};
		
	}
	
	@Override
	public void goToThenExec(int versionToExecute,GameState<Robot> actualState, ArrayList<Hook> hooksToConsider) throws UnableToMoveException, SerialConnexionException, PathNotFoundException, SerialFinallyException, InObstacleException, ExecuteException
	{
		EnumSet<ObstacleGroups> obstacleNotConsidered = EnumSet.noneOf(ObstacleGroups.class);
		if (versionToExecute == 0)
		{
			obstacleNotConsidered.add(ObstacleGroups.GREEN_PLOT_0);
		}
		else if (versionToExecute == 1)
		{
			obstacleNotConsidered.add(ObstacleGroups.GREEN_PLOT_1);
		}
		else if (versionToExecute == 2)
		{
			obstacleNotConsidered.add(ObstacleGroups.GREEN_PLOT_2);
		}
		else if (versionToExecute == 7)
		{
			obstacleNotConsidered.add(ObstacleGroups.GREEN_PLOT_7);
		}
		else if (versionToExecute == 34)
		{
			obstacleNotConsidered.add(ObstacleGroups.GREEN_PLOT_3);
		
			obstacleNotConsidered.add(ObstacleGroups.GREEN_PLOT_4);
	
			obstacleNotConsidered.add(ObstacleGroups.GOBLET_0);
		}
		else if (versionToExecute == 56 || versionToExecute == 65)
		{
			obstacleNotConsidered.add(ObstacleGroups.GREEN_PLOT_5);
		
			obstacleNotConsidered.add(ObstacleGroups.GREEN_PLOT_6);
		}
		else if (versionToExecute == 567)
		{
			obstacleNotConsidered.add(ObstacleGroups.GREEN_PLOT_5);
		
			obstacleNotConsidered.add(ObstacleGroups.GREEN_PLOT_6);
		}
		else 
		{
			log.debug("version de Script inconnue de GetPlot :"+versionToExecute, this);
			return;
		}
			
		// va jusqu'au point d'entrée de la version demandée
		actualState.robot.moveToCircle(entryPosition(versionToExecute,actualState.robot.robotRay, actualState.robot.getPosition()), hooksToConsider, actualState.table,obstacleNotConsidered);
		
		// exécute la version demandée
		execute(versionToExecute, actualState, hooksToConsider);
	}
	
	@Override
	public void execute(int versionToExecute, GameState<Robot> stateToConsider, ArrayList<Hook> hooksToConsider) throws SerialFinallyException, ExecuteException
	{
		try
		{
			//version circulaire
			if (versionToExecute == 0 || versionToExecute == 1 || versionToExecute == 2)
			{
				//si on a plus de place dans la pile on termine
				if (stateToConsider.robot.storedPlotCount >= 4)
				{
					log.debug("Trop de plots !",this);//Why Can't I Hold All These Limes ?
					return;
				}
										
	//		//	le robot est deja en face du plot puisqu'on a appele goToThenExec (qui met en face du centre du script) si un jour on autorise de lancer exec il faudra remettre ces lignes (et les debugger)
	//			stateToConsider.robot.turn(Math.atan2(	entryPosition(versionToExecute,stateToConsider.robot.robotRay, stateToConsider.robot.getPosition()).position.y - stateToConsider.robot.getPosition().y,	// position voulue - position actuelle
	//						 							entryPosition(versionToExecute,stateToConsider.robot.robotRay, stateToConsider.robot.getPosition()).position.x - stateToConsider.robot.getPosition().x	// de meme
	//						 						 ));
				
				//on mange le plot
				try 
				{
					if (versionToExecute == 1)
					{
						if (stateToConsider.robot.storedPlotCount<4)
						{
							stateToConsider.robot.turn(Math.PI);
							eatPlot(false, true, stateToConsider, true, false);
							stateToConsider.table.eatPlotX(versionToExecute);
						}
					}
					if(versionToExecute==0 || versionToExecute==2 )
					{
						// isSecondtry est a true car 2 essais sont inutiles (statistiquement, le 1er fonctionne)
						eatPlot(false, true, stateToConsider, true, false);
						stateToConsider.table.eatPlotX(versionToExecute);
					}
	
					
				} 
				catch (UnableToEatPlot e) 
				{
					//on a pas reussi a manger, on le dit et on termine le script
					stateToConsider.table.eatPlotX(versionToExecute);
					log.debug("impossible de manger le plot n°"+versionToExecute+" mangeage echoue", this);
					finalize(stateToConsider);
					return;
				}			
			}
			//version 34 on mange en plus un goblet
			else if (versionToExecute == 34)
			{
				//debut du script recuperation du goblet
				stateToConsider.robot.turn(0,hooksToConsider, false);
				
				if (!stateToConsider.table.isGlassXTaken(0))
				{
					try 
					{
						// On ne ramasse pas l verre si on en a deja 2
						if (!stateToConsider.robot.isGlassStoredLeft)
						{
							stateToConsider.robot.useActuator(ActuatorOrder.ARM_LEFT_OPEN, true);					
							stateToConsider.robot.moveLengthwise(130, hooksToConsider);
							stateToConsider.robot.useActuator(ActuatorOrder.ARM_LEFT_CLOSE_SLOW, true);
							stateToConsider.robot.moveLengthwise(170, hooksToConsider);
							stateToConsider.robot.isGlassStoredLeft = true;
						}
						else if(!stateToConsider.robot.isGlassStoredRight)
						{
							stateToConsider.robot.useActuator(ActuatorOrder.ARM_RIGHT_OPEN, true);					
							stateToConsider.robot.moveLengthwise(130, hooksToConsider);
							stateToConsider.robot.useActuator(ActuatorOrder.ARM_RIGHT_CLOSE_SLOW, true);
							stateToConsider.robot.moveLengthwise(170, hooksToConsider);
							stateToConsider.robot.isGlassStoredRight = true;
						}
		
						stateToConsider.table.removeGlassX(0);
					}
					catch (SerialConnexionException | UnableToMoveException e) 
					{
						finalize(stateToConsider);
						stateToConsider.table.removeGlassX(0);
						stateToConsider.robot.moveLengthwise(-150, hooksToConsider);
						throw new ExecuteException(e);
					}
				}
				else
				{
					stateToConsider.robot.moveLengthwise(320, hooksToConsider);
				}
				
				// on ne mange que si on est assez vide
				if(stateToConsider.robot.storedPlotCount < 3)
				{
					//on mange le plot 4
					try 
					{
						eatPlot(true, true, stateToConsider, false, false);
					}
					catch (UnableToEatPlot | SerialConnexionException e) 
					{
						finalize(stateToConsider);
						stateToConsider.table.eatPlotX(4);
						stateToConsider.table.eatPlotX(3);
						stateToConsider.robot.moveLengthwise(-150, hooksToConsider);
						return;
					}
					stateToConsider.table.eatPlotX(4);
				
					//on mange le plot 3
					try 
					{
						eatPlot(true, false, stateToConsider, false, false);
					}
					catch (UnableToEatPlot e) 
					{
						finalize(stateToConsider);
						stateToConsider.table.eatPlotX(3);
						stateToConsider.table.eatPlotX(4);
						stateToConsider.robot.moveLengthwise(-150, hooksToConsider);
						return;
					}
					stateToConsider.table.eatPlotX(3);
				}
	//			
	//			// se dégage du bord de la table pour que la pathfinding ait un comportement correct (notamment pour enchainer avec CloseClap version 12)
	//			stateToConsider.robot.moveLengthwise(-30, hooksToConsider);
				
			}
			// attention version hardcodée ne pas utilser hors du match scripté
			// Version qui cherche  creer des piles de 1 plot
			else if (versionToExecute == 56)
			{
				stateToConsider.robot.moveLengthwise(40); // On avance vers le suivant
				
				// digère les plots si besoin
				if (stateToConsider.robot.hasRobotNonDigestedPlot())
				{
					stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_HIGH, true);
					stateToConsider.robot.digestPlot();
				}
				stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_GROUND, true);
				
				stateToConsider.robot.turn(Math.PI/2);
				if(stateToConsider.robot.storedPlotCount < 4)
				{
					stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_GROUND, true);
					stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_OPEN_JAW, false);
					stateToConsider.robot.useActuator(ActuatorOrder.ARM_LEFT_OPEN_SLOW, true);
	
					if (checkSensor(stateToConsider))
						stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_CLOSE_JAW, true);
					stateToConsider.table.eatPlotX(5);
					stateToConsider.robot.aMiamiam();
	
				
					stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_LOW, false);
					
					if(!stateToConsider.robot.isGlassStoredLeft)
					{
						stateToConsider.robot.moveLengthwise(60); // On avance vers le suivant
						stateToConsider.robot.useActuator(ActuatorOrder.ARM_LEFT_CLOSE_SLOW, true);
						stateToConsider.table.eatPlotX(6);
					}
				}
				stateToConsider.robot.moveLengthwise(-300, hooksToConsider, false);
				
				// Dans tous les cas, on ferme la machoire 
				stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_CLOSE_JAW, false);
			}
			// Version qui cherche à completer sa pile
			else if (versionToExecute == 65)
			{
				if(stateToConsider.robot.storedPlotCount >= 3)
				{
					// digère les plots si besoin
					if (stateToConsider.robot.hasRobotNonDigestedPlot())
					{
						stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_HIGH, true);
						stateToConsider.robot.digestPlot();
					}
					stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_GROUND, true);
					
					stateToConsider.robot.turn(Math.PI/2);
					if(stateToConsider.robot.storedPlotCount < 4)
					{
						stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_GROUND, true);
						stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_OPEN_JAW, false);
						stateToConsider.robot.useActuator(ActuatorOrder.ARM_LEFT_OPEN_SLOW, true);
		
						if (checkSensor(stateToConsider))
							stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_CLOSE_JAW, true);
						stateToConsider.table.eatPlotX(5);
		
					
						stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_LOW, false);
						
						if(!stateToConsider.robot.isGlassStoredLeft)
						{
							stateToConsider.robot.moveLengthwise(100); // On avance vers le suivant
							stateToConsider.robot.useActuator(ActuatorOrder.ARM_LEFT_CLOSE_SLOW, true);
							stateToConsider.table.eatPlotX(6);
						}
					}
				}
				// si la pile a besoin de 2 plots pour etre completée
				else if(stateToConsider.robot.storedPlotCount < 3)
				{
					// digère les plots si besoin
					if (stateToConsider.robot.hasRobotNonDigestedPlot())
					{
						stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_HIGH, true);
						stateToConsider.robot.digestPlot();
					}
					stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_GROUND, true);
					
					stateToConsider.robot.turn(Math.PI/2);
					try 
					{
						eatPlot(true, true, stateToConsider, false, false);
					} 
					catch (UnableToEatPlot e) 
					{
						finalize(stateToConsider);
						stateToConsider.table.eatPlotX(5);
						return;
					}
					
					if (checkSensor(stateToConsider))
						stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_CLOSE_JAW, true);
					
					stateToConsider.table.eatPlotX(5);
					stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_LOW, false);
					stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_OPEN_JAW, false);
	
					stateToConsider.robot.moveLengthwise(100); // On avance vers le suivant
	
					try 
					{
						eatPlot(true, true, stateToConsider, false, false);
					} 
					catch (UnableToEatPlot e) 
					{
						finalize(stateToConsider);
						stateToConsider.table.eatPlotX(6);
						return;
					}
					
					if (checkSensor(stateToConsider))
						stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_CLOSE_JAW, true);
					
					stateToConsider.table.eatPlotX(6);
				}
				stateToConsider.robot.moveLengthwise(-300, hooksToConsider, false);
				
				// Dans tous les cas, on ferme la machoire 
				stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_CLOSE_JAW, false);
			}
			else if(versionToExecute==567)
			{
				stateToConsider.robot.turn(Math.PI/2);
						//plot 5 et 6 pas mangé, on mange les deux avec notre bras gauche (celui du coté de l'estrade)
				try 
				{
					eatPlot(false, true, stateToConsider, false, false);
				} 
				catch (UnableToEatPlot e1) 
				{
					stateToConsider.table.eatPlotX(5);
					finalize(stateToConsider);
					log.debug( e1.logStack(), this);
				}
				//si on est suffisamment vide on mange le plot suivant
				if (stateToConsider.robot.storedPlotCount<4)
				{
					stateToConsider.robot.moveLengthwise(100); // On avance vers le suivant
					
					try 
					{
						eatPlot(false, true, stateToConsider, false, false);
					} 
					catch (UnableToEatPlot e) 
					{
						stateToConsider.table.eatPlotX(6);
						finalize(stateToConsider);
						log.debug( e.logStack(), this);
					}
				}
					
				if (stateToConsider.robot.storedPlotCount<4)
				{
					stateToConsider.robot.turn(0);
					//TODO valeur a tester
					stateToConsider.robot.moveLengthwise(420);
					try
					{
						eatPlot(true, true, stateToConsider, false, false);
						stateToConsider.table.eatPlotX(7);
					}
					catch (UnableToEatPlot e)
					{
						stateToConsider.table.eatPlotX(7);
						finalize(stateToConsider);
						log.debug( e.logStack(), this);
					}
					stateToConsider.robot.moveLengthwise(-200);
					stateToConsider.robot.turn(Math.PI/4);
					stateToConsider.robot.moveLengthwise(-300);
				}
			}
		}
		catch (UnableToMoveException | SerialConnexionException e)
		{
			throw new ExecuteException(e);
		}
	}

	@Override
	public Circle entryPosition(int id, int ray, Vec2 robotPosition)
	{
		if (id==0)
			return new Circle (200,600,200);
		else if (id==1)
			return new Circle (670,260,0);//ce point n'est pas le centre du plot (pour cause de PathDD)
		else if (id==2)
			return new Circle (630,645,200);
		else if (id==34)
			return new Circle (900,210,0);
		else if (id==56 || id==65)
			return new Circle (780,1620,0); // Position devant le plot 5, on longeant l'escalier
		else if (id==567)
			return new Circle (780,1620,0); // Position devant le plot 5, on longeant l'escalier
		else if (id==7)
			return new Circle (1410,1800,200);//Point d'entrée dangereux mais (1280,1700) passe (On est à 166 du centre (1410,1800) )
		else 
			log.debug("out of bound : mauvais numero de script", this);
			return new Circle (0,1000);
	}

	@Override
	public int remainingScoreOfVersion(int id_version, GameState<?> state) 
	{
		int toReturn = 0;
		int nbPlotOfVersion = 1;
		if (id_version == 34)
		{
			nbPlotOfVersion = 2;
			if (state.table.isPlotXEaten(3))
				nbPlotOfVersion -= 1;
			if (state.table.isPlotXEaten(4))
				nbPlotOfVersion -= 1;
		}
		else if (id_version == 56 || id_version == 65)
		{
			nbPlotOfVersion = 2;
			if (state.table.isPlotXEaten(5))
				nbPlotOfVersion -= 1;
			if (state.table.isPlotXEaten(6))
				nbPlotOfVersion -= 1;
		}
		else if (state.table.isPlotXEaten(id_version))
			nbPlotOfVersion -= 1;
		
		
		if ((90000-state.getTimeEllapsed())>timeToDoPile/*si il nous reste assez de temps*/ && state.robot.storedPlotCount<(5-nbPlotOfVersion)/*si on a assez de place dans le robot*/ && (state.table.getPileValue(0)==0 || state.table.getPileValue(1)==0)/*si on a pas deja fait deux piles*/)
		{
			toReturn = (state.robot.storedPlotCount+nbPlotOfVersion)*(3+2*(state.robot.isBallStored?1:0));
		}
		else
			toReturn = Integer.MIN_VALUE+1;
		
		if (id_version == 34 && !state.table.isGlassXTaken(0))
			toReturn += 4;
		return toReturn;
	}

	@Override
	public void finalize(GameState<?> stateToConsider) throws SerialFinallyException 
	{
		try 
		{
			stateToConsider.robot.useActuator(ActuatorOrder.ARM_LEFT_CLOSE, false);
			stateToConsider.robot.useActuator(ActuatorOrder.ARM_RIGHT_CLOSE, true);
			stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_CLOSE_JAW, true);
			stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_LOW, false);
		} 
		catch (SerialConnexionException e) 
		{
			throw new SerialFinallyException ();
		}
	}
	
	/**
	 * mangeage de plots, essaie a nouveau si il est impossible de manger (et que isSecondTry est = a false)
	 * ne se deplace pas, bloquante, ne met pas a jour la table mais met a jour le robot
	 * eleve le plot precedemment dans les machoires (si il existe)
	 * 
	 * @param isSecondTry vrai si l'essai de mangeage de plot est le deuxieme ou si on ne veux pas reessayer
	 * @param isArmChosenLeft vrai si on mange avec le bras gauche
	 * @param stateToCOnsider la table
	 * @param movementAllowed vrai si on autorise le robot a avancer pour manger le plot
	 * @param forbidArmUsage vrai si le plot doit être mangé par le seul déplacement du robot (pas de mouvement de bras pour mettre le plot dans la machoire
	 * @throws UnableToEatPlot si le mangeage echoue
	 * @throws SerialConnexionException si impossible de communiquer avec les carte
	 */
	
	private void eatPlot (boolean isSecondTry, boolean isArmChosenLeft, GameState<Robot> stateToConsider, boolean movementAllowed, boolean forbidArmUsage) throws UnableToEatPlot, SerialConnexionException
	{
		boolean sensorAnswer=false;

		//si on a deja 4 plots dans la bouche on me mange plus
		if (stateToConsider.robot.storedPlotCount >= 4)
			throw new UnableToEatPlot();
		
		
		if (stateToConsider.robot.hasRobotNonDigestedPlot())
		{
			stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_HIGH, true);
			stateToConsider.robot.digestPlot();
		}
		stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_GROUND, true);
		stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_OPEN_JAW, false);
		if (movementAllowed)
		{
			try 
			{
				stateToConsider.robot.moveLengthwise(150);
				//premiere verif (avant les bras)
				sensorAnswer = checkSensor(stateToConsider);
			}
			catch (UnableToMoveException e1) 
			{
				log.debug("mouvement impossible, script GetPlot", this);
				sensorAnswer = false;
			}
		}
		
		// si on a pas reussi a chopper le plot en avancant (ou si interdit) on essaye avec les bras
		if (!sensorAnswer)
		{
			if (!forbidArmUsage)
			{
				if (isArmChosenLeft)
				{
					stateToConsider.robot.useActuator(ActuatorOrder.ARM_LEFT_OPEN_SLOW, true);
					sensorAnswer = checkSensor(stateToConsider);
				} else
				{
					stateToConsider.robot.useActuator(ActuatorOrder.ARM_RIGHT_OPEN_SLOW, true);
					sensorAnswer = checkSensor(stateToConsider);
				}
				// si on a attrape qqc on termine sinon on essaie avec l'autre bras (si isSecondTry == false)
				// si deuxieme essai ecrire dans le log qu'on a essaye de manger un plot et on jette une exeption impossible de manger
				if (sensorAnswer)
				{
					stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_CLOSE_JAW, true);
					if (isArmChosenLeft)
						stateToConsider.robot.useActuator(ActuatorOrder.ARM_LEFT_CLOSE, true);
					else
						stateToConsider.robot.useActuator(ActuatorOrder.ARM_RIGHT_CLOSE, true);
				}
				else
				{
					if (isArmChosenLeft)
						stateToConsider.robot.useActuator(ActuatorOrder.ARM_LEFT_CLOSE, true);
					else
						stateToConsider.robot.useActuator(ActuatorOrder.ARM_RIGHT_CLOSE, true);

					if (isSecondTry)
					{

						log.debug("impossible d'attraper le plot, tentative en fermant les machoires", this);
						stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_CLOSE_JAW, true);
					} 
					else
					{

						eatPlot(true, !isArmChosenLeft, stateToConsider, false, forbidArmUsage);
						return;
					}
				}
			} 
			else
			{
				stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_CLOSE_JAW, true);
			}

		}
		// si on a reussi a chopper le plot en avancant on ferme les machoires
		else
		{
			stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_CLOSE_JAW, true);
		}
		
	
		//si la reponse etait fausse et que c'est le deuxieme essai jusque là on reverifie une fois les machoires fermées
		// si l'usage des bras est interdit, on vérifie si le plot a été mangé
		if ( (!sensorAnswer && isSecondTry) || forbidArmUsage)
		{
			sensorAnswer = checkSensor(stateToConsider);
			if (!sensorAnswer)
			{
				log.debug("impossible de manger le plot", this);
				throw new UnableToEatPlot();
			}
		}
			
		stateToConsider.robot.storedPlotCount++;
		stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_LOW, false);
		
		stateToConsider.robot.aMiamiam();
	}
	
	/**
	 * check le capteur des machoires pour voir si il est actionné
	 * 
	 * @param stateToConsider l'étate de la table a checker
	 * @return vrai si le robot a un object qui actionne le capteur des machoires
	 */
	private boolean checkSensor (GameState<Robot> stateToConsider)
	{
		boolean sensorAnswer;
		try 
		{
			sensorAnswer = ((Boolean) stateToConsider.robot.getSensorValue(SensorNames.JAW_SENSOR));
		} 
		catch (SerialConnexionException e1) 
		{
			stateToConsider.robot.sleep(40);
			try 
			{
				sensorAnswer = ((Boolean) stateToConsider.robot.getSensorValue(SensorNames.JAW_SENSOR));
			}
			catch (SerialConnexionException e2) 
			{
				//si impossible de communiquer avec le capteur on suppose qu'on a attrape le plot
				sensorAnswer = ((Boolean) SensorNames.JAW_SENSOR.getDefaultValue());
			}
		}
		return sensorAnswer;
	}


	
	public Integer[] getVersion(GameState<?> stateToConsider)
	{
		ArrayList <Integer> versionList = new ArrayList<Integer>(Arrays.asList(versions));
		
		if (stateToConsider.table.isPlotXEaten(0))
			versionList.remove((Integer)0);
		if (stateToConsider.table.isPlotXEaten(1))
			versionList.remove((Integer)1);
		if (stateToConsider.table.isPlotXEaten(2))
			versionList.remove((Integer)2);
		if (stateToConsider.table.isPlotXEaten(7))
			versionList.remove((Integer)7);
		if (stateToConsider.table.isPlotXEaten(3) && stateToConsider.table.isPlotXEaten(4) && stateToConsider.table.isGlassXTaken(0))
		{
			versionList.remove((Integer)34);
		}
		if (stateToConsider.table.isPlotXEaten(5) && stateToConsider.table.isPlotXEaten(6))
			versionList.remove((Integer)56);
		
		//on converti en Integer[]
		Integer[] retour = new Integer[versionList.size()];
	    for (int i=0; i < retour.length; i++)
	    {
	    	retour[i] = versionList.get(i).intValue();
	    }
	    return retour;
	}

}
