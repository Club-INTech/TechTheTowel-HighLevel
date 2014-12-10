package table;

import java.util.ArrayList;

import table.obstacles.*;
import container.Service;
import utils.*;
import smartMath.*;

/* Positions :
 * 			_______________________________________________________
 * 			|-1500,1000         	0,1000		         1500,1000|
 * 			|           		      							  |
 * 			|           		     							  |
 * 			|           	 		  							  |
 * 			|           	 		  							  |
 * 			|           	 		  							  |
 * 			|-1500,0           		 0,0       				1500,0|
 *          -------------------------------------------------------
 *          
 *          TODO : migrer la liste d'obstacles vers l'obstacle manager
 */

/**
 * Stocke toute les informations liées a la table (muables et immuables) au cours d'un match.
 */
@SuppressWarnings("unused")
public class Table implements Service
{
	/** Le gestionnaire d'obstacle. */
	public ObstacleManager mObstacleManager;

<<<<<<< HEAD
	public ObstacleManager gestionobstacles;

	// Dépendances
=======
	/** système de log sur lequel écrire. */
>>>>>>> refs/remotes/origin/refactor
	private Log log;

	/** endroit ou lire la configuration du robot */
	private Config config;
	private ArrayList<ObstacleLinear> m_lines;
	private ArrayList<ObstacleCircular> m_circles;
	private ArrayList<ObstacleRectangular> m_rects;

	private boolean isClap1Closed;
	private boolean isClap2Closed;
	private boolean isClap3Closed;

	private boolean isLeftCarpetDropped;
	private boolean isRightCarpetDropped;
	
	/**
	 * Instancie une nouvelle table
	 *
	 * @param log le système de log sur lequel écrire.
	 * @param config l'endroit ou lire la configuration du robot
	 */
	public Table(Log log, Config config)
	{
		this.log = log;
		this.config = config;
<<<<<<< HEAD
		this.gestionobstacles = new ObstacleManager(log, config);
		initialise();
	}
	
	public void initialise()//initialise la table du debut du jeu (obstacles fixes)
	{
		m_lines = new ArrayList<ObstacleLinear>();
		m_circles = new ArrayList<ObstacleCircular>();
		m_rects = new ArrayList<ObstacleRectangular>();
		
		double radius = 190;
		int rayonPlot = 30;
		
		//1 + 2 + 3 + nodes
		m_lines.add(new ObstacleLinear(new Point(-1500 + radius, 778 - radius), new Point(-1100 + radius, 778 - radius), 1, new Point(-1095 + radius, 778 - radius), new Point(0, 0)));
		m_lines.add(new ObstacleLinear(new Point(-1100 + radius, 778 - radius), new Point(-1100 + radius, 1222 + radius), 2, new Point(-1095 + radius, 778 - radius), new Point(-1095 + radius, 1222 + radius)));
		m_lines.add(new ObstacleLinear(new Point(-1100 + radius, 1222 + radius), new Point(-1500 + radius, 1222 + radius), 1, new Point(-1095 + radius, 1222 + radius), new Point(0, 0)));
		
		//10 + 11 + 12 + nodes
		m_lines.add(new ObstacleLinear(new Point(1500 - radius, 1222 + radius), new Point(1100 - radius, 1222 + radius), 1, new Point(1095 - radius, 1222 + radius), new Point(0, 0)));
		m_lines.add(new ObstacleLinear(new Point(1100 - radius, 1222 + radius), new Point(1100 - radius, 778 - radius), 2, new Point(1095 - radius, 1222 + radius), new Point(1095 - radius, 778 - radius)));
		m_lines.add(new ObstacleLinear(new Point(1100 - radius, 778 - radius), new Point(1500 - radius, 778 - radius), 1, new Point(1095 - radius, 778 - radius), new Point(0, 0)));
		
		//6 + nodes
		m_lines.add(new ObstacleLinear(new Point(533 + radius, 1930 - radius), new Point(533 + radius, 1420 - radius), 1, new Point(533 + radius, 1415 - radius), new Point(0, 0)));
		m_lines.add(new ObstacleLinear(new Point(533 + radius, 1420 - radius), new Point(-533 - radius, 1420 - radius), 2, new Point(533 + radius, 1415 - radius), new Point(-533 - radius, 1415 - radius)));
		m_lines.add(new ObstacleLinear(new Point(-533 - radius, 1420 - radius), new Point(-533 - radius, 1930 - radius), 1, new Point(-533 - radius, 1415 - radius), new Point(0, 0)));
		
		
		//7 + nodes
		m_lines.add(new ObstacleLinear(new Point(300 + radius, 0 + radius), new Point(300 + radius, 100 + radius), 1, new Point(300 + radius, 105 + radius), new Point(0, 0)));
		m_lines.add(new ObstacleLinear(new Point(300 + radius, 100 + radius), new Point(-300 - radius, 100 + radius), 2, new Point(300 + radius, 105 + radius), new Point(-300 - radius, 105 + radius)));
		m_lines.add(new ObstacleLinear(new Point(-300 - radius, 100 + radius), new Point(-300 - radius, 0 + radius), 1, new Point(-300 - radius, 105 + radius), new Point(0, 0)));
		
		//table
		m_lines.add(new ObstacleLinear(new Point(-1500 + radius, 0 + radius), new Point(1500 - radius, 0 + radius), 0, new Point(0, 0), new Point(0, 0)));
		m_lines.add(new ObstacleLinear(new Point(1500 - radius, 0 + radius), new Point(1500 - radius, 1930 - radius), 0, new Point(0, 0), new Point(0, 0)));
		m_lines.add(new ObstacleLinear(new Point(1500 - radius, 1930 - radius), new Point(-1500 + radius, 1930 - radius), 0, new Point(0, 0), new Point(0, 0)));
		m_lines.add(new ObstacleLinear(new Point(-1500 + radius, 1930 - radius), new Point(-1500 + radius, 0 + radius), 0, new Point(0, 0), new Point(0, 0)));
		
		
		
		//obstacle plots verts 
		m_circles.add(new ObstacleCircular(new Vec2(-1410, 800), rayonPlot));
		m_circles.add(new ObstacleCircular(new Vec2(-650, 900), rayonPlot));
		m_circles.add(new ObstacleCircular(new Vec2(-650, 800), rayonPlot));
		m_circles.add(new ObstacleCircular(new Vec2(-630, -355), rayonPlot));
		m_circles.add(new ObstacleCircular(new Vec2(-200, -400), rayonPlot));
		m_circles.add(new ObstacleCircular(new Vec2(-400, -750), rayonPlot));
		m_circles.add(new ObstacleCircular(new Vec2(-1410, -750), rayonPlot));
		m_circles.add(new ObstacleCircular(new Vec2(-1410, -850), rayonPlot));
		
		// obstacle plots jaunes
		m_circles.add(new ObstacleCircular(new Vec2(1410, 800), rayonPlot));
		m_circles.add(new ObstacleCircular(new Vec2(650, 900), rayonPlot));
		m_circles.add(new ObstacleCircular(new Vec2(650, 800), rayonPlot));
		m_circles.add(new ObstacleCircular(new Vec2(630, -355), rayonPlot));
		m_circles.add(new ObstacleCircular(new Vec2(200, -400), rayonPlot));
		m_circles.add(new ObstacleCircular(new Vec2(400, -750), rayonPlot));
		m_circles.add(new ObstacleCircular(new Vec2(1410, -750), rayonPlot));
		m_circles.add(new ObstacleCircular(new Vec2(1410, -850), rayonPlot));
		
		// Claps
		
		isClap1Closed=false;
		isClap2Closed=false;
		isClap3Closed=false;

		//les tapis
		isLeftCarpetDropped = false;
		isRightCarpetDropped = false;
	}
	
	public boolean getIsClap1Closed() {
		return isClap1Closed;
	}

	public void setIsClap1Closed(boolean isClap1Closed) {
		this.isClap1Closed = isClap1Closed;
	}

	public boolean getIsClap2Closed() {
		return isClap2Closed;
	}

	public void setIsClap2Closed(boolean isClap2Closed) {
		this.isClap2Closed = isClap2Closed;
	}

	public boolean getIsClap3Closed() {
		return isClap3Closed;
	}

	public void setIsClap3Closed(boolean isClap3Closed) {
		this.isClap3Closed = isClap3Closed;
	}

	//La table
	/**
	 * La table en argument deviendra la copie de this (this reste inchangé)
	 * @param ct
	 */
	public void copy(Table ct) // TODO
	{
        if(!equals(ct))
		{
        	// TODO: faire grande optimisation de ceci a grand coup de hashs
        	
        	
			if(!gestionobstacles.equals(ct.gestionobstacles))
			    gestionobstacles.copy(ct.gestionobstacles);
		}
	}
	
	public Table clone()
	{
		Table cloned_table = new Table(log, config);
		copy(cloned_table);
		return cloned_table;
	}

	/**
	 * Utilisé pour les tests
	 * @param other
	 * @return
	 */
	public boolean equals(Table other)
	{
		return 	false; //TODO
 	}

	@Override
	public void updateConfig() {
		// TODO Auto-generated method stub
=======
		this.mObstacleManager = new ObstacleManager(log, config);
>>>>>>> refs/remotes/origin/refactor
		
	}
	
<<<<<<< HEAD
	//accesseurs
	
	public ArrayList<ObstacleLinear> getLines()
	{
		return m_lines;
	}
	
	public ArrayList<ObstacleCircular> getCircles()
	{
		return m_circles;
	}
	
    public boolean getIsLeftCarpetDropped() 
	{
		return isLeftCarpetDropped;
	}
    public void setIsLeftCarpetDropped(boolean newValue)
    {
    	isLeftCarpetDropped=newValue;
    }
    public boolean getIsRightCarpetDropped() 
  	{
  		return isRightCarpetDropped;
  	}
      public void setIsRightCarpetDropped(boolean newValue)
      {
      	isRightCarpetDropped=newValue;
      }
=======
	/**
	 * Compare deux tables et indique si elles sont égales.
	 * Utilisé pour les tests.
	 *
	 * @param other l'autre table a comparer
	 * @return true, si les deux tables sont identiques
	 */
	public boolean equals(Table other)
	{
		return false; //TODO écrire puis compléter au fur et a mesure cette fonction pour qu'elle reste a jour.
 	}

	/* (non-Javadoc)
	 * @see container.Service#updateConfig()
	 */
	@Override
	public void updateConfig()
	{
		// TODO Auto-generated method stub
		
	}
>>>>>>> refs/remotes/origin/refactor
}

