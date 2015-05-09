package exceptions;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * exception levee si aucun chemin n'est trouve par le pathfinding
 * @author Etienne
 *
 */
public class PathNotFoundException extends Exception
{
	private static final long serialVersionUID = -7968975910907981869L;
	
	public PathNotFoundException()
	{
		super();
	}
	
	public PathNotFoundException(String m)
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
