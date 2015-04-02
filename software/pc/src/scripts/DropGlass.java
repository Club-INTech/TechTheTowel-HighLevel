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
 * @author Théo
 *
 *Script pour deposer notre gobelet :
 *1 seul gobelet par zone, avec 3 zones 
 *
 *  ____________________________
 * 	|							|
 * 	|1							|
 *	|----					----|
 * 	|Debut ennemi			   0|Debut de NOTRE robot
 * 	|----					----|
 * 	|2							|
 *	|___________________________|
 *
 */
public class DropGlass extends AbstractScript
{
	

	private static final int AverageTimeToGetPlot = 5000;

	public DropGlass(HookFactory hookFactory, Config config, Log log) 
	{
		super(hookFactory, config, log);
		versions = new int[]{0, 1, 2}; // liste des versions
	}

	@Override
	public void execute(int version, GameState<Robot> stateToConsider,ArrayList<Hook> hooksToConsider,boolean shouldRetryIfBlocke) throws UnableToMoveException, SerialConnexionException
	{
		boolean isThereGlassLeft = true; //TODO capteurs

		if (version==0)
		{
			stateToConsider.robot.turn(0);//On se tourne dans le bon sens
			
			//On avance
			stateToConsider.robot.moveLengthwise(350, hooksToConsider, true);
			
			if(isThereGlassLeft)
			{
				stateToConsider.robot.useActuator(ActuatorOrder.ARM_LEFT_OPEN, true);
			}
			else 
				stateToConsider.robot.useActuator(ActuatorOrder.ARM_RIGHT_OPEN, true);
			
			//On recule en laissant notre gobelet
			stateToConsider.robot.moveLengthwise(-50, hooksToConsider, true);//TODO doucement pour eviter de faire tomber le gobelet (en envoyant 350, le gobelet vacille donc bof niveau fiabilité..											 sinon vive les commentaires de 2m de long ! Et oui c'est voulu, surtout ssi tu t'es fais chmir à tout lire <3
			stateToConsider.robot.moveLengthwise(-300, hooksToConsider, true);
			
			//On met à jour la table 
			//TODO mettre à jour quel verre est posé ( stateToConsider.table.glassXDropped(x) )
			stateToConsider.table.areaXFilled(1);
			
			//On referme les 2 bras
			stateToConsider.robot.useActuator(ActuatorOrder.ARM_LEFT_CLOSE, true);
			stateToConsider.robot.useActuator(ActuatorOrder.ARM_RIGHT_CLOSE, true);
			
			stateToConsider.robot.turn(Math.PI);//On se tourne dans le bon sens
		}
		else if (version==1)
		{
			// 950 -> 1200 en x pour eviter les PatNotFoundEXeception
			stateToConsider.robot.turn(Math.PI);
			stateToConsider.robot.moveLengthwise(250);
			
			stateToConsider.robot.turn((5/4)*Math.PI); // On se tourne aux 3/4 afin de pouvoir mettre l'un ou l'autre des verres

			isThereGlassLeft=false;//TODO histoire de changer mais lees capteurs feront le boulot
			if(isThereGlassLeft)
			{
				stateToConsider.robot.useActuator(ActuatorOrder.ARM_LEFT_OPEN, true);
			}
			else 
				stateToConsider.robot.useActuator(ActuatorOrder.ARM_RIGHT_OPEN, true);
			
			//On recule en laissant notre gobelet
			stateToConsider.robot.moveLengthwise(-50, hooksToConsider, true);//TODO doucement pour eviter de faire tomber le gobelet
			stateToConsider.robot.moveLengthwise(-300, hooksToConsider, true);	
			
			//On met à jour la table 
			//TODO mettre à jour quel verre est posé ( stateToConsider.table.glassXDropped(x) )
			stateToConsider.table.areaXFilled(2);
			
			//On referme les 2 bras
			stateToConsider.robot.useActuator(ActuatorOrder.ARM_LEFT_CLOSE, true);
			stateToConsider.robot.useActuator(ActuatorOrder.ARM_RIGHT_CLOSE, true);
		}
		else if (version==2)
		{
			//-900 -> -1200 pour eviter la PathNotFound Exception
			stateToConsider.robot.turn(Math.PI);
			stateToConsider.robot.moveLengthwise(300, hooksToConsider, true);
			
			
			stateToConsider.robot.turn(Math.PI*3/4); // On se tourne aux 3/4 afin de pouvoir mettre l'un ou l'autre des verres
			isThereGlassLeft=false;//histoire de changer mais lees capteurs feront le boulot

			if(isThereGlassLeft)
			{
				stateToConsider.robot.useActuator(ActuatorOrder.ARM_LEFT_OPEN, true);
			}
			else 
				stateToConsider.robot.useActuator(ActuatorOrder.ARM_RIGHT_OPEN, true);
			
			//On recule en laissant notre gobelet
			stateToConsider.robot.moveLengthwise(-50, hooksToConsider, true);//TODO doucement pour eviter de faire tomber le gobelet
			stateToConsider.robot.moveLengthwise(-300, hooksToConsider, true);			
			
			//On met à jour la table 
			//TODO mettre à jour quel verre est posé ( stateToConsider.table.glassXDropped(x) )
			stateToConsider.table.areaXFilled(3);
			
			//On referme les 2 bras
			stateToConsider.robot.useActuator(ActuatorOrder.ARM_LEFT_CLOSE, true);
			stateToConsider.robot.useActuator(ActuatorOrder.ARM_RIGHT_CLOSE, true);
		}
	}
	
	@Override
	public Circle entryPosition(int id, int ray) 
	{
		if (id==0)
		{
			return new Circle(881,1000,0); // endroit de depart -50 cm en x
		}
		else if (id==1)
		{
			return new Circle(-950,1500,0); //milieu zone ennemi haute
		}
		else if (id==2)
		{
			return new Circle(-900,500,0); //milieu zone haute chez l'ennemi
		}
		else
		{
			log.debug("erreur version dans DropGlass", this);
			return new Circle(0,1000);
		}
	}

	@Override
	public int remainingScoreOfVersion(int version, GameState<?> stateToConsider)
	{
		int toReturn=4;
		//si la zone a remplir n'est pas deja remplie
		if (!stateToConsider.table.isAreaXFilled(version))
			//si on a un gobelet stocke
			if (stateToConsider.robot.isGlassStoredLeft || stateToConsider.robot.isGlassStoredRight)
			{
				//si on gene la future prise de balle on retire des points
				if(stateToConsider.table.isBallTaken() && version == 0)
					toReturn -= 5*Math.min((int)(90000-stateToConsider.timeEllapsed)/AverageTimeToGetPlot,
											stateToConsider.table.numberOfPlotLeft());
				return toReturn;
			}
		return 0;
	}

	@Override
	protected void finalise(GameState<?> stateToConsider) throws SerialFinallyException, UnableToMoveException 
	{
		try 
		{
			stateToConsider.robot.useActuator(ActuatorOrder.ARM_LEFT_CLOSE, true);
		}
		catch (SerialConnexionException e1) 
		{
			e1.printStackTrace();
		}
		try 
		{
			stateToConsider.robot.useActuator(ActuatorOrder.ARM_RIGHT_CLOSE, true);
		} 
		catch (SerialConnexionException e) 
		{
			e.printStackTrace();
		}
	}

	public int[] getVersion(GameState<?> stateToConsider)
	{
		ArrayList<Integer> versionList = new ArrayList<Integer>();
		versionList.add(0);
		versionList.add(1);
		versionList.add(2);
		for (int i = 0; i<3; i++)
			if (stateToConsider.table.isAreaXFilled(i))
				versionList.remove((Integer) i);
			
			
		//on convertit l'arrayList en int[]	
		int[] retour = new int[versionList.size()];
	    for (int i=0; i < retour.length; i++)
	    {
	    	retour[i] = versionList.get(i).intValue();
	    }
		return retour;
	}

}

