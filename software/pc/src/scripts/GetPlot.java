package scripts;

import java.util.ArrayList;

import enums.ActuatorOrder;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialConnexionException;
import hook.Hook;
import hook.types.HookFactory;
import robot.Robot;
import smartMath.Vec2;
import strategie.GameState;
import utils.Config;
import utils.Log;

public class GetPlot extends AbstractScript
{
	//TODO: doc	
	private int timeLowClose=1000; //TODO calculer les valeurs
	private int timeCloseJaw=1000;
	private int TimeOpenArm=800;
	private int timeOpenJaw=1000;
	private int distanceEntrePlots;
	

	public GetPlot(HookFactory hookFactory, Config config, Log log) 
	{
		super(hookFactory, config, log);
		
		//TODO: tableau des versions
	}
	
	@Override
	public void execute(int versionToExecute, GameState<Robot> stateToConsider, ArrayList<Hook> hooksToConsider, boolean shouldRetryIfBlocked) throws UnableToMoveException, SerialConnexionException
	{
		//TODO le script en lui meme
		
		//version au centre de la table (version circulaire)
		//choisir le bras le plus adapte (assez dificile)
		//se placer en face
		//manger le plot
		//si on a ramasse qqc on incrément le nb de plots
		//si compteur < 4 on fait monter ?
		
		try 
		{
			//TODO si on en a 3 au depart il ne faut pas ramasser le deuxieme plot mais il faut pouvoir recommencer le script a partir du deuxieme (point de depart different)
			//version proche des escaliers
			stateToConsider.robot.turn(Math.PI*0.5, hooksToConsider, false);
			eatPlot(true, false, stateToConsider);
			//si on a ramasse qqc on incrément le nb de plots
			//si compteur < 4 on fait monter ?
			stateToConsider.robot.moveLengthwise(distanceEntrePlots, hooksToConsider, false);
			eatPlot(true, false, stateToConsider);
			//si on a ramasse qqc on incrément le nb de plots
			//si compteur < 4 on fait monter ?
		} 
		catch (UnableToMoveException e) 
		{
			log.debug("bloque", this);
			e.printStackTrace();	// TODO: remonter cette exception
		}
		catch (SerialConnexionException e)
		{
			log.debug("mauvaise entree serie", this);
			e.printStackTrace(); // TODO: remonter cette exception
		}
		
		//TODO la version du tout seul et pas en cercle
		
		//TODO si on en a 3 au depart il ne faut pas ramasser le deuxieme plot mais il faut pouvoir recommencer le script a partir du deuxieme (point de depart different)
		//version proche de la zone de depart TODO ne pas oublier le goblet
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
	public Vec2 entryPosition(int id)
	{
		//le cercle autour des plots
		//calcul des poins d'entree sur les deux versions non-circulaires
		return null;
	}

	@Override
	public int remainingScoreOfVersion(int id_version, GameState<?> state) 
	{
		//version circulaire
		//si la pile est pas de 4 plots
		//et si on a precharge une balle
		return 5;
		
		//autres versions
		//si balle prechargee
		//return max (nb de places libres dans la pile, nb de plots a prendre) * 5
	}

	@Override
	protected void finalise(GameState<?> stateToConsider) 
	{
		try 
		{
			stateToConsider.robot.useActuator(ActuatorOrder.ARM_LEFT_CLOSE, true);
			stateToConsider.robot.useActuator(ActuatorOrder.ARM_RIGHT_CLOSE, true);
			stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_CLOSE_JAW, false);
			stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_GROUND, true);
		} 
		catch (SerialConnexionException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * mangeage de plots, essaie a nouveau si il est impossible de manger 
	 * ne se deplace pas
	 * 
	 * @param isSecondTry vrai si l'essai de mangeage de plot est le deuxieme ou si on ne veux pas reessayer
	 * @param isArmChosenLeft vrai si on mange avec le bras droit
	 * 
	 * @throws SerialException
	 */
	private void eatPlot (boolean isSecondTry, boolean isArmChosenLeft, GameState<Robot> stateToConsider) throws SerialConnexionException
	{
		stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_OPEN_JAW, false);
		if (isArmChosenLeft) 
		{
			stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_OPEN_JAW, true);
			stateToConsider.robot.useActuator(ActuatorOrder.ARM_LEFT_CLOSE_SLOW, true);
			stateToConsider.robot.useActuator(ActuatorOrder.ARM_LEFT_OPEN, true);
		}
		else
		{
			stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_OPEN_JAW, true);
			stateToConsider.robot.useActuator(ActuatorOrder.ARM_RIGHT_CLOSE_SLOW, true);
			stateToConsider.robot.useActuator(ActuatorOrder.ARM_RIGHT_OPEN, true);
		}
		//si on a attrape qqc on termine sinon on essaie avec l'autre bras (si isSecondTry == false)
		//si deuxieme essai ecrire dans le log qu'on a essaye de manger un plot et on jette une exeption impossible de manger
		stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_CLOSE_JAW, false);
		stateToConsider.robot.sleep(Math.max(timeCloseJaw,TimeOpenArm));
		
		//TODO le capteur de sylvain
		if (true/*"on a rien attrape"*/)	
			if (isSecondTry)
			{
				log.debug("impossible d'attraper le plot", this);	
				//TODO jetter une exeption 
			}
			else
			{
				eatPlot(true,!isArmChosenLeft, stateToConsider);
			}
	}

}
