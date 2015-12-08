package exceptions;

import java.io.PrintWriter;
import java.io.StringWriter;

public class UnknownOrderException extends Exception
{
	//TODO
	private static final long serialVersionUID = 0L;
	
	/**
	 * Exception levée par les scripts lorsque le bas niveau reçoit un ordre non référencé
	 * @author Cérézas
	 *
	 */
	public UnknownOrderException()
	{
		//TODO étoffer avec un string contenant l'ordre série
		super();
	}
	
	public UnknownOrderException(String message)
	{
		super(message);
	}
	
	public String logStack()
	{
		// TODO afficher la commande série qui a causé l'erreur (+ retenter la commande au cas où il s'agit d'une erreur de la série)
		StringWriter sw = new StringWriter();
		this.printStackTrace(new PrintWriter(sw));
		
		String exceptionAsString = sw.toString();	
		exceptionAsString = exceptionAsString.replaceAll("(\r\n|\n\r|\r|\n)", " -> ");
		
		return exceptionAsString;
	}
}
