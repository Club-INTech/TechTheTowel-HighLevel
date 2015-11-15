package tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Random;

import org.junit.Test;
import java.lang.System;
import enums.ServiceNames;
import exceptions.PathNotFoundException;
import exceptions.PointInObstacleException;
import exceptions.Locomotion.UnableToMoveException;
import hook.Hook;
import robot.Robot;
import robot.RobotChrono;
import scripts.ScriptManager;
import smartMath.Vec2;
import strategie.GameState;
import table.Table;
import utils.Log;

public class JUnit_TestBaliseClement extends JUnit_Test
{

	GameState<Robot> clement;
	Table table;
	//TODO deja un log dans la classe mere
	Log log;
	long time;
	
	@SuppressWarnings("unchecked")
	public void setUp() throws Exception
	{
		//creation des objets pour le test
		super.setUp();                                                                                                                                
		clement = (GameState<Robot>)container.getService(ServiceNames.GAME_STATE);
		
		//position initiale du robot
		clement.robot.setPosition(Table.entryPosition);
		clement.robot.setOrientation(Math.PI);
		
		table = (Table)container.getService(ServiceNames.TABLE);
		log = (Log)container.getService(ServiceNames.LOG);
		this.time = System.currentTimeMillis();
	}	
		
	@Test
	public void test() {
		//TODO vraiment utile comme appel ? 
		//la fonction run n'est utilisée qu'une fois et elle n'a pas vocation a etre reutilisee
		run();
	}
	
	/**
	 * La mï¿½thode NextPoint renvoie un point gï¿½nï¿½rï¿½ alï¿½atoirement
	 * @return un point sur la table ([-1500,1500],[0,2000])
	 */
	private Vec2 NextPoint() 
	{
		Random random = new Random();
		Vec2 point = new Vec2(random.nextInt(3001)-1500,random.nextInt(2001));
		log.debug(point);
		return(point);
	}
		
	public void run()
	{
		while((System.currentTimeMillis()-time)<90000)
		{
			Vec2 point = NextPoint();
			try
			{	
				clement.robot.moveToLocation(point,new ArrayList<Hook>(), table);
			} 
			catch (PathNotFoundException e) 
			{
				log.debug("pas de chemin entre : "+clement.robot.getPosition()+" et : "+point);
			} 
			catch (UnableToMoveException e) 
			{
				log.debug("robot bloque");
				return;//on arrete le test pour preserver la mecanique du robot
			}
			catch (PointInObstacleException e) 
			{
				log.debug("le point : "+point+" est dans un obtacle");
				//Si le point est mal placÃ©, on relance
				run();//TODO mauvaise idee. tu est dans un while donc ne rien faire continuera aussi ta boucle
			}
		}
	}
}