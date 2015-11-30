package enums;

/**
 * Définition des vitesses possibles de déplacement du robot.
 * Les deux arguments passés dans les vitesses correspondent à des valeurs en mm/s pour la translation puis en rad/s pour la rotation
 * @author pf, marsu
 *
 */

public enum Speed
{
	//TODO régler les valeurs de vitesse en translations et rotations avec des phases de test
	
	/** vitesse lente */
	SLOW(200, Math.PI),

	/** vitesse en temps normal (pour des distances de 200mm à 1000 mm) */
	BETWEEN_SCRIPTS_SLOW(10, 10),
	
	/** Vitesse standard de déplacement */
	MEDIUM(400,2*(Math.PI)),
	
	/** vitesse rapide */
	FAST(800,4*(Math.PI));
	
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
     * @param PWM_translation la vitesse (entre 0 et 255) translationnel
     * @param PWM_rotation la vitesse (entre 0 et 255) en rotation
     */
    private Speed(int translationSpeed, double rotationSpeed)
    {
        this.translationSpeed = translationSpeed;
        this.rotationSpeed = rotationSpeed;
    }

    
}
