package scripts;

import hook.types.HookGenerator;
import robot.highlevel.LocomotionHiLevel;
import smartMath.Vec2;
import strategie.GameState;
import utils.Config;
import utils.Log;

public class DropCarpet extends Script 
{
	private boolean DroppedLeftCarpet=false, DroppedRightCarpet=false;
	private int numberOfCarpetNotDropped=2;
	private int distance=0;//distance de déplacement pour placer les tapis

	public DropCarpet (HookGenerator hookgenerator, Config config, Log log) 
	{
		super(hookgenerator,config,log);
		//cree la liste des versions donc des id
	}
	
	public void execute () 
	{
		//premier test de script
		locomotion.tourner(Math.PI); //on presente ses arrieres a l'escalier
		locomotion.avancer(-distance); //on se rapproche de l'eescalier
		if (!DroppedLeftCarpet)
		{
			baisserTapisGauche();
			DroppedLeftCarpet=true;
			numberOfCarpetNotDropped--;
			monterTapisGauche();
		}
		if (!DroppedRightCarpet)
		{
			baisserTapisDroit();
			DroppedRightCarpet=true;
			numberOfCarpetNotDropped--;
			monterTapisDroit();
		}
		locomotion.avancer(distance);//on s'eloigne de l'escalier
	}
	
	@Override
	public Vec2 point_entree(int id) 
	{
		// le point d'entrée (261,1210)(notation commune (0,0) au niveau du plateau rouge) pour les verts, on change comment de couleur si on est jaune ?
		return new Vec2(261,1210);
	}

	@Override
	public int score(int id_version, GameState<?> state) 
	{
		return 12*(numberOfCarpetNotDropped);
	}

	@Override
	protected void termine(GameState<?> state) 
	{
		monterTapisGauche();
		monterTapisDroit();
	}

}
