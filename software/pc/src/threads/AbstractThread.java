package threads;

import utils.Log;
import utils.Config;
import container.Service;

/**
 * Classe abstraite des threads.
 * Elle héreite des threads java
 *
 * @author pf,marsu
 */

public abstract class AbstractThread extends Thread implements Service
{

	/** Fichier ou lire la configuration du match. Ce sera le même pour tout les threads, et est directement rempli par le ThreadManager */
	protected static Config config;
	
	/** Le système de log a utiliser pour écrire. Ce sera le même pour tout les threads, et est directement rempli par le ThreadManager */
	protected static Log log;

	/** Commande d'arrêt des Threads: si ce boolée passe a true, les threads autres que main vont terminer leur exécution */
	protected static boolean stopThreads = false;
	
	/**
	 * Crée un nouveau Thread abstrait.
	 *
	 * @param config Fichier ou lire la configuration du match.
	 * @param log Le système de log a utiliser pour écrire.
	 */
	public AbstractThread(Config config, Log log)
	{
		AbstractThread.config = config;
		AbstractThread.log = log;
	}

	/**
	 * Crée un nouveau Thread abstrait.
	 * C'est ce constructeur qui sera appellé par les classes héritant de AbstractThread 
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

