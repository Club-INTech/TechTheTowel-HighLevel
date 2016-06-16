package enums;

public enum ContactSensors {
	// Syntaxe: NOM_METHODE("protocole_serie", duree_action, valeur_par_default, nombre_de_ligne_reponse)
	// exemple : DOOR_OPENED_LEFT("ildo",10,true),
	// TODO : rajouter les contacteurs
	;

	/**la duree moyenne que fait perdre une autre valeur que la valeur par default*/
	private int averageDuration;
	
	/**le string a envoyer a la serie*/
	private String serialSensor;
	
	/**la valeur par default envoyee par le capteur (celle qui fait terminer le script le plus vite)*/
	private boolean defaultValue;

	public int getAverageDuration() 
	{
		return averageDuration;
	}
	
	private ContactSensors(String serialString, int duration, boolean defaultValue)
	{
		this.defaultValue = defaultValue;
		this.serialSensor = serialString;
		this.averageDuration = duration;
	}
	
	/**
	 * Retrouve la chaine de caractère a envoyer par la série a la carte capteur pour obtenir la valeure du capteur
	 * @return la chaine de caractère a envoyer par la série a la carte
	 */
	public String getSerialCommunication()
	{
		return serialSensor;
	}
	
	/**
	 *  
	 * @return la valeur par default du capteur
	 */
	public boolean getDefaultValue()
	{
		return defaultValue;
	}
}
