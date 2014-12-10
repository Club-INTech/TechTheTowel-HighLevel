package scripts;

import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialException;
import hook.types.HookGenerator;
import pathdinding.Pathfinding;
import robot.Robot;
import robot.cards.ActuatorsManager;
import robot.highlevel.LocomotionHiLevel;
import smartMath.Vec2;
import strategie.GameState;
import table.Table;
import utils.Config;
import utils.Log;

public class GetPlot extends Script {

	private int timeLowClose=1000; //TODO calculer les valeurs
	private int timeCloseJaw=1000;
	private int TimeOpenArm=800;
	private int timeOpenJaw=1000;
	private int distanceEntrePlots;
	
	
	public GetPlot(HookGenerator hookgenerator, Config config, Log log,	Pathfinding pathfinding, Robot robot, ActuatorsManager move,Table table) 
	{
		super(hookgenerator, config, log, pathfinding, robot, move, table);
		// TODO le tableau des versions
	}
	
	public void execute (int id_version) 
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
			robot.tourner(Math.PI*0.5);
			eatPlot(true, false);
			//si on a ramasse qqc on incrément le nb de plots
			//si compteur < 4 on fait monter ?
			robot.avancer(distanceEntrePlots);
			eatPlot(true, false);
			//si on a ramasse qqc on incrément le nb de plots
			//si compteur < 4 on fait monter ?
		} 
		catch (UnableToMoveException e) 
		{
			log.debug("bloque", this);
			e.printStackTrace();
		} catch (SerialException e) {
			log.debug("mauvaise entree serie", this);
			e.printStackTrace();
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
	public Vec2 point_entree(int id) 
	{
		//le cercle autour des plots
		//calcul des poins d'entree sur les deux versions non-circulaires
		return null;
	}

	@Override
	public int score(int id_version, GameState<?> state) 
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
	protected void termine(GameState<?> state) 
	{
		try 
		{
			actionneurs.closeLeftArm();
			actionneurs.closeRightArm();
			actionneurs.closeJaw();
			actionneurs.groundElevator();
		} 
		catch (SerialException e) 
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
	 * @param move les actionneurs
	 * @throws SerialException
	 */
	private void eatPlot (boolean isSecondTry, boolean isArmChosenLeft) throws SerialException
	{
		actionneurs.openJaw();
		robot.sleep(timeOpenJaw);
		if (isArmChosenLeft) 
		{
			actionneurs.openLowLeftArm();
			robot.sleep(timeLowClose);
			actionneurs.closeLeftArm();
		}
		else
		{
			actionneurs.openLowRightArm();
			robot.sleep(timeLowClose);
			actionneurs.closeRightArm();
		}
		actionneurs.closeJaw();
		robot.sleep(Math.max(timeCloseJaw,TimeOpenArm));
		//TODO le capteur de sylvain
		if (true/*"on a rien attrape"*/)	
			if (isSecondTry)
			{
				log.debug("impossible d'attraper le plot", this);
				//TODO jetter une exeption ?
			}
			else
			{
				eatPlot(true,!isArmChosenLeft);
			}
	}

}
