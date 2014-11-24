package tests;

<<<<<<< HEAD
import java.util.ArrayList;
import java.util.Random;

import hook.Hook;
=======
>>>>>>> 8c3d78f82c75d2971e2f7dd1f4a887351f6ec51d
import hook.types.HookGenerator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import Pathfinding.Pathfinding;
import robot.cards.ActuatorsManager;
import robot.highlevel.LocomotionHiLevel;
import scripts.DropCarpet;
/**
 * test pour le script du depose tapis 
 * on suppose que le robot est place en (261,1410)
 * @author paul
 *
 */
public class JUnit_CarpetDropper extends JUnit_Test
{
	
	ActuatorsManager actionneursMgr;
	DropCarpet dropCarpetScript;
	LocomotionHiLevel locomotion;
	Pathfinding pathfinding = new Pathfinding();
	HookGenerator hookgenerator;
<<<<<<< HEAD

=======
	
>>>>>>> 8c3d78f82c75d2971e2f7dd1f4a887351f6ec51d
	@Before
	public void setUp() throws Exception 
	{
		//creation des objets pour le test
		super.setUp();                                                                                                                                 
		actionneursMgr = (ActuatorsManager)container.getService("Actionneurs");
		hookgenerator = (HookGenerator)container.getService("HookGenerator");
		locomotion = (LocomotionHiLevel)container.getService("DeplacementsHautNiveau");
<<<<<<< HEAD
		script = new DropCarpet(hookgenerator, config, log, pathfinding, locomotion, actionneurs);
		ArrayList<Hook>emptyHook = new ArrayList<Hook>();
		
		//positionnement du robot
		actionneurs.monterTapisDroit();
		actionneurs.monterTapisGauche();
		locomotion.avancer(script.getDistance(), emptyHook, false);
		Random rand = new Random();
		locomotion.tourner(rand.nextDouble(), emptyHook, false);

=======
		dropCarpetScript = new DropCarpet(hookgenerator, config, log, pathfinding, locomotion, actionneursMgr);
		
		// TODO lever les bras avant les tests
>>>>>>> 8c3d78f82c75d2971e2f7dd1f4a887351f6ec51d
	}

	@After
	public void tearDown() throws Exception 
	{
<<<<<<< HEAD
		actionneurs.monterTapisDroit();
		actionneurs.monterTapisGauche();
		//le robot reste sur place donc il est bien positionné pour le prochain test
=======
		
		// TODO lever les bras
>>>>>>> 8c3d78f82c75d2971e2f7dd1f4a887351f6ec51d
	}

	@Test
	public void test() 
	{
		log.debug("debut du depose tapis", this);
		dropCarpetScript.execute(0);

		log.debug("fin du depose tapis", this);
	}

}
