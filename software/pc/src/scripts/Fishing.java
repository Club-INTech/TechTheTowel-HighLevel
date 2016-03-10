package scripts;

import enums.ActuatorOrder;
import enums.Speed;
import exceptions.ExecuteException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialConnexionException;
import exceptions.serial.SerialFinallyException;
import hook.Callback;
import hook.Hook;
import hook.methods.DropFish;
import hook.methods.GetFish;
import hook.methods.RiseArm;
import hook.types.HookFactory;
import robot.Robot;
import smartMath.Arc;
import smartMath.Circle;
import smartMath.Vec2;
import strategie.GameState;
import table.Table;
import utils.Config;
import utils.Log;

import java.util.ArrayList;
/**
 * Script pour récuperer les poissons
 * Version 0 et 1: déplacement le long du bac pour récupérer les poissons, puis déplacement près du filet pour les lâcher. On suppose deux allers suffisant.
 * Version 2 : idem version 0 ou 1 avec déplacements supplémentaires au franchissement du filet pour éviter les blocages avec les vitres
 * Version 3 : intégration des hooks pour gagner en vitesse pour les mouvements des bras
 * Version 4 : intégration des trajectroires courbes pour gagner en vitesse sur le placement près du bac + hooks
 * @author CF, Discord
 */

public class Fishing extends AbstractScript
{
	public Fishing(HookFactory hookFactory, Config config, Log log) 
	{
		super(hookFactory, config, log);
		/**
		 * Versions du script
		 */
		versions = new Integer[]{0,1,2,3,4};
		
	}
	
	/**
	 * Méthode condensant quatre instructions à la suite pour relâcher les poissons
	 * @param stateToConsider ensemble des informations sur le match
	 * @throws SerialConnexionException 
	 */
	public void fishThem(GameState<Robot> stateToConsider) throws SerialConnexionException
	{
		stateToConsider.robot.useActuator(ActuatorOrder.MAGNET_DOWN, true);
		stateToConsider.robot.useActuator(ActuatorOrder.FINGER_DOWN, true);
		stateToConsider.robot.useActuator(ActuatorOrder.MAGNET_UP, true);
		stateToConsider.robot.useActuator(ActuatorOrder.FINGER_UP, true);
	}
	
	/**
	 * On lance le script choisi.
	 * @param versionToExecute Version a lancer
	 * @param stateToConsider Notre bon vieux robot
	 * @param hooksToConsider Les hooks necessaires pour l'execution du script
	 * @throws SerialFinallyException
	 * @throws ExecuteException
	 */
	@Override
	public void execute(int versionToExecute, GameState<Robot> stateToConsider,ArrayList<Hook> hooksToConsider) throws SerialFinallyException, ExecuteException
	{
		/*
		 * On exécute la version 0 pour que le robot effectue un créneau depuis la vitre centrale 
		 * La version 1 le fait s'approcher en marche avant depuis le coin de la table
		 */
		
		if (versionToExecute == 0)
		{
			try
			{
				// On prend une vitesse lente pour que les aimants puissent récupérer les poissons
				Speed speedBeforeScriptWasCalled = stateToConsider.robot.getLocomotionSpeed();
				stateToConsider.robot.setLocomotionSpeed(Speed.SLOW_ALL);
				
				// On commence à se placer près du bord
				stateToConsider.robot.turn(Math.PI - 0.18);
				
				stateToConsider.robot.moveLengthwise(-220, hooksToConsider, true);
		
				// On s'oriente vers le côté ennemi
				stateToConsider.robot.turn((Math.PI), hooksToConsider, true);
				
				// Avance légère pour placer le bras au dessus du bac
				//stateToConsider.robot.moveLengthwise(30, hooksToConsider, false);
			
				// On baisse le bras aimanté
				stateToConsider.robot.useActuator(ActuatorOrder.FISHING_POSITION, true);
				
				//stateToConsider.robot.sleep(800);

				// On longe le bac
				stateToConsider.robot.moveLengthwise(280, hooksToConsider, true);

				// On indique au robot que les poissons sont sur le bras
				stateToConsider.robot.setAreFishesOnBoard(true);
				
				// On remonte le bras pour passer au dessus du filet
				stateToConsider.robot.useActuator(ActuatorOrder.MIDDLE_POSITION, true);
				
				// Petite attente
				stateToConsider.robot.sleep(300);
				
				// On avance jusqu'au niveau du filet, distance à vérifier avec le robot final
				stateToConsider.robot.moveLengthwise(240, hooksToConsider, true);
				
				// On lâche les poissons
				this.fishThem(stateToConsider);
				
				// Points gagnés moyen pour ce passage
				stateToConsider.obtainedPoints += 20;
				
				// On indique au robot que les poissons ne sont plus sur le bras
				stateToConsider.robot.setAreFishesOnBoard(false);
				
				// On indique que deux poissons en moyenne ont été pris
				stateToConsider.table.fishesFished+=2;
				
				stateToConsider.robot.turn(Math.PI, hooksToConsider, true);
				
				stateToConsider.robot.moveLengthwise(-460, hooksToConsider, false);
				
				stateToConsider.robot.useActuator(ActuatorOrder.FISHING_POSITION, true);
				
				stateToConsider.robot.sleep(300);
				
				stateToConsider.robot.moveLengthwise(270, hooksToConsider, true);

				// On indique au robot que les poissons sont sur le bras
				stateToConsider.robot.setAreFishesOnBoard(true);
				
				// On remonte le bras pour passer au dessus du filet
				stateToConsider.robot.useActuator(ActuatorOrder.MIDDLE_POSITION, true);
				
				//Petite attente
				//stateToConsider.robot.sleep(800);
				
				//Légère modification d'orientation pour éviter de percuter le mur lors du retour du robot, à enlever pour un match
				//stateToConsider.robot.turn(Math.PI - Math.PI/25);
				
				// On avance jusqu'au niveau du filet, distance à vérifier avec le robot final
				stateToConsider.robot.moveLengthwise(280, hooksToConsider, true);
				
				// On lâche les poissons
				this.fishThem(stateToConsider);
				
				// On indique au robot que les poissons ne sont plus sur le bras
				stateToConsider.robot.setAreFishesOnBoard(false);
				
				// On indique que deux poissons en moyenne ont été pris
				stateToConsider.table.fishesFished+=2;
				
				// Points gagnés moyen pour ce passage
				stateToConsider.obtainedPoints += 20;
				
				stateToConsider.robot.setLocomotionSpeed(speedBeforeScriptWasCalled);
			}
			catch(UnableToMoveException | SerialConnexionException e)
			{
				finalize(stateToConsider);
				throw new ExecuteException(e);
			}
		}
		else if (versionToExecute == 1)
		{
			try
			{
				// On prend une vitesse lente pour que les aimants puissent récupérer les poissons
				Speed speedBeforeScriptWasCalled = stateToConsider.robot.getLocomotionSpeed();
				stateToConsider.robot.setLocomotionSpeed(Speed.SLOW_ALL);

				//On commence à se placer près du bord
				stateToConsider.robot.turn(Math.PI + 0.70);

				stateToConsider.robot.moveLengthwise(185,hooksToConsider,false);

				// On s'oriente vers le côté ennemi
				stateToConsider.robot.turn((Math.PI), hooksToConsider, true);

				// On baisse le bras aimanté
				stateToConsider.robot.useActuator(ActuatorOrder.FISHING_POSITION, true);

				stateToConsider.robot.sleep(800);

				// On longe le bac
				stateToConsider.robot.moveLengthwise(270, hooksToConsider, true);

				// On indique au robot que les poissons sont sur le bras
				stateToConsider.robot.setAreFishesOnBoard(true);

				// On remonte le bras pour passer au dessus du filet
				stateToConsider.robot.useActuator(ActuatorOrder.MIDDLE_POSITION, true);

				// Petite attente
				stateToConsider.robot.sleep(300);

				// On avance jusqu'au niveau du filet, distance à vérifier avec le robot final
				stateToConsider.robot.moveLengthwise(240, hooksToConsider, true);

				// On lâche les poissons
				this.fishThem(stateToConsider);

				// Points gagnés moyen pour ce passage
				stateToConsider.obtainedPoints += 20;

				// On indique au robot que les poissons ne sont plus sur le bras
				stateToConsider.robot.setAreFishesOnBoard(false);

				// On indique que deux poissons en moyenne ont été pris
				stateToConsider.table.fishesFished+=2;

				stateToConsider.robot.turn(Math.PI, hooksToConsider, true);

				stateToConsider.robot.moveLengthwise(-440, hooksToConsider, false);

				stateToConsider.robot.useActuator(ActuatorOrder.FISHING_POSITION, true);

				stateToConsider.robot.sleep(300);

				stateToConsider.robot.moveLengthwise(280, hooksToConsider, true);

				// On indique au robot que les poissons sont sur le bras
				stateToConsider.robot.setAreFishesOnBoard(true);

				// On remonte le bras pour passer au dessus du filet
				stateToConsider.robot.useActuator(ActuatorOrder.MIDDLE_POSITION, true);

				//Petite attente
				stateToConsider.robot.sleep(800);

				//Légère modification d'orientation pour éviter de percuter le mur lors du retour du robot, à enlever pour un match
				stateToConsider.robot.turn(Math.PI - Math.PI/25);

				// On avance jusqu'au niveau du filet, distance à vérifier avec le robot final
				stateToConsider.robot.moveLengthwise(280, hooksToConsider, true);

				// On lâche les poissons
				this.fishThem(stateToConsider);

				// On indique au robot que les poissons ne sont plus sur le bras
				stateToConsider.robot.setAreFishesOnBoard(false);

				// On indique que deux poissons en moyenne ont été pris
				stateToConsider.table.fishesFished+=2;

				// Points gagnés moyen pour ce passage
				stateToConsider.obtainedPoints += 20;

				stateToConsider.robot.setLocomotionSpeed(speedBeforeScriptWasCalled);


			}
			catch (UnableToMoveException | SerialConnexionException e)
			{
				finalize(stateToConsider);
				throw new ExecuteException(e);
			}

		}
		else if (versionToExecute == 2)
		{
			try
			{
				// On prend une vitesse lente pour que les aimants puissent récupérer les poissons
				Speed speedBeforeScriptWasCalled = stateToConsider.robot.getLocomotionSpeed();
				stateToConsider.robot.setLocomotionSpeed(Speed.SLOW_ALL);
				
				//On commence à se placer près du bord
				stateToConsider.robot.turn(Math.PI + 0.94);
				
				stateToConsider.robot.moveLengthwise(210,hooksToConsider,false);
				
				// On s'oriente vers le côté ennemi
				stateToConsider.robot.turn((Math.PI), hooksToConsider, true);

                //stateToConsider.robot.moveLengthwise(-0,hooksToConsider,false);

                // On baisse le bras aimanté
				stateToConsider.robot.useActuator(ActuatorOrder.FISHING_POSITION, true);
				
				stateToConsider.robot.sleep(800);

				// On longe le bac
				stateToConsider.robot.moveLengthwise(280, hooksToConsider, true);

				// On indique au robot que les poissons sont sur le bras
				stateToConsider.robot.setAreFishesOnBoard(true);
				
				// On remonte le bras pour passer au dessus du filet
				stateToConsider.robot.useActuator(ActuatorOrder.MIDDLE_POSITION, true);
				
				// Petite attente
				stateToConsider.robot.sleep(300);

				stateToConsider.robot.turn(stateToConsider.robot.getOrientation() - (Math.PI/25));
				
				// On avance jusqu'au niveau du filet, distance à vérifier avec le robot final
				stateToConsider.robot.moveLengthwise(160, hooksToConsider, true);

				stateToConsider.robot.turn(stateToConsider.robot.getOrientation() + 2*(Math.PI/25));

				stateToConsider.robot.moveLengthwise(100, hooksToConsider, true);


				// On lâche les poissons
				this.fishThem(stateToConsider);
				
				// Points gagnés moyen pour ce passage
				stateToConsider.obtainedPoints += 20;
				
				// On indique au robot que les poissons ne sont plus sur le bras
				stateToConsider.robot.setAreFishesOnBoard(false);
				
				// On indique que deux poissons en moyenne ont été pris
				stateToConsider.table.fishesFished+=2;
				
				//stateToConsider.robot.turn(Math.PI, hooksToConsider, true);

				stateToConsider.robot.moveLengthwise(-160, hooksToConsider, false);

				stateToConsider.robot.turn(stateToConsider.robot.getOrientation() - 2*(Math.PI/25));

				stateToConsider.robot.moveLengthwise(-180, hooksToConsider, false);

				stateToConsider.robot.turn(stateToConsider.robot.getOrientation() + (Math.PI/25));

				stateToConsider.robot.moveLengthwise(-250, hooksToConsider, false);
				
				stateToConsider.robot.useActuator(ActuatorOrder.FISHING_POSITION, true);

				stateToConsider.robot.turn(Math.PI);
				
				stateToConsider.robot.sleep(300);
				
				stateToConsider.robot.moveLengthwise(340, hooksToConsider, true);

				// On indique au robot que les poissons sont sur le bras
				stateToConsider.robot.setAreFishesOnBoard(true);
				
				// On remonte le bras pour passer au dessus du filet
				stateToConsider.robot.useActuator(ActuatorOrder.MIDDLE_POSITION, true);
				
				//Petite attente
				stateToConsider.robot.sleep(800);

				stateToConsider.robot.turn(stateToConsider.robot.getOrientation() - (Math.PI/25));

				// On avance jusqu'au niveau du filet, distance à vérifier avec le robot final
				stateToConsider.robot.moveLengthwise(160, hooksToConsider, true);

                stateToConsider.robot.turn(stateToConsider.robot.getOrientation() + 2*(Math.PI/25));

				stateToConsider.robot.moveLengthwise(100);
				
				// On lâche les poissons
				this.fishThem(stateToConsider);
				
				// On indique au robot que les poissons ne sont plus sur le bras
				stateToConsider.robot.setAreFishesOnBoard(false);
				
				// On indique que deux poissons en moyenne ont été pris
				stateToConsider.table.fishesFished+=2;
				
				// Points gagnés moyen pour ce passage
				stateToConsider.obtainedPoints += 20;

                stateToConsider.robot.moveLengthwise(100);
				
				stateToConsider.robot.setLocomotionSpeed(speedBeforeScriptWasCalled);
				
				
			}
			catch (UnableToMoveException | SerialConnexionException e)
			{
				finalize(stateToConsider);
				throw new ExecuteException(e);
			}
				
		}
		else if(versionToExecute == 3)
		{
			try
			{
				// On prend une vitesse lente pour que les aimants puissent récupérer les poissons
				Speed speedBeforeScriptWasCalled = stateToConsider.robot.getLocomotionSpeed();
				stateToConsider.robot.setLocomotionSpeed(Speed.SLOW_ALL);

				// On commence à se placer près du bord
				stateToConsider.robot.turn(Math.PI - 0.18);

				stateToConsider.robot.moveLengthwise(-310, hooksToConsider, true);

				// On s'oriente vers le côté ennemi
				stateToConsider.robot.turn((Math.PI), hooksToConsider, true);

				// On baisse le bras aimanté
				stateToConsider.robot.useActuator(ActuatorOrder.FISHING_POSITION, true);

				//stateToConsider.robot.sleep(800);

				// On crée le hook de position
				Hook hook = hookFactory.newXLesserHook(660);

				// On y ajoute un callback qui a pour action de lever le bras
				hook.addCallback(new Callback(new RiseArm(), true, stateToConsider));

				// On ajoute le hook à la liste
				hooksToConsider.add(hook);

				// On crée le hook de position
				Hook hook2 = hookFactory.newXLesserHook(520);

				// On y ajoute un callback qui a pour action de lâcher les poissons
				hook2.addCallback(new Callback(new DropFish(), true, stateToConsider));

				// On ajoute le hook à la liste
				hooksToConsider.add(hook2);

				// On longe le bac
				stateToConsider.robot.moveLengthwise(460, hooksToConsider, true);
				
				//On indique ques les poissons se trouvent sur le bras
				stateToConsider.robot.setAreFishesOnBoard(true);

				// Points gagnés moyen pour ce passage
				stateToConsider.obtainedPoints += 20;

				// On indique que deux poissons en moyenne ont été pris
				stateToConsider.table.fishesFished+=2;
				
				// On indique au robot que les poissons ne sont plus sur le bras
				stateToConsider.robot.setAreFishesOnBoard(false);
				
				// On crée le hook de position
				Hook hook3 = hookFactory.newXGreaterHook(750);

				// On y ajoute un callback qui a pour action de prendre les poissons
				hook3.addCallback(new Callback(new GetFish(), true, stateToConsider));

				// On ajoute le hook à la liste
				hooksToConsider.add(hook3);

				// on repart chercher d'autre poissons
				stateToConsider.robot.moveLengthwise(-460, hooksToConsider, true);
				
				// nouvelle condition pour le hook levant le bras
				hook = hookFactory.newXLesserHook(600);

				// On y ajoute un callback qui a pour action de lever le bras
				hook.addCallback(new Callback(new RiseArm(), true, stateToConsider));
				
				// mise à jour du hook avec la nouvelle condition
				hooksToConsider.add(hook);
				
				// nouvelle condition pour le hook lâchant les poissons
				hook2 = hookFactory.newXLesserHook(480);
				
				// On y ajoute un callback qui a pour action de lâcher les poissons
				hook2.addCallback(new Callback(new DropFish(), true, stateToConsider));
				
				// mise à jour du hook
				hooksToConsider.add(hook2);
				
				// on indique que les poissons sont sur le bras 
				stateToConsider.robot.setAreFishesOnBoard(true);
				
				// on longe le bac
				stateToConsider.robot.moveLengthwise(530, hooksToConsider, true);
				
				// On indique au robot que les poissons ne sont plus sur le bras
				stateToConsider.robot.setAreFishesOnBoard(false);

				// On indique que deux poissons en moyenne ont été pris
				stateToConsider.table.fishesFished+=2;

				// Points gagnés moyen pour ce passage
				stateToConsider.obtainedPoints += 20;

				stateToConsider.robot.setLocomotionSpeed(speedBeforeScriptWasCalled);
			}
			catch(UnableToMoveException | SerialConnexionException e)
			{
				finalize(stateToConsider);
				throw new ExecuteException(e);
			}
		}
			
		else if (versionToExecute == 4)
		{
			try
			{
				// On prend une vitesse lente pour que les aimants puissent récupérer les poissons
				Speed speedBeforeScriptWasCalled = stateToConsider.robot.getLocomotionSpeed();
				stateToConsider.robot.setLocomotionSpeed(Speed.SLOW_ALL);
				
				// Création du hook de position pour baisser le bras à l'arrivée du robot
				Hook hook1 = hookFactory.newXLesserHook(370);
				
				// On y ajoute le callback pour baisser le bras
				hook1.addCallback(new Callback(new GetFish(), true, stateToConsider));
				
				// On l'ajoute à la liste des hooks à executer
				hooksToConsider.add(hook1);
				
				// Mouvement du robot suivant un arc pour se placer près du bac
				stateToConsider.robot.moveArc(new Arc(stateToConsider.robot.getPosition(), new Vec2(1030,355), -Math.PI/2,false), hooksToConsider);
				
				// On s'oriente vers le côté ennemi, juste pour être sûr de l'orientation
				stateToConsider.robot.turn((Math.PI), hooksToConsider, true);
				
				// On crée le hook de position
				Hook hook2 = hookFactory.newXLesserHook(660);

				// On y ajoute un callback qui a pour action de lever le bras
				hook2.addCallback(new Callback(new RiseArm(), true, stateToConsider));

				// On ajoute le hook à la liste
				hooksToConsider.add(hook2);

				// On crée le hook de position
				Hook hook3 = hookFactory.newXLesserHook(520);

				// On y ajoute un callback qui a pour action de lâcher les poissons
				hook3.addCallback(new Callback(new DropFish(), true, stateToConsider));

				// On ajoute le hook à la liste
				hooksToConsider.add(hook3);

				// On longe le bac
				stateToConsider.robot.moveLengthwise(520, hooksToConsider, true);
				
				//On indique ques les poissons se trouvent sur le bras
				stateToConsider.robot.setAreFishesOnBoard(true);

				// Points gagnés moyen pour ce passage
				stateToConsider.obtainedPoints += 20;

				// On indique que deux poissons en moyenne ont été pris
				stateToConsider.table.fishesFished+=2;
				
				// On indique au robot que les poissons ne sont plus sur le bras
				stateToConsider.robot.setAreFishesOnBoard(false);
				
				// On modifie la condition du premier hook
				hook1 = hookFactory.newXGreaterHook(750);

				// On y ajoute un callback qui a pour action de prendre les poissons
				hook1.addCallback(new Callback(new GetFish(), true, stateToConsider));

				// Mide à jour du hook
				hooksToConsider.add(hook1);

				// on repart chercher d'autre poissons
				stateToConsider.robot.moveLengthwise(-460, hooksToConsider, true);
				
				// nouvelle condition pour le hook levant le bras
				hook2 = hookFactory.newXLesserHook(600);

				// On y ajoute un callback qui a pour action de lever le bras
				hook2.addCallback(new Callback(new RiseArm(), true, stateToConsider));
				
				// mise à jour du hook avec la nouvelle condition
				hooksToConsider.add(hook2);
				
				// nouvelle condition pour le hook lâchant les poissons
				hook2 = hookFactory.newXLesserHook(480);
				
				// On y ajoute un callback qui a pour action de lâcher les poissons
				hook2.addCallback(new Callback(new DropFish(), true, stateToConsider));
				
				// mise à jour du hook
				hooksToConsider.add(hook2);
				
				// on indique que les poissons sont sur le bras 
				stateToConsider.robot.setAreFishesOnBoard(true);
				
				// on longe le bac
				stateToConsider.robot.moveLengthwise(530, hooksToConsider, true);
				
				// On indique au robot que les poissons ne sont plus sur le bras
				stateToConsider.robot.setAreFishesOnBoard(false);

				// On indique que deux poissons en moyenne ont été pris
				stateToConsider.table.fishesFished+=2;

				// Points gagnés moyen pour ce passage
				stateToConsider.obtainedPoints += 20;

				stateToConsider.robot.setLocomotionSpeed(speedBeforeScriptWasCalled);
			}
			catch(UnableToMoveException e)
			{
				finalize(stateToConsider);
				throw new ExecuteException(e);
			}
		}
	}

	@Override
	public int remainingScoreOfVersion(int version, GameState<?> state) 
	{
		// Score maximal possible de 40 points
		int score=40;
		
		// Pour toutes les versions, on gagne 10 points par poisson dans le filet
		if (version == 0 || version ==1 || version ==2 || version==3 || version==4)
		{
			score-=((state.table.fishesFished)*10);
		}
		
		return score;
	}

	@Override
	public Circle entryPosition(int version, int ray, Vec2 robotPosition) 
	{
		// TODO a modifier avec les phases de test
		if (version == 0 || version == 3)
		{
			return new Circle(new Vec2(620,255));
		}
		else if (version == 1 || version == 2)
		{
			return new Circle(new Vec2(1030,355));
		}
		else if (version ==4)
		{
			return new Circle(robotPosition);
		}
		else
		{
			//TODO jetter une exception
			log.debug("erreur : mauvaise version de script");
			return new Circle(new Vec2(0,0));
		}
	}

	@Override
	public void finalize(GameState<?> stateToConsider) throws SerialFinallyException // TODO faire un joli finalize où l'on redépose les poissons en cas de rencontre avec robot adverse
	{
		try
		{
		// On remonte le bras en fin de script
		stateToConsider.robot.useActuator(ActuatorOrder.ARM_INIT, true);
		}
		catch (SerialConnexionException e) 
		{
			log.debug("Fishing : Impossible de ranger les bras !");
			throw new SerialFinallyException();
		}
	}

	@Override
	public Integer[] getVersion(GameState<?> stateToConsider) 
	{
		// Au vu des deux versions disponibles pour l'instant, sans informations sur le nombre de poissons pris, on retourne les deux versions
		return versions;
	}
	
}
