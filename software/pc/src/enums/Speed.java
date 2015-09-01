package enums;

/**
 * Définition des vitesses possibles de déplacement du robot.
 * @author pf, marsu
 *
 */

public enum Speed
{
	//FIXME a regler, demander au bas niveau.
	
	/** vitesse très lente */
	SLOW(3, 3),

	/** vitesse en temps normal (pour des distances de 200mm à 1000 mm) */
	BETWEEN_SCRIPTS_SLOW(10, 10);
	
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
    public float rotationSpeed;

        
    /**
     * Constructeur d'une vitesse.
     * @param PWM_translation la vitesse (entre 0 et 255) translationnel
     * @param PWM_rotation la vitesse (entre 0 et 255) en rotation
     */
    private Speed(int translationSpeed, int rotationSpeed)
    {
        this.translationSpeed = translationSpeed;
        this.rotationSpeed = rotationSpeed;
    }

    
}
