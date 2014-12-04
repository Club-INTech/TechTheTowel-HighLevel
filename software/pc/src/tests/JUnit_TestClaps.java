package tests;

//Autho Théo + architecture Paul

import org.junit.Before;
import org.junit.Test;
import exceptions.serial.SerialException;
import robot.cards.ActuatorsManager;
import robot.cards.Locomotion;
import utils.Sleep;


public class JUnit_TestClaps extends JUnit_Test {
	
	Locomotion locomotion;
	ActuatorsManager actionneurs;
	
	@Before
	public void setUp() throws Exception {
		super.setUp();
		locomotion = (Locomotion)container.getService("Deplacements");
		actionneurs = (ActuatorsManager)container.getService("Actionneurs");
	}

	@Test
	public void test()
	{
		try 
		{
			int distanceBetweenClaps=300;//distance entre 2 claps (bout identique de claque clap, ex : charnieres)
			int lenghtClap=160; //LOngueur clap
			int lenghtStair=200; // L'estradee fait 100, on met 200
			int distanceRightLeft=1600; // distance entre les deux triplettes de claps : entre le 3 et le 5
			int sleepTime = 800; //le temps d'attente (en ms) entre la commande de dépose du tapis ( le bras se baisse) et la commande qui remonte le bras

			
			//A partir de la zone de depart
			/*
			 *		________
			 * 		|		|
			 * 		V
			 
			locomotion.avancer((700));
			Sleep.sleep(2000);

			locomotion.tourner(-Math.PI/2);
			Sleep.sleep(2000);//                                
			locomotion.avancer(750);       
			Sleep.sleep(2000);
			
            locomotion.tourner(0);          
            Sleep.sleep(2000);
			locomotion.avancer(600);
			Sleep.sleep(2000);
			locomotion.tourner(Math.PI);
			Sleep.sleep(2000);
			*/
			
			
			//Prevoir marche arriere
			
			// A partir du coin de la table
			
			actionneurs.midLeftClap();//On ouvre puis on avance
			Sleep.sleep(sleepTime);
			locomotion.avancer(lenghtClap);//On baisse le premier clap, le notre
			Sleep.sleep(sleepTime);	
			actionneurs.highLeftClap();
			Sleep.sleep(sleepTime);
			
			//On evite le clap ennemi
			
			locomotion.avancer(2*distanceBetweenClaps-lenghtClap);		
			Sleep.sleep(sleepTime);	
			
			//On ferme le notre
			
			actionneurs.midLeftClap();
			Sleep.sleep(sleepTime);	

			locomotion.tourner(Math.PI/2);
			Sleep.sleep(sleepTime);	
			actionneurs.lowLeftClap();
			Sleep.sleep(sleepTime);	
	
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
				
			//On se tourne pour s'avancer des claps
			locomotion.tourner(-Math.PI/2);
			Sleep.sleep(sleepTime);	
			locomotion.avancer(lenghtStair);
			Sleep.sleep(sleepTime);	
				
			//On se tourne dans le bon sens
			locomotion.tourner(0);
			Sleep.sleep(sleepTime);	
			
			//On ferme le notre
			
			actionneurs.midRightClap();
			Sleep.sleep(sleepTime);	
			
			locomotion.avancer(lenghtClap);
			Sleep.sleep(sleepTime);	
			
			//Et on s'en va !

			locomotion.tourner(-Math.PI/2);
			Sleep.sleep(sleepTime);	
			
			locomotion.avancer(lenghtStair);
			Sleep.sleep(sleepTime);	
			
			actionneurs.lowRightClap();


		} 
		catch (SerialException e) 
		{
			log.debug("erreur dans le texte serie",this);
			e.printStackTrace();
		}
	}

}
