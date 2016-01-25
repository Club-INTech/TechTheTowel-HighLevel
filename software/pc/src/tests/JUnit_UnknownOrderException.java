package tests;

import enums.ServiceNames;
import exceptions.serial.SerialConnexionException;
import org.junit.Before;
import robot.serial.SerialConnexion;
import utils.Log;

public class JUnit_UnknownOrderException extends JUnit_Test
{
	private int baudrate = 115200;
	private SerialConnexion serialTest;
	
	public void main() throws SerialConnexionException
	{
		try
		{
			serialTest.communiquer("uoe", 2);
		}
		catch(SerialConnexionException e)
		{
			e.printStackTrace();
		}
	}
	
	@Before
	public void attributes() throws Exception
	{
		serialTest = new SerialConnexion((Log)container.getService(ServiceNames.LOG), "test_UOE");
		serialTest.initialize("/dev/ttyUSB0", baudrate);
	}

}
