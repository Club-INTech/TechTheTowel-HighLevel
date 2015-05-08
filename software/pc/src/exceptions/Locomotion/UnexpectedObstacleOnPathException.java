package exceptions.Locomotion;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Exception levée en cas de détection d'un ennemi proche (par les capteurs, ultrason, infrarouge, etc.)
 * @author pf, marsu
 *
 */
public class UnexpectedObstacleOnPathException extends Exception
{

	private static final long serialVersionUID = -3791360446545658528L;

	public UnexpectedObstacleOnPathException()
	{
		super();
	}
	
	public UnexpectedObstacleOnPathException(String m)
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
