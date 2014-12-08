package scripts;

import java.util.ArrayList;

import pathdinding.Pathfinding;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialException;
import hook.Hook;
import hook.types.HookGenerator;
import robot.cards.ActuatorsManager;
import robot.highlevel.LocomotionHiLevel;
import smartMath.Vec2;
import strategie.GameState;
import utils.Config;
import utils.Log;
import utils.Sleep;

/**
 * 
 * @author theo
 * Exemple sur Paul
 *Sript Fermer les claps
 *
 *Table :
 *
 *    ___________________
 *   |					|
 * 	 |					|
 *   |					|Debut du robot ici
 *   |					|
 *   |		N° claps	|
 *    6_5_4________3_2_1
 *    
 *    
 *    
 *    Angles :
 *    
 *    ___________________
 *   |		PI/2		|
 * 	 |					|
 *   |PI			   0|  Debut du robot ici
 *   |					|
 *   |					|
 *   |______-PI/2_______|
 *   
 */

public class CloseClap extends Script 
{
	private boolean OpenedLeftClap=false,OpenedRightClap=false,ClosedClap1=false,ClosedClap2=false,ClosedClap3=false,ClosedClap4=false,ClosedClap5=false,ClosedClap6=false;//clap fermé au debut
	private int distanceBetweenClaps=300;//distance entre 2 claps (bout identique de claque clap, ex : charnieres)
	private int lenghtClap=160; //LOngueur clap
	private int lenghtStair=200; // L'estradee fait 100, on met 200
	private int distanceInit;//distance intiale au script
	private int distanceRightLeft=1700; // distance entre les deux triplettes de claps : entre le 3 et le 5
	private int sleepTime = 800; // le temps d'attente (en ms) entre la commande de dépose du tapis ( le bras se baisse) et la commande qui remonte le bras

	
	public CloseClap (HookGenerator hookgenerator, Config config, Log log, Pathfinding pathfinding, LocomotionHiLevel locomotion, ActuatorsManager move) 
	{
		super(hookgenerator,config,log,pathfinding,locomotion,move);
	}
	
	public void executeAllClapFromStart (int id_version)  //Ferme tous les Claps, depuis le  debut
	{
		ArrayList<Hook> emptyHookList = new ArrayList<Hook>(); //Liste Hook vide
		try
		{
			try 
			{
				//Partie debut à partir du coin de la table (après une marche arrière)
				actionneurs.lowLeftClap();//Init
				actionneurs.lowRightClap();
				
				if (!OpenedLeftClap)//On ouvre le bras si ce n'est deja fait
				{
					actionneurs.highLeftClap();
					Sleep.sleep(sleepTime);
				}
				
				if(!ClosedClap1)//On ferme le clap le plus proche de nous,
				{	
					actionneurs.midLeftClap();//On ouvre puis on avance
					Sleep.sleep(sleepTime);
					ClosedClap1=true;
				}		
				
				locomotion.avancer(lenghtClap,emptyHookList,true);//On baisse le premier clap, le notre
				Sleep.sleep(sleepTime);
				
				if(ClosedClap2)//Si l'ennemi a toujours son clap
				{
					actionneurs.highLeftClap();
					Sleep.sleep(sleepTime);	
				}
				
				locomotion.avancer(2*distanceBetweenClaps-lenghtClap-20,emptyHookList,true);//On avance entre le 1 et le 3
				Sleep.sleep(sleepTime);	
					
				if(!ClosedClap3)//Clap 3, le plus loin sur notre zone
				{
					actionneurs.midLeftClap();
					Sleep.sleep(sleepTime);	
					ClosedClap3=true;//On ferme notre 2eme clap	
				}

				locomotion.tourner(Math.PI/2,emptyHookList,true);//On tourne en fermant le notre

				Sleep.sleep(sleepTime);	
				actionneurs.lowLeftClap();
				
				//Partie deplacements du clap 3 au clap 6, pret à fermer le 5
				// On se tourne, on avance pour eviter l'estrade
				
				
				locomotion.avancer(lenghtStair,emptyHookList,true); 
				Sleep.sleep(sleepTime);	

				//On se tourne, on avance vers le clap interessant
				locomotion.tourner(Math.PI,emptyHookList,true);
				Sleep.sleep(sleepTime);	
				locomotion.avancer(distanceRightLeft,emptyHookList,true); 
				Sleep.sleep(sleepTime);	
				
				//On se tourne pour s'avancer des claps
				locomotion.tourner(-Math.PI/2,emptyHookList,true);
				Sleep.sleep(sleepTime);	
				locomotion.avancer(lenghtStair,emptyHookList,true);
				Sleep.sleep(sleepTime);	
				
				//On se tourne dans le bon sens
				locomotion.tourner(0,emptyHookList,true);
				Sleep.sleep(sleepTime);	

				
				//Partie clap de fin
				// A ce stade, on est devant le 5
			
				
				if (!OpenedRightClap)
				{
					actionneurs.highRightClap();
					Sleep.sleep(sleepTime);
				}
				
				if(!ClosedClap5)//Clap 5
				{
					actionneurs.midRightClap();
					Sleep.sleep(sleepTime);	
					ClosedClap5=true;//On ferme notre clap	
				}
				
				locomotion.avancer(lenghtClap,emptyHookList,true);
				Sleep.sleep(sleepTime);	

				locomotion.tourner(Math.PI/2,emptyHookList,true);
				Sleep.sleep(sleepTime);	

				locomotion.avancer(lenghtStair,emptyHookList,true);
				Sleep.sleep(sleepTime);	
				
				actionneurs.lowRightClap();
				
			}
			catch (UnableToMoveException e) 
			{
			log.debug("Probleme avec le deplacement pendant tous les claps", this);
			} 
		}
		catch (SerialException e) 
		{
			log.debug("Mauvaise entrée serie !",this);
			e.printStackTrace();	
		}	
	}
	
	public void executeSltDebut (int id_version)  //Ferme les 2 claps du debut
	{
		ArrayList<Hook> emptyHookList = new ArrayList<Hook>(); //Liste Hook vide
		try
		{
			try 
			{
				//Partie debut à partir du coin de la table (après une marche arrière)
				
				if (!OpenedLeftClap)//On ouvre le bras si ce n'est deja fait
				{
					actionneurs.highLeftClap();
					Sleep.sleep(sleepTime);
				}
				
				if(!ClosedClap1)//On ferme le clap le plus proche de nous,
				{				
					actionneurs.midLeftClap();//On ouvre puis on avance
					Sleep.sleep(sleepTime);
					ClosedClap1=true;
				}		
				
				locomotion.avancer(lenghtClap,emptyHookList,true);//On baisse le premier clap, le notre
				Sleep.sleep(sleepTime);
				
				if(ClosedClap2)//Si l'ennemi a toujours son clap
				{
					actionneurs.highLeftClap();
					Sleep.sleep(sleepTime);	
				}
				
				locomotion.avancer(2*distanceBetweenClaps-lenghtClap-20,emptyHookList,true);//On avance entre le 1 et le 3
				Sleep.sleep(sleepTime);	
					
				if(!ClosedClap3)//Clap 3, le plus loin sur notre zone
				{
					actionneurs.midLeftClap();
					Sleep.sleep(sleepTime);	
					ClosedClap3=true;//On ferme notre 2eme clap	
				}

				locomotion.tourner(Math.PI/2,emptyHookList,true);//On tourne en fermant le notre

				Sleep.sleep(sleepTime);	
				actionneurs.lowLeftClap();
			}
			catch (UnableToMoveException e) 
			{
			log.debug("Probleme avec le deplacement pendant les claps près", this);
			} 
		}
		catch (SerialException e) 
		{
			log.debug("Mauvaise entrée serie !",this);
			e.printStackTrace();	
		}	
	}

	public void executeSltFin (int id_version)  //Ferme le claps de fin
	{
		ArrayList<Hook> emptyHookList = new ArrayList<Hook>(); //Liste Hook vide
		try
		{
			try 
			{
				// A ce stade, on est devant le 5, pret à avancer
			
				if (!OpenedRightClap)
				{
					actionneurs.highRightClap();
					Sleep.sleep(sleepTime);
				}
				
				if(!ClosedClap5)//Clap 5
				{
					actionneurs.midRightClap();
					Sleep.sleep(sleepTime);	
					ClosedClap5=true;//On ferme notre clap	
				}
				
				locomotion.avancer(lenghtClap,emptyHookList,true);
				Sleep.sleep(sleepTime);	

				locomotion.tourner(Math.PI/2,emptyHookList,true);
				Sleep.sleep(sleepTime);	

				locomotion.avancer(lenghtStair,emptyHookList,true);
				Sleep.sleep(sleepTime);	
				
				actionneurs.lowRightClap();
			
			}
			catch (UnableToMoveException e) 
			{
			log.debug("Probleme avec le deplacement pendant les claps loins", this);
			} 
		}
		catch (SerialException e) 
		{
			log.debug("Mauvaise entrée serie !",this);
			e.printStackTrace();	
		}	
	}
	
	
	@Override
	public Vec2 point_entree(int id) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public int score(int id_version, GameState<?> state) {
		int score = 15;
		if(ClosedClap1)
			score -= 5;
		if(ClosedClap3)
			score -= 5;
		if(ClosedClap5)
			score -= 5;
		return score;
	}


	@Override
	protected void termine(GameState<?> state) {
		
		try 
		{
			actionneurs.lowRightClap();
			actionneurs.lowLeftClap(); //On ferme le robot à la fin, attention à ne rien cogner (rembarde ,
		} 
		catch (SerialException e) 
		{
			log.debug("Erreur termine : ne peux pas replier claps", this);;
		}
	}

	public int getDistanceInit() {
		return distanceInit;
	}

	public void setDistanceInit(int distanceInit) {
		this.distanceInit = distanceInit;
	}

	public int getSleepTime() {
		return sleepTime;
	}

	public void setSleepTime(int sleepTime) {
		this.sleepTime = sleepTime;
	}
	
}
