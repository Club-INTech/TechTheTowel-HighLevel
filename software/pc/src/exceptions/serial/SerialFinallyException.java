package exceptions.serial;

import java.io.PrintWriter;
import java.io.StringWriter;

public class SerialFinallyException extends Exception {

private static final long serialVersionUID = 1826278884421114631L;
	
	public SerialFinallyException()
	{
		super();
	}
	
	public SerialFinallyException(String m)
	{
		super(m);
	}

	public SerialFinallyException(Throwable cause) 
	{
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public SerialFinallyException(String message, Throwable cause) 
	{
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public SerialFinallyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
	{
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
