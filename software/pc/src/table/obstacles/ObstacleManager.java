package table.obstacles;

import java.util.ArrayList;

import smartMath.Vec2;
import utils.Log;
import utils.Config;

// TODO: Auto-generated Javadoc
/**
 * Traite tout ce qui concerne la gestion des obstacles.
 * @author pf, marsu
 *
 */

public class ObstacleManager
{
    
    /** The log. */
    @SuppressWarnings("unused")
    private Log log;
    
    /** The config. */
    @SuppressWarnings("unused")
	private Config config;

    /** The list obstacles. */
    private ArrayList<ObstacleCircular> listObstacles = new ArrayList<ObstacleCircular>();
  
    /**
     * Instantiates a new obstacle manager.
     *
     * @param log the log
     * @param config the config
     */
    public ObstacleManager(Log log, Config config)
    {
        this.log = log;
        this.config = config;
        
        maj_config();
    }
    
    /**
     * Maj_config.
     */
    public void maj_config()
    {
    }
    

    /**
     * Copy.
     *
     * @param other the other
     */
    public void copy(ObstacleManager other)
    {
    }
    
    

    /**
     * Utilis� par le pathfinding. Retourne uniquement les obstacles temporaires.
     *
     * @return the list obstacles
     */
    public ArrayList<ObstacleCircular> getListObstacles()
    {
        return listObstacles;
    }
    
    /**
     * Utilis� par le pathfinding. Retourne uniquement les obstacles fixes.
     *
     * @param codeTorches the code torches
     * @return the list obstacles fixes
     */
    public ArrayList<Obstacle> getListObstaclesFixes(int codeTorches)
    {
    	// TODO
        return new ArrayList<Obstacle>();
    }
    
    

    /**
     * Creer_obstacle.
     *
     * @param position the position
     */
    public synchronized void creer_obstacle(final Vec2 position)
    {
    	// TODO
    }

    /**
     * Appel fait lors de l'anticipation, supprime les obstacles p�rim�s � une date future.
     *
     * @param date the date
     */
    public synchronized void supprimerObstaclesPerimes(long date)
    {
    	// Et pouf !
    	// TODO
    }
    

    /**
     * Renvoie true si un obstacle est � une distance inf�rieur � "distance" du point "centre_detection".
     *
     * @param centre_detection the centre_detection
     * @param distance the distance
     * @return true, if successful
     */
    public boolean obstaclePresent(final Vec2 centre_detection, int distance)
    {
    	//TODO
    	return false;
    }   

    /**
     * Change le position d'un robot adverse.
     *
     * @param i num�ro du robot
     * @param position nouvelle position du robot
     */
    public synchronized void deplacer_robot_adverse(int i, final Vec2 position)
    {
    	//TODO
    }
    
    /**
     * Utilis� par le thread de strat�gie.
     *
     * @return the _positions_ennemis
     */
    public Vec2[] get_positions_ennemis()
    {
    	// TODO
        return  new Vec2[1];
    }
    
    
    /**
     * Utilis� pour les tests.
     *
     * @return le nombre ed'obstacles mobiles d�tect�s
     */
    public int nb_obstacles()
    {
        return listObstacles.size();
    }
    
    
    /**
     * Dans_obstacle.
     *
     * @param pos the pos
     * @param obstacle the obstacle
     * @return true, if successful
     */
    public boolean dans_obstacle(Vec2 pos, Obstacle obstacle)
    {

    	//TODO !
    	return true;

    }
    
    
    
    /**
     * Indique si un obstacle fixe de centre proche de la position indiquée existe.
     *
     * @param position the position
     * @return true, if successful
     */
    public synchronized boolean obstacle_existe(Vec2 position)
    {
    	//TODO
    	boolean IDontKnow = false;
        return IDontKnow;
    	
    }
    
    /**
     *  Cette instance est elle dans le même état que other ?.
     *
     * @param other the other
     * @return true, if successful
     */
    public boolean equals(ObstacleManager other)
    {
    	//TODO
    	boolean IDontKnow = false;
        return IDontKnow;
    }
    

}
