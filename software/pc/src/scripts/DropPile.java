package scripts;

import java.util.ArrayList;

import enums.ActuatorOrder;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialConnexionException;
import exceptions.serial.SerialFinallyException;
import hook.Hook;
import hook.types.HookFactory;
import robot.Robot;
import smartMath.Circle;
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
		versions=new int[]{1,2};
	}

	@Override
	public void execute(int version, GameState<Robot> stateToConsider,ArrayList<Hook> hooksToConsider,boolean shouldRetryIfBlocke) throws UnableToMoveException, SerialConnexionException
	{
		if (version==1)
		{
			//TODO: la version actuelle pose la pile A COTE DE l'estrade (il faut la modifier pour qu'elle la pose dessus)
			//on se tourne vers la plus belle (l'estrade)  #^_^# 
			
			//Notice me Sempai  #'_'#
			
			stateToConsider.robot.turn(Math.PI*-3/4, hooksToConsider, false);//On avance pour eviter le PathNotFoundd EXception

			//on eleve notre membre (l'ascenseur)
//			stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_STAGE, true);
			//on se deplace vers elle
//			stateToConsider.robot.moveLengthwise(50, hooksToConsider, true);
			
			//on y place notre membre
			//Sem....Pai...  =O
			
//			stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_LOW, true);
			stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_GROUND, true);
			//on ouvre notre coeur (le guide) un peu
			stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_OPEN_JAW, true);
			stateToConsider.robot.moveLengthwise((int) (100*Math.sqrt(4.5)), hooksToConsider, true);
//			stateToConsider.robot.moveLengthwise(50, hooksToConsider, true);
			stateToConsider.robot.useActuator(ActuatorOrder.MID_LEFT_GUIDE, true);
			stateToConsider.robot.useActuator(ActuatorOrder.MID_RIGHT_GUIDE, true);
			
			//puis beaucoup
			//Ya... Yamete  ! #O_o#
			
			stateToConsider.robot.useActuator(ActuatorOrder.OPEN_RIGHT_GUIDE, true);
			stateToConsider.robot.useActuator(ActuatorOrder.OPEN_LEFT_GUIDE, true);
			//on se vide (de nos plots) et on se reajuste (de nos points)
			int ball = 0;
			if (stateToConsider.robot.isBallStored)
				ball = 1;
			stateToConsider.obtainedPoints += (2*ball+3)*stateToConsider.robot.storedPlotCount;
			stateToConsider.robot.storedPlotCount = 0;
			stateToConsider.robot.isBallStored = false;

			
			//on sort notre membre
			stateToConsider.robot.moveLengthwise(-200, hooksToConsider, false);
			
			//mais on se referme un peu
			stateToConsider.robot.useActuator(ActuatorOrder.MID_LEFT_GUIDE, false);
			stateToConsider.robot.useActuator(ActuatorOrder.MID_RIGHT_GUIDE, true);
			
			//puis beaucoup
			//Yamete Kudasai !
			stateToConsider.robot.useActuator(ActuatorOrder.CLOSE_LEFT_GUIDE, false);
			stateToConsider.robot.useActuator(ActuatorOrder.CLOSE_RIGHT_GUIDE, true);
			stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_CLOSE_JAW, true);
			
				
			//on remet notre membre en position de deplacement
			stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_LOW, true);
			
			// THank... you, sempai #'.'#
		}
		else if (version==2)
		{
			stateToConsider.robot.moveLengthwise(150, hooksToConsider, false);
			
			stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_GROUND, true);
			//on ouvre le guide un peu
			stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_OPEN_JAW, true);
			
			stateToConsider.robot.useActuator(ActuatorOrder.MID_LEFT_GUIDE, true);
			stateToConsider.robot.useActuator(ActuatorOrder.MID_RIGHT_GUIDE, true);
			
			//puis beaucoup
			
			stateToConsider.robot.useActuator(ActuatorOrder.OPEN_RIGHT_GUIDE, true);
			stateToConsider.robot.useActuator(ActuatorOrder.OPEN_LEFT_GUIDE, true);
			//on se vide de nos plots et on met a jour les points
			int ball = 0;
			if (stateToConsider.robot.isBallStored)
				ball = 1;
			stateToConsider.obtainedPoints += (2*ball+3)*stateToConsider.robot.storedPlotCount;
			stateToConsider.robot.storedPlotCount = 0;
			stateToConsider.robot.isBallStored = false;
			
			stateToConsider.robot.moveLengthwise(-150, hooksToConsider, false);
			
			//On referme doucement, un peu
			stateToConsider.robot.useActuator(ActuatorOrder.MID_LEFT_GUIDE, false);
			stateToConsider.robot.useActuator(ActuatorOrder.MID_RIGHT_GUIDE, true);
			
			//Puis on finit
			stateToConsider.robot.useActuator(ActuatorOrder.CLOSE_LEFT_GUIDE, false);
			stateToConsider.robot.useActuator(ActuatorOrder.CLOSE_RIGHT_GUIDE, true);
			stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_CLOSE_JAW, true);
				
			//on remet l'ascenceur en position de deplacement
			stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_LOW, true);
			}
		else
		{
			// TODO: version ?
		}
	}
	
	
	@Override
	public Circle entryPosition(int id,int robotRay) 
	{
		if (id==1)
		{
			return new Circle(500,325,0);
		}
		else if (id==2)
		{
			return new Circle(881,1000,0); // endroit de depart -50 cm en x
		}
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
			if (version == 2 && !stateToConsider.table.isBallTaken())
				toReturn -= 5*Math.min((int)(90000-stateToConsider.timeEllapsed)/AverageTimeToGetPlot,
											stateToConsider.table.numberOfPlotLeft());
			return toReturn;
	}

	@Override
	protected void finalise(GameState<?> stateToConsider) throws SerialFinallyException, UnableToMoveException 
	{
		try 
		{
			stateToConsider.robot.useActuator(ActuatorOrder.CLOSE_RIGHT_GUIDE, false);
			stateToConsider.robot.useActuator(ActuatorOrder.CLOSE_LEFT_GUIDE, true);	
			stateToConsider.robot.moveLengthwise(-20);
			stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_LOW, true);
		} 
		catch (SerialConnexionException e) 
		{
			throw new SerialFinallyException ();
		}
	}

	public int[] getVersion(GameState<?> stateToConsider)
	{
		//TODO
		return versions;
	}

}

