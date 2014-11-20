package scripts;

import hook.Hook;
import hook.types.HookGenerator;

import java.util.ArrayList;

import exceptions.Locomotion.UnableToMoveException;
import smartMath.Vec2;
import strategie.GameState;
import utils.Config;
import utils.Log;

/**
 * 
 * @author paul
 *script pour sortir de la zone de depart. a executer imperativement et uniquement au depart
 */
public class ExitBeginZone extends Script {

	public ExitBeginZone(HookGenerator hookgenerator, Config config, Log log) 
	{
		super(hookgenerator, config, log);
	}

	@Override
	public Vec2 point_entree(int id) {
		return new Vec2(1350,1000);//point de depart du match a modifier a chaque base roulante
	}
	
	@Override
	public void execute ()
	{
		int distanceToExit=100;
		ArrayList<Hook> hook = new ArrayList<Hook>();
		try {
			locomotion.avancer(distanceToExit,hook,true);
		} catch (UnableToMoveException e) {
			log.debug("erreur ExitBeginZone script : impossible de sortir de la zone de depart\n", this);
		}
	}

	@Override
	public int score(int id_version, GameState<?> state) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected void termine(GameState<?> state) {
		// TODO Auto-generated method stub
		
	}
}
