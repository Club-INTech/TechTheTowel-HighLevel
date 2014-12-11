package tests;

//@author Théo + architecture Paul

import hook.types.HookFactory;

import org.junit.Before;

import pathDingDing.PathDingDing;
import robot.Robot;
import robot.RobotReal;
import scripts.DropCarpet;
import strategie.GameState;
import java.util.ArrayList;
import hook.Hook;
import scripts.CloseClap;
import table.Table;
import enums.ServiceNames;

import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialConnexionException;

public class JUnit_Claps extends JUnit_Test {

	CloseClap scriptCloseClap;
	DropCarpet scriptCarpet;
	RobotReal robot;
	Table table;
	PathDingDing pathfinding;
	HookFactory hookFactory;
	GameState<Robot> game;
	ArrayList<Hook> emptyHook = new ArrayList<Hook>();

	@Before
	public void setUp() throws Exception {
		super.setUp();
		
		table = (Table)container.getService(ServiceNames.TABLE);
		robot = (RobotReal)container.getService(ServiceNames.ROBOT_REAL);
		hookFactory = (HookFactory)container.getService(ServiceNames.HOOK_FACTORY);
		pathfinding = new PathDingDing(table);
		scriptCloseClap = new CloseClap(hookFactory, config, log);
	}
	
	public void test() throws UnableToMoveException, SerialConnexionException
	{
		scriptCloseClap.execute(1, null, false);
		//Test de fermeture des claps  1 , 2 , 3 (du plus proche au plu loin du depart)
	}

	{/* Premier jet du test, fonctionne 
	@Test
	public void test()
	{
		try 
		{
			int distanceBetweenClaps=300;//distance entre 2 claps (bout identique de claque clap, ex : charnieres)
			int lenghtClap=160; //LOngueur clap
			int lenghtStair=200; // L'estradee fait 100, on met 200
			int distanceRightLeft=1750; // distance entre les deux triplettes de claps : entre le 3 et le 5
			int sleepTime = 2500; //le temps d'attente (en ms) entre la commande de dépose du tapis ( le bras se baisse) et la commande qui remonte le bras

			
			//A partir de la zone de depart
			///
			 //		________
			 // 		|		|
			 // 		V
			 //
			actionneurs.lowLeftClap();
			actionneurs.lowRightClap();
			
			locomotion.avancer(500);
			Sleep.sleep(sleepTime);

			locomotion.tourner(-Math.PI/2);
			Sleep.sleep(sleepTime);                              
			locomotion.avancer(750);       
			Sleep.sleep(sleepTime);
			
			locomotion.tourner(Math.PI);
            Sleep.sleep(sleepTime);
			locomotion.avancer(-500);
			Sleep.sleep(sleepTime);// A partir du coin de la table
			
			actionneurs.midLeftClap();//On ouvre puis on avance
			Sleep.sleep(sleepTime);
			locomotion.avancer(lenghtClap);//On baisse le premier clap, le notre
			Sleep.sleep(sleepTime);	
			actionneurs.highLeftClap();
			Sleep.sleep(sleepTime);
				
			locomotion.avancer(2*distanceBetweenClaps-lenghtClap-20);			//On evite le clap ennemi
			Sleep.sleep(sleepTime);	
			
			actionneurs.midLeftClap();	//On ferme le notre
			Sleep.sleep(sleepTime);	

			locomotion.tourner(Math.PI/2);
			Sleep.sleep(sleepTime);	
			actionneurs.lowLeftClap();
			
			//On est devant l'estrade !
				
			//Partie deplacements du clap 3 au clap 6, pret à fermer le 5
			// On se tourne, on avance pour eviter l'estrade
			
			locomotion.avancer(lenghtStair); 
			Sleep.sleep(sleepTime);	
				
			//On se tourne, on avance vers le clap interessant
			locomotion.tourner(Math.PI);
			Sleep.sleep(sleepTime);	
			locomotion.avancer(distanceRightLeft); 
			Sleep.sleep(sleepTime);
			
			actionneurs.midRightClap();
			Sleep.sleep(sleepTime);	
			
			//On se tourne pour s'avancer des claps
			locomotion.tourner(-Math.PI/2);
			Sleep.sleep(sleepTime);	
			locomotion.avancer(lenghtStair);
			Sleep.sleep(sleepTime);	
				
			//On se tourne dans le bon sens en fermant le notre
			locomotion.tourner(0);
			Sleep.sleep(sleepTime);	
			
			locomotion.avancer(lenghtClap);
			Sleep.sleep(sleepTime);	
			
			//Et on s'en va !

			locomotion.tourner(Math.PI/2);
			Sleep.sleep(sleepTime);	
			actionneurs.lowRightClap();
			locomotion.avancer(lenghtStair);
			Sleep.sleep(sleepTime);	
		} 
		catch (SerialException e) 
		{
			log.debug("erreur dans le texte serie",this);
			e.printStackTrace();
		}
	}*/}

}
