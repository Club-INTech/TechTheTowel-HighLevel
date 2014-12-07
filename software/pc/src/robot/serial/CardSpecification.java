package robot.serial;

import enums.ServiceNames;

/**
 * @author pierre
 * N'est utilise que par le SerialManager afin de connaitre les attributs des cartes
 */
class CardSpecification 
{
	/**
	 * Nom de la carte électronique.
	 * Les noms peuvent être parmis:
	 * ServiceNames.SERIE_ASSERVISSEMENT(ServiceType.SERIAL, 0),  
	 * ServiceNames.SERIE_CAPTEURS_ACTIONNEURS(ServiceType.SERIAL, 1),  
	 * ServiceNames.SERIE_LASER(ServiceType.SERIAL, 2),  
	 */
	ServiceNames name;
	
	/**
	 * identifiant de la carte
	 */
	int id;
	
	/**
	 * baudrate que la connexion série doit avoir pour parler a cette carte
	 */
	int baudrate;
	
	
	/**
	 * Construit les spécifications d'une carte électronique.
	 * @param name Nom de la carte électronique.
	 * @param id identifiant de la carte
	 * @param baudrate baudrate que la connexion série doit avoir pour parler a cette carte
	 */
	CardSpecification(ServiceNames name, int id, int baudrate)
	{
		this.name = name;
		this.id = id;
		this.baudrate = baudrate;
	}
}
