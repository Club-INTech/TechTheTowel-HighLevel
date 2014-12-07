package threads;

import java.util.Hashtable;

import robot.cards.laser.LaserFiltration;
import robot.cards.laser.Laser;
import robot.cardsWrappers.ActuatorCardWrapper;
import robot.cardsWrappers.LocomotionCardWrapper;
import robot.cardsWrappers.SensorsCardWrapper;
import table.Table;
import robot.RobotReal;
import utils.Log;
import utils.Config;
// TODO: Auto-generated Javadoc
/**
 * Service qui instancie les threads.
 *
 * @author pf
 */

public class ThreadManager
{
	
	/** The log. */
	private Log log;
	
	/** The threads. */
	private Hashtable<String, AbstractThread> threads;
	
	/**
	 * Instantiates a new thread manager.
	 *
	 * @param config the config
	 * @param log the log
	 */
	public ThreadManager(Config config, Log log)
	{
		this.log = log;
		
		threads = new Hashtable<String, AbstractThread>();

		AbstractThread.log = log;
		AbstractThread.config = config;
		AbstractThread.stopThreads = false;
	}

	/**
	 * Donne un thread à partir de son nom. Utilisé par container uniquement.
	 *
	 * @param table the table
	 * @param capteur the capteur
	 * @param deplacements the deplacements
	 * @param actionneurs the actionneurs
	 * @return the thread timer
	 */
	public AbstractThread getThreadTimer(Table table, SensorsCardWrapper capteur, LocomotionCardWrapper deplacements, ActuatorCardWrapper actionneurs)
	{
		AbstractThread thread = threads.get("threadTimer");
		if(thread == null)
			threads.put("threadTimer", new ThreadTimer(table, capteur, deplacements));
		return threads.get("threadTimer");
	}

	/**
	 * Gets the thread capteurs.
	 *
	 * @param robotvrai the robotvrai
	 * @param table the table
	 * @param capteurs the capteurs
	 * @return the thread capteurs
	 */
	public AbstractThread getThreadCapteurs(RobotReal robotvrai, Table table, SensorsCardWrapper capteurs)
	{
		AbstractThread thread = threads.get("threadCapteurs");
		if(thread == null)
			threads.put("threadCapteurs", new ThreadSensor(robotvrai, table, capteurs));
		return threads.get("threadCapteurs");
	}

	/**
	 * Gets the thread laser.
	 *
	 * @param laser the laser
	 * @param table the table
	 * @param filtragelaser the filtragelaser
	 * @return the thread laser
	 */
	public AbstractThread getThreadLaser(Laser laser, Table table, LaserFiltration filtragelaser)
	{
		AbstractThread thread = threads.get("threadLaser");
		if(thread == null)
			threads.put("threadLaser", new ThreadLaser(laser, table, filtragelaser));
		return threads.get("threadLaser");
	}

	/**
	 * Start instancied threads.
	 */
	public void startInstanciedThreads()
	{
		log.debug("Démarrage des threads enregistrés", this);
		for(String nom: threads.keySet())
			threads.get(nom).start();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Stop all threads.
	 */
	public void stopAllThreads()
	{
		AbstractThread.stopThreads = true;
	}
}
