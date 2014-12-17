package table.obstacles;

import java.util.ArrayList;

import smartMath.Point;
import smartMath.Vec2;
import utils.Log;
import utils.Config;

/**
 * Traite tout ce qui concerne la gestion des obstacles sur la table.
 * Les obstacles peuvent être fixes (bordures de la table par exemple) ou bien mobile (et alors considérés temporaires).
 * Un robot ennemi est une obstacle mobile par exemple. 
 * 
 * @author pf, marsu
 */

public class ObstacleManager
{
	/** système de log sur lequel écrire. */
    @SuppressWarnings("unused")
    private Log log;

	/** endroit ou lire la configuration du robot */
    @SuppressWarnings("unused")
	private Config config;

    /** Ensemble des obstacles mobiles/temporaires se trouvant sur la table */
    private ArrayList<Obstacle> mobileObstacles = new ArrayList<Obstacle>();
    
    private ArrayList<ObstacleLinear> m_lines;
	private ArrayList<ObstacleCircular> m_circles;
	private ArrayList<ObstacleRectangular> m_rects;
  
    /**
     * Instancie un nouveau gestionnaire d'obstacle.
     *
     * @param log le système de log sur lequel écrire.
     * @param config l'endroit ou lire la configuration du robot
     */
    public ObstacleManager(Log log, Config config)
    {
        this.log = log;
        this.config = config;
        
        //creation des obstacles, a migrer dans l'initialisation si necessaire
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
      		
      	m_rects.add(new ObstacleRectangular(new Vec2(-1300, 1200), 400, 22));
      	m_rects.add(new ObstacleRectangular(new Vec2(-1465, 800),70,400));
      	m_rects.add(new ObstacleRectangular(new Vec2(-1300, 778),400,22));
      	m_rects.add(new ObstacleRectangular(new Vec2(-1200, 1930),70,70));
      	m_rects.add(new ObstacleRectangular(new Vec2(-900, 1930),70,70));
      	m_rects.add(new ObstacleRectangular(new Vec2(0, 1420),1066,580));
      	m_rects.add(new ObstacleRectangular(new Vec2(0,0), 600,100));
      	m_rects.add(new ObstacleRectangular(new Vec2(900, 1930),70,70));
      	m_rects.add(new ObstacleRectangular(new Vec2(1200, 1930),70,70));
      	m_rects.add(new ObstacleRectangular(new Vec2(1300, 1200),400,22));
      	m_rects.add(new ObstacleRectangular(new Vec2(1465, 800),70,400));
      	m_rects.add(new ObstacleRectangular(new Vec2(1300, 778),400,22));
      	
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
    }    

    /**
     * Rends le gestionnaire d'obstacle fourni en argument explicite égal a ce gestionnaire.
     *
     * @param other les gestionnaire a modifier
     */
    public void copy(ObstacleManager other)
    {
    	//TODO: méthode de copie de ObstacleManager 
    }

    /**
     *  Cette instance est elle dans le même état que celle fournie en arguement explicite ?
     *
     * @param other l'autre instance a comparer
     * @return true, si les deux instance sont dans le meme etat
     */
    public boolean equals(ObstacleManager other)
    {
    	//TODO : a garder a jour
    	boolean IDontKnow = false;
        return IDontKnow;
    }
    
    /**
     * Utilis� par le pathfinding.
     * Retourne tout les les obstacles temporaires/mobiles. (détectés par la balise laser, les capteurs de distance, etc.)
     *
     * @return la liste des obstacles temporaires/mobiles de la table
     */
    public ArrayList<Obstacle> getMobileObstacles()
    {
        return mobileObstacles;
    }
    
    /**
     * Utilis� par le pathfinding.
     * Retourne tout les les obstacles fixes de la table.
     *
     * @return la liste des obstacles fixes de la table
     */
    public ArrayList<Obstacle> getFixedObstacles()
    {
    	// TODO renvoyer la liste des obstacles fixes
        return new ArrayList<Obstacle>();
    }
    
	public ArrayList<ObstacleLinear> getLines()
	{
		return m_lines;
	}
	
	public ArrayList<ObstacleCircular> getCircles()
	{
		return m_circles;
	}
	
	public ArrayList<ObstacleRectangular> getRects()
	{
		return m_rects;
	}
    
    /**
     * Ajoute un obstacle sur la table a la position spécifiée
     *
     * @param position position ou ajouter l'obstacle
     */
    public synchronized void addObstacle(final Vec2 position)
    {
    	// TODO jouter un obstacle quand demandé
    }

    /**
	 * Supprime du gestionnaire tout les obstacles dont la date de péremption est antérieure a la date fournie
     *
     * @param date La date de péremption a partir de laquelle on garde les obstacles.
     */
    public synchronized void removeOutdatedObstacles(long date)
    {
    	// Et pouf !
    	// TODO supprimer les obstacles qui sont périmés
    }

    /**
     * Renvoie true si un obstacle chevauche un disque.
     *
     * @param discCenter le centre du disque a vérifier
     * @param radius le rayon du disque
     * @return true, si au moins un obstacle chevauche le disque
     */
    public boolean isDiscObstructed(final Vec2 discCenter, int radius)
    {
    	//TODO vérifier si le disque est obstrué
    	return false;
    }   

    /**
     * Change le position d'un robot adverse.
     *
     * @param ennemyID num�ro du robot
     * @param position nouvelle position du robot
     */
    public synchronized void setEnnemyNewLocation(int ennemyID, final Vec2 position)
    {
    	//TODO changer la position de l'ennemi demandé
    }
    
    /**
     * Utilis� par le thread de stratégie.
     * renvois la position du robot ennemi voulu sur la table.
     *
     * @return la position de l'ennemi spécifié
     */
    public Vec2 getEnnemyLocation(int ennemyID)
    {
    	//TODO donner la position de l'ennemi demandé
        return  new Vec2();
    }
    
    
    /**
     * Utilis� pour les tests.
     * Renvois le nombre d'obstacles mobiles actuellement en mémoire
     *
     * @return le nombre d'obstacles mobiles actuellement en mémoire
     */
    public int getMobileObstaclesCount()
    {
        return mobileObstacles.size();
    }
    
    /**
     * Vérifie si le position spécifié est dans l'obstacle spécifié ou non
     *
     * @param pos la position a vérifier
     * @param obstacle l'obstacle a considérer
     * @return true, si la position est dans l'obstacle
     */
    public synchronized boolean isPositionInObstacle(Vec2 pos, Obstacle obstacle)
    {
    	//TODO: vérifier si la position actuelle est ou non dans l'obstacle
    	return true;

    }
    
    /**
	 * Vérifie si la position donnée est dégagée ou si elle est dans l'un des obstacles sur la table
     *
     * @param position la position a vérifier
     * @return true, si la position est dans un obstacle
     */
    public synchronized boolean isObstructed(Vec2 position)
    {
    	//TODO : vérifier si la position est dans un obstacle ou non
    	boolean IDontKnow = false;
        return IDontKnow;
    	
    }
}
