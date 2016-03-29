package tests;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import enums.ServiceNames;
import exceptions.Locomotion.UnableToMoveException;
import hook.Hook;
import robot.Robot;
import strategie.GameState;
import table.Table;

public class JUnit_Disengage extends JUnit_Test
{
	private GameState<Robot> state;
	
	private ArrayList<Hook> hooks;
	
	/** si le robot doit effectuer une marche arrière*/
	boolean reverse;
	
	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		state = (GameState<Robot>)container.getService(ServiceNames.GAME_STATE);
		state.updateConfig();
		// à modifier en début de test
		//state.robot.setOrientation(Math.PI);
		//state.robot.setPosition(Table.entryPosition);
	}
	
	@Test
	public void test()
	{	
		// axe x limite entre la table et sa sortie
		int zone = 1500-state.robot.getRobotRadius();
		
		try
		{
			// cas où l'on est entre pi/2 et 3pi/2
			if(state.robot.getOrientation()>-Math.PI/2 && state.robot.getOrientationFast()<Math.PI/2)
			{
				reverse=false;
				state.robot.turn(Math.PI, hooks, true);
			}
			// sinon, nous sommes entre -pi/2 et pi/2
			else
			{
				reverse=true;
				state.robot.turn(0,hooks,true);
			}
		}
		catch(UnableToMoveException e)
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
				int d = (int) (safe/Math.cos(theta));
				if(reverse)
				{
					d=-d;
				}

				state.robot.moveLengthwise(d);
			}
			catch(UnableToMoveException ex)
			{
				log.debug("Echec de sortie");
				state.robot.immobilise();
			}
		}
	}
	
	@After
	public void after()
	{
		log.debug("Fin de test de sortie !");
		state.robot.immobilise();
	}
}
