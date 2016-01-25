package threads;

import robot.serial.SerialConnexion;
import utils.Config;
import utils.Log;

/**
 * Thread pour le pilotage des yeux ; A faire compléter par Aline
 * @author Aline, discord
 **/
public class ThreadEyes extends AbstractThread
{
    /** Connexion série avec les arduinos */
    private SerialConnexion serial;

    /**
     * Constructeur du thread
     * @param config la config
     * @param log le log
     */
    public ThreadEyes(Config config, Log log)
    {
        super(config, log);
        this.serial = new SerialConnexion(log, "EYES");
        serial.initialize("/dev/ttyACM", 115200); //TODO Baudrate à changer
    }

    @Override
    public void run()
    {
        //TODO Traitement
    }

}