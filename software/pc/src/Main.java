import container.Container;
import enums.ActuatorOrder;
import enums.ServiceNames;
import enums.Speed;
import exceptions.ContainerException;
import exceptions.serial.SerialConnexionException;
import exceptions.serial.SerialManagerException;
import hook.Hook;
import robot.Locomotion;
import robot.Robot;
import robot.cardsWrappers.SensorsCardWrapper;
import scripts.ScriptManager;
import strategie.GameState;
import strategie.Strategie;
import table.Table;
import threads.ThreadTimer;
import utils.Config;
import utils.Log;
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
	static ArrayList<Hook> emptyHook = new ArrayList<>();
	static ScriptManager scriptmanager;
	static SensorsCardWrapper mSensorsCardWrapper;
	static Locomotion mLocomotion;
	
	
// dans la config de debut de match, toujours demander une entrée clavier assez longue (ex "oui" au lieu de "o", pour éviter les fautes de frappes. Une erreur a ce stade coûte cher.
// ---> En même temps si tu tapes n à la place de o, c'est que tu es vraiment con.  -Discord
// PS : Les vérifications et validations c'est pas pour les chiens.
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
			strategos = (Strategie) container.getService(ServiceNames.STRATEGIE);
			mLocomotion=(Locomotion) container.getService(ServiceNames.LOCOMOTION);
			config.updateConfig();

            Thread.currentThread().setPriority(6);

			realState.robot.setPosition(Table.entryPosition);
			realState.robot.setOrientation(Math.PI);
			realState.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);

			try {
				realState.robot.useActuator(ActuatorOrder.CLOSE_DOOR, false);
				realState.robot.useActuator(ActuatorOrder.CLOSE_DOOR_LEFT, false);
				realState.robot.useActuator(ActuatorOrder.ARM_INIT, false);
			} catch (SerialConnexionException e) {
				e.printStackTrace();
			}

			container.startAllThreads();

			waitMatchBegin();

			System.out.println("Le robot commence le match");

			strategos.updateConfig();
			strategos.IA();
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
		while(!couleur.contains("jaune") && !couleur.contains("vert"))
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
