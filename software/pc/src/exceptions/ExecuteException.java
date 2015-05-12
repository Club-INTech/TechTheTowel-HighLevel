package exceptions;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExecuteException extends Exception
{
	/**
	 * Exception levée par un Execute raté
	 * @author Théo
	 */
	
	// La raison du probleme dans l'execute
	Exception exception;
	
	public ExecuteException(Exception e)
	{
		super();
		exception=e;
	}
	
	public ExecuteException(String m, Exception e)
	{
		super(m);
		exception=e;
	}
	
	public String logStack()
	{
		StringWriter sw = new StringWriter();
		this.printStackTrace(new PrintWriter(sw));
		
		String exceptionAsString = sw.toString();	
		exceptionAsString = exceptionAsString.replaceAll("(\r\n|\n\r|\r|\n)", " -> ");
		
		return exceptionAsString;
	}
	
	public Exception getExceptionThrownByExecute()
	{
		return exception;
	}
}
