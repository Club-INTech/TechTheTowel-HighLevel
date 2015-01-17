package table.obstacles;

import java.util.ArrayList;

import smartMath.*;
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
    
    private ArrayList<Segment> mLines;
	private ArrayList<ObstacleCircular> mGreenPlots;
	private ArrayList<ObstacleCircular> mYellowPlots;
	private ArrayList<ObstacleCircular> mEnnemyRobot;
	private ArrayList<ObstacleRectangular> mRects;
  
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
        
        //creation des obstacles
        mLines = new ArrayList<Segment>();
		mGreenPlots = new ArrayList<ObstacleCircular>();
		mYellowPlots = new ArrayList<ObstacleCircular>();
		mEnnemyRobot = new ArrayList<ObstacleCircular>();
		mRects = new ArrayList<ObstacleRectangular>();
        
		//TODO: appeler la config pour ces valeurs
        double radius = 190;
		int rayonPlot = 30;
		
		
		//obstacles 1, 2, 3
      	mLines.add(new Segment(new Vec2(-1500, 778), new Vec2(-1100, 778)));
      	mLines.add(new Segment(new Vec2(-1100, 778), new Vec2(-1100, 1222)));
   		mLines.add(new Segment(new Vec2(-1100, 1222), new Vec2(-1500, 1222)));
     		
      	//obstacles 10, 11, 12
     	mLines.add(new Segment(new Vec2(1500, 1222), new Vec2(1100, 1222)));
   		mLines.add(new Segment(new Vec2(1100, 1222), new Vec2(1100, 778)));
      	mLines.add(new Segment(new Vec2(1100, 778), new Vec2(1500, 778)));
      		
      	//obstacle 6
      	mLines.add(new Segment(new Vec2(533, 1930), new Vec2(533, 1420)));
      	mLines.add(new Segment(new Vec2(533, 1420), new Vec2(-533, 1420)));
      	mLines.add(new Segment(new Vec2(-533, 1420), new Vec2(-533, 1930)));
      		
      		
      	//obstacle 7
      	mLines.add(new Segment(new Vec2(300, 0), new Vec2(300, 100)));
      	mLines.add(new Segment(new Vec2(300, 100), new Vec2(-300, 100)));
      	mLines.add(new Segment(new Vec2(-300, 100), new Vec2(-300, 0)));
      		
      	//bords de la table
      	mLines.add(new Segment(new Vec2(-1500, 0), new Vec2(1500, 0)));
      	mLines.add(new Segment(new Vec2(1500, 0), new Vec2(1500, 1930)));
      	mLines.add(new Segment(new Vec2(1500, 1930), new Vec2(-1500, 1930)));
      	mLines.add(new Segment(new Vec2(-1500, 1930), new Vec2(-1500, 0)));
      	
      	mRects.add(new ObstacleRectangular(new Vec2(-1300, 778),400,444));
      	mRects.add(new ObstacleRectangular(new Vec2(-1200, 1930),70,70));
      	mRects.add(new ObstacleRectangular(new Vec2(-900, 1930),70,70));
      	mRects.add(new ObstacleRectangular(new Vec2(0, 1420),1066,580));
      	mRects.add(new ObstacleRectangular(new Vec2(0,0), 600,100));
      	mRects.add(new ObstacleRectangular(new Vec2(900, 1930),70,70));
      	mRects.add(new ObstacleRectangular(new Vec2(1200, 1930),70,70));
      	mRects.add(new ObstacleRectangular(new Vec2(1300, 778),400,444));

      	//obstacles plots verts 
      	mGreenPlots.add(new ObstacleCircular(new Vec2(-1410, 1800), rayonPlot));
      	mGreenPlots.add(new ObstacleCircular(new Vec2(-650, 1900), rayonPlot));
      	mGreenPlots.add(new ObstacleCircular(new Vec2(-650, 1800), rayonPlot));
      	mGreenPlots.add(new ObstacleCircular(new Vec2(-630, 645), rayonPlot));
      	mGreenPlots.add(new ObstacleCircular(new Vec2(-200, 600), rayonPlot));
      	mGreenPlots.add(new ObstacleCircular(new Vec2(-400, 250), rayonPlot));
      	mGreenPlots.add(new ObstacleCircular(new Vec2(-1410, 250), rayonPlot));
      	mGreenPlots.add(new ObstacleCircular(new Vec2(-1410, 150), rayonPlot));

      	// obstacles plots jaunes
      	mYellowPlots.add(new ObstacleCircular(new Vec2(1410, 1800), rayonPlot));
      	mYellowPlots.add(new ObstacleCircular(new Vec2(650, 1900), rayonPlot));
      	mYellowPlots.add(new ObstacleCircular(new Vec2(650, 1800), rayonPlot));
      	mYellowPlots.add(new ObstacleCircular(new Vec2(630, 645), rayonPlot));
      	mYellowPlots.add(new ObstacleCircular(new Vec2(200, 600), rayonPlot));
      	mYellowPlots.add(new ObstacleCircular(new Vec2(400, 250), rayonPlot));
      	mYellowPlots.add(new ObstacleCircular(new Vec2(1410, 250), rayonPlot));
      	mYellowPlots.add(new ObstacleCircular(new Vec2(1410, 150), rayonPlot));
      	
      	//ennemi
      	mEnnemyRobot.add(new ObstacleCircular(new Vec2(0, 1000), 200));
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
    
	public ArrayList<Segment> getLines()
	{
		return mLines;
	}
	
	public ArrayList<ObstacleCircular> getGreenPlots()
	{
		return mGreenPlots;
	}
	
	public ArrayList<ObstacleCircular> getYellowPlots()
	{
		return mYellowPlots;
	}
	
	public ArrayList<ObstacleCircular> getEnnemyRobot()
	{
		return mEnnemyRobot;
	}
	
	public void setEnnemyRobotPosition(Vec2 position, int index)
	{
		mEnnemyRobot.get(index).setPosition(position);
	}
	
	public ArrayList<ObstacleRectangular> getRects()
	{
		return mRects;
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
