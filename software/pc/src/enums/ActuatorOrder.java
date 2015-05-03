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
	LEFT_CARPET_DROP("ptg",312),
	RIGHT_CARPET_DROP("ptd",312),
	LEFT_CARPET_FOLDUP("rtg",375),
	RIGHT_CARPET_FOLDUP("rtd",375),
	
	// Consignes ferme-clap
	HIGH_LEFT_CLAP("cgh",375),
	HIGH_RIGHT_CLAP("cdh",375),
	MID_LEFT_CLAP("cgm",218),
	MID_RIGHT_CLAP("cdm",218),
	LOW_LEFT_CLAP("cgb",375),
	LOW_RIGHT_CLAP("cdb",375),
	
	// Consignes guide-plot ouvrable
	OPEN_RIGHT_GUIDE("ogd",265),
	OPEN_LEFT_GUIDE("ogg",265),
	MID_RIGHT_GUIDE("gdi",125),
	MID_LEFT_GUIDE("ggi",125),
	CLOSE_RIGHT_GUIDE("fgd",265),
	CLOSE_LEFT_GUIDE("fgg",265),
	
	// Consignes de l'ascenseur monte-plot
	ELEVATOR_HIGH("ah"),
	ELEVATOR_STAGE("ae"),
	ELEVATOR_ON_STAGE("ase"),
	ELEVATOR_LOW("ab"),
	ELEVATOR_GROUND("as"),
	
	// Consignes machoires de l'ascenseur monte-plot
	ELEVATOR_OPEN_JAW("om",615),		// mesures de la machoires effectuées a 515ms/mouvments, mais cela génère des bugs, on prends 100ms de sécurité
	ELEVATOR_CLOSE_JAW("fm",615),
	ELEVATOR_OPEN_JAW_RIGHT("omd",615),
	ELEVATOR_OPEN_JAW_LEFT("omg",615),
	ELEVATOR_CLOSE_JAW_RIGHT("fmd",615),
	ELEVATOR_CLOSE_JAW_LEFT("fmg",615),
	
	// Consignes Bras ramène-goblelets
	ARM_RIGHT_OPEN("obd",781),
	ARM_LEFT_OPEN("obg",781),
	ARM_RIGHT_CLOSE("fbd",781),
	ARM_LEFT_CLOSE("fbg",781),
	ARM_RIGHT_OPEN_SLOW("obdl",1250),
	ARM_LEFT_OPEN_SLOW("obgl",1250),
	ARM_RIGHT_CLOSE_SLOW("fbdl",1250),
	ARM_LEFT_CLOSE_SLOW("fbgl",1250),
	ARM_RIGHT_MIDDLE("mbd",468),
	ARM_LEFT_MIDDLE("mbg",468);

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
		this.duration = 700;	// valaur par défaut de la durée de mouvement d'un actionneur
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
