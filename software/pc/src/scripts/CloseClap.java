package scripts;

import java.util.ArrayList;
import pathdinding.Pathfinding;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialException;
import hook.Hook;
import hook.types.HookGenerator;
import robot.Robot;
import robot.cards.ActuatorsManager;
import smartMath.Vec2;
import strategie.GameState;
import table.Table;
import utils.Config;
import utils.Log;

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
	private int distanceBetweenClaps=300;//distance entre 2 claps (bout identique de claque clap, ex : charnieres)
	private int lenghtClap=160; //LOngueur clap
	private int lenghtStair=200; // L'estradee fait 100, on met 200
	private int distanceInit;//distance intiale au script
	private int distanceRightLeft=1700; // distance entre les deux triplettes de claps : entre le 3 et le 5
	private int sleepTime = 800; // le temps d'attente (en ms) entre la commande de dépose du tapis ( le bras se baisse) et la commande qui remonte le bras

	
	public CloseClap (HookGenerator hookgenerator, Config config, Log log, Pathfinding pathfinding, Robot robot, ActuatorsManager move, Table table) 
	{
		super(hookgenerator,config,log,pathfinding,robot,move,table);
	}
	
	@Override
	public void execute(int id)
	{
		if (id == 123)
			_123();
		else if (id == 1)
			_1();
		else if (id == 2)
			_2();
		else if (id == 3)
			_3();
		else if (id == 12)
			_12();
		else
			log.debug("Souci de version", this);
	}
	
	public void _1 ()  
	{
		ArrayList<Hook> emptyHookList = new ArrayList<Hook>(); //Liste Hook vide
		try
		{
			try 
			{
				actionneurs.midLeftClap();//On ouvre puis on avance
				robot.sleep(sleepTime);
				
				robot.avancer(lenghtClap,emptyHookList,true);//On baisse le premier clap, le notre
				table.setIsClap1Closed(true);	
		
				actionneurs.lowLeftClap();
				robot.sleep(sleepTime);	
			}
			catch (UnableToMoveException e) 
			{
			log.debug("Probleme avec le deplacement pendant le clap 1", this);
			} 
		}
		catch (SerialException e) 
		{
			log.debug("Mauvaise entrée serie !",this);
			e.printStackTrace();	
		}	
	}

	public void _2 ()  
	{
		ArrayList<Hook> emptyHookList = new ArrayList<Hook>(); //Liste Hook vide
		try
		{
			try 
			{
				actionneurs.midLeftClap();//On ouvre puis on avance
				robot.sleep(sleepTime);
				
				robot.avancer(lenghtClap,emptyHookList,true);//On baisse le premier clap, le notre
				table.setIsClap2Closed(true);	
				
				actionneurs.lowLeftClap();
				robot.sleep(sleepTime);	
			}
			catch (UnableToMoveException e) 
			{
			log.debug("Probleme avec le deplacement pendant le clap 2", this);
			} 
		}
		catch (SerialException e) 
		{
			log.debug("Mauvaise entrée serie !",this);
			e.printStackTrace();	
		}	
	}
	
	public void _3 ()  //Ferme le claps de fin
	{
		ArrayList<Hook> emptyHookList = new ArrayList<Hook>(); //Liste Hook vide
		try
		{
			try 
			{
				actionneurs.midRightClap();
				robot.sleep(sleepTime);	
				
				robot.avancer(lenghtClap,emptyHookList,true);
				table.setIsClap3Closed(true);//On ferme notre clap	

				//Partie fuite 
				
				robot.tourner(Math.PI/2,emptyHookList,true);
				robot.avancer(lenghtStair,emptyHookList,true);
				
				actionneurs.lowRightClap();
				robot.sleep(sleepTime);	
			}
			catch (UnableToMoveException e) 
			{
			log.debug("Probleme avec le deplacement pendant le clap 3", this);
			} 
		}
		catch (SerialException e) 
		{
			log.debug("Mauvaise entrée serie !",this);
			e.printStackTrace();	
		}	
	}
	
	public void _12 ()  //Ferme les 2 claps du debut
	{
		ArrayList<Hook> emptyHookList = new ArrayList<Hook>(); //Liste Hook vide
		try
		{
			try 
			{
				actionneurs.midLeftClap();//On ouvre puis on avance
				robot.sleep(sleepTime);
				
				robot.avancer(lenghtClap,emptyHookList,true);//On baisse le premier clap, le notre
				table.setIsClap1Closed(true);	
				
				actionneurs.highLeftClap(); //on evite le clap adverse
				
				robot.sleep(sleepTime);	
				robot.avancer(2*distanceBetweenClaps-lenghtClap-20,emptyHookList,true);//On avance entre le 1 et le 2
				
				actionneurs.midLeftClap();
				robot.sleep(sleepTime);	
				
				robot.tourner(Math.PI/2,emptyHookList,true);//On tourne en fermant le clap
				table.setIsClap2Closed(true);//On ferme notre 2eme clap	
				
				actionneurs.lowLeftClap(); //on referme le clap pour eviter de le perdre
				robot.sleep(sleepTime);	
			}
			catch (UnableToMoveException e) 
			{
			log.debug("Probleme avec le deplacement pendant les claps près, 1 et 2", this);
			} 
		}
		catch (SerialException e) 
		{
			log.debug("Mauvaise entrée serie !",this);
			e.printStackTrace();	
		}	
	}
	
	public void _123()  //Ferme tous les Claps, depuis le  debut
	{
		ArrayList<Hook> emptyHookList = new ArrayList<Hook>(); //Liste Hook vide
		try
		{
			try 
			{
				actionneurs.midLeftClap();//On ouvre puis on avance
				robot.sleep(sleepTime);
				
				robot.avancer(lenghtClap,emptyHookList,true);//On baisse le premier clap, le notre
				table.setIsClap1Closed(true);	
				
				actionneurs.highLeftClap(); //on evite le clap adverse
				
				robot.sleep(sleepTime);	
				robot.avancer(2*distanceBetweenClaps-lenghtClap-20,emptyHookList,true);//On avance entre le 1 et le 2
				
				actionneurs.midLeftClap();
				robot.sleep(sleepTime);	
				
				robot.tourner(Math.PI/2,emptyHookList,true);//On tourne en fermant le clap
				table.setIsClap2Closed(true);//On ferme notre 2eme clap	
				
				actionneurs.lowLeftClap(); //on referme le clap pour eviter de le perdre
				robot.sleep(sleepTime);	

				
				// On se tourne, on avance pour eviter l'estrade
				robot.avancer(lenghtStair,emptyHookList,true); 

				//On se tourne, on avance vers le clap interessant
				robot.tourner(Math.PI,emptyHookList,true);
				robot.avancer(distanceRightLeft,emptyHookList,true); 
				
				//On se tourne pour s'avancer des claps
				robot.tourner(-Math.PI/2,emptyHookList,true);
				robot.avancer(lenghtStair,emptyHookList,true);
				
				//On se tourne dans le bon sens
				robot.tourner(0,emptyHookList,true);
				
				//Partie clap de fin
				// A ce stade, on est devant le 3
				
				actionneurs.midRightClap();
				robot.sleep(sleepTime);	
				
				robot.avancer(lenghtClap,emptyHookList,true);
				table.setIsClap3Closed(true);//On ferme notre clap	

				//Partie fuite 
				
				robot.tourner(Math.PI/2,emptyHookList,true);
				robot.avancer(lenghtStair,emptyHookList,true);
				
				actionneurs.lowRightClap();
				robot.sleep(sleepTime);					
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
	
	@Override
	public Vec2 point_entree(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int score(int id_version, GameState<?> state) {
		int score = 15;
		if(table.getIsClap1Closed())
			score -= 5;
		if(table.getIsClap2Closed())
			score -= 5;
		if(table.getIsClap3Closed())
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
