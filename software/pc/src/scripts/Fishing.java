package scripts;

import enums.ActuatorOrder;
import enums.ContactSensors;
import enums.Speed;
import enums.UnableToMoveReason;
import exceptions.BadVersionException;
import exceptions.BlockedActuatorException;
import exceptions.ExecuteException;
import exceptions.Locomotion.BlockedException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialConnexionException;
import exceptions.serial.SerialFinallyException;
import hook.Callback;
import hook.Hook;
import hook.methods.DropFish;
import hook.methods.GetFish;
import hook.methods.RiseArm;
import hook.methods.SetFishesOnBoard;
import hook.types.HookFactory;
import robot.Robot;
import smartMath.Arc;
import smartMath.Circle;
import smartMath.Vec2;
import strategie.GameState;
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
	/** Coordonnée en x pour le cas d'obstacle sur le chemin*/
	private int xBefore;
	
	/** Position idéale pour s'arrêter avant le bord du filet*/
	private int netPosX = 520;
	
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
	public void freeThem(GameState<?> stateToConsider) throws SerialConnexionException
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
	 * @throws BadVersionException si la version de script demandée n'existe pas
	 */
	@Override
	public void execute(int versionToExecute, GameState<Robot> stateToConsider,ArrayList<Hook> hooksToConsider) throws SerialFinallyException, ExecuteException, SerialConnexionException, UnableToMoveException, BlockedActuatorException
	{
		/*
		 * On exécute la version 0 pour que le robot effectue un créneau depuis la vitre centrale 
		 * La version 1 le fait s'approcher en marche avant depuis le coin de la table
		 */
		
		try
		{
			if(stateToConsider.robot.getContactSensorValue(ContactSensors.DOOR_BLOCKED))
			{
				log.debug("Porte bloquée, interruption de Fishing !");
				throw new BlockedActuatorException();
			}
			if (versionToExecute == 0)
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
				stateToConsider.robot.useActuator(ActuatorOrder.FISHING_POSITION_LOW, true);
				
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
				this.freeThem(stateToConsider);
				
				// Points gagnés moyen pour ce passage
				stateToConsider.obtainedPoints += 20;
				
				// On indique au robot que les poissons ne sont plus sur le bras
				stateToConsider.robot.setAreFishesOnBoard(false);
				
				// On indique que deux poissons en moyenne ont été pris
				stateToConsider.table.fishesFished+=2;
				
				stateToConsider.robot.turn(Math.PI, hooksToConsider, true);
				
				stateToConsider.robot.moveLengthwise(-460, hooksToConsider, false);
				
				stateToConsider.robot.useActuator(ActuatorOrder.FISHING_POSITION_LOW, true);
				
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
				this.freeThem(stateToConsider);
				
				// On indique au robot que les poissons ne sont plus sur le bras
				stateToConsider.robot.setAreFishesOnBoard(false);
				
				// On indique que deux poissons en moyenne ont été pris
				stateToConsider.table.fishesFished+=2;
				
				// Points gagnés moyen pour ce passage
				stateToConsider.obtainedPoints += 20;
				
				stateToConsider.robot.setLocomotionSpeed(speedBeforeScriptWasCalled);
			}

		
			else if (versionToExecute == 1)
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
				stateToConsider.robot.useActuator(ActuatorOrder.FISHING_POSITION_LOW, true);

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
				this.freeThem(stateToConsider);

				// Points gagnés moyen pour ce passage
				stateToConsider.obtainedPoints += 20;

				// On indique au robot que les poissons ne sont plus sur le bras
				stateToConsider.robot.setAreFishesOnBoard(false);

				// On indique que deux poissons en moyenne ont été pris
				stateToConsider.table.fishesFished+=2;

				stateToConsider.robot.turn(Math.PI, hooksToConsider, true);

				stateToConsider.robot.moveLengthwise(-440, hooksToConsider, false);

				stateToConsider.robot.useActuator(ActuatorOrder.FISHING_POSITION_LOW, true);

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
				this.freeThem(stateToConsider);

				// On indique au robot que les poissons ne sont plus sur le bras
				stateToConsider.robot.setAreFishesOnBoard(false);

				// On indique que deux poissons en moyenne ont été pris
				stateToConsider.table.fishesFished+=2;

				// Points gagnés moyen pour ce passage
				stateToConsider.obtainedPoints += 20;

				stateToConsider.robot.setLocomotionSpeed(speedBeforeScriptWasCalled);


			}

			else if (versionToExecute == 2)
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
				stateToConsider.robot.useActuator(ActuatorOrder.FISHING_POSITION_LOW, true);
				
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
				this.freeThem(stateToConsider);
				
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
				
				stateToConsider.robot.useActuator(ActuatorOrder.FISHING_POSITION_LOW, true);

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
				this.freeThem(stateToConsider);
				
				// On indique au robot que les poissons ne sont plus sur le bras
				stateToConsider.robot.setAreFishesOnBoard(false);
				
				// On indique que deux poissons en moyenne ont été pris
				stateToConsider.table.fishesFished+=2;
				
				// Points gagnés moyen pour ce passage
				stateToConsider.obtainedPoints += 20;

                stateToConsider.robot.moveLengthwise(100);
				
				stateToConsider.robot.setLocomotionSpeed(speedBeforeScriptWasCalled);
				
				
			}

			else if(versionToExecute == 4)
			{

				if(stateToConsider.robot.getContactSensorValue(ContactSensors.DOOR_BLOCKED))
                {
                    //if(!stateToConsider.robot.getContactSensorValue(ContactSensors.DOOR_OPENED))
                       // throw new ExecuteException(new BlockedActuatorException());


                    if(stateToConsider.robot.getContactSensorValue(ContactSensors.DOOR_BLOCKED))
                        throw new ExecuteException(new BlockedActuatorException());

                    stateToConsider.robot.useActuator(ActuatorOrder.CLOSE_DOOR, true);

                }

                if(!stateToConsider.robot.getContactSensorValue(ContactSensors.DOOR_CLOSED))
                    throw new ExecuteException(new BlockedActuatorException());

                // Vitesse rapide jusqu'au début de pêche
				Speed speedBeforeScriptWasCalled = stateToConsider.robot.getLocomotionSpeed();
				stateToConsider.robot.setLocomotionSpeed(Speed.SLOW_MEDIUM);

				stateToConsider.robot.setBasicDetection(true);

				// On commence à s'orienter pour le créneau près du bord
				//stateToConsider.robot.turn(Math.PI - 0.24);
				
				// Ajout d'un hook pour baisser le bras
				Hook arm = hookFactory.newXLesserHook(950);
				arm.addCallback(new Callback(new GetFish(),true,stateToConsider));
				hooksToConsider.add(arm);

                //On indique ques les poissons se trouvent sur le bras lors du déplacement de la moitié du bac
                Hook specialHook = hookFactory.newXLesserHook(900);
                specialHook.addCallback(new Callback(new SetFishesOnBoard(),true,stateToConsider));
                hooksToConsider.add(specialHook);

                // On crée le hook de position, l'action pour lever le bras et ajout à la liste
                Hook hook = hookFactory.newXLesserHook(650);
                hook.addCallback(new Callback(new RiseArm(), true, stateToConsider));
                hooksToConsider.add(hook);

                // On crée le hook de position, l'action pour lâcher les poissons et ajout à la liste
                Hook hook2 = hookFactory.newXLesserHook(400);
                hook2.addCallback(new Callback(new DropFish(), true, stateToConsider));
                hooksToConsider.add(hook2);

				try

				{
				// marche arrière pour se placer près du bac
				//stateToConsider.robot.moveLengthwise(-260, hooksToConsider, true);
				
				stateToConsider.robot.moveArc(new Arc(-470,467,-Math.PI/2,false),hooksToConsider);
				
				// reprise de vitesse medium
				stateToConsider.robot.setLocomotionSpeed(Speed.SLOW_ALL);

				// On s'oriente vers le côté ennemi
				stateToConsider.robot.turn((Math.PI), hooksToConsider, true);

				// On longe le bac avec gestion de blocage sur le bord du filet

					xBefore=stateToConsider.robot.getPosition().x;
					log.debug("Premier passage !");
					stateToConsider.robot.moveLengthwise(500, hooksToConsider, false);
				}
				catch(UnableToMoveException e)
				{
					if(e.reason == UnableToMoveReason.OBSTACLE_DETECTED)
					{
						log.debug("Ennemi détecté au premier passage ! Attente avant de progresser !");
						if(!waitForEnnemy(stateToConsider, stateToConsider.robot.getPosition(), true))
						{
							log.debug("Le salaud ne bouge pas : abort du premier passage !");
							hooksToConsider.clear();
							throw new UnableToMoveException(e.aim, UnableToMoveReason.OBSTACLE_DETECTED);
						}
						else
						{
							log.debug("Voie dégagée, on reprend le premier passage !");
							stateToConsider.robot.moveLengthwise(500-(xBefore-stateToConsider.robot.getPosition().x),hooksToConsider,false);
						}
					}
					else
					{
						log.debug("Bord du filet touché au premier passage ! Retrait puis dépose !");
						try
						{
							stateToConsider.robot.useActuator(ActuatorOrder.MIDDLE_POSITION, true);
                            stateToConsider.robot.setForceMovement(true);
							stateToConsider.robot.moveArc(new Arc(-400, -140, stateToConsider.robot.getOrientation(), false),hooksToConsider);
							stateToConsider.robot.turn(Math.PI-0.06,hooksToConsider,false);
                            stateToConsider.robot.setForceMovement(false);
                            stateToConsider.robot.moveLengthwise(500,hooksToConsider,false);
						}
						catch(Exception ex)
						{
							log.debug("Problème lors de la première dépose : \n" );
							ex.printStackTrace();
							throw ex;
						}
					}
				}
				
				// Points gagnés moyen pour ce passage
				stateToConsider.obtainedPoints += 20;

				// On indique que deux poissons en moyenne ont été pris
				stateToConsider.table.fishesFished+=2;
				
				// On indique au robot que les poissons ne sont plus sur le bras
				stateToConsider.robot.setAreFishesOnBoard(false);
				
				// On crée le hook de position pour prendre les poissons et ajout à la liste
				Hook hook3 = hookFactory.newXGreaterHook(600);
				hook3.addCallback(new Callback(new GetFish(), true, stateToConsider));
				hooksToConsider.add(hook3);

				// on repart chercher d'autre poissons rapidement 
				stateToConsider.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);
				log.debug("On part pour un second passage !");
				stateToConsider.robot.moveLengthwise(-500, hooksToConsider, false);
				stateToConsider.robot.setLocomotionSpeed(Speed.SLOW_ALL);
				
				// nouvelle condition pour le hook lâchant les poissons et mise à jour dans la liste
				hook2 = hookFactory.newXLesserHook(330);
				hook2.addCallback(new Callback(new DropFish(), true, stateToConsider));
				hooksToConsider.add(hook2);
				
				//Rajout du hook pour le booléen
				specialHook = hookFactory.newXLesserHook(900);
				specialHook.addCallback(new Callback(new SetFishesOnBoard(),true,stateToConsider));
				hooksToConsider.add(specialHook);
				
				// on longe le bac avec gestion de blocage
				try
				{
					xBefore=stateToConsider.robot.getPosition().x;
					log.debug("Second passage !");
					stateToConsider.robot.moveLengthwise(xBefore-netPosX, hooksToConsider, false);	
				}
				catch(UnableToMoveException e)
				{
					if(e.reason == UnableToMoveReason.OBSTACLE_DETECTED)
					{
						log.debug("Ennemi détecté au second passage ! Attente avant de progresser !");
						if(!waitForEnnemy(stateToConsider, stateToConsider.robot.getPosition(), true))
						{
							log.debug("Le salaud ne bouge pas : abort du second passage !");
							throw new UnableToMoveException(e.aim, UnableToMoveReason.OBSTACLE_DETECTED);
						}
						else
						{
							log.debug("Voie dégagée, on reprend le second passage !");
							stateToConsider.robot.moveLengthwise(netPosX-(xBefore-stateToConsider.robot.getPosition().x),hooksToConsider,false);
						}
					}
					else
					{
						log.debug("Bord du filet touché au second passage, tentative de dégagement !");
						try
						{
							if(stateToConsider.robot.getAreFishesOnBoard())
							{
								log.debug("Poissons à bord au second passage, on les garde !");
								stateToConsider.robot.useActuator(ActuatorOrder.MIDDLE_POSITION, true);
							}
							else
							{
								log.debug("Pas de poissons à bord au second passage, on sort !");
								stateToConsider.robot.useActuator(ActuatorOrder.ARM_INIT, true);
							}
							hooksToConsider.clear();
							stateToConsider.robot.moveArc(new Arc(-400, -300, stateToConsider.robot.getOrientation(), false),hooksToConsider);
						}
						catch(Exception ex)
						{
							log.debug("Problème lors du second passage :");
							ex.printStackTrace();
							throw ex;
						}
						log.debug("Lancement d'une exception pour remonter à l'IA !");
						throw new ExecuteException(new BlockedException());
					}
				}
				
				try
				{
					// relève du bras puis déplacement au dessus du filet
					stateToConsider.robot.useActuator(ActuatorOrder.MIDDLE_POSITION, true);
					xBefore=stateToConsider.robot.getPosition().x;
					stateToConsider.robot.moveLengthwise(340,hooksToConsider,false);
				}
				catch(UnableToMoveException e)
				{
					if(e.reason == UnableToMoveReason.OBSTACLE_DETECTED)
					{
						log.debug("Ennemi détecté au second passage ! Attente avant de progresser !");
						if(!waitForEnnemy(stateToConsider, stateToConsider.robot.getPosition(), true))
						{
							log.debug("Le salaud ne bouge pas : abort du second passage !");
							throw new UnableToMoveException(e.aim, UnableToMoveReason.OBSTACLE_DETECTED);
						}
						else
						{
							log.debug("Voie dégagée, reprise du second passage !");
							stateToConsider.robot.moveLengthwise(340-(xBefore-stateToConsider.robot.getPosition().x),hooksToConsider,true);
						}
					}
					else
					{
						log.debug("Bord du filet touché au second passage, tentative de dégagement !");
						try
						{
							if(stateToConsider.robot.getAreFishesOnBoard())
							{
								log.debug("Poissons à bord au second passage, on les garde !");
								stateToConsider.robot.useActuator(ActuatorOrder.MIDDLE_POSITION, true);
							}
							else
							{
								stateToConsider.robot.useActuator(ActuatorOrder.ARM_INIT, true);
							}
							hooksToConsider.clear();
							stateToConsider.robot.moveArc(new Arc(-400, -300, stateToConsider.robot.getOrientation(), false),hooksToConsider);
						}
						catch(Exception ex)
						{
							throw ex;
						}
						log.debug("Lancement d'une exception pour remonter à l'IA !");
						throw new ExecuteException(new BlockedException());
					}
				}
				
				// On indique au robot que les poissons ne sont plus sur le bras
				stateToConsider.robot.setAreFishesOnBoard(false);

				// On indique que deux poissons en moyenne ont été pris
				stateToConsider.table.fishesFished+=2;

				// Points gagnés moyen pour ce passage
				stateToConsider.obtainedPoints += 20;

				// On crée le hook de position pour prendre les poissons et ajout à la liste
                hook3 = hookFactory.newXGreaterHook(600);
				hook3.addCallback(new Callback(new GetFish(), true, stateToConsider));
				hooksToConsider.add(hook3);

				// on repart chercher d'autre poissons rapidement
				stateToConsider.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);
				log.debug("On part faire la troisième tentative !");
				stateToConsider.robot.moveLengthwise(-610, hooksToConsider, false);
				stateToConsider.robot.setLocomotionSpeed(Speed.SLOW_ALL);

				// nouvelle condition pour le hook lâchant les poissons et mise à jour dans la liste
				hook2 = hookFactory.newXLesserHook(330);
				hook2.addCallback(new Callback(new DropFish(), true, stateToConsider));
				hooksToConsider.add(hook2);

				//Rajout du hook pour le booléen
				specialHook = hookFactory.newXLesserHook(850);
				specialHook.addCallback(new Callback(new SetFishesOnBoard(),true,stateToConsider));
				hooksToConsider.add(specialHook);

				// on longe le bac avec gestion de blocage
				try
				{
					xBefore=stateToConsider.robot.getPosition().x;
					log.debug("Troisième tentative !");
					stateToConsider.robot.moveLengthwise(xBefore-netPosX, hooksToConsider, false);
				}
				catch(UnableToMoveException e)
				{
					if(e.reason == UnableToMoveReason.OBSTACLE_DETECTED)
					{
						log.debug("Ennemi détecté au troisième passage ! Attente avant de progresser !");
						if(!waitForEnnemy(stateToConsider, stateToConsider.robot.getPosition(), true))
						{
							log.debug("Le salaud ne bouge pas : abort du troisième passage !");
							throw new UnableToMoveException(e.aim, UnableToMoveReason.OBSTACLE_DETECTED);
						}
						else
						{
							log.debug("Voie dégagée, reprise du troisième passage !");
							stateToConsider.robot.moveLengthwise(netPosX-(xBefore-stateToConsider.robot.getPosition().x),hooksToConsider,false);
						}
					}
					else
					{
						log.debug("Bord du filet touché au troisième passage, tentative de dégagement !");
						try
						{
							if(stateToConsider.robot.getAreFishesOnBoard())
							{
								log.debug("Poissons à bord au troisime passage, on les garde !");
								stateToConsider.robot.useActuator(ActuatorOrder.MIDDLE_POSITION, true);
							}
							else
							{
								log.debug("Pas de poissons au troisème passage, on sort !");
								stateToConsider.robot.useActuator(ActuatorOrder.ARM_INIT, true);
							}
							hooksToConsider.clear();
							stateToConsider.robot.moveArc(new Arc(-400, -300, stateToConsider.robot.getOrientation(), false),hooksToConsider);
						}
						catch(Exception ex)
						{
							log.debug("Problème lord du troisième passage :");
							e.printStackTrace();
							throw ex;
						}
						log.debug("Lancement d'une exception pour remonter à l'IA !");
						throw new ExecuteException(new BlockedException());
					}
				}

				try
				{
					// relève du bras puis déplacement au dessus du filet
					stateToConsider.robot.useActuator(ActuatorOrder.MIDDLE_POSITION, true);
					xBefore=stateToConsider.robot.getPosition().x;
					stateToConsider.robot.moveLengthwise(350,hooksToConsider,false);
				}
				catch(UnableToMoveException e)
				{
					if(e.reason == UnableToMoveReason.OBSTACLE_DETECTED)
					{
						log.debug("Ennemi détecté au troisième passage ! Attente avant de progresser !");
						if(!waitForEnnemy(stateToConsider, stateToConsider.robot.getPosition(), true))
						{
							log.debug("Le salaud ne bouge pas : abort du troisième passage !");
							throw new UnableToMoveException(e.aim, UnableToMoveReason.OBSTACLE_DETECTED);
						}
						else
						{
							log.debug("Voie dégagée, reprise du troisième passage !");
							stateToConsider.robot.moveLengthwise(350-(xBefore-stateToConsider.robot.getPosition().x),hooksToConsider,true);
						}
					}
					else
					{
						log.debug("Bord du filet touché au troisième passage, tentative de dégagement !");
						try
						{
							if(stateToConsider.robot.getAreFishesOnBoard())
							{
								log.debug("Poissons à bord au troisième passage, on les garde !");
								stateToConsider.robot.useActuator(ActuatorOrder.MIDDLE_POSITION, true);
							}
							else
							{
								log.debug("Pas de poissons au troisième passage, on sort !");
								stateToConsider.robot.useActuator(ActuatorOrder.ARM_INIT, true);
							}
							hooksToConsider.clear();
							stateToConsider.robot.moveArc(new Arc(-400, -300, stateToConsider.robot.getOrientation(), false),hooksToConsider);
						}
						catch(Exception ex)
						{
							log.debug("Problème lors du troisième passage :");
							ex.printStackTrace();
							throw ex;
						}
						log.debug("Lancement d'une exception pour remonter à l'IA !");
						throw new ExecuteException(new BlockedException());
					}
				}

				// On indique au robot que les poissons ne sont plus sur le bras
				stateToConsider.robot.setAreFishesOnBoard(false);

				// On indique que deux poissons en moyenne ont été pris
				stateToConsider.table.fishesFished+=2;

				// Points gagnés moyen pour ce passage
				stateToConsider.obtainedPoints += 20;
				
			/*	// On crée le hook de position pour prendre les poissons et ajout à la liste
                hook3 = hookFactory.newXGreaterHook(600);
				hook3.addCallback(new Callback(new GetFish(), true, stateToConsider));
				hooksToConsider.add(hook3);

				// on repart chercher d'autre poissons rapidement
				stateToConsider.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);
				log.debug("On part faire la quatrième tentative !");
				stateToConsider.robot.moveLengthwise(-460, hooksToConsider, false);
				stateToConsider.robot.setLocomotionSpeed(Speed.SLOW_ALL);

				// nouvelle condition pour le hook lâchant les poissons et mise à jour dans la liste
				hook2 = hookFactory.newXLesserHook(330);
				hook2.addCallback(new Callback(new DropFish(), true, stateToConsider));
				hooksToConsider.add(hook2);

				//Rajout du hook pour le booléen
				specialHook = hookFactory.newXLesserHook(850);
				specialHook.addCallback(new Callback(new SetFishesOnBoard(),true,stateToConsider));
				hooksToConsider.add(specialHook);

				// on longe le bac avec gestion de blocage
				try
				{
					xBefore=stateToConsider.robot.getPosition().x;
					log.debug("Quatrième tentative !");
					stateToConsider.robot.moveLengthwise(xBefore-netPosX, hooksToConsider, false);
				}
				catch(UnableToMoveException e)
				{
					if(e.reason == UnableToMoveReason.OBSTACLE_DETECTED)
					{
						log.debug("Ennemi détecté au quatrième passage ! Attente avant de progresser !");
						if(!waitForEnnemy(stateToConsider, stateToConsider.robot.getPosition(), true))
						{
							log.debug("Le salaud ne bouge pas : abort du quatrième passage !");
							throw new UnableToMoveException(e.aim, UnableToMoveReason.OBSTACLE_DETECTED);
						}
						else
						{
							log.debug("Voie dégagée, reprise du quatrième passage !");
							stateToConsider.robot.moveLengthwise(netPosX-(xBefore-stateToConsider.robot.getPosition().x),hooksToConsider,false);
						}
					}
					else
					{
						log.debug("Bord du filet touché au quatrième passage, tentative de dégagement !");
						try
						{
							if(stateToConsider.robot.getAreFishesOnBoard())
							{
								log.debug("Poissons à bord au quatrième passage, on les garde !");
								stateToConsider.robot.useActuator(ActuatorOrder.MIDDLE_POSITION, true);
							}
							else
							{
								log.debug("Pas de poissons au quatrième passage, on sort !");
								stateToConsider.robot.useActuator(ActuatorOrder.ARM_INIT, true);
							}
							hooksToConsider.clear();
							stateToConsider.robot.moveArc(new Arc(-400, -300, stateToConsider.robot.getOrientation(), false),hooksToConsider);
						}
						catch(Exception ex)
						{
							log.debug("Problème lors du quatrième passage :");
							e.printStackTrace();
							throw ex;
						}
						log.debug("Lancement d'une exception pour remonter à l'IA !");
						throw new ExecuteException(new BlockedException());
					}
				}

				try
				{
					// relève du bras puis déplacement au dessus du filet
					stateToConsider.robot.useActuator(ActuatorOrder.MIDDLE_POSITION, true);
					xBefore=stateToConsider.robot.getPosition().x;
					stateToConsider.robot.moveLengthwise(300,hooksToConsider,false);
				}
				catch(UnableToMoveException e)
				{
					if(e.reason == UnableToMoveReason.OBSTACLE_DETECTED)
					{
						log.debug("Ennemi détecté au quatrième passage ! Attente avant de progresser !");
						if(!waitForEnnemy(stateToConsider, stateToConsider.robot.getPosition(), true))
						{
							log.debug("Le salaud ne bouge pas : abort du quatrième passage !");
							throw new UnableToMoveException(e.aim, UnableToMoveReason.OBSTACLE_DETECTED);
						}
						else
						{
							log.debug("Voie dégagée, reprise du quatrième passage !");
							stateToConsider.robot.moveLengthwise(300-(xBefore-stateToConsider.robot.getPosition().x),hooksToConsider,true);
						}
					}
					else
					{
						log.debug("Bord du filet touché au quatrième passage, tentative de dégagement !");
						try
						{
							if(stateToConsider.robot.getAreFishesOnBoard())
							{
								log.debug("Poissons à bord au quatrième passage, on les garde !");
								stateToConsider.robot.useActuator(ActuatorOrder.MIDDLE_POSITION, true);
							}
							else
							{
								log.debug("Pas de poissons au quatrième passage, on sort !");
								stateToConsider.robot.useActuator(ActuatorOrder.ARM_INIT, true);
							}
							hooksToConsider.clear();
							stateToConsider.robot.moveArc(new Arc(-400, -300, stateToConsider.robot.getOrientation(), false),hooksToConsider);
						}
						catch(Exception ex)
						{
							log.debug("Problème lors du troisième passage :");
							ex.printStackTrace();
							throw ex;
						}
						log.debug("Lancement d'une exception pour remonter à l'IA !");
						throw new ExecuteException(new BlockedException());
					}
				}

				// On indique au robot que les poissons ne sont plus sur le bras
				stateToConsider.robot.setAreFishesOnBoard(false);

				// On indique que deux poissons en moyenne ont été pris
				stateToConsider.table.fishesFished+=2;

				// Points gagnés moyen pour ce passage
				stateToConsider.obtainedPoints += 20;

                hook3 = hookFactory.newXGreaterHook(600);
                hook3.addCallback(new Callback(new GetFish(), true, stateToConsider));
                hooksToConsider.add(hook3);

                // on repart chercher d'autre poissons rapidement
                stateToConsider.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);
                log.debug("On part faire la cinquième tentative !");
                stateToConsider.robot.moveLengthwise(-460, hooksToConsider, false);
                stateToConsider.robot.setLocomotionSpeed(Speed.SLOW_ALL);

                // nouvelle condition pour le hook lâchant les poissons et mise à jour dans la liste
                hook2 = hookFactory.newXLesserHook(330);
                hook2.addCallback(new Callback(new DropFish(), true, stateToConsider));
                hooksToConsider.add(hook2);

                //Rajout du hook pour le booléen
                specialHook = hookFactory.newXLesserHook(850);
                specialHook.addCallback(new Callback(new SetFishesOnBoard(),true,stateToConsider));
                hooksToConsider.add(specialHook);

                // on longe le bac avec gestion de blocage
                try
                {
                    xBefore=stateToConsider.robot.getPosition().x;
                    log.debug("Cinquième tentative !");
                    stateToConsider.robot.moveLengthwise(xBefore-netPosX, hooksToConsider, false);
                }
                catch(UnableToMoveException e)
                {
                    if(e.reason == UnableToMoveReason.OBSTACLE_DETECTED)
                    {
                        log.debug("Ennemi détecté au cinquième passage ! Attente avant de progresser !");
                        if(!waitForEnnemy(stateToConsider, stateToConsider.robot.getPosition(), true))
                        {
                            log.debug("Le salaud ne bouge pas : abort du cinquième passage !");
                            throw new UnableToMoveException(e.aim, UnableToMoveReason.OBSTACLE_DETECTED);
                        }
                        else
                        {
                            log.debug("Voie dégagée, reprise du cinquième passage !");
                            stateToConsider.robot.moveLengthwise(netPosX-(xBefore-stateToConsider.robot.getPosition().x),hooksToConsider,false);
                        }
                    }
                    else
                    {
                        log.debug("Bord du filet touché au cinquième passage, tentative de dégagement !");
                        try
                        {
                            if(stateToConsider.robot.getAreFishesOnBoard())
                            {
                                log.debug("Poissons à bord au cinquième passage, on les garde !");
                                stateToConsider.robot.useActuator(ActuatorOrder.MIDDLE_POSITION, true);
                            }
                            else
                            {
                                log.debug("Pas de poissons au cinquième passage, on sort !");
                                stateToConsider.robot.useActuator(ActuatorOrder.ARM_INIT, true);
                            }
                            hooksToConsider.clear();
                            stateToConsider.robot.moveArc(new Arc(-400, -300, stateToConsider.robot.getOrientation(), false),hooksToConsider);
                        }
                        catch(Exception ex)
                        {
                            log.debug("Problème lors du cinquième passage :");
                            e.printStackTrace();
                            throw ex;
                        }
                        log.debug("Lancement d'une exception pour remonter à l'IA !");
                        throw new ExecuteException(new BlockedException());
                    }
                }

                try
                {
                    // relève du bras puis déplacement au dessus du filet
                    stateToConsider.robot.useActuator(ActuatorOrder.MIDDLE_POSITION, true);
                    xBefore=stateToConsider.robot.getPosition().x;
                    stateToConsider.robot.moveLengthwise(300,hooksToConsider,false);
                }
                catch(UnableToMoveException e)
                {
                    if(e.reason == UnableToMoveReason.OBSTACLE_DETECTED)
                    {
                        log.debug("Ennemi détecté au cinquième passage ! Attente avant de progresser !");
                        if(!waitForEnnemy(stateToConsider, stateToConsider.robot.getPosition(), true))
                        {
                            log.debug("Le salaud ne bouge pas : abort du cinquième passage !");
                            throw new UnableToMoveException(e.aim, UnableToMoveReason.OBSTACLE_DETECTED);
                        }
                        else
                        {
                            log.debug("Voie dégagée, reprise du quatrième passage !");
                            stateToConsider.robot.moveLengthwise(300-(xBefore-stateToConsider.robot.getPosition().x),hooksToConsider,true);
                        }
                    }
                    else
                    {
                        log.debug("Bord du filet touché au cinquième passage, tentative de dégagement !");
                        try
                        {
                            if(stateToConsider.robot.getAreFishesOnBoard())
                            {
                                log.debug("Poissons à bord au cinquième passage, on les garde !");
                                stateToConsider.robot.useActuator(ActuatorOrder.MIDDLE_POSITION, true);
                            }
                            else
                            {
                                log.debug("Pas de poissons au cinquième passage, on sort !");
                                stateToConsider.robot.useActuator(ActuatorOrder.ARM_INIT, true);
                            }
                            hooksToConsider.clear();
                            stateToConsider.robot.moveArc(new Arc(-400, -300, stateToConsider.robot.getOrientation(), false),hooksToConsider);
                        }
                        catch(Exception ex)
                        {
                            log.debug("Problème lors du troisième passage :");
                            ex.printStackTrace();
                            throw ex;
                        }
                        log.debug("Lancement d'une exception pour remonter à l'IA !");
                        throw new ExecuteException(new BlockedException());
                    }
                }

                // On indique au robot que les poissons ne sont plus sur le bras
                stateToConsider.robot.setAreFishesOnBoard(false);
*/
				// arc pour sortir du bord de table
				log.debug("Fishing terminé sans encombres, dégagement !");
				stateToConsider.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);
				Arc disengage = new Arc(-320,160,stateToConsider.robot.getOrientation(),false);
				stateToConsider.robot.moveArc(disengage, hooksToConsider);
                stateToConsider.robot.moveLengthwiseWithoutDetection(50);
				hooksToConsider.clear();

				// reprise de vitesse inter script
				stateToConsider.robot.setLocomotionSpeed(speedBeforeScriptWasCalled);
			}

			else if (versionToExecute == 3)
			{
				// On prend une vitesse lente pour que les aimants puissent récupérer les poissons
				Speed speedBeforeScriptWasCalled = stateToConsider.robot.getLocomotionSpeed();
				stateToConsider.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);
				stateToConsider.robot.setBasicDetection(true);

				
				// On commence à s'orienter pour le créneau près du bord
				//stateToConsider.robot.turn(Math.PI - 0.24);
				
				// Création du hook de position pour baisser le bras à l'arrivée du robot, puis ajout dans la liste de hooks
				Hook hook1 = hookFactory.newXLesserHook(980);
				hook1.addCallback(new Callback(new GetFish(), true, stateToConsider));
				hooksToConsider.add(hook1);

				stateToConsider.robot.setLocomotionSpeed(Speed.SLOW_ALL);

				// marche arrière pour se placer près du bac
				//stateToConsider.robot.moveLengthwise(-248, hooksToConsider, true);

				stateToConsider.robot.moveArc(new Arc(-440,467,-Math.PI/2,false),hooksToConsider);

				// reprise de vitesse medium
				//stateToConsider.robot.setLocomotionSpeed(Speed.SLOW_ALL);

				// On s'oriente vers le côté ennemi
				stateToConsider.robot.turn((Math.PI-0.04), hooksToConsider, false);
				
				// On crée le hook de position pour lever le bras près du filet, puis ajout à la liste de hooks
				Hook hook2 = hookFactory.newXLesserHook(680);
				hook2.addCallback(new Callback(new RiseArm(), true, stateToConsider));
				hooksToConsider.add(hook2);

				// On crée le hook de position pour lâcher les poissons, puis ajout à la liste
				Hook hook3 = hookFactory.newXLesserHook(380);
				hook3.addCallback(new Callback(new DropFish(), true, stateToConsider));
				hooksToConsider.add(hook3);
				
				//Rajout du hook pour le booléen
				Hook specialHook = hookFactory.newXLesserHook(900);
				specialHook.addCallback(new Callback(new SetFishesOnBoard(),true,stateToConsider));
				hooksToConsider.add(specialHook);

				// On longe le bac avec gestion de blocage sur le bord du filet
				try
				{
					//On retient la coordonnée x d'avant mouvement en cas d'obstacle
					xBefore = stateToConsider.robot.getPosition().x;
					
					log.debug("Premier passage !");
					stateToConsider.robot.moveLengthwise(530, hooksToConsider, false);
				}
				catch(UnableToMoveException e)
				{
					if(e.reason == UnableToMoveReason.OBSTACLE_DETECTED)
					{
						log.debug("Ennemi détecté ! Attente avant de progresser !");
						if(!waitForEnnemy(stateToConsider, stateToConsider.robot.getPosition(), true))
						{
							log.debug("Le salaud ne bouge pas : abort !");
							hooksToConsider.clear();
							throw e;
						}
						else
						{
							log.debug("Voie dégagée, reprise de mouvement !");
							stateToConsider.robot.moveLengthwise(530-(xBefore-stateToConsider.robot.getPositionFast().x),hooksToConsider,false);
						}
					}
					else
					{
						log.debug("Bord du filet touché !");
						hooksToConsider.clear();
						try
						{
							if(stateToConsider.robot.getAreFishesOnBoard())
							{
								log.debug("Poisson à bord, on les garde !");
								stateToConsider.robot.useActuator(ActuatorOrder.MIDDLE_POSITION, true);
							}
							else
							{
								log.debug("Aucun poisson à bord, rangement des bras !");
								stateToConsider.robot.useActuator(ActuatorOrder.ARM_INIT, true);
							}
							log.debug("Tentative de dégagement !");
							if(stateToConsider.robot.getOrientation()<=Math.PI && stateToConsider.robot.getOrientationFast()>=0)
							{
								stateToConsider.robot.moveArc(new Arc(-400,300,stateToConsider.robot.getOrientationFast(),false),hooksToConsider);
							}
							else
							{
								stateToConsider.robot.moveArc(new Arc(-400, -300, stateToConsider.robot.getOrientationFast(), false),hooksToConsider);
							}
						}
						catch(Exception ex)
						{
							log.debug("Problème lors du dégagement : " + ex );
						}
						throw e;
					}
				}

				// Points gagnés moyen pour ce passage
				stateToConsider.obtainedPoints += 10;

				// On indique qu'un poisson en moyenne a été pris
				stateToConsider.table.fishesFished+=1;
				
				// On indique au robot que les poissons ne sont plus sur le bras
				stateToConsider.robot.setAreFishesOnBoard(false);
				
				// On modifie la condition du premier hook, même action, puis mise à jour dans la liste
				hook1 = hookFactory.newXGreaterHook(700);
				hook1.addCallback(new Callback(new GetFish(), true, stateToConsider));
				hooksToConsider.add(hook1);

				// on repart chercher d'autre poissons
				stateToConsider.robot.setLocomotionSpeed(Speed.FAST_ALL);
				try
				{
					log.debug("On part chercher d'autres poissons !");
					stateToConsider.robot.moveLengthwise(-500, hooksToConsider, false);
				}
				catch(Exception e)
				{
					log.debug("Filet percuté !");
					hooksToConsider.clear();
					try
					{
						log.debug("Tentative de dégagement !");
						if(stateToConsider.robot.getOrientation()<=Math.PI && stateToConsider.robot.getOrientationFast()>=0)
						{
							stateToConsider.robot.moveArc(new Arc(-600,200,stateToConsider.robot.getOrientationFast(),false),hooksToConsider);
						}
						else
						{
							stateToConsider.robot.moveArc(new Arc(-600, 200, stateToConsider.robot.getOrientation(), false),hooksToConsider);
						}
					}
					catch(Exception ex)
					{
						log.debug("Impossible de sortir du bord de table : " + ex);
					}
					throw e;
				}
				stateToConsider.robot.setLocomotionSpeed(Speed.SLOW_ALL);

				// nouvelle condition pour le hook lâchant les poissons, même action, mise à jour dans la liste
				hook3 = hookFactory.newXLesserHook(350);
				hook3.addCallback(new Callback(new DropFish(), true, stateToConsider));
				hooksToConsider.add(hook3);
				
				//Rajout du hook pour le booléen
				specialHook = hookFactory.newXLesserHook(900);
				specialHook.addCallback(new Callback(new SetFishesOnBoard(),true,stateToConsider));
				hooksToConsider.add(specialHook);
				
				// on longe le bac avec gestion de blocage
				try
				{
					xBefore=stateToConsider.robot.getPosition().x;
					log.debug("Second passage !");
					stateToConsider.robot.moveLengthwise(xBefore-netPosX, hooksToConsider, false);
				}
				catch(UnableToMoveException e)
				{
					if(e.reason == UnableToMoveReason.OBSTACLE_DETECTED)
					{
						log.debug("Ennemi détecté ! Attente avant de progresser !");
						if(!waitForEnnemy(stateToConsider, stateToConsider.robot.getPosition(), true))
						{
							log.debug("Le salaud ne bouge pas : abort !");
							hooksToConsider.clear();
							throw e;
						}
						else
						{
							log.debug("Voie dégagée, reprise de script !");
							stateToConsider.robot.moveLengthwise(300-(xBefore-stateToConsider.robot.getPosition().x),hooksToConsider,false);
						}
					}
					else
					{
						log.debug("Bord du filet touché !");
						try
						{
							hooksToConsider.clear();
							if(stateToConsider.robot.getAreFishesOnBoard())
							{
								log.debug("Poisson à bord, on les garde !");
								stateToConsider.robot.useActuator(ActuatorOrder.MIDDLE_POSITION, true);
							}
							else
							{
								log.debug("Pas de poisson à bord !");
								stateToConsider.robot.useActuator(ActuatorOrder.ARM_INIT, true);
							}
							log.debug("Dégagement !");
							if(stateToConsider.robot.getOrientation()<=Math.PI && stateToConsider.robot.getOrientationFast()>=0)
							{
								stateToConsider.robot.moveArc(new Arc(-400,300,stateToConsider.robot.getOrientationFast(),false),hooksToConsider);
							}
							else
							{
								stateToConsider.robot.moveArc(new Arc(-400, -300, stateToConsider.robot.getOrientationFast(), false),hooksToConsider);
							}
						}
						catch(Exception ex)
						{
							log.debug("Problème lors du dégagement : " + ex);
						}
					}
					throw e;
				}
				
				try
				{
					xBefore=stateToConsider.robot.getPosition().x;
					stateToConsider.robot.useActuator(ActuatorOrder.MIDDLE_POSITION, true);
					log.debug("Dépose de poisson !");
					stateToConsider.robot.moveLengthwise(310,hooksToConsider,false);
				}
				catch(UnableToMoveException e)
				{
					if(e.reason == UnableToMoveReason.OBSTACLE_DETECTED)
					{
						log.debug("Ennemi détecté ! Attente avant de progresser !");
						if(!waitForEnnemy(stateToConsider, stateToConsider.robot.getPosition(), true))
						{
							log.debug("Le salaud ne bouge pas : abort !");
							hooksToConsider.clear();
							throw e;
						}
						else
						{
							log.debug("Voie dégagée, reprise de script !");
							stateToConsider.robot.moveLengthwise(310-(xBefore-stateToConsider.robot.getPosition().x),hooksToConsider,false);
						}
					}
					else
					{
						log.debug("Bord du filet touché !");
						try
						{
							hooksToConsider.clear();
							if(stateToConsider.robot.getAreFishesOnBoard())
							{
								log.debug("Poisson à bord, on les garde !");
								stateToConsider.robot.useActuator(ActuatorOrder.MIDDLE_POSITION, true);
							}
							else
							{
								log.debug("Pas de poissons à bord !");
								stateToConsider.robot.useActuator(ActuatorOrder.ARM_INIT, true);
							}
							log.debug("Dégagement !");
							if(stateToConsider.robot.getOrientation()<=Math.PI && stateToConsider.robot.getOrientationFast()>=0)
							{
								stateToConsider.robot.moveArc(new Arc(-400,300,stateToConsider.robot.getOrientationFast(),false),hooksToConsider);
							}
							else
							{
								stateToConsider.robot.moveArc(new Arc(-400, -300, stateToConsider.robot.getOrientationFast(), false),hooksToConsider);
							}
						}
						catch(Exception ex)
						{
							log.debug("Problème lors du dégagement : " + ex);
						}
						throw e;
					}
				}
				
				// On indique au robot que les poissons ne sont plus sur le bras
				stateToConsider.robot.setAreFishesOnBoard(false);

				// On indique que deux poissons en moyenne ont été pris
				stateToConsider.table.fishesFished+=2;

				// Points gagnés moyen pour ce passage
				stateToConsider.obtainedPoints += 20;
				
				// On modifie la condition du premier hook, même action, puis mise à jour dans la liste
				hook1 = hookFactory.newXGreaterHook(600);
				hook1.addCallback(new Callback(new GetFish(), true, stateToConsider));
				hooksToConsider.add(hook1);

				// on repart chercher d'autre poissons
				stateToConsider.robot.setLocomotionSpeed(Speed.FAST_ALL);
				try
				{
					log.debug("On repart chercher des poissons !");
					stateToConsider.robot.moveLengthwise(-520, hooksToConsider, false);
				}
				catch(Exception e)
				{
					log.debug("Filet percuté en allant chercher d'autre poissons !");
					hooksToConsider.clear();
					try
					{
						log.debug("Tentative de dégagement !");
						if(stateToConsider.robot.getOrientation()<=Math.PI && stateToConsider.robot.getOrientationFast()>=0)
						{
							stateToConsider.robot.moveArc(new Arc(-600,200,stateToConsider.robot.getOrientationFast(),false),hooksToConsider);
						}
						else
						{
							stateToConsider.robot.moveArc(new Arc(-600, -200, stateToConsider.robot.getOrientation(), false),hooksToConsider);
						}
					}
					catch(Exception ex)
					{
						log.debug("Impossible de sortir du bord de table : " +ex);
					}
					throw e;
				}
				stateToConsider.robot.setLocomotionSpeed(Speed.SLOW_ALL);

				// nouvelle condition pour le hook lâchant les poissons, même action, mise à jour dans la liste
				hook3 = hookFactory.newXLesserHook(350);
				hook3.addCallback(new Callback(new DropFish(), true, stateToConsider));
				hooksToConsider.add(hook3);
				
				//Rajout du hook pour le booléen
				specialHook = hookFactory.newXLesserHook(900);
				specialHook.addCallback(new Callback(new SetFishesOnBoard(),true,stateToConsider));
				hooksToConsider.add(specialHook);
				
				// on longe le bac avec gestion de blocage
				try
				{	
					xBefore=stateToConsider.robot.getPosition().x;
					log.debug("Troisième passage !");
					stateToConsider.robot.moveLengthwise(xBefore-netPosX, hooksToConsider, false);
				}
				catch(UnableToMoveException e)
				{
					if(e.reason == UnableToMoveReason.OBSTACLE_DETECTED)
					{
						log.debug("Ennemi détecté ! Attente avant de progresser !");
						if(!waitForEnnemy(stateToConsider, stateToConsider.robot.getPosition(), true))
						{
							log.debug("Le salaud ne bouge pas : abort !");
							hooksToConsider.clear();
							throw e;
						}
						else
						{
							log.debug("Voie dégagée, reprise de script !");
							stateToConsider.robot.moveLengthwise(290-(xBefore-stateToConsider.robot.getPosition().x),hooksToConsider,false);
						}
					}
					else
					{
						log.debug("Bord du filet touché !");
						try
						{
							hooksToConsider.clear();
							if(stateToConsider.robot.getAreFishesOnBoard())
							{
								log.debug("Poisson à bord, on les garde !");
								stateToConsider.robot.useActuator(ActuatorOrder.MIDDLE_POSITION, true);
							}
							else
							{
								log.debug("Pas de poissons à bord !");
								stateToConsider.robot.useActuator(ActuatorOrder.ARM_INIT, true);
							}
							log.debug("Dégagement !");
							if(stateToConsider.robot.getOrientation()<=Math.PI && stateToConsider.robot.getOrientationFast()>=0)
							{
								stateToConsider.robot.moveArc(new Arc(-400,300,stateToConsider.robot.getOrientationFast(),false),hooksToConsider);
							}
							else
							{
								stateToConsider.robot.moveArc(new Arc(-400, -300, stateToConsider.robot.getOrientationFast(), false),hooksToConsider);
							}
						}
						catch(Exception ex)
						{
							log.debug("Problème lors du dégagement : " + ex);
						}
						throw e;
					}
				}
				
				try
				{
					xBefore=stateToConsider.robot.getPosition().x;
					stateToConsider.robot.useActuator(ActuatorOrder.MIDDLE_POSITION, true);
					log.debug("Dépose de poissons !");
					stateToConsider.robot.moveLengthwise(310,hooksToConsider,false);
				}
				catch(UnableToMoveException e)
				{
					if(e.reason == UnableToMoveReason.OBSTACLE_DETECTED)
					{
						log.debug("Ennemi détecté ! Attente avant de progresser !");
						if(!waitForEnnemy(stateToConsider, stateToConsider.robot.getPosition(), true))
						{
							log.debug("Le salaud ne bouge pas : abort !");
							hooksToConsider.clear();
							throw e;
						}
						else
						{
							log.debug("Voie dégagée, reprise de script !");
							stateToConsider.robot.moveLengthwise(310-(xBefore-stateToConsider.robot.getPosition().x),hooksToConsider,false);
						}
					}
					else
					{
						log.debug("Bord du filet touché !");
						try
						{
							hooksToConsider.clear();
							if(stateToConsider.robot.getAreFishesOnBoard())
							{
								log.debug("Poisson à bord, on les garde !");
								stateToConsider.robot.useActuator(ActuatorOrder.MIDDLE_POSITION, true);
							}
							else
							{
								log.debug("Pas de poissons à bord !");
								stateToConsider.robot.useActuator(ActuatorOrder.ARM_INIT, true);
							}
							log.debug("Tentative de dégagement !");
							if(stateToConsider.robot.getOrientation()<=Math.PI && stateToConsider.robot.getOrientationFast()>=0)
							{
								stateToConsider.robot.moveArc(new Arc(-400,300,stateToConsider.robot.getOrientationFast(),false),hooksToConsider);
							}
							else
							{
								stateToConsider.robot.moveArc(new Arc(-400, -300, stateToConsider.robot.getOrientationFast(), false),hooksToConsider);
							}
						}
						catch(Exception ex)
						{
							log.debug("Problème lors du dégagement : " + ex);
						}
						throw e;
					}
				}
				
				// On indique au robot que les poissons ne sont plus sur le bras
				stateToConsider.robot.setAreFishesOnBoard(false);

				// On indique qu'un poisson en moyenne a été pris
				stateToConsider.table.fishesFished+=1;

				// Points gagnés moyen pour ce passage
				stateToConsider.obtainedPoints += 10;
				
				// On modifie la condition du premier hook, même action, puis mise à jour dans la liste
				hook1 = hookFactory.newXGreaterHook(600);
				hook1.addCallback(new Callback(new GetFish(), true, stateToConsider));
				hooksToConsider.add(hook1);

				// on repart chercher d'autre poissons
				stateToConsider.robot.setLocomotionSpeed(Speed.FAST_ALL);
				try
				{
					log.debug("On repart chercher des poissons !");
					stateToConsider.robot.moveLengthwise(-520, hooksToConsider, false);
				}
				catch(Exception e)
				{
					log.debug("Filet percuté en allant chercher d'autre poissons !");
					hooksToConsider.clear();
					try
					{
						log.debug("Tentative de dégagement !");
						if(stateToConsider.robot.getOrientation()<=Math.PI && stateToConsider.robot.getOrientationFast()>=0)
						{
							stateToConsider.robot.moveArc(new Arc(-600,200,stateToConsider.robot.getOrientationFast(),false),hooksToConsider);
						}
						else
						{
							stateToConsider.robot.moveArc(new Arc(-600, -200, stateToConsider.robot.getOrientation(), false),hooksToConsider);
						}
					}
					catch(Exception ex)
					{
						log.debug("Impossible de sortir du bord de table : " +ex);
					}
					throw e;
				}
				stateToConsider.robot.setLocomotionSpeed(Speed.SLOW_ALL);

				// nouvelle condition pour le hook lâchant les poissons, même action, mise à jour dans la liste
				hook3 = hookFactory.newXLesserHook(350);
				hook3.addCallback(new Callback(new DropFish(), true, stateToConsider));
				hooksToConsider.add(hook3);
				
				//Rajout du hook pour le booléen
				specialHook = hookFactory.newXLesserHook(900);
				specialHook.addCallback(new Callback(new SetFishesOnBoard(),true,stateToConsider));
				hooksToConsider.add(specialHook);
				
				// on longe le bac avec gestion de blocage
				try
				{	
					xBefore=stateToConsider.robot.getPosition().x;
					log.debug("Quatrième passage !");
					stateToConsider.robot.moveLengthwise(xBefore-netPosX, hooksToConsider, false);
				}
				catch(UnableToMoveException e)
				{
					if(e.reason == UnableToMoveReason.OBSTACLE_DETECTED)
					{
						log.debug("Ennemi détecté ! Attente avant de progresser !");
						if(!waitForEnnemy(stateToConsider, stateToConsider.robot.getPosition(), true))
						{
							log.debug("Le salaud ne bouge pas : abort !");
							hooksToConsider.clear();
							throw e;
						}
						else
						{
							log.debug("Voie dégagée, reprise de script !");
							stateToConsider.robot.moveLengthwise(270-(xBefore-stateToConsider.robot.getPosition().x),hooksToConsider,false);
						}
					}
					else
					{
						log.debug("Bord du filet touché !");
						try
						{
							hooksToConsider.clear();
							if(stateToConsider.robot.getAreFishesOnBoard())
							{
								log.debug("Poisson à bord, on les garde !");
								stateToConsider.robot.useActuator(ActuatorOrder.MIDDLE_POSITION, true);
							}
							else
							{
								log.debug("Pas de poissons à bord !");
								stateToConsider.robot.useActuator(ActuatorOrder.ARM_INIT, true);
							}
							log.debug("Dégagement !");
							if(stateToConsider.robot.getOrientation()<=Math.PI && stateToConsider.robot.getOrientationFast()>=0)
							{
								stateToConsider.robot.moveArc(new Arc(-400,300,stateToConsider.robot.getOrientationFast(),false),hooksToConsider);
							}
							else
							{
								stateToConsider.robot.moveArc(new Arc(-400, -300, stateToConsider.robot.getOrientationFast(), false),hooksToConsider);
							}
						}
						catch(Exception ex)
						{
							log.debug("Problème lors du dégagement : " + ex);
						}
						throw e;
					}
				}
				
				try
				{
					xBefore=stateToConsider.robot.getPosition().x;
					stateToConsider.robot.useActuator(ActuatorOrder.MIDDLE_POSITION, true);
					log.debug("Dépose de poissons !");
					stateToConsider.robot.moveLengthwise(310,hooksToConsider,false);
				}
				catch(UnableToMoveException e)
				{
					if(e.reason == UnableToMoveReason.OBSTACLE_DETECTED)
					{
						log.debug("Ennemi détecté ! Attente avant de progresser !");
						if(!waitForEnnemy(stateToConsider, stateToConsider.robot.getPosition(), true))
						{
							log.debug("Le salaud ne bouge pas : abort !");
							hooksToConsider.clear();
							throw e;
						}
						else
						{
							log.debug("Voie dégagée, reprise de script !");
							stateToConsider.robot.moveLengthwise(310-(xBefore-stateToConsider.robot.getPosition().x),hooksToConsider,false);
						}
					}
					else
					{
						log.debug("Bord du filet touché !");
						try
						{
							hooksToConsider.clear();
							if(stateToConsider.robot.getAreFishesOnBoard())
							{
								log.debug("Poisson à bord, on les garde !");
								stateToConsider.robot.useActuator(ActuatorOrder.MIDDLE_POSITION, true);
							}
							else
							{
								log.debug("Pas de poissons à bord !");
								stateToConsider.robot.useActuator(ActuatorOrder.ARM_INIT, true);
							}
							log.debug("Tentative de dégagement !");
							if(stateToConsider.robot.getOrientation()<=Math.PI && stateToConsider.robot.getOrientationFast()>=0)
							{
								stateToConsider.robot.moveArc(new Arc(-400,300,stateToConsider.robot.getOrientationFast(),false),hooksToConsider);
							}
							else
							{
								stateToConsider.robot.moveArc(new Arc(-400, -300, stateToConsider.robot.getOrientationFast(), false),hooksToConsider);
							}
						}
						catch(Exception ex)
						{
							log.debug("Problème lors du dégagement : " + ex);
						}
						throw e;
					}
				}
				
				// On indique au robot que les poissons ne sont plus sur le bras
				stateToConsider.robot.setAreFishesOnBoard(false);

                // On modifie la condition du premier hook, même action, puis mise à jour dans la liste
                hook1 = hookFactory.newXGreaterHook(600);
                hook1.addCallback(new Callback(new GetFish(), true, stateToConsider));
                hooksToConsider.add(hook1);

                // on repart chercher d'autre poissons
                stateToConsider.robot.setLocomotionSpeed(Speed.FAST_ALL);
                try
                {
                    log.debug("On repart chercher des poissons !");
                    stateToConsider.robot.moveLengthwise(-520, hooksToConsider, false);
                }
                catch(Exception e)
                {
                    log.debug("Filet percuté en allant chercher d'autre poissons !");
                    hooksToConsider.clear();
                    try
                    {
                        log.debug("Tentative de dégagement !");
                        if(stateToConsider.robot.getOrientation()<=Math.PI && stateToConsider.robot.getOrientationFast()>=0)
                        {
                            stateToConsider.robot.moveArc(new Arc(-600,200,stateToConsider.robot.getOrientationFast(),false),hooksToConsider);
                        }
                        else
                        {
                            stateToConsider.robot.moveArc(new Arc(-600, -200, stateToConsider.robot.getOrientation(), false),hooksToConsider);
                        }
                    }
                    catch(Exception ex)
                    {
                        log.debug("Impossible de sortir du bord de table : " +ex);
                    }
                    throw e;
                }
                stateToConsider.robot.setLocomotionSpeed(Speed.SLOW_ALL);

                // nouvelle condition pour le hook lâchant les poissons, même action, mise à jour dans la liste
                hook3 = hookFactory.newXLesserHook(350);
                hook3.addCallback(new Callback(new DropFish(), true, stateToConsider));
                hooksToConsider.add(hook3);

                //Rajout du hook pour le booléen
                specialHook = hookFactory.newXLesserHook(900);
                specialHook.addCallback(new Callback(new SetFishesOnBoard(),true,stateToConsider));
                hooksToConsider.add(specialHook);

                // on longe le bac avec gestion de blocage
                try
                {
                    xBefore=stateToConsider.robot.getPosition().x;
                    log.debug("Cinquième passage !");
                    stateToConsider.robot.moveLengthwise(xBefore-netPosX, hooksToConsider, false);
                }
                catch(UnableToMoveException e)
                {
                    if(e.reason == UnableToMoveReason.OBSTACLE_DETECTED)
                    {
                        log.debug("Ennemi détecté ! Attente avant de progresser !");
                        if(!waitForEnnemy(stateToConsider, stateToConsider.robot.getPosition(), true))
                        {
                            log.debug("Le salaud ne bouge pas : abort !");
                            hooksToConsider.clear();
                            throw e;
                        }
                        else
                        {
                            log.debug("Voie dégagée, reprise de script !");
                            stateToConsider.robot.moveLengthwise(270-(xBefore-stateToConsider.robot.getPosition().x),hooksToConsider,false);
                        }
                    }
                    else
                    {
                        log.debug("Bord du filet touché !");
                        try
                        {
                            hooksToConsider.clear();
                            if(stateToConsider.robot.getAreFishesOnBoard())
                            {
                                log.debug("Poisson à bord, on les garde !");
                                stateToConsider.robot.useActuator(ActuatorOrder.MIDDLE_POSITION, true);
                            }
                            else
                            {
                                log.debug("Pas de poissons à bord !");
                                stateToConsider.robot.useActuator(ActuatorOrder.ARM_INIT, true);
                            }
                            log.debug("Dégagement !");
                            if(stateToConsider.robot.getOrientation()<=Math.PI && stateToConsider.robot.getOrientationFast()>=0)
                            {
                                stateToConsider.robot.moveArc(new Arc(-400,300,stateToConsider.robot.getOrientationFast(),false),hooksToConsider);
                            }
                            else
                            {
                                stateToConsider.robot.moveArc(new Arc(-400, -300, stateToConsider.robot.getOrientationFast(), false),hooksToConsider);
                            }
                        }
                        catch(Exception ex)
                        {
                            log.debug("Problème lors du dégagement : " + ex);
                        }
                        throw e;
                    }
                }

                try
                {
                    xBefore=stateToConsider.robot.getPosition().x;
                    stateToConsider.robot.useActuator(ActuatorOrder.MIDDLE_POSITION, true);
                    log.debug("Dépose de poissons !");
                    stateToConsider.robot.moveLengthwise(310,hooksToConsider,false);
                }
                catch(UnableToMoveException e)
                {
                    if(e.reason == UnableToMoveReason.OBSTACLE_DETECTED)
                    {
                        log.debug("Ennemi détecté ! Attente avant de progresser !");
                        if(!waitForEnnemy(stateToConsider, stateToConsider.robot.getPosition(), true))
                        {
                            log.debug("Le salaud ne bouge pas : abort !");
                            hooksToConsider.clear();
                            throw e;
                        }
                        else
                        {
                            log.debug("Voie dégagée, reprise de script !");
                            stateToConsider.robot.moveLengthwise(310-(xBefore-stateToConsider.robot.getPosition().x),hooksToConsider,false);
                        }
                    }
                    else
                    {
                        log.debug("Bord du filet touché !");
                        try
                        {
                            hooksToConsider.clear();
                            if(stateToConsider.robot.getAreFishesOnBoard())
                            {
                                log.debug("Poisson à bord, on les garde !");
                                stateToConsider.robot.useActuator(ActuatorOrder.MIDDLE_POSITION, true);
                            }
                            else
                            {
                                log.debug("Pas de poissons à bord !");
                                stateToConsider.robot.useActuator(ActuatorOrder.ARM_INIT, true);
                            }
                            log.debug("Tentative de dégagement !");
                            if(stateToConsider.robot.getOrientation()<=Math.PI && stateToConsider.robot.getOrientationFast()>=0)
                            {
                                stateToConsider.robot.moveArc(new Arc(-400,300,stateToConsider.robot.getOrientationFast(),false),hooksToConsider);
                            }
                            else
                            {
                                stateToConsider.robot.moveArc(new Arc(-400, -300, stateToConsider.robot.getOrientationFast(), false),hooksToConsider);
                            }
                        }
                        catch(Exception ex)
                        {
                            log.debug("Problème lors du dégagement : " + ex);
                        }
                        throw e;
                    }
                }

                // On indique au robot que les poissons ne sont plus sur le bras
                stateToConsider.robot.setAreFishesOnBoard(false);
				
				// arc pour sortir du bord de table
				stateToConsider.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);
				log.debug("Script OK !");
				Arc disengage = new Arc(-320,160,stateToConsider.robot.getOrientation(),false);
				stateToConsider.robot.moveArc(disengage, hooksToConsider);
				
				// reprise de vitesse d'avant script
				stateToConsider.robot.setLocomotionSpeed(speedBeforeScriptWasCalled);
			}
		}		
		catch(Exception e)
		{
			hooksToConsider.clear();
			finalize(stateToConsider, e);
			throw e;
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
	public Circle entryPosition(int version, int ray, Vec2 robotPosition) throws BadVersionException
	{
		// Modifiable avec les phases de test
		if (version == 0 || version == 3 || version==4)
		{
		//	return new Circle(new Vec2(620,255));
			return new Circle(new Vec2(1150,492));
		}
		else if (version == 1 || version == 2)
		{
			return new Circle(new Vec2(1030,355));
		}
//		else if (version ==4)
//		{
//			return new Circle(new Vec2(1180,450));
//		}
		else
		{
			log.debug("erreur : mauvaise version de script");
			throw new BadVersionException();
		}
	}

	@Override
	public void finalize(GameState<?> stateToConsider, Exception ex) throws SerialFinallyException 
	{
		log.debug("Exception " + ex + "dans Fishing :");
		ex.printStackTrace();
		log.debug("Lancement du Finalize !");
		try
		{
			log.debug("Poissons à bord ? :" + stateToConsider.robot.getAreFishesOnBoard());
			stateToConsider.robot.setBasicDetection(false);
			// Si on possède un poisson lors du finalize, on garde le bras à mi-hauteur
			if(stateToConsider.robot.getAreFishesOnBoard())
			{
				log.debug("Arrêt de Fishing avec des poissons à bord !");
				stateToConsider.robot.useActuator(ActuatorOrder.MIDDLE_POSITION, true);
			}
			else
			{
				log.debug("Pas de poissons à bord, lancement de ARM_INIT !");
				// On remonte le bras en fin de script, puis arrêt du robot
				stateToConsider.robot.useActuator(ActuatorOrder.ARM_INIT, true);
			}
		}
		catch (SerialConnexionException e) 
		{
			log.debug("Fishing : impossible d'utiliser le bras !");
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
