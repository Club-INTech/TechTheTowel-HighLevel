package table;


import smartMath.Vec2;
import table.obstacles.*;
import container.Service;
import enums.ObstacleGroups;
import utils.*;

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
 *  
 */

/**
 * Stocke toute les informations liées a la table (muables et immuables) au cours d'un match.
 */
public class Table implements Service
{
	/** Le gestionnaire d'obstacle. */
	private ObstacleManager mObstacleManager;
	
	/** système de log sur lequel écrire. */
	private Log log;

	/** endroit ou lire la configuration du robot */
	private Config config;
	
	//claps fermés ou non
	private boolean isClap1Closed;
	private boolean isClap2Closed;
	private boolean isClap3Closed;

	//tapis posés ou non
	private boolean isLeftCarpetDropped;
	private boolean isRightCarpetDropped;
	
	/**
	 * le nombre de points que valent les piles posées dans la zone de depart (0) ou sur l'estrade (1)
	 */
	private int[] pileValue = {0,0}; 
	
	//les huits plots (voir numerotation sur la table)
	private boolean isPlotXEaten[];
	
	//les 5 verres, pris ou non 
	private boolean isGlassXTaken[];
	
	//les verres posés ou non
	private boolean isGlassXDropped[];

	//les aires sont pleines ou non
	private boolean[] isAreaXFilledWithGlass;
	
	//la balle de tennis
	private boolean isBallTaken;
	
	// point de depart du match a modifier a chaque base roulante
	public static final Vec2 entryPosition = new Vec2 (1500-320-77,1000);
	//1500 le bout de la table, 320 la taille de la cale et 77 la taille de l'arriere du robot a son centre,1000);
	
	
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
		this.mObstacleManager = new ObstacleManager(log, config);
		
		isPlotXEaten = new boolean[8];
		isGlassXTaken = new boolean[5];
		isGlassXDropped = new boolean[5];
		isAreaXFilledWithGlass = new boolean[3];
		
		initialise();
	}
	
	public void initialise() // initialise la table du debut du jeu
	{
		// Claps
		isClap1Closed=false;
		isClap2Closed=false;
		isClap3Closed=false;

		//les tapis
		isLeftCarpetDropped = false;
		isRightCarpetDropped = false;
		
		//les plots
		for (int i = 0; i< 8; i++)
			isPlotXEaten[i]=false;
		
		//les verres
		for (int i = 0; i< 5; i++)
			isGlassXTaken[i]=false;
		
		//Les verres posés et leurs zones
		for (int i = 0; i< 5; i++)
			isGlassXDropped[i]=false;
		
		//Les zones où sont posés les verres
		for(int i = 0; i<3; i++)
			isAreaXFilledWithGlass[i]=false;
	}
	
	public ObstacleManager getObstacleManager()
	{
		return mObstacleManager;
	}
	
	/** Fonction explicitant si le clap "numberOfClap" est fermé*/
	public boolean isClapXClosed(int numberOfClap)
	{
		if( numberOfClap==1)
			return isClap1Closed;
		if( numberOfClap==2)
			return isClap2Closed;
		if( numberOfClap==3)
			return isClap3Closed;
		else
		{
			log.debug("Out of bound isClapXClosed", this);
			return false;
		}
	}

	
	/** Fonction a appeler quand le clap x est fermé*/
	public void clapXClosed(int numberOfClap)
	{
		if( numberOfClap==1)
			isClap1Closed=true;
		if( numberOfClap==2)
			isClap2Closed=true;
		if( numberOfClap==3)
			isClap3Closed=true;
		else
			log.debug("Out of bound clapXClosed", this);
	}
		
	/**
	 * 
	 * @param x le numero du plot
	 * @return vrai si le plot a ete mange, faux sinon ou si le nombre n'est pas dans [0..7]
	 */
	public boolean isPlotXEaten (int x)
	{
		if (0<=x && x<=8)
			return isPlotXEaten[x];
		else
			log.debug("out of bound, plot counter",this);
			return false;
	}
	/**
	 * 
	 * @return le nombre de plots restants sur la table
	 */
	public int numberOfPlotLeft() 
	{
		int toReturn = 8;
		for (int i=0; i<isPlotXEaten.length; i++)
		{
			if (isPlotXEaten[i])
					toReturn -= 1;
		}
		return toReturn;
	}
	
	/**
	 * mange le plot x
	 * @param x le numero du plot doit etre dans [0..7]
	 */
	public void eatPlotX (int x)
	{
		if (0<=x && x<=7)
		{
			isPlotXEaten[x]=true;
		}
		else
			log.debug("out of bound, plot counter",this);

		if(x==0)
			mObstacleManager.removeFixedObstacle(ObstacleGroups.GREEN_PLOT_0);
		else if(x==1)
			mObstacleManager.removeFixedObstacle(ObstacleGroups.GREEN_PLOT_1);
		else if(x==2)
			mObstacleManager.removeFixedObstacle(ObstacleGroups.GREEN_PLOT_2);
		else if(x==3)
			mObstacleManager.removeFixedObstacle(ObstacleGroups.GREEN_PLOT_3);
		else if(x==4)
			mObstacleManager.removeFixedObstacle(ObstacleGroups.GREEN_PLOT_4);
		else if(x==5)
			mObstacleManager.removeFixedObstacle(ObstacleGroups.GREEN_PLOT_5);
		else if(x==6)
			mObstacleManager.removeFixedObstacle(ObstacleGroups.GREEN_PLOT_6);
		else if(x==7)
			mObstacleManager.removeFixedObstacle(ObstacleGroups.GREEN_PLOT_7);	
	
		mObstacleManager.printObstacleFixedList();

	}
	
	/**
	 * demande a la table si on a attrapé la balle
	 * @return vrai si la balle a ete attrapée
	 */
	public boolean isBallTaken() 
	{
		return isBallTaken;
	}

	/**
	 * prends la balle de la table
	 */
	public void takeBall() 
	{
		isBallTaken = true;
	}

	/**
	 * 
	 * @param place 0 pour la zone de depart 1 pour l'estrade
	 * @return le nombre de points que vaut la pile de plots a cet endroit
	 */
	public int getPileValue(int place) 
	{
		return pileValue[place];
	}

	/**
	 * 
	 * @param place 0 pour la zone de depart 1 pour l'estrade
	 * @param value le nouveau nombre de point que vaut la pile de plots a cet endroit
	 */
	public void setPileValue(int place, int value) 
	{
		if (place == 0 || place ==1)
			this.pileValue[place] = value;
		else
			log.debug("on essaye de mettre une valeur de pile a un endroit inexistant", this);
	}

	/**
	 * enleve le verre x de la table
	 * @param x le numero du verre doit etre dans [0..4]
	 */
	public void removeGlassX (int x)
	{
		if (0<=x && x<=4)
			isGlassXTaken[x]=true;
		else
			log.debug("out of bound isGlassTaken",this);
		
		if(x==0)
			mObstacleManager.removeFixedObstacle(ObstacleGroups.GOBLET_0);
		else if(x==1)
			mObstacleManager.removeFixedObstacle(ObstacleGroups.GOBLET_1);
		else if(x==2)
			mObstacleManager.removeFixedObstacle(ObstacleGroups.GOBLET_2);
		else if(x==3)
			mObstacleManager.removeFixedObstacle(ObstacleGroups.GOBLET_3);
		else if(x==4)
			mObstacleManager.removeFixedObstacle(ObstacleGroups.GOBLET_4);
		
		mObstacleManager.printObstacleFixedList();
	}
	
	/**
	 * dis si le verre x a été pris
	 * @param x le numero du verre doit etre dans [0..4]
	 */
	public boolean isGlassXTaken (int x)
	{
		if (0<=x && x<=5)
			return isGlassXTaken[x];
		else
			log.debug("out of bound isGlassTaken",this);
			return false;
	}
	
	/**
	 * Un verre a été deposé dans la zone x, on  met à jour 
	 * @param x la zone (0 la notre, 1 a notre droite et 2 a gauche)
	 *  ____________________________
	 * 	|							|
	 * 	|1							|
	 *	|----					----|
	 * 	|Debut ennemi			   0|Debut de NOTRE robot
	 * 	|----					----|
	 * 	|2							|
	 *	|___________________________|
	 *
	 */
	public void areaXFilled (int x)
	{
		if (0<=x && x<=2)
			isAreaXFilledWithGlass[x]=true;
		else
			log.debug("out of bound areaXFilled",this);
	}
	
	/** Fonction renvoyant si la zone en argument est remplie d'un verre ou non 
	 * 
	 * @param x la zone (0 la notre, 1 a notre droite et 2 a gauche)
	 *  ____________________________
	 * 	|							|
	 * 	|1							|
	 *	|----					----|
	 * 	|Debut ennemi			   0|Debut de NOTRE robot
	 * 	|----					----|
	 * 	|2							|
	 *	|___________________________|
	 *
	 *
	 * @return si la zone est remplie
	 */
	
	public boolean isAreaXFilled (int x)
	{
		if (0<=x && x<=2)
			return isAreaXFilledWithGlass[x];
		log.debug("out of bound isAreaXFilled", this);
		return false;
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
        	
        	
			if(!mObstacleManager.equals(ct.mObstacleManager))
				mObstacleManager.copy(ct.mObstacleManager);
		}
	}
	
	public Table clone()
	{
		Table cloned_table = new Table(log, config);
		copy(cloned_table);
		return cloned_table;
	}
	
	//accesseurs
	
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
		// TODO Auto-generated
	}
	
	public Config getConfig()
	{
		return config;
	}
}

