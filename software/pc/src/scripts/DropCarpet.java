package scripts;

import hook.types.HookGenerator;
import smartMath.Vec2;
import strategie.GameState;
import utils.Config;
import utils.Log;

public class DropCarpet extends Script 
{

	public DropCarpet (HookGenerator hookgenerator, Config config, Log log) 
	{
		super(hookgenerator,config,log);
		//cree la liste des versions
	}
	
	public void execute () 
	{
		//premier test de script
		tourner(Math.PI);
		avancer(-var);
		baisserTapisGauche();
		monterTapisGauche();
		baisserTapisDroit();
		monterTapisDroit();
		avancer(var);
	}
	
	public void goToThenExec () 
	{
		
	}
	@Override
	public Vec2 point_entree(int id) 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int score(int id_version, GameState<?> state) 
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected void termine(GameState<?> state) 
	{
		// TODO Auto-generated method stub

	}

}
