
package tests;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.junit.Test;

import exceptions.ConfigPropertyNotFoundException;
import exceptions.ExecuteException;
import exceptions.serial.SerialConnexionException;

/**
 * classe des tests d'exceptions
 */

public class JUnit_Exceptions extends JUnit_Test 
{	
	//@Test 
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
			log.debug(exceptionAsString);
		}
	}
	
	@Test public void comparaison()
	{
		/*try {
			throw new UnableToMoveException(new Vec2(0,0), UnableToMoveReason.OBSTACLE_DETECTED);
		}
		catch (UnableToMoveException e)
		{*/
		SerialConnexionException e1 = new SerialConnexionException();

			try 
			{
				throw new ExecuteException(new SerialConnexionException());
			} 
			catch (ExecuteException e2) 
			{
				if (e2.compareInitialException(e1))
				//if(e2.getExceptionThrownByExecute().getClass().equals(e1.getClass()))
					log.debug("Wééé !");
				else {
					log.debug("Pouet !");

				}
			}
			
		//}
		
	}
	
	public void buggingFunction() throws ConfigPropertyNotFoundException
	{
		config.getProperty("Les_Pinguins_Vaincrons");
	}
}
