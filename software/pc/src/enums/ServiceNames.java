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

	 LOCOMOTION_CARD_WRAPPER,
	 SENSORS_CARD_WRAPPER,
	 ACTUATOR_CARD_WRAPPER,
	 
	 PATHFINDING,
	 
	 LASER,
	 LASER_FILTRATION,
	 
	 GAME_STATE,
	 
	 SCRIPT_MANAGER,
	 
	 SERIE_ASSERVISSEMENT(ServiceType.SERIAL, 0),
	 SERIE_CAPTEURS_ACTIONNEURS(ServiceType.SERIAL, 1),
	 SERIE_LASER(ServiceType.SERIAL, 2),
	 
	 THREAD_SENSOR,
	 THREAD_LASER,
	 THREAD_TIMER,
	 
	 CHECK_UP;
	
	 /** type de service, par défault: GENERIC*/
	 private ServiceType type = ServiceType.GENERIC;

	 /** Si ce service est de type SERIAL, identifie le port série auquel ce service est rattaché par un index dans la liste des ports série*/
	 private int serialIndex = 0;

	 /**
	  * Constructeur vide
	  */
	 private ServiceNames()
	 {
		 
	 }
	 
	 /**
	  * Renvois 
	  * @return
	  * @throws ServiceTypeException
	  */
	 public int getSerialIndex() throws ServiceTypeException
	 {
		 if(type != ServiceType.SERIAL)
			 throw new ServiceTypeException();
		 return serialIndex;
	 }
	 
	 private ServiceNames(ServiceType type, int nbSerie)
	 {
		 this.serialIndex = nbSerie;
		 this.type = type;
	 }
	 
	 public ServiceType getType()
	 {
		 return type;
	 }
	 
	 public enum ServiceType
	 {
		 GENERIC,
		 SERIAL;		 
	 }
	 
}
