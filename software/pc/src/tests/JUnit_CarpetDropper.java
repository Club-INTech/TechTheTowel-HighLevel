package tests;

import hook.types.HookGenerator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import Pathfinding.Pathfinding;
import robot.cards.ActuatorsManager;
import robot.highlevel.LocomotionHiLevel;
import scripts.DropCarpet;

public class JUnit_CarpetDropper extends JUnit_Test
{
	
	ActuatorsManager actionneursMgr;
	DropCarpet dropCarpetScript;
	LocomotionHiLevel locomotion;
	Pathfinding pathfinding = new Pathfinding();
	HookGenerator hookgenerator;
	
	@Before
	public void setUp() throws Exception 
	{
		super.setUp();                                                                                                                                 
		actionneursMgr = (ActuatorsManager)container.getService("Actionneurs");
		hookgenerator = (HookGenerator)container.getService("HookGenerator");
		locomotion = (LocomotionHiLevel)container.getService("DeplacementsHautNiveau");
		dropCarpetScript = new DropCarpet(hookgenerator, config, log, pathfinding, locomotion, actionneursMgr);
		
		// TODO lever les bras avant les tests
	}

	@After
	public void tearDown() throws Exception 
	{
		
		// TODO lever les bras
	}

	@Test
	public void test() 
	{
		log.debug("debut du depose tapis", this);
		dropCarpetScript.execute(0);

		log.debug("fin du depose tapis", this);
	}

}
