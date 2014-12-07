package table;

import table.obstacles.ObstacleManager;
import container.Service;
import utils.*;

/**
 * Stocke toute les informations liées a la table (muables et immuables) au cours d'un match.
 */
@SuppressWarnings("unused")
public class Table implements Service
{
	/** Le gestionnaire d'obstacle. */
	public ObstacleManager mObstacleManager;

	/** système de log sur lequel écrire. */
	private Log log;

	/** endroit ou lire la configuration du robot */
	private Config config;
	
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

