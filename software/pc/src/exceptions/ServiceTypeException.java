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
	public ServiceTypeException() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public ServiceTypeException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public ServiceTypeException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ServiceTypeException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public ServiceTypeException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
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
