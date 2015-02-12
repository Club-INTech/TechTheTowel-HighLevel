package enums;

public enum SensorNames {
	/** le capteur des machoires */
	JAW_SENSOR("mp",100,true,1), //true on a attrape qqch //TODO: valeurs a modifier
	/** le capteur de la zone gauche */
	LEFT_ZONE_SENSOR("bgp",100,true,1), //true = on a qqch dans la poche de gauche
	/** le capteur de la zone droite */
	RIGHT_ZONE_SENSOR("bdp",100,true,1), //true = on a qqch dans la poche de droite
	/** les capteur ultrason avant, comme il y en a deux la reponse est un int[2] */
	ULTRASOUND_FRONT_SENSOR("us_av",100,new int[]{3000,3000},2), //3000 mm correspond a la taille maximale, comme ça on ne detecte pas d'obstacle dans robotChrono
	/** les capteurs ultrason arriere, comme il y en a deux la reponse est un int[2]  */
	ULTRASOUND_BACK_SENSOR("us_ar",100,new int[]{3000,3000},2)
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
