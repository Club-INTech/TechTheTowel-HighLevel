package threads;

import enums.EyesEvent;
import exceptions.ThreadException;
import exceptions.serial.SerialConnexionException;
import robot.serial.SerialConnexion;
import utils.Config;
import utils.Log;
import utils.Sleep;

import java.io.*;
import java.util.ArrayList;
import java.util.function.BooleanSupplier;

/**
 * Thread pour le pilotage des yeux ; A faire compléter par Aline
 * @author Aline, discord
 **/
public class ThreadEyes extends AbstractThread
{

    private final int NUMBER_OF_COLUMNS = 14;

    private final int NUMBER_OF_LINES = 7;

    /** Liste des tableaux de booléens représentant l'image à envoyer aux arduinos */
    private ArrayList<boolean[]> frames = new ArrayList<>();

    /** Connexion série avec les arduinos */
    private SerialConnexion serial;

    /**
     * Liste des fichier à charger, initilisée par une classe anonyme
     * MERCI JAVA POUR TON INCAPACITE A INITIALISER DES LISTES CORRECTEMENT!
     **/
    private final ArrayList<String> animList = new ArrayList<String>()
    {{
        add("test.txt");
    }};

    /**
     * Cet enum sert d'indicateur s'il y a une action spéciale à effectuer
     */
    private EyesEvent event = EyesEvent.IDLE;

    /**
     * Permet d'indiquer si une animation est en cours ou non
     */
    private boolean eventEnded = true;

    /**
     * Constructeur du thread
     * @param config la config
     * @param log le log
     */
    public ThreadEyes(Config config, Log log) {
        super(config, log);
        this.serial = new SerialConnexion(log, "EYES");
        //serial.initialize("/dev/ttyACM", 115200); //TODO Baudrate à changer
        try {
            readAnimations();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run()
    {
        boolean[] image = new boolean[NUMBER_OF_COLUMNS*NUMBER_OF_LINES];
        while(true)
        {
            //TODO Traitement
            switch (event)
            {
                case IDLE:
                    image = frames.get(0);
                    break;
                case BLOCKED:
                    break;
                case ENNEMY:
                    break;
                case SUCCESS:
                    break;
                case END:
                    break;
            }
            try
            {
                sendFrame(image);
                Thread.sleep(100);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Lit les animations présentes dans les fichiers
     */
    private void readAnimations() throws IOException, ThreadException {
        String buffer;
        int k;
        for(String anim : animList)
        {
            boolean[] res = new boolean[NUMBER_OF_LINES*NUMBER_OF_COLUMNS];
            k=0;
            BufferedReader reader = new BufferedReader(new FileReader("animations/"+anim));
            for(int i=0 ; i<NUMBER_OF_LINES ; i++)
            {
                buffer = reader.readLine();
                if(buffer.isEmpty())
                    throw new ThreadException(anim+" : BAD ANIM FILE !!");
                for(char j : buffer.toCharArray())
                {
                    if(j=='X')
                        res[k]=false;
                    else if(j=='O')
                        res[k]=true;
                    else
                        throw new ThreadException(anim+" : BAD ANIM FILE !!");
                    k++;

                }
            }
            frames.add(res);
        }

    }

    /**
     * Envoie l'image aux arduinos, ils feront la différence entre ce qui est à eux ou non
     * @param frame l'image
     */
    private void sendFrame(boolean[] frame) throws SerialConnexionException {
        for(Boolean i : frame)
        {
            if(i)
                serial.communiquer("T", 0);
            else
                serial.communiquer("F", 0);
        }
    }

    /**
     * Active un event
     * @param event l'event à activer
     */
    public void setEvent(EyesEvent event)
    {
        this.event = event;
        eventEnded = false;
    }
}