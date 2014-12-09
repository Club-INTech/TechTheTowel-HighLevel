package scripts;

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
		
		//TODO si on en a 3 au depart il ne faut pas ramasser le deuxieme plot mais il faut pouvoir recommencer le script a partir du deuxieme (point de depart different)
		//version proche des escaliers
		//se placer dans le bon sens 
		//manger premier plot (bras gauche) (on essaiera quand meme avec l'autre bras au cas où ?)
		//si on a ramasse qqc on incrément le nb de plots
		//si compteur < 4 on fait monter ?
		//avancer !ne pas reculer sinon on peut perdre un verre
		//manger deuxieme plot (bras gauche) (on essaiera quand meme avec l'autre bras au cas où ?)
		//si on a ramasse qqc on incrément le nb de plots
		//si compteur < 4 on fait monter ?
		
		//TODO si on en a 3 au depart il ne faut pas ramasser le deuxieme plot mais il faut pouvoir recommencer le script a partir du deuxieme (point de depart different)
		//version proche de la zone de depart
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
		//ouvrir les deux bras
		//fermer les machoires
		//baisser l'elevateur ?
	}
	/**
	 * mangeage de plots, essaie a nouveau si il est impossible de manger 
	 * ne se deplace pas
	 * 
	 * @param isSecondTry vrai si l'essai de mangeage de plot est le deuxieme
	 * @param isArmChosenLeft vrai si on mange avec le bras droit
	 * @param locomotion le deplacement haut niveau
	 * @param move les actionneurs
	 * @throws SerialException
	 */
	private void EatPlot (boolean isSecondTry, boolean isArmChosenLeft, LocomotionHiLevel locomotion, ActuatorsManager move) throws SerialException
	{
		move.openJaw();
		if (isArmChosenLeft) 
		{
			move.closeLowLeftArm();
			move.openLeftArm();
		}
		else
		{
			move.closeLowRightArm();
			move.openRightArm();
		}
		move.closeJaw();
		//si on a attrape qqc on termine sinon on essaie avec l'autre bras (si isSecondTry == false)
		//si deuxieme essai ecrire dans le log qu'on a essaye de manger un plot et on jette une exeption impossible de manger
	}

}
