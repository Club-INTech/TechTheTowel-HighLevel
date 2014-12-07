package table;

import table.obstacles.ObstacleManager;
import container.Service;
import utils.*;



// TODO: Auto-generated Javadoc
/**
 * The Class Table.
 */
@SuppressWarnings("unused")
public class Table implements Service
{


	/** The gestionobstacles. */
	public ObstacleManager gestionobstacles;

	// Dépendances
	
	/** The log. */
	private Log log;
	
	/** The config. */
	private Config config;
	
	/**
	 * Instantiates a new table.
	 *
	 * @param log
	 *            the log
	 * @param config
	 *            the config
	 */
	public Table(Log log, Config config)
	{
		this.log = log;
		this.config = config;
		this.gestionobstacles = new ObstacleManager(log, config);
		initialise();
	}
	
	/**
	 * Initialise.
	 */
	public void initialise()
	{
	}
	
	/**
	 * Utilisé pour les tests.
	 *
	 * @param other
	 *            the other
	 * @return true, if successful
	 */
	public boolean equals(Table other)
	{
		return false; //TODO
 	}

	/* (non-Javadoc)
	 * @see container.Service#updateConfig()
	 */
	@Override
	public void updateConfig() {
		// TODO Auto-generated method stub
		
	}
	

}

