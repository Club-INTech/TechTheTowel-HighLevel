package scripts;

import hook.types.HookGenerator;
import smartMath.Vec2;
import strategie.GameState;
import utils.Config;
import utils.Log;

public class DropCarpet extends Script 
{
	private boolean DroppedLeftCarpet=false, DroppedRightCarpet=false;
	int numberOfCarpetNotDropped=2;
	int distance=0;//distance de déplacement pour placer les tapis

	public DropCarpet (HookGenerator hookgenerator, Config config, Log log) 
	{
		super(hookgenerator,config,log);
		//cree la liste des versions donc des id
	}
	
	public void execute () 
	{
		//premier test de script
		tourner(Math.PI); //on present ses arriere a l'escalier
		avancer(-distance); //on se raproche de l'escalier
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
		avancer(distance);//on s'eloigne de l'escalier
	}
	
	public void goToThenExec () 
	{
		int id;
		goTo(point_entree(id));
		execute();
	}
	@Override
	public Vec2 point_entree(int id) 
	{
		// le point d'entrée (261,1210) pour les verts on change comment de couleur ?
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
