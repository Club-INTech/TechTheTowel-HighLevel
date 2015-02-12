package table;


import table.obstacles.*;
import container.Service;
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
 *          TODO : migrer la liste d'obstacles vers l'obstacle manager
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
	
	
	//les huits plots (voir numerotation sur la table)
	private boolean isPlot0Eaten;
	private boolean isPlot1Eaten;
	private boolean isPlot2Eaten;
	private boolean isPlot3Eaten;
	private boolean isPlot4Eaten;
	private boolean isPlot5Eaten;
	private boolean isPlot6Eaten;
	private boolean isPlot7Eaten;
	
	//les 5 verres, pris ou non 
	private boolean isGlass0Taken;
	private boolean isGlass1Taken;
	private boolean isGlass2Taken;
	private boolean isGlass3Taken;
	private boolean isGlass4Taken;
	
	//les verres posés ou non
	private boolean isGlass0Dropped;
	private boolean isGlass1Dropped;
	private boolean isGlass2Dropped;
	private boolean isGlass3Dropped;
	private boolean isGlass4Dropped;

	//Les emplacements où sont posés les verres (voir doc des zones dans DropGlasss : 
	//1=Notre zone, 2=haut zone ennemi, 3=bas zone ennemi
	
	private boolean isArea1FilledWithGlass;
	private boolean isArea2FilledWithGlass;
	private boolean isArea3FilledWithGlass;
	
	
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
		isPlot0Eaten = false;
		isPlot1Eaten = false;
		isPlot2Eaten = false;
		isPlot3Eaten = false;
		isPlot4Eaten = false;
		isPlot5Eaten = false;
		isPlot6Eaten = false;
		isPlot7Eaten = false;
		
		//les verres
		isGlass0Taken=false;
		isGlass1Taken=false;
		isGlass2Taken=false;
		isGlass3Taken=false;
		isGlass4Taken=false;
		
		//Les verres posés et leurs zones
		isGlass0Dropped=false;
		isGlass1Dropped=false;
		isGlass2Dropped=false;
		isGlass3Dropped=false;
		isGlass4Dropped=false;
		
		//Les zones où sont posés les verres
		isArea1FilledWithGlass=false;
		isArea2FilledWithGlass=false;
		isArea3FilledWithGlass=false;
	}
	public ObstacleManager getObstacleManager()
	{
		return mObstacleManager;
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
	
	/**
	 * 
	 * @param x le numero du plot
	 * @return vrai si le plot a ete mange, faux sinon ou si le nombre n'est pas dans [0..7]
	 */
	public boolean isPlotXEaten (int x)
	{
		if (x==0)
			return isPlot0Eaten;
		else if (x==1)
			return isPlot1Eaten;
		else if (x==2)
			return isPlot2Eaten;
		else if (x==3)
			return isPlot3Eaten;
		else if (x==4)
			return isPlot4Eaten;
		else if (x==5)
			return isPlot5Eaten;
		else if (x==6)
			return isPlot6Eaten;
		else if (x==7)
			return isPlot7Eaten;
		else
			log.debug("out of bound, plot counter",this);
			return false;
	}
	
	/**
	 * mange le plot x
	 * @param x le numero du plot doit etre dans [0..7]
	 */
	public void eatPlotX (int x)
	{
		if (x==0)
			isPlot0Eaten=true;
		else if (x==1)
			isPlot1Eaten=true;
		else if (x==2)
			isPlot2Eaten=true;
		else if (x==3)
			isPlot3Eaten=true;
		else if (x==4)
			isPlot4Eaten=true;
		else if (x==5)
			isPlot5Eaten=true;
		else if (x==6)
			isPlot6Eaten=true;
		else if (x==7)
			isPlot7Eaten=true;
		else
			log.debug("out of bound, plot counter",this);
	}
	
	/**
	 * enleve le verre x de la table
	 * @param x le numero du verre doit etre dans [0..4]
	 */
	public void removeGlassX (int x)
	{
		if (x==0)
			isGlass0Taken=true;
		else if (x==1)
			isGlass1Taken=true;
		else if (x==2)
			isGlass2Taken=true;
		else if (x==3)
			isGlass3Taken=true;
		else if (x==4)
			isGlass4Taken=true;
		else
			log.debug("Probleme dans isGlassTaken",this);
	}
	
	/**
	 * dis si le verre x a été pris
	 * @param x le numero du verre doit etre dans [0..4]
	 */
	public boolean isGlassXTaken (int x)
	{
		if (x==0)
			return isGlass0Taken;
		else if (x==1)
			return isGlass1Taken;
		else if (x==2)
			return isGlass2Taken;
		else if (x==3)
			return isGlass3Taken;
		else if (x==4)
			return isGlass4Taken;
		else
			log.debug("Probleme dans isGlassTaken",this);
			return false;
	}
	
	/**
	 * le verre x a été laché, on met à jour la table
	 * @param x le numero du verre doit etre dans [0..4]
	 */
	public void glassXDropped (int x)
	{
		if (x==0)
			isGlass0Dropped=true;
		else if (x==1)
			isGlass1Dropped=true;
		else if (x==2)
			isGlass2Dropped=true;
		else if (x==3)
			isGlass3Dropped=true;
		else if (x==4)
			isGlass4Dropped=true;
		else
			log.debug("Probleme dans glassXDropped",this);
	}
	
	/**
	 * Un verre a été deposé dans la zone x, on  met à jour 
	 * @param x le numero du verre doit etre dans [0..3]
	 */
	public void areaXFilled (int x)
	{
		if (x==1)
			isArea1FilledWithGlass=true;
		else if (x==2)
			isArea2FilledWithGlass=true;
		else if (x==3)
			isArea3FilledWithGlass=true;
		else
			log.debug("Probleme dans areaXFilled",this);
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

