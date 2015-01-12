package scripts;

import java.util.ArrayList;

import enums.ActuatorOrder;
import exceptions.UnableToEatPlot;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialConnexionException;
import hook.Hook;
import hook.types.HookFactory;
import robot.Robot;
import smartMath.Circle;
import strategie.GameState;
import utils.Config;
import utils.Log;

public class GetPlot extends AbstractScript
{
	//TODO: doc	
	private int distanceEntrePlots;
	

	public GetPlot(HookFactory hookFactory, Config config, Log log) 
	{
		super(hookFactory, config, log);
		ArrayList<Integer> versionList = new ArrayList<Integer>();
		
		//pour le plot 0
		versionList.add(0); 
		
		 //pour le plot 1
		versionList.add(1);
		
		//pour le plot 2
		versionList.add(2); 
		
		//pour les plots 3 et 4
		versionList.add(34); 
		
		//pour les plots 5 et 6
		versionList.add(56); 
		
		//pour le plot 7
		versionList.add(7); 
		
	}
	
	@Override
	public void execute(int versionToExecute, GameState<Robot> stateToConsider, ArrayList<Hook> hooksToConsider, boolean shouldRetryIfBlocked) throws UnableToMoveException, SerialConnexionException
	{
		
		//version circulaire
		if (versionToExecute == 0 || versionToExecute == 1 || versionToExecute == 2 || versionToExecute == 7)
		{
			//si on a plus de place dans la pile on termine
			if (stateToConsider.robot.storedPlotCount == 4)
			{
				return;
			}
			
			//TODO: trouver le bon bras pour manger
			//on choisi le bras le plus adapte (assez dificile)
			boolean isChoosenArmLeft = true;
			
			//on se place en face
			stateToConsider.robot.turn(Math.atan2(entryPosition(versionToExecute).center.y-stateToConsider.robot.getPosition().y/*position voulue - position actuelle*/
					, entryPosition(versionToExecute).center.x-stateToConsider.robot.getPosition().x/*de meme*/));
			
			//on mange le plot
			try 
			{
				eatPlot(false, isChoosenArmLeft, stateToConsider);
			} 
			catch (UnableToEatPlot e) 
			{
				//on a pas reussi a manger, on le dit et on termine le script
				log.debug("impossible de manger le plot n°"+versionToExecute+" mangeage echoue", this);
				finalise(stateToConsider);
				return;
			}
			
			//si on a ramasse qqc on incrément le nb de plots
			stateToConsider.robot.storedPlotCount++;
			stateToConsider.table.eatPlotX(versionToExecute);
		}
		//TODO les versions maquantes du script en lui meme
		else if (versionToExecute == 34)
		{
			//si on a plus de place dans la pile on termine
			if (stateToConsider.robot.storedPlotCount == 4)
			{
				return;
			}
			stateToConsider.robot.turn(Math.PI*0.5);
			
			//si le plot 3 a deja ete mangé
			if (stateToConsider.table.isPlotXEaten(3))
			{
				//si les plots 3 et 4 ont deja ete manges alors on a fini
				if (stateToConsider.table.isPlotXEaten(4))
				{
					return;
				}
				
				//on mange le plot 4 avec le bras droit (si on est vert)
				stateToConsider.robot.moveLengthwise(distanceEntrePlots, hooksToConsider);
				try 
				{
					//on ne veut pas reessayer
					eatPlot(true, false, stateToConsider);
				} 
				catch (UnableToEatPlot e) 
				{
					//on a pas reussi a manger, on le dit et on termine le script
					log.debug("impossible de manger le plot n°4 mangeage echoue", this);
					finalise(stateToConsider);
					return;
				}
			}
			//si le plot 3 n'a pas ete mange
			else
			{
				//si on a plus qu'un seule place dans la pile
				if (stateToConsider.robot.storedPlotCount == 3)
				{
					//on mange le plot 3 uniquement
				}
				else
				{
					//sinon on mange les deux
				}
			}
		}
		else if (versionToExecute == 56)
		{
			stateToConsider.robot.turn(Math.PI*-0.5);
			
			//si le plot 3 a deja ete mangé
			if (stateToConsider.table.isPlotXEaten(5))
			{
				
			}
			else
			{
				
			}
		}
		else
		
		
		try 
		{
			//TODO si on en a 3 au depart il ne faut pas ramasser le deuxieme plot mais il faut pouvoir recommencer le script a partir du deuxieme (point de depart different)
			//version double
			//on fait monter le potenetiel plot en cours
			stateToConsider.robot.turn(Math.PI*0.5, hooksToConsider, false);
			eatPlot(true, false, stateToConsider);
			//si on a ramasse qqc on incrément le nb de plots
			//on fait monter
			stateToConsider.robot.moveLengthwise(distanceEntrePlots, hooksToConsider, false);
			eatPlot(true, false, stateToConsider);
			//si on a ramasse qqc on incrément le nb de plots
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
		} catch (UnableToEatPlot e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
	public Circle entryPosition(int id)
	{
		if (id==0)
			return new Circle (200,600,200);
		else if (id==1)
			return new Circle (400,250,200);
		else if (id==2)
			return new Circle (630,645,200);
		else if (id==34)
			return new Circle (410,550,0);
		else if (id==56)
			return new Circle (650,1700,0);
		else if (id==7)
			return new Circle (1410,1800,200);
		else 
			log.debug("out of bound : mauvais numero de script", this);
			return new Circle (0,0);
	}

	@Override
	public int remainingScoreOfVersion(int id_version, GameState<?> state) 
	{
		//Uniquement pour la version ou on attrape une unique balle
		if (id_version == 0 || id_version == 1 || id_version == 2 || id_version == 7)
		{
			if (state.robot.storedPlotCount<4 && !state.table.isPlotXEaten(id_version))
			//si la pile est moins de 4 plots et que le plot n'a pas ete mange
				if (state.robot.asBallStored)
					return 5;
				else
					return 2;
			else
				return 0;
		}
		//verions ou on attrape deux plots
		else
		{
			int plot1ToEat;
			int plot2ToEat;
			if (id_version == 34)
			{
				plot1ToEat = 3;
				plot2ToEat = 4;
			}
			else
			{
				plot1ToEat = 5;
				plot2ToEat = 6;
			}
				
			if (state.robot.storedPlotCount<3 && !state.table.isPlotXEaten(plot1ToEat) && !state.table.isPlotXEaten(plot2ToEat))
				//si la pile est de moins de 3 plots et que le premier et le second plot n'a pas ete mange
					if (state.robot.asBallStored)
						return 10;
					else
						return 4;
				else if (state.robot.storedPlotCount<4 && (!state.table.isPlotXEaten(plot1ToEat) || !state.table.isPlotXEaten(plot2ToEat)))
					//si la pile est de 3 et que le plot 1 ou 2 n'a pas ete mange 
					if (state.robot.asBallStored)
						return 5;
					else
						return 2;
				else
					return 0;
		}
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
	 * ne se deplace pas, bloquante
	 * eleve le plot precedemment dans les machoires (si il existe)
	 * 
	 * @param isSecondTry vrai si l'essai de mangeage de plot est le deuxieme ou si on ne veux pas reessayer
	 * @param isArmChosenLeft vrai si on mange avec le bras gauche
	 * @throws UnableToEatPlot si le mangeage echoue
	 * 
	 * @throws SerialException
	 */
	private void eatPlot (boolean isSecondTry, boolean isArmChosenLeft, GameState<Robot> stateToConsider) throws SerialConnexionException, UnableToEatPlot
	{
		
		if (stateToConsider.robot.storedPlotCount!=0)
		{
			elevatePlot(stateToConsider);
		}
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
		stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_CLOSE_JAW, true);
		
		//TODO le capteur de sylvain
		if (true/*"on a rien attrape"*/)	
			if (isSecondTry)
			{
				log.debug("impossible d'attraper le plot", this);	
				throw new UnableToEatPlot();
			}
			else
			{
				eatPlot(true,!isArmChosenLeft, stateToConsider);
			}
		//sinon on a attrape qqc, mettre a jour la table
	}

	/**
	 * eleve un plot, bloquante
	 * @param stateToConsider l'etat de la table a considerer
	 * @throws SerialConnexionException si la connexion serie fonctionne mal
	 */
	private void elevatePlot (GameState<Robot> stateToConsider) throws SerialConnexionException
	{
		stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_HIGH, true);
		stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_GROUND, true);
	}
}
