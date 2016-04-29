package threads;

import enums.EyesEvent;
import exceptions.ThreadException;
import exceptions.serial.SerialConnexionException;
import robot.serial.SerialConnexion;
import utils.Config;
import utils.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * Thread pour le pilotage des yeux ; A faire compléter par Aline
 * @author Aline, discord
 **/
public class ThreadEyes extends AbstractThread
{

    private final int NUMBER_OF_COLUMNS = 11;//TODO A changer

    private final int NUMBER_OF_LINES = 7; //TODO A changer

    /** Liste des tableaux de booléens représentant l'image à envoyer aux arduinos */
    private ArrayList<boolean[]> frames = new ArrayList<>();

    /** Connexion série avec les arduinos */
    private SerialConnexion serial;

    private int count = 0;

    private final Random rand = new Random();

    /**
     * Liste des fichier à charger, initilisée par une classe anonyme
     * MERCI JAVA POUR TON INCAPACITE A INITIALISER DES LISTES CORRECTEMENT!
     * TODO A remplir
     **/
    private final ArrayList<String> animList = new ArrayList<String>()
    {{
        add("coeur.txt"); //1
        add("content.txt"); //2
        add("croix.txt"); //3
        add("FOCUS.txt"); //4
        add("genee.txt"); //5
        add("INI.txt"); //6
        add("INI2.txt"); //7
        add("Noob.txt"); //8
        add("poweron.txt"); //9
        add("sourirebasique.txt"); //10
        add("visagesouriant.txt"); //11
        add("CHOQUE.txt"); //12
    }};

    /**
     * Cet enum sert d'indicateur s'il y a une action spéciale à effectuer
     */
    private static EyesEvent event = EyesEvent.IDLE;

    /**
     * Animation suivante à effectuer
     */
    private static EyesEvent next = EyesEvent.IDLE;

    /**
     * Nombre de frames déjà affichées durant l'animation actuelle
     */
    private static int frame=0;

    /**
     * Luminosité
     */
    private byte luminosity = 10;

    /**
     * Constructeur du thread
     * @param config la config
     * @param log le log
     */
    public ThreadEyes(Config config, Log log) {
        super(config, log);
        this.serial = new SerialConnexion(log, "EYES");
        System.setProperty("gnu.io.rxtx.SerialPorts", "/dev/ttyAMA0");
        serial.initialize("/dev/ttyAMA0", 38400); //TODO Baudrate à changer
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
            /** On affiche une image à la fois, si l'on arrive à la fin on reset frame à 0 et on met l'évènement
             *   suivant dans event
             **/
            switch (event)
            {
                case IDLE:
                    if(frame==0)
                        image = frames.get(5);
                    else if(frame==1)
                        image = frames.get(6);
                    else if(frame==2)
                    {
                        frame = -1;
                        event = next;
                    }
                    break;
                case BLOCKED:
                    if(frame==0)
                        image = rand.nextBoolean() ? frames.get(4) : frames.get(7);
                    else if(frame==1)
                    {
                        frame = -1;
                        event = next;
                    }
                    break;
                case ENNEMY:
                    if(frame==0)
                        image = frames.get(11);
                    else if(frame==1)
                    {
                        frame = -1;
                        event = next;
                    }
                    break;
                case SUCCESS:
                    if(frame==0)
                        image = frames.get(0);
                    else if(frame==1)
                    {
                        frame = -1;
                        event = next;
                    }
                    break;
                case END:
                    if(frame==0)
                        image = frames.get(10);
                    else if(frame==1)
                    {
                        frame = -1;
                        event = next;
                    }
                    break;
                case TEST:
                    image = testPanel();
                    break;
                case BEGIN:
                    if(frame==0)
                        image = frames.get(3);
                    else if(frame==1)
                    {
                        frame = -1;
                        event = next;
                    }
                    break;
                case POWERON:
                    if(frame==0)
                        image = frames.get(8);
                    else if(frame==1)
                    {
                        frame = -1;
                        event = next;
                    }
                    break;

            }
            try
            {
                sendFrame(image);
                frame++;
                Thread.sleep(1500);//Temps d'attente entre chaque image TODO A ajuster
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean[] testPanel()
    {
        boolean[] res = new boolean[NUMBER_OF_LINES*NUMBER_OF_COLUMNS];
        for(int i=0 ; i<res.length ; i++)
            res[i] = false;
        res[count] = true;
        count++;
        if(count == NUMBER_OF_COLUMNS*NUMBER_OF_LINES)
            count = 0;
        return res;
    }

    /**
     * Lit les animations présentes dans les fichiers
     */
    private void readAnimations() throws IOException, ThreadException {
        String buffer;
        int k;
        for(String anim : animList)
        {
            boolean[] res = new boolean[NUMBER_OF_LINES*NUMBER_OF_COLUMNS*2];
            k=0;
            BufferedReader reader = new BufferedReader(new FileReader("animations/"+anim));
            for(int i=0 ; i<NUMBER_OF_LINES ; i++)
            {
                buffer = reader.readLine();
                if(buffer.isEmpty())
                    throw new ThreadException(anim+" : BAD ANIM FILE !! Buffer empty");
                for(char j : buffer.toCharArray())
                {
                    if(j=='X')
                        res[k]=false;
                    else if(j=='O' || j=='0')
                        res[k]=true;
                    else
                        throw new ThreadException(anim+" : BAD ANIM FILE !! erreur conversion : " + j );
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
    private synchronized void sendFrame(boolean[] frame) throws SerialConnexionException {
        byte[] buffer1 = new byte[NUMBER_OF_COLUMNS*NUMBER_OF_LINES + 2];
        byte[] buffer2 = new byte[NUMBER_OF_COLUMNS*NUMBER_OF_LINES + 3];
        buffer1[0] = (byte)128;
        buffer1[NUMBER_OF_COLUMNS*NUMBER_OF_LINES +1] = luminosity;
        buffer2[NUMBER_OF_COLUMNS*NUMBER_OF_LINES +1] = luminosity;
        buffer2[0] = (byte)129;
        buffer2[NUMBER_OF_COLUMNS*NUMBER_OF_LINES +2] = (byte)255;
        short count = 0;
        short c1 = 0;
        short c2 = 0;
        boolean side = true;
        for(int i=0 ; i<frame.length ; i++)
        {
            if(side)
                buffer1[(i%NUMBER_OF_COLUMNS + NUMBER_OF_COLUMNS*c1)+1] = (byte)(frame[i] ? 1 : 0);
            else
                buffer2[(i%NUMBER_OF_COLUMNS + NUMBER_OF_COLUMNS*c2)+1] = (byte)(frame[i] ? 1 : 0);

            count+=1;
            if(count == NUMBER_OF_COLUMNS)
            {
                if(side)
                    c1+=1;
                else
                    c2+=1;
                side = !side;
                count = 0;
            }
        }
        try {
            byte[] result = Arrays.copyOf(buffer1, buffer1.length + buffer2.length);
            System.arraycopy(buffer2, 0, result, buffer1.length, buffer2.length);
            serial.sendRaw(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Active un event
     * @param event l'event à activer
     */
    public synchronized static void setEvent(EyesEvent event)
    {
        next = event;
    }

    /**
     * Force l'activation immédiate d'une animation, une fois effectuée, il sera en IDLE
     * @param event l''event à forcer
     */
    public synchronized static void forceEvent(EyesEvent event)
    {
        ThreadEyes.event = event;
        next = EyesEvent.IDLE;
        frame = 0;
    }
}