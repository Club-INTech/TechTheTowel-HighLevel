package scripts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;

import enums.ActuatorOrder;
import enums.ObstacleGroups;
import exceptions.InObstacleException;
import exceptions.PathNotFoundException;
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
 * 
 * @author Paul
 *
 *Version 1 on pose la pile sur l'estrade (en (0,0))
 *Version 2 on pose la pile dans notre zone de depart 
 *attention executer le script 1 avant le 2 sinon impossible de recuperer la balle
 */
public class DropPile extends AbstractScript
{
	/**
	 * le temps moyen qu'on met a manger un plot (ms)
	 */
	private static final int AverageTimeToGetPlot = 5000;

	public DropPile(HookFactory hookFactory, Config config, Log log) 
	{
		super(hookFactory, config, log);
		
		//on initialise le membre versions
		versions=new Integer[]{0,1,2};
	}

	@Override
	public void goToThenExec(int versionToExecute,GameState<Robot> actualState, ArrayList<Hook> hooksToConsider) throws UnableToMoveException, SerialConnexionException, PathNotFoundException, SerialFinallyException, InObstacleException
	{
		EnumSet<ObstacleGroups> obstacleNotConsidered = EnumSet.noneOf(ObstacleGroups.class);
		if (versionToExecute == 1)
			obstacleNotConsidered.add(ObstacleGroups.GOBLET_2);
		if (versionToExecute == 0)
			obstacleNotConsidered.add(ObstacleGroups.GREEN_PLOT_2);
		
		// va jusqu'au point d'entrée de la version demandée
		actualState.robot.moveToCircle(entryPosition(versionToExecute,actualState.robot.robotRay, actualState.robot.getPosition()), hooksToConsider, actualState.table,obstacleNotConsidered);
		
		// exécute la version demandée
		execute(versionToExecute, actualState, hooksToConsider);
	}
	@Override
	public void execute(int version, GameState<Robot> stateToConsider,ArrayList<Hook> hooksToConsider) throws UnableToMoveException, SerialConnexionException, SerialFinallyException
	{
		try
		{
			//version estrade
			if (version==1)
			{
				//on se tourne vers la plus belle (l'estrade)  #^_^# 
				//Notice me Sempai  #'_'#
				
				stateToConsider.robot.turn(-Math.PI/2, hooksToConsider, false);
	
				//on eleve notre membre (l'ascenseur)
				stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_STAGE, true);
				//on se deplace vers elle
				stateToConsider.robot.moveLengthwise(120, hooksToConsider, true);
				
				//on y place notre membre
				//Sem....Pai...  =O
				
				stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_ON_STAGE, true);

				//on ouvre notre coeur (le guide) un peu
				stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_OPEN_JAW, true);
				stateToConsider.robot.moveLengthwise(20, hooksToConsider, true);//On peux avancer bien pluss avec la machoire ouverte

				
				stateToConsider.robot.useActuator(ActuatorOrder.MID_RIGHT_GUIDE, false);
				stateToConsider.robot.useActuator(ActuatorOrder.MID_LEFT_GUIDE, true);
				

				stateToConsider.robot.sleep(700);	// attente pour que la pile retrouve son équilibre
				
				//puis beaucoup
				//Ya... Yamete  ! #O_o#
				
				stateToConsider.robot.useActuator(ActuatorOrder.OPEN_RIGHT_GUIDE, false);
				stateToConsider.robot.useActuator(ActuatorOrder.OPEN_LEFT_GUIDE, true);
				//on se vide (de nos plots) et on se reajuste (de nos points)
				int ball = 0;
				if (stateToConsider.robot.isBallStored)
					ball = 1;
				//le nombre de points que nous raporte le script
				int valuePoints = (2*ball+3)*stateToConsider.robot.storedPlotCount;
				stateToConsider.obtainedPoints += valuePoints;
				stateToConsider.table.setPileValue(1,valuePoints);
				stateToConsider.robot.storedPlotCount = 0;
				stateToConsider.robot.isBallStored = false;
	
				
				//on sort notre membre
				stateToConsider.robot.moveLengthwise(-200, hooksToConsider, false);
	
				
				//mais on se referme
				//Yamete Kudasai !
				stateToConsider.robot.useActuator(ActuatorOrder.CLOSE_RIGHT_GUIDE, true);
				stateToConsider.robot.useActuator(ActuatorOrder.CLOSE_LEFT_GUIDE, true);
				
				//on remet notre membre en position de deplacement
				stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_GROUND, true);
				stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_CLOSE_JAW, true);
				stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_LOW, false);

								
				// Thank... you, sempai #'.'#
	
			}
			//version zone de depart
			else if (version==0)
			{
				stateToConsider.robot.turnWithoutDetection(Math.PI/4, hooksToConsider);
				stateToConsider.robot.moveLengthwiseWithoutDetection(350);
				
				stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_GROUND, true);
				//on ouvre le guide un peu
				stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_OPEN_JAW, true);
				
				stateToConsider.robot.useActuator(ActuatorOrder.MID_LEFT_GUIDE, false);
				stateToConsider.robot.useActuator(ActuatorOrder.MID_RIGHT_GUIDE, true);
				stateToConsider.robot.sleep(700);	// attente pour que la pile retrouve son équilibre

				//puis beaucoup
				stateToConsider.robot.useActuator(ActuatorOrder.OPEN_RIGHT_GUIDE, false);
				stateToConsider.robot.useActuator(ActuatorOrder.OPEN_LEFT_GUIDE, true);
				//on se vide de nos plots et on met a jour les points
				int ball = 0;
				if (stateToConsider.robot.isBallStored)
					ball = 1;
				int valuePoints = (2*ball+3)*stateToConsider.robot.storedPlotCount;
				stateToConsider.obtainedPoints += (2*ball+3)*stateToConsider.robot.storedPlotCount;
				stateToConsider.table.setPileValue(0, valuePoints);
				stateToConsider.robot.storedPlotCount = 0;
				stateToConsider.robot.isBallStored = false;
				stateToConsider.robot.digestPlot();
				
				stateToConsider.robot.moveLengthwiseWithoutDetection(-180);
				//Puis on finit
				stateToConsider.robot.useActuator(ActuatorOrder.CLOSE_RIGHT_GUIDE, true);
				stateToConsider.robot.useActuator(ActuatorOrder.CLOSE_LEFT_GUIDE, false);
				stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_CLOSE_JAW, true);
				
				//on remet l'ascenceur en position de deplacement
				stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_LOW, false);
				
				
				// On ne depose que si la zone est vide de gobelets ET qu'on en a au moins un verre
				if ( !stateToConsider.table.isAreaXFilled(0) 
					&& 
				     (stateToConsider.robot.isGlassStoredLeft || stateToConsider.robot.isGlassStoredRight) )
				{
					if (stateToConsider.robot.isGlassStoredLeft)
					{
						stateToConsider.robot.turn(-Math.PI/4);
						stateToConsider.robot.moveLengthwiseWithoutDetection(100);
						stateToConsider.robot.useActuator(ActuatorOrder.ARM_LEFT_OPEN, true);
						stateToConsider.robot.moveLengthwiseWithoutDetection(-250);
						stateToConsider.robot.useActuator(ActuatorOrder.ARM_LEFT_CLOSE, false);
						stateToConsider.robot.isGlassStoredLeft = false;
						stateToConsider.table.areaXFilled(0);
						stateToConsider.robot.moveLengthwiseWithoutDetection(-100);
					}	
					else if (stateToConsider.robot.isGlassStoredRight)
					{
						stateToConsider.robot.turn(Math.PI/8);
						stateToConsider.robot.useActuator(ActuatorOrder.ARM_RIGHT_OPEN, true);
						stateToConsider.robot.moveLengthwiseWithoutDetection(-250);
						stateToConsider.robot.useActuator(ActuatorOrder.ARM_RIGHT_CLOSE, false);
						stateToConsider.robot.isGlassStoredRight = false;
						stateToConsider.table.areaXFilled(0);
						stateToConsider.robot.moveLengthwiseWithoutDetection(-50);
					}
				}
				else
				{
					stateToConsider.robot.moveLengthwise(-250, hooksToConsider);
				}
			}
			else if (version == 2)
			{
				stateToConsider.robot.turn(-Math.PI/2);
				stateToConsider.robot.moveLengthwise(180, hooksToConsider, true);
				
				
				stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_GROUND, true);
				//on ouvre le guide un peu
				stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_OPEN_JAW, true);
				
				stateToConsider.robot.useActuator(ActuatorOrder.MID_LEFT_GUIDE, false);
				stateToConsider.robot.useActuator(ActuatorOrder.MID_RIGHT_GUIDE, true);
				stateToConsider.robot.sleep(700);	// attente pour que la pile retrouve son équilibre

				//puis beaucoup
				stateToConsider.robot.useActuator(ActuatorOrder.OPEN_RIGHT_GUIDE, false);
				stateToConsider.robot.useActuator(ActuatorOrder.OPEN_LEFT_GUIDE, true);
				//on se vide de nos plots et on met a jour les points
				int ball = 0;
				if (stateToConsider.robot.isBallStored)
					ball = 1;
				int valuePoints = (2*ball+3)*stateToConsider.robot.storedPlotCount;
				stateToConsider.obtainedPoints += (2*ball+3)*stateToConsider.robot.storedPlotCount;
				stateToConsider.table.setPileValue(0, stateToConsider.table.getPileValue(0)+valuePoints);
				stateToConsider.robot.storedPlotCount = 0;
				stateToConsider.robot.isBallStored = false;
				
				stateToConsider.robot.useActuator(ActuatorOrder.ARM_LEFT_MIDDLE, true);
				
				stateToConsider.robot.moveLengthwise(-400, hooksToConsider);
				//Puis on finit
				stateToConsider.robot.useActuator(ActuatorOrder.CLOSE_RIGHT_GUIDE, true);
				stateToConsider.robot.useActuator(ActuatorOrder.CLOSE_LEFT_GUIDE, true);
				stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_CLOSE_JAW, true);
				stateToConsider.robot.useActuator(ActuatorOrder.ARM_LEFT_CLOSE, true);
			}
			else
			{
				log.debug("version inconnue DropPile :"+version, this);
				finalize(stateToConsider);
			}
			
		}
		catch (UnableToMoveException | SerialConnexionException e) 
		{
			log.debug("Catch de "+e+" dans le script" , this);
			finalize(stateToConsider);
			throw e;
		}
	}
	
	
	@Override
	public Circle entryPosition(int id,int robotRay, Vec2 robotPosition) 
	{
		if (id==1)
		{
			return new Circle(200,300,0);
		}
		else if (id==0)
		{
			return new Circle(770,800); 
		}
		else if (id == 2)
			return new Circle(850,1300);
		else
		{
			log.debug("erreur DropPile script : out of bound id", this);
			return new Circle(0,1000);
		}
	}

	@Override
	public int remainingScoreOfVersion(int version, GameState<?> stateToConsider)
	{
			int ball = 0;
			if (stateToConsider.robot.isBallStored)
				ball = 1;
			int toReturn = (2*ball+3)*stateToConsider.robot.storedPlotCount;
			//si on pose dans notre base et que la balle n'a pas deja ete attrapee on reduit le nombre de points par 5*le nombre de plots encore possible d'empiler
			if (version == 0)
			{
				if (!stateToConsider.table.isBallTaken())
					toReturn -= 5*Math.min((int)(90000-stateToConsider.getTimeEllapsed())/AverageTimeToGetPlot,
												stateToConsider.table.numberOfPlotLeft());
				if (!stateToConsider.table.isAreaXFilled(0) && (stateToConsider.robot.isGlassStoredLeft || stateToConsider.robot.isGlassStoredRight))
					toReturn += 4;
			}
			//on retire les points deja faits (que l'on va casser)
			if (version != 2)
				toReturn -= stateToConsider.table.getPileValue(version);
			return toReturn;
	}

	@Override
	public void finalize(GameState<?> stateToConsider) throws SerialFinallyException
	{
		try 
		{
			stateToConsider.robot.useActuator(ActuatorOrder.CLOSE_RIGHT_GUIDE, false);
			stateToConsider.robot.useActuator(ActuatorOrder.CLOSE_LEFT_GUIDE, true);	
			stateToConsider.robot.moveLengthwise(-20);
			stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_LOW, false);
			stateToConsider.robot.useActuator(ActuatorOrder.ARM_LEFT_CLOSE, false);
			stateToConsider.robot.useActuator(ActuatorOrder.ARM_RIGHT_CLOSE, false);
		} 
		catch (SerialConnexionException | UnableToMoveException e) 
		{
			throw new SerialFinallyException();
		}
	}

	public Integer[] getVersion(GameState<?> stateToConsider)
	{
		ArrayList <Integer> versionList = new ArrayList<Integer>(Arrays.asList(versions));
		
		if(stateToConsider.table.getPileValue(0) != 0)
			versionList.remove((Integer)0);
		if(stateToConsider.table.getPileValue(1) != 0)
			versionList.remove((Integer)1);
		
		Integer[] retour = new Integer[versionList.size()];
	    for (int i=0; i < retour.length; i++)
	    {
	    	retour[i] = versionList.get(i).intValue();
	    }
	    return retour;
	}
}

