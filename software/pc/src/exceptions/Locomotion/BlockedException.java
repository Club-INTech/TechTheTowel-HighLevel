package exceptions.Locomotion;

import java.io.PrintWriter;
import java.io.StringWriter;


/**
 * Exception lancée en cas de blocage mécanique du robot (les moteurs forcent sans que les codeuses ne tournent pas)
 * @author pf, marsu
 *
 */
public class BlockedException extends Exception
{
	
	private static final long serialVersionUID = -8074280063169359572L;

	public BlockedException()
	{
		super();
	}
	
	public BlockedException(String m)
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
