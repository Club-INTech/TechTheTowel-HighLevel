/**
 * 
 */
package exceptions;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 
 * Exception lancée lorsque l'on demande une information a un Service dont le type ne permet pas de fournir l'information demandée
 * @author karton
 *
 */
public class ServiceTypeException extends Exception
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8462580190897214226L;

	/**
	 * 
	 */
	public ServiceTypeException() 
	{
		super();
	}

	/**
	 * @param message
	 */
	public ServiceTypeException(String message) 
	{
		super(message);
	}

	/**
	 * @param cause
	 */
	public ServiceTypeException(Throwable cause) 
	{
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ServiceTypeException(String message, Throwable cause) 
	{
		super(message, cause);
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public ServiceTypeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) 
	{
		super(message, cause, enableSuppression, writableStackTrace);
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
