package tests;

import java.util.ArrayList;

import hook.Hook;
import hook.types.HookGenerator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pathdinding.Pathfinding;
import exceptions.Locomotion.UnableToMoveException;
import robot.RobotReal;
import robot.cards.ActuatorsManager;
import robot.highlevel.LocomotionHiLevel;
import scripts.DropCarpet;
import scripts.ExitBeginZone;
import table.Table;
import utils.Sleep;


public class JUnit_Match extends JUnit_Test 
{
ActuatorsManager actionneurs;
DropCarpet scriptCarpet;
ExitBeginZone scriptOut;
LocomotionHiLevel locomotion;
<<<<<<< HEAD
PathDingDing pathfinding = new PathDingDing();
=======
Table table;
Pathfinding pathfinding;
RobotReal robot;
>>>>>>> branch 'master' of gitosis@git.club-intech.fr:intech-2015.git
HookGenerator hookgenerator;
ArrayList<Hook> emptyHook = new ArrayList<Hook>();

@Before
public void setUp() throws Exception 
{
	//creation des objets pour le test
	super.setUp();                                                                                                                                 
	actionneurs = (ActuatorsManager)container.getService("Actionneurs");
	hookgenerator = (HookGenerator)container.getService("HookGenerator");
	locomotion = (LocomotionHiLevel)container.getService("DeplacementsHautNiveau");
	robot = (RobotReal)container.getService("RobotVrai");
	table = (Table)container.getService("Table");
	scriptCarpet = new DropCarpet(hookgenerator, config, log, pathfinding, robot, actionneurs, table);
	scriptOut = new ExitBeginZone(hookgenerator, config, log, pathfinding, robot, actionneurs, table);
	
	//positionnement du robot
	actionneurs.highRightCarpet();
	actionneurs.highLeftCarpet();
}

@After
public void tearDown() throws Exception 
{
	actionneurs.highRightCarpet();
	actionneurs.highLeftCarpet();
	
}

@Test
public void test() 
{
	scriptOut.execute(0);
	Sleep.sleep(2000);
	try 
	{
		locomotion.avancer(639, emptyHook, false);
		locomotion.tourner(0, emptyHook, false);
		locomotion.avancer(scriptCarpet.point_entree(0).y-1000, emptyHook, false);
	} 
	catch (UnableToMoveException e) 
	{
		e.printStackTrace();
		log.debug("robot bloque", this);
	}
	Sleep.sleep(2000);
	scriptCarpet.execute(0);
}
}

