package exceptions;

import java.io.PrintWriter;
import java.io.StringWriter;
import robot.serial.SerialConnexion;

public class UnknownOrderException extends Exception
{
	
	private static final long serialVersionUID = 7149002583810463587L;

	/**
	 * canCommunicate à true signifie qu'on peut renvoyer l'ordre au bas niveau
	 * Cette implémentation annule les boucles infinies dans la méthode communiquer de la classe SerialConnexion
	**/
	public static boolean canCommunicate = true;
	
	/**
	 * Sauvegarde du message auquel le bas niveau répond "Ordre inconnu"
	 */
	public String[] serialOrder;
	
	private SerialConnexion serialTest;
	
	/**
	 * Exception levée par les scripts lorsque le bas niveau reçoit un ordre non référencé
	 * @author Cérézas
	 *
	 */
	public UnknownOrderException(String[] order, SerialConnexion serialTest)
	{
		super();
		this.serialOrder=order;
		this.serialTest=serialTest;
	}
	
	public boolean verifyConnexion()
	{
		canCommunicate=!canCommunicate;
		String ping = serialTest.ping();
		return (ping != null && ping.equals("0"));
	}
		
	public String logStack()
	{
		StringWriter sw = new StringWriter();
		this.printStackTrace(new PrintWriter(sw));
		
		String exceptionAsString = sw.toString();	
		exceptionAsString = exceptionAsString.replaceAll("(\r\n|\n\r|\r|\n)", " -> ");
		exceptionAsString += "\n"+"Ordre à problème";
		
		for (String send : serialOrder)
		{
			exceptionAsString += "\n"+send;
		}	
		
		return exceptionAsString;
	}
}
