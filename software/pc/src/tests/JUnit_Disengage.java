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
import smartMath.Arc;
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
		state.robot.setOrientation(Math.PI/2);
		log.debug(state.robot.getOrientation());
		log.debug(Math.PI/2);
		state.robot.setPosition(new Vec2(1330,1050));
	}
	/*
	@Test
	public void testXNeg()
	{	
		log.debug("Début de test Disengage !");
		// axe x limite pour que le robot puisse tourner
		int zone = -1499+state.robot.getRobotRadius();
		
		try
		{
			// cas où l'on est entre pi/2 et -pi/2
			if(state.robot.getOrientation()<Math.PI/2 && state.robot.getOrientation()>-Math.PI/2)
			{
				reverse=false;
				hasTurned=true;
				state.robot.turn(0, hooks, true);
			}
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
					double robotOrientation = state.robot.getOrientationFast();

					// détermination de l'angle formé avec l'axe d'équation y constant
					if(robotOrientation<Math.PI/2 && robotOrientation>-Math.PI/2)
					{
						theta=robotOrientation;
					}
					else
					{
						theta=robotOrientation-Math.PI/2;
					}


					// dans le cas favorable, on se déplace en ligne droite
					int d = (int) Math.abs((safe/Math.cos(theta)));
					
					// déplacement selon y pour voir si la trajectoire rectiligne est intéressante
					deltaY = (int)(Math.abs(Math.tan(theta)*safe));
					if(state.robot.getPositionFast().y+deltaY>1999-state.robot.getRobotRadius() || state.robot.getPositionFast().y-deltaY<state.robot.getRobotRadius())
					{
						log.debug("Déplacement de d :" + d);
						log.debug("Composante selon y de d :" + deltaY);
						log.debug("Robot en " + state.robot.getPositionFast());
						log.debug("Soit arrivée en y prévue à :" + (deltaY+state.robot.getPositionFast().y));
						log.debug("Mouvement rectiligne non pertinent, il faut passer par des arcs !");
						return;
					}
					
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
	
	/*
	@Test
	public void testYInf()
	{	
		log.debug("Début de test Disengage !");
		// axe y limite pour que le robot puisse tourner
		int zone = state.robot.getRobotRadius();
		int move = Math.abs(zone-state.robot.getPosition().y);
		
		try
		{
			// cas orentation négative
			if(state.robot.getOrientation()<0)
			{
				reverse=true;
				hasTurned=true;
				move=-move;
				state.robot.turn(-Math.PI/2, hooks, true);
			}
			// sinon, angle positif
			else
			{
				reverse=false;
				hasTurned=true;
				state.robot.turn(Math.PI/2,hooks,true);
			}
			
			// on sort des limites de la table
			state.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);
			log.debug("Aucun blocage en tournant, mouvement rectiligne classique !");
			hasTurned=false;
			state.robot.moveLengthwise(move);
			state.robot.turn(Math.PI/2);

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
					if(state.robot.getOrientationFast()<0)
					{
						theta=state.robot.getOrientationFast() + Math.PI/2;
					}
					else
					{
						theta=Math.PI/2-state.robot.getOrientationFast();
					}

					// dans le cas favorable, on se déplace en ligne droite
					int d = (int) Math.abs((safe/Math.cos(theta)));
					
					// déplacement selon x pour voir si la trajectoire rectiligne est intéressante
					deltaX = (int)(Math.tan(theta)*safe);
					if(reverse && (state.robot.getPositionFast().x-deltaX>1499-state.robot.getRobotRadius() || state.robot.getPositionFast().x-deltaX<-1499+state.robot.getRobotRadius()))
					{
						log.debug("Déplacement de d :" + d);
						log.debug("Composante selon x de d :" + deltaX);
						log.debug("Robot en " + state.robot.getPositionFast());
						log.debug("Soit arrivée en x prévue à :" + (deltaX+state.robot.getPositionFast().x));
						log.debug("Mouvement rectiligne non pertinent, il faut passer par des arcs !");
						return;
					}
					
					if(reverse)
					{
						d=-d;
					}
					state.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);
					state.robot.moveLengthwise(d);
					state.robot.turn(Math.PI/2);
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
	
	@Test
	public void arcXPositive()
	{
		// tant qu'on n'est pas sorti
		while(!isInTable())
		{
			try
			{
				// détermination de la marche avant ou arrière
				log.debug(state.robot.getOrientation());
				if(state.robot.getPosition().x<0)
				{
					if(state.robot.getOrientation()>-Math.PI/2 && state.robot.getOrientationFast()< Math.PI/2)
					{
						reverse = false;
					}
					else
					{
						reverse = true;
					}
				}
				else
				{
					if(state.robot.getOrientation()>-Math.PI/2 && state.robot.getOrientationFast()< Math.PI/2)
					{
						reverse = true;
					}
					else
					{
						reverse = false;
					}
				}

				// longueur d'arc arbitraire
				int length = 100;
				log.debug(reverse);
				if (reverse)
				{
					length=-length;
				}

				// rayon arbitraire
				Arc test = new Arc(600,length,state.robot.getOrientation(),false);
				state.robot.moveArc(test, hooks);
				log.debug(state.robot.getPosition());
			}
			catch(Exception e)
			{
				log.debug("Fail : " + e ); // poney
				log.debug("Tentative de déplacement");
				try
				{
					if(reverse)
					{
						state.robot.moveLengthwise(100, hooks, true);
					}
					else
					{
						state.robot.moveLengthwise(-100,hooks,true);
					}
				}
				catch(Exception ex)
				{
					log.debug("Fail : " + ex);
					log.debug("Nouvelle boucle !");
				}
			}
		}
	}
	
	/** booléen indiquant si le robot est dans les limites habituelles de la table*/
	public boolean isInTable()
	{
		int botX = state.robot.getPosition().x;
		int botY = state.robot.getPosition().y;
		int radius = state.robot.getRobotRadius();
		log.debug(botX);
		log.debug(botY);
		if(botX <= 1499 - radius && botX >= -1499 + radius)
		{
			if(botY<=1999 - radius && botY>= 1+radius )
			{
				return true;
			}
		}
		return false;
	}
	
	
	@After
	public void after()
	{
		log.debug("Fin de test de sortie !");
	}
}
