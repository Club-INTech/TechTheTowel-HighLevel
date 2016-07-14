import container.Container;
import enums.ServiceNames;
import enums.Speed;
import exceptions.ContainerException;
import exceptions.serial.SerialManagerException;
import hook.Hook;
import robot.Locomotion;
import robot.Robot;
import robot.cardsWrappers.SensorsCardWrapper;
import scripts.ScriptManager;
import strategie.GameState;
import table.Table;
import threads.ThreadTimer;
import utils.Config;
import utils.Log;
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
	static GameState<Robot> realState;
	static ArrayList<Hook> emptyHook = new ArrayList<>();
	static ScriptManager scriptmanager;
	static SensorsCardWrapper mSensorsCardWrapper;
	static Locomotion mLocomotion;
	
	
// dans la config de debut de match, toujours demander une entrée clavier assez longue (ex "oui" au lieu de "o", pour éviter les fautes de frappes. Une erreur a ce stade coûte cher.
// ---> En même temps si tu tapes n à la place de o, c'est que tu es vraiment con.  -Discord
// PS : Les vérifications et validations c'est pas pour les chiens.
	@SuppressWarnings("unchecked")
	public static void main(String[] args)
	{
		try
		{
			container = new Container();
			container.getService(ServiceNames.LOG);
			config = (Config) container.getService(ServiceNames.CONFIG);
			realState = (GameState<Robot>) container.getService(ServiceNames.GAME_STATE);
			scriptmanager = (ScriptManager) container.getService(ServiceNames.SCRIPT_MANAGER);
			mSensorsCardWrapper = (SensorsCardWrapper) container.getService(ServiceNames.SENSORS_CARD_WRAPPER);
			mLocomotion=(Locomotion) container.getService(ServiceNames.LOCOMOTION);
			config.updateConfig();

            Thread.currentThread().setPriority(6);

            // TODO : faire une initialisation du robot et de ses actionneurs
			realState.robot.setPosition(Table.entryPosition);
			realState.robot.setOrientation(Math.PI);
			realState.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);

			container.startAllThreads();
			waitMatchBegin();

			System.out.println("Le robot commence le match");

			// TODO : lancer l'IA
			
			Log.stop();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ContainerException e) {
			e.printStackTrace();
		} catch (SerialManagerException e) {
			e.printStackTrace();
		}
	}

	

	/**
	 * Attends que le match soit lancé
	 * cette fonction prend fin quand le match a démarré
	 */
	static void waitMatchBegin()
	{

		System.out.println("Robot pret pour le match, attente du retrait du jumper");

		while(mSensorsCardWrapper.isJumperAbsent())
		{
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		while(!mSensorsCardWrapper.isJumperAbsent())
		{
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
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
		while(!couleur.contains("violet") && !couleur.contains("vert")) // TODO : modifier les couleurs
		{
			System.out.println("Rentrez \"vert\" ou \"violet\" : ");
			BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in)); 
			 
			try 
			{
				couleur = keyboard.readLine();
			}
			catch (IOException e) 
			{
				System.out.println("Erreur IO: le clavier est il bien branché ?");
			} 
			if(couleur.contains("violet"))
				config.set("couleur","violet");
			else if(couleur.contains("vert"))
				config.set("couleur", "vert");	
		}
	}	
}
