package scripts;

import java.util.ArrayList;

import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialConnexionException;
import hook.Hook;
import hook.types.HookFactory;
import robot.Robot;
import smartMath.Circle;
import strategie.GameState;
import utils.Config;
import utils.Log;

public class GetGlass extends AbstractScript 
{

	public GetGlass(HookFactory hookFactory, Config config, Log log) 
	{
		super(hookFactory, config, log);
		versions = new int[]{0,1, 2, 3 ,4 }; 
		
		/* Table : numero des verres
		 * 	___________________________________
		 * 	|			Escalier			  |
		 * 	|		3				1		  |
		 * 	|ennemi						depart|
		 * 	|			  2					  |
		 * 	|4			Claps				0 |
		 *   ---------------------------------
		 *   
		 *   Bref : plus pret = 0; plus loin = 4
		 */
	}
	
	@Override
	public void execute(int versionToExecute, GameState<Robot> stateToConsider, ArrayList<Hook> hooksToConsider, boolean shouldRetryIfBlocked) throws UnableToMoveException, SerialConnexionException
	{
		//TODO le script en lui meme
		//on se tourne vers le goblet
		//on choisit le bras disponible (ici on montre avec le bras gauche)
		//si aucun bras disponible (logiquement l'IA ne devrai pas lancer le script (erreur ?)) on arrete le script
		//on avance en ouvrant le bras gauche (respectivement droit)
		//on se place proche du goblet pour le ramasser
		//on ferme lentement le bras gauche (respectivement droit) pour attraper le goblet
		//on demande si on a bien quelque chose a gauche (respectivement a droite)
		//si on a rien (et que l'autre bras n'est pas occupe) on recule, on ouvre l'autre bras (droit , repectivement gauche), on avance et on ferme le bras droit (respectivement gauche)
		//si on a toujours rien on arrete		
		//si on a attrape quelque chose on le dit au robot ainsi que sa position (gauche / droite)
		
		if (versionToExecute == 0)
		{
			if(stateToConsider.table.isPlotXEaten(0))
				takeGlass0(stateToConsider, hooksToConsider, shouldRetryIfBlocked);
		}
		else if (versionToExecute == 1)
		{
			if(stateToConsider.table.isPlotXEaten(1))
				takeGlass1(stateToConsider, hooksToConsider, shouldRetryIfBlocked);
		}
		else if (versionToExecute == 2)
		{
			if(stateToConsider.table.isPlotXEaten(2))
				takeGlass2(stateToConsider, hooksToConsider, shouldRetryIfBlocked);
		}
		else if (versionToExecute == 3)
		{
			if(stateToConsider.table.isPlotXEaten(3))
				takeGlass3(stateToConsider, hooksToConsider, shouldRetryIfBlocked);
		}
		else if (versionToExecute == 4)
		{
			if(stateToConsider.table.isPlotXEaten(4))
				takeGlass4(stateToConsider, hooksToConsider, shouldRetryIfBlocked);
		}
		else
			log.debug("Souci de version avec les Verres", this);	//TODO: lancer une exception de version inconnue (la créer si besoin)
	}
	
	public void takeGlass0 (GameState<Robot> stateToConsider,  ArrayList<Hook> hooksToConsider, boolean shouldRetryIfBlocked) throws UnableToMoveException, SerialConnexionException
	{
		
	}
	
	public void takeGlass1 (GameState<Robot> stateToConsider,  ArrayList<Hook> hooksToConsider, boolean shouldRetryIfBlocked) throws UnableToMoveException, SerialConnexionException
	{
		
	}
	
	public void takeGlass2 (GameState<Robot> stateToConsider,  ArrayList<Hook> hooksToConsider, boolean shouldRetryIfBlocked) throws UnableToMoveException, SerialConnexionException
	{
		
	}
	
	public void takeGlass3 (GameState<Robot> stateToConsider,  ArrayList<Hook> hooksToConsider, boolean shouldRetryIfBlocked) throws UnableToMoveException, SerialConnexionException
	{
		
	}
	
	public void takeGlass4 (GameState<Robot> stateToConsider,  ArrayList<Hook> hooksToConsider, boolean shouldRetryIfBlocked) throws UnableToMoveException, SerialConnexionException
	{
		
	}

	@Override
	public Circle entryPosition(int id)
	{
		if (id==0)
			return new Circle (-1250,250,300);
		else if (id==1)
			return new Circle (-590,1170,300);
		else if (id==2)
			return new Circle (0,350,300);
		else if (id==3)
			return new Circle (590,1170,300);
		else if (id==4)
			return new Circle (1250,250,300);
		else 
			log.debug("Probleme de numero de script de Verre", this);
			return new Circle (0,0);
	}

	@Override
	public int remainingScoreOfVersion(int id_version, GameState<?> state) 
	{
		//TODO si le robot est pein a droite et a gauche (on attends les capteurs de sylvain)
		//if (robot.fullRight) 
		//{
		//	if (robot.fullLeft)
		//	{
		//		return 0;
		//	}
		//}
		return 4;
	}

	@Override
	protected void finalise(GameState<?> state) 
	{
		// TODO fermer (ouvrir ?) le bras gauche et droit 
	}
	
	private void eatGlass (boolean isSecondTry , boolean isArmChosenLeft)
	{
		
	}

}
