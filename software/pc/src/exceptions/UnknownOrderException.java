package exceptions;

import java.io.PrintWriter;
import java.io.StringWriter;
import robot.serial.SerialConnexion;

public class UnknownOrderException extends Exception
{
	
	private static final long serialVersionUID = 7149002583820463587L;

	/**
	 * canCommunicate à true signifie qu'on peut renvoyer l'ordre au bas niveau.
	 * Annule les boucles infinies dans la méthode communiquer de la classe SerialConnexion.
	 * Son implémentation statique gère les différentes intanciations,
	 * sachant que l'exception est levée dans un bloc synchronized.
	**/
	public static boolean canCommunicate = true;
	
	/**
	 * Sauvegarde du message auquel le bas niveau répond "Ordre inconnu"
	 */
	public String[] serialOrder;
	
	/**
	 * Sauvegarde de l'instance de la classe SerialConnexion qui lève l'exception
	 */
	private SerialConnexion serialTest;
	
	/**
	 * Exception levée par la méthode "communiquer" de la classe SerialConnexion
	 * lorsque le bas niveau répond un "Ordre inconnu" (former "Ordre innonu")
	 * @author Cérézas
	 * @param order : dernier ordre envoyé au bas niveau, auquel le bas niveau a répondu "Ordre inconnu" (former "Ordre innonu")
	 * @param serialTest : l'instance de la classe SerialConnexion qui lève l'exception est envoyée en argument pour "verifyConnexion"
	 */
	public UnknownOrderException(String[] order, SerialConnexion serialTest)
	{
		super();
		this.serialOrder=order;
		this.serialTest=serialTest;
	}
	
	/**
	 *	ATTENTION! verifyConnexion inverse le bouléen canCommunicate ;
		car si cette méthode est appelée, la communication de l'ordre est potentiellement répétée
		(cf. SerialConnexion.communiquer)
	 * @return true si la connexion série semble effective, false sinon
	 */
	public boolean verifyConnexion()
	{
		canCommunicate=!canCommunicate;
		//ping de la série via la méthode éponyme de SerialConnexion
		String ping = serialTest.ping();
		//qui doit renvoyer "0"
		return (ping != null && ping.equals("0"));
	}
	
	public String logStack()
	{
		StringWriter sw = new StringWriter();
		this.printStackTrace(new PrintWriter(sw));
		
		String exceptionAsString = sw.toString();	
		exceptionAsString = exceptionAsString.replaceAll("(\r\n|\n\r|\r|\n)", " -> ");
		exceptionAsString += "\n"+"Ordre à problème :	";
		
		//sauvegarde de l'ordre qui lève l'exception dans le traçage de l'erreur
		for (String send : serialOrder)
		{
			exceptionAsString += " "+send;
		}	
		
		return exceptionAsString;
	}
}
