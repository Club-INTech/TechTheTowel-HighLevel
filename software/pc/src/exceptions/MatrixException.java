package exceptions;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Exception levée en cas de calculs matriciels impossibles
 * @author pf
 *
 */

public class MatrixException  extends Exception
{

	private static final long serialVersionUID = -7968975910907981869L;

	public MatrixException()
	{
		super();
	}
	
	public MatrixException(String m)
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
