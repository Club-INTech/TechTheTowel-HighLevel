package scripts;

import java.util.ArrayList;

import enums.ActuatorOrder;
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
		
		
		stateToConsider.robot.useActuator(ActuatorOrder.ARM_LEFT_CLOSE, true);
		stateToConsider.robot.useActuator(ActuatorOrder.ARM_RIGHT_CLOSE, true);
		
		//gestion des version, si le verre est deja pris on ne le re-prend pas (bawi)
		if (versionToExecute == 0)
		{
			if(!stateToConsider.table.isGlassXTaken(0))
				takeGlass0(stateToConsider, hooksToConsider, shouldRetryIfBlocked);
		}
		else if (versionToExecute == 1)
		{
			if(!stateToConsider.table.isGlassXTaken(1))
				takeGlass1(stateToConsider, hooksToConsider, shouldRetryIfBlocked);
		}
		else if (versionToExecute == 2)
		{
			if(!stateToConsider.table.isGlassXTaken(2))
				takeGlass2(stateToConsider, hooksToConsider, shouldRetryIfBlocked);
		}
		else if (versionToExecute == 3)
		{
			if(!stateToConsider.table.isGlassXTaken(3))
				takeGlass3(stateToConsider, hooksToConsider, shouldRetryIfBlocked);
		}
		else if (versionToExecute == 4)
		{
			if(!stateToConsider.table.isGlassXTaken(4))
				takeGlass4(stateToConsider, hooksToConsider, shouldRetryIfBlocked);
		}
		else
			log.debug("Souci de version avec les Verres", this);	//TODO: lancer une exception de version inconnue (la créer si besoin)
	}
	
	public void takeGlass0 (GameState<Robot> stateToConsider,  ArrayList<Hook> hooksToConsider, boolean shouldRetryIfBlocked) throws UnableToMoveException, SerialConnexionException
	{
		takeGlass(stateToConsider,hooksToConsider,false,false);
		stateToConsider.table.glassXTaken(0);
	}
	
	public void takeGlass1 (GameState<Robot> stateToConsider,  ArrayList<Hook> hooksToConsider, boolean shouldRetryIfBlocked) throws UnableToMoveException, SerialConnexionException
	{
		takeGlass(stateToConsider,hooksToConsider,false,false);
		stateToConsider.table.glassXTaken(1);
	}
	
	public void takeGlass2 (GameState<Robot> stateToConsider,  ArrayList<Hook> hooksToConsider, boolean shouldRetryIfBlocked) throws UnableToMoveException, SerialConnexionException
	{
		takeGlass(stateToConsider,hooksToConsider,false,true);
		stateToConsider.table.glassXTaken(2);
	}
	
	public void takeGlass3 (GameState<Robot> stateToConsider,  ArrayList<Hook> hooksToConsider, boolean shouldRetryIfBlocked) throws UnableToMoveException, SerialConnexionException
	{
		takeGlass(stateToConsider,hooksToConsider, false,true);
		stateToConsider.table.glassXTaken(3);
	}
	
	public void takeGlass4 (GameState<Robot> stateToConsider,  ArrayList<Hook> hooksToConsider, boolean shouldRetryIfBlocked) throws UnableToMoveException, SerialConnexionException
	{
		takeGlass(stateToConsider,hooksToConsider, false,false);
		stateToConsider.table.glassXTaken(4);
	}

	//positions des plots = positions d'entrée des scrpits, symetrie gerée plus haut
	@Override
	public Circle entryPosition(int id)
	{
		if (id==0)
			return new Circle (1355,250,310);
		else if (id==1)
			return new Circle (590,1170,310);
		else if (id==2)
			return new Circle (0,350,310);
		else if (id==3)
			return new Circle (-590,1170,310);
		else if (id==4)
			return new Circle (-1355,250,310);
		else 
		{
			log.debug("Probleme de numero de script de Verre", this);
			return new Circle (0,0);
		}
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
		return 0;
	}

	@Override
	protected void finalise(GameState<?> state) 
	{
		// TODO fermer (ouvrir ?) le bras gauche et droit 
	}
	
	//attrape le plot (ouvre / avance / ferme )
	private void takeGlass (GameState<Robot> stateToConsider, ArrayList<Hook> hooksToConsider, boolean isSecondTry , boolean isArmChosenLeft) throws SerialConnexionException, UnableToMoveException
	{	
		//On change le bras choisi suivant la symetrie : à voir si l'IA s'en occupera, mais pour les tests ca reste là
		if(stateToConsider.robot.getSymmetry())
		{
			isArmChosenLeft=!isArmChosenLeft;
		}
		//On ouvre le bras
		if(isArmChosenLeft)
		{
			stateToConsider.robot.useActuator(ActuatorOrder.ARM_LEFT_OPEN, true);
		}
		else 
			stateToConsider.robot.useActuator(ActuatorOrder.ARM_RIGHT_OPEN, true);
		
		//On avance vers le plot
		stateToConsider.robot.moveLengthwise(80,hooksToConsider);

		//On prend le verre
		if(isArmChosenLeft)
		{
			stateToConsider.robot.useActuator(ActuatorOrder.ARM_LEFT_CLOSE_SLOW, true);
		}
		else 
			stateToConsider.robot.useActuator(ActuatorOrder.ARM_RIGHT_CLOSE_SLOW, true);
		
		stateToConsider.robot.sleep(200);
		
		//on verifie
		//si non, et si second essai on retente
	}

}
