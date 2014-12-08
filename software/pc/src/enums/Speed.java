package enums;

/**
 * Définition des vitesses possibles de déplacement du robot.
 * @author pf, marsu
 *
 */

public enum Speed
{
	/** vitesse en temps normal */
    BETWEEN_SCRIPTS(170, 160),
    
    /** vitesse a avoirs i l'on veut parcuter un mur. Celle ci est plsu faible pour prendre soin de la méca du robot */
    INTO_WALL(90, 160),
    
    /** Vitesse a avoir en cours de recalage du robot sur la table. Le recalage nous fais percuter dans les murs, donc on avance pas trop vite */
    READJUSTMENT(90, 90);
    
    /** PWM des moteurs lors d'une translation, ce sont ces valeurs qui seront envoyées à la carte d'asserv */
    public int PWMTranslation;

    /** PWM des moteurs lors d'une rotation, ce sont ces valeurs qui seront envoyées à la carte d'asserv */
    public int PWMRotation;
    
    /** temps nécéssaire en milisecondes au robot pour partourir un miliètre. Utilisé par RobotChrono*/
    public int invertedTranslationnalSpeed;

    /** temps nécéssaire en milisecondes au robot pour tourner d'un radian. Utilisé par RobotChrono */
    public int invertedRotationnalSpeed;
        
    /**
     * Constructeur d'une vitesse.
     * @param PWM_translation, la vitesse (entre 0 et 255) translatoire
     * @param PWM_rotation, la vitesse (entre 0 et 255) en rotation
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
