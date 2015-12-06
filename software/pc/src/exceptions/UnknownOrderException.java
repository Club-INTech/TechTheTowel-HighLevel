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
		super();
	}
	
	public UnknownOrderException(String message)
	{
		super(message);
	}
	
	public String logStack()
	{
		StringWriter sw = new StringWriter();
		this.printStackTrace(new PrintWriter(sw));
		
		String exceptionAsString = sw.toString();	
		exceptionAsString = exceptionAsString.replaceAll("(\r\n|\n\r|\r|\n)", " -> ");
		
		return exceptionAsString;
	}
}
