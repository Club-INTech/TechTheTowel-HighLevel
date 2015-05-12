package strategie;

import hook.Hook;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.EnumSet;

import com.sun.org.apache.bcel.internal.generic.NEW;

import pathDingDing.PathDingDing;
import container.Container;
import container.Service;
import enums.ActuatorOrder;
import enums.ScriptNames;
import enums.ObstacleGroups;
import enums.Speed;
import enums.UnableToMoveReason;
import exceptions.ConfigPropertyNotFoundException;
import exceptions.ExecuteException;
import exceptions.InObstacleException;
import exceptions.PathNotFoundException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialConnexionException;
import exceptions.serial.SerialFinallyException;
import robot.*;
import scripts.AbstractScript;
import scripts.GetPlot;
import scripts.ScriptManager;
import smartMath.Vec2;
import table.Table;
import utils.Log;
import utils.Config;
import utils.Sleep;


/**
 *	Classe de l'IA
 * @author Paul, marsu
 */



public class Strategie implements Service
{
	/** système de log sur lequel écrire*/
	private Log log;
	
	/** Endroit où on peut lire la configuration du robot */
	private Config config;
	
	/** La table sur laquelle le robot se déplace */
	private Table table;
	
	/** Le robot sur lequel on travaille :*/
	
	private RobotReal robotReal;
	
	/** Le gameState reel*/
	private GameState<RobotReal> realGameState;
	
	
	/** Les scripts Manager des deux robots*/
	ScriptManager scriptmanager;	
	
	/** Le container necessaire pour les services */
	protected Container container;
	
	/** Les hooks des deux robots*/
	ArrayList<Hook> hookRobot = new ArrayList<Hook>();

	/**
	 * le prochain script que l'IA executera mis a jour dans takeDecision
	 */
	@SuppressWarnings("unused")
	private AbstractScript nextScript;
	/**
	 * la valeure en point du prochain script
	 */
	private int nextScriptValue;
	/**
	 * le numero de version du procahin script a executer
	 */
	@SuppressWarnings("unused")
	private int nextScriptVersion;
	
	/**
	 * le trouveur de chemin de la strategie
	 */
	private PathDingDing pathDingDing;
	/**
	 * le chrono de la strategie
	 */
	private RobotChrono robotChrono;

	private int matchDuration;
	
	/**
	 * la liste des scripts a executer pour le match scripté
	 */
	private ArrayList<AbstractScript> scriptedMatchScripts = new ArrayList<AbstractScript>();
	/**
	 * la liste des versions a executer pour le match scripté
	 */
	private ArrayList<Integer> scriptedMatchVersions = new ArrayList<Integer>();
	

	/**
	 * la liste des fonctions qui gèrent de facon personalisées les exceptions durant le match scripté 
	 */
	private ArrayList<Method> scriptedMatchCustomExceptionHandlers = new ArrayList<Method>();
	
	
	/**
	 * Temps en ms qui doit s'écouler dans le match avant que le robnt arrete de réessayer ses scripts
	 */
	private int timeBeforeRushMode = 50000;
	
/**
 * Crée la strategie, l'IA decisionnelle
 * @param config
 * @param log
 * @param state
 * @param scriptManager
 * @param trouveurDeChemin
 */
	public Strategie(Config config, Log log, GameState<RobotReal> state, ScriptManager scriptManager, PathDingDing trouveurDeChemin)
	{
		this.realGameState = state;
		this.config = config;
		this.log = log;
        this.table = state.table;
        this.robotReal = state.robot;
        this.scriptmanager = scriptManager;
        this.pathDingDing = trouveurDeChemin;
		updateConfig();

        robotChrono = new RobotChrono(config, log, pathDingDing);
	}

	public void updateConfig() 
	{
		table.updateConfig();
        robotReal.updateConfig();
        try
        {
            matchDuration = Integer.parseInt(config.getProperty("temps_match"));
		}
        catch (ConfigPropertyNotFoundException e)
        {
        	log.debug("Revoir le code : impossible de trouver la propriété "+e.getPropertyNotFound(), this);
		}

	}
	
	/**
	 * on suppose que tout les AX-12 sont initialisés avant de lancer l'IA
	 * et que la position du robot est la position de depart
	 */
	public void IA()
	{
		GameState<Robot> gameState = new GameState<Robot>(config, log, table, robotReal);
		
		
		//premier script pour sortir de la zone, on essaye en premier de sortir en deposant les tapis et en recuperant le gobelet
		try 
		{
			log.debug("Execution du script : DROP_CARPET version 2", this);
			scriptmanager.getScript(ScriptNames.DROP_CARPET).execute(2, gameState, hookRobot);
		} 
		catch(ExecuteException e)
		{
			if(e.compareInitialException(new UnableToMoveException(new Vec2(0,0), UnableToMoveReason.PHYSICALLY_BLOCKED)))
			{
				// Si on s'est raté mais qu'on est proches, on ajoute le script de depose tapis simplement 
				if (robotReal.getPosition().distance(Table.entryPosition)<250)
				{
					scriptedMatchScripts.add(scriptmanager.getScript(ScriptNames.DROP_CARPET));
					scriptedMatchVersions.add(0);
				}
				
				// On tente de sortir à tout prix ! On retente tant qu'on a pas reussi
				while (robotReal.getPosition().distance(Table.entryPosition)<250)
				{
					try 
					{
						scriptmanager.getScript(ScriptNames.EXIT_START_ZONE).execute(0, gameState, hookRobot);
					} 
					catch (ExecuteException | SerialFinallyException e1) 
					{
						log.critical("impossible de sortir de la zone de depart", this);
						Sleep.sleep(500);
					}
				}
					
			}
			else if(e.compareInitialException(new SerialConnexionException()))
			{		
				initInMatch();
			}
		}
		catch (SerialFinallyException e)
		{
			while (true)
			{
				try 
				{	// On lancee le finalize en brute
					scriptmanager.getScript(ScriptNames.DROP_CARPET).finalize(gameState);
					break; // sortie du while
				} 
				catch (UnableToMoveException | SerialFinallyException e1) 
				{
					;
				}
			}
		}
		
		// match scripté de l'IA
		scriptedMatch(gameState);
					
//		//tant que le match n'est pas fini, on prend des decisions :
//		while(realGameState.getTimeEllapsed()   <  matchDuration )
//		{
//			log.debug("======choix script======", this);
//			System.out.println();
//			
//			updateConfig();
//			takeDecision();
//			
//			log.debug("script choisit :"+nextScript.getClass().getName(), this);
//			log.debug("version :"+nextScriptVersion, this);
//			
//			try 
//			{
//				nextScript.goToThenExec(nextScriptVersion, gameState, hookRobot);
//			} 
//			catch (PathNotFoundException e1)
//			{
//				//un obstacle a été ajouté depuis le calcul de robot chrono donc il faut relancher la prise de decision
//			}
//			catch (UnableToMoveException e1) 
//			{
//				//si le robot se cogne sans detecter l'obstacle
//				if (e1.reason.compareTo(UnableToMoveReason.PHYSICALLY_BLOCKED)==0)
//				{
//					//on ajoute cet obstacle
//					table.getObstacleManager().addObstacle(robotReal.getPosition());
//					//on essaye de se degager
//					int numberOfTry = 0;
//					while (numberOfTry<5)
//					{
//						try
//						{
//							if (robotReal.getIsRobotMovingBackward())
//								robotReal.moveLengthwise(250, hookRobot, false, false);
//							else // si on tourne ou qu'on avancais on recule pour se degager
//								robotReal.moveLengthwise(-250, hookRobot, false, false);
//							break;
//						} 
//						catch (UnableToMoveException e2)
//						{
//							log.warning("impossible de se degager de l'obstacle : tentative n°"+numberOfTry, this);
//							numberOfTry++;
//						}
//					}
//					//qu'on ai reussi ou non a se degager on fait autre chose
//				}
//				//puis on relance la prise de decision
//				
//				//sinon on relance la pise de decision
//			}
//			catch (InObstacleException e1)
//			{
//				//TODO gerer cette exception en cours de match (pas uniquement pour le debug)
//				log.debug("le script "+nextScript.getClass()+"emmet un inObstacleException", this);
//			}
//			catch (SerialConnexionException e1)
//			{
//				initInMatch();
//			}
//			catch (SerialFinallyException e1)
//			{
//				while (true)
//				{
//					try 
//					{
//						nextScript.finalize(gameState);
//						break;
//					} 
//					catch (UnableToMoveException | SerialFinallyException e2) 
//					{
//						log.critical("multiple finalize exceptions", this);
//					}
//				}
//			}
//		}
	}
	
	/**
	 * initialize le robot real pendant un match (suite a une SerialConnexionException)
	 * se relance tant qu'il y a des SerialConnexionException (pour preserver la meca)
	 */
	private void initInMatch() 
	{
		robotReal.setPosition(robotReal.getPositionFast());
		robotReal.setOrientation(robotReal.getOrientationFast());
		
		try 
		{
			robotReal.useActuator(ActuatorOrder.ELEVATOR_GROUND, false);
		} 
		catch (SerialConnexionException e) 
		{
			log.warning("elevator ne reponds pas (ground)", this);
			initInMatch();
			return;
		}
		
		try 
		{
			robotReal.useActuator(ActuatorOrder.ARM_LEFT_CLOSE, false);
		} 
		catch (SerialConnexionException e)
		{
			log.warning("le bras gauche ne reponds pas", this);
			initInMatch();
			return;
		}
		
		try 
		{
			robotReal.useActuator(ActuatorOrder.ARM_RIGHT_CLOSE, false);
		} catch (SerialConnexionException e) 
		{
			log.warning("le bras droit ne reponds pas", this);
			initInMatch();
			return;
		}
		
		try 
		{
			robotReal.useActuator(ActuatorOrder.CLOSE_RIGHT_GUIDE, false);
		} 
		catch (SerialConnexionException e) 
		{
			log.warning("le guide droit ne reponds pas", this);
			initInMatch();
			return;
		}
		
		try 
		{
			robotReal.useActuator(ActuatorOrder.CLOSE_LEFT_GUIDE, false);
		} 
		catch (SerialConnexionException e) 
		{
			log.warning("le guide gauche ne reponds pas", this);
			initInMatch();
			return;
		}
		
		try 
		{
			robotReal.useActuator(ActuatorOrder.LEFT_CARPET_FOLDUP, false);
		}
		catch (SerialConnexionException e) 
		{
			log.warning("le tapis gauche ne reponds pas", this);
			initInMatch();
			return;
		}
		
		try 
		{
			robotReal.useActuator(ActuatorOrder.RIGHT_CARPET_FOLDUP, false);
		}
		catch (SerialConnexionException e)
		{
			log.warning("le tapis droit ne reponds pas", this);
			initInMatch();
			return;
		}
		
		try
		{
			robotReal.useActuator(ActuatorOrder.LOW_LEFT_CLAP, false);
		} 
		catch (SerialConnexionException e) 
		{
			log.warning("le clap gauche ne reponds pas", this);
			initInMatch();
			return;
		}
		
		try 
		{
			robotReal.useActuator(ActuatorOrder.LOW_RIGHT_CLAP, false);
		}
		catch (SerialConnexionException e2) 
		{
			log.warning("le clap droit ne reponds pas", this);
			initInMatch();
			return;
		}
		
		try
		{
			robotReal.useActuator(ActuatorOrder.ELEVATOR_CLOSE_JAW, true);
		} 
		catch (SerialConnexionException e) 
		{
			log.warning("les machoires ne repondent pas", this);
			initInMatch();
			return;
		}
		
		try 
		{
			robotReal.useActuator(ActuatorOrder.ELEVATOR_LOW, true);
		}
		catch (SerialConnexionException e) 
		{
			log.warning("elevator ne reponds pas (low)", this);
			initInMatch();
			return;
		}
		
		robotReal.setLocomotionSpeed(Speed.SLOW);
	}

	/**
	 * 	Lance le match scripté de l'IA, suite de scripts
	 * @param gameState l'etat de la table
	 */
	private void scriptedMatch(GameState<Robot> gameState)
	{
		if(! gameState.table.getIsLeftCarpetDropped() || !gameState.table.getIsRightCarpetDropped())
		{
			scriptedMatchScripts.add(scriptmanager.getScript(ScriptNames.DROP_CARPET));
			scriptedMatchVersions.add(1);
			scriptedMatchCustomExceptionHandlers.add(null);
		}

		try 
		{
			scriptedMatchScripts.add(scriptmanager.getScript(ScriptNames.GRAB_PLOT));
			scriptedMatchVersions.add(2);
	//		scriptedMatchCustomExceptionHandlers.add(Strategie.class.getDeclaredMethod(new String("scriptedMatchHandePile0Plot"),(Class[])null));	// si quelqu'un se demande ce que c'est que ce délire, c'est un "pointeur sur fonction" en mode hack de java
			scriptedMatchCustomExceptionHandlers.add(null);
			
			scriptedMatchScripts.add(scriptmanager.getScript(ScriptNames.GRAB_PLOT));
			scriptedMatchVersions.add(34);
	//		scriptedMatchCustomExceptionHandlers.add(Strategie.class.getDeclaredMethod(new String("scriptedMatchHandePile0Plot"),(Class[])null));
			scriptedMatchCustomExceptionHandlers.add(null);
			
			scriptedMatchScripts.add(scriptmanager.getScript(ScriptNames.CLOSE_CLAP));
			scriptedMatchVersions.add(12);
			scriptedMatchCustomExceptionHandlers.add(null);
			
			scriptedMatchScripts.add(scriptmanager.getScript(ScriptNames.GRAB_PLOT));
			scriptedMatchVersions.add(1);
			scriptedMatchCustomExceptionHandlers.add(null);
			
			scriptedMatchScripts.add(scriptmanager.getScript(ScriptNames.FREE_STACK));
			scriptedMatchVersions.add(0);
			scriptedMatchCustomExceptionHandlers.add(null);
			
			scriptedMatchScripts.add(scriptmanager.getScript(ScriptNames.GRAB_PLOT));
			scriptedMatchVersions.add(56);
			scriptedMatchCustomExceptionHandlers.add(null);
			
			scriptedMatchScripts.add(scriptmanager.getScript(ScriptNames.FREE_STACK));
			scriptedMatchVersions.add(2);
//		} catch (NoSuchMethodException e2) {
//			// TODO Auto-generated catch block
//			e2.printStackTrace();
		} 
		catch (SecurityException e2) 
		{
			e2.printStackTrace();
		}
		
		
		// tant qu'on a pas tout fait
		while(!scriptedMatchScripts.isEmpty())
		{
			try 
			{
				boolean tryAgain = true;
				while (tryAgain)
				{
					try 
					{
						try
						{
							log.debug("================================== Nouvelle Tentative de script ========================================", this);
							log.debug("================================== Liste actuelle:", this);
							
							for(int i = 0; i < scriptedMatchScripts.size(); ++i)
								log.debug("scriptedMatchScripts(" + i + ") = " + scriptedMatchScripts.get(i).getClass().getCanonicalName() + " version " + scriptedMatchVersions.get(i), this);

							log.debug("==================================", this);
							 
							// si le temps presse, on n'attends pas pour faire les scripts
							if (realGameState.getTimeEllapsed() > timeBeforeRushMode)
								tryAgain = false;

							// Loggue qu'on passe en mode Rush
							if (realGameState.getTimeEllapsed() > timeBeforeRushMode && realGameState.getTimeEllapsed() < timeBeforeRushMode + 300)
								log.warning("Le temps presse: passage en mode Rush !  (TimeEllapsed = " + realGameState.getTimeEllapsed(), this);

							// exécute le prochain script sur la liste
							log.debug("Execution du script : " + scriptedMatchScripts.get(0).getClass().getCanonicalName() + " version " + scriptedMatchVersions.get(0), this);
							scriptedMatchScripts.get(0).goToThenExec(scriptedMatchVersions.get(0), gameState, hookRobot);

							tryAgain = false;

							scriptedMatchScripts.remove(0);
							scriptedMatchVersions.remove(0);
							scriptedMatchCustomExceptionHandlers.remove(0);
						}
						catch (Exception e) 
						{
							// en cas d'erreur d'exécution, demande la gestion de l'erreur par le gestionnaire custom s'il y en a un, sinon applique la politique par défaut.
							if(scriptedMatchCustomExceptionHandlers.get(0) != null) 
							{
								try 
								{
									scriptedMatchCustomExceptionHandlers.get(0).invoke(this, (Object[])null);
								} 
								catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e1) 
								{
									e1.printStackTrace();
								}
							}
							else
								throw e;
						}
						
					}
					catch (UnableToMoveException e) 
					{
						log.warning("Catch de UnableToMoveException dans Strategie", this);
						
						if (e.reason.compareTo(UnableToMoveReason.PHYSICALLY_BLOCKED)==0)
						{
							// attention: ne pas bouger tryAgain dans ce catch //TODO :why ?
							tryAgain = false;
							
							try
							{
								realGameState.robot.moveLengthwise(-200);
							}
							catch (UnableToMoveException e1)
							{
								try 
								{
									realGameState.robot.moveLengthwise(200);
								} 
								catch (UnableToMoveException e2)
								{
									log.critical("Le robot est complètent bloqué et n'arrive pas a se dégader", this);
								}
							}
						}
						else {
							;
						}
					} 
					catch (PathNotFoundException e)
					{
						log.warning("Catch de PathNotFoundException dans Strategie", this);
						
						//on ajoute le script dans le tableau un peu plus loin
						scriptedMatchScripts.add(Math.max(0,scriptedMatchScripts.size()-3), scriptedMatchScripts.get(0));
						scriptedMatchVersions.add(Math.max(0,scriptedMatchVersions.size()-3), scriptedMatchVersions.get(0));
						scriptedMatchCustomExceptionHandlers.add(Math.max(0,scriptedMatchScripts.size()-3), null);
						
						//et on abandonne le script pour le moment
						scriptedMatchScripts.remove(0);
						scriptedMatchVersions.remove(0);
						scriptedMatchCustomExceptionHandlers.remove(0);
						tryAgain = false;
					} 
					catch (ExecuteException e)
					{
						if(e.compareInitialException(new UnableToMoveException(new Vec2(0,0), UnableToMoveReason.OBSTACLE_DETECTED)))
						{
							UnableToMoveException e1 = (UnableToMoveException) e.getExceptionThrownByExecute();
							if(e1.reason.compareTo(UnableToMoveReason.PHYSICALLY_BLOCKED)==0)
							{
								disengage();
							}
							else if (e1.reason.compareTo(UnableToMoveReason.OBSTACLE_DETECTED)==0)
							{
								//on ajoute le script dans le tableau un peu plus loin
								scriptedMatchScripts.add(Math.max(0,scriptedMatchScripts.size()-3), scriptedMatchScripts.get(0));
								scriptedMatchVersions.add(Math.max(0,scriptedMatchVersions.size()-3), scriptedMatchVersions.get(0));
								scriptedMatchCustomExceptionHandlers.add(Math.max(0,scriptedMatchScripts.size()-3), null);
								
								//et on abandonne le script pour le moment
								scriptedMatchScripts.remove(0);
								scriptedMatchVersions.remove(0);
								scriptedMatchCustomExceptionHandlers.remove(0);
								tryAgain = false;
							}
						}
						else if(e.compareInitialException(new SerialConnexionException()))
						{
							initInMatch();
						}
						else
						{
							log.warning("type d'exception non prevue ExecuteException : "+e.getExceptionThrownByExecute().toString(), this);
						}
					}
					catch (InObstacleException e) 
					{
						log.warning("Catch de InObstacleException dans Strategie", this);
						
						// un obstacle gene le pathfinding : on parcourt la liste de ceux qui nous genent
						for (ObstacleGroups obstacle : e.getObstacleGroup())
						{
							log.warning("attention, obstacle : "+obstacle.getClass().toString(),this);
							
							// si c'est un robot ennemi qui empèche l'accès au point d'entrée, alors on reporte a plus tard ce script
							if(obstacle.equals(ObstacleGroups.ENNEMY_ROBOTS) && realGameState.getTimeEllapsed() < timeBeforeRushMode)
							{
								scriptedMatchScripts.add(Math.max(0,scriptedMatchScripts.size()-3), scriptedMatchScripts.get(0));
								scriptedMatchVersions.add(Math.max(0,scriptedMatchVersions.size()-3), scriptedMatchVersions.get(0));
								scriptedMatchCustomExceptionHandlers.add(Math.max(0,scriptedMatchScripts.size()-3), null);
								
								//et on abandonne le script pour le moment
								scriptedMatchScripts.remove(0);
								scriptedMatchVersions.remove(0);
								scriptedMatchCustomExceptionHandlers.remove(0);
								tryAgain = false;
								
								// du coup, on passe à autre chose, on quite ce for
								return;
							}
							// si on est bloqué par les plots 3, 4 ou le gobelet 0 (ce qui n'arrive que quand on essaye de faire clap 12 sans avoir faire getPlot34) 
							// et qu'on est pas en mode rush on execute immediatement le script pour les recuperer (ces scripts sont critiques)
							else if ( (obstacle.compareTo(ObstacleGroups.GREEN_PLOT_3)==0 || obstacle.compareTo(ObstacleGroups.GREEN_PLOT_4)==0
									   || obstacle.compareTo(ObstacleGroups.GOBLET_0)==0 ) && realGameState.getTimeEllapsed() < timeBeforeRushMode)
							{
								//on enleve le script de get plot version 34 le plus proche (le seul)
								for (int i = 0 ; i<scriptedMatchScripts.size(); i++)
								{
									if ((scriptedMatchScripts.get(i)) instanceof GetPlot && scriptedMatchVersions.get(i)==34)
									{
										scriptedMatchScripts.remove(i);
										scriptedMatchVersions.remove(i);
										scriptedMatchCustomExceptionHandlers.remove(i);
										break;
									}
								}
								
//								
//								//et on le remet en position 0
//								scriptedMatchScripts.add(0, scriptmanager.getScript(ScriptNames.GRAB_PLOT));
//								scriptedMatchVersions.add(0, 34);
//								scriptedMatchCustomExceptionHandlers.add(null);
//								
//								// etant donné qu'on a resolu un probleme, il est temps de quitter le for(obstacle)
//								return;
								
								
								//si il existe un chemin jusqu'au script de recuperation des plots on continue
								try 
								{
									pathDingDing.computePath(robotReal.getPosition(), scriptmanager.getScript(ScriptNames.GRAB_PLOT).entryPosition(34, robotReal.robotRay, robotReal.getPositionFast()).position, EnumSet.allOf(ObstacleGroups.class));
									//et on le remet en position 0
									scriptedMatchScripts.add(0, scriptmanager.getScript(ScriptNames.GRAB_PLOT));
									scriptedMatchVersions.add(0, 34);
									scriptedMatchCustomExceptionHandlers.add(null);
								}
								catch (PathNotFoundException | InObstacleException e1) 
								{
									log.critical("On change totalement le match", this);
									//sinon on change totalement le match scripté
									scriptedMatchScripts.clear();
									scriptedMatchVersions.clear();
									scriptedMatchCustomExceptionHandlers.clear();
									
									scriptedMatchScripts.add(scriptmanager.getScript(ScriptNames.GRAB_PLOT));
									scriptedMatchVersions.add(0, 567);
									scriptedMatchCustomExceptionHandlers.add(null);
									
									scriptedMatchScripts.add(scriptmanager.getScript(ScriptNames.FREE_STACK));
									scriptedMatchVersions.add(0, 0);
									scriptedMatchCustomExceptionHandlers.add(null);
									
									scriptedMatchScripts.add(scriptmanager.getScript(ScriptNames.CLOSE_CLAP));
									scriptedMatchVersions.add(0, 12);
									scriptedMatchCustomExceptionHandlers.add(null);
									
									if(!realGameState.table.isClapXClosed(2))
									{
										scriptedMatchScripts.add(scriptmanager.getScript(ScriptNames.CLOSE_CLAP));
										scriptedMatchVersions.add(0, 2);
										scriptedMatchCustomExceptionHandlers.add(null);
									}
									
									tryAgain = false;
								}
								
								
								try 
								{
									scriptedMatchCustomExceptionHandlers.add(Strategie.class.getDeclaredMethod(new String("scriptedMatchHandePile0Plot"),(Class[])null));
								} 
								catch (NoSuchMethodException | SecurityException e1)
								{
									e1.printStackTrace();
								}
							}
							//sinon  on arrete d'essayer le script
							else
							{
								scriptedMatchScripts.remove(0);
								scriptedMatchVersions.remove(0);
								scriptedMatchCustomExceptionHandlers.remove(0);
								tryAgain = false;
								
								// le souci est autre qu'un obstacle classique, on quitte donc tout ceci après avoir enlevé le script posant probleme
								return;
							}
						}
					}
				}
			}
			catch (IndexOutOfBoundsException e)
			{
				log.debug("out of bound, IA's scripted match", this);
				return;
			}
			catch (SerialConnexionException | SerialFinallyException e) 
			{
				while (true)
				{
					//on attends 3 secondes pour (re)tenter un finalise
					gameState.robot.sleep(3000);
					try 
					{
						scriptedMatchScripts.get(0).finalize(gameState);
						break;
					} 
					catch (IndexOutOfBoundsException e1)
					{
						log.debug("out of bound, IA's scripted match", this);
						//on ajoute le script de depart pour lancer son finalize (puisqu'il n'y avait pas de script prevu apres c'est pas grave)
						scriptedMatchScripts.add(scriptmanager.getScript(ScriptNames.EXIT_START_ZONE));
					}
					catch (SerialFinallyException | UnableToMoveException e1)
					{
						log.critical("enchainement de SerialFinallyException", this);
					}
				}
			}
		}
		
	}

private void disengage() {
		// TODO Auto-generated method stub
		
	}

//	/** Fonction principale : prend une decision en prenant tout en compte */
//	@SuppressWarnings("unused")
//	private void takeDecision()
//	{
//		//TODO ajouter un script qui ne fait rien si tout les scripts ont deja étés effectués (qui fait 0 points)
//		nextScriptValue=Integer.MIN_VALUE;
//		for(ScriptNames scriptName : ScriptNames.values())
//		{
//			if (scriptName != ScriptNames.EXIT_START_ZONE)
//			{
//				AbstractScript script = scriptmanager.getScript(scriptName);
//				Integer[] versions = script.getVersion(realGameState);
//				
//				for(int i=0; i<(versions.length);i++)
//				{
//					int currentScriptValue = scriptValue(script, versions[i]);
//					if (currentScriptValue>nextScriptValue)
//					{
//						nextScript=script;
//						nextScriptValue=currentScriptValue;
//						nextScriptVersion=versions[i];
//					}
//						
//				}
//			}
//		}
//	}
	
	@SuppressWarnings("unused")
	private void scriptedMatchHandePile0Plot()
	{
		
		log.debug("HAHAHA ! Pouet Mégatest !", this);
		int a = this.scriptedMatchScripts.size();
		a*=100;
		return;
	}

//	/**
//	 * donne la valeur d'un script au sens de l'IA
//	 * @param script le script dont on veut connaitre la valeur
//	 * @param version la version du script a tester
//	 * @return un entier qui est la valeur de ce script
//	 */
//	private int scriptValue(AbstractScript script, int version) 
//	{
//		int points = 0;
//		//on replace le robotChrono pour le calcul du temps et la table (pour ne pas modifier la table actuelle)
//		robotReal.copy(robotChrono);
//		Table tableCopy = table.clone();
//		GameState<Robot> chronoState = new GameState<Robot>(config, log, tableCopy, robotChrono);
//		
//		
//		//calcul de la duree du script
//		long durationScript;
//		robotChrono.resetChrono();
//		try 
//		{
//			script.goToThenExec(version, chronoState, hookRobot);
//			durationScript = robotChrono.getCurrentChrono();
//		} 
//		catch (UnableToMoveException | SerialConnexionException
//				| PathNotFoundException | SerialFinallyException e) 
//		{
//			durationScript = Long.MAX_VALUE;
//		} 
//		catch (InObstacleException e) 
//		{
//			//on enleve les obstacles genants en adaptant les points
//			if (!e.getObstacleGroup().isEmpty())
//			{
//				try 
//				{
//					robotChrono.resetChrono();
//					script.goToThenExec(version, chronoState, hookRobot, e.getObstacleGroup());
//					durationScript = robotChrono.getCurrentChrono();
//				}
//				catch (UnableToMoveException | SerialConnexionException
//						| PathNotFoundException | SerialFinallyException
//						| InObstacleException e1) 
//				//en cas de double erreur on suppose que le robot n'y arrivera pas
//				{
//					durationScript = Long.MAX_VALUE;
//				}
//			
//				//on retire le nombre de points correspondant a ces obstacles = malus
//				//on suppose ces obstacles toulours sur la table (a voir si on peut tester)
//				for (ObstacleGroups obstacle : e.getObstacleGroup())
//				{
//					if 
//					(	
//						obstacle == ObstacleGroups.GOBLET_0 || 
//						obstacle == ObstacleGroups.GOBLET_1 || 
//						obstacle == ObstacleGroups.GOBLET_2 || 
//						obstacle == ObstacleGroups.GOBLET_3 || 
//						obstacle == ObstacleGroups.GOBLET_4
//					)
//						points -= 4;
//					else if 
//					(	
//						obstacle == ObstacleGroups.YELLOW_PLOT_0 || 
//						obstacle == ObstacleGroups.YELLOW_PLOT_1 || 
//						obstacle == ObstacleGroups.YELLOW_PLOT_2 || 
//						obstacle == ObstacleGroups.YELLOW_PLOT_3 || 
//						obstacle == ObstacleGroups.YELLOW_PLOT_4 ||
//						obstacle == ObstacleGroups.YELLOW_PLOT_5 || 
//						obstacle == ObstacleGroups.YELLOW_PLOT_6 || 
//						obstacle == ObstacleGroups.YELLOW_PLOT_7 || 
//						obstacle == ObstacleGroups.GREEN_PLOT_0 || 
//						obstacle == ObstacleGroups.GREEN_PLOT_1 || 
//						obstacle == ObstacleGroups.GREEN_PLOT_2 || 
//						obstacle == ObstacleGroups.GREEN_PLOT_3 || 
//						obstacle == ObstacleGroups.GREEN_PLOT_4 || 
//						obstacle == ObstacleGroups.GREEN_PLOT_5 || 
//						obstacle == ObstacleGroups.GREEN_PLOT_6 || 
//						obstacle == ObstacleGroups.GREEN_PLOT_7
//					)
//						points -= 5;
//					//si il faut suprimer le zone adverse ou le robot enemi on suprime les points de ce script (puni)
//					else if
//					(
//						obstacle == ObstacleGroups.ENNEMY_ROBOTS ||
//						obstacle == ObstacleGroups.ENNEMY_ZONE
//						
//					)
//						points = Integer.MIN_VALUE;
//				}
//			}
//			//si aucun obstacle a enlever alors le point visé est hors de la table engueuler les scripts
//			else
//				durationScript = Long.MAX_VALUE;
//			
//			
//		}
//		// supr debug
//		log.debug("script :"+script.getClass().getName(), this);
//		log.debug("version :"+version, this);
//		log.debug("temps :"+durationScript, this);
//		
//		
//		points += script.remainingScoreOfVersion(version, realGameState);
//		points *= ((matchDuration-realGameState.getTimeEllapsed())-durationScript)/durationScript;
//		log.debug("points :"+points, this);
//		//points = (pointsScript+malus) * (tempsRestant - duree)/duree
//		return points;
//		
//	}
	
	private void rushMode()
	{
		try 
		{
			// On s'eloigne des ennemis potentiels, en attrapant les plots 5 et 6
			scriptedMatchScripts.add(scriptmanager.getScript(ScriptNames.GRAB_PLOT));
			scriptedMatchVersions.add(65);
	//		scriptedMatchCustomExceptionHandlers.add(Strategie.class.getDeclaredMethod(new String("scriptedMatchHandePile0Plot"),(Class[])null));	// si quelqu'un se demande ce que c'est que ce délire, c'est un "pointeur sur fonction" en mode hack de java
			scriptedMatchCustomExceptionHandlers.add(null);
			
			// On vide la pile principale et le gobelet
			scriptedMatchScripts.add(scriptmanager.getScript(ScriptNames.FREE_STACK));
			scriptedMatchVersions.add(0);
			scriptedMatchCustomExceptionHandlers.add(null);
			
			// On vide le plot dans notre bras
			scriptedMatchScripts.add(scriptmanager.getScript(ScriptNames.FREE_STACK));
			scriptedMatchVersions.add(2);
			scriptedMatchCustomExceptionHandlers.add(null);
		} 
		catch (SecurityException e2) 
		{
			e2.printStackTrace();
		}
		
	}
}
