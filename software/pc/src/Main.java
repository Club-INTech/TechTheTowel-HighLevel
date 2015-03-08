import hook.Hook;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import robot.Robot;
import robot.cardsWrappers.SensorsCardWrapper;
import scripts.AbstractScript;
import scripts.ScriptManager;
import smartMath.Vec2;
import strategie.GameState;
import threads.ThreadTimer;
import utils.Config;
import utils.Sleep;
import container.Container;
import enums.ScriptNames;
import enums.ServiceNames;
import exceptions.ContainerException;
import exceptions.PathNotFoundException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialConnexionException;
import exceptions.serial.SerialFinallyException;
import exceptions.serial.SerialManagerException;


/**
 * Code qui démarre le robot en début de match
 * @author marsu
 *
 */
public class Main
{
	static Container container;
	static Config config;
	static GameState<Robot> real_state; //TODO le robot de ce game state est il un robot real ?
	static ArrayList<Hook> emptyHook;
	static ScriptManager scriptmanager;
	static SensorsCardWrapper mSensorsCardWrapper;
	static boolean mustStartQuickly;
	
	static boolean isGrabGlass1Succeded,isGrabGlass2Succeded,isGrabGlass3Succeded;
	static boolean isDropCarpetSucceded;
	static boolean isPlot0Succeded,isPlot1Succeded,isPlot2Succeded,isPlot34Succeded,isGrabGlass56Succeded,isGrabGlass7Succeded;
	static boolean isClap12Succeded,isClap3Succeded;
	static boolean isStackFree1Succeded,isStackFree2Succeded;
	static boolean isDropGlass1Succeded, isDropGlass2Succeded,isDropGlass3Succeded;

	
	
// dans la config de debut de match, toujours demander une entrée clavier assez longue (ex "oui" au lieu de "o", pour éviter les fautes de frappes. Une erreur a ce stade coûte cher.
	
	/**
	 * Point d'entrée du programme. C'est ici que le code commence par être exécuté 
	 * @param args chaine de caractère des arguments de la ligne de commande
	 * @throws Exception TODO : quels sont les exeptions lancés ?
	 */
	@SuppressWarnings("unchecked")
	public static void main(String[] args)
	{
		//nombre initial des essai pour le container et la serie
		int numberOfTryContainer=0;
		int numberOfTrySerial=0;
		
		//nombre maximum d'essai autorisee pour le container et la serie
		int maximumOfTryContainer=5;
		int maximumOfTrySerial=3;

		System.out.println("=== Robot INTech 2015 : initialisation ===");
        // si on veut exécuter un test unitaire sur la rasbe, recopier test.nomDeLaClasseDeTest
		//JUnitCore.main(		"tests.JUnit_DeplacementsTest");  
		
		// Système d'injection de dépendances
		//tant que le nombre d'essai n'est pas trop grand on recommence
		while (numberOfTryContainer<maximumOfTryContainer || numberOfTrySerial<maximumOfTrySerial)
		{
			try 
			{
				container = new Container();
				container.getService(ServiceNames.LOG);
				config = (Config) container.getService(ServiceNames.CONFIG);
			
				// initialise les singletons
				real_state = (GameState<Robot>) container.getService(ServiceNames.GAME_STATE);
			    scriptmanager = (ScriptManager) container.getService(ServiceNames.SCRIPT_MANAGER);
			    mSensorsCardWrapper = (SensorsCardWrapper) container.getService(ServiceNames.SENSORS_CARD_WRAPPER);
			    emptyHook = new ArrayList<Hook>(); //TODO la veritable liste des hooks pour le match
			} 
			catch (ContainerException e) 
			//on gere les exceptions du container, en cas de probleme on a pas d'aure solution que de reessayer, mais si c'est vraiment impossible il faut debugger
			{
				if (numberOfTryContainer<maximumOfTryContainer)
				{
					numberOfTryContainer++;
					System.out.println("erreur dans le containeur, essai n°"+numberOfTryContainer);
				}
				else
				{
					System.out.println ("erreur dans le container, a debugger d'urgence !");
					e.printStackTrace();
				}
			}  
			catch (SerialManagerException | IOException e)  
			//SerialManager gere le serie et IOException gere les entrees/sortie donc le probleme serai dans les deux cas un probleme de branchement
			{
				if (numberOfTrySerial<maximumOfTrySerial)
				{
					numberOfTrySerial++;
					System.out.println("erreur dans la connexion serie, essai n°"+numberOfTrySerial);
				}
				else
				{
					System.out.println("mauvais branchement serie, la rasbe est-elle bien branchee ?");
					e.printStackTrace();
				}
			} 
		}

		// Threads
		container.startAllThreads();
		
		//initialisation du match
		if (real_state.robot.getSymmetry())
		{
			real_state.robot.setPosition(new Vec2 (-1381,1000));
			real_state.robot.setOrientation(0); 
			//si on est jaune on est en 0 
		}
		else
		{
			real_state.robot.setPosition(new Vec2 (1381,1000));
			real_state.robot.setOrientation(Math.PI);
			//sinon on est vert donc on est en PI
		}
		
		
		// attends que le jumper soit retiré
		waitMatchBegin();
		
		
		//premiere action du match
		
		System.out.println("Le robot commence le match");
		
		//le nombre d'essai maximum autorise pour les exceptions UnableToMoveException
		int maxNumberOfTryUnableToMove = 5;
		
		//le nombre d'essai courant pour les exceptions UnableToMoveException
		int numberOfTryUnableToMove = 0;
		
		while (numberOfTryUnableToMove<=maxNumberOfTryUnableToMove)
		{
			try 
			{
				scriptmanager.getScript(ScriptNames.EXIT_START_ZONE).goToThenExec(0, real_state, true, emptyHook );
			} 
			catch (SerialConnexionException  e) 
			{
				System.out.println("CRITICAL : Carte mal branchée. Match termine");
				e.printStackTrace();
				return;
			}
			catch (UnableToMoveException e) 
			{
				System.out.println("CRITICAL : Chemin bloque, enlevez votre main");
				if (numberOfTryUnableToMove>=maxNumberOfTryUnableToMove)
				{
					e.printStackTrace();
					return;
				}
				numberOfTryUnableToMove++;
			}
			catch (PathNotFoundException e) 
			{
				System.out.println("CRITICAL : Le robot ne sait pas rester sur place");
				System.out.println("verifiez le point d'etree de ExitBeginZone");
				return;
			} catch (SerialFinallyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		//debut du match
		System.out.println("debut du match");
		
		while(true) //TODO 90 secondes 
		{
			 scripts();
			
		}
	}
	

	/**
	 * Recale le robot
	 * @throws Exception
	 */
	static void recalerRobot()
	{

		// System.out.println("Pret au recalage, appuyez sur entrée pour continuer");
		System.out.println("TODO : code de recalage");// TODO le code de recalage
	}	
	

	/**
	 * Attends que le match soit lancé
	 * cette fonciton prends fin quand le match a démarré
	 */
	static void waitMatchBegin()
	{

		System.out.println("Robot pret pour le match, attente du retrait du jumper");
		
		// attends que le jumper soit retiré du robot
		boolean jumperWasAbsent = mSensorsCardWrapper.isJumperAbsent();
		while(jumperWasAbsent && mSensorsCardWrapper.isJumperAbsent())
			 	Sleep.sleep(100);
		
		// maintenant que le jumper est retiré, le match a commencé
		ThreadTimer.matchStarted = true;
	}
	
	public static void scripts()
	{
		if(!isGrabGlass1Succeded)
		{
			try 
			{
				scriptmanager.getScript(ScriptNames.GRAB_GLASS).goToThenExec(1, real_state, true, emptyHook );//On prend le verre,  notre droite en sortant
				System.out.println("en position ("+real_state.robot.getPosition().x+", "+real_state.robot.getPosition().y+") après le verre 1");
				isGrabGlass1Succeded=true;
			} 
			catch (UnableToMoveException | SerialConnexionException | PathNotFoundException | SerialFinallyException e) 
			{
				e.printStackTrace();
				isGrabGlass1Succeded=false;
			}
		}
			
		if(!isDropCarpetSucceded)
		{
			try 
			{
				System.out.println("en position ("+real_state.robot.getPosition().x+", "+real_state.robot.getPosition().y+") avant les tapis");
				scriptmanager.getScript(ScriptNames.DROP_CARPET).goToThenExec(1, real_state, true, emptyHook ); // On depose les tapis
				System.out.println("en position ("+real_state.robot.getPosition().x+", "+real_state.robot.getPosition()+" après les tapis");
				isDropCarpetSucceded=true;
			}
			catch (UnableToMoveException | SerialConnexionException| PathNotFoundException | SerialFinallyException e) 
			{
				e.printStackTrace();
				isDropCarpetSucceded=false;
			}
		}
		
		if(!isPlot2Succeded)
		{
			try 
			{
				System.out.println("en position ("+real_state.robot.getPosition().x+", "+real_state.robot.getPosition().y+") avant le plot 2");
				scriptmanager.getScript(ScriptNames.GRAB_PLOT).goToThenExec(2, real_state, true, emptyHook ); // On prend le plot a notre gauche, en sortant de la zone de depart
				System.out.println("en position ("+real_state.robot.getPosition().x+", "+real_state.robot.getPosition().y+") après le plot 2");
				isPlot2Succeded=true;
			} 
			catch (UnableToMoveException | SerialConnexionException | PathNotFoundException | SerialFinallyException e1) 
			{
				e1.printStackTrace();
				isPlot2Succeded=false;
			}
		}
		
		System.out.println("Plot 2 pris");

		if(!isPlot34Succeded)
		{
			try 
			{
				System.out.println("en position ("+real_state.robot.getPosition().x+", "+real_state.robot.getPosition().y+") avant les plots 3 et 4, et le verre 0");
				scriptmanager.getScript(ScriptNames.GRAB_PLOT).goToThenExec(34, real_state, true, emptyHook ); // On prend les 2 plots en bas de notre zonee de depart, et le verre
				System.out.println("en position ("+real_state.robot.getPosition().x+", "+real_state.robot.getPosition().y+") après les plots 3 et 4 et verre 0");
				isPlot34Succeded=true;
			} 
			catch (UnableToMoveException | SerialConnexionException | PathNotFoundException | SerialFinallyException e1) 
			{
				e1.printStackTrace();
				isPlot34Succeded=false;
			}
		}
		
		System.out.println("Plot 3, 4 et gobelet pris");
		

		if(!isClap12Succeded)
		{
			try 
			{
				System.out.println("en position ("+real_state.robot.getPosition().x+", "+real_state.robot.getPosition().y+") avant les claps 1 et 2");
				scriptmanager.getScript(ScriptNames.CLOSE_CLAP).goToThenExec(-12, real_state, true, emptyHook );
				isClap12Succeded=true;
			}
			catch (UnableToMoveException | SerialConnexionException | PathNotFoundException | SerialFinallyException e) 
			{
				e.printStackTrace();
				isClap12Succeded=false;
			}
		}
		
		System.out.println("Clap 1 et 2 Fermés");

		if(!isPlot1Succeded)
		{
			try 
			{
				System.out.println("en position ("+real_state.robot.getPosition().x+", "+real_state.robot.getPosition().y+") avant le plot 1");
				scriptmanager.getScript(ScriptNames.GRAB_PLOT).goToThenExec(1, real_state, true, emptyHook ); // On prend le plot a cote de l'estrade
				System.out.println("Plot 1 pris");
				isPlot1Succeded=true;
			}
			catch (UnableToMoveException | SerialConnexionException | PathNotFoundException | SerialFinallyException e) 
			{
				isPlot1Succeded=false;
				e.printStackTrace();
			}
		}
		
		System.out.println("Plot 1 pris");
		
		if(!isStackFree1Succeded)
		{
			try 
			{			
				System.out.println("en position ("+real_state.robot.getPosition().x+", "+real_state.robot.getPosition().y+") avant de deposer la pile sur l'estrade");
				scriptmanager.getScript(ScriptNames.FREE_STACK).goToThenExec(1, real_state, true, emptyHook ); // On lache notree pile devnt (bientot sur l'estrade
				real_state.robot.moveLengthwise(-300);		//On recule pour ne pas taper (le PF evitera ca)
				isStackFree1Succeded=true;
			}
			catch (UnableToMoveException | SerialConnexionException | PathNotFoundException | SerialFinallyException e) 
			{
				e.printStackTrace();
				isStackFree1Succeded=false;
			}
		}
		
		System.out.println("Pile vidée");
		if(!isDropGlass1Succeded)
		{		
			try 
			{	
				/*Le robot n'en est pas encore capable mais ca va venir avec le bas niveau et les fonctions "bras au milieu" etc
				/*	scriptmanager.getScript(ScriptNames.TAKE_TENNIS_BALL).goToThenExec(1, real_state, true, emptyHook );*/
				
				System.out.println("en position ("+real_state.robot.getPosition().x+", "+real_state.robot.getPosition().y+") avant de deposer le verre");
				scriptmanager.getScript(ScriptNames.DROP_GLASS).goToThenExec(1, real_state, true, emptyHook );//On depose 1 verre dans notre zone
				System.out.println("en position ("+real_state.robot.getPosition().x+", "+real_state.robot.getPosition().y+") après avoir deposé le verre");
				isDropGlass1Succeded=true;
			}
			catch (UnableToMoveException | SerialConnexionException | PathNotFoundException | SerialFinallyException e) 
			{
				e.printStackTrace();
				isDropGlass1Succeded=false;
			}
		}
		
		System.out.println("Verre deposé");
		if(!isPlot0Succeded)
		{	
			try
			{
				System.out.println("en position ("+real_state.robot.getPosition().x+", "+real_state.robot.getPosition().y+") avant le plot 0");
				scriptmanager.getScript(ScriptNames.GRAB_PLOT).goToThenExec(0, real_state, true, emptyHook ); // On recupere le plot en face de l'escalier
				isPlot0Succeded=true;
			}
			catch (UnableToMoveException | SerialConnexionException | PathNotFoundException | SerialFinallyException e) 
			{
				isPlot0Succeded=false;
				e.printStackTrace();
			}
		}

		System.out.println("Plot 0 pris");
		
		if(!isGrabGlass2Succeded)
		{	
			try 
			{
				System.out.println("en position ("+real_state.robot.getPosition().x+", "+real_state.robot.getPosition().y+") avant le verre 2");
				scriptmanager.getScript(ScriptNames.GRAB_GLASS).goToThenExec(2, real_state, true, emptyHook ); // ON recupere le verre devant l'estrade
				isGrabGlass2Succeded=true;
			}
			catch (UnableToMoveException | SerialConnexionException | PathNotFoundException | SerialFinallyException e) 
			{
				e.printStackTrace();
				isGrabGlass2Succeded=false;
			}
		}
		
		System.out.println("Verre 2 pris");
		if(!isClap3Succeded)
		{	
			try 
			{			
				System.out.println("en position ("+real_state.robot.getPosition().x+", "+real_state.robot.getPosition().y+") avant le clap 3");
				scriptmanager.getScript(ScriptNames.CLOSE_CLAP).goToThenExec(3, real_state, true, emptyHook ); // ON recupere le verre devant l'estrade
				isClap3Succeded=true;
			}
			catch (UnableToMoveException | SerialConnexionException | PathNotFoundException | SerialFinallyException e) 
			{
				e.printStackTrace();
				isClap3Succeded=false;
			}
		}
		
		System.out.println("Clap 3 fermé");
		if(!isDropGlass2Succeded)
		{
			try 
			{
				System.out.println("en position ("+real_state.robot.getPosition().x+", "+real_state.robot.getPosition().y+") avant le deposage en zone basse enemie");
				scriptmanager.getScript(ScriptNames.DROP_GLASS).goToThenExec(2, real_state, true, emptyHook ); // On depose le verre chez les ennemis, en bas.
				isDropGlass2Succeded=true;
			}
			catch (UnableToMoveException | SerialConnexionException | PathNotFoundException | SerialFinallyException e) 
			{
				e.printStackTrace();
				isDropGlass2Succeded=false;
			}
		}
		
		if(!isDropGlass3Succeded)
		{
			try 
			{
				System.out.println("en position ("+real_state.robot.getPosition().x+", "+real_state.robot.getPosition().y+") avant le deposage en zone haute enemie");	
				scriptmanager.getScript(ScriptNames.DROP_GLASS).goToThenExec(3, real_state, true, emptyHook ); // On depose le verre chez les ennemis, en haut.
				isDropGlass3Succeded=true;
			}
			catch (UnableToMoveException | SerialConnexionException | PathNotFoundException | SerialFinallyException e) 
			{
				e.printStackTrace();
				isDropGlass3Succeded=false;
			}
		}
		
		if(!isGrabGlass56Succeded)
		{
			try 
			{
				System.out.println("en position ("+real_state.robot.getPosition().x+", "+real_state.robot.getPosition().y+") avant le plot 5 et 6");
				scriptmanager.getScript(ScriptNames.GRAB_PLOT).goToThenExec(56, real_state, true, emptyHook );//On recupere les 2 plots a droite de l'escalier
				isGrabGlass56Succeded=true;
			}
			catch (UnableToMoveException | SerialConnexionException | PathNotFoundException | SerialFinallyException e) 
			{
				e.printStackTrace();
				isGrabGlass56Succeded=false;
			}
		}
		
		// TODO  Balles de Pop Corn ?
		if(!isGrabGlass7Succeded)
		{
			try 
			{
				real_state.robot.turn(0);	
				System.out.println("en position ("+real_state.robot.getPosition().x+", "+real_state.robot.getPosition().y+") avant le plot 7");
				scriptmanager.getScript(ScriptNames.GRAB_PLOT).goToThenExec(7, real_state, true, emptyHook );//On recupere le dernier plot
				isGrabGlass7Succeded=true;
			}
			catch (UnableToMoveException | SerialConnexionException | PathNotFoundException | SerialFinallyException e) 
			{
				e.printStackTrace();
				isGrabGlass7Succeded=false;
			}
		}
		if(!isStackFree2Succeded)
		{
			try 
			{
				
				System.out.println("en position ("+real_state.robot.getPosition().x+", "+real_state.robot.getPosition().y+") avant le deposage de la pile dans notre zone");
				scriptmanager.getScript(ScriptNames.FREE_STACK).goToThenExec(2, real_state, true, emptyHook ); // On libere la pile ans notre zone
				isStackFree2Succeded=true;
			}
			catch (UnableToMoveException | SerialConnexionException | PathNotFoundException | SerialFinallyException e) 
			{
				e.printStackTrace();
				isStackFree2Succeded=false;
			}
		}
	}

	
}
