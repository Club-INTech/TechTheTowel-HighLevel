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
/**
 * 
 * @author paul
 * Script pour deposer les tapis sur l'escalier
 */
public class DropCarpet extends Script 
{
	private boolean DroppedLeftCarpet=false, DroppedRightCarpet=false;//booleens pour savoir si le tapis gauche (respectivement droit) a ete depose
	private int undroppedCarpetCount=2;//nombre de tapis pas depose
	private int distance=0;//distance de déplacement pour placer les tapis

	public DropCarpet (HookGenerator hookgenerator, Config config, Log log, Pathfinding pathfinding, LocomotionHiLevel locomotion, ActuatorsManager move) 
	{
		super(hookgenerator,config,log,pathfinding,locomotion,move);
		//cree la liste des versions donc des id
	}
	
	@Override
	public void execute (int id_version) 
	{
		ArrayList<Hook> hook = new ArrayList<Hook>(); //liste des hook vide pour le moment mais a modifier
		//premier test de script
		try 
		{
			try 
			{
				locomotion.tourner(Math.PI,hook,true);
				//on presente ses arrieres a l'escalier
				locomotion.avancer(-distance,hook,true); //on se rapproche de l'escalier
				if (!DroppedLeftCarpet)
				{
					move.baisserTapisGauche();
					DroppedLeftCarpet=true;
					undroppedCarpetCount--;
					move.monterTapisGauche();
				}
				if (!DroppedRightCarpet)
				{
					move.baisserTapisDroit();
					DroppedRightCarpet=true;
					undroppedCarpetCount--;
					move.monterTapisDroit();
				}
				locomotion.avancer(distance,hook,true);//on s'eloigne de l'escalier
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
		return 12*(undroppedCarpetCount);
	}

	@Override
	protected void termine(GameState<?> state) 
	{
		try 
		{
			move.monterTapisGauche();
			move.monterTapisDroit();
		} 
		catch (SerialException e) 
		{
			log.debug("erreur termine DropCarpet script : impossible de ranger", this);;
		}
	}

}
