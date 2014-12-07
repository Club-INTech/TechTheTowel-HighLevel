package utils;

import robot.RobotReal;
import container.Service;

// TODO: Auto-generated Javadoc
/**
 * Service qui permettra de faire un checkup du robot avant le match.
 *
 * @author pf
 * (marsu) : Cette année on l'utilisera, promi !
 */

public class CheckUp implements Service 
{

	/** The log. */
	private Log log;
	
	/** The robotvrai. */
	private RobotReal robotvrai;
	
	/**
	 * Instantiates a new check up.
	 *
	 * @param log the log
	 * @param robotvrai the robotvrai
	 */
	public CheckUp(Log log, RobotReal robotvrai)
	{
		this.robotvrai = robotvrai;
		this.log = log;
	}
	
	// TODO check-up du robot
	/**
	 * Lancer.
	 */
	public void lancer()
	{
		// Par exemple
		log.debug("Lancement d'un check-up", this);
		try
		{
		robotvrai.moveLengthwise(100);
		}
		catch(Exception e)
		{
			log.warning(e, this);
		}
	}
	
	/* (non-Javadoc)
	 * @see container.Service#updateConfig()
	 */
	public void updateConfig()
	{
	}
}
