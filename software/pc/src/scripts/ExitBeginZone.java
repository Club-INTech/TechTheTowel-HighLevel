package scripts;

import hook.Hook;
import hook.types.HookGenerator;

import java.util.ArrayList;

import pathdinding.Pathfinding;
import exceptions.Locomotion.UnableToMoveException;
import robot.cards.ActuatorsManager;
import robot.highlevel.LocomotionHiLevel;
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

	int distanceToExit=450;
	ArrayList<Hook> emptyHook = new ArrayList<Hook>();
	
	public ExitBeginZone(HookGenerator hookgenerator, Config config, Log log, Pathfinding pathfinding, LocomotionHiLevel locomotion, ActuatorsManager move) 
	{
		super(hookgenerator, config, log, pathfinding, locomotion, move);
	}

	@Override
	public Vec2 point_entree(int id) {
		return new Vec2(1340,1000);//point de depart du match a modifier a chaque base roulante
	}
	
	@Override
	public void execute (int id_version)
	{
		try {
			locomotion.avancer(distanceToExit,emptyHook,true);
		} catch (UnableToMoveException e) {
			log.debug("erreur ExitBeginZone script : impossible de sortir de la zone de depart\n", this);
		}
	}

	@Override
	public int score(int id_version, GameState<?> state) 
	{		
		return 0;
	}

	@Override
	protected void termine(GameState<?> state) 
	{
		//abwa ?
	}
}
