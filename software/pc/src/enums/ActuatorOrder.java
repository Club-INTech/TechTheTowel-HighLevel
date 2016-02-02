package enums;

/**
 * Protocole des actionneurs.
 * contient pour chaque actionneur le nom des consignes java, la chaine à transmetttre à la carte actionneurs et la durée que cette action prend
 * @author pf, marsu
 *
 */

public enum ActuatorOrder
{
	// Syntaxe: NOM_METHODE("protocole_serie") Cette syntaxe suppose que l'action prends une seconde pour s'exécuter
	// Syntaxe alternative: NOM_METHODE("protocole_serie", durée actions)
	// exemple : MOVE_FORWARD("av")
	
	// Consignes avancer / reculer
	MOVE_FORWARD("av"),
	MOVE_BACKWARD("rc"),
	TURN_RIGHT("td"),
	TURN_LEFT("tg"),
	
	//========================================================
	// Consignes pour les bras chargés de prendre les poissons
	//========================================================
	
	//!\ Remarque : il n'y a pas de "left" pour les actuators du bras gauche, on se contente d'appeler une des consignes neutres /!\
	//!\ La symétrie du jeu se chargera de déterminer si l'actuator concerné est le gauche ou le droit /!\
	
	/** Bras aimanté baissé pour récupérer les poissons */
	FISHING_POSITION("fpl"),
	
	/** Bras aimanté en position medium */
	MIDDLE_POSITION("mpl"),
	
	/**Bras aimanté pour lâcher les poissons*/
	MAGNET_DOWN("lmd"),
	
	/**Bras aimanté rangé*/
	MAGNET_UP("lmu"),
	
	/**Doigt en position basse*/
	FINGER_DOWN("lfd"),
	
	/**Doigt rangé*/
	FINGER_UP("lfu"),
	
	FISHING_POSITION_RIGHT("fpr"),	// bras aimanté droit baissé pour récupérer les poissons
	MIDDLE_POSITION_RIGHT("mpr"),	// bras aimanté droit en position medium
	RIGHT_MAGNET_DOWN("rmd"),		// bras aimanté droit pour lâcher les poissons
	RIGHT_MAGNET_UP("rmu"),			// bras aimanté droit rangé
	RIGHT_FINGER_DOWN("rfd"),		// doigt droit en position basse
	RIGHT_FINGER_UP("rfu"),			// doigt droit rangé
	
	/**Consigne pour remettre les bras en position initiale*/
	ARM_INIT("aif"),

	/** Ouverture des portes */
	OPEN_DOOR("odr",3000),
	OPEN_DOOR_LEFT("odl",3000),

	/** Fermeture des portes */
	CLOSE_DOOR("cdl",3000),
	CLOSE_DOOR_RIGHT("cdr",3000),
	
	/** Arrêt des portes */
	STOP_DOOR_LEFT("sdl"),
	STOP_DOOR("sdr"),

	/** Lancement des axes */
	START_AXIS_LEFT("ral"),
	START_AXIS("rar"),

	/** Arret des axes */
	STOP_AXIS_LEFT("sal"),
	STOP_AXIS("sar"),

	STOP("stop");

	/**
	 *  chaine de caractère envoyée au travers de la liaison série
	 */
	private String serialOrder;

	/** duurée de l'action en millisecondes */
	private int duration;

	/**
	 * Construit un ordre pour un actionneur
	 * on suppose que son temps d'exécution est d'une seconde
	 * @param serialString la chaine de caractère à envoyer à la carte actionnneurs
	 */
	private ActuatorOrder(String serialString)
	{
		this.serialOrder = serialString;
		this.duration = 700;	// valeur par défaut de la durée de mouvement d'un actionneur
	}

	/**
	 * Construit un ordre pour un actionneur avec le temps d'exécution spécifié 
	 * @param serialString la chaine de caractère à envoyer à la carte actionnneurs
	 */
	private ActuatorOrder(String serialString, int duration)
	{
		this.serialOrder = serialString;
		this.duration = duration;
	}
	
	/**
	 * Retrouve la chaine de caractère a envoyer par la série a la carte actionneur pour qu'elle effectue cet ordre
	 * @return la chaine de caractère à envoyer par la série à la carte actionneur
	 */
	public String getSerialOrder()
	{
		return serialOrder;
	}

	/**
	 * Renvoie la durée de l'action
	 * @return durée d'exécution de l'action
	 */
	public int getDuration()
	{
		return duration;
	}
}
