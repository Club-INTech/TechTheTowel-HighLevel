package threads;

import java.util.ArrayList;

import exceptions.serial.SerialConnexionException;
import robot.cardsWrappers.LocomotionCardWrapper;
import robot.cardsWrappers.SensorsCardWrapper;
import table.Table;
import utils.Sleep;
import graphics.*;
import robot.RobotReal;
import smartMath.Vec2;

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
	
	//TODO : le robot, à supprimer eventuellement
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
	public static long matchDuration = Integer.parseInt(config.getProperty("temps_match").replaceAll(" ","") ) *1000;
	
	/** Temps en ms qui s'écoule entre deux mise a jour de la liste des obstacle périmables. Lors de chaque mise a jour, les obstacles périmés sont détruits. */
	public static int obstacleRefreshInterval = 50;
	
	//TODO : interface graphique à enlever eventuellement (necessaire pour les tests)
	public Window window;
	
	/**
	 * Crée le thread timer.
	 * TODO : enlever le robot eventuellement
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
		
		//TODO : interface graphique à enlever (necessaire pour les tests)
		window = new Window(table, robot);
	}

	/* (non-Javadoc)
	 * @see threads.AbstractThread#run()
	 */
	@Override
	public void run()
	{
		log.debug("Lancement du thread timer", this);

		// on eteind les capteursgetObstacleManager
		config.set("capteurs_on", "false");
		mSensorsCardWrapper.updateConfig();	
		
		// Attente du démarrage du match
		while(!mSensorsCardWrapper.isJumperAbsent() && !matchStarted)
		{
			if(stopThreads)
			{
				log.debug("Arrêt du thread timer avant le début du match", this);
				return;
			}
			Sleep.sleep(50);
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
			
			//on rafraichit l'interface graphique, TODO : à enlever
			window.getPanel().repaint();
			
			
			window.getPanel().drawArrayList(robot.cheminSuivi);
			
			try
			{
				Thread.sleep(obstacleRefreshInterval);
			}
			catch(Exception e)
			{
				log.warning(e.toString(), this);
			}
		}

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

		try {
			mLocomotionCardWrapper.immobilise();
		} catch (SerialConnexionException e) {
			e.printStackTrace();
		}

		// fin du match : désasser
		try 
		{
			mLocomotionCardWrapper.disableRotationnalFeedbackLoop();
			mLocomotionCardWrapper.disableTranslationnalFeedbackLoop();
		}
		catch (SerialConnexionException e)
		{
			e.printStackTrace();
		}
		
		// et on coupe la connexion avec la carte d'asser comme ca on est sur qu'aucune partie du code ne peut faire quoi que ce soit pour faire bouger le robot
		mLocomotionCardWrapper.closeLocomotion();
	}
	
	
	/**
	 * Temps_restant.
	 *
	 * @return the long
	 */
	public long temps_restant()
	{
		return matchStartTimestamp + matchDuration - System.currentTimeMillis();
	}
	
	/* (non-Javadoc)
	 * @see threads.AbstractThread#updateConfig()
	 */
	public void updateConfig()
	{
		// facteur 1000 car temps_match est en secondes et duree_match en ms
		try
		{
			matchDuration = 1000*Long.parseLong(config.getProperty("temps_match"));
		}
		catch(Exception e)
		{
			log.warning(e, this);
		}
	}
	
}
