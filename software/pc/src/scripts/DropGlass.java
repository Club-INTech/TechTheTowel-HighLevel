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
 * 	|2							|
 *	|----					----|
 * 	|Debut ennemi			   1|Debut de NOTRE robot
 * 	|----					----|
 * 	|3							|
 *	|___________________________|
 *
 */
public class DropGlass extends AbstractScript
{
	

	public DropGlass(HookFactory hookFactory, Config config, Log log) 
	{
		super(hookFactory, config, log);
		versions = new int[]{1, 2, 3}; // liste des versions
	}

	@Override
	public void execute(int version, GameState<Robot> stateToConsider,ArrayList<Hook> hooksToConsider,boolean shouldRetryIfBlocke) throws UnableToMoveException, SerialConnexionException
	{
		boolean isThereGlassLeft = true; //TODO capteurs

		if (version==1)
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
		}
		else if (version==2)
		{
			stateToConsider.robot.turn((5/4)*Math.PI); // On se tourne aux 3/4 afin de pouvoir mettre l'un ou l'autre des verres

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
			stateToConsider.table.areaXFilled(2);
			
			//On referme les 2 bras
			stateToConsider.robot.useActuator(ActuatorOrder.ARM_LEFT_CLOSE, true);
			stateToConsider.robot.useActuator(ActuatorOrder.ARM_RIGHT_CLOSE, true);
		}
		else
		{
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
	public Circle entryPosition(int id) 
	{
		if (id==1)
		{
			return new Circle(881,1000,0); // endroit de depart -50 cm en x
		}
		else if (id==2)
		{
			return new Circle(-1200,1500,0); //milieu zone ennemi haute
		}
		else if (id==3)
		{
			return new Circle(-1200,500,0); //milieu zone haute chez l'ennemi
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
		return 0;//TODO calculer score : normalement, 4 par version .
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
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try 
		{
			stateToConsider.robot.useActuator(ActuatorOrder.ARM_RIGHT_CLOSE, true);
		} 
		catch (SerialConnexionException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

