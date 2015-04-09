package scripts;

import java.util.ArrayList;
import java.util.Arrays;

import com.sun.org.apache.bcel.internal.generic.IREM;

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
	/**
	 * le temps en ms pour faire un epile d'au moins 1 plot
	 */
	private static final int timeToDoPile = 2000;
	int sleepAfterSlow=500;


	public GetPlot(HookFactory hookFactory, Config config, Log log) 
	{
		super(hookFactory, config, log);
		versions = new Integer[]{0,1,2,34,56,7};
		
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
				stateToConsider.table.eatPlotX(versionToExecute);
				log.debug("impossible de manger le plot n°"+versionToExecute+" mangeage echoue", this);
				finalise(stateToConsider);
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
				// On ne ramasse pas l verre si on en a deja 2
				if (!stateToConsider.robot.isGlassStoredLeft)
				{
					stateToConsider.robot.useActuator(ActuatorOrder.ARM_LEFT_OPEN, true);					
					stateToConsider.robot.moveLengthwise(180, hooksToConsider);
					stateToConsider.robot.useActuator(ActuatorOrder.ARM_LEFT_CLOSE_SLOW, true);
					stateToConsider.robot.sleep(sleepAfterSlow);
					stateToConsider.robot.moveLengthwise(140, hooksToConsider);
					stateToConsider.robot.isGlassStoredLeft = true;
				}
				else if(!stateToConsider.robot.isGlassStoredRight)
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
			
			// on ne mange que si on est assez vide
			if(stateToConsider.robot.storedPlotCount < 3)
			{
				//on mange le plot 4

				try 
				{
					eatPlot(true, true, stateToConsider, false);
				}
				catch (UnableToEatPlot e) 
				{
					finalise(stateToConsider);
					stateToConsider.robot.moveLengthwise(-150, hooksToConsider);
					return;
				}
				stateToConsider.table.eatPlotX(4);
			
				//on mange le plot 3
				try 
				{
					eatPlot(false, false, stateToConsider, false);
				}
				catch (UnableToEatPlot e) 
				{
					finalise(stateToConsider);
					stateToConsider.robot.moveLengthwise(-150, hooksToConsider);
					return;
				}
				stateToConsider.table.eatPlotX(3);
			}
			
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
		else if (id_version == 56)
		{
			nbPlotOfVersion = 2;
			if (state.table.isPlotXEaten(5))
				nbPlotOfVersion -= 1;
			if (state.table.isPlotXEaten(6))
				nbPlotOfVersion -= 1;
		}
		else if (state.table.isPlotXEaten(id_version))
			nbPlotOfVersion -= 1;
		
		
		if ((90000-state.timeEllapsed)>timeToDoPile/*si il nous reste assez de temps*/ && state.robot.storedPlotCount<(5-nbPlotOfVersion)/*si on a assez de place dans le robot*/ && (state.table.getPileValue(0)==0 || state.table.getPileValue(1)==0)/*si on a pas deja fait deux piles*/)
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
	protected void finalise(GameState<?> stateToConsider) 
	{
		try 
		{
			stateToConsider.robot.useActuator(ActuatorOrder.ARM_LEFT_CLOSE, true);
			stateToConsider.robot.useActuator(ActuatorOrder.ARM_RIGHT_CLOSE, true);
			stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_CLOSE_JAW, true);
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
	 * @throws SerialConnexionException si impossible de communiquer avec les carte
	 * @throws SerialException
	 */
	private void eatPlot (boolean isSecondTry, boolean isArmChosenLeft, GameState<Robot> stateToConsider, boolean movementAllowed) throws UnableToEatPlot, SerialConnexionException
	{
		//On change le bras choisi suivant la symetrie :TODO à voir si l'IA s'en occupera, mais pour les tests ca reste là
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
		catch (SerialConnexionException e1) 
		{
			stateToConsider.robot.sleep(500);
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


	
	public Integer[] getVersion(GameState<?> stateToConsider)
	{
		ArrayList <Integer> versionList = new ArrayList<Integer>(Arrays.asList(versions));
		
		if (stateToConsider.table.isPlotXEaten(0))
			versionList.remove(0);
		if (stateToConsider.table.isPlotXEaten(1))
			versionList.remove(1);
		if (stateToConsider.table.isPlotXEaten(2))
			versionList.remove(2);
		if (stateToConsider.table.isPlotXEaten(7))
			versionList.remove(7);
		if (stateToConsider.table.isPlotXEaten(3) && stateToConsider.table.isPlotXEaten(4) && stateToConsider.table.isGlassXTaken(0))
		{
			versionList.remove(34);
		}
		if (stateToConsider.table.isPlotXEaten(5) && stateToConsider.table.isPlotXEaten(6))
			versionList.remove(56);
		
		//on converti en Integer[]
		Integer[] retour = new Integer[versionList.size()];
	    for (int i=0; i < retour.length; i++)
	    {
	    	retour[i] = versionList.get(i).intValue();
	    }
	    return retour;
	}

}
