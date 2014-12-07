package threads;

import utils.Log;
import utils.Config;
import container.Service;

// TODO: Auto-generated Javadoc
/**
 * Classe abstraite des threads.
 *
 * @author pf,marsu
 */

public abstract class AbstractThread extends Thread implements Service {

	/** The config. */
	protected static Config config;
	
	/** The log. */
	protected static Log log;

	/** The stop threads. */
	protected static boolean stopThreads = false;
	
	/**
	 * Instantiates a new abstract thread.
	 *
	 * @param config the config
	 * @param log the log
	 */
	public AbstractThread(Service config, Service log)
	{
		AbstractThread.config = (Config) config;
		AbstractThread.log = (Log) log;
	}

	/**
	 * Instantiates a new abstract thread.
	 */
	protected AbstractThread()
	{		
	}

	/* (non-Javadoc)
	 * @see container.Service#updateConfig()
	 */
	public void updateConfig()
	{
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public abstract void run();

}

