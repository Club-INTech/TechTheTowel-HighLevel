package utils;

import container.Service;
import robot.RobotReal;

/**
 * Service qui permet de tester tout les actionneurs.
 * Ce système est prévu pour être aussi utilisé durant les 3 minutes de préparations avant le début du match
 *
 * @author pf, marsu
 */

public class CheckUp implements Service 
{

	/** Le système de log a utiliser pour écrire */
	private Log log;
	
	/** Le RobotReal a tester */
	private RobotReal robotReal;
	
	/**
	 * Le système de log a utiliser pour écrire 
	 *
	 * @param log Le système de log a utiliser pour écrires
	 * @param robotReal Le RobotReal a tester
	 */
	public CheckUp(Log log, RobotReal robotReal)
	{
		this.robotReal = robotReal;
		this.log = log;
	}
	
		/**
	 * Effectue un check-up du robot
	 */
	public void doCheckUp()
	{
		// Par exemple
		log.debug("Lancement d'un check-up");
		try
		{
		robotReal.moveLengthwise(100);
		}
		catch(Exception e)
		{
			log.warning(e);
		}
	}
	
	/* (non-Javadoc)
	 * @see container.Service#updateConfig()
	 */
	public void updateConfig()
	{
	}
}
