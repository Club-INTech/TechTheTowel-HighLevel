package enums;

import exceptions.ServiceTypeException;

/**
 * Enumération des différents services. Plus d'informations sur les services dans la classe Container.
 * @author pf
 *
 */
public enum ServiceNames
{
	 LOG,
	 CONFIG,
	 
	 TABLE,
	 
	 LOCOMOTION,
	 ROBOT_REAL,
	 HOOK_FACTORY,

	 SERIAL_WRAPPER,

	 GAME_STATE,
	 
	 SCRIPT_MANAGER,

	 STM_CARD,

	 THREAD_SENSOR,
	 THREAD_TIMER,
	 THREAD_INTERFACE,
	 THREAD_EYES,
	 THREAD_WORKER,
	 
	 STRATEGIE;

	 /**
	  * Constructeur vide
	  */
	 private ServiceNames()
	 {
		 
	 }
	 
}
