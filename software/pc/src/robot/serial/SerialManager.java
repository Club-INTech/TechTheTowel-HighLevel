package robot.serial;
import utils.Log;
import enums.ServiceNames;
import gnu.io.CommPortIdentifier;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

import exceptions.ServiceTypeException;
import exceptions.serial.SerialManagerException;

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
	public SerialConnexion serieAsservissement = null;

	/** Liste contenant les connexion séries avec les cartes */
	private SerialConnexion[] series = new SerialConnexion[3];

	//Pour chaque carte, on connait a l'avance son nom, son ping et son baudrate
	/** Carte d'assservissement, paramétré a l'avance par son nom, son id et son baudrate */
	private CardSpecification STM_Card = new CardSpecification(ServiceNames.STM_CARD, 0, 115200);
	
	/** On stock les cartes dans une liste */
	private ArrayList <CardSpecification> cards = new ArrayList <CardSpecification>();

	/** Liste pour stocker les series qui sont connectees au pc */ 
	private ArrayList<String> connectedSerial = new ArrayList<String>();

	/** Liste pour stocker les baudrates des differentes serie */
	private ArrayList<Integer> baudrate = new ArrayList<Integer>();

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

		cards.add(this.STM_Card);

		Iterator<CardSpecification> e = cards.iterator();
		while (e.hasNext())
		{
			int baud = e.next().baudrate;
			if (!this.baudrate.contains(baud))
				this.baudrate.add(baud);
		}

		this.serieAsservissement = new SerialConnexion(log, this.STM_Card.name);

		try
		{
			this.series[this.STM_Card.name.getSerialIndex()] = this.serieAsservissement;
		} 
		catch (ServiceTypeException e1)
		{
			log.critical( e1.logStack(), this);
		}

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
		int id = -1;
		//Liste des series deja attribues
		ArrayList<Integer> deja_attribues = new ArrayList<Integer>();
		String pings[] = new String[20];
		for (int baudrate : this.baudrate)
		{
			log.debug("liste des pings pour le baudrate " + baudrate, this);

			for(int k = 0; k < this.connectedSerial.size(); k++)
			{
				if (!deja_attribues.contains(k))
				{
					//Creation d'une serie de test
					SerialConnexion serialTest = new SerialConnexion(log, "Carte de test");
					serialTest.initialize(this.connectedSerial.get(k), baudrate);
					
					
					if(serialTest.ping() != null)
						id = Integer.parseInt(serialTest.ping());
					else 
					{
						serialTest.close();
						continue;
					}

					if(!isKnownPing(id))
					{
						serialTest.close();
						continue;
					}

					if (!goodBaudrate(baudrate, id))
					{
						serialTest.close();
						continue;
					}
					//On stock le port de la serie (connectedSerial) dans le tabeau à la case [id]
					pings[id] = this.connectedSerial.get(k);

					//Après les tests de pings sur la serie, on ferme la communication
					serialTest.close();

					deja_attribues.add(k);

					log.debug(id + " sur: " + connectedSerial.get(k),this);

				}
			}
		}

		//Association de chaque serie a son port
		Iterator<CardSpecification> e = cards.iterator();
		while (e.hasNext())
		{
			CardSpecification serial = e.next();
			if(serial.id == 0 && pings[serial.id] != null)
			{
				this.serieAsservissement.initialize(pings[serial.id], serial.baudrate);
			}

			if (pings[serial.id] == null)
			{
				log.critical("La carte " + serial.name + " n'est pas détectée", this);
				throw new SerialManagerException();
			}
		}
	}
	
	/**
	 * Cette m�thode v�rifie si id est bien associ� � baudrate
	 * (en comparant avec les param�tres des SpecificationCard qu'on lui a donn� au d�but de cette classe).
	 * Utilis� dans createSerial.
	 * @param baudrate a tester
	 * @param id a tester
	 * @return
	 */
	private boolean goodBaudrate(int baudrate, int id)
	{
		Iterator<CardSpecification> e = cards.iterator();
		while(e.hasNext())
		{
			CardSpecification serial = e.next();
			if((id == serial.id) && (baudrate == serial.baudrate))
				return true;
		}
		return false;
	}

	/**
	 * Permet de savoir si id est connu (info qu'il trouve dans SpecificationCard.
	 * @param id : id a v�rifier
	 * @return
	 */
	private boolean isKnownPing(int id)
	{
		Iterator<CardSpecification> e = cards.iterator();
		while(e.hasNext())
		{
			if(id == e.next().id)
				return true;
		}
		return false;
	}

	/**
	 * Permet d'obtenir une série au pr�alable instanci� dans le constructeur.
	 * @param name : Nom de la série
	 * @return L'instance de la série
	 * @throws SerialManagerException 
	 */
	public SerialConnexion getSerial(ServiceNames name)	throws SerialManagerException
	{
		try
		{
			if (this.series[name.getSerialIndex()] != null)
			{
				return this.series[name.getSerialIndex()];
			}
			else
			{
				log.critical("Aucune série du nom : " + name.toString() + " n'existe", this);
				log.critical("Vérifiez les branchements ou l'interface+simulateur (redémarrez si besoin).", this);
				log.critical("Vérifiez aussi que tous les processus Java exécutant ce code sont éteints.", this);
				throw new SerialManagerException("serie non trouvée");
			}
		}
		catch (ServiceTypeException e)
		{
			log.debug( e.logStack(), this);
		}
		return serieAsservissement;
	}
}
