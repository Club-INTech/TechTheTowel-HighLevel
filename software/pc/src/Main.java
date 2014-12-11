import hook.Hook;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import robot.Robot;
import robot.cardsWrappers.SensorsCardWrapper;
import scripts.ScriptManager;
import strategie.GameState;
import threads.ThreadTimer;
import utils.Config;
import container.Container;
import enums.ScriptNames;
import enums.ServiceNames;
import exceptions.ContainerException;
import exceptions.Locomotion.BlockedException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialConnexionException;
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
	static GameState<Robot> real_state;
	static ArrayList<Hook> emptyHook;
	static ScriptManager scriptmanager;
	static SensorsCardWrapper capteurs;
	static boolean doitFaireDepartRapide;
	
	
// dans la config de debut de match, toujours demander une entrée clavier assez longue (ex "oui" au lieu de "o", pour éviter les fautes de frappes. Une erreur a ce stade coûte cher.
	
	/**
	 * Point d'entrée du programme. C'est ici que le code commence par être exécuté 
	 * @param args chaine de caractère des arguments de la ligne de commande
	 * @throws Exception TODO : quels sont les exeptions lancés ?
	 */
	@SuppressWarnings("unchecked")
	public static void main(String[] args)
	{
		

		System.out.println("=== Robot INTech 2015 : initialisation ===");
        // si on veut exécuter un test unitaire sur la rapbe, recopier test.nomDeLaClasseDeTest
		//JUnitCore.main(		"tests.JUnit_DeplacementsTest");  
		
		
		// Système d'injection de dépendances
		try 
		{

			container = new Container();
			container.getService(ServiceNames.LOG);
			config = (Config) container.getService(ServiceNames.CONFIG);
			                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            
			
			//Début des paramétrages
			//configCouleur();
		
			
			// initialise les singletons
			real_state = (GameState<Robot>) container.getService(ServiceNames.GAME_STATE);
		    scriptmanager = (ScriptManager) container.getService(ServiceNames.SCRIPT_MANAGER);
		    capteurs = (SensorsCardWrapper) container.getService(ServiceNames.SENSORS_CARD_WRAPPER);
		    emptyHook = new ArrayList<Hook>();
		    

		
		} 
		catch (ContainerException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		catch (SerialManagerException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Threads
		//
		//	container.demarreTousThreads();

		// attends que le jumper soit retiré
		try 
		{
			attendreDebutMatch();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
				
		//initialisation du match		
		System.out.println("Le robot commence le match");
				 
		try 
		{
			scriptmanager.getScript(ScriptNames.EXIT_START_ZONE).goToThenExec(0, real_state, true, emptyHook );
		} 
		catch (UnableToMoveException | SerialConnexionException | BlockedException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//debut du match
		System.out.println("debut du match");
		try 
		{
			scriptmanager.getScript(ScriptNames.DROP_CARPET).goToThenExec(0, real_state, true, emptyHook );
			scriptmanager.getScript(ScriptNames.CLOSE_CLAP).goToThenExec(12, real_state, true, emptyHook );
			scriptmanager.getScript(ScriptNames.CLOSE_CLAP).goToThenExec(3, real_state, true, emptyHook );
		}
		catch (UnableToMoveException | SerialConnexionException | BlockedException e1) 
		{
				// TODO Auto-generated catch block
				e1.printStackTrace();
		}
		System.out.println("fini !");


		//Le match s'arrête
		container.destructor();
		
		
		
	}
	
	
	

	/**
	 * Demande si la couleur est rouge au jaune
	 * @throws Exception
	 */
	static void configCouleur()
	{

		String couleur = "";
		while(!couleur.contains("rouge") && !couleur.contains("jaune"))
		{
			System.out.println("Rentrez \"jaune\" ou \"rouge\" : ");
			BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in)); 
			 
			try {
				couleur = keyboard.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			if(couleur.contains("rouge"))
				config.set("couleur","rouge");
			else if(couleur.contains("jaune"))
				config.set("couleur", "jaune");
			
		}
		
	}
	

	/**
	 * Recale le robot
	 * @throws Exception
	 */
	static void recalerRobot()
	{

		// System.out.println("Pret au recalage, appuyez sur entrée pour continuer");
		System.out.println("TODO : code de recalage");// TODO

		
	}	
	

	/**
	 * Attends que le match soit lancé
	 * @throws Exception
	 */
	static void attendreDebutMatch()
	{

		System.out.println("Robot pret pour le match, attente du retrait du jumper");
		
		// hack si le jumper est inopérant
		ThreadTimer.matchStarted = true;

		// while(!capteurs.demarrage_match())
		//	 	Sleep.sleep(100);
	}
	

	
}
