package enums;

public enum SensorNames {
	// Syntaxe: NOM_METHODE("protocole_serie", duree_action, valeur_par_default, nombre_de_ligne_reponse)
	// exemple : JAW_SENSOR("ccm",10,true,1)
	// exemple 2 : ULTRASOUND_BACK_SENSOR("us_ar",10,new int[]{0,0},2)

	/** les capteur ultrason exemple a modifier*/
	ULTRASOUND("us",10,0,1),

	;

	/**la duree moyenne que fait perdre une autre valeur que la valeur par default*/
	private int averageDuration;
	
	/**le string a envoyer a la serie*/
	private String serialCaptor;
	
	/**la valeur par default envoyee par le capteur (celle qui fait terminer le script le plus vite)*/
	private Object defaultValue;
	
	/**le nombre de ligne de la reponse du bas niveau (sans compter les aquitements)*/
	private int awnserLineAmount;

	public int getAverageDuration() 
	{
		return averageDuration;
	}
	
	private SensorNames(String serialString, int duration, Object defaultValue, int awnserLineAmount)
	{
		this.defaultValue = defaultValue;
		this.serialCaptor = serialString;
		this.averageDuration = duration;
		this.awnserLineAmount = awnserLineAmount;
	}
	
	/**
	 * Retrouve la chaine de caractère a envoyer par la série a la carte capteur pour obtenir la valeure du capteur
	 * @return la chaine de caractère a envoyer par la série a la carte
	 */
	public String getSerialCommunication()
	{
		return serialCaptor;
	}
	
	/**
	 *  
	 * @return la valeur par default du capteur
	 */
	public Object getDefaultValue()
	{
		return defaultValue;
	}

	public int getAwnserLineAmount()
	{
		return awnserLineAmount;
	}
}
