package exceptions;

import exceptions.serial.SerialConnexionException;
import java.io.PrintWriter;
import java.io.StringWriter;
import robot.serial.SerialConnexion;
import utils.Log;

public class UnknownOrderException extends Exception
{
	
	private static final long serialVersionUID = 7149002583810463587L;

	/**
	 * reCall à true signifie qu'on peut renvoyer l'ordre au bas niveau
	 */
	private static boolean orderNotRecalledYet = true;
	
	private String serialOrder;
	
	private String serialMess;
	
	private SerialConnexion serialTest;
	
	/**
	 * Exception levée par les scripts lorsque le bas niveau reçoit un ordre non référencé
	 * @author Cérézas
	 *
	 */
	public UnknownOrderException(String order, String message, SerialConnexion serialTest)
	{
		super();
		this.serialOrder=order;
		this.serialMess=message;
		this.serialTest=serialTest;
	}
	
	public boolean verifyConnexion()
	{
		String ping = serialTest.ping();
		if (ping != null && ping.equals("0") && orderNotRecalledYet)
		{
			orderNotRecalledYet=false;
			reCall(serialOrder, serialTest);
			orderNotRecalledYet=true;
			return true;
		}
		else return false;
	}
	
	public void reCall(String ordre, SerialConnexion serialTest)
	{
		//TODO
	}
	
	
	public String logStack()
	{
		StringWriter sw = new StringWriter();
		this.printStackTrace(new PrintWriter(sw));
		
		String exceptionAsString = sw.toString();	
		exceptionAsString = exceptionAsString.replaceAll("(\r\n|\n\r|\r|\n)", " -> ");
		exceptionAsString+="\n"+this.serialMess;
		
		
		return exceptionAsString;
	}
}
