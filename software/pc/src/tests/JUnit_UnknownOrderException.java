package tests;

import enums.ServiceNames;
import exceptions.serial.SerialConnexionException;
import org.junit.Before;
import org.junit.Test;
import robot.serial.SerialConnexion;


/**
 * Teste l'exception UnknownOrderException qui considère l'absence de véritables ordres inconnus
 * au sein des communications bas-niveau vers haut-niveau :
 * si l'interprétation dun message du haut-niveau à la série échoue à la réception par le haut-niveau,
 * celui-ci lève une SerialConnexionException (considérant que la liaison fait défaut à la communication).
 * 
 * Si le test échoue, s'intéresser d'abord à la méthode "exception.UnknownOrderException.verifyConnexion".
 * 
 * @author Cérézas
 *
 */
public class JUnit_UnknownOrderException extends JUnit_Test
{
	private SerialConnexion serialTest;
	
	@Test
	public void main() throws SerialConnexionException
	{
		try
		{
			//ordre "uoe" géré dans le bas-niveau qui dans le cadre du teste renvoie "Une fraise"
			//(en plus de l'acquittement "_" non comptabilisé dans le nombre de réponses attendues)
			serialTest.communiquer("uof", 1);
		}
		catch(SerialConnexionException e)
		{
			e.printStackTrace();
		}
	}
	
	//Instanciation de la classe SerialConnexion afin d'exécuter sa méthode "communiquer"
	@Before
	public void attributes() throws Exception
	{
		try
		{
			serialTest = (SerialConnexion) container.getService(ServiceNames.STM_CARD);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
