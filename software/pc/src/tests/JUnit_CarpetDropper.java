package tests;

import static org.junit.Assert.*;
import hook.types.HookGenerator;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import Pathfinding.Pathfinding;
import robot.cards.ActuatorsManager;
import robot.highlevel.LocomotionHiLevel;
import scripts.DropCarpet;

public class JUnit_CarpetDropper extends JUnit_Test
{
	
	ActuatorsManager actionneurs;
	DropCarpet script;
	LocomotionHiLevel locomotion;
	Pathfinding pathfinding = new Pathfinding();
	HookGenerator hookgenerator;
	

	@BeforeClass
	public static void setUpBeforeClass() throws Exception 
	{
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception 
	{
	}

	@Before
	public void setUp() throws Exception 
	{
		super.setUp();
		actionneurs = (ActuatorsManager)container.getService("Actionneurs");
		hookgenerator = (HookGenerator)container.getService("HookGenerator");
		locomotion = (LocomotionHiLevel)container.getService("DeplacementsHautNiveau");
		script = new DropCarpet(hookgenerator, config, log, pathfinding, locomotion, actionneurs);
		
	}

	@After
	public void tearDown() throws Exception 
	{
	}

	@Test
	public void test() 
	{
		script.execute(0);
	}

}
