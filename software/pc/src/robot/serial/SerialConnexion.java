package robot.serial;

import enums.ServiceNames;
import exceptions.serial.SerialConnexionException;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.*;

import utils.Log;
import container.Service;
import utils.Sleep;

/**
 * Classe implÃƒÂ©mentant le concept d'une connexion sÃƒÂ©rie.
 * UtilisÃƒÂ©e pour parler aux cartes ÃƒÂ©lectroniques.
 * Chaque port a un nom (asserv par exemple), un id (0 par exemple), un port (/dev/ttyUSB0 par exemple)
 * et un baudrate (57600 par exemple, c'est la vitesse de communication).
 * @author karton, dede, kayou, pf
 *
 */
public class SerialConnexion implements SerialPortEventListener, Service
{
    /**
     * Port de la connexion
     */
    SerialPort serialPort;

    /**
     * Sortie de log a utiliser
     */
    Log log;

    /**
     * Nom de la connexion sÃƒÂ©rie
     */
    String name;

    String port_name;
    int baudrate;

    /**
     * Flux d'entÃ¯Â¿Â½e du port
     */
    private InputStream input;

    /**
     * Flux de sortie du port
     */
    private OutputStream output;

    /**
     * TIME_OUT d'attente de rÃ¯Â¿Â½ception d'un message
     */
    private static final int TIME_OUT = 1000;


    private BufferedWriter out;
    private boolean debug = true;

    /**
     * Permet de couper la communication, oui c'est dégueulasse
     */
    public static boolean shutdown = false;

    /**
     * Construit une connexion sÃ¯Â¿Â½rie
     * @param log Sortie de log a utiliser
     * @param name nom de la connexion sÃƒÂ©rie
     */
    SerialConnexion (Log log, ServiceNames name)
    {
        this(log, name.toString());
    }

    /**
     * Construit une connexion sÃ¯Â¿Â½rie
     * @param log Sortie de log a utiliser
     * @param name nom de la connexion sÃƒÂ©rie
     */
    public SerialConnexion (Log log, String name)
    {
        super();
        this.log = log;
        this.name = name;
        if(this.debug)
        {
            try
            {
                File file = new File("orders.txt");
                if (!file.exists())
                {
                    //file.delete();
                    file.createNewFile();
                }
                out = new BufferedWriter(new FileWriter(file));

            } catch (IOException e) {
                log.critical("Manque de droits pour l'output des ordres");
                //out = null;
                e.printStackTrace();
            }
        }
        else
            this.out = null;
    }

    /**
     * AppelÃ¯Â¿Â½ par le SerialManager, il donne Ã¯Â¿Â½ la sÃ¯Â¿Â½rie tout ce qu'il faut pour fonctionner
     * @param port_name : Le port oÃ¯Â¿Â½ est connectÃ¯Â¿Â½ la carte (/dev/ttyUSB ou /dev/ttyACM)
     * @param baudrate : Le baudrate que la carte utilise
     */
    public void initialize(String port_name, int baudrate)
    {
        CommPortIdentifier portId = null;
        try
        {
            portId = CommPortIdentifier.getPortIdentifier(port_name);
        }
        catch (NoSuchPortException e2)
        {
            log.critical("Catch de "+e2+" dans initialize");
        }

        try
        {
            serialPort = (SerialPort) portId.open(this.getClass().getName(), TIME_OUT);
        }
        catch (PortInUseException e1)
        {
            log.critical("Catch de "+e1+" dans initialize");
        }
        try
        {
            serialPort.setSerialPortParams(baudrate,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);
            serialPort.notifyOnDataAvailable(false);

            input = serialPort.getInputStream();
            output = serialPort.getOutputStream();

        }
        catch (Exception e)
        {
            log.critical("Catch de "+e+" dans initialize");
        }

        this.port_name = port_name;
        this.baudrate = baudrate;
    }

    /**
     * MÃ¯Â¿Â½thode pour communiquer Ã¯Â¿Â½ la liaison sÃ¯Â¿Â½rie. Il ne faut absolument pas se tromper sur le nombre de lignes attendu en retour.
     * (une ligne est dÃ¯Â¿Â½limitÃ¯Â¿Â½ par un "\r\n" sur une communication sÃ¯Â¿Â½rie. elle peut Ã¯Â¿Â½tre envoyÃ¯Â¿Â½ par le bas niveau dans un:
     * printf("\r\n") ou un printfln("...") oÃ¯Â¿Â½ ici le ln veut dire retour Ã¯Â¿Â½ la ligne donc se charge de mettre "\r\n" Ã¯Â¿Â½ la fin du message pour l'utilisateur).
     * @param message Message ÃƒÂ  envoyer
     * @param nb_lignes_reponse Nombre de lignes que le bas niveau va rÃƒÂ©pondre (sans compter les acquittements)
     * @return Un tableau contenant le message
     * @throws SerialConnexionException
     */
    public String[] communiquer(String message, int nb_lignes_reponse) throws SerialConnexionException
    {
        String[] messages = {message};
        return communiquer(messages, nb_lignes_reponse);
    }

    /**
     * MÃ¯Â¿Â½thode pour communiquer Ã¯Â¿Â½ la liaison sÃ¯Â¿Â½rie. Il ne faut absolument pas se tromper sur le nombre de lignes attendu en retour.
     * (une ligne est dÃ¯Â¿Â½limitÃ¯Â¿Â½ par un "\r\n" sur une communication sÃ¯Â¿Â½rie. elle peut Ã¯Â¿Â½tre envoyÃ¯Â¿Â½ par le bas niveau dans un:
     * printf("\r\n") ou un printfln("...") oÃ¯Â¿Â½ ici le ln veut dire retour Ã¯Â¿Â½ la ligne donc se charge de mettre "\r\n" Ã¯Â¿Â½ la fin du message pour l'utilisateur).
     * @param messages Messages ÃƒÂ  envoyer
     * @param nb_lignes_reponse Nombre de lignes que l'avr va rÃƒÂ©pondre (sans compter les acquittements)
     * @return Un tableau contenant le message
     * @throws SerialConnexionException
     */
    public synchronized String[] communiquer(String[] messages, int nb_lignes_reponse) throws SerialConnexionException
    {
        if(shutdown)
            throw new SerialConnexionException();
        synchronized(output)
        {
            String inputLines[] = new String[nb_lignes_reponse];
            try
            {
                for (String m : messages)
                {
                    // affiche dans la console ce qu'on envois sur la sÃƒÂ©rie -> On cache ca, pour eviter le xy0? en permanence, mais ca peux etre interessant de le garder.
                    // ne jamais push un code avec cette ligne decommentee
					//log.debug("Envoi serie : '" + m  + "'");
                    m += "\r";

                    output.flush();

                    output.write(m.getBytes());
                    if(this.debug) 
                    {
                        out.write(m);
                        out.newLine();
                        out.flush();
                    }
                    int nb_tests = 0;
                  //  char acquittement = ' ';
                    boolean acquitte = false;

                    while (!acquitte)
                    {
                        nb_tests++;

                        // affiche dans la console ce qu'on lit sur la sÃƒÂ©rie
                        String resposeFromCard = readLine();

                        //TODO commenter.
						//log.debug("Reception acquitement : '" + resposeFromCard  + "'");

                        for(int i=0 ; i < resposeFromCard.length() ; i++)
                            acquitte = acquitte || (resposeFromCard.charAt(i) == '_');

                       // acquittement = resposeFromCard.charAt(resposeFromCard.length()-1);
                        if (!acquitte)
                        {
                            // clearInputBuffer();
                            output.flush();
                            output.write('\r');

                            Sleep.sleep(500);

                            while(available())
                                read();

                            output.write(m.getBytes());
                        }
                        if (nb_tests > 10)
                        {
                            log.critical("La série " + this.name + " ne répond pas après " + nb_tests + " tentatives (envoyé : '" + m + "', reponse : '" + resposeFromCard + "')");
                            break;
                        }
                    }
                }
            }
            catch (Exception e)
            {
                log.critical("Ne peut pas parler a la carte " + this.name + " lancement de "+e);
             //   clearInputBuffer();
                communiquer(messages, nb_lignes_reponse);
            }

            try
            {
                for (int i = 0 ; i < nb_lignes_reponse; i++)
                {
                    inputLines[i] = readLine();

                    //TODO commenter.
					//log.debug("Ligne "+i+": '"+inputLines[i]+"'");
                    if(inputLines[i].equals(null) || inputLines[i].replaceAll(" ", "").equals("")|| inputLines[i].replaceAll(" ", "").equals("-"))
                    {
                        log.critical("='( , envoi de "+inputLines[i]+" envoi du message a nouveau");
                  //      clearInputBuffer();
                        communiquer(messages, nb_lignes_reponse);
                    }

                    if(!isAsciiExtended(inputLines[i]))
                    {
                        log.critical("='( , envoi de "+inputLines[i]+" envoi du message a nouveau");
                     //   clearInputBuffer();
                        communiquer(messages, nb_lignes_reponse); // On retente
                    }
                }
               // while(available())
                   // read();
            }
            catch (Exception e)
            {
                log.critical("Ne peut pas parler a la carte " + this.name + " lancement de "+e);
            //    clearInputBuffer();
                communiquer(messages, nb_lignes_reponse);
            }
            return inputLines;
        }
    }

    /**
     * Doit ÃƒÂªtre appelÃƒÂ© quand on arrÃƒÂªte de se servir de la sÃƒÂ©rie
     */
    public void close()
    {
        if (serialPort != null)
        {
            log.debug("Fermeture de "+name);
            serialPort.close();
        }
    }

    /**
     * Handle an event on the serial port.
     * NE PAS SUPPRIMER!!!!!! Cette mÃ¯Â¿Â½thode est essentielle au fonctionnement de la communication sÃ¯Â¿Â½rie, mÃ¯Â¿Â½me si elle est vide.
     */
    public synchronized void serialEvent(SerialPortEvent oEvent)
    {
     /*   try {
            if(input.available() > 0)
                notify();
//			else
//				log.debug("Fausse alerte");
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    /**
     * Envoie un String sans chercher d'acquittement ou quoi que ce soit
     * @param message le message
     */
    public synchronized void sendRaw(byte[] message) throws IOException {
        output.write(message);

    }

    /**
     * Ping de la carte.
     * Peut envoyer un message d'erreur lors de l'exÃ¯Â¿Â½cution de createSerial() dans SerialManager.
     *
     * (Avec la carte de test dans createSerial(), on ne sait pas encore si celle-ci va rÃ¯Â¿Â½pondre ou non, c'est Ã¯Â¿Â½ dire,
     * si il s'agit bien d'une liaison sÃ¯Â¿Â½rie, ou alors d'un autre pÃ¯Â¿Â½riphÃ¯Â¿Â½rique. Si il s'agit d'un autre pÃ¯Â¿Â½riphÃ¯Â¿Â½rique,
     * alors cette mÃ¯Â¿Â½thode va catch une exception)
     * UtilisÃƒÂ© que par createSerial de SerialManager
     * @return l'id de la carte
     */
    public synchronized String ping()
    {
        synchronized(output) {
            try
            {
                serialPort.notifyOnDataAvailable(false);


                //Evacuation de l'eventuel buffer indÃƒÂ©sirable
                output.flush();

                byte[] ping = new byte[2];
                ping[0] = (byte)'?';
                ping[1] = (byte)'\r';
                output.write(ping);

                Sleep.sleep(1000);

                while(input.available() != 0)
                {
                    if(input.read() == 48)
                    {
                        // serialPort.notifyOnDataAvailable(true);
                        return "0";
                    }
                }

            }
            catch (Exception e)
            {
                log.critical("Catch de "+e+" dans ping");
            }
            return null;
        }
    }


    public void updateConfig()
    {
    }


    /**
     * Fonction verifiant si on recoit bien de l'ascii etendu : sinon, bah le bas niveau deconne.
     * @param inputLines
     * @return
     * @throws Exception
     */
    @SuppressWarnings("javadoc")
    public boolean isAsciiExtended(String inputLines) throws Exception
    {
        for (int i = 0; i < inputLines.length(); i++)
        {
            if (inputLines.charAt(i) > 259)
            {
                log.critical(inputLines+" n'est pas ASCII");
                return false;
            }
        }
        return true;
    }

    public boolean available() throws IOException
    {
        // tant qu'on est occupé, on dit qu'on ne reçoit rien
       /* if(busy)
            return false;*/
        return input.available() != 0;
    }

    /**
     * Lit un byte. On sait qu'il doit y en a avoir un.
     * @return
     * @throws IOException
     */
    public int read() throws IOException
    {
        synchronized (output) {
            if (input.available() == 0)
                Sleep.sleep(5); // On attend un tout petit peu, au cas où

            if (input.available() == 0)
                throw new IOException(); // visiblement on ne recevra rien de plus

            byte out = (byte) input.read();


            return out & 0xFF;
        }
    }

    public String readLine()
    {
        String res = "";
        synchronized (output) {
            try {
                int lastReceived;

                long time = System.currentTimeMillis();
                while (!available())
                {
                    if(System.currentTimeMillis() - time > TIME_OUT)
                    {
                        log.critical("Il ne daigne même pas répondre !");
                        return (res+(char)260);
                    }
                    Thread.sleep(5);
                }

                while (available()) {

                    if ((lastReceived = read()) == 13)
                     //   if ((lastReceived = read()) == 10)
                            break;

                    res += (char) lastReceived;

                    time = System.currentTimeMillis();
                    while (!available())
                    {
                        if(System.currentTimeMillis() - time > TIME_OUT)
                        {
                            log.critical("blocaqe attente nouveau char (pas de /r ?) dernier : "+(int)lastReceived);
                            return (res+(char)260);
                        }
                        Thread.sleep(5);
                    }
                }

                time = System.currentTimeMillis();
                while (!available())
                {
                    if(System.currentTimeMillis() - time > TIME_OUT)
                    {
                        log.critical("bloquage attente newChar (normalement newLine)");
                        return (res+(char)260);
                    }
                    Thread.sleep(5);
                }

                while(available()) {

                    if (read() == 10)
                        break;
                    time = System.currentTimeMillis();
                    while (!available())
                    {
                        if(System.currentTimeMillis() - time > TIME_OUT)
                        {
                            log.critical("Bloquage attente newLine");
                            return (res+(char)260);
                        }
                        Thread.sleep(5);
                    }
                }

            } catch (IOException e) {
                log.debug("On a perdu la série !!");
                while (ping() == null) {
                    Sleep.sleep(100);
                }
                res+=(char)260;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return res;
        }
    }
}