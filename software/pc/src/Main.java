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
// TODO: refactor
public class Main
{
	static Container container;
	static Config config;
	static GameState<Robot> real_state; //TODO le robot de ce game state est il un robot real ?
	static ArrayList<Hook> emptyHook;
	static ScriptManager scriptmanager;
	static SensorsCardWrapper mSensorsCardWrapper;
	static boolean mustStartQuickly;
	
	
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
				                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            
				
				//Début des paramétrages
				configColor();
			
				
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
		real_state.robot.setPosition(new Vec2(1381,1000));
		real_state.robot.setOrientation(Math.PI);
		
		
		
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
		
		//premier script
		
		try 
		{
			scriptmanager.getScript(ScriptNames.DROP_CARPET).goToThenExec(0, real_state, true, emptyHook );
		}
		catch (UnableToMoveException | SerialConnexionException e) 
		{
			// TODO Main erreur critique :
			//attention ce sont surement des erreurs dans le finally d'un script donc elle servent a proteger le meca !
			//ou un robot ennemi devant. Donc beaucoup moins critique (ce serai bie de pouvoir differencer les deux)
			e.printStackTrace();
		
		} 
		catch (PathNotFoundException e)
		{
			//TODO: le pathfinding ne trouve pas de chemin
			e.printStackTrace();
		} catch (SerialFinallyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//second script
		
		try 
		{
			scriptmanager.getScript(ScriptNames.CLOSE_CLAP).goToThenExec(12, real_state, true, emptyHook );
		}
		catch (UnableToMoveException | SerialConnexionException e) 
		{
			// TODO Main erreur critique :
			//attention ce sont surement des erreurs dans le finally d'un script donc elle servent a proteger le meca !
			//ou un robot ennemi devant. Donc beaucoup moins critique (ce serai bie de pouvoir differencer les deux)
			e.printStackTrace();
		
		} 
		catch (PathNotFoundException e)
		{
			e.printStackTrace();
			
		} 
		catch (SerialFinallyException e) 
		{
			e.printStackTrace();
		}
		
		
		//dernier script
		
		try 
		{
			scriptmanager.getScript(ScriptNames.CLOSE_CLAP).goToThenExec(3, real_state, true, emptyHook );
		}
		catch (UnableToMoveException | SerialConnexionException e) 
		{
			// TODO Main erreur critique :
			//attention ce sont surement des erreurs dans le finally d'un script donc elle servent a proteger le meca !
			//ou un robot ennemi devant. Donc beaucoup moins critique (ce serai bie de pouvoir differencer les deux)
			e.printStackTrace();
		
		} 
		catch (PathNotFoundException e)
		{
			e.printStackTrace();
			
		} catch (SerialFinallyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("match fini !");


		//Le match s'arrête
		container.destructor();
		
		
		
	}
	
	
	

	/**
	 * Demande si la couleur est rouge au jaune
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
	

	
}
