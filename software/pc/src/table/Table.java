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
public class Table implements Service
{
	/** Le gestionnaire d'obstacle. */
	private ObstacleManager mObstacleManager;
	
	/** système de log sur lequel écrire. */
	private Log log;

	/** endroit ou lire la configuration du robot */
	private Config config;
	
	// TODO: doc
	
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
		// TODO Auto-generated method stub
		
	}
}

