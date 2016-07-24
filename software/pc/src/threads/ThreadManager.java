package threads;

import robot.Robot;
import robot.serial.SerialWrapper;
import table.Table;
import threads.dataHandlers.*;
import utils.Config;
import utils.Log;

import java.util.Hashtable;

/**
 * Service qui instancie les threads et le démarre a la demande.
 *
 * @author pf, marsu
 */

public class ThreadManager
{
	
	/** Le système de log a utiliser par le threamManager pour écrire */
	private Log log;
	
	/** Ensemble des threads instanciés jusqu'ici */
	private Hashtable<String, AbstractThread> instanciedThreads;
	
	/**
	 * Instancie un nouveau thread manager.
	 *
	 * @param config Fichier de configureation a utiliser par le threadmanager ainsi que tout les threads autres que main pour configure le match 
	 * @param log Le système de log a utiliser par le threamManager ainsique tout les threads autes que main pour écrire 
	 */
	public ThreadManager(Config config, Log log)
	{
		// renseigne le système de log que le ThreadManager va lui meme utiliser
		this.log = log;
		
		// initialise la liste des threads instanciés a une miste vide
		instanciedThreads = new Hashtable<String, AbstractThread>();

		// renseigne le système de log que l'ensmeble des threads vont utiliser 
		AbstractThread.log = log;
		AbstractThread.config = config;
		AbstractThread.stopThreads = false;
	}

	/**
	 * Renvois le thread Timer
	 * L'instancie si c'est la première fois qu'on le demande.
	 *
	 * @param table La table a l'intérieure de laquelle le thread doit croire évoluer
	 * @param robot 
	 * @param sensorsCardWrapper La carte capteurs avec laquelle le thread va parler
	 * @return le thread timer
	 */
	public AbstractThread getThreadTimer(Table table, Robot robot, SerialWrapper sensorsCardWrapper)
	{
		AbstractThread thread = instanciedThreads.get("threadTimer");
		if(thread == null)
			instanciedThreads.put("threadTimer", new ThreadTimer(table, robot, sensorsCardWrapper));
		return instanciedThreads.get("threadTimer");
	}

	/**
	 * Renvoie le thread capteurs.
	 * L'instancie si c'est la première fois qu'on le demande.
	 * 
	 * @param table La table a l'intérieure de laquelle le thread doit croire évoluer
	 * @param robot 
	 * @param sensorsCardWrapper La carte capteurs avec laquelle le thread va parler
	 * @return le thread capteurs
	 */
	public AbstractThread getThreadSensors(Table table, Robot robot, SerialWrapper sensorsCardWrapper, ThreadSerial serial)
	{
		AbstractThread thread = instanciedThreads.get("threadCapteurs");
		if(thread == null)
			instanciedThreads.put("threadCapteurs", new threads.dataHandlers.ThreadSensor( table, robot, sensorsCardWrapper, serial));
		return instanciedThreads.get("threadCapteurs");
	}

	/**
	 * Renvoie le thread graphique
	 * @param config la config
	 * @param log le log
	 * @param table la table
	 * @param robot le robot réel
     */
	public AbstractThread getThreadInterface(Config config, Log log, Table table, Robot robot)
	{
		AbstractThread thread = instanciedThreads.get("threadInterface");
		if(thread == null)
			instanciedThreads.put("threadInterface", new ThreadInterface(config, log, table, robot));
		return instanciedThreads.get("threadInterface");
	}

	/**
	 * Démarre tout les threads instanciés auparavant.
	 * On instancie un thread en utilisant  ThreadManager.getThreadLaser, ThreadManager.getThreadSensors ou ThreadManager.getThreadTimer
	 */
	public void startInstanciedThreads()
	{
		// démarre tout les threads déja instanciées
		log.debug("Démarrage des threads enregistrés");
		for(String nom: instanciedThreads.keySet())
			instanciedThreads.get(nom).start();
		
		// attends un dixième de seconde pour voir si un thread ne plante pas tout de suite.
		try
		{
			Thread.sleep(100);
		} 
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Demande a tout les threads lancés de s'interrompre
	 */
	public void stopAllThreads()
	{
		AbstractThread.stopThreads = true;
	}
}
