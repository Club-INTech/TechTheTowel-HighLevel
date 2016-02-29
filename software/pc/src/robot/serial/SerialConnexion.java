package robot.serial;

import container.Service;
import enums.ServiceNames;
import exceptions.UnknownOrderException;
import exceptions.serial.SerialConnexionException;
import gnu.io.*;
import utils.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Classe implémentant le concept d'une connexion série.
 * Utilisée pour parler aux cartes électroniques.
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
	 * Nom de la connexion série
	 */
	private final String name;
	
	/**
	 * Flux d'ent�e du port
	 */
	private BufferedReader input;
	
	/** 
	 * Flux de sortie du port
	 */
	private BriztoutOutputStream output;
	
	/**
	 * TIME_OUT d'attente de r�ception d'un message
	 */
	private static final int TIME_OUT = 2000;
	
	/**
	 * Sauvegarde d'un message à problème
	 * (qui lève UnknownOrderException)
	 */
	private String[] unknownMessages = null;
	
	/**
	 * Combien de fois unknownMessages a été enregistré ?
	 * (correspond au nombre de fois que l'ordre
	 * à l'origine de l'erreur UnknownOrderException a été relancé)
	 */
	private int unknownCounter = 0;
	
	/**
	 * Pointe vers l'emplacement du tableau messages courant qui pose problème
	 * (qui lève UnknownOrderException)
	 */
	//TODO gérer les paramètres de merde dans le bas niveau en renvoyant "Ordre inconnu"
	private int unknownLine = -1;

	/**
	 * Construit une connexion s�rie
	 * @param log Sortie de log a utiliser
	 * @param name nom de la connexion série
	 */
	SerialConnexion (Log log, ServiceNames name)
	{
		this(log, name.toString());
	}

	/**
	 * Construit une connexion s�rie
	 * @param log Sortie de log a utiliser
	 * @param name nom de la connexion série
	 */
	public SerialConnexion (Log log, String name)
	{
		super();
		this.log = log;
		this.name = name;
	}

	/**
	 * Appel� par le SerialManager, il donne � la s�rie tout ce qu'il faut pour fonctionner
	 * @param port_name : Le port o� est connect� la carte (/dev/ttyUSB ou /dev/ttyACM)
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

		// Ouverture du port s�rie
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
			// r�gle certains param�tres li� � la s�rie
			serialPort.setSerialPortParams(baudrate,
					SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);

            serialPort.disableReceiveTimeout();

			// ouverture des flux Input/Output
			input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
			output = new BriztoutOutputStream(serialPort.getOutputStream());

		}
		catch (Exception e)
		{
			log.critical("Catch de "+e+" dans initialize");
		}
		
		// permet d'avoir un readLine non bloquant
		//try
		//{
			//serialPort.enableReceiveTimeout(1000);
		//}
		//catch (UnsupportedCommOperationException e)
		//{
		//	log.critical("Catch de "+e+" dans initialize");
		//}
	}

	/**
	 * M�thode pour communiquer � la liaison s�rie. Il ne faut absolument pas se tromper sur le nombre de lignes attendu en retour.
	 * (une ligne est d�limit� par un "\r\n" sur une communication s�rie. elle peut �tre envoy� par le bas niveau dans un:
	 * printf("\r\n") ou un printfln("...") o� ici le ln veut dire retour � la ligne donc se charge de mettre "\r\n" � la fin du message pour l'utilisateur).
	 * @param message Message à envoyer
	 * @param nb_lignes_reponse Nombre de lignes que le bas niveau va répondre (sans compter les acquittements)
	 * @return Un tableau contenant le message
	 * @throws SerialConnexionException 
	 */
	public String[] communiquer(String message, int nb_lignes_reponse) throws SerialConnexionException
	{
		String[] messages = {message};
		return communiquer(messages, nb_lignes_reponse);
	}
	
	/**
	 * M�thode pour communiquer � la liaison s�rie. Il ne faut absolument pas se tromper sur le nombre de lignes attendu en retour.
	 * (une ligne est d�limit� par un "\r\n" sur une communication s�rie. elle peut �tre envoy� par le bas niveau dans un:
	 * printf("\r\n") ou un printfln("...") o� ici le ln veut dire retour � la ligne donc se charge de mettre "\r\n" � la fin du message pour l'utilisateur).
	 * @param messages Messages à envoyer
	 * @param nb_lignes_reponse Nombre de lignes que l'avr va répondre (sans compter les acquittements)
	 * @return Un tableau contenant le message
	 * @throws SerialConnexionException 
	 */
	public String[] communiquer(String[] messages, int nb_lignes_reponse) throws SerialConnexionException
	{
		boolean uoe = false;
		synchronized(name)
		{
			String inputLines[] = new String[nb_lignes_reponse];
			int c=-1;
			//emptyInputBuffer();
			try
			{
				//while(input.read()!=-1); TODO Le vidage de buffer renvoie une exception s'il est vide
				for (String m : messages)
				{
					//wait(10);

					c++;
					//Vidage du buffer (expérimental)
					output.clear();


					// affiche dans la console ce qu'on envois sur la série -> On cache ca, pour eviter le xy0? en permanence, mais ca peux etre interessant de le garder.
					// ne jamais push un code avec cette ligne decommentee
					log.debug("Envoi serie : '" + m  + "'");
					m += "\r";
					output.write(m.getBytes());
					int nb_tests = 0;
					char acquittement = ' ';
					output.flush();

					while (acquittement != '_')
					{
						nb_tests++;

                        //while(!input.ready());

						// affiche dans la console ce qu'on lit sur la série
						String resposeFromCard = input.readLine();
						//TODO commenter.
						 log.debug("Reception acquitement : '" + resposeFromCard  + "'");
						
						acquittement = resposeFromCard.charAt(0);
						if (acquittement != '_')
						{
                            clearInputBuffer();
							//Vidage du buffer (expériemental)
							output.clear();

							output.write(m.getBytes());
							output.flush();
                            log.debug("Renvoi ordre pour acquittement");
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
				log.critical("Ne peut pas parler à la carte " + this.name + " lancement de "+e);
				throw new SerialConnexionException();
			}

			try
			{
				for (int i = 0 ; i < nb_lignes_reponse; i++)
				{
                   // while(!input.ready());

					inputLines[i] = input.readLine();
					//TODO commenter.
					log.debug("Ligne "+i+": '"+inputLines[i]+"'");
					if(inputLines[i].equals(null) || inputLines[i].replaceAll(" ", "").equals("")|| inputLines[i].replaceAll(" ", "").equals("-"))
					{
						log.critical("='( , envoi de "+inputLines[i]+" envoi du message a nouveau");
                        clearInputBuffer();
						inputLines = communiquer(messages, nb_lignes_reponse);
					}
					if(inputLines[i].replaceAll(" ", "").equalsIgnoreCase("ordreinconnu"))
					{
						// Avant de throw, on vide le buffer de lecture à coups de input.read() qui renverra -1 une fois vidé.
						//while(input.read()!=-1);
						uoe = true;
                        break;
					}
					if(!isAsciiExtended(inputLines[i]))
					{
						log.critical("='( , réception défectueuse: "+inputLines[i]);
						uoe = true;
                        break;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(uoe)
			{
				try
				{
                    output.clear();
                    output.write("?\r".getBytes());
                    output.write("?\r".getBytes());
                    output.write("?\r".getBytes());
                    output.flush();
                    clearInputBuffer();
                    output.clear();
				} catch (IOException e) {
                    e.printStackTrace();
                }
                if (!messages.equals(unknownMessages))
				{
					unknownMessages=messages;
					unknownCounter=0;
					unknownLine=c;
					if (verifyConnexion())
					{
						log.debug("On retente la communication, 1er test");
						inputLines = communiquer(messages, nb_lignes_reponse);
					}
                    else
                    {
                        log.critical("Echec du ping ; on retente quand même");
                        try {
                            clearInputBuffer();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        inputLines = communiquer(messages, nb_lignes_reponse);
                    }
				}

				// l'ordre à problème est relancé 6 fois
				else if (messages.equals(unknownMessages) && unknownCounter<5)
				{
					unknownCounter++;
					unknownLine=c;
					if (verifyConnexion())
					{
						log.debug("On retente la communication, 2e-6e test");

						inputLines = communiquer(messages, nb_lignes_reponse);
					}
					else if(unknownCounter>=5)
					{
						log.debug("La connexion dans UOE a echoue");

						throw new SerialConnexionException("Liaison série considérée défectueuse: réception récurrente d'une réponse bas-niveau non indexée");
					}
                    else
                    {
                        log.critical("Echec du ping ; on retente quand même");
                        try {
                            clearInputBuffer();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        inputLines = communiquer(messages, nb_lignes_reponse);
                    }
				}

				else
				{
					unknownCounter=0;
					// Ici un paradoxe se glisse car l'ordre est inconnu mais pas inonnu ! Et oui !
					// Une fraise.
					log.critical("Ordre REALLYNIGGA inonnu\n après "+unknownCounter+" tentatives");
					if (unknownLine==c)
					{
						log.critical("Mention spéciale à -> "+messages[unknownLine]);
					}
				}
			}
			return inputLines;
		}
	}



	/**
	 * Doit être appelé quand on arrête de se servir de la série
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
	 * NE PAS SUPPRIMER!!!!!! Cette m�thode est essentielle au fonctionnement de la communication s�rie, m�me si elle est vide.
	 */
	public synchronized void serialEvent(SerialPortEvent oEvent)
	{
	}

	/**
	 * Ping de la carte.
	 * Peut envoyer un message d'erreur lors de l'ex�cution de createSerial() dans SerialManager.
	 * 
	 * (Avec la carte de test dans createSerial(), on ne sait pas encore si celle-ci va r�pondre ou non, c'est � dire,
	 * si il s'agit bien d'une liaison s�rie, ou alors d'un autre p�riph�rique. Si il s'agit d'un autre p�riph�rique,
	 * alors cette m�thode va catch une exception)
	 * Utilisé que par createSerial de SerialManager
	 * @return l'id de la carte
	 */
	public String ping()
	{
		synchronized(name) {
			String ping = null;
			try
			{
			    /* OUTDATED
				//Evacuation de l'eventuel buffer indésirable
				output.write("CeciNestPasUnOrdre\r".getBytes());
				output.flush();
				//evacuation de l'acquittement "_"
				input.readLine();
				//evacuation de reponse "Ordre inonnu"
				input.readLine(); */

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

    private synchronized void clearInputBuffer() throws IOException {
        try
        {
            InputStream st = serialPort.getInputStream();
            if(st.available() > 0)
                st.skip(st.available());
        }
        catch(IOException e)
        {
            log.critical("DISCORD, TON CLEAR DU BUFFER D'INPUT C'EST DE LA MERDE !!!!");
        }
        this.input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
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
	        int characterSet = inputLines.charAt(i);
	        if (characterSet > 259) 
	        {
                log.critical(inputLines+"n'est pas ASCII");
				return false;
	        }
	    }
		return true;
	}

	/**
	 * Envoie un String sans chercher d'acquittement ou quoi que ce soit
	 * SEULEMENT UTILE POUR LES YEUX
	 * @param message le message
     */
	public synchronized void sendRaw(String message) throws IOException {
		output.clear();
		message += "\r";
		output.write(message.getBytes());

        output.flush();
	}

	/**
	 *	ATTENTION! verifyConnexion inverse le bouléen canCommunicate ;
	 car si cette méthode est appelée, la communication de l'ordre est potentiellement répétée
	 (cf. SerialConnexion.communiquer)
	 * @return true si la connexion série semble effective, false sinon
	 */
	public synchronized boolean verifyConnexion()
	{
		String ping = ping();
		int num = -1;
		try
		{
			if (ping != null)
				num = Integer.parseInt(ping);
		}
		catch(NumberFormatException e)
		{
			return false;
		}

		return(num == 0);

	}


}