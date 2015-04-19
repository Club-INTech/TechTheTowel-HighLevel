import hook.Hook;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import robot.Robot;
import robot.cardsWrappers.SensorsCardWrapper;
import scripts.ScriptManager;
import smartMath.Vec2;
import strategie.GameState;
import strategie.Strategie;
import threads.ThreadTimer;
import utils.Config;
import utils.Sleep;
import container.Container;
import enums.ActuatorOrder;
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
 * @author marsu, paul
 *
 */
public class Main
{
	static Container container;
	static Config config;
	static Strategie strategos;
	static GameState<Robot> real_state;
	static ArrayList<Hook> emptyHook;
	static ScriptManager scriptmanager;
	static SensorsCardWrapper mSensorsCardWrapper;
	
	
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
		int numberOfTrymatchSetup=0;
		
		
		//nombre maximum d'essai autorisee pour le container et la serie
		int maximumOfTryContainer=5;
		int maximumOfTrySerial=3;
		int maximumOfmatchSetup=5;
		
		// booleen explicitant si on a reussi l'initialisation
	    boolean isInitialisationDone = false;

		


		System.out.println("=== Robot INTech 2015 : initialisation ===");
        // si on veut exécuter un test unitaire sur la rasbe, recopier test.nomDeLaClasseDeTest
		//JUnitCore.main(		"tests.JUnit_DeplacementsTest");  
		
		// Système d'injection de dépendances
		//tant que le nombre d'essai n'est pas trop grand on recommence
		while (numberOfTryContainer<maximumOfTryContainer || numberOfTrySerial<maximumOfTrySerial || !isInitialisationDone)
		{
			try 
			{
				container = new Container();
				container.getService(ServiceNames.LOG);
				config = (Config) container.getService(ServiceNames.CONFIG);
			
				//configColor();
				
				
				// initialise les singletons
				real_state = (GameState<Robot>) container.getService(ServiceNames.GAME_STATE);
			    scriptmanager = (ScriptManager) container.getService(ServiceNames.SCRIPT_MANAGER);
			    mSensorsCardWrapper = (SensorsCardWrapper) container.getService(ServiceNames.SENSORS_CARD_WRAPPER);
			    strategos = (Strategie) container.getService(ServiceNames.STRATEGIE);
			    emptyHook = new ArrayList<Hook>(); //TODO la veritable liste des hooks pour le match
			    
			    config.updateConfig(); // instancie la couleur, etc
			    
			    isInitialisationDone=true;
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
		
		isInitialisationDone=false;
		while(numberOfTrymatchSetup<maximumOfmatchSetup || !isInitialisationDone) // On retente jusqu'à ce que ca fonctionne. 
		{
			try 
			{
				matchSetUp(real_state.robot);
				isInitialisationDone=true;
			}
			catch (SerialConnexionException e) 
			{
				numberOfTrySerial++;

				if(numberOfTrymatchSetup<maximumOfmatchSetup)
					System.out.println ("erreur dans le matchSetup et la connexion serie");
				else 
				{
					System.out.println ("erreur critique dans la connexion serie");
					e.printStackTrace();
				}
			}
		}

		//initialisation du match
		configColor();
		real_state.robot.setPosition(new Vec2 (1381,1000));
		real_state.robot.setOrientation(Math.PI);
		
		// Threads
		container.startAllThreads();
		
		// attends que le jumper soit retiré
		waitMatchBegin();
		
		
		//premiere action du match
		System.out.println("Le robot commence le match");
		
		strategos.updateConfig();
		strategos.IA();
	}
	
	
	/**
	 * le set up du match en cours (mise en place des actionneurs)
	 * @param robot le robot a setuper
	 * @throws SerialConnexionException si l'ordinateur n'arrive pas a communiquer avec les cartes
	 */
	private static void matchSetUp(Robot robot) throws SerialConnexionException
	{
		robot.useActuator(ActuatorOrder.ELEVATOR_GROUND, true);
		robot.useActuator(ActuatorOrder.ELEVATOR_OPEN_JAW, false);

		robot.useActuator(ActuatorOrder.OPEN_LEFT_GUIDE, false);
		robot.useActuator(ActuatorOrder.OPEN_RIGHT_GUIDE, true);
		
		robot.useActuator(ActuatorOrder.ARM_LEFT_CLOSE, false);
		robot.useActuator(ActuatorOrder.ARM_RIGHT_CLOSE, false);
		
		robot.useActuator(ActuatorOrder.CLOSE_RIGHT_GUIDE, true);
		robot.useActuator(ActuatorOrder.CLOSE_LEFT_GUIDE, true);
		
		robot.useActuator(ActuatorOrder.LEFT_CARPET_FOLDUP, false);
		robot.useActuator(ActuatorOrder.RIGHT_CARPET_FOLDUP, false);
		
		robot.useActuator(ActuatorOrder.LOW_LEFT_CLAP, false);
		robot.useActuator(ActuatorOrder.LOW_RIGHT_CLAP, false);
		
		robot.useActuator(ActuatorOrder.ELEVATOR_CLOSE_JAW, true);
		
		robot.useActuator(ActuatorOrder.ELEVATOR_LOW, true);
	}

	/**
	 * Recale le robot
	 * @throws Exception
	 */
	void recalerRobot()
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
	

	/**
	 * Demande si la couleur est verte au jaune
	 * @throws Exception
	 */
	static void configColor()
	{

		String couleur = "";
		while(!couleur.contains("jaune") && !couleur.contains("vert"))
		{
			System.out.println("Rentrez \"vert\" ou \"jaune\" : ");
			BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in)); 
			 
			try 
			{
				couleur = keyboard.readLine();
			}
			catch (IOException e) 
			{
				System.out.println("Eurreur IO: le clavier est il bien branché ?");
			} 
			if(couleur.contains("jaune"))
				config.set("couleur","jaune");
			else if(couleur.contains("vert"))
				config.set("couleur", "vert");
			
		}
		
	}
	
}
