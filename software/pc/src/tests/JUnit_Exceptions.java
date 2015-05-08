
package tests;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.junit.Test;

import com.sun.rmi.rmid.ExecOptionPermission;

import exceptions.ConfigPropertyNotFoundException;
import utils.Log;

/**
 * classe des tests d'exceptions
 */

public class JUnit_Exceptions extends JUnit_Test 
{	
	@Test 
	public void printStack()
	{
		try 
		{
			buggingFunction();
		} 
		catch (Exception e) 
		{
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			String exceptionAsString = sw.toString();	
			
			exceptionAsString = exceptionAsString.replaceAll("(\r\n|\n\r|\r|\n)", " -> ");
			log.debug(exceptionAsString, this);
		}
	}
	
	public void buggingFunction() throws ConfigPropertyNotFoundException
	{
		config.getProperty("Les_Pinguins_Vaincrons");
	}
}
