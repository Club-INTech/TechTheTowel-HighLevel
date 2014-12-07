package threads;

import java.util.ArrayList;

import robot.cards.laser.Beacon;
import robot.cards.laser.LaserFiltration;
import robot.cards.laser.Laser;
import smartMath.Vec2;
import table.Table;
import utils.Sleep;

// TODO: Auto-generated Javadoc
/**
 * Thread de gestion de la balise laser.
 *
 * @author pf
 */

class ThreadLaser extends AbstractThread {

	/** The filtragelaser. */
	private LaserFiltration filtragelaser;
	
	/** The laser. */
	private Laser laser;
	
	/** The table. */
	private Table table;

	/** The table_x. */
	private int table_x;
	
	/** The table_y. */
	private int table_y;
	
	/** The lasers_frequence. */
	private float lasers_frequence;

	/**
	 * Instantiates a new thread laser.
	 *
	 * @param laser the laser
	 * @param table the table
	 * @param filtragelaser the filtragelaser
	 */
	public ThreadLaser(Laser laser, Table table, LaserFiltration filtragelaser)
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
		while(!ThreadTimer.match_demarre)
		{
			if(stopThreads)
			{
				log.debug("Arrêt du thread laser", this);
				return;
			}
			Sleep.sleep(100);
		}
		
		
		while(!ThreadTimer.fin_match)
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
					if(p_filtre.x > -table_x/2 && p_filtre.y > 0 && p_filtre.x < table_x/2 && p_filtre.y < table_y)
					{
						table.gestionobstacles.deplacer_robot_adverse(balise.id, p_filtre);
						log.debug("Laser voit ennemi en : " + p_filtre, this);
					}

					sleep((long)(1./lasers_frequence));
					long end = System.currentTimeMillis();
					filtragelaser.update_dt((int)(end-start));
				}
				catch(Exception e)
				{
					// TODO (désactiver pour le moment car, vu que FiltrageLaser n'est pas fini, y'a des erreurs tout le temps)
					//					e.printStackTrace();
					continue;
				}
			}

		}

		laser.eteindre();
		log.debug("Fin du thread des lasers", this);

	}

	/* (non-Javadoc)
	 * @see threads.AbstractThread#updateConfig()
	 */
	public void updateConfig()
	{
		try {
			table_x = Integer.parseInt(config.get("table_x"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		try {
			table_y = Integer.parseInt(config.get("table_y"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		try {
			lasers_frequence = Float.parseFloat(config.get("lasers_frequence"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
