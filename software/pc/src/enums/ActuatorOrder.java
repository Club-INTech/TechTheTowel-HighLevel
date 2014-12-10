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
	
	
	// Consignes dépose-tapis
	LEFT_CARPET_DROP("ptg"),
	RIGHT_CARPET_DROP("ptd"),
	LEFT_CARPET_FOLDUP("rtg"),
	RIGHT_CARPET_FOLDUP("rtd"),
	
	// Consignes ferme-clap
	HIGH_LEFT_CLAP("cgh"),
	HIGH_RIGHT_CLAP("cdh"),
	MID_LEFT_CLAP("cgm"),
	MID_RIGHT_CLAP("cdm"),
	LOW_LEFT_CLAP("cgb"),
	LOW_RIGHT_CLAP("cdb"),
	
	// Consignes guide-plot ouvrable
	OPEN_RIGHT_GUIDE("ogd"),
	OPEN_LEFT_GUIDE("ogg"),
	MID_RIGHT_GUIDE("gdi"),
	MID_LEFT_GUIDE("ggi"),
	CLOSE_RIGHT_GUIDE("fgd"),
	CLOSE_LEFT_GUIDE("fgg"),
	
	// Consignes de l'ascenseur monte-plot
	ELEVATOR_HIGH("ah"),
	ELEVATOR_STAGE("ae"),
	ELEVATOR_LOW("ab"),
	ELEVATOR_GROUND("as"),
	
	// Consignes machoires de l'ascenseur monte-plot
	ELEVATOR_OPEN_JAW("om"),
	ELEVATOR_CLOSE_JAW("fm"),
	ELEVATOR_OPEN_JAW_RIGHT("omd"),
	ELEVATOR_OPEN_JAW_LEFT("omg"),
	ELEVATOR_CLOSE_JAW_RIGHT("fmd"),
	ELEVATOR_CLOSE_JAW_LEFT("fmg"),
	
	// Consignes Bras ramène-goblelets
	ARM_RIGHT_OPEN("obd"),
	ARM_LEFT_OPEN("obg"),
	ARM_RIGHT_CLOSE("fbd"),
	ARM_LEFT_CLOSE("fbg"),
	ARM_RIGHT_OPEN_SLOW("obdl"),
	ARM_LEFT_OPEN_SLOW("obgl"),
	ARM_RIGHT_CLOSE_SLOW("fbdl"),
	ARM_LEFT_CLOSE_SLOW("fbdl");


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
		this.duration = 1000;
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
