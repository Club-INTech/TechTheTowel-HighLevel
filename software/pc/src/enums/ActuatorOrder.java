package enums;

/**
 * Protocole des actionneurs
 * @author pf, marsu
 *
 */

public enum ActuatorOrder
{

	// Syntaxe: NOM_METHODE("protocole_serie")

	BOUGER_BRAS("bb"),
	DEPOSER_TAPIS("dt");	// ce sont des exemples, vous pouvez les virer

	/**
	 *  chaine de caractère envoyé au travers de la liaison série
	 */
	private String serialOrder;
	
	/**
	 * Construit un ordre pur un actionneur
	 * @param serialString la chaine de caractère a envoyer a la cartea actionnneurs
	 */
	private ActuatorOrder(String serialString)
	{
		this.serialOrder = serialString;
	}
	
	/**
	 * Retrouve la chaine de caractère a envoyer par la série a la carte actionneur pour qu'elle effectue cet ordre
	 */
	public String getSerialOrder()
	{
		return serialOrder;
	}
}
