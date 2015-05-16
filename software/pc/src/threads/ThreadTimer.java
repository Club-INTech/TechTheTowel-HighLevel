package threads;

import exceptions.serial.SerialConnexionException;
import robot.cardsWrappers.LocomotionCardWrapper;
import robot.cardsWrappers.SensorsCardWrapper;
import table.Table;
import graphics.*;
import robot.RobotReal;

/**
 * Thread qui s'occupe de la gestion du temps: début du match et immobilisation du robot en fin de match
 * demande aussi périodiquement a la table qu'on lui fournit de retirer les obstacles périmés
 * C'est lui qui active les capteurs en début de match.
 * @author pf, marsu
 *
 */

public class ThreadTimer extends AbstractThread
{
	/** La table sur laquelle le thread doit croire évoluer */
	private Table table;
	
	private RobotReal robot;

	/** La carte capteurs avec laquelle on doit communiquer */
	private SensorsCardWrapper mSensorsCardWrapper;

	/** La carte d'asservissement avec laquelle on doit communiquer */
	private LocomotionCardWrapper mLocomotionCardWrapper;
	
	/** vrai si le match a effectivment démarré, faux sinon */
	public static boolean matchStarted = false;

	/** vrai si le match a effectivment pris fin, faux sinon */
	public static boolean matchEnded = false;
	
	/** Date de début du match. */
	public static long matchStartTimestamp;
	
	/** Durée en miliseconde d'un match recupéré de la config */
	public static long matchDuration = 90000;
	
	/** Temps en ms qui s'écoule entre deux mise a jour de la liste des obstacle périmables. Lors de chaque mise a jour, les obstacles périmés sont détruits. */
	public static int obstacleRefreshInterval = 0;
	
	/** interface graphique d'affichage de la table, pour le debug */
	public Window window;
	
	/**
	 * indique si l'interface graphique est activée ou non 
	 */
	private boolean isGraphicalInterfaceEnabled = false; 
	
	
	/**
	 * Crée le thread timer.
	 * TODO : enlever le robot eventuellement (si on arrete d'afficher l'interface)
	 *
	 * @param table La table sur laquelle le thread doit croire évoluer
	 * @param sensorsCardWrapper La carte capteurs avec laquelle on doit communiquer
	 * @param locomotionCardWrapper La carte d'asservissement avec laquelle on doit communiquer
	 */
	
	ThreadTimer(Table table, RobotReal robot, SensorsCardWrapper sensorsCardWrapper, LocomotionCardWrapper locomotionCardWrapper)
	{
		this.table = table;
		this.mSensorsCardWrapper = sensorsCardWrapper;
		this.mLocomotionCardWrapper = locomotionCardWrapper;
		this.robot=robot;
		
		updateConfig();
		Thread.currentThread().setPriority(1);

		// DEBUG: interface graphique
		try
		{
			window = new Window();
		}
		catch (Exception e)
		{
			isGraphicalInterfaceEnabled = false;
			log.debug("Affichage graphique non disponible", this);
		}
	}

	/* (non-Javadoc)
	 * @see threads.AbstractThread#run()
	 */
	@Override
	public void run()
	{
		log.debug("Lancement du thread timer", this);

		// on eteind les capteursgetObstacleManager
		config.set("capteurs_on", "true");
		mSensorsCardWrapper.updateConfig();	
		
		// Attente du démarrage du match
		while(!mSensorsCardWrapper.isJumperAbsent() && !matchStarted)
		{
			if(stopThreads)
			{
				log.debug("Arrêt du thread timer avant le début du match", this);
				return;
			}
			try
			{
				Thread.sleep(50);
			} 
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		
		// Le match démarre ! On chage l'état du thread pour refléter ce changement
		matchStartTimestamp = System.currentTimeMillis();
		matchStarted = true;

		config.set("capteurs_on", "true");
		mSensorsCardWrapper.updateConfig();

		log.debug("LE MATCH COMMENCE !", this);

		// boucle principale, celle qui dure tout le match
		while(System.currentTimeMillis() - matchStartTimestamp < matchDuration)
		{
			if(stopThreads)
			{
				// ons 'arrète si le ThreadManager le demande
				log.debug("Arrêt du thread timer demandé durant le match", this);
				return;
			}
			

			// On retire périodiquement les obstacles périmés
			table.getObstacleManager().removeOutdatedObstacles();
			
			//on rafraichit l'interface graphique de la table
			if(isGraphicalInterfaceEnabled && window != null)
			{
				window.getPanel().repaint();
				
				window.getPanel().drawArrayList(robot.cheminSuivi);
			}
			
			try
			{
				Thread.sleep(obstacleRefreshInterval);
			}
			catch(Exception e)
			{
				log.warning(e.toString(), this);
			}
		}
		log.debug("Fin des "+matchDuration+" ms de match, temps : "+(System.currentTimeMillis() - matchStartTimestamp) , this);


		// actions de fin de match
		onMatchEnded();
		
		log.debug("Fin du thread timer", this);
		
	}
	
	/**
	 * On match ended.
	 */
	private void onMatchEnded()
	{
 
		log.debug("Fin du Match car fin des 90s !", this);

		// Le match est fini, immobilisation du robot
		matchEnded = true;

		try
		{
			mLocomotionCardWrapper.immobilise();
		} catch (SerialConnexionException e) {
			log.debug( e.logStack(), this);
		}

		// fin du match : on eteint la STM
		try 
		{	
			mLocomotionCardWrapper.disableRotationnalFeedbackLoop();
			mLocomotionCardWrapper.disableTranslationnalFeedbackLoop();
			mLocomotionCardWrapper.shutdownSTM();
			
		}
		catch (SerialConnexionException e)
		{
			log.critical( e.logStack(), this);
		}
		
		// et on coupe la connexion avec la carte d'asser comme ca on est sur qu'aucune partie du code ne peut faire quoi que ce soit pour faire bouger le robot
		mLocomotionCardWrapper.closeLocomotion();
	}
	
	
	/**
	 * Temps restant avant la fin du match.
	 *
	 * @return le temps restant du match en milisecondes
	 */
	public static long remainingTime()
	{
		return matchStartTimestamp + matchDuration - System.currentTimeMillis();
	}
	

	/**
	 * Temps écoulé depuis le début du match
	 *
	 * @return le temps écoulé du match en milisecondes
	 */
	public static long ellapsedTimeSinceMatchStarted()
	{
		return  System.currentTimeMillis() - matchStartTimestamp;
	}
	
	/* (non-Javadoc)
	 * @see threads.AbstractThread#updateConfig()
	 */
	public void updateConfig()
	{
		// facteur 1000 car temps_match est en secondes et duree_match en ms
		try
		{
			matchDuration =  Integer.parseInt(config.getProperty("temps_match").replaceAll(" ","") ) *1000;
		}
		catch(Exception e)
		{
			log.warning(e, this);
		}
	}
	
}
