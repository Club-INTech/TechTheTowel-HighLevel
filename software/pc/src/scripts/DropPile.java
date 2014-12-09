package scripts;

import java.util.ArrayList;

import pathdinding.Pathfinding;
import exceptions.ScriptException;
import hook.Hook;
import hook.types.HookGenerator;
import robot.Robot;
import robot.RobotReal;
import robot.cards.ActuatorsManager;
import robot.highlevel.LocomotionHiLevel;
import smartMath.Vec2;
import strategie.GameState;
import table.Table;
import utils.Config;
import utils.Log;

public class DropPile extends Script {
	
	private ArrayList<Hook> emptyHook = new ArrayList<Hook>();

	public DropPile(HookGenerator hookgenerator, Config config, Log log, Pathfinding pathfinding, Robot robot, ActuatorsManager move, Table table) 
	{
		super(hookgenerator, config, log,pathfinding, robot,move,table);
		ArrayList<Integer> id = new ArrayList<Integer>();
		id.add(1);
		id.add(2);
	}

	@Override
	protected void execute(int id_version)
	{
		if (id_version==1)
		{
			robot.tourner((Math.PI*0.5), emptyHook, false);
			robot.avancer(100, emptyHook, true);
			actionneurs.elevatorGround();
			actionneurs.ouvrirLentGuide();
			robot.avancer(-20, emptyHook, true);
			robot.setPlotCounter(0);
			actionneurs.guideGaucheClose();
			actionneurs.guideDroitClose();
			robot.avancer(-80,emptyHook,true);
		}
		else if (id_version==2)
		{
			
		}
		else
		{
			
		}
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
		return 5*robot.getPlotCounter();
	}

	@Override
	protected void termine(GameState<?> state) 
	{
		fermerMachoire();
		robot.avancer(-20, emptyHook, true);
		baisserAscenseur();
		fermerGuide();
	}

}

