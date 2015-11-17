import container.Container;
import hook.Hook;
import robot.Locomotion;
import robot.Robot;
import robot.cardsWrappers.SensorsCardWrapper;
import scripts.ScriptManager;
import strategie.GameState;
import strategie.Strategie;
import threads.ThreadTimer;
import utils.Config;
import utils.Sleep;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


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
	static GameState<Robot> realState;
	static ArrayList<Hook> emptyHook;
	static ScriptManager scriptmanager;
	static SensorsCardWrapper mSensorsCardWrapper;
	static Locomotion mLocomotion;
	
	
// dans la config de debut de match, toujours demander une entrée clavier assez longue (ex "oui" au lieu de "o", pour éviter les fautes de frappes. Une erreur a ce stade coûte cher.
	
	public static void main(String[] args)
	{
		//TODO main
	}

	

	/**
	 * Attends que le match soit lancé
	 * cette fonction prend fin quand le match a démarré
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
				System.out.println("Erreur IO: le clavier est il bien branché ?");
			} 
			if(couleur.contains("jaune"))
				config.set("couleur","jaune");
			else if(couleur.contains("vert"))
				config.set("couleur", "vert");	
		}
	}	
}
