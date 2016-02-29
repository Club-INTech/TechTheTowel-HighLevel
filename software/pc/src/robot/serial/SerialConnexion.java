package robot.serial;

import enums.ServiceNames;
import exceptions.serial.SerialConnexionException;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

import utils.Log;
import container.Service;

/**
 * Classe implÃ©mentant le concept d'une connexion sÃ©rie.
 * UtilisÃ©e pour parler aux cartes Ã©lectroniques.
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
     * Nom de la connexion sÃ©rie
     */
    String name;

    /**
     * Flux d'entï¿½e du port
     */
    private BufferedReader input;

    /**
     * Flux de sortie du port
     */
    private BriztoutOutputStream output;

    /**
     * TIME_OUT d'attente de rï¿½ception d'un message
     */
    private static final int TIME_OUT = 2000;


    /**
     * Construit une connexion sï¿½rie
     * @param log Sortie de log a utiliser
     * @param name nom de la connexion sÃ©rie
     */
    SerialConnexion (Log log, ServiceNames name)
    {
        this(log, name.toString());
    }

    /**
     * Construit une connexion sï¿½rie
     * @param log Sortie de log a utiliser
     * @param name nom de la connexion sÃ©rie
     */
    public SerialConnexion (Log log, String name)
    {
        super();
        this.log = log;
        this.name = name;
    }

    /**
     * Appelï¿½ par le SerialManager, il donne ï¿½ la sï¿½rie tout ce qu'il faut pour fonctionner
     * @param port_name : Le port oï¿½ est connectï¿½ la carte (/dev/ttyUSB ou /dev/ttyACM)
     * @param baudrate : Le baudrate que la carte utilise
     */
    void initialize(String port_name, int baudrate)
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

        // Ouverture du port sï¿½rie
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
            // rï¿½gle certains paramï¿½tres liï¿½ ï¿½ la sï¿½rie
            serialPort.setSerialPortParams(baudrate,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);

            // ouverture des flux Input/Output
            input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
            output = new BriztoutOutputStream(serialPort.getOutputStream());

        }
        catch (Exception e)
        {
            log.critical("Catch de "+e+" dans initialize");
        }

        // permet d'avoir un readLine non bloquant
        try
        {
            serialPort.enableReceiveTimeout(1000);
        }
        catch (UnsupportedCommOperationException e)
        {
            log.critical("Catch de "+e+" dans initialize");
        }
    }

    /**
     * Mï¿½thode pour communiquer ï¿½ la liaison sï¿½rie. Il ne faut absolument pas se tromper sur le nombre de lignes attendu en retour.
     * (une ligne est dï¿½limitï¿½ par un "\r\n" sur une communication sï¿½rie. elle peut ï¿½tre envoyï¿½ par le bas niveau dans un:
     * printf("\r\n") ou un printfln("...") oï¿½ ici le ln veut dire retour ï¿½ la ligne donc se charge de mettre "\r\n" ï¿½ la fin du message pour l'utilisateur).
     * @param message Message Ã  envoyer
     * @param nb_lignes_reponse Nombre de lignes que le bas niveau va rÃ©pondre (sans compter les acquittements)
     * @return Un tableau contenant le message
     * @throws SerialConnexionException
     */
    public String[] communiquer(String message, int nb_lignes_reponse) throws SerialConnexionException
    {
        String[] messages = {message};
        return communiquer(messages, nb_lignes_reponse);
    }

    /**
     * Mï¿½thode pour communiquer ï¿½ la liaison sï¿½rie. Il ne faut absolument pas se tromper sur le nombre de lignes attendu en retour.
     * (une ligne est dï¿½limitï¿½ par un "\r\n" sur une communication sï¿½rie. elle peut ï¿½tre envoyï¿½ par le bas niveau dans un:
     * printf("\r\n") ou un printfln("...") oï¿½ ici le ln veut dire retour ï¿½ la ligne donc se charge de mettre "\r\n" ï¿½ la fin du message pour l'utilisateur).
     * @param messages Messages Ã  envoyer
     * @param nb_lignes_reponse Nombre de lignes que l'avr va rÃ©pondre (sans compter les acquittements)
     * @return Un tableau contenant le message
     * @throws SerialConnexionException
     */
    public String[] communiquer(String[] messages, int nb_lignes_reponse) throws SerialConnexionException
    {
        synchronized(output)
        {
            String inputLines[] = new String[nb_lignes_reponse];
            try
            {
                for (String m : messages)
                {
                    // affiche dans la console ce qu'on envois sur la sÃ©rie -> On cache ca, pour eviter le xy0? en permanence, mais ca peux etre interessant de le garder.
                    // ne jamais push un code avec cette ligne decommentee
//					log.debug("Envoi serie : '" + m  + "'", this);
                    m += "\r";
                    output.clear();
                    output.write(m.getBytes());
                    output.flush();
                    int nb_tests = 0;
                    char acquittement = ' ';

                    while (acquittement != '_')
                    {
                        nb_tests++;

                        // affiche dans la console ce qu'on lit sur la sÃ©rie
                        String resposeFromCard = input.readLine();
                        //TODO commenter.
//						log.debug("Reception acquitement : '" + resposeFromCard  + "'", this);

                        acquittement = resposeFromCard.charAt(0);
                        if (acquittement != '_')
                        {
                            output.clear();
                            output.write(m.getBytes());
                            output.flush();
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
                throw new SerialConnexionException();
            }

            try
            {
                for (int i = 0 ; i < nb_lignes_reponse; i++)
                {
                    inputLines[i] = input.readLine();

                    //TODO commenter.
//					log.debug("Ligne "+i+": '"+inputLines[i]+"'",this);
                    if(inputLines[i].equals(null) || inputLines[i].replaceAll(" ", "").equals("")|| inputLines[i].replaceAll(" ", "").equals("-"))
                    {
                        log.critical("='( , envoi de "+inputLines[i]+" envoi du message a nouveau");
                        communiquer(messages, nb_lignes_reponse);
                    }

                    if(!isAsciiExtended(inputLines[i]))
                    {
                        log.critical("='( , envoi de "+inputLines[i]+" envoi du message a nouveau");
                        communiquer(messages, nb_lignes_reponse); // On retente
                    }
                }
            }
            catch (Exception e)
            {
                log.critical("Ne peut pas parler a la carte " + this.name + " lancement de "+e);
                throw new SerialConnexionException();
            }
            return inputLines;
        }
    }

    /**
     * Doit Ãªtre appelÃ© quand on arrÃªte de se servir de la sÃ©rie
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
     * NE PAS SUPPRIMER!!!!!! Cette mï¿½thode est essentielle au fonctionnement de la communication sï¿½rie, mï¿½me si elle est vide.
     */
    public synchronized void serialEvent(SerialPortEvent oEvent)
    {
    }

    /**
     * Envoie un String sans chercher d'acquittement ou quoi que ce soit
     * SEULEMENT UTILE POUR LES YEUX
     * @param message le message
     */
    public synchronized void sendRaw(String message) throws IOException {
        message += "\r";
        output.write(message.getBytes());
        output.flush();

    }

    /**
     * Ping de la carte.
     * Peut envoyer un message d'erreur lors de l'exï¿½cution de createSerial() dans SerialManager.
     *
     * (Avec la carte de test dans createSerial(), on ne sait pas encore si celle-ci va rï¿½pondre ou non, c'est ï¿½ dire,
     * si il s'agit bien d'une liaison sï¿½rie, ou alors d'un autre pï¿½riphï¿½rique. Si il s'agit d'un autre pï¿½riphï¿½rique,
     * alors cette mï¿½thode va catch une exception)
     * UtilisÃ© que par createSerial de SerialManager
     * @return l'id de la carte
     */
    public synchronized String ping()
    {
        synchronized(output) {
            String ping = null;
            try
            {

                //Evacuation de l'eventuel buffer indÃ©sirable
                output.clear();

                //ping
                output.write("?\r".getBytes());
                output.flush();
                //evacuation de l'acquittement
                input.readLine();

                //recuperation de l'id de la carte
                ping = input.readLine();

            }
            catch (Exception e)
            {
                log.critical("Catch de "+e+" dans ping");
            }
            return ping;
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
        Boolean isAsciiExtended=true;
        for (int i = 0; i < inputLines.length(); i++)
        {
            int characterSet = inputLines.charAt(i);
            if (characterSet > 259)
            {
                isAsciiExtended=false;
                log.critical(inputLines+"n'est pas ASCII");
                return isAsciiExtended;
            }
        }
        return isAsciiExtended;
    }
}