package robot.serial;

import enums.ServiceNames;
import exceptions.ServiceTypeException;
import exceptions.serial.SerialManagerException;
import gnu.io.CommPortIdentifier;
import utils.Log;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

/**
 * Instancie toutes les s�ries, il faut bien faire attention � d�finir les cartes
 * qui seront utilis�es dans le robot, avec le ping et le baudrate de fonctionnement.
 * 
 * Cette classe va au pr�alable charger les param�tres des cartes (dans le constructeur),
 * puis regarder toutes les liaisons s�ries qui sont susceptibles d'�tre connect�es
 * (dans /dev/ttyUSB* ou /dev/ACM*, mais pas besoin de savoir �a, il se d�brouille comme un grand).
 * @author pierre
 * @author pf
 */
public class SerialManager 
{
	/**
	 * Sortie de log a utiliser pour informer l'utilisateur
	 */
	private Log log;

	/** Series a instancier : celle pour la carte d'asser */
	public SerialConnexion serialConnexion = null;

	//Pour chaque carte, on connait a l'avance son nom, son ping et son baudrate
	/** Carte d'assservissement, paramétré a l'avance par son nom, son id et son baudrate */
	private CardSpecification STM_Card = new CardSpecification(ServiceNames.STM_CARD, 0, 115200);

	/** Liste pour stocker les series qui sont connectees au pc */ 
	private ArrayList<String> connectedSerial = new ArrayList<String>();

	/** Baudrate de la liaison série */
	public static final int baudrate = 115200;

	/**
	 * Recuperation des param�tres des cartes dans cards et des baudrates dans baudrate
	 * (ceux d�finis plus haut), puis fait appel � checkSerial() et createSerial().
	 * A la fin de ce constructeur, les s�ries sont d�tect�es et instanci�es. 
	 * @param log : la sortie de log à utiliser
	 * @throws SerialManagerException 
	 */
	public SerialManager(Log log) throws SerialManagerException
	{
		this.log = log;

		this.serialConnexion = new SerialConnexion(log, this.STM_Card.name);

		checkSerial();
		createSerial();
	}

	/**
	 * Regarde toutes les series qui sont branchees (sur /dev/ttyUSB* et /dev/ttyACM*)
	 */
	public  void checkSerial()
	{
		Enumeration<?> ports = CommPortIdentifier.getPortIdentifiers();
		while (ports.hasMoreElements())
		{
			CommPortIdentifier port = (CommPortIdentifier) ports.nextElement();
			this.connectedSerial.add(port.getName());
		}
	}

	/**
	 * Création des series (il faut au prealable faire un checkSerial()).
	 *
	 * Cette m�thode cr�e une s�rie de test pour chaque port /dev/ttyUSB* et /dev/ttyACM* d�tect�
	 * dans le but de ping ces ports et d�terminer si il nous interesse (en v�rifiant le ping re�u,
	 * si il en re�oit un). Si un /dev/ttyUSB (ou ACM) n'est pas une liaison s�rie,
	 * il se peut que l'on ait un message d'erreur li� au fait que l'on ping un /dev/ttyUSB (ou ACM)
	 * qui ne nous r�pond pas.
	 * @throws SerialManagerException
	 */
	public void createSerial() throws SerialManagerException
	{
		int id;

		for (String connectedSerial : this.connectedSerial)
		{
			SerialConnexion ser = new SerialConnexion(log,"test");
			ser.initialize(connectedSerial, baudrate);

			if (ser.ping() != null)
				id = Integer.parseInt(ser.ping());
			else {
				ser.close();
				continue;
			}

			if (id != 0) {
				ser.close();
				continue;
			}

			ser.close();
			System.out.println("Carte sur: " + connectedSerial);

			serialConnexion.initialize(connectedSerial, baudrate);
			return;
		}

		log.critical("La carte STM32 n'est pas détectée");
		throw new SerialManagerException();
	}

	/**
	 * Permet d'obtenir une série au pr�alable instanci� dans le constructeur.
	 * @return L'instance de la série
	 * @throws SerialManagerException 
	 */
	public SerialConnexion getSerial()	throws SerialManagerException
	{
		return serialConnexion;
	}
}
