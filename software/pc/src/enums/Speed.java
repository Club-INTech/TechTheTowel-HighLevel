package enums;

/**
 * Définition des vitesses possibles de déplacement du robot.
 * Les deux arguments passés dans les vitesses correspondent à des valeurs en mm/s pour la translation puis en rad/s pour la rotation
 * @author pf, marsu, CF, Discord
 *
 */

public enum Speed
{
	//TODO régler les valeurs de vitesse en translations et rotations avec des phases de test, décider des combinaisons de vitesses inutiles
	
	/** Vitesse ultra lente de translation, ultra lente en rotation */
	ULTRA_SLOW_ALL(85,Math.PI/8),

	/** vitesse lente en translation, lente en rotation*/
	SLOW_ALL(200, Math.PI/4),
	
	/** vitesse lente en translation, normale en rotation*/
	SLOW_T_MEDIUM_R(200,Math.PI/2),
	
	/** Vitesse standard de déplacement et rotation*/
	MEDIUM_ALL(420,(Math.PI/2)),
	
	/** vitesse standard de déplacement, lente en rotation*/
	MEDIUM_T_SLOW_R(350,Math.PI/4),

	FAST_T_SLOW_R(500, Math.PI/4),

	/** vitesse rapide en translation et rotation*/
	FAST_ALL(500,(Math.PI));
	
	///** vitesse en temps normal (pour des distances de 200mm à 1000 mm) */
		//BETWEEN_SCRIPTS_SLOW(10, 10),
	
	//buggé jusqu'a nouveau test
//	/** vitesse en temps normal (distances de 1000+ mm) */
//	BETWEEN_SCRIPTS(20, 20),
//    
//    /** vitesse rapide */
//    FAST(60,60),
//	
//    /** vitesse très rapide */
//	VERY_FAST(100, 100);

    
    /** vitesse des moteurs lors d'une translation, ce sont ces valeurs qui seront envoyées à la STM*/
    public float translationSpeed;

    /** vitesse des moteurs lors d'une rotation, ce sont ces valeurs qui seront envoyées à la STM*/
    public double rotationSpeed;

        
    /**
     * Constructeur d'une vitesse.
     * @param translationSpeed la vitesse (entre 0 et 255) translationnel
     * @param rotationSpeed la vitesse (entre 0 et 255) en rotation
     */
    private Speed(int translationSpeed, double rotationSpeed)
    {
        this.translationSpeed = translationSpeed;
        this.rotationSpeed = rotationSpeed;
    }

    
}
