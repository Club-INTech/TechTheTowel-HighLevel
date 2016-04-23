package table.obstacles;

import exceptions.ConfigPropertyNotFoundException;
import smartMath.Circle;
import smartMath.Geometry;
import smartMath.Segment;
import smartMath.Vec2;
import utils.Config;
import utils.Log;

import java.util.ArrayList;

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
	private Config config;

    /** Ensemble des obstacles mobiles/temporaires se trouvant sur la table */
    private ArrayList<ObstacleProximity> mMobileObstacles;
    private ArrayList<ObstacleCircular> mFixedObstacles;
    
    /**
     * Ensemble des obstacles mobiles/temporaires a tester pour les placer sur la table
     */
	private ArrayList<ObstacleProximity> mUntestedMobileObstacles;

    
    //les bords de la table auxquels on ajoute le rayon du robot. Utilisé par le pathfinding.
    private ArrayList<Segment> mLines;
    //les obstacles rectangulaires de la table
	private ArrayList<ObstacleRectangular> mRectangles;

	private int defaultObstacleRadius;
	//le rayon de notre robot
	public int mRobotRadius;
	
	// TODO virer : juste du debugg / interface graphique
	private int radiusDetectionDisc=0;
	private Vec2 positionDetectionDisc=new Vec2(0,0);

	/**	le temps donné aux obstacles pour qu'ils soit vérifiés */
	private final int timeToTestObstacle = 1000;

	/** Temps de vie d'un robot ennemi */
	private final int defaultLifetime = 2000;

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
		
		mUntestedMobileObstacles= new ArrayList<ObstacleProximity>();


		
		updateConfig();
       
        //par defaut
        //mEnnemyRobot1 = new ObstacleCircular(new Vec2(0, 0), 200 + robotRadius);
      	//mEnnemyRobot2 = new ObstacleCircular(new Vec2(0, 0), 200 + robotRadius);
		
	
      		
      	//bords de la table
      	mLines.add(new Segment(new Vec2(-1500 + mRobotRadius, 0 + mRobotRadius), new Vec2(1500 - mRobotRadius, 0 + mRobotRadius)));
      	mLines.add(new Segment(new Vec2(1500 - mRobotRadius, 0 + mRobotRadius), new Vec2(1500 - mRobotRadius, 2000 - mRobotRadius)));
      	mLines.add(new Segment(new Vec2(1500 - mRobotRadius, 2000 - mRobotRadius), new Vec2(-1500 + mRobotRadius, 2000 - mRobotRadius)));
      	mLines.add(new Segment(new Vec2(-1500 + mRobotRadius, 2000 - mRobotRadius), new Vec2(-1500 + mRobotRadius, 0 + mRobotRadius)));
      	
      	//Les différents obstacles fixés sur la table
      	//planches au sud
      	mRectangles.add(new ObstacleRectangular(new Vec2(711, 1900), 22 + 2*mRobotRadius, 200 + 2*mRobotRadius));
      	mRectangles.add(new ObstacleRectangular(new Vec2(-711, 1900), 200 + 2*mRobotRadius, 200 + 2*mRobotRadius));
      	
      	//Vitre centrale

      	mRectangles.add(new ObstacleRectangular(new Vec2(0, 950), 48 + 2*mRobotRadius, 600 + 2*mRobotRadius));
      	
      	//planches à côté de la vitre
      	mRectangles.add(new ObstacleRectangular(new Vec2(0, 1239), 1200 + 2*mRobotRadius, 22 + 2*mRobotRadius));
      	
      	//Rochers
      	mFixedObstacles.add(new ObstacleCircular(new Vec2(1500, 0), 250 + mRobotRadius));
      	mFixedObstacles.add(new ObstacleCircular(new Vec2(-1500, 0), 250 + mRobotRadius));

		//Packs de sable (merci Sylvaing19)
		mRectangles.add(new ObstacleRectangular(new Vec2(0, 1913), 522 + 2*mRobotRadius , 174 + 2*mRobotRadius));
		mRectangles.add(new ObstacleRectangular(new Vec2(-620, 1942), 116 + 2*mRobotRadius, 116 + 2*mRobotRadius));
		mRectangles.add(new ObstacleRectangular(new Vec2(620, 1942), 116 + 2*mRobotRadius, 116 + 2*mRobotRadius));
		//mRectangles.add(new ObstacleRectangular(new Vec2(850, 1100), 116 + 2*mRobotRadius, 116 + 2*mRobotRadius));

		//Portes
		mRectangles.add(new ObstacleRectangular(new Vec2(900,1970), 100 + 2*mRobotRadius , 60 + 2*mRobotRadius));
		mRectangles.add(new ObstacleRectangular(new Vec2(1200,1970), 100 + 2*mRobotRadius , 60 + 2*mRobotRadius));
		mRectangles.add(new ObstacleRectangular(new Vec2(-900,1970), 100 + 2*mRobotRadius , 60 + 2*mRobotRadius));
		mRectangles.add(new ObstacleRectangular(new Vec2(-1200,1970), 100 + 2*mRobotRadius , 60 + 2*mRobotRadius));

		//Tapis Adverse
		mRectangles.add(new ObstacleRectangular(new Vec2(-1350,850), 300 + 2*mRobotRadius, 500 + 2*mRobotRadius));

		// Points d'accroche du filet qui empiètent sur la mer
		mRectangles.add(new ObstacleRectangular(new Vec2(561,11),22 + 2*mRobotRadius, 22 + 2*mRobotRadius));
		mRectangles.add(new ObstacleRectangular(new Vec2(-561,11),22 + 2*mRobotRadius, 22 + 2*mRobotRadius));
		
	}


    /**
     * Rend le gestionnaire d'obstacle fourni en argument explicite égal a ce gestionnaire.
     *
     * @param other les gestionnaire a modifier
     */
    public void copy(ObstacleManager other)
    {
    	//TODO innutilise
    }

    /**
     *  Cette instance est elle dans le même état que celle fournie en argument explicite ?
     *
     * @param other l'autre instance a comparer
     * @return true, si les deux instances sont dans le meme etat
     */
    public boolean equals(ObstacleManager other)
    {
    	//TODO inutilise
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
    	addObstacle(position,defaultObstacleRadius, defaultLifetime);
    }

    
    /**
     * Ajoute un obstacle sur la table a la position spécifiée, du rayon specifie (de type obstacleProximity)
     *
     * @param position position ou ajouter l'obstacle
     * @param radius rayon de l'obstacle a ajouter    
     * @param lifetime durée de vie (en ms) de l'obstace a ajouter
     * TODO A réadapter à l'année en cours
      */
    public synchronized void addObstacle(final Vec2 position, final int radius, final int lifetime)
    {
    	//vérification que l'on ne détecte pas un obstacle "normal"
    	if (position.x>-1500+mRobotRadius+100 && position.x<1500-mRobotRadius-100 && position.y>mRobotRadius+100 && position.y<2000-mRobotRadius-100 //hors de la table
                && !( Geometry.isBetween(position.x, -100, 100) && Geometry.isBetween(position.y, 800, 1450)) //C'est la vitre
                && !( Geometry.isBetween(position.x, -800, 800) && Geometry.isBetween(position.y, 1500, 2000)) //château de sable
				&& !( Geometry.isBetween(position.x, 700, 1000) && Geometry.isBetween(position.y, 950, 1250)) //château de sable tapis
				&& !( Geometry.isBetween(position.x, -1000, -700) && Geometry.isBetween(position.y, 950, 1250)) //château de sable tapis adv
				)
    	{
    		boolean isThereAnObstacleIntersecting=false;
    		for (int i = 0; i<mUntestedMobileObstacles.size(); i++)
    		{
    			ObstacleProximity obstacle = mUntestedMobileObstacles.get(i);
    			
    			//si l'obstacle est deja dans la liste des obstacles non-testés on l'ajoute dans la liste des obstacles
	    		if(obstacle.position.distance(position)<(obstacle.radius+radius)/2)
	    		{
    				mUntestedMobileObstacles.get(i).numberOfTimeDetected++;
    				position.copy(mUntestedMobileObstacles.get(i).position);
    				mUntestedMobileObstacles.get(i).radius=radius;
    				mUntestedMobileObstacles.get(i).setLifeTime(lifetime);
    				
    				// si on l'a deja vu plin de fois
    				if(mUntestedMobileObstacles.get(i).numberOfTimeDetected >= mUntestedMobileObstacles.get(i).getMaxNumberOfTimeDetected())
    					mUntestedMobileObstacles.get(i).numberOfTimeDetected = mUntestedMobileObstacles.get(i).getMaxNumberOfTimeDetected();

	    			// si on valide sa vision 
	    			if(mUntestedMobileObstacles.get(i).numberOfTimeDetected >= mUntestedMobileObstacles.get(i).getThresholdConfirmedOrUnconfirmed())
	    			{
	    				isThereAnObstacleIntersecting=true;
	    				mUntestedMobileObstacles.get(i).setLifeTime(lifetime);
	    				
	    				mMobileObstacles.add(mUntestedMobileObstacles.get(i));
	    				mUntestedMobileObstacles.remove(i);
	    			}
	    		}
    		}
    		for(int i = 0; i<mMobileObstacles.size(); i++)
    		{
    			ObstacleProximity obstacle = mMobileObstacles.get(i);
    			if(obstacle.position.distance(position)<obstacle.radius+radius)
    			{
    				isThereAnObstacleIntersecting=true;
    				
    				mMobileObstacles.get(i).numberOfTimeDetected++;
    				position.copy(mMobileObstacles.get(i).position);
    				mMobileObstacles.get(i).radius=radius;
    				mMobileObstacles.get(i).setLifeTime(lifetime);
    				
    				// si on l'a deja vu plin de fois
    				if(mMobileObstacles.get(i).numberOfTimeDetected >= mMobileObstacles.get(i).getMaxNumberOfTimeDetected())
    					mMobileObstacles.get(i).numberOfTimeDetected = mMobileObstacles.get(i).getMaxNumberOfTimeDetected();
    			}
    		}
    		if (!isThereAnObstacleIntersecting)
    			mUntestedMobileObstacles.add(new ObstacleProximity(position, radius, timeToTestObstacle));

    			
    		/*on ne test pas si la position est dans un obstacle deja existant 
    		 *on ne detecte pas les plots ni les gobelets (et si on les detectes on prefere ne pas prendre le risque et on les evites)
    		 * et si on detecte une deuxieme fois l'ennemi on rajoute un obstacle sur lui
    		 */
    	}
    	else
    	{
    		//log.debug("Obstacle hors de la table");
		}
    }

	/**
	 * Ajoute un obstacle rectangulaire
	 * @param obs le ObstacleRectangular en question
     */
	public synchronized void addObstacle(ObstacleRectangular obs)
	{
		mRectangles.add(obs);
	}

	/**
	 * Ajoute un obstacle circulaire
	 * @param obs le ObstacleCircular en question
     */
	public synchronized void addObstacle(ObstacleCircular obs)
	{
		mFixedObstacles.add(obs);
	}

	/**
	 * Supprime la première occurence de cet obstacle
	 * @param obs l'obstacle
     */
	public synchronized void removeObstacle(ObstacleCircular obs)
	{
		mFixedObstacles.remove(obs);
	}

	/**
	 * Supprime la première occurence de cet obstacle
	 * @param obs l'obstacle
     */
	public synchronized void removeObstacle(ObstacleRectangular obs)
	{
		mRectangles.remove(obs);
	}

    /**
	 * Supprime du gestionnaire tout les obstacles dont la date de péremption est antérieure a la date fournie
     *
     */
    public synchronized void removeOutdatedObstacles()
    {
    	
    	// enlève les obstacles confirmés s'ils sont périmés
    	for(int i = 0; i < mMobileObstacles.size(); i++)
    		if(mMobileObstacles.get(i).getOutDatedTime() < System.currentTimeMillis())
    		{
    			mMobileObstacles.remove(i--);
    		}
    	
    	// enlève les obstacles en attente s'ils sont périmés
    	for(int i = 0; i < mUntestedMobileObstacles.size(); i++)
    		if(mUntestedMobileObstacles.get(i).getOutDatedTime() < System.currentTimeMillis())
    		{
    			mUntestedMobileObstacles.remove(i--);
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
    			log.debug("Disque obstructed avec l'obstacle "+mMobileObstacles.get(i).getPosition()+"de rayon"+mMobileObstacles.get(i).radius);
    			log.debug("Disque en "+discCenter+" de rayon "+radius);
    			isDiscObstructed=true;
    			
    		}
    	}
    	return isDiscObstructed;
    }  

    /**
     * retourne la distance à l'ennemi le plus proche (en mm)
     * Les ennemis ne sont pris en compte que si ils sont dans la direstion donnée, a 90° près
     * si l'ennemi le plus proche est tangent à notre robot, ou plus proche, on retourne 0
     * @param position la position a laquelle on doit mesurer la proximité des ennemis
     * @param direction direction selon laquelle on doit prendre en compte les ennemis
     * @return la distance à l'ennemi le plus proche (>= 0)
     */
    public synchronized int distanceToClosestEnemy(Vec2 position, Vec2 direction)
    {
    	try
    	{
	    	//si aucun ennemi n'est détecté, on suppose que l'ennemi le plus proche est à 1m)
	    	
	    	int squaredDistanceToClosestEnemy = 10000000;
	    	
	    	
	    	int squaredDistanceToEnemyUntested=10000000;
	    	int squaredDistanceToEnemyTested=10000000 ;
	    	
	    	ObstacleCircular closestEnnemy = null;
	
	     	if(mMobileObstacles.size() == 0 && mUntestedMobileObstacles.size()==0)
	    		return 1000;
	     	
	     	
	     	//trouve l'ennemi le plus proche parmis les obstacles confirmés
	    	for(int i=0; i<mMobileObstacles.size(); i++)
	    	{
	    		Vec2 ennemyRelativeCoords = new Vec2((mMobileObstacles.get(i).position.x - position.x), 
	    											  mMobileObstacles.get(i).position.y - position.y);
	    		if(direction.dot(ennemyRelativeCoords) > 0)
	    		{
		    		squaredDistanceToEnemyTested = ennemyRelativeCoords.squaredLength(); 
		    		if(squaredDistanceToEnemyTested < squaredDistanceToClosestEnemy)
		    		{
		    			squaredDistanceToClosestEnemy = squaredDistanceToEnemyTested;
		    			closestEnnemy = mMobileObstacles.get(i);
		    		}
	    		}
	    	}
	      	
	     	//trouve l'ennemi non confirmé le plus proche parmis les obstacles 
	    	// (et remplace la distance a l'ennemi le plus proche d'un ennemi confirmé par une distance a un ennemi non confirmé s'il est plus proche)
	    	for(int i=0; i<mUntestedMobileObstacles.size(); i++)
	    	{
	    		Vec2 ennemyRelativeCoords = new Vec2((mUntestedMobileObstacles.get(i).position.x - position.x), 
	    											  mUntestedMobileObstacles.get(i).position.y - position.y);
	    		if(direction.dot(ennemyRelativeCoords) > 0)
	    		{
		    		squaredDistanceToEnemyUntested = ennemyRelativeCoords.squaredLength(); 
		    		if(squaredDistanceToEnemyUntested < squaredDistanceToClosestEnemy)
		    		{
		    			squaredDistanceToClosestEnemy = squaredDistanceToEnemyUntested;
		    			closestEnnemy = mUntestedMobileObstacles.get(i);
		    		}
	    		}
	    	}
	    	
	    	if(squaredDistanceToClosestEnemy <= 0)
	    		return 0;

	    	if(closestEnnemy != null)
	    	{
	    		//log.debug("Position de l'ennemi le plus proche, non testé, d'après distanceToClosestEnnemy: "+mUntestedMobileObstacles.get(indexOfClosestEnnemy).getPosition(), this);
		    	return (int)Math.sqrt((double)squaredDistanceToClosestEnemy) - mRobotRadius /*- closestEnnemy.radius*/;
	    	}
	    	else
	    		return 10000;
    	}
    	catch(IndexOutOfBoundsException e)
    	{
    		log.critical("Ah bah oui, out of bound");
    		throw e;
    	}
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
     * @param ennemyID l'ennemi dont on veut la position
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
     *  On enleve les obstacles presents sur la table virtuelle mais non detectés
     * @param position 
     * @param orientation 
     * @param detectionRadius 
     * @param detectionAngle 
     *  @return true si on a enlevé un obstacle, false sinon
     */
    public synchronized boolean removeNonDetectedObstacles(Vec2 position, double orientation, double detectionRadius, double detectionAngle)
    {
		boolean obstacleDeleted=false;
		//check non testés ;--;et si <=0 remove
		// check testés ; -- ; et si <maxnon goto nonteste  remove de testés
		
		
    	//parcours des obstacles
		for(int i = 0; i < mUntestedMobileObstacles.size(); i++)
    	{
    		Vec2 positionEnnemy = mUntestedMobileObstacles.get(i).position;
    		int ennemyRay = mUntestedMobileObstacles.get(i).radius;
    		// On verifie que l'ennemi est dans le cercle de detection actuel
    		if((positionEnnemy.distance(position) < (detectionRadius+ennemyRay)*(detectionRadius+ennemyRay)))
    		{
    			if(isEnnemyInCone(positionEnnemy, position, detectionRadius, orientation,  detectionAngle, ennemyRay) )
    			{
    				mUntestedMobileObstacles.get(i).numberOfTimeDetected--;
    			
    				if(mUntestedMobileObstacles.get(i).numberOfTimeDetected <= 0)
    				{
    					mUntestedMobileObstacles.remove(i--);
	    				obstacleDeleted=true;
	    				log.debug("Ennemi untested en "+positionEnnemy+" enlevé !");
    				}
    			}
    		}
    	}
    	for(int i = 0; i < mMobileObstacles.size(); i++)
    	{
    		Vec2 positionEnnemy = mMobileObstacles.get(i).position;
    		int ennemyRay = mMobileObstacles.get(i).radius;
    		// On verifie que l'ennemi est dans le cercle de detection actuel
    		if((positionEnnemy.distance(position) < (detectionRadius+ennemyRay)*(detectionRadius+ennemyRay)))
    		{
    			if(isEnnemyInCone(positionEnnemy, position, detectionRadius, orientation,  detectionAngle, ennemyRay) )
    			{
    				mMobileObstacles.get(i).numberOfTimeDetected--;
    			
    				if(mMobileObstacles.get(i).numberOfTimeDetected < mMobileObstacles.get(i).getThresholdConfirmedOrUnconfirmed())
    				{
    					mMobileObstacles.get(i).setLifeTime(5000);
        				mUntestedMobileObstacles.add(mMobileObstacles.get(i));
	    				mMobileObstacles.remove(i--);
	    				
	    				obstacleDeleted=true;
	    				log.debug("Ennemi en "+positionEnnemy+" enlevé !");
    				}
    			}
    		}
    	}
    	return obstacleDeleted;
    }
    
    
    public boolean isEnnemyInCone(Vec2 positionEnnemy, Vec2 position, double detectionRadius, double orientation, double detectionAngle, int ennemyRay) 
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
		
		return (ennemyAngle < (orientation + detectionAngle/2)
	    && ennemyAngle > (orientation - detectionAngle/2)
	    || ( ( Geometry.intersects( coteGaucheCone , 
	    						   new Circle(positionEnnemy, ennemyRay)) )
	    || ( Geometry.intersects(	coteDroitCone, 
	    						   new Circle(positionEnnemy, ennemyRay))) )  );
	}
    
    /**
     * Debug / interface graphique
     * @return 
     */
    @SuppressWarnings("javadoc")
	public int getDiscRadius()
    {
    	return radiusDetectionDisc;
    }
    public Vec2 getDiscPosition()
    {
    	return positionDetectionDisc;
    }
    
    /**
     *  On enleve les obstacles qui sont en confrontation avec nous :
     *  Cela evite de se retrouver dans un obstacle
     * @param position 
     */
    public void removeObstacleInUs(Vec2 position)
    {
    	for(int i=0; i<mMobileObstacles.size(); i++)
    	{ 
    		if( (   (position.x-mMobileObstacles.get(i).getPosition().x)*(position.x-mMobileObstacles.get(i).getPosition().x)
    	    	+   (position.y-mMobileObstacles.get(i).getPosition().y)*(position.y-mMobileObstacles.get(i).getPosition().y) ) 
    	    	<=( (mRobotRadius+mMobileObstacles.get(i).radius)*(mRobotRadius+mMobileObstacles.get(i).radius)) ) 	    	
    			mMobileObstacles.remove(mMobileObstacles.get(i));
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
    		 <= mRobotRadius*mRobotRadius)
    			mFixedObstacles.remove(mFixedObstacles.get(i));
    }
    
    public void printObstacleFixedList()
    {
    	for(int i=0; i<mFixedObstacles.size(); i++)
    		mFixedObstacles.get(i).printObstacleMemory();
    }
    
    public void updateConfig()
	{
		try 
		{
			mRobotRadius = Integer.parseInt(config.getProperty("rayon_robot"));
		    defaultObstacleRadius = Integer.parseInt(config.getProperty("rayon_robot_adverse"));
		}
	    catch (ConfigPropertyNotFoundException e)
    	{
    		log.debug("Revoir le code : impossible de trouver la propriété "+e.getPropertyNotFound());;
    	}
	}
    
    public ArrayList<ObstacleProximity> getUntestedArrayList()
    {
    	return mUntestedMobileObstacles;
    }

	/**
	 * Permet de update les obstacles avec un nouveau rayon de robot
	 * @param newRobotRadius le nouveau rayon
     */
	public void updateObstacles(int newRobotRadius)
	{
		if(this.mRobotRadius == newRobotRadius)
			return;

		for(ObstacleRectangular i : mRectangles)
		{
			i.changeDim(i.getSizeX()-2*mRobotRadius+2*newRobotRadius, i.getSizeY()-2*mRobotRadius+2*newRobotRadius);
		}

		for(ObstacleCircular i : mFixedObstacles)
		{
			i.setRadius(i.getRadius()-mRobotRadius+newRobotRadius);
		}

		for(ObstacleProximity i : mUntestedMobileObstacles)
		{
			i.setRadius(i.getRadius()-mRobotRadius+newRobotRadius);
		}

		for(ObstacleProximity i : mMobileObstacles)
		{
			i.setRadius(i.getRadius()-mRobotRadius+newRobotRadius);
		}

		this.mRobotRadius = newRobotRadius;
	}

    /**
     * Supprime tous les obstacles fixes qui superposent le point donné
     * Utile pour forcer le passage si les obstacles vont subir un changement
     * @param point le point à dégager
     * @return les obstacles supprimés
     */
	public ArrayList<Obstacle> freePoint(Vec2 point)
    {
        ArrayList<Obstacle> deleted = new ArrayList<>();

        for (int i=0;i< mFixedObstacles.size();i++)
        {
            if(mFixedObstacles.get(i).isInObstacle(point))
            {
                deleted.add(mFixedObstacles.get(i));
                removeObstacle(mFixedObstacles.get(i));
            }
        }

        for (int i=0;i< mRectangles.size();i++)
        {
            if(mRectangles.get(i).isInObstacle(point))
            {
                deleted.add(mRectangles.get(i));
                removeObstacle(mRectangles.get(i));
            }
        }
        return deleted;
    }

	/**
	 * Supprime TOUS les obstacles fixes de la table
     * http://cdn.meme.am/instances/500x/21541512.jpg
	 */
	public void destroyEverything()
	{
		mRectangles.clear();
		mLines.clear();
		mFixedObstacles.clear();
		mMobileObstacles.clear();
		mUntestedMobileObstacles.clear();
	}

}
