package exceptions;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Exception levée par le container
 * @author pf
 */
public class ContainerException extends Exception
{

	private static final long serialVersionUID = -960091158805232282L;

	/**
	 * Exception levée par le container
	 * @author pf
	 */
	public ContainerException()
	{
		super();
	}
	
	public ContainerException(String m)
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
