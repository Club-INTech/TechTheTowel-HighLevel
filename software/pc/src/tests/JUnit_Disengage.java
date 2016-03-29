package tests;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import enums.ServiceNames;
import enums.Speed;
import exceptions.Locomotion.UnableToMoveException;
import hook.Hook;
import robot.Robot;
import smartMath.Vec2;
import strategie.GameState;
import table.Table;

public class JUnit_Disengage extends JUnit_Test
{
	private GameState<Robot> state;
	
	private ArrayList<Hook> hooks;
	
	/** si le robot doit effectuer une marche arrière*/
	boolean reverse;
	
	/** si le robot a bloqué pendant qu'il tourne*/
	boolean hasTurned;
	
	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		state = (GameState<Robot>)container.getService(ServiceNames.GAME_STATE);
		state.updateConfig();
		state.robot.setLocomotionSpeed(Speed.SLOW_ALL);
		// à modifier en début de test
		state.robot.setOrientation(1.01*Math.PI/2);
		state.robot.setPosition(new Vec2(1330,1000));
	}
	
	@Test
	public void testXSup()
	{	
		log.debug("Début de test Disengage !");
		// axe x limite pour que le robot puisse tourner
		int zone = 1499-state.robot.getRobotRadius();
		
		try
		{
			// cas où l'on est entre pi/2 et 3pi/2
			if(state.robot.getOrientation()>Math.PI/2 && state.robot.getOrientationFast()<3*Math.PI/2)
			{
				reverse=false;
				hasTurned=true;
				state.robot.turn(Math.PI, hooks, true);
			}
			// sinon, nous sommes entre -pi/2 et pi/2
			else
			{
				reverse=true;
				hasTurned=true;
				state.robot.turn(0,hooks,true);
			}


			int move = Math.abs(zone-state.robot.getPosition().x);

			if(reverse)
			{
				move=-move;
			}
			
			// on sort des limites de la table
			state.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);
			log.debug("Aucun blocage en tournant, mouvement rectiligne classique !");
			hasTurned=false;
			state.robot.moveLengthwise(move);
			state.robot.turn(0);

		}
		catch(UnableToMoveException e)
		{
			// dans le cas où le robot s'est bloqué pendant sa rotation
			if(hasTurned)
			{
				log.debug(("Mur percuté avec pour orientation :" + state.robot.getOrientation()));
				try
				{
					// une fois bloqué, on tente d'avancer au-delà de la zone limite #trigo
					int safe = Math.abs(zone-state.robot.getPosition().x);
					double theta;
					int deltaY;

					// détermination de l'angle formé avec l'axe d'équation y constant
					if(state.robot.getOrientationFast()>-Math.PI/2 && state.robot.getOrientationFast()<Math.PI/2)
					{
						theta=state.robot.getOrientationFast()-Math.PI;
					}
					else
					{
						theta=state.robot.getOrientationFast();
					}

					// détermination de la longueur parcourue selon y pour se sortir, si elle nous met hors de la table en y, c'est pas intéressant
					if(state.robot.getOrientationFast()>-Math.PI/2 && state.robot.getOrientationFast()<Math.PI)
					{
						deltaY=2000-state.robot.getRobotRadius();
					}
					else
					{
						deltaY=Math.abs(state.robot.getPositionFast().y-state.robot.getRobotRadius());
					}
					if(safe*Math.tan(theta)>deltaY)
					{
						log.debug("Mouvement rectiligne non pertinent, il faut passer par des arcs !");
					}

					// dans le cas favorable, on se déplace en ligne droite
					int d = (int) Math.abs((safe/Math.cos(theta)));
					if(reverse)
					{
						d=-d;
					}
					state.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);
					state.robot.moveLengthwise(d);
					state.robot.turn(Math.PI);
				}
				catch(UnableToMoveException ex)
				{
					log.debug("Echec de sortie");
					state.robot.immobilise();
				}
			}
			// cas où le robot s'est bloqué pendant la trajectoire rectiligne
			else
			{
				log.debug("Robot bloqué pendant le moveLengthwise !");
			}
		}
	}
	
	/*
	@Test
	public void testYInf()
	{	
		log.debug("Début de test Disengage !");
		// axe y limite pour que le robot puisse tourner
		int zone = 1999-state.robot.getRobotRadius();
		
		try
		{
			// cas où l'on est entre pi et 2pi
			if(state.robot.getOrientation()>Math.PI && state.robot.getOrientationFast()<2*Math.PI)
			{
				reverse=false;
				hasTurned=true;
				state.robot.turn(-Math.PI/2, hooks, true);
			}
			// sinon, nous sommes entre 0 et pi
			else
			{
				reverse=true;
				hasTurned=true;
				state.robot.turn(0,hooks,true);
			}


			int move = Math.abs(zone-state.robot.getPosition().y);

			if(reverse)
			{
				move=-move;
			}
			
			// on sort des limites de la table
			state.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);
			log.debug("Aucun blocage en tournant, mouvement rectiligne classique !");
			hasTurned=false;
			state.robot.moveLengthwise(move);
			state.robot.turn(0);

		}
		catch(UnableToMoveException e)
		{
			// dans le cas où le robot s'est bloqué pendant sa rotation
			if(hasTurned)
			{
				log.debug(("Mur percuté avec pour orientation :" + state.robot.getOrientation()));
				try
				{
					// une fois bloqué, on tente d'avancer au-delà de la zone limite #trigo
					int safe = Math.abs(zone-state.robot.getPosition().y);
					double theta;
					int deltaX;

					// détermination de l'angle formé avec l'axe d'équation y constant
					if(state.robot.getOrientationFast()>-Math.PI/2 && state.robot.getOrientationFast()<Math.PI/2)
					{
						theta=state.robot.getOrientationFast()-Math.PI;
					}
					else
					{
						theta=state.robot.getOrientationFast();
					}

					// détermination de la longueur parcourue selon y pour se sortir, si elle nous met hors de la table en x, c'est pas intéressant
					if(state.robot.getOrientationFast()>-Math.PI/2 && state.robot.getOrientationFast()<Math.PI)
					{
						deltaX=1500-state.robot.getRobotRadius();
					}
					else
					{
						deltaX=Math.abs(state.robot.getPositionFast().y-state.robot.getRobotRadius());
					}
					if(safe*Math.tan(theta)>deltaX)
					{
						log.debug("Mouvement rectiligne non pertinent, il faut passer par des arcs !");
					}

					// dans le cas favorable, on se déplace en ligne droite
					int d = (int) Math.abs((safe/Math.cos(theta)));
					if(reverse)
					{
						d=-d;
					}
					state.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);
					state.robot.moveLengthwise(d);
					state.robot.turn(Math.PI);
				}
				catch(UnableToMoveException ex)
				{
					log.debug("Echec de sortie");
					state.robot.immobilise();
				}
			}
			// cas où le robot s'est bloqué pendant la trajectoire rectiligne
			else
			{
				log.debug("Robot bloqué pendant le moveLengthwise !");
			}
		}
	}
	*/
	
	@After
	public void after()
	{
		log.debug("Fin de test de sortie !");
		state.robot.immobilise();
	}
}
