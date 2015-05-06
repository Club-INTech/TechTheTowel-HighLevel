package enums;

/**
 * Définition des vitesses possibles de déplacement du robot.
 * @author pf, marsu
 *
 */

public enum Speed
{
	
	/** vitesse très lente */
	SLOW(3, 3);
	// Buggé jusqu'a nouvel ordre
//
//	/** vitesse en temps normal (pour des distances de 200mm à 1000 mm) */
//	BETWEEN_SCRIPTS_SLOW(10, 10),
//	
//	/** vitesse en temps normal (distances de 1000+ mm) */
//	BETWEEN_SCRIPTS(20, 20),
//    
//    /** vitesse rapide */
//    FAST(60,60),
//	
//    /** vitesse très rapide */
//	VERY_FAST(100, 100);

    
    /** PWM des moteurs lors d'une translation, ce sont ces valeurs qui seront envoyées à la carte d'asserv */
    public int PWMTranslation;

    /** PWM des moteurs lors d'une rotation, ce sont ces valeurs qui seront envoyées à la carte d'asserv */
    public int PWMRotation;
    
    /** temps nécéssaire en millisecondes au robot pour partourir un milliètre. Utilisé par RobotChrono*/
    public int invertedTranslationnalSpeed;

    /** temps nécéssaire en millisecondes au robot pour tourner d'un radian. Utilisé par RobotChrono */
    public int invertedRotationnalSpeed;
        
    /**
     * Constructeur d'une vitesse.
     * @param PWM_translation la vitesse (entre 0 et 255) translationnel
     * @param PWM_rotation la vitesse (entre 0 et 255) en rotation
     */
    private Speed(int PWM_translation, int PWM_rotation)
    {
        this.PWMTranslation = PWM_translation;
        this.PWMRotation = PWM_rotation;
        
        /**
         * Formule héritée de Deboc (intech 2012).
         * Ca peut être intéressant de refaire des mesures et une interpolation.
         */
        invertedTranslationnalSpeed = (int) (1./(((float)2500)/((float)613.52 * (float)(Math.pow((double)PWM_translation,(double)(-1.034))))/1000));
        invertedRotationnalSpeed = (int) (1./(((float)Math.PI)/((float)277.85 * (float)Math.pow(PWM_rotation,(-1.222)))/1000));
    }

    
}
