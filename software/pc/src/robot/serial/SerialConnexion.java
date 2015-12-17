package robot.serial;

import container.Service;
import enums.ServiceNames;
import exceptions.UnknownOrderException;
import exceptions.serial.SerialConnexionException;
import gnu.io.*;
import utils.Log;

import java.io.BufferedReader;
import java.io.IOException;
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
	String name;
	
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
	 * Pointe vers l'emplacement du tableau messages courant qui pose problème
	 * (qui lève UnknownOrderException)
	 */
	//TODO gérer les paramètres de merde dans le bas niveau en renvoyant "Ordre inconnu"
	private int counter = -1;

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
	SerialConnexion (Log log, String name)
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
	 * @throws IOException 
	 */
	public String[] communiquer(String[] messages, int nb_lignes_reponse) throws SerialConnexionException
	{
		synchronized(output)
		{
			String inputLines[] = new String[nb_lignes_reponse];
			int c=-1;
			try
			{
				while(input.read()!=-1);
				for (String m : messages)
				{
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

						// affiche dans la console ce qu'on lit sur la série
						String resposeFromCard = input.readLine();
						//TODO commenter.
						log.debug("Reception acquitement : '" + resposeFromCard  + "'"); 
						
						acquittement = resposeFromCard.charAt(0);
						if (acquittement != '_')
						{
							//Vidage du buffer (expériemental)
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
				log.critical("Ne peut pas parler à la carte " + this.name + " lancement de "+e);
				throw new SerialConnexionException();
			}

			try
			{
				for (int i = 0 ; i < nb_lignes_reponse; i++)
				{
					inputLines[i] = input.readLine();
					//TODO commenter.
					log.debug("Ligne "+i+": '"+inputLines[i]+"'"); 
					if(inputLines[i].equals(null) || inputLines[i].replaceAll(" ", "").equals("")|| inputLines[i].replaceAll(" ", "").equals("-"))
					{
						log.critical("='( , envoi de "+inputLines[i]+" envoi du message a nouveau");
						communiquer(messages, nb_lignes_reponse);
					}
					if(inputLines[i].replaceAll(" ", "").equalsIgnoreCase("ordreinconnu"))
					{
						// Avant de throw, on vide le buffer de lecture à coups de input.read() qui renverra -1 une fois vidé.
						while(input.read()!=-1);
						throw new UnknownOrderException(messages, this); // Balance l'exception à la méthode parente et quitte la méthode actuelle
					}
					if(!isAsciiExtended(inputLines[i]))
					{
						log.critical("='( , envoi de "+inputLines[i]+" envoi du message a nouveau");
						communiquer(messages, nb_lignes_reponse); // On retente
					}
				}
			}
			catch (UnknownOrderException uoe)
			{
				if (!messages.equals(unknownMessages))
				{
					unknownMessages=messages;
					counter=c;
					if (uoe.verifyConnexion())
					{
						communiquer(messages, nb_lignes_reponse);
					}
					else
					{
						throw new SerialConnexionException();
					}
				}
				else
				{
					log.critical("Ordre REALLYNIGGA inonnu");
					uoe.logStack();
					if (counter==c)
					{
						log.critical("Mention spéciale à -> "+messages[counter]);
					}
				}
			}
			catch (Exception e)
			{
				log.critical("Ne peut pas parler à la carte " + this.name + " lancement de "+e);
				throw new SerialConnexionException();
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
	public synchronized String ping()
	{
		synchronized(output) {
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