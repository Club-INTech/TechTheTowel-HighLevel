package utils;

/**
 * Classe qui fournit juste un sleep sans try/catch.
 *
 * @author pf, marsu
 */

public class Sleep 
{

	// Constructeur privé car cette classe n'a qu'une méthode statique
	/**
	 * Instantiates a new sleep.
	 */
	private Sleep()
	{
	}
	
	
	/**
	 * Fait attendre le programme.
	 * Attention: Le programme va vraiment attendre ! 
	 * Si on veut juste une estimation du temps que cela va prendre d'attendre ce temps là (sic), utiliser RobotC.sleep, qui en fonction de son appel
	 * sur Robot ou RobotChrono, déclenchera vraiment une attente ou une simple incrémentation du chronomètre
	 * @param delay durée en ms d'attente
	 */
	public static void sleep(long delay)
	{
		try
		{
			// fait attendre le thread appelant cette méthode
			Thread.sleep(delay);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
	
}
