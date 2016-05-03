package tests;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import enums.ServiceNames;
import enums.Speed;
import hook.Hook;
import robot.Robot;
import smartMath.Arc;
import smartMath.Vec2;
import strategie.GameState;

public class JUnit_Disengage extends JUnit_Test
{
	private GameState<Robot> state;
	
	private ArrayList<Hook> hooks;
	
	/** si le robot a bloqué pendant qu'il tourne*/
	private boolean hasTurned;
	
	/**Boléen déterminant si une marche arrière est nécessaire*/
	private boolean reverse;
	
	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		state = (GameState<Robot>)container.getService(ServiceNames.GAME_STATE);
		state.updateConfig();
		state.robot.setLocomotionSpeed(Speed.SLOW_ALL);
		// à modifier en début de test
	
		state.robot.setPosition(new Vec2(1380,1120));
		state.robot.setOrientation(-Math.PI/2+0.1);
	}
	
	@Test
	public void arcXPositive()
	{
		// tant qu'on n'est pas sorti
		while(!isInTable())
		{
			// rayon arbitraire
			int radius=600;
			try
			{
				log.debug("Orientation du robot : " + state.robot.getOrientation());
				double rOrient = state.robot.getOrientationFast();
				// détermination de la marche avant ou arrière
				if(state.robot.getPosition().x<0)
				{
					reverse = !(rOrient >= -Math.PI / 2 && rOrient < Math.PI / 2);
				}
				else
				{
					reverse = rOrient >= -Math.PI / 2 && rOrient < Math.PI / 2;
					if((rOrient>=-Math.PI && rOrient<=-Math.PI/2) || (rOrient>=-Math.PI/2 && rOrient<=0))
					{
						radius=-radius;
					}
				}
				
				log.debug("Marche arrière ? : " + reverse);
				log.debug("Tentative de dégagement par arc !");

				// longueur d'arc arbitraire
				int length = 200;
				if (reverse)
				{
					length=-length;
				}
				if(config.getProperty("couleur").equals("violet"))
				{
					log.debug("anormal");
					radius=-radius;
				}

				log.debug("Rayon : " + radius);
				log.debug("Longueur : " + length);

				Arc test = new Arc(radius,length,state.robot.getOrientation(),false);
				state.robot.moveArc(test, hooks);
				log.debug("Position du robot : " + state.robot.getPosition());

			}
			catch(Exception e)
			{
				log.debug("Fail : " + e ); // poney
				log.debug("Tentative de déplacement rectiligne !");
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
					log.debug("Nouvelle boucle de dégagement !");
				}
			}
		}
	}
	
//
//	/** Méthode débloquant le robot si hors table x positifs*/
//	public void disengageXPositive()
//	{
//		int radius = state.robot.getRobotRadius();
//		// axe x limite pour que le robot puisse tourner
//		int zone = 1489-radius;
//		
//		try
//		{
//			// cas où l'on est entre pi/2 et -pi/2
//			if(state.robot.getOrientation()>Math.PI/2 || state.robot.getOrientationFast()<-Math.PI/2)
//			{
//				reverse=false;
//				hasTurned=true;
//				state.robot.turn(Math.PI, hooks, true);
//			}
//			
//			else
//			{
//				reverse=true;
//				hasTurned=true;
//				state.robot.turn(0,hooks,true);
//			}
//
//			// distance minimale séparant le robot des limites de la table
//			int move = Math.abs(zone-state.robot.getPosition().x);
//
//			if(reverse)
//			{
//				move=-move;
//			}
//			
//			// on sort des limites de la table
//			state.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);
//			log.debug("Aucun blocage en tournant, mouvement rectiligne classique !");
//			hasTurned=false;
//			state.robot.moveLengthwise(move);
//
//		}
//		catch(UnableToMoveException e)
//		{
//			// dans le cas où le robot s'est bloqué pendant sa rotation
//			if(hasTurned)
//			{
//				log.debug(("Mur percuté avec pour orientation :" + state.robot.getOrientation()));
//				try
//				{
//					// on tente d'avancer au-delà de la zone limite #trigo
//					int safe = Math.abs(zone-state.robot.getPosition().x);
//					double theta;
//					int deltaY;
//					double robotOrientation = state.robot.getOrientationFast();
//
//					// détermination de l'angle formé avec l'axe d'équation y constant
//					if(robotOrientation>-Math.PI/2 && robotOrientation<Math.PI/2)
//					{
//						theta=robotOrientation-Math.PI;
//					}
//					else
//					{
//						theta=robotOrientation;
//					}
//
//
//					// dans le cas favorable, on se déplace en ligne droite
//					int d = (int) Math.abs((safe/Math.cos(theta)));
//					
//					// composante de d selon y pour voir si la trajectoire rectiligne est intéressante
//					deltaY = (int)(Math.abs(Math.tan(theta)*safe));
//					if(state.robot.getPositionFast().y+deltaY>1989-radius || state.robot.getPositionFast().y-deltaY<radius)
//					{
//						log.debug("Ne peut pas sortir sans arcs !");
//						return;
//					}
//					
//					if(reverse)
//					{
//						d=-d;
//					}
//					state.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);
//					state.robot.moveLengthwise(d);
//					state.robot.turn(Math.PI);
//				}
//				catch(UnableToMoveException ex)
//				{
//					log.debug("Echec de sortie :" + ex);
//				}
//			}
//			// cas où le robot s'est bloqué pendant la trajectoire rectiligne
//			else
//			{
//				log.debug("Robot bloqué pendant le moveLengthwise !");
//			}
//		}
//        return;
//	}
//	
//	/** Méthode débloquant le robot près du bord côté sable*/
//	public void disengageYSand()
//	{
//		int radius = state.robot.getRobotRadius();
//		// axe y limite pour que le robot puisse tourner
//		int zone = 1989-radius;
//		int move = Math.abs(zone-state.robot.getPosition().y);
//		
//		try
//		{
//			// cas orentation négative
//			if(state.robot.getOrientation()<0)
//			{
//				reverse=false;
//				hasTurned=true;
//				state.robot.turn(-Math.PI/2, hooks, true);
//			}
//
//			else
//			{
//				reverse=true;
//				hasTurned=true;
//				state.robot.turn(Math.PI/2,hooks,true);
//				move=-move;
//			}
//			
//			// on sort des limites de la table
//			state.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);
//			log.debug("Aucun blocage en tournant, mouvement rectiligne classique !");
//			hasTurned=false;
//			state.robot.moveLengthwise(move);
//
//		}
//		catch(UnableToMoveException e)
//		{
//			// dans le cas où le robot s'est bloqué pendant sa rotation
//			if(hasTurned)
//			{
//				log.debug(("Mur percuté avec pour orientation :" + state.robot.getOrientation()));
//				try
//				{
//					// on tente d'avancer au-delà de la zone limite #trigo
//					int safe = Math.abs(zone-state.robot.getPosition().y);
//					double theta;
//					int deltaX;
//					double robotOrientation = state.robot.getOrientationFast();
//
//					// détermination de l'angle formé avec l'axe d'équation x constant
//					if(robotOrientation<0)
//					{
//						theta=-Math.PI/2-robotOrientation;
//					}
//					else
//					{
//						theta=Math.PI/2-robotOrientation;
//					}
//
//					// dans le cas favorable, on se déplace en ligne droite
//					int d = (int) Math.abs((safe/Math.cos(theta)));
//					
//					// composante de d pingu was here selon x pour voir si la trajectoire rectiligne est intéressante
//					deltaX = (int)(Math.tan(theta)*d);
//					if(reverse && (state.robot.getPositionFast().x-deltaX>1489-radius || state.robot.getPositionFast().x-deltaX<-1489+radius))
//					{
//						log.debug("Ne peut pas sortir sans arcs !");
//						return;
//					}
//					
//					if(reverse)
//					{
//						d=-d;
//					}
//					state.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);
//					state.robot.moveLengthwise(d);
//				}
//				catch(UnableToMoveException ex)
//				{
//					log.debug("Echec de sortie : " + e);
//				}
//			}
//			// cas où le robot s'est bloqué pendant la trajectoire rectiligne
//			else
//			{
//				log.debug("Robot bloqué pendant le moveLengthwise !");
//			}
//		}
//        return;
//	}
//	
//	/** Méthode débloquant le robot près de la mer*/
//	public void disengageYSea()
//	{
//		log.debug("Début de test Disengage !");
//		// axe y limite pour que le robot puisse tourner
//		int zone = state.robot.getRobotRadius();
//		int move = Math.abs(zone-state.robot.getPosition().y);
//		
//		try
//		{
//			// cas orentation négative
//			if(state.robot.getOrientation()<0)
//			{
//				reverse=true;
//				hasTurned=true;
//				move=-move;
//				state.robot.turn(-Math.PI/2, hooks, true);
//			}
//
//			else
//			{
//				reverse=false;
//				hasTurned=true;
//				state.robot.turn(Math.PI/2,hooks,true);
//			}
//			
//			// on sort des limites de la table
//			state.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);
//			log.debug("Aucun blocage en tournant, mouvement rectiligne classique !");
//			hasTurned=false;
//			state.robot.moveLengthwise(move);
//
//		}
//		catch(UnableToMoveException e)
//		{
//			// dans le cas où le robot s'est bloqué pendant sa rotation
//			if(hasTurned)
//			{
//				log.debug(("Mur percuté avec pour orientation :" + state.robot.getOrientation()));
//				try
//				{
//					// on tente d'avancer au-delà de la zone limite #trigo
//					int safe = Math.abs(zone-state.robot.getPosition().y);
//					double theta;
//					int deltaX;
//
//					// détermination de l'angle formé avec l'axe d'équation y constant
//					if(state.robot.getOrientationFast()<0)
//					{
//						theta=state.robot.getOrientationFast() + Math.PI/2;
//					}
//					else
//					{
//						theta=Math.PI/2-state.robot.getOrientationFast();
//					}
//
//					// dans le cas favorable, on se déplace en ligne droite
//					int d = (int) Math.abs((safe/Math.cos(theta)));
//					
//					// déplacement selon x pour voir si la trajectoire rectiligne est intéressante
//					deltaX = (int)(Math.tan(theta)*safe);
//					if(reverse && (state.robot.getPositionFast().x-deltaX>1489-state.robot.getRobotRadius() || state.robot.getPositionFast().x-deltaX<-1489+state.robot.getRobotRadius()))
//					{
//						log.debug("Ne peut pas sortir sans arcs !");
//						return;
//					}
//					
//					if(reverse)
//					{
//						d=-d;
//					}
//					state.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);
//					state.robot.moveLengthwise(d);
//					state.robot.turn(Math.PI/2);
//				}
//				catch(UnableToMoveException ex)
//				{
//					log.debug("Echec de sortie");
//					state.robot.immobilise();
//				}
//			}
//			// cas où le robot s'est bloqué pendant la trajectoire rectiligne
//			else
//			{
//				log.debug("Robot bloqué pendant le moveLengthwise !");
//			}
//		}
//	}
	
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
	
	/** booléen indiquant si le robot est dans les limites habituelles de la table*/
	public boolean isInTable()
	{
		int botX = state.robot.getPosition().x;
		int botY = state.robot.getPosition().y;
		int radius = state.robot.getRobotRadius();
		try
		{
			if(botX <= 1489 - radius && botX >= -1489 + radius)
			{
				if(botY<=1989 - radius && botY>= 10+radius)
				{
					if(config.getProperty("couleur").equals("vert"))
					{
						log.debug("Robot replacé dans la table !");
						return true;
					}
					else if(config.getProperty("couleur").equals("violet"))
					{
						log.debug("Robot encore hors de table !");
						return false;
					}
				}
			}
			else
			{
				if(config.getProperty("couleur").equals("violet"))
				{
					log.debug("Robot replacé dans la table !");
					return true;
				}
				else if(config.getProperty("couleur").equals("vert"))
				{
					log.debug("Robot encore hors de table !");
					return false;
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		log.debug("Robot encore hors de table !");
		return false;
	}
	
	
	@After
	public void after()
	{
		log.debug("Fin de test de sortie !");
	}
}
