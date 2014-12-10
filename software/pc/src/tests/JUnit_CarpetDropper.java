package tests;


import java.util.ArrayList;
import java.util.Random;

import hook.Hook;
import hook.types.HookGenerator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pathdinding.Pathfinding;
import robot.RobotReal;
import robot.cards.ActuatorsManager;
import robot.highlevel.LocomotionHiLevel;
import scripts.DropCarpet;
import table.Table;
/**
 * test pour le script du depose tapis 
 * on suppose que le robot est place en (261,1410)
 * @author paul
 *
 */
public class JUnit_CarpetDropper extends JUnit_Test
{
	
	ActuatorsManager actionneurs;
	DropCarpet scriptCarpet;
	LocomotionHiLevel locomotion;
<<<<<<< HEAD
	PathDingDing pathfinding = new PathDingDing();
=======
	Table table;
	Pathfinding pathfinding;
>>>>>>> branch 'master' of gitosis@git.club-intech.fr:intech-2015.git
	HookGenerator hookgenerator;
	RobotReal robot;

	@Before
	public void setUp() throws Exception 
	{
		//creation des objets pour le test
		super.setUp();                                                                                                                                 
		actionneurs = (ActuatorsManager)container.getService("Actionneurs");
		hookgenerator = (HookGenerator)container.getService("HookGenerator");
		locomotion = (LocomotionHiLevel)container.getService("DeplacementsHautNiveau");
		table = (Table)container.getService("Table");
		robot = (RobotReal)container.getService("RobotVrai");
		pathfinding = new Pathfinding(table);
		scriptCarpet = new DropCarpet(hookgenerator, config, log, pathfinding, robot , actionneurs, table);
		ArrayList<Hook>emptyHook = new ArrayList<Hook>();
		
		//positionnement du robot
		actionneurs.highRightCarpet();
		actionneurs.highLeftCarpet();
		robot.avancer(scriptCarpet.getDistance(), emptyHook, false);
		Random rand = new Random();
		locomotion.tourner(rand.nextDouble(), emptyHook, false);
	}

	@After
	public void tearDown() throws Exception 
	{
		actionneurs.highRightCarpet();
		actionneurs.highLeftCarpet();
		//le robot reste sur place donc il est bien positionné pour le prochain test
	}

	@Test
	public void test() 
	{
		log.debug("debut du depose tapis", this);
		scriptCarpet.execute(0);

		log.debug("fin du depose tapis", this);
	}

}
