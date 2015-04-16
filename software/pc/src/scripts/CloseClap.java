package scripts;

import java.util.ArrayList;
import java.util.Arrays;

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
 * @author theo
 * Exemple sur Paul
 * Script Fermer les claps
 *
 *Table :
 *
 *    ___________________
 *   |					|
 * 	 |					|
 *   |					|Debut du robot ici
 *   |					|
 *   |		N° claps	|
 *    /_3_/________2_/_1     
 *    
 *    1,2,3 nous appartiennent
 *    
 *   
 */

public class CloseClap extends AbstractScript 
{
	private static final int AverageTimeToPlaceGlass = 10000;

	private static final int AverageTimeToPlacePlot = 5000;

	//Distance à avancer après le clap 2 pour esquiver l'estrade rouge en (0,0)
	private int distanceToDodgeEstrade = 250;
	
	//Distance à avancer après le clap 2 pour aller au clap 3
	private int distanceBetween2and3 = 1700;
	
	
	/**
	 * Constructeur (normalement appelé uniquement par le scriptManager) du script fermant les Claps
	 * Le container se charge de renseigner la hookFactory, le système de config et de log.
	 * @param hookFactory La factory a utiliser pour générer les hooks dont pourra avoir besoin le script
	 * @param config le fichier de config a partir duquel le script pourra se configurer
	 * @param log le système de log qu'utilisera le script
	 * TODO: seul closeAllClaps fonctionne, il faut modifier les autres
	 */
	
	public CloseClap(HookFactory hookFactory, Config config, Log log)
	{
		super(hookFactory, config, log);
		versions = new Integer[]{1, 2, 3 ,12 ,123 , -1, -12}; // liste des versions
	}
	
	@Override
	public void execute(int versionToExecute, GameState<Robot> stateToConsider, ArrayList<Hook> hooksToConsider, boolean shouldRetryIfBlocked) throws UnableToMoveException, SerialConnexionException, SerialFinallyException
	{		
		if (versionToExecute == 123)
			closeAllOurClaps(stateToConsider, hooksToConsider, shouldRetryIfBlocked);
		else if (versionToExecute == 1)
			closeFirstClap(stateToConsider, hooksToConsider, shouldRetryIfBlocked);
		else if (versionToExecute == 2)
			closeSecondClap(stateToConsider, hooksToConsider, shouldRetryIfBlocked);
		else if (versionToExecute == 3)
			closeThirdClap(stateToConsider, hooksToConsider, shouldRetryIfBlocked);
		else if (versionToExecute == 12)
			closeFirstAndSecondClap(stateToConsider, hooksToConsider, shouldRetryIfBlocked);
		else if (versionToExecute == -1)
			closeFirstClapBackward(stateToConsider, hooksToConsider, shouldRetryIfBlocked);
		else if (versionToExecute == -12)
			closeFirstAndSecondClapBackward(stateToConsider, hooksToConsider, shouldRetryIfBlocked);
		else
			log.debug("Souci de version", this);	//TODO: lancer une exception de version inconnue (la créer si besoin)
		
		finalise(stateToConsider);
	}
	
	public void closeFirstClap (GameState<Robot> stateToConsider,  ArrayList<Hook> hooksToConsider, boolean shouldRetryIfBlocked) throws UnableToMoveException, SerialConnexionException
	{
		//pour ne pas frotter l'ascenceur
		stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_LOW, true);

		//on commence en (1290,231), on se tourne dans le bon sens
		stateToConsider.robot.turn(Math.PI, hooksToConsider, false);
				
		//on recule pour se mettre en (1360,231)
		stateToConsider.robot.moveLengthwise(-120, hooksToConsider, true);//-100
			
		//On ouvre le bras puis on avance pour se retrouver en (1010,231)
		if(stateToConsider.robot.getSymmetry())
		{
			//Coté jaune
			stateToConsider.robot.useActuator(ActuatorOrder.MID_RIGHT_CLAP, true);
		}
		else //coté vert
		{
			stateToConsider.robot.useActuator(ActuatorOrder.MID_LEFT_CLAP, true);
		}
		stateToConsider.robot.moveLengthwise(250, hooksToConsider, false);
		stateToConsider.table.clapXClosed(1);
	
		if(stateToConsider.robot.getSymmetry())
		{
			//Coté jaune
			stateToConsider.robot.useActuator(ActuatorOrder.HIGH_RIGHT_CLAP, true);
		}
		else //coté vert
		{
			stateToConsider.robot.useActuator(ActuatorOrder.HIGH_LEFT_CLAP, true);
		}	
		//On s'echape
		stateToConsider.robot.turn(Math.PI/2, hooksToConsider, false);
		
		//On ferme tout pour finir
		stateToConsider.robot.useActuator(ActuatorOrder.LOW_RIGHT_CLAP, false);
		stateToConsider.robot.useActuator(ActuatorOrder.LOW_LEFT_CLAP, false);
	}

	public void closeSecondClap (GameState<Robot> stateToConsider,  ArrayList<Hook> hooksToConsider, boolean shouldRetryIfBlocked) throws UnableToMoveException, SerialConnexionException
	{
		//pour ne pas frotter l'ascenceur
		stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_LOW, true);
				
		//on commence en (700,231), on se tourne dans le bon sens
		stateToConsider.robot.turn(Math.PI, hooksToConsider, false);
		
		//On ouvre le bras puis on avance de 300mm pour se retrouver en (400,231)
		if(stateToConsider.robot.getSymmetry())
		{
			//Coté jaune
			stateToConsider.robot.useActuator(ActuatorOrder.MID_RIGHT_CLAP, true);
		}
		else //coté vert
		{
			stateToConsider.robot.useActuator(ActuatorOrder.MID_LEFT_CLAP, true);
		}	
		
		stateToConsider.robot.moveLengthwise(300, hooksToConsider, false);
		
		//On s'echape
		stateToConsider.robot.turn(Math.PI/2, hooksToConsider, false);
		stateToConsider.table.clapXClosed(2);	

						
		//On ferme tout pour finir
		stateToConsider.robot.useActuator(ActuatorOrder.LOW_RIGHT_CLAP, false);
		stateToConsider.robot.useActuator(ActuatorOrder.LOW_LEFT_CLAP, false);	
	}
	
	public void closeThirdClap (GameState<Robot> stateToConsider,  ArrayList<Hook> hooksToConsider, boolean shouldRetryIfBlocked) throws UnableToMoveException, SerialConnexionException  //Ferme le claps de fin
	{
		
		//pour ne pas frotter l'ascenceur
		stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_LOW, true);
		
		
		//(-900,500)->(-1050,230), mis en place pour contrer le PathNotFound
		stateToConsider.robot.turn(Math.PI, hooksToConsider, false);
		stateToConsider.robot.moveLengthwise(150, hooksToConsider, false);
		stateToConsider.robot.turn(-Math.PI/2, hooksToConsider, false);
		stateToConsider.robot.moveLengthwise(500-230, hooksToConsider, false);

		
		stateToConsider.robot.turn(0, hooksToConsider, false);
		//on ouvre notre bras puis on avance de 200mm pour se retrouver en 
		if(!stateToConsider.robot.getSymmetry())
		{
			//Coté jaune
			stateToConsider.robot.useActuator(ActuatorOrder.MID_RIGHT_CLAP, true);
		}
		else //coté vert
		{
			stateToConsider.robot.useActuator(ActuatorOrder.MID_LEFT_CLAP, true);
		}			
		stateToConsider.robot.moveLengthwise(200, hooksToConsider, false);//(-850,231) 
		stateToConsider.table.clapXClosed(3);
		
		if(!stateToConsider.robot.getSymmetry())
		{
			//Coté jaune
			stateToConsider.robot.useActuator(ActuatorOrder.HIGH_RIGHT_CLAP, true);
		}
		else //coté vert
		{
			stateToConsider.robot.useActuator(ActuatorOrder.HIGH_LEFT_CLAP, true);
		}		
		//On s'echape
		stateToConsider.robot.turn(Math.PI/2, hooksToConsider, false);
				
		//On ferme tout pour finir
		stateToConsider.robot.useActuator(ActuatorOrder.LOW_RIGHT_CLAP, false);
		stateToConsider.robot.useActuator(ActuatorOrder.LOW_LEFT_CLAP, false);
	}
	
	public void closeFirstAndSecondClap (GameState<Robot> stateToConsider,  ArrayList<Hook> hooksToConsider, boolean shouldRetryIfBlocked) throws UnableToMoveException, SerialConnexionException
	{
		//pour ne pas frotter l'ascenceur
		stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_LOW, true);
		
		//on commence en (1290,231), on se tourne dans le bon sens
		stateToConsider.robot.turn(Math.PI, hooksToConsider, false);
		
		//on reculle pour se mettre en (1350,231)
		stateToConsider.robot.moveLengthwise(-40, hooksToConsider, true);//-60
	
		//On ouvre le bras puis on avance de 300mm pour se retrouver en (1050,231)
		if(stateToConsider.robot.getSymmetry())
		{
			//Coté jaune
			stateToConsider.robot.useActuator(ActuatorOrder.MID_RIGHT_CLAP, true);
		}
		else //coté vert
		{
			stateToConsider.robot.useActuator(ActuatorOrder.MID_LEFT_CLAP, true);
		}
		
		stateToConsider.robot.moveLengthwise(300, hooksToConsider, false);
		stateToConsider.table.clapXClosed(1);
	
		//On monte notre bras pour passer au dessus du clap ennemi notre bras et on avance de 350mm pour se retrouver en (700,231)
		if(stateToConsider.robot.getSymmetry())
		{
			//Coté jaune
			stateToConsider.robot.useActuator(ActuatorOrder.HIGH_RIGHT_CLAP, true);
		}
		else //coté vert
		{
			stateToConsider.robot.useActuator(ActuatorOrder.HIGH_LEFT_CLAP, true);
		}
		stateToConsider.robot.moveLengthwise(350, hooksToConsider, false);

		//On ouvre le bras puis on avance de 300mm pour se retrouver en (400,231)
		if(stateToConsider.robot.getSymmetry())
		{
			//Coté jaune
			stateToConsider.robot.useActuator(ActuatorOrder.MID_RIGHT_CLAP, true);
		}
		else //coté vert
		{
			stateToConsider.robot.useActuator(ActuatorOrder.MID_LEFT_CLAP, true);
		}		
		stateToConsider.robot.moveLengthwise(300, hooksToConsider, false);
		stateToConsider.table.clapXClosed(2);
		
		if(stateToConsider.robot.getSymmetry())
		{
			//Coté jaune
			stateToConsider.robot.useActuator(ActuatorOrder.HIGH_RIGHT_CLAP, true);
		}
		else //coté vert
		{
			stateToConsider.robot.useActuator(ActuatorOrder.HIGH_LEFT_CLAP, true);
		}	
		//On s'echape
		stateToConsider.robot.turn(Math.PI/2, hooksToConsider, false);
				
		//On ferme tout pour finir
		stateToConsider.robot.useActuator(ActuatorOrder.LOW_RIGHT_CLAP, false);
		stateToConsider.robot.useActuator(ActuatorOrder.LOW_LEFT_CLAP, false);
	}
	
	public void closeAllOurClaps(GameState<Robot> stateToConsider,  ArrayList<Hook> hooksToConsider, boolean shouldRetryIfBlocked) throws UnableToMoveException, SerialConnexionException  //Ferme tous les Claps, depuis le  debut
	{
		//pour ne pas frotter l'ascenceur
		stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_LOW, true);
				
		//on commence en (1290,231), on se tourne dans le bon sens
		stateToConsider.robot.turn(Math.PI, hooksToConsider, false);
		
		//on recule pour se mettre en (1360,231)
		stateToConsider.robot.moveLengthwise(-120, hooksToConsider, true);//-100
	
		//On ouvre le bras puis on avance de 250mm pour se retrouver en (1010,231)
		if(stateToConsider.robot.getSymmetry())
		{
			//Coté jaune
			stateToConsider.robot.useActuator(ActuatorOrder.MID_RIGHT_CLAP, true);
		}
		else //coté vert
		{
			stateToConsider.robot.useActuator(ActuatorOrder.MID_LEFT_CLAP, true);
		}
		stateToConsider.robot.moveLengthwise(250, hooksToConsider, false);
		stateToConsider.table.clapXClosed(1);
	
		//On monte notre bras pour passer au dessus du clap ennemi notre bras et on avance de 250mm pour se retrouver en (660,231)
		if(stateToConsider.robot.getSymmetry())
		{
			//Coté jaune
			stateToConsider.robot.useActuator(ActuatorOrder.HIGH_RIGHT_CLAP, true);
		}
		else //coté vert
		{
			stateToConsider.robot.useActuator(ActuatorOrder.HIGH_LEFT_CLAP, true);
		}
		stateToConsider.robot.moveLengthwise(250, hooksToConsider, false);

		//On ouvre le bras puis on avance de 220mm pour se retrouver en (400,231)
		if(stateToConsider.robot.getSymmetry())
		{
			//Coté jaune
			stateToConsider.robot.useActuator(ActuatorOrder.MID_RIGHT_CLAP, true);
		}
		else //coté vert
		{
			stateToConsider.robot.useActuator(ActuatorOrder.MID_LEFT_CLAP, true);
		}
		stateToConsider.robot.moveLengthwise(220, hooksToConsider, false);
		stateToConsider.table.clapXClosed(2);	

		//on baisse notre bras
		stateToConsider.robot.turn(0.5*Math.PI, hooksToConsider, false);
		if(stateToConsider.robot.getSymmetry())
		{
			//Coté jaune
			stateToConsider.robot.useActuator(ActuatorOrder.LOW_RIGHT_CLAP, true);
		}
		else //coté vert
		{
			stateToConsider.robot.useActuator(ActuatorOrder.LOW_LEFT_CLAP, true);
		}		
		//on vas au 3eme clap donc en (-1340,231)
		stateToConsider.robot.moveLengthwise(distanceToDodgeEstrade, hooksToConsider, false);
		stateToConsider.robot.turn(Math.PI, hooksToConsider, false);
		stateToConsider.robot.moveLengthwise(distanceBetween2and3, hooksToConsider, false);
		stateToConsider.robot.turn(-0.5*Math.PI, hooksToConsider, false);
		stateToConsider.robot.moveLengthwise(distanceToDodgeEstrade, hooksToConsider, false);
		
		//on est en (-1340,231), on se retourne dans le bon sens
		stateToConsider.robot.turn(0, hooksToConsider, false);
		
		//on ouvre notre bras puis on avance de 200mm pour se retrouver en (-1140,231) 
		if(!stateToConsider.robot.getSymmetry())
		{
			//Coté jaune
			stateToConsider.robot.useActuator(ActuatorOrder.MID_RIGHT_CLAP, true);
		}
		else //coté vert
		{
			stateToConsider.robot.useActuator(ActuatorOrder.MID_LEFT_CLAP, true);
		}	
		stateToConsider.robot.moveLengthwise(200, hooksToConsider, false);
		stateToConsider.table.clapXClosed(3);
		
		if(!stateToConsider.robot.getSymmetry())
		{
			//Coté jaune
			stateToConsider.robot.useActuator(ActuatorOrder.HIGH_RIGHT_CLAP, true);
		}
		else //coté vert
		{
			stateToConsider.robot.useActuator(ActuatorOrder.HIGH_LEFT_CLAP, true);
		}		//On s'echape
		stateToConsider.robot.turn(Math.PI/2, hooksToConsider, false);
		
		//On ferme tout pour finir
		stateToConsider.robot.useActuator(ActuatorOrder.LOW_RIGHT_CLAP, false);
		stateToConsider.robot.useActuator(ActuatorOrder.LOW_LEFT_CLAP, false);
	}
	
	
	void closeFirstClapBackward(GameState<Robot> stateToConsider,  ArrayList<Hook> hooksToConsider, boolean shouldRetryIfBlocked) throws UnableToMoveException, SerialConnexionException
	{
		//pour ne pas frotter l'ascenceur
		stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_LOW, true);

		//on commence en (1290,231), on se tourne dans le sens inverse au clap
		stateToConsider.robot.turn(0, hooksToConsider, false);
				
		//on recule pour se mettre en (1360,231)
		stateToConsider.robot.moveLengthwise(120, hooksToConsider, true);//-100
			
		//On ouvre le bras puis on recule pour se retrouver en (1010,231)
		if(!stateToConsider.robot.getSymmetry())
		{
			//Coté jaune
			stateToConsider.robot.useActuator(ActuatorOrder.MID_RIGHT_CLAP, true);
		}
		else //coté vert
		{
			stateToConsider.robot.useActuator(ActuatorOrder.MID_LEFT_CLAP, true);
		}
		
		stateToConsider.robot.moveLengthwise(-250, hooksToConsider, false);
		stateToConsider.table.clapXClosed(1);
	
		if(!stateToConsider.robot.getSymmetry())
		{
			//Coté jaune
			stateToConsider.robot.useActuator(ActuatorOrder.HIGH_RIGHT_CLAP, true);
		}
		else //coté vert
		{
			stateToConsider.robot.useActuator(ActuatorOrder.HIGH_LEFT_CLAP, true);
		}	
		//On s'echape dans le sens normal
		stateToConsider.robot.turn(Math.PI/2, hooksToConsider, false);
		
		//On ferme tout pour finir
		stateToConsider.robot.useActuator(ActuatorOrder.LOW_RIGHT_CLAP, false);
		stateToConsider.robot.useActuator(ActuatorOrder.LOW_LEFT_CLAP, false);
	}

	void closeFirstAndSecondClapBackward(GameState<Robot> stateToConsider,  ArrayList<Hook> hooksToConsider, boolean shouldRetryIfBlocked) throws UnableToMoveException, SerialConnexionException
	{
		try 
		{
			stateToConsider.robot.turn (Math.PI*0.25);
			
			if (stateToConsider.robot.getSymmetry())
				stateToConsider.robot.useActuator(ActuatorOrder.MID_LEFT_CLAP, true);
			else
				stateToConsider.robot.useActuator(ActuatorOrder.MID_RIGHT_CLAP, true);
			
			stateToConsider.robot.turn (0);
			if (stateToConsider.robot.getSymmetry())
				stateToConsider.robot.useActuator(ActuatorOrder.HIGH_LEFT_CLAP, true);
			else
				stateToConsider.robot.useActuator(ActuatorOrder.HIGH_RIGHT_CLAP, true);
			
			stateToConsider.robot.moveLengthwise(-400);
			if (stateToConsider.robot.getSymmetry())
				stateToConsider.robot.useActuator(ActuatorOrder.MID_LEFT_CLAP, true);
			else
				stateToConsider.robot.useActuator(ActuatorOrder.MID_RIGHT_CLAP, true);
			
			stateToConsider.robot.turn(Math.PI*-0.5);
			if (stateToConsider.robot.getSymmetry())
				stateToConsider.robot.useActuator(ActuatorOrder.LOW_LEFT_CLAP, true);
			else
				stateToConsider.robot.useActuator(ActuatorOrder.LOW_RIGHT_CLAP, true);
			
			stateToConsider.robot.turn (Math.PI);
			
			//On ferme tout pour finir
			stateToConsider.robot.useActuator(ActuatorOrder.LOW_RIGHT_CLAP, false);
			stateToConsider.robot.useActuator(ActuatorOrder.LOW_LEFT_CLAP, false);
		}
		catch (UnableToMoveException e1) 
		{
			e1.printStackTrace();
		} 
	}
	
	
	@Override
	public Circle entryPosition(int version, int ray)
	{		
		if (version == 1)
			return new Circle(1290,230); //point d'entrée : bord de la table, robot devant le clap 1
		else if(version == 2)
			return new Circle(700,230); //point d'entrée : devant le clap 2
		else if(version == 3)
			return new Circle(-900,500);//point d'entrée : devant le clap 3
		else if(version == 12)
			return new Circle(1290,230); //point d'entrée : devant le clap 1
		else if(version == 123)
			return new Circle(1290,230); //point d'entrée : devant le clap 1
		else if(version == -1)
			return new Circle(1240,230); //point d'entrée : devant le clap 1 //TODO point d'entrée à changer
		else if(version == -12)
			return new Circle(1220,230); //point d'entrée : devant le clap 1 //TODO point d'entrée à changer
		else
		{
			log.debug("Probleme d'entrée de position", this);
			return null;
		}
	}

	@Override
	public int remainingScoreOfVersion(int version, GameState<?> stateToConsider)
	{
		//On met à jour le nombre de points restants pour la version : à  0, on a tout fait
		int score = 15;
		if(stateToConsider.table.isClapXClosed(1) || version==2 || version==3)
			score -= 5;
		if(stateToConsider.table.isClapXClosed(2) || version == 1 || version == 3 || version == -1 )
			score -= 5;
		if(stateToConsider.table.isClapXClosed(3) || version == 1 || version == 2 || version == 12 || version == -1 || version == -12)
			score -= 5;
		if(!stateToConsider.table.isGlassXTaken(0) && (version ==1 || version == 12 || version == -1 || version == -12))
			score -=Math.min((int)(90000-stateToConsider.timeEllapsed)/AverageTimeToPlaceGlass, 4);
		if(!stateToConsider.table.isPlotXEaten(3) && (version ==1 || version == 12 || version == -1 || version == -12))
			score -= Math.min((int)(90000-stateToConsider.timeEllapsed)/AverageTimeToPlacePlot, 5);
		if(!stateToConsider.table.isPlotXEaten(4) && (version ==1 || version == 12 || version == -1 || version == -12))
			score -=Math.min((int)(90000-stateToConsider.timeEllapsed)/AverageTimeToPlacePlot,5);
		return score;
	}

	@Override
	protected void finalise(GameState<?> stateToConsider) throws SerialFinallyException
	{	
		try 
		{
			//On ferme le robot à la fin, attention à ne rien cogner ! (rembarde , etc)
			stateToConsider.robot.useActuator(ActuatorOrder.LOW_RIGHT_CLAP, false);
			stateToConsider.robot.useActuator(ActuatorOrder.LOW_LEFT_CLAP, false);
			
			// TODO: voir si on ne peut pas mettre ici une protection anti-cognage en demandant au service de gestion d'obstacle s'il y a un obstacle sur les cotés du robot. (Attention, les bras doivent toujours essayer de se fermer, mais c'est juste pour voir si on ne peut pas le faire 3cm plus loin)
		} 
		catch (SerialConnexionException e) 
		{
			log.debug("Erreur termine : ne peux pas replier claps", this); // Viens me parler de cette ligne. Je dois t'expliquer ce que veut dire "remonter une exception"
			throw new SerialFinallyException ();
		}
	}


	public Integer[] getVersion(GameState<?> stateToConsider)
	{
		ArrayList<Integer> versionList = new ArrayList<Integer>(Arrays.asList(versions));
		
		if (stateToConsider.table.isClapXClosed(1))
		{
			versionList.remove((Integer)1);
			versionList.remove((Integer)12);
			versionList.remove((Integer)123);
			versionList.remove((Integer)(-12));
			versionList.remove((Integer)(-1));
		}
		if (stateToConsider.table.isClapXClosed(2))
		{
			versionList.remove((Integer)2);
			versionList.remove((Integer)12);
			versionList.remove((Integer)(-12));
			versionList.remove((Integer)123);
		}
		if (stateToConsider.table.isClapXClosed(3))
		{
			versionList.remove((Integer)3);
			versionList.remove((Integer)123);
		}
		
		
		//on convertit l'arrayList en Integer[]	
				Integer[] retour = new Integer[versionList.size()];
			    for (int i=0; i < retour.length; i++)
			    {
			    	retour[i] = versionList.get(i).intValue();
			    }
				return retour;
	}
}



