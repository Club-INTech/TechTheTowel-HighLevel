package exceptions;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Exception levée par les threads
 * @author pf
 *
 */
public class ThreadException extends Exception
{

	private static final long serialVersionUID = 3551305502065045527L;
	
	/**
	 * Exception levée par les threads
	 * @author pf
	 *
	 */
	public ThreadException()
	{
		super();
	}
	
	public ThreadException(String m)
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
