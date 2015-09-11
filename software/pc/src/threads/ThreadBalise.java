package threads;

import java.util.ArrayList;

import robot.cards.balise.BaliseCardWrapper;
import robot.cards.balise.BaliseFiltration;
import robot.cards.balise.Beacon;
import smartMath.Vec2;
import table.Table;
import utils.Sleep;


/**
 * Thread de gestion de la balise laser.
 *
 * @author pf
 * TODO suprimer et transformer la balise en sensor ?
 */

class ThreadBalise extends AbstractThread
{

	/** Le système de filtrage de valeurs a utiliser */
	private BaliseFiltration filtragelaser;
	
	/** La carte électronique Laser a utiliser */
	private BaliseCardWrapper laser;
	
	/** La table sur laquelle le thread doit croire évoluer */
	private Table table;

	/** Taille de la table selon l'axe X */
	private int sizeTableX;

	/** Taille de la table selon l'axe Y */
	private int sizeTableY;
	
	/** La fréquances des lasers */
	private float lasersFrequanecy;

	/**
	 * Crée un jouveau thread Laser
	 *
	 * @param laser La carte électronique Laser a utiliser
	 * @param table La table sur laquelle le thread doit croire évoluer
	 * @param filtragelaser Le système de filtrage de valeurs a utiliser
	 */
	public ThreadBalise(BaliseCardWrapper laser, Table table, BaliseFiltration filtragelaser)
	{
		this.filtragelaser = filtragelaser;
		this.laser = laser;
		this.table = table;
		updateConfig();
		Thread.currentThread().setPriority(2);
	}

	/* (non-Javadoc)
	 * @see threads.AbstractThread#run()
	 */
	@Override
	public void run()
	{
		log.debug("Lancement du thread de laser", this);

		// On attends le démarrage du match et qu'au moins une balise réponde
		while(laser.checkConnectedBeacons() == 0)
		{
			if(stopThreads)
			{
				log.debug("Stoppage du thread laser", this);
				return;
			}
			Sleep.sleep(100);
		}

		// Allumage des lasers
		log.debug("Allumage des lasers", this);
		laser.turnOn();

		// Attente de la vitesse stable
		Sleep.sleep(3000);

		for(Beacon balise: laser.ignoredBeacons())
		{
			log.warning("balise n°" + Integer.toString(balise.id) + " ignorée pendant le match, pas de réponses aux ping", this);
		}

		// Vérification de la cohérence des données des balises
		laser.checkBeaconConsistency();

		// Liste des balises prises en compte
		ArrayList<Beacon> balises = laser.activeBeacons();

		// attente du début du match
		while(!ThreadTimer.matchStarted)
		{
			if(stopThreads)
			{
				log.debug("Arrêt du thread laser", this);
				return;
			}
			Sleep.sleep(100);
		}
		
		
		while(!ThreadTimer.matchEnded)
		{
			long start = System.currentTimeMillis();
			if(stopThreads)
			{
				log.debug("Stoppage du thread laser", this);
				return;
			}

			for(Beacon balise: balises)
			{
				// Récupération de la position brute
				try {
					Vec2 p_bruit = laser.position_balise(balise.id); 
					// Aucune réponse valable
					if(p_bruit == null)
						continue;

					// Mise à jour du modèle de filtrage
					filtragelaser.update(p_bruit);

					// Récupération des valeurs filtrées
					Vec2 p_filtre = filtragelaser.position();
					

					// Vérification si l'obstacle est sur la table 
					if(p_filtre.x > -sizeTableX/2 && p_filtre.y > 0 && p_filtre.x < sizeTableX/2 && p_filtre.y < sizeTableY)
					{
						table.getObstacleManager().setEnnemyNewLocation(balise.id, p_filtre);
						log.debug("Laser voit ennemi en : " + p_filtre, this);
					}

					sleep((long)(1./lasersFrequanecy));
					long end = System.currentTimeMillis();
					filtragelaser.update_dt((int)(end-start));
				}
				catch(Exception e)
				{
					continue;
				}
			}

		}

		laser.turnOff();
		log.debug("Fin du thread des lasers", this);

	}

	
	/* (non-Javadoc)
	 * @see threads.AbstractThread#updateConfig()
	 */
	public void updateConfig()
	{
		try {
			sizeTableX = Integer.parseInt(config.getProperty("table_x"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		try {
			sizeTableY = Integer.parseInt(config.getProperty("table_y"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		try {
			lasersFrequanecy = Float.parseFloat(config.getProperty("lasers_frequence"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
