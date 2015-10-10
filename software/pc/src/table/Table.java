package table;


import smartMath.Vec2;
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
	
	
	/** point de depart du match a modifier a chaque base roulante */
	public static final Vec2 entryPosition = new Vec2 (1099,1000); //TODO position entree
	
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
		//TODO initialiser la table
	}
	
	public ObstacleManager getObstacleManager()
	{
		return mObstacleManager;
	}

	/* (non-Javadoc)
	 * @see container.Service#updateConfig()
	 */
	@Override
	public void updateConfig()
	{
		// TODO update config
	}
}

