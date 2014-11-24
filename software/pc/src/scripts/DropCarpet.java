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
 * @author paul
 * Script pour deposer les tapis sur l'escalier
 */
public class DropCarpet extends Script 
{
	// TODO ? bouger ces booléens dans table.
	private boolean DroppedLeftCarpet=false, DroppedRightCarpet=false;//booleens pour savoir si le tapis gauche (respectivement droit) a ete depose
	private int distance=200;//distance de déplacement pour placer les tapis

	public DropCarpet (HookGenerator hookgenerator, Config config, Log log, Pathfinding pathfinding, LocomotionHiLevel locomotion, ActuatorsManager move) 
	{
		super(hookgenerator,config,log,pathfinding,locomotion,move);
		//cree la liste des versions donc des id
	}
	
	@Override
	public void execute (int id_version) 
	{
		// le temps d'attente (en ms) entre la commande de dépose du tapis ( le bras se baisse) et la commande qui remonte le bras
		int timeToDropCarpet = 800;
		
		
		ArrayList<Hook> emptyHookList = new ArrayList<Hook>(); //liste des hook vide pour le moment mais a modifier
		//premier test de script
		try 
		{
			try 
			{
				//on presente ses arrieres a l'escalier
				locomotion.tourner(Math.PI,emptyHookList,true);
				// on avance vers ces demoiselles (les marches) 
				locomotion.avancer(-distance,emptyHookList,true); //on se rapproche de l'escalier
				
				if (!DroppedLeftCarpet)
				{
					mActuatorMgr.baisserTapisGauche();
					Sleep.sleep(timeToDropCarpet);
					DroppedLeftCarpet = true;
					mActuatorMgr.monterTapisGauche();
				}
				if (!DroppedRightCarpet)
				{
					mActuatorMgr.baisserTapisDroit();
					Sleep.sleep(timeToDropCarpet);
					DroppedRightCarpet = true;
					mActuatorMgr.monterTapisDroit();
				}
				locomotion.avancer(distance,emptyHookList,true);//on s'eloigne de l'escalier
			} 
			catch (UnableToMoveException e) 
			{
			log.debug("erreur DropCarpet Script : impossible de bouger", this);
			} 
		}
		catch (SerialException e) 
		{
			log.debug("ca marche pas !",this);
			
		}
	}
	
	@Override
	public Vec2 point_entree(int id) 
	{
		// le point d'entrée (261,1210) pour les verts, on change comment de couleur si on est jaune ?
		return new Vec2(261,1210);
	}

	@Override
	public int score(int id_version, GameState<?> state) 
	{
		int score = 24;
		if(DroppedLeftCarpet)
			score -= 12;
		if(DroppedRightCarpet)
			score -= 12;
		
		return score;
	}

	@Override
	protected void termine(GameState<?> state) 
	{
		try 
		{
			mActuatorMgr.monterTapisGauche();
			mActuatorMgr.monterTapisDroit();
		} 
		catch (SerialException e) 
		{
			log.debug("erreur termine DropCarpet script : impossible de ranger", this);;
		}
	}

}
