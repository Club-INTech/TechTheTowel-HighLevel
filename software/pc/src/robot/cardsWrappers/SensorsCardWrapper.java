package robot.cardsWrappers;

import container.Service;
import enums.ContactSensors;
import enums.USsensors;
import exceptions.ConfigPropertyNotFoundException;
import exceptions.serial.SerialConnexionException;
import robot.serial.SerialConnexion;
import utils.Config;
import utils.Log;

/**
 * Classe simplifiant le dialogue avec les capteurs
 * @author PF, marsu, paul
 */

public class SensorsCardWrapper implements Service
{
	/**
	 * service de log a utiliser en cas de soucis
	 */
	private Log log;
	
	/**
	 *  connexion série avec la carte capteurs
	 */
	private SerialConnexion sensorsCardSerial;
	
	/**
	 * fichier de configuration du robot a lire
	 */
	private Config config;

	/**
	 * Vrai si les capteurs sont allumés, faux si les capteurs sont ignorés
	 */
	private boolean areSensorsActive = true;

	public SensorsCardWrapper(Config config, Log log, SerialConnexion serie)
	{
		this.log = log;
		this.config = config;
		this.sensorsCardSerial = serie;
		updateConfig();
	}
	
	public void updateConfig()
	{
		try
		{
			areSensorsActive = Boolean.parseBoolean(config.getProperty("capteurs_on"));
		}
		catch (ConfigPropertyNotFoundException e)
		{
    		log.debug("Revoir le code : impossible de trouver la propriété "+e.getPropertyNotFound());
		}
	}

	/**
	 * demande aux capteurs de fournir la distance entre le robot et le prochain obstacle
	 * @return la distance en mm estimée par les capteurs avant un obstacle. Une valeur de 3000 est considérée infinie
	 */
	public int getSensedDistance()
	{
		if(!areSensorsActive)
    		return 3000;
		
		
		String infoBuffer;
		
		// demande au capteur la distance qu'il détecte
		try
		{
			infoBuffer = sensorsCardSerial.communiquer("us", 1)[0];
    		
		}
		catch(SerialConnexionException e)
		{
			log.critical("La carte capteurs ne répond pas !");
			log.critical( e.logStack());
			return 3000; // valeur considérée comme infinie
		}

		// parse la distance que les capteurs nous ont donné et renvois cette valeur a l'utilisateur
		int distance = Integer.parseInt(infoBuffer);
		return distance;
	}
	
	/**
	 * Demande a la carte capteurs de nous indiquer si le jumper de début de match est présent ou non
	 * @return vrai si le jumper est absent, faux sinon
	 */
    public boolean isJumperAbsent()
    {
		try 
		{
			// demande a la carte si le jumper est présent, parse sa réponse, et si on lit 0 c'est que le jumper n'est pas/plus la
			return Integer.parseInt(sensorsCardSerial.communiquer("j", 1)[0]) != 0;
		} 
		catch (NumberFormatException e)
		{
			log.critical("réponse corrompue du jumper !");
			e.printStackTrace();
			return false;
		}
		catch (SerialConnexionException e)
		{
			log.critical(" Problème de communication avec la carte capteurs en essayent de parler au jumper.");
			log.debug( e.logStack());
			return false;
		}
    }
    
    /**
     * recupere la valeur d'un capteur de contact
     * @param sensor le capteur dont on veut recuperer la valeur
     * @return la valeur du capteur
     * @throws SerialConnexionException si erreur de connexion avec le capteur
     */
    public boolean getContactSensorValue(ContactSensors sensor) throws SerialConnexionException
    {
    	String[] sensorAnswer = sensorsCardSerial.communiquer(sensor.getSerialCommunication(),1);
		return (!sensorAnswer[0].toString().equals("0"));
    }
    
    /**
     * recupere la valeur d'un capteur ultrason
     * @param sensor le capteur dont on veut recuperer la valeur
     * @return la valeur du capteur
     * @throws SerialConnexionException si erreur de connexion avec le capteur
     */
    public int getUSSensorValue(USsensors sensor) throws SerialConnexionException
    {
    	String[] sensorAnswer = sensorsCardSerial.communiquer(sensor.getSerialCommunication(),1);
    	return Integer.parseInt(sensorAnswer[0]);
    }
}
