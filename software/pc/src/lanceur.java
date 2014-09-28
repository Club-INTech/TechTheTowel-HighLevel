//import hook.sortes.HookGenerator;

import java.io.BufferedReader;
import java.io.InputStreamReader;

//import org.junit.runner.JUnitCore;



import robot.RobotVrai;
import robot.cartes.Capteurs;
import robot.cartes.Deplacements;
import robot.hautniveau.DeplacementsHautNiveau;
import scripts.ScriptManager;
import strategie.GameState;
import threads.ThreadTimer;
//import tests.JUnit_StrategieThreadTest;
//import sun.rmi.runtime.Log;
//import threads.ThreadTimer;
import utils.Read_Ini;
import container.Container;


/**
 * Classe de lancement du robot.
 * @author marsu
 *
 */

		
public class lanceur
{
	static Container container;
	static Read_Ini config;
	static GameState<RobotVrai> real_state;
	static ScriptManager scriptmanager;
	static DeplacementsHautNiveau deplacements;
	static Deplacements dep;
	static Capteurs capteurs;
	static boolean doitFaireDepartRapide;
	
	
// dans la config de debut de match, toujours demandé une entrée clavier assez longue (ex "oui" au lieu de "o", pour éviter les fautes de frappes. Une erreur a ce stade coûte cher.
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception
	{
		

		System.out.println("=== Robot INTech 2015 : initialisation ===");
		System.out.println("LANCEUR DE TEST: version de nettoyage du code 2014");
		
        // si on veut exécuter un test unitaire sur la rapbe, recopier test.nomDeLaClasseDeTest
		//JUnitCore.main(		"tests.JUnit_DeplacementsTest");  
		
		
		// Système d'injection de dépendances
		container = new Container();
		container.getService("Log");
		config = (Read_Ini) container.getService("Read_Ini");
	
		
		                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            
		
		//Début des paramétrages
		//configCouleur();
	
		
		// initialise les singletons
	    dep = (Deplacements)container.getService("Deplacements");
		real_state = (GameState<RobotVrai>) container.getService("RealGameState");
	    scriptmanager = (ScriptManager) container.getService("ScriptManager");
	    deplacements = (DeplacementsHautNiveau)container.getService("DeplacementsHautNiveau");
	    capteurs = (Capteurs) container.getService("Capteur");
	    
		real_state.robot.initialiser_actionneurs_deplacements();
		

		// Threads
		//
	//	container.demarreTousThreads();

		
		// mettre le checkup du robot.
		// ici juste des tests de déplcement
		System.out.println("deplacement bas niveau");
		dep.avancer(300);
		Thread.sleep(1000);
		dep.avancer(-300);
		Thread.sleep(1000);
		dep.tourner(1.0);
		Thread.sleep(1000);
		dep.tourner(1.57);
		Thread.sleep(1000);
		System.out.println("fini !");
		System.out.println("deplacement haut niveau");
		deplacements.avancer(100, null, true);
		System.out.println("fini !");


		
		
		
		// attends que le jumper soit retiré
		attendreDebutMatch();
		
		
		System.out.println("Le robot commence le match");
		 
		
		
		//Le match s'arrête
		container.destructeur();
	}
	
	
	

	/**
	 * Demande si la couleur est rouge au jaune
	 * @throws Exception
	 */
	static void configCouleur()  throws Exception
	{

		String couleur = "";
		while(!couleur.contains("rouge") && !couleur.contains("jaune"))
		{
			System.out.println("Rentrez \"jaune\" ou \"rouge\" : ");
			BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in)); 
			 
			couleur = keyboard.readLine(); 
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
	static void recalerRobot()  throws Exception
	{

		// System.out.println("Pret au recalage, appuyez sur entrée pour continuer");
		System.out.println("TODO : code de recalage");// TODO

		
	}	
	

	/**
	 * Attends que le match soit lancé
	 * @throws Exception
	 */
	static void attendreDebutMatch()  throws Exception
	{

		System.out.println("Robot pret pour le match, attente du retrait du jumper");
		
		// hack si le jumper est inopérant
		ThreadTimer.match_demarre = true;

		// while(!capteurs.demarrage_match())
		//	 	Sleep.sleep(100);
	}
	

	
}