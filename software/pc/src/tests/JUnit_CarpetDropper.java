package tests;


import java.util.ArrayList;
import java.util.Random;

import hook.Hook;
import hook.types.HookGenerator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pathfinding.Pathfinding;
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
	
	ActuatorsManager actionneurs;
	DropCarpet scriptCarpet;
	LocomotionHiLevel locomotion;
	PathDingDing pathfinding = new PathDingDing();
	HookGenerator hookgenerator;

	@Before
	public void setUp() throws Exception 
	{
		//creation des objets pour le test
		super.setUp();                                                                                                                                 
		actionneurs = (ActuatorsManager)container.getService("Actionneurs");
		hookgenerator = (HookGenerator)container.getService("HookGenerator");
		locomotion = (LocomotionHiLevel)container.getService("DeplacementsHautNiveau");
		scriptCarpet = new DropCarpet(hookgenerator, config, log, pathfinding, locomotion, actionneurs);
		ArrayList<Hook>emptyHook = new ArrayList<Hook>();
		
		//positionnement du robot
		actionneurs.monterTapisDroit();
		actionneurs.monterTapisGauche();
		locomotion.avancer(scriptCarpet.getDistance(), emptyHook, false);
		Random rand = new Random();
		locomotion.tourner(rand.nextDouble(), emptyHook, false);
	}

	@After
	public void tearDown() throws Exception 
	{
		actionneurs.monterTapisDroit();
		actionneurs.monterTapisGauche();
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
