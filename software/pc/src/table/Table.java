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
	private boolean isPLotXEaten[];
	
	//les 5 verres, pris ou non 
	private boolean isGlassXTaken[];
	
	//les verres posés ou non
	private boolean isGlassXDropped[];

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
		for (int i = 0; i< 8; i++)
			isPLotXEaten[i]=false;
		
		//les verres
		for (int i = 0; i< 5; i++)
			isGlassXTaken[i]=false;
		
		//Les verres posés et leurs zones
		for (int i = 0; i< 5; i++)
			isGlassXDropped[i]=false;
		
		//Les zones où sont posés les verres
		isArea1FilledWithGlass=false;
		isArea2FilledWithGlass=false;
		isArea3FilledWithGlass=false;
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
			return isPLotXEaten[x];
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
		if (0<=x && x<=8)
			isPLotXEaten[x]=true;
		else
			log.debug("out of bound, plot counter",this);
	}
	
	/**
	 * enleve le verre x de la table
	 * @param x le numero du verre doit etre dans [0..4]
	 */
	public void removeGlassX (int x)
	{
		if (0<=x && x<=5)
			isGlassXTaken[x]=true;
		else
			log.debug("Probleme dans isGlassTaken",this);
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
			log.debug("Probleme dans isGlassTaken",this);
			return false;
	}
	
	/**
	 * le verre x a été laché, on met à jour la table
	 * @param x le numero du verre doit etre dans [0..4]
	 */
	public void glassXDropped (int x)
	{
		if (0<=x && x<=5)
			isGlassXDropped[x]=true;
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

