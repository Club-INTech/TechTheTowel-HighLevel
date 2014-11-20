package scripts;

import java.util.ArrayList;

import hook.Hook;
import hook.types.HookGenerator;
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

	public DropCarpet (HookGenerator hookgenerator, Config config, Log log) 
	{
		super(hookgenerator,config,log);
		//cree la liste des versions donc des id
	}
	
	@Override
	public void execute () 
	{
		ArrayList<Hook> hook = new ArrayList<Hook>(); //liste des hook vide pour le moment mais a modifier
		//premier test de script
		locomotion.tourner(Math.PI,hook,true); //on presente ses arrieres a l'escalier
		locomotion.avancer(-distance,hook,true); //on se rapproche de l'escalier
		if (!DroppedLeftCarpet)
		{
			baisserTapisGauche();
			DroppedLeftCarpet=true;
			undroppedCarpetCount--;
			monterTapisGauche();
		}
		if (!DroppedRightCarpet)
		{
			baisserTapisDroit();
			DroppedRightCarpet=true;
			undroppedCarpetCount--;
			monterTapisDroit();
		}
		locomotion.avancer(distance,hook,true);//on s'eloigne de l'escalier
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
		monterTapisGauche();
		monterTapisDroit();
	}

}
