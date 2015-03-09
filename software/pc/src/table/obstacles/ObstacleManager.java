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
    private ArrayList<ObstacleProximity> mMobileObstacles;
    private ArrayList<ObstacleCircular> mFixedObstacles;
    
    private ArrayList<Segment> mLines;
	private ArrayList<ObstacleRectangular> mRectangles;

	private int defaultObstacleRadius;
  
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
        
        //creation des listes qui contiendrons les differents types d'obstacles
        mMobileObstacles = new ArrayList<ObstacleProximity>();
        mFixedObstacles = new ArrayList<ObstacleCircular>();
        mLines = new ArrayList<Segment>();
		mRectangles = new ArrayList<ObstacleRectangular>();
		
        int robotRadius = Integer.parseInt(config.getProperty("rayon_robot"));
        defaultObstacleRadius = Integer.parseInt(config.getProperty("rayon_robot_adverse"));
        
        //par defaut
        //mEnnemyRobot1 = new ObstacleCircular(new Vec2(0, 0), 200 + robotRadius);
      	//mEnnemyRobot2 = new ObstacleCircular(new Vec2(0, 0), 200 + robotRadius);
		
        // les numeros sont ceux de la doc sur /pc/config/obstacles
		
		//obstacles 1, 2, 3
      	mLines.add(new Segment(new Vec2(-1500, 778 - robotRadius), new Vec2(-1100 + robotRadius, 778 - robotRadius)));
      	mLines.add(new Segment(new Vec2(-1100 + robotRadius, 778 - robotRadius), new Vec2(-1100 + robotRadius, 1222 + robotRadius)));
   		mLines.add(new Segment(new Vec2(-1100 + robotRadius, 1222 + robotRadius), new Vec2(-1500, 1222 + robotRadius)));
     		
      	//obstacles 10, 11, 12
     	mLines.add(new Segment(new Vec2(1500, 1222 + robotRadius), new Vec2(1100 - robotRadius, 1222 + robotRadius)));
   		mLines.add(new Segment(new Vec2(1100 - robotRadius, 1222 + robotRadius), new Vec2(1100 - robotRadius, 778 - robotRadius)));
      	mLines.add(new Segment(new Vec2(1100 - robotRadius, 778 - robotRadius), new Vec2(1500, 778 - robotRadius)));
      		
      	//obstacle 6
      	mLines.add(new Segment(new Vec2(533 + robotRadius, 2000), new Vec2(533 + robotRadius, 1420 - robotRadius)));
      	mLines.add(new Segment(new Vec2(533 + robotRadius, 1420 - robotRadius), new Vec2(-533 - robotRadius, 1420 - robotRadius)));
      	mLines.add(new Segment(new Vec2(-533 - robotRadius, 1420 - robotRadius), new Vec2(-533 - robotRadius, 2000)));
      		
      		
      	//obstacle 7
      	mLines.add(new Segment(new Vec2(300 + robotRadius, 0), new Vec2(300 + robotRadius, 100 + robotRadius)));
      	mLines.add(new Segment(new Vec2(300 + robotRadius, 100 + robotRadius), new Vec2(-300 - robotRadius, 100 + robotRadius)));
      	mLines.add(new Segment(new Vec2(-300 - robotRadius, 100 + robotRadius), new Vec2(-300 - robotRadius, 0)));
      		
      	//bords de la table
      	mLines.add(new Segment(new Vec2(-1500 + robotRadius, 0 + robotRadius), new Vec2(1500 - robotRadius, 0 + robotRadius)));
      	mLines.add(new Segment(new Vec2(1500 - robotRadius, 0 + robotRadius), new Vec2(1500 - robotRadius, 1930 - robotRadius)));
      	mLines.add(new Segment(new Vec2(1500 - robotRadius, 1930 - robotRadius), new Vec2(-1500 + robotRadius, 1930 - robotRadius)));
      	mLines.add(new Segment(new Vec2(-1500 + robotRadius, 1930 - robotRadius), new Vec2(-1500 + robotRadius, 0 + robotRadius)));
      	
      	//obstacles rectangulaires
      	mRectangles.add(new ObstacleRectangular(new Vec2(-1300, 778),400,444));
      	mRectangles.add(new ObstacleRectangular(new Vec2(-1200, 1930),70,70));
      	mRectangles.add(new ObstacleRectangular(new Vec2(-900, 1930),70,70));
      	mRectangles.add(new ObstacleRectangular(new Vec2(0, 1420),1066,580));
      	mRectangles.add(new ObstacleRectangular(new Vec2(0,0), 600,100));
      	mRectangles.add(new ObstacleRectangular(new Vec2(900, 1930),70,70));
      	mRectangles.add(new ObstacleRectangular(new Vec2(1200, 1930),70,70));
      	mRectangles.add(new ObstacleRectangular(new Vec2(1300, 778),400,444));

	    // obstacles plots verts
	    mFixedObstacles.add(new ObstacleCircular(new Vec2(200, 600), 30 + robotRadius)); // plot 0
	    mFixedObstacles.add(new ObstacleCircular(new Vec2(400, 250), 30 + robotRadius)); // plot 1
	    mFixedObstacles.add(new ObstacleCircular(new Vec2(630, 645), 30 + robotRadius)); // plot 2
	    mFixedObstacles.add(new ObstacleCircular(new Vec2(1410, 150), 30 + robotRadius)); // plot 3
	    mFixedObstacles.add(new ObstacleCircular(new Vec2(1410, 250), 30 + robotRadius)); // plot 4
	    mFixedObstacles.add(new ObstacleCircular(new Vec2(650, 1800), 30 + robotRadius)); // plot 5
	    mFixedObstacles.add(new ObstacleCircular(new Vec2(650, 1900), 30 + robotRadius)); // plot 6
	    mFixedObstacles.add(new ObstacleCircular(new Vec2(1410, 1800), 30 + robotRadius)); // plot 7
      	
	    //obstacles plots jaunes
	    mFixedObstacles.add(new ObstacleCircular(new Vec2(-200, 600), 30 + robotRadius)); // plot 0
	    mFixedObstacles.add(new ObstacleCircular(new Vec2(-400, 250), 30 + robotRadius)); // plot 1
	    mFixedObstacles.add(new ObstacleCircular(new Vec2(-630, 645), 30 + robotRadius)); // plot 2
	    mFixedObstacles.add(new ObstacleCircular(new Vec2(-1410, 150), 30 + robotRadius)); // plot 3
	    mFixedObstacles.add(new ObstacleCircular(new Vec2(-1410, 250), 30 + robotRadius)); // plot 4
	    mFixedObstacles.add(new ObstacleCircular(new Vec2(-650, 1800), 30 + robotRadius)); // plot 5
	    mFixedObstacles.add(new ObstacleCircular(new Vec2(-650, 1900), 30 + robotRadius)); // plot 6
	    mFixedObstacles.add(new ObstacleCircular(new Vec2(-1410, 1800), 30 + robotRadius)); // plot 7

	    // gobelets
	    mFixedObstacles.add(new ObstacleCircular(new Vec2(1250, 250), 48 + robotRadius)); // gobelet 0
	    mFixedObstacles.add(new ObstacleCircular(new Vec2(590, 1170), 48 + robotRadius)); // gobelet 1
	    mFixedObstacles.add(new ObstacleCircular(new Vec2(0, 350), 48 + robotRadius)); // gobelet 2
	    mFixedObstacles.add(new ObstacleCircular(new Vec2(-590, 1170), 48 + robotRadius)); // gobelet 3
	    mFixedObstacles.add(new ObstacleCircular(new Vec2(-1250, 250), 48 + robotRadius)); // gobelet 4
    }    

    /**
     * Rends le gestionnaire d'obstacle fourni en argument explicite égal a ce gestionnaire.
     *
     * @param other les gestionnaire a modifier
     */
    public void copy(ObstacleManager other)
    {
    	//TODO innutilise
    }

    /**
     *  Cette instance est elle dans le même état que celle fournie en arguement explicite ?
     *
     * @param other l'autre instance a comparer
     * @return true, si les deux instance sont dans le meme etat
     */
    public boolean equals(ObstacleManager other)
    {
    	//TODO innutilise
    	boolean IDontKnow = false;
        return IDontKnow;
    }
    
    /**
     * Utilis� par le pathfinding.
     * Retourne tout les les obstacles temporaires/mobiles. (détectés par la balise laser, les capteurs de distance, etc.)
     *
     * @return la liste des obstacles temporaires/mobiles de la table
     */
    public ArrayList<ObstacleProximity> getMobileObstacles()
    {
        return mMobileObstacles;
    }
    
    /**
     * Utilis� par le pathfinding.
     * Retourne tout les les obstacles fixes de la table.
     *
     * @return la liste des obstacles fixes de la table
     */
    public ArrayList<ObstacleCircular> getFixedObstacles()
    {
        return mFixedObstacles;
    }
    
    /**
     * 
     * @return la liste des lignes formant les bords des obstacles
     */
	public ArrayList<Segment> getLines()
	{
		return mLines;
	}
	
	public ArrayList<ObstacleRectangular> getRectangles()
	{
		return mRectangles;
	}
    
    /**
     * Ajoute un obstacle sur la table a la position spécifiée (de type obstacleProximity)
     *
     * @param position position ou ajouter l'obstacle
     */
    public synchronized void addObstacle(final Vec2 position)
    {
    	addObstacle (position,defaultObstacleRadius);
    }

    
    /**
     * Ajoute un obstacle sur la table a la position spécifiée, du rayon specifie (de type obstacleProximity)
     *
     * @param position position ou ajouter l'obstacle
     * @param radius rayon de l'obstacle a ajouter     */
    public synchronized void addObstacle(final Vec2 position, final int radius)
    {
    	//TODO tester si il est utile d'ajouter l'obstacle
    	log.debug("obstacle ajouté en (" + position.x + ", " + position.y + ")", this);
    	mMobileObstacles.add(new ObstacleProximity(position, radius));
    }

    /**
	 * Supprime du gestionnaire tout les obstacles dont la date de péremption est antérieure a la date fournie
     *
     * @param date La date de péremption a partir de laquelle on garde les obstacles.
     */
    public synchronized void removeOutdatedObstacles(long date)
    {
    	for(int i = 0; i < mMobileObstacles.size(); i++)
    		if(mMobileObstacles.get(i).getOutDatedTime() > System.currentTimeMillis())
    			mMobileObstacles.remove(i);
    }

    /**
     * Renvoie true si un obstacle chevauche un disque. (uniquement un obstacle detecte par les capteurs)
     *
     * @param discCenter le centre du disque a vérifier
     * @param radius le rayon du disque
     * @return true, si au moins un obstacle chevauche le disque
     */
    public boolean isDiscObstructed(final Vec2 discCenter, int radius)
    {
    	boolean isDiscObstructed = false;
    	for(int i=0; i<mMobileObstacles.size(); i++)
    	{
    		if ((radius+mMobileObstacles.get(i).radius)*(radius+mMobileObstacles.get(i).radius)>(discCenter.x-mMobileObstacles.get(i).getPosition().x)*(discCenter.x-mMobileObstacles.get(i).getPosition().x)+(discCenter.y-mMobileObstacles.get(i).getPosition().y)*(discCenter.y-mMobileObstacles.get(i).getPosition().y))
    			isDiscObstructed=true;
    	}
    	return isDiscObstructed;
    }   

    /**
     * Change le position d'un robot adverse.
     *
     * @param ennemyID num�ro du robot
     * @param position nouvelle position du robot
     */
    public synchronized void setEnnemyNewLocation(int ennemyID, final Vec2 position)
    {
    	//TODO innutilise
    	//changer la position de l'ennemi demandé
    	//cela sera utilise par la strategie, la methode sera ecrite si besoin
    	mMobileObstacles.get(ennemyID).setPosition(position);
    }
    
    /**
     * Utilis� par le thread de stratégie. (pas implemente : NE PAS UTILISER!!!)
     * renvoie la position du robot ennemi voulu sur la table.
     *
     * @return la position de l'ennemi spécifié
     */
    public Vec2 getEnnemyLocation(int ennemyID)
    {
    	//TODO innutilise
    	//donner la position de l'ennemi demandé
    	//cela sera utilise par la strategie, la methode sera ecrite si besoin
        return  mMobileObstacles.get(ennemyID).position;
    }
    
    
    /**
     * Utilis� pour les tests.
     * Renvois le nombre d'obstacles mobiles actuellement en mémoire
     *
     * @return le nombre d'obstacles mobiles actuellement en mémoire
     */
    public int getMobileObstaclesCount()
    {
        return mMobileObstacles.size();
    }
    
    /**
     * Vérifie si le position spécifié est dans l'obstacle spécifié ou non
     * Attention : l'obstacle doit etre issu des classes ObstacleCircular ou ObstacleRectangular sous peine d'exception
     * Attention : verifie si le point (et non le robot) est dans l'obstacle.
     *
     * @param pos la position a vérifier
     * @param obstacle l'obstacle a considérer
     * @return true, si la position est dans l'obstacle
     */
    public synchronized boolean isPositionInObstacle(Vec2 pos, Obstacle obstacle)
    {
    	if(obstacle instanceof ObstacleCircular)
    	{
    		ObstacleCircular obstacleCircular = (ObstacleCircular)obstacle;
    		return (pos.x-obstacleCircular.position.x)*(pos.x-obstacleCircular.position.x)+(pos.y-obstacleCircular.position.y)*(pos.y-obstacleCircular.position.y)<obstacleCircular.radius*obstacleCircular.radius;
    	}
    	if(obstacle instanceof ObstacleRectangular)
    	{
    		ObstacleRectangular obstacleRectangular = (ObstacleRectangular)obstacle;
	    	return pos.x<(obstacleRectangular.position.x-(obstacleRectangular.sizeX/2)) || pos.x>(obstacleRectangular.position.x+(obstacleRectangular.sizeX/2)) || pos.y<(obstacleRectangular.position.y-(obstacleRectangular.sizeY/2)) || pos.y>(obstacleRectangular.position.y+(obstacleRectangular.sizeY/2));
    	}
    	else
    		throw new IllegalArgumentException();
    }
    
    /**
	 * Vérifie si la position donnée est dégagée ou si elle est dans l'un des obstacles sur la table (tous les obstacles)
     *
     * @param position la position a vérifier
     * @return true, si la position est dans un obstacle
     */
    public synchronized boolean isObstructed(Vec2 position)
    {
    	boolean isObstructed = false;
    	for(int i=0; i<mMobileObstacles.size(); i++)
    		isObstructed=isPositionInObstacle(position, mMobileObstacles.get(i));
    	for(int i=0; i<mFixedObstacles.size(); i++)
    		isObstructed=isPositionInObstacle(position, mFixedObstacles.get(i));
    	for(int i=0; i<mRectangles.size(); i++)
    		isObstructed=isPositionInObstacle(position, mRectangles.get(i));
        return isObstructed;
    }
}
