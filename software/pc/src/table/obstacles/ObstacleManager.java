package table.obstacles;

import java.util.ArrayList;
import java.util.EnumSet;

import com.sun.org.apache.xpath.internal.axes.OneStepIterator;

import enums.ObstacleGroups;
import pathDingDing.PathDingDing;
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
    private Log log;

	/** endroit ou lire la configuration du robot */
    @SuppressWarnings("unused")
	private Config config;

    /** Ensemble des obstacles mobiles/temporaires se trouvant sur la table */
    private ArrayList<ObstacleProximity> mMobileObstacles;
    private ArrayList<ObstacleCircular> mFixedObstacles;
    
    //les bords de la table auxquels on ajoute le rayon du robot. Utilisé par le pathfinding.
    private ArrayList<Segment> mLines;
    //les obstacles rectangulaires de la table
	private ArrayList<ObstacleRectangular> mRectangles;

	private int defaultObstacleRadius;
	//le rayon de notre robot
	private int mRobotRadius;
	
	// TODO virer : juste du debugg / interface graphique
	private int radiusDetectionDisc=0;
	private Vec2 positionDetectionDisc=new Vec2(0,0);
		
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
		
        mRobotRadius = Integer.parseInt(config.getProperty("rayon_robot"));
        defaultObstacleRadius = Integer.parseInt(config.getProperty("rayon_robot_adverse"));
        
        //par defaut
        //mEnnemyRobot1 = new ObstacleCircular(new Vec2(0, 0), 200 + robotRadius);
      	//mEnnemyRobot2 = new ObstacleCircular(new Vec2(0, 0), 200 + robotRadius);
		
        // les numeros sont ceux de la doc sur /pc/config/obstacles
		
		//obstacles 1, 2, 3
      	mLines.add(new Segment(new Vec2(-1500, 778 - mRobotRadius), new Vec2(-1100 + mRobotRadius, 778 - mRobotRadius)));
      	mLines.add(new Segment(new Vec2(-1100 + mRobotRadius, 778 - mRobotRadius), new Vec2(-1100 + mRobotRadius, 1222 + mRobotRadius)));
   		mLines.add(new Segment(new Vec2(-1100 + mRobotRadius, 1222 + mRobotRadius), new Vec2(-1500, 1222 + mRobotRadius)));
     		
      	//obstacles 10, 11, 12
     	mLines.add(new Segment(new Vec2(1500, 1222 + mRobotRadius), new Vec2(1100 - mRobotRadius, 1222 + mRobotRadius)));
   		mLines.add(new Segment(new Vec2(1100 - mRobotRadius, 1222 + mRobotRadius), new Vec2(1100 - mRobotRadius, 778 - mRobotRadius)));
      	mLines.add(new Segment(new Vec2(1100 - mRobotRadius, 778 - mRobotRadius), new Vec2(1500, 778 - mRobotRadius)));
      		
      	//obstacle 6
      	mLines.add(new Segment(new Vec2(533 + mRobotRadius, 2000), new Vec2(533 + mRobotRadius, 1420 - mRobotRadius)));
      	mLines.add(new Segment(new Vec2(533 + mRobotRadius, 1420 - mRobotRadius), new Vec2(-533 - mRobotRadius, 1420 - mRobotRadius)));
      	mLines.add(new Segment(new Vec2(-533 - mRobotRadius, 1420 - mRobotRadius), new Vec2(-533 - mRobotRadius, 2000)));
      		
      		
      	//obstacle 7
      	mLines.add(new Segment(new Vec2(300 + mRobotRadius, 0), new Vec2(300 + mRobotRadius, 100 + mRobotRadius)));
      	mLines.add(new Segment(new Vec2(300 + mRobotRadius, 100 + mRobotRadius), new Vec2(-300 - mRobotRadius, 100 + mRobotRadius)));
      	mLines.add(new Segment(new Vec2(-300 - mRobotRadius, 100 + mRobotRadius), new Vec2(-300 - mRobotRadius, 0)));
      		
      	//bords de la table
      	mLines.add(new Segment(new Vec2(-1500 + mRobotRadius, 0 + mRobotRadius), new Vec2(1500 - mRobotRadius, 0 + mRobotRadius)));
      	mLines.add(new Segment(new Vec2(1500 - mRobotRadius, 0 + mRobotRadius), new Vec2(1500 - mRobotRadius, 1930 - mRobotRadius)));
      	mLines.add(new Segment(new Vec2(1500 - mRobotRadius, 1930 - mRobotRadius), new Vec2(-1500 + mRobotRadius, 1930 - mRobotRadius)));
      	mLines.add(new Segment(new Vec2(-1500 + mRobotRadius, 1930 - mRobotRadius), new Vec2(-1500 + mRobotRadius, 0 + mRobotRadius)));
      	
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
	    mFixedObstacles.add(new ObstacleCircular(new Vec2(200, 600), 30, ObstacleGroups.GREEN_PLOT_0));
	    mFixedObstacles.add(new ObstacleCircular(new Vec2(400, 250), 30, ObstacleGroups.GREEN_PLOT_1));
	    mFixedObstacles.add(new ObstacleCircular(new Vec2(630, 645), 30, ObstacleGroups.GREEN_PLOT_2));
	    mFixedObstacles.add(new ObstacleCircular(new Vec2(1410, 150), 30, ObstacleGroups.GREEN_PLOT_3));
	    mFixedObstacles.add(new ObstacleCircular(new Vec2(1410, 250), 30, ObstacleGroups.GREEN_PLOT_4));
	    mFixedObstacles.add(new ObstacleCircular(new Vec2(650, 1800), 30, ObstacleGroups.GREEN_PLOT_5));
	    mFixedObstacles.add(new ObstacleCircular(new Vec2(650, 1900), 30, ObstacleGroups.GREEN_PLOT_6));
	    mFixedObstacles.add(new ObstacleCircular(new Vec2(1410, 1800), 30, ObstacleGroups.GREEN_PLOT_7));
	    
	    //obstacles plots jaunes
	    mFixedObstacles.add(new ObstacleCircular(new Vec2(-200, 600), 30, ObstacleGroups.YELLOW_PLOT_0));
	    mFixedObstacles.add(new ObstacleCircular(new Vec2(-400, 250), 30, ObstacleGroups.YELLOW_PLOT_1));
	    mFixedObstacles.add(new ObstacleCircular(new Vec2(-630, 645), 30, ObstacleGroups.YELLOW_PLOT_2));
	    mFixedObstacles.add(new ObstacleCircular(new Vec2(-1410, 150), 30, ObstacleGroups.YELLOW_PLOT_3));
	    mFixedObstacles.add(new ObstacleCircular(new Vec2(-1410, 250), 30, ObstacleGroups.YELLOW_PLOT_4));
	    mFixedObstacles.add(new ObstacleCircular(new Vec2(-650, 1800), 30, ObstacleGroups.YELLOW_PLOT_5));
	    mFixedObstacles.add(new ObstacleCircular(new Vec2(-650, 1900), 30, ObstacleGroups.YELLOW_PLOT_6));
	    mFixedObstacles.add(new ObstacleCircular(new Vec2(-1410, 1800), 30, ObstacleGroups.YELLOW_PLOT_7));

	    // gobelets
	    mFixedObstacles.add(new ObstacleCircular(new Vec2(1250, 250), 48, ObstacleGroups.GOBLET_0));
	    mFixedObstacles.add(new ObstacleCircular(new Vec2(590, 1170), 48, ObstacleGroups.GOBLET_1));
	    mFixedObstacles.add(new ObstacleCircular(new Vec2(0, 350), 48, ObstacleGroups.GOBLET_2));
	    mFixedObstacles.add(new ObstacleCircular(new Vec2(-590, 1170), 48, ObstacleGroups.GOBLET_3));
	    mFixedObstacles.add(new ObstacleCircular(new Vec2(-1250, 250), 48, ObstacleGroups.GOBLET_4));
	    
      	//la zone ennemie
	    if(config.getProperty("couleur").equals("jaune"))
	    	mFixedObstacles.add(new ObstacleCircular(new Vec2(1100, 1000), 200, ObstacleGroups.ENNEMY_ZONE));
	    else
	    	mFixedObstacles.add(new ObstacleCircular(new Vec2(-1100, 1000), 200, ObstacleGroups.ENNEMY_ZONE));
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
     * @return la liste des lignes formant les bords des obstacles sous forme de segments
     */
	public ArrayList<Segment> getLines()
	{
		return mLines;
	}
	
	/**
	 * 
	 * @return la liste des rectangles formant les obstacles rectangulaires
	 */
	public ArrayList<ObstacleRectangular> getRectangles()
	{
		return mRectangles;
	}
	
	/**
	 * 
	 * @return le rayon de notre robot
	 */
	public int getRobotRadius()
	{
		return mRobotRadius;
	}
    
    /**
     * Ajoute un obstacle sur la table a la position spécifiée (de type obstacleProximity)
     *
     * @param position position ou ajouter l'obstacle
     */
    public synchronized void addObstacle(final Vec2 position)
    {
    	addObstacle(position,defaultObstacleRadius);
    }

    
    /**
     * Ajoute un obstacle sur la table a la position spécifiée, du rayon specifie (de type obstacleProximity)
     *
     * @param position position ou ajouter l'obstacle
     * @param radius rayon de l'obstacle a ajouter    
      */
    public synchronized void addObstacle(final Vec2 position, final int radius)
    {
    	//si la position est dans la table on continue les tests
    	if (position.x>-1500-radius && position.x<1500+radius && position.y>0-radius && position.y<2000+radius)
    	{
    		/*on ne test pas si la position est dans un obstcle deja existant 
    		 *on ne detecte pas les plots ni les goblets (et si on les detectes on prefere ne pas prendre le risque et on les evites)
    		 * et si on detecte une deuxieme fois l'ennemi on rajoute un obstacle sur lui
    		 */
    		mMobileObstacles.add(new ObstacleProximity(position, radius, ObstacleGroups.ENNEMY_ROBOTS));
    		log.debug("Ennemi ajouté en "+position.x+";"+position.y, this);
    	}
    	else
    	{
    		log.debug("Ennemi hors de la table", this);
		}
    }

    /**
	 * Supprime du gestionnaire tout les obstacles dont la date de péremption est antérieure a la date fournie
     *
     * @param date La date de péremption a partir de laquelle on garde les obstacles.
     */
    public synchronized void removeOutdatedObstacles()
    {
    	for(int i = 0; i < mMobileObstacles.size(); i++)
    		if(mMobileObstacles.get(i).getOutDatedTime() < System.currentTimeMillis())
    		{
    			mMobileObstacles.remove(i--);
    		}
    }

    /**
     * Renvoie true si un obstacle chevauche un disque. (uniquement un obstacle detecte par les capteurs)
     *
     * @param discCenter le centre du disque a vérifier
     * @param radius le rayon du disque
     * @return true, si au moins un obstacle chevauche le disque
     */
    public synchronized boolean isDiscObstructed(final Vec2 discCenter, int radius)
    {
    	boolean isDiscObstructed = false;
    	radiusDetectionDisc=radius;
    	positionDetectionDisc=discCenter;
    	
    	for(int i=0; i<mMobileObstacles.size(); i++)
    	{
    		if ((radius+mMobileObstacles.get(i).radius)*(radius+mMobileObstacles.get(i).radius)
    			 > (discCenter.x-mMobileObstacles.get(i).getPosition().x)*(discCenter.x-mMobileObstacles.get(i).getPosition().x)
    			 + (discCenter.y-mMobileObstacles.get(i).getPosition().y)*(discCenter.y-mMobileObstacles.get(i).getPosition().y))
    		{
    			log.debug("Disque obstructed avec l'obstacle "+mMobileObstacles.get(i).getPosition()+"de rayon"+mMobileObstacles.get(i).radius, this);
    			log.debug("Disque en "+discCenter+"de rayon"+radius, this);
    			isDiscObstructed=true;
    			
    		}
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
    
    /**
     * 
     * @param position
     * @return les groupes d'obstacles dans lesquels est le point
     */
    //TODO : trouver un meilleur nom?
    public EnumSet<ObstacleGroups> obstacleGroupsInPosition(Vec2 position)
    {
    	EnumSet<ObstacleGroups> obstacleGroups = EnumSet.noneOf(ObstacleGroups.class);
    	for(int i = 0; i < mMobileObstacles.size(); i++)
			if(isPositionInObstacle(position, mMobileObstacles.get(i)))
			{
				obstacleGroups.add(mMobileObstacles.get(i).getObstacleGroup());
				break;
			}
		for(int i = 0; i < mFixedObstacles.size(); i++)
			if(isPositionInObstacle(position, mFixedObstacles.get(i)))
				obstacleGroups.add(mFixedObstacles.get(i).getObstacleGroup());
    	return obstacleGroups;
    }
    
    /**
     *  On enleve les obstacles presents sur la table virtuelle mais non detectés
     *  @return true si on a enlevé un obstacle, false sinon
     */
    public synchronized boolean removeNonDetectedObstacles(Vec2 position, double orientation, double detectionRadius, double detectionAngle)
    {
		boolean obstacleDeleted=false;

    	//parcours des obstacles
    	for(int i = 0; i < mMobileObstacles.size(); i++)
    	{
    		Vec2 positionEnnemy = mMobileObstacles.get(i).position;
    		int ennemyRay = mMobileObstacles.get(i).radius;
    		// On verifie que l'ennemi est dans le cercle de detection actuel
    		if((positionEnnemy.x - position.x)*(positionEnnemy.x - position.x)
    		 + (positionEnnemy.y - position.y)*(positionEnnemy.y - position.y)
    		 < (detectionRadius+ennemyRay)*(detectionRadius+ennemyRay))
    		{
    			double ennemyAngle = Math.atan2(positionEnnemy.x - position.x, positionEnnemy.y - position.y);
    			
    			// si le centre de l'obstacle est dans le cone 
    			// ou 
    			// si on intersecte avec le coté gauche 
    			// ou
    			// si on interesecte avec le coté droit
    			Segment coteGaucheCone = new Segment(position, 
						new Vec2( position.x + (int)(detectionRadius*Math.cos(orientation + detectionAngle/2)), 
								  position.y + (int)(detectionRadius*Math.sin(orientation + detectionAngle/2)) ) );
    			Segment coteDroitCone = new Segment(position, 
						new Vec2( position.x + (int)(detectionRadius*Math.cos(orientation - detectionAngle/2)), 
								  position.y + (int)(detectionRadius*Math.sin(orientation - detectionAngle/2)) ) );
    			
    			if(ennemyAngle < (orientation + detectionAngle/2)
    		    && ennemyAngle > (orientation - detectionAngle/2)
    		    || ( ( PathDingDing.intersects( coteGaucheCone , 
    		    						   new Circle(positionEnnemy, ennemyRay)) )
    		    || ( PathDingDing.intersects(	coteDroitCone, 
    		    						   new Circle(positionEnnemy, ennemyRay))) )  )
    			{
    				mMobileObstacles.remove(i--);
    				obstacleDeleted=true;
    				log.debug("Ennemi en "+positionEnnemy+" enlevé !", this);
    				

    				// TODO enlever, Pourle debug 
    				if(ennemyAngle < (orientation + detectionAngle/2)&& ennemyAngle > (orientation - detectionAngle/2) ) 
        				log.debug("Cause : dans l'angle du cone", this);
	    			if(PathDingDing.intersects(coteGaucheCone ,
    		    						   new Circle(positionEnnemy, ennemyRay)) )
	    			{
        				log.debug("Cause : intersectionne avec le coté gauche du cone", this);
	    			}
	    			
    		    	if( PathDingDing.intersects( coteDroitCone, 
    		    						   new Circle(positionEnnemy, ennemyRay)))
    		    	{
        				log.debug("Cause : intersectionne avec le coté droit du cone", this);   
    		    	}
    			}
    		}
    	}
    	return obstacleDeleted;
    }
    
    
    /**
     * Debug / interface graphique
     */
    public int getDiscRadius()
    {
    	return radiusDetectionDisc;
    }
    public Vec2 getDiscPosition()
    {
    	return positionDetectionDisc;
    }
    
    /**
     * supprime tous les obstacles dont le groupe est celui spécifié
     * 
     * @param obstacleGroupToDelete
     */
    public void removeFixedObstacle(ObstacleGroups obstacleGroupToDelete)
    {
    	for(int i=0; i<mFixedObstacles.size(); i++)
    	{
    		if( mFixedObstacles.get(i).getObstacleGroup() == obstacleGroupToDelete )
    		{
				if(mFixedObstacles.remove(mFixedObstacles.get(i)))
					return;
				else 
					log.debug("Impossible d'enlever l'obstacle "+obstacleGroupToDelete, this);
    		}
    	}
    }
    
    /**
     * supprime les obstacles fixes dans le disque
     * 
     * @param position
     * @param radius
     */
    public void removeFixedObstaclesInDisc(Vec2 position, int radius)
    {
    	for(int i=0; i<mFixedObstacles.size(); i++)
    		if((position.x-mFixedObstacles.get(i).getPosition().x)*(position.x-mFixedObstacles.get(i).getPosition().x)
    		 + (position.y-mFixedObstacles.get(i).getPosition().y)*(position.y-mFixedObstacles.get(i).getPosition().y)
    		 <= radius*radius)
    			mFixedObstacles.remove(mFixedObstacles.get(i));
    }
    
    public void printObstacleFixedList()
    {
    	for(int i=0; i<mFixedObstacles.size(); i++)
    		mFixedObstacles.get(i).printObstacleMemory();
    }
}
