package robot.cards.laser;

import java.util.ArrayList;

import robot.RobotReal;
import robot.serial.SerialConnexion;
import smartMath.Vec2;
import utils.Log;
import utils.Config;
import container.Service;
import exceptions.serial.SerialConnexionException;

/**
 * Classe qui gère la balise laser
 * @author pf
 * @author clément
 *
 */

public class LaserCardWrapper implements Service {

	// Dépendances
	private Log log;
	private SerialConnexion serie;
	private RobotReal robotvrai;

	private Beacon[] beacons;

	public LaserCardWrapper(Config config, Log log, SerialConnexion serie, RobotReal robotvrai)
	{
		this.log = log;
		this.serie = serie;
		this.robotvrai = robotvrai;

		beacons = new Beacon[2]; //balises
		beacons[0] = new Beacon(0, false);
		beacons[1] = new Beacon(1, false);

	}

	/**
	 * Indique les balises considérées comme opérationnelle pour le match
	 * @return la liste des balises considérées comme opérationnelle pour le match
	 */
	public ArrayList<Beacon> activeBeacons() //balises_actives
	{
		ArrayList<Beacon> out = new ArrayList<Beacon>();
		for(Beacon b: beacons)
			if(b.active)
				out.add(b);
		return out;
	}

	/**
	 * Indique les balises considérées comme non opérationnelle pour le match
	 * @return
	 */
	public ArrayList<Beacon> ignoredBeacons()//balises_ignorees()
	{
		ArrayList<Beacon> out = new ArrayList<Beacon>();
		for(Beacon b: beacons)
			if(!b.active)
				out.add(b);
		return out;
	}

	/**
	 * Allumer le moteur et les lasers
	 */
	public void turnOn()//allumer()
	{
		try {
			serie.communiquer("motor_on", 0);
			serie.communiquer("laser_on", 0);
		} catch (SerialConnexionException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Eteindre le moteur et les lasers
	 */
	public void eteindre()
	{
		try {
			serie.communiquer("motor_off", 0);
			serie.communiquer("laser_off", 0);
		} catch (SerialConnexionException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Ping chaque balise et vérifie celles qui sont connectées
	 * @return nbeacons_ok : nombre de balises actives
	 */
	public int checkConnectedBeacons()//verifier_balises_connectes()
	{
		int beacons_ok = 0;//balises_ok
		for(Beacon b: beacons)
			try {
				if(ping_beacon(b.id))
				{
					if(!b.active)
					{
						b.active = true;
						log.debug("balise n°" + Integer.toString(b.id) + " répondant au ping", this);
					}
					beacons_ok++;
				}
				else
				{
					//log.warning("balise n°"+b.id+" ne répond pas.", this);
				}
			} catch (SerialConnexionException e) {
				e.printStackTrace();
			}
		return beacons_ok;
	}

	/**
	 * Ping une balise
	 * @param id
	 * @return
	 * @throws SerialException 
	 */
	private boolean ping_beacon(int id) throws SerialConnexionException
	{
		String[] ping = serie.communiquer("ping_all", beacons.length);	    
		return !ping[id].equals("aucune réponse");
	}

	/**
	 * Récupère la fréquence actuelle du moteur
	 * @return
	 */
	private float motorFrequency()//frequence_moteur()
	{
		try {
			String[] reponse = serie.communiquer("freq", 1);
			//System.out.println(reponse[0]);
			return Float.parseFloat(reponse[0]);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return 18; // TODO valeur normale
		}

	}
	/**
	 * Récupère la valeur (rayon, angle) d'une balise
	 * Et renvoie la position relative de la balise dans le système de coordonnées du laser.
	 * @param id
	 * @return point
	 * @throws SerialException 
	 */
	public Vec2 position_balise_relative(int id) throws SerialConnexionException
	{
		String chaines[] = {"value", Integer.toString(id)};
		String[] response = serie.communiquer(chaines, 3);

		if(response[0].equals("NO_RESPONSE") || response[1].equals("NO_RESPONSE")
				|| response[0].equals("OLD_VALUE") || response[1].equals("OLD_VALUE")
				|| response[0].equals("UNVISIBLE") || response[1].equals("UNVISIBLE"))
			return null;

		// Fréquence actuelle du moteur
		float freq = motorFrequency();

		// Valeur de la distance, sur l'échelle du timer 8 bit
		float timer = Float.parseFloat(response[0]);

		// Délai du passage des deux lasers, en seconde
		float delai = 128 * timer / 20000000;

		// Calcul de la distance (en mm)
		float gap_laser = 35;
		float theta = (float) (delai * freq * 2 * Math.PI);

		if(theta == 0)
		{
			log.warning("Division par zéro dans le calcul d'angle : freq = "+Float.toString(freq)+", delai = "+Float.toString(delai), this);
			return null;
		}

		int distance = (int) (gap_laser / Math.sin(theta / 2));

		// Angle
		float angle = Float.parseFloat(response[1]);

		// Changement dans le repère de la table
		Vec2 point = new Vec2((int)(distance * Math.cos(angle)), (int)(distance * Math.sin(angle)));
		return point;
	}

	/**
	 * Récupère la valeur (rayon, angle) d'une balise
	 * @param id
	 * @return
	 * @throws SerialException 
	 */
	public Vec2 position_balise(int id) throws SerialConnexionException
	{
		String chaines[] = {"value", Integer.toString(id)};
		String[] response = serie.communiquer(chaines, 3);

		if(response[0].equals("NO_RESPONSE") || response[1].equals("NO_RESPONSE")
				|| response[0].equals("OLD_VALUE") || response[1].equals("OLD_VALUE")
				|| response[0].equals("UNVISIBLE") || response[1].equals("UNVISIBLE"))
			return null;

		// Fréquence actuelle du moteur
		float freq = motorFrequency();

		// Valeur de la distance, sur l'échelle du timer 8 bit
		float timer = Float.parseFloat(response[0]);

        // Délai du passage des deux lasers, en seconde
        float delai = 128 * timer / 20000000;
        
        // Calcul de la distance (en mm)
        float gap_laser = 35;
        float theta = (float) (delai * freq * 2 * Math.PI);

        if(theta == 0)
        {
            log.warning("Division par zéro dans le calcul d'angle : freq = "+Float.toString(freq)+", delai = "+Float.toString(delai), this);
            return null;
        }

        int distance = (int) (gap_laser / Math.sin(theta / 2));
        // Angle
        float angle = Float.parseFloat(response[1]);

        // Changement dans le repère de la table
        Vec2 point = robotvrai.getPosition();
        double orientation = robotvrai.getOrientation();
        
        point.plus(new Vec2((int)(distance * Math.cos(angle + orientation)), (int)(distance * Math.sin(angle + orientation))));
        return point;
	}
	/**
	 * Fonction que pour les tests
	 * 
	 * @param id
	 * @return distance entre le laser et la balise
	 * @throws SerialException
	 */
	/*
	public float recupereRayon(int id) throws SerialConnexionException
	{
		String chaines[] = {"value", Integer.toString(id)};
		String[] reponse = serie.communiquer(chaines, 3);

		if(reponse[0].equals("NO_RESPONSE") || reponse[1].equals("NO_RESPONSE")
				|| reponse[0].equals("OLD_VALUE") || reponse[1].equals("OLD_VALUE")
				|| reponse[0].equals("UNVISIBLE") || reponse[1].equals("UNVISIBLE"))
			return null;

		// Fréquence actuelle du moteur
		float freq = frequence_moteur();

		// Valeur de la distance, sur l'échelle du timer 8 bit
		float timer = Float.parseFloat(reponse[0]);

        // Délai du passage des deux lasers, en seconde
        float delai = 128 * timer / 20000000;
        
        // Calcul de la distance (en mm)
        float ecart_laser = 35;
        float theta = (float) (delai * freq * 2 * Math.PI);

        if(theta == 0)
        {
            log.warning("Division par zéro dans le calcul d'angle : freq = "+Float.toString(freq)+", delai = "+Float.toString(delai), this);
            return null;
        }

        int distance = (int) (ecart_laser / Math.sin(theta / 2));
        
        return distance;
	}
	*/
	/**
	 * Fonction que pour les tests
	 * 
	 * @param id
	 * @return angle 
	 */
	/*
	public float retournerAngleAbsolue(int id)
	{
		String chaines[] = {"value", Integer.toString(id)};
		String[] reponse = serie.communiquer(chaines, 3);
		float angle = Float.parseFloat(reponse[1]);
		return angle;
	}
	*/
	/**
	 * Vérifie si les données des balises actives sont cohérentes en début de match
	 * On calcule la moyenne et l'écart-type et ça aide pour voir la cohérence
	 */
	public void checkBeaconConsistency()//verifier_coherence_balise()
	{
		// Nombre d'essais pour les calculs
		int trials = 10;

		ArrayList<Beacon> beacons_active = activeBeacons();
		for(Beacon b : beacons_active)
		{
			float mean = 0; //moyenne
			ArrayList<Float> valeurs = new ArrayList<Float>();
			float standard_deviation = 0; //écart-type
			int n = 0; // nombre de réponses valides de la série 

			for(int i = 0; i < trials; i++)
			{
				try {
					String strings[] = {"value", Integer.toString(b.id)};
					String[] response = serie.communiquer(strings, 3);
					//System.out.println(reponse[0]/* + " " + reponse[1] + " " + reponse[2]*/);
					if(!(response[0].equals("NO_RESPONSE") || response[1].equals("NO_RESPONSE")
							|| response[0].equals("OLD_VALUE") || response[1].equals("OLD_VALUE")
							|| response[0].equals("UNVISIBLE") || response[1].equals("UNVISIBLE")))
					{
						float angle = Float.parseFloat(response[1]);
						n++;
						mean += angle;
						valeurs.add(angle);
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}

			// Calcul de la moyenne
			if(n > 0)
			{
				mean /= (float) n;

				// Calcul de l'écart type
				for(Float v: valeurs)
					standard_deviation += (v - mean) * (v - mean);
				standard_deviation /= (float) n;

			}

			// Vérification de la cohérence
			if(n < trials / 2 || standard_deviation > 1)
			{
				log.critical("balise n°"+Integer.toString(b.id)+" ignorée pendant le match, valeurs renvoyées incohérentes (valeurs reçues = "+Integer.toString(n)+" / "+Integer.toString(trials)+", angle moyen = "+Float.toString(mean)+", écart-type = "+Float.toString(standard_deviation)+")", this);
				b.active = false;
			}
			else
				log.debug("balise n°"+Integer.toString(b.id)+" renvoie des valeurs cohérentes (valeurs reçues = "+Integer.toString(n)+" / "+Integer.toString(trials)+", angle moyen = "+Float.toString(mean)+", écart-type = "+Float.toString(standard_deviation)+")", this);				
		}


	}

	public void updateConfig()
	{
	}
}
