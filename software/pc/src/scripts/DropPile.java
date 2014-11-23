package scripts;

import java.util.ArrayList;

import Pathfinding.Pathfinding;
import exceptions.ScriptException;
import hook.Hook;
import hook.types.HookGenerator;
import robot.RobotReal;
import robot.cards.ActuatorsManager;
import robot.highlevel.LocomotionHiLevel;
import smartMath.Vec2;
import strategie.GameState;
import utils.Config;
import utils.Log;

public class DropPile extends Script {
	
	private ArrayList<Hook> hook = new ArrayList<Hook>();

	public DropPile(HookGenerator hookgenerator, Config config, Log log, Pathfinding pathfinding, LocomotionHiLevel locomotion, ActuatorsManager move) 
	{
		super(hookgenerator, config, log,pathfinding, locomotion,move);
		ArrayList<Integer> id = new ArrayList<Integer>();
		id.add(1);
		id.add(2);
	}

	@Override
	protected void execute(int id_version)
	{
		if (id_version==1)
		{
			locomotion.tourner((Math.PI*0.5), hook, false);
			locomotion.avancer(100, hook, true);
			move.elevatorGround();
			move.ouvrirLentGuide();
			locomotion.avancer(-20, hook, true);
			this.setPlotCounter(0);
			move.guideGaucheClose();
			move.guideDroitClose();
			locomotion.avancer(-80,hook,true);
		}
		else if (id_version==2)
		{
			
		}
		else
		{
			
		}
	}
	
	@Override
	public void goToThenExec(int id_version, GameState<RobotReal> state, boolean retenter_si_blocage) throws ScriptException
	{
		
	}
	
	@Override
	public Vec2 point_entree(int id) 
	{
		if (id==1)
		{
			return new Vec2(1300,1000);
		}
		else if (id==2)
		{
			return new Vec2(70,40);
		}
		else
		{
			log.debug("erreur DropPile script : out of bound id", this);
			return new Vec2(0,1000);
		}
	}

	@Override
	public int score(int id_version, GameState<?> state)
	{
		return 5*this.getPlotCounter();
	}

	@Override
	protected void termine(GameState<?> state) 
	{
		fermerMachoire();
		locomotion.avancer(-20, hook, true);
		baisserAscenseur();
		fermerGuide();
	}

}
