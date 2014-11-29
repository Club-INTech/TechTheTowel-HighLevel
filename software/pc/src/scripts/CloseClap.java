package scripts;

import java.util.ArrayList;

import Pathfinding.Pathfinding;
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
	private int distanceClaps=50;//TODO distance d'avance entre 2 claps
	private int distance; //TODO
	private int distanceA; //TODO
	private int sleepTime = 800; //TODO le temps d'attente (en ms) entre la commande de dépose du tapis ( le bras se baisse) et la commande qui remonte le bras

	
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
				
				//Partie debut
				
				
				//On met le coté gauche du robot devant les claps 
				locomotion.tourner(Math.PI/2,emptyHookList,true);//TODO verifier
				locomotion.avancer(distanceA,emptyHookList,true);//TODO DISTANCE INITIALE
				
				if (!OpenedLeftClap)
				{
					actionneurs.highLeftClap();
					Sleep.sleep(sleepTime);
				}
				if(!ClosedClap1)//On ferme le clap le plus proche de nous
				{
				
					locomotion.avancer(distance,emptyHookList,true);
					actionneurs.midLeftClap();
					Sleep.sleep(sleepTime);
					locomotion.avancer(distanceClaps,emptyHookList,true);//On baisse le premier clap, le notre
					ClosedClap1=true;
				}		
				if(ClosedClap2)//Si l'ennemi a toujours son clap
				{
					actionneurs.highLeftClap();
					Sleep.sleep(sleepTime);	
					locomotion.avancer(distanceClaps,emptyHookList,true);
					locomotion.avancer(distanceClaps,emptyHookList,true);//On evite le clap adverse
				}	
				if(!ClosedClap3)//Clap 3, le plus loin sur notre zone
				{
					actionneurs.midLeftClap();
					Sleep.sleep(sleepTime);	
					locomotion.avancer(distanceClaps,emptyHookList,true);
					ClosedClap3=true;//On ferme notre 2eme clap	
				}
				
				actionneurs.lowLeftClap();
				
				//Partie deplacements du clap 3 au clap 6, pret à fermer le 5
				
				locomotion.tourner(Math.PI/6,emptyHookList,true);//TODO
				locomotion.avancer(distanceA,emptyHookList,true);//TODO 
				locomotion.tourner(Math.PI,emptyHookList,true);//TODO
				locomotion.avancer(distanceA,emptyHookList,true);//TODO
				
				//Partie clap de fin
				// met le coté droit du robot devant les claps 
				locomotion.tourner(Math.PI*-1/2,emptyHookList,true);//TODO verifier
				locomotion.avancer(distanceA,emptyHookList,true);//TODO DISTANCE INITIALE
				
				if (!OpenedRightClap)
				{
					actionneurs.highRightClap();
					Sleep.sleep(sleepTime);
				}
				
				if(ClosedClap6)//Si l'ennemi a toujours son clap
				{
					actionneurs.highRightClap();
					Sleep.sleep(sleepTime);	
					locomotion.avancer(distanceClaps,emptyHookList,true);//On evite le clap adverse
				}	
				
				if(!ClosedClap5)//Clap 5
				{
					actionneurs.midRightClap();
					Sleep.sleep(sleepTime);	
					locomotion.avancer(distanceClaps,emptyHookList,true);
					ClosedClap5=true;//On ferme notre clap	
				}
				
				if(ClosedClap4)//Si l'ennemi a toujours son clap
				{
					actionneurs.highRightClap();
					Sleep.sleep(sleepTime);	
					locomotion.avancer(distanceClaps,emptyHookList,true);//On evite le clap adverse
				}	
				
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
				locomotion.tourner(Math.PI/2,emptyHookList,true);//TODO verifier
				locomotion.avancer(distanceA,emptyHookList,true);//TODO DISTANCE INITIALE
				
				if (!OpenedLeftClap)
				{
					actionneurs.highLeftClap();
					Sleep.sleep(sleepTime);
				}
				if(!ClosedClap1)//On ferme le clap le plus proche de nous
				{
				
					locomotion.avancer(distance,emptyHookList,true);
					actionneurs.midLeftClap();
					Sleep.sleep(sleepTime);
					locomotion.avancer(distanceClaps,emptyHookList,true);//On baisse le premier clap, le notre
					ClosedClap1=true;
				}		
				if(ClosedClap2)//Si l'ennemi a toujours son clap
				{
					actionneurs.highLeftClap();
					Sleep.sleep(sleepTime);	
					locomotion.avancer(distanceClaps,emptyHookList,true);
					locomotion.avancer(distanceClaps,emptyHookList,true);//On evite le clap adverse
				}	
				if(!ClosedClap3)//Clap 3, le plus loin sur notre zone
				{
					actionneurs.midLeftClap();
					Sleep.sleep(sleepTime);	
					locomotion.avancer(distanceClaps,emptyHookList,true);
					ClosedClap3=true;//On ferme notre 2eme clap	
				}
				
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
				// met le coté droit du robot devant les claps 
				locomotion.tourner(Math.PI*-1/2,emptyHookList,true);//TODO verifier
				locomotion.avancer(distanceA,emptyHookList,true);//TODO DISTANCE INITIALE
				
				if (!OpenedRightClap)
				{
					actionneurs.highRightClap();
					Sleep.sleep(sleepTime);
				}
				
				if(ClosedClap6)//Si l'ennemi a toujours son clap
				{
					actionneurs.highRightClap();
					Sleep.sleep(sleepTime);	
					locomotion.avancer(distanceClaps,emptyHookList,true);//On evite le clap adverse
				}	
				
				if(!ClosedClap5)//Clap 5
				{
					actionneurs.midRightClap();
					Sleep.sleep(sleepTime);	
					locomotion.avancer(distanceClaps,emptyHookList,true);
					ClosedClap5=true;//On ferme notre clap	
				}
				
				if(ClosedClap4)//Si l'ennemi a toujours son clap
				{
					actionneurs.highRightClap();
					Sleep.sleep(sleepTime);	
					locomotion.avancer(distanceClaps,emptyHookList,true);//On evite le clap adverse
				}	
				
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
	
	public int getSleepTime()
	{
		return sleepTime;
	}
	public void setSleepTime(int newSleepTime)
	{
		this.sleepTime = newSleepTime;
	}
	public int getDistance()
	{
		return distance;
	}
	public void setDistance(int newDistance)
	{
		this.distance = newDistance;
	}
	
}
