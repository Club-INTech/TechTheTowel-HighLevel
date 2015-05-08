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

public class GetGlass extends AbstractScript 
{

	private static final int timeToDropGlass = 3000;

	public GetGlass(HookFactory hookFactory, Config config, Log log) 
	{
		super(hookFactory, config, log);
		versions = new Integer[]{0,1, 2, 3 ,4 }; 
		
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
	public void goToThenExec(int versionToExecute,GameState<Robot> actualState, ArrayList<Hook> hooksToConsider) throws UnableToMoveException, SerialConnexionException, PathNotFoundException, SerialFinallyException, InObstacleException
	{
		EnumSet<ObstacleGroups> obstacleNotConsidered = EnumSet.noneOf(ObstacleGroups.class);
		
		if (versionToExecute == 0)
			obstacleNotConsidered.add(ObstacleGroups.GOBLET_0);
		else if (versionToExecute == 1)
			obstacleNotConsidered.add(ObstacleGroups.GOBLET_1);
		else if (versionToExecute == 2)
			obstacleNotConsidered.add(ObstacleGroups.GOBLET_2);
		else if (versionToExecute == 3)
			obstacleNotConsidered.add(ObstacleGroups.GOBLET_3);
		else if (versionToExecute == 4)
			obstacleNotConsidered.add(ObstacleGroups.GOBLET_4);
		else
		{
			log.debug("version de Script inconnue de GetGlass :"+versionToExecute, this);
			return;
		}
		

		// va jusqu'au point d'entrée de la version demandée
		actualState.robot.moveToCircle(entryPosition(versionToExecute,actualState.robot.robotRay, actualState.robot.getPosition()), hooksToConsider, actualState.table,obstacleNotConsidered);
		
		// exécute la version demandée
			execute(versionToExecute, actualState, hooksToConsider);
}
	
	@Override
	public void execute(int versionToExecute, GameState<Robot> stateToConsider, ArrayList<Hook> hooksToConsider) throws UnableToMoveException, SerialConnexionException, SerialFinallyException
	{
		try 
		{
			
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
			
			//gestion des version, si le verre est deja pris on ne le re-prend pas (bawi)
			if (versionToExecute == 0)
			{
				if(!stateToConsider.table.isGlassXTaken(0))
					takeGlass0(stateToConsider, hooksToConsider);
			}
			else if (versionToExecute == 1)
			{
				if(!stateToConsider.table.isGlassXTaken(1))
					takeGlass1(stateToConsider, hooksToConsider);
			}
			else if (versionToExecute == 2)
			{
				if(!stateToConsider.table.isGlassXTaken(2))
					takeGlass2(stateToConsider, hooksToConsider);
			}
			else if (versionToExecute == 3)
			{
				if(!stateToConsider.table.isGlassXTaken(3))
					takeGlass3(stateToConsider, hooksToConsider);
			}
			else if (versionToExecute == 4)
			{
				if(!stateToConsider.table.isGlassXTaken(4))
					takeGlass4(stateToConsider, hooksToConsider);
			}
			else
				log.debug("Souci de version avec les Verres", this);	//TODO: lancer une exception de version inconnue (la créer si besoin)
			
		}
		catch (UnableToMoveException | SerialConnexionException e) 
		{
			finalize(stateToConsider);
			throw e;
		}
	}
	
	public void takeGlass0 (GameState<Robot> stateToConsider,  ArrayList<Hook> hooksToConsider) throws UnableToMoveException, SerialConnexionException
	{
		
		takeGlass(stateToConsider,hooksToConsider,false,false);
		stateToConsider.table.removeGlassX(0);
	}
	
	public void takeGlass1 (GameState<Robot> stateToConsider,  ArrayList<Hook> hooksToConsider) throws UnableToMoveException, SerialConnexionException
	{
		takeGlass(stateToConsider,hooksToConsider,false,stateToConsider.robot.isGlassStoredRight);
		stateToConsider.table.removeGlassX(1);
	}
	
	public void takeGlass2 (GameState<Robot> stateToConsider,  ArrayList<Hook> hooksToConsider) throws UnableToMoveException, SerialConnexionException
	{
		stateToConsider.robot.turn(-Math.PI/2);//POur eviter le pathnotfound exception
		stateToConsider.robot.moveLengthwise(stateToConsider.robot.robotRay);
		takeGlass(stateToConsider,hooksToConsider,false,stateToConsider.robot.isGlassStoredRight);
		stateToConsider.table.removeGlassX(2);
	}
	
	public void takeGlass3 (GameState<Robot> stateToConsider,  ArrayList<Hook> hooksToConsider) throws UnableToMoveException, SerialConnexionException
	{
		takeGlass(stateToConsider,hooksToConsider, false,stateToConsider.robot.isGlassStoredRight);
		stateToConsider.table.removeGlassX(3);
	}
	
	public void takeGlass4 (GameState<Robot> stateToConsider,  ArrayList<Hook> hooksToConsider) throws UnableToMoveException, SerialConnexionException
	{
		takeGlass(stateToConsider,hooksToConsider, false,false);
		stateToConsider.table.removeGlassX(4);
	}

	//positions des plots = positions d'entrée des scrpits, symetrie gerée plus haut
	@Override
	public Circle entryPosition(int id, int ray, Vec2 robotPosition)
	{
		if (id==0)
			return new Circle (1355,250,310);
		else if (id==1)
			return new Circle (590,1170,310);
		else if (id==2)
			return new Circle (0,350+ray,310);//POur eviter le pathnotfound exception
		else if (id==3)
			return new Circle (-590,1170,310);
		else if (id==4)
			return new Circle (-1355,250,310);// Dangereux :(-1045,250) passe mais le reste difficilement.
		else 
		{
			log.debug("Probleme de numero de script de Verre", this);
			return new Circle (0,0);
		}
	}

	@Override
	public int remainingScoreOfVersion(int id_version, GameState<?> state) 
	{
		if (!(state.robot.isGlassStoredRight && state.robot.isGlassStoredLeft) && state.table.isGlassXTaken(id_version) && (90000-state.timeEllapsed)>timeToDropGlass) 
		{
			return 4;
		}
		return 0;
	}

	@Override
	public void finalize(GameState<?> stateToConsider) throws SerialFinallyException 
	{
		try 
		{
			stateToConsider.robot.useActuator(ActuatorOrder.ARM_LEFT_CLOSE, false);
			stateToConsider.robot.useActuator(ActuatorOrder.ARM_RIGHT_CLOSE, false);
		} 
		catch (SerialConnexionException e)
		{
			throw new SerialFinallyException ();
		}
		
	}
	
	//attrape le plot (ouvre / avance / ferme )
	private void takeGlass (GameState<Robot> stateToConsider, ArrayList<Hook> hooksToConsider, boolean isSecondTry , boolean isArmChosenLeft) throws SerialConnexionException, UnableToMoveException
	{
		//On ouvre le bras
		if(isArmChosenLeft)
		{
			stateToConsider.robot.useActuator(ActuatorOrder.ARM_LEFT_OPEN, false);
			stateToConsider.robot.turn((-Math.PI/12), hooksToConsider, false, true);
		}
		else 
		{
			stateToConsider.robot.useActuator(ActuatorOrder.ARM_RIGHT_OPEN, false);
			stateToConsider.robot.turn((Math.PI/12), hooksToConsider, false, true);
		}
		
		//On avance vers le plot
		stateToConsider.robot.moveLengthwise(60,hooksToConsider);

		//On prend le verre
		if(isArmChosenLeft)
		{
			stateToConsider.robot.useActuator(ActuatorOrder.ARM_LEFT_CLOSE_SLOW, true);
		}
		else 
			stateToConsider.robot.useActuator(ActuatorOrder.ARM_RIGHT_CLOSE_SLOW, true);
	}

	public Integer[] getVersion(GameState<?> stateToConsider)
	{
		ArrayList <Integer> versionList = new ArrayList<Integer>(Arrays.asList(versions));
		
		for (int i = 0; i<versionList.size(); i++)
		{
			if (stateToConsider.table.isGlassXTaken(i))
				versionList.remove((Integer)i);
		}
		
		//on converti en Integer[]
		Integer[] retour = new Integer[versionList.size()];
	    for (int i=0; i < retour.length; i++)
	    {
	    	retour[i] = versionList.get(i).intValue();
	    }
	    return retour;
	}

}
