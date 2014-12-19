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
import java.io.InputStreamReader;
import java.io.OutputStream;

import utils.Log;
import container.Service;

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
	private OutputStream output;
	
	/**
	 * TIME_OUT d'attente de r�ception d'un message
	 */
	private static final int TIME_OUT = 2000;

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
			e2.printStackTrace();
		}

		// Ouverture du port s�rie
		try
		{
			serialPort = (SerialPort) portId.open(this.getClass().getName(), TIME_OUT);
		} 
		catch (PortInUseException e1)
		{
			e1.printStackTrace();
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
			output = serialPort.getOutputStream();

		}
		catch (Exception e)
		{
			System.err.println(e.toString());
		}
		
		// permet d'avoir un readLine non bloquant
		try
		{
			serialPort.enableReceiveTimeout(1000);
		} 
		catch (UnsupportedCommOperationException e)
		{
			e.printStackTrace();
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
					// affiche dans la console ce qu'on envois sur la série
					//log.debug("Envois serie : '" + m  + "'", this);
					m += "\r";
					output.write(m.getBytes());
					int nb_tests = 0;
					char acquittement = ' ';
	
					while (acquittement != '_')
					{
						nb_tests++;
						String resposeFromCard = input.readLine();

						// affiche dans la console ce qu'on envois sur la série
						log.debug("Reception serie : '" + resposeFromCard  + "'", this);
						
						acquittement = resposeFromCard.charAt(0);
						if (acquittement != '_')
						{
							output.write(m.getBytes());
						}
						if (nb_tests > 10)
						{
							log.critical("La série " + this.name + " ne répond pas après " + nb_tests + " tentatives (envoyé : '" + m + "', reponse : '" + resposeFromCard + "')", this);
							break;
						}
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
				log.critical("Ne peut pas parler à la carte " + this.name, this);
				throw new SerialConnexionException();
			}
	
			try
			{
				for (int i = 0 ; i < nb_lignes_reponse; i++)
				{
					inputLines[i] = input.readLine();
				}
			}
			catch (Exception e)
			{
				log.critical("Ne peut pas parler à la carte " + this.name, this);
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
			log.debug("Fermeture de "+name, this);
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
	synchronized String ping()
	{
		synchronized(output) {
			String ping = null;
			try
			{
			
				//Evacuation de l'eventuel buffer indésirable
				output.write("dfsfdsgdfgfd\r".getBytes());
				//Evacuation de l'acquittement
				input.readLine();
			
				//ping
				output.write("?\r".getBytes());
				//evacuation de l'acquittement
				input.readLine();
	
				//recuperation de l'id de la carte
				ping = input.readLine();
	
			}
			catch (Exception e)
			{
				
				//TODO: n'afficher que les infos intéressantes.
				// TODO: fair ca sur tout les printStackTrace du  projet
				e.printStackTrace();
				log.critical(e.getMessage(),this);
			}
			return ping;
		}
	}
	
	public void updateConfig()
	{
	}

}