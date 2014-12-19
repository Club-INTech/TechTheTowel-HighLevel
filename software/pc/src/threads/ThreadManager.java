package threads;

import java.util.Hashtable;


import robot.cards.laser.LaserFiltration;
import robot.cards.laser.LaserCardWrapper;
import robot.cardsWrappers.ActuatorCardWrapper;
import robot.cardsWrappers.LocomotionCardWrapper;
import robot.cardsWrappers.SensorsCardWrapper;
import table.Table;
import utils.Log;
import utils.Config;

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
	 * @param sensorsCardWrapper La carte capteurs avec laquelle le thread va parler
	 * @param locomotionCardWrapper La carte d'asservissements avec laquelle le thread va parler
	 * @param actuatorCardWrapper La carte actionneurs avec laquelle le thread va parler
	 * @return le thread timer
	 */
	public AbstractThread getThreadTimer(Table table, SensorsCardWrapper sensorsCardWrapper, LocomotionCardWrapper locomotionCardWrapper, ActuatorCardWrapper actuatorCardWrapper)
	{
		AbstractThread thread = instanciedThreads.get("threadTimer");
		if(thread == null)
			instanciedThreads.put("threadTimer", new ThreadTimer(table, sensorsCardWrapper, locomotionCardWrapper));
		return instanciedThreads.get("threadTimer");
	}

	/**
	 * Renvois le thread capteurs.
	 * L'instancie si c'est la première fois qu'on le demande.
	 * 
	 * @param table La table a l'intérieure de laquelle le thread doit croire évoluer
	 * @param sensorsCardWrapper La carte capteurs avec laquelle le thread va parler
	 * @return le thread capteurs
	 */
	public AbstractThread getThreadSensors( Table table, SensorsCardWrapper sensorsCardWrapper)
	{
		AbstractThread thread = instanciedThreads.get("threadCapteurs");
		if(thread == null)
			instanciedThreads.put("threadCapteurs", new ThreadSensor( table, sensorsCardWrapper));
		return instanciedThreads.get("threadCapteurs");
	}

	/**
	 * Renvois le thread laser.
	 * L'instancie si c'est la première fois qu'on le demande.
	 * 
	 * @param laserCardWrapper la carte laser avec laquelle le thread va parler
	 * @param table La table a l'intérieure de laquelle le thread doit croire évoluer
	 * @param laserFiltration la méthode de filtrage de valeurs que le thread laser va utiliser
	 * @return le thread laser
	 */
	public AbstractThread getThreadLaser(LaserCardWrapper laserCardWrapper, Table table, LaserFiltration laserFiltration)
	{
		AbstractThread thread = instanciedThreads.get("threadLaser");
		if(thread == null)
			instanciedThreads.put("threadLaser", new ThreadLaser(laserCardWrapper, table, laserFiltration));
		return instanciedThreads.get("threadLaser");
	}

	/**
	 * Démarre tout les threads instanciés auparavant.
	 * On instancie un thread en utilisant  ThreadManager.getThreadLaser, ThreadManager.getThreadSensors ou ThreadManager.getThreadTimer
	 */
	public void startInstanciedThreads()
	{
		// démarre tout les threads déja instanciées
		log.debug("Démarrage des threads enregistrés", this);
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
