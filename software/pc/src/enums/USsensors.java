package enums;

public enum USsensors {
	// Syntaxe: NOM_METHODE("protocole_serie", duree_action, valeur_par_default, nombre_de_ligne_reponse)
	// exemple : ULTRASOUND_BACK_SENSOR("us_ar",10,new int[]{0,0})
	// TODO : ajouter les capteurs
	;

	/**la duree moyenne que fait perdre une autre valeur que la valeur par default*/
	private int averageDuration;
	
	/**le string a envoyer a la serie*/
	private String serialSensor;
	
	/**la valeur par default envoyee par le capteur (celle qui fait terminer le script le plus vite)*/
	private int defaultValue;

	public int getAverageDuration() 
	{
		return averageDuration;
	}
	
	private USsensors(String serialString, int duration, int defaultValue)
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
	public int getDefaultValue()
	{
		return defaultValue;
	}
}