package scripts;

import java.util.ArrayList;
import pathFinding.PathFinding;
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
 */

public class CloseClap extends Script 
{
	
	private boolean OpenedLeftClap=false,OpenedRightClap=false,ClosedClap1=false,ClosedClap2=false,ClosedClap3=false,ClosedClap4=false,ClosedClap5=false,ClosedClap6=false;//clap fermé au debut
	private int distanceBetweenClaps=300;//distance entre 2 claps (bout identique de claque clap, ex : charnieres)
	private int lenghtClap=160; //LOngueur clap
	private int lenghtStair=200; // L'estradee fait 100, on met 200
	private int distanceInit;//distance intiale au script
	private int distanceRightLeft=1600; // distance entre les deux triplettes de claps : entre le 3 et le 5
	private int sleepTime = 800; //TODO le temps d'attente (en ms) entre la commande de dépose du tapis ( le bras se baisse) et la commande qui remonte le bras

	
	public CloseClap (HookGenerator hookgenerator, Config config, Log log, PathFinding pathfinding, LocomotionHiLevel locomotion, ActuatorsManager move) 
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
				
				//Partie debut
				
				
				//On met le coté gauche du robot devant les claps 
				locomotion.tourner(-Math.PI/2,emptyHookList,true);//TODO verifier
				locomotion.avancer(distanceInit,emptyHookList,true);//TODO DISTANCE INITIALE
				
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
				
				if(ClosedClap2)//Si l'ennemi a toujours son clap
				{
					actionneurs.highLeftClap();
					Sleep.sleep(sleepTime);	
				}
				
					locomotion.avancer(distanceBetweenClaps,emptyHookList,true);//On avance entre le 1 et le 3
					locomotion.avancer(lenghtClap,emptyHookList,true);
					locomotion.avancer(distanceBetweenClaps,emptyHookList,true);
					
				if(!ClosedClap3)//Clap 3, le plus loin sur notre zone
				{
					actionneurs.midLeftClap();
					Sleep.sleep(sleepTime);	
					ClosedClap3=true;//On ferme notre 2eme clap	
				}

				locomotion.avancer(lenghtClap,emptyHookList,true); // On avance jusqu'au bout du 3
				
				actionneurs.lowLeftClap();
				
				//Partie deplacements du clap 3 au clap 6, pret à fermer le 5
				// On se tourne, on avance pour eviter l'estrade
				locomotion.tourner(Math.PI/2,emptyHookList,true);//TODO
				locomotion.avancer(lenghtStair,emptyHookList,true); 
				//On se tourne, on avance vers le clap interessant
				locomotion.tourner(Math.PI,emptyHookList,true);//TODO
				locomotion.avancer(distanceRightLeft,emptyHookList,true); 
				//On se tourne pour s'avancer des claps
				locomotion.tourner(-Math.PI/2,emptyHookList,true);//TODO
				locomotion.avancer(lenghtStair,emptyHookList,true);
				//On se tourne dans le bon sens
				locomotion.tourner(0,emptyHookList,true);//TODO
				

				
				//Partie clap de fin
				// A ce stade, on est devant le 5
			
				
				if (!OpenedRightClap)
				{
					actionneurs.highRightClap();
					Sleep.sleep(sleepTime);
				}
				
				if(ClosedClap6)//Si l'ennemi a toujours son clap //Pas sur que ca soit utile
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

				
				if(ClosedClap4)//Si l'ennemi a toujours son clap
				{
					actionneurs.highRightClap();
					Sleep.sleep(sleepTime);	
				}	
				
				locomotion.avancer(distanceBetweenClaps,emptyHookList,true);//On s'eloigne de 4
				locomotion.avancer(lenghtClap,emptyHookList,true);
				locomotion.avancer(distanceBetweenClaps,emptyHookList,true);

				
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
				//On met le coté gauche du robot devant les claps 
				locomotion.tourner(-Math.PI/2,emptyHookList,true);//TODO verifier
				locomotion.avancer(distanceInit,emptyHookList,true);//TODO DISTANCE INITIALE
				
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
				
				if(ClosedClap2)//Si l'ennemi a toujours son clap
				{
					actionneurs.highLeftClap();
					Sleep.sleep(sleepTime);	
				}
				
					locomotion.avancer(distanceBetweenClaps,emptyHookList,true);//On avance entre le 1 et le 3
					locomotion.avancer(lenghtClap,emptyHookList,true);
					locomotion.avancer(distanceBetweenClaps,emptyHookList,true);
					
				if(!ClosedClap3)//Clap 3, le plus loin sur notre zone
				{
					actionneurs.midLeftClap();
					Sleep.sleep(sleepTime);	
					ClosedClap3=true;//On ferme notre 2eme clap	
				}

				locomotion.avancer(lenghtClap,emptyHookList,true); // On avance jusqu'au bout du 3
				
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
				
				if(ClosedClap6)//Si l'ennemi a toujours son clap //Pas sur que ca soit utile
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

				
				if(ClosedClap4)//Si l'ennemi a toujours son clap
				{
					actionneurs.highRightClap();
					Sleep.sleep(sleepTime);	
				}	
				
				locomotion.avancer(distanceBetweenClaps,emptyHookList,true);//On s'eloigne de 4
				locomotion.avancer(lenghtClap,emptyHookList,true);
				locomotion.avancer(distanceBetweenClaps,emptyHookList,true);

				
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
