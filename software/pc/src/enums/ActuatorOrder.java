package enums;

/**
 * Protocole des actionneurs.
 * contient pour chaque actionneur le nom des consignes java, la chaine a transmetttre a la carte actionneurs et la durée que cette action prends 
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
	
	// Consignes pour les bras chargés de prendre les poissons
	FISHING_POSITION_LEFT("fpl"),   // bras aimanté gauche baissé pour récupérer les poissons
	MIDDLE_POSITION_LEFT("mpl"),    // bras aimanté gauche en position medium 
	LEFT_MAGNET_DOWN("lmd"),		// bras aimanté gauche pour lâcher les poissons
	LEFT_MAGNET_UP("lmu"),			// bras aimanté gauche rangé
	LEFT_FINGER_DOWN("lfd"),		// doigt gauche en position basse
	LEFT_FINGER_UP("lfu"),			// doigt gauche rangé
	
	FISHING_POSITION_RIGHT("fpr"),	// bras aimanté droit baissé pour récupérer les poissons
	MIDDLE_POSITION_RIGHT("mpr"),	// bras aimanté droit en position medium
	RIGHT_MAGNET_DOWN("rmd"),		// bras aimanté droit pour lâcher les poissons
	RIGHT_MAGNET_UP("rmu"),			// bras aimanté droit rangé
	RIGHT_FINGER_DOWN("rfd"),		// doigt droit en position basse
	RIGHT_FINGER_UP("rfu"),			// doigt droit rangé
	
	// Consigne pour remettre les bras en position initiale
	ARM_INIT("aif"),
	
	//Consigne d'ouverture du parasol
	//TODO : mesurer la durée de déploiement du parasol, implémenter la commande "ps" au bas niveau
	OPEN_PARASOL("ps",4),
	
	STOP("stop");

	/**
	 *  chaine de caractère envoyé au travers de la liaison série
	 */
	private String serialOrder;

	/** duurée de l'action en milisecondes */
	private int duration;

	/**
	 * Construit un ordre pur un actionneur
	 * on suppose que son temps d'exécution est d'une seconde
	 * @param serialString la chaine de caractère a envoyer a la cartea actionnneurs
	 */
	private ActuatorOrder(String serialString)
	{
		this.serialOrder = serialString;
		this.duration = 700;	// valeur par défaut de la durée de mouvement d'un actionneur
	}

	/**
	 * Construit un ordre pour un actionneur avec le temps d'exécution spécifié 
	 * @param serialString la chaine de caractère a envoyer a la cartea actionnneurs
	 */
	private ActuatorOrder(String serialString, int duration)
	{
		this.serialOrder = serialString;
		this.duration = duration;
	}
	
	/**
	 * Retrouve la chaine de caractère a envoyer par la série a la carte actionneur pour qu'elle effectue cet ordre
	 * @return la chaine de caractère a envoyer par la série a la carte actionneur
	 */
	public String getSerialOrder()
	{
		return serialOrder;
	}

	/**
	 * Renvois la durée de l'action
	 * @return durée d'exécution de l'action
	 */
	public int getDuration()
	{
		return duration;
	}
}
