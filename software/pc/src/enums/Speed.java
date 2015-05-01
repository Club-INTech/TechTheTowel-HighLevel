package enums;

/**
 * Définition des vitesses possibles de déplacement du robot.
 * @author pf, marsu
 *
 */

public enum Speed
{
	/** vitesse en temps normal */
    // BETWEEN_SCRIPTS(100, 100), // valeurs des anciens asservissements
	BETWEEN_SCRIPTS(10, 10), // valeurs des anciens asservissements
    
    /** vitesse a avoir si l'on veut percuter un mur. Celle ci est plus faible pour prendre soin de la méca du robot */
//    INTO_WALL(90, 160),
    
    /** vitesse lente pour ne pas percuter des objects et les perdre */
    SLOW(60,60);
    
    /** Vitesse a avoir en cours de recalage du robot sur la table. Le recalage nous fais percuter dans les murs, donc on avance pas trop vite */
//    READJUSTMENT(90, 90);
    
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
