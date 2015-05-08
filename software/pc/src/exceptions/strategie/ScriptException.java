package exceptions.strategie;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Exception levée par le gestionnaire des connexions séries
 * @author pf
 *
 */
public class ScriptException extends Exception
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1826278884421114631L;

	public ScriptException()
	{
		super();
	}
	
	public ScriptException(String m)
	{
		super(m);
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
