package container;

import enums.ServiceNames;
import exceptions.ContainerException;
import exceptions.serial.SerialConnexionException;
import exceptions.serial.SerialManagerException;
import hook.types.HookFactory;
import robot.Locomotion;
import robot.Robot;
import robot.serial.SerialManager;
import robot.serial.SerialWrapper;
import scripts.ScriptManager;
import strategie.GameState;
import table.Table;
import threads.ThreadManager;
import threads.dataHandlers.ThreadSerial;
import utils.Config;
import utils.Log;
import utils.Sleep;

import java.io.IOException;

/**
 * 
 * Gestionnaire de la durée de vie des objets dans le code.
 * Permet à n'importe quelle classe implémentant l'interface "Service" d'appeller d'autres instances de services via son constructeur.
 * Une classe implémentant service n'est instanciée que par la classe "Container"
 * La liste des services est disponible dans l'énumération ServiceNames 
 * 
 * @author pf
 */
public class Container
{

	/** liste des services déjà instanciés. Contient au moins Config et Log. Les autres services appelables seront présents s'ils ont déjà étés appellés au moins une fois */
	private Service[] instanciedServices = new Service[ServiceNames.values().length];
	
	/** le serial manager */
	private SerialManager serialManager = null;

	/** le thread manager */
	private ThreadManager threadManager;
	

	/** gestion des log */
	private Log log;
	
	/** gestion de la configuration du robot */
	private Config config;

	/**
	 * Fonction à appeler à la fin du programme.
	 * ferme la connexion serie, termine les différents threads, et ferme le log.
	 */
	public void destructor()
	{
		log.debug("Destruction de container");
		
		// stoppe les différents threads
		stopAllThreads();
		Sleep.sleep(700); // attends qu'ils soient bien tous arrètés
		
		// désasservit le robot
		if(instanciedServices[ServiceNames.SERIAL_WRAPPER.ordinal()] != null)
			try
			{
				log.debug("Désasservissement du robot");
				((Locomotion) instanciedServices[ServiceNames.LOCOMOTION.ordinal()]).enableFeedbackLoop();
			} 
			catch (SerialConnexionException e)
			{
			}
		
		// coupe les connexions séries
		if(serialManager != null)
		{
			if(serialManager.threadSerial != null)
				serialManager.threadSerial.close();
		}
		
		// ferme le log
		log.close();
	}
	
	
	/**
	 * instancie le gestionnaire de dépendances et quelques services critiques
	 * Services instanciés:
	 * 		Config
	 * 		Log
	 * Instancie aussi le ThreadManager.
	 * @throws IOException si le fichier de config du robot ne peut être lu.
	 */
	public Container() throws IOException
	{
		// affiche la configuration avant toute autre chose
		System.out.println("== Container bootstrap ==");
		System.out.println("Loading config from current directory : " +  System.getProperty("user.dir"));
		
		//parse le ficher de configuration.
		instanciedServices[ServiceNames.CONFIG.ordinal()] = (Service)new Config("./config/");
		config = (Config)instanciedServices[ServiceNames.CONFIG.ordinal()];
		
		// démarre le système de log
		instanciedServices[ServiceNames.LOG.ordinal()] = (Service)new Log(config);
		log = (Log)instanciedServices[ServiceNames.LOG.ordinal()];
		
		// instancie le gestionnnaire de thread
		threadManager = new ThreadManager(config, log);
	}

	/**
	 * fourni le service demandé par l'utilisateur
	 * Le service sera instancié lors de sa première demande. 
	 * @param serviceRequested le service demandé par l'utilisateur
	 * @return Le service demandé. Un cast dans le type effectif sera nécéssaire, puisque le type de retour est le type générique Service. Il est innoffensif de faire ce cast.
	 * @throws ContainerException Déclenché si on demande un service inconnu du système de container Cette exception signifie que cette méthode n'est pas a jour vis a vis de l'énummération ServiceNames
	 * @throws SerialManagerException quand le service demandé est une carte et qu'elle n'est pas détecté
	 */
	@SuppressWarnings("unchecked")
	public Service getService(ServiceNames serviceRequested) throws ContainerException, SerialManagerException
	{
    	// instancie le service demandé lors de son premier appel 
    	
    	// si le service est déja instancié, on ne le réinstancie pas
		if(instanciedServices[serviceRequested.ordinal()] != null)
			;
		
		// Si le service n'est pas encore instancié, on l'instancie avant de le retourner à l'utilisateur
		else if(serviceRequested == ServiceNames.TABLE)
			instanciedServices[serviceRequested.ordinal()] = 	(Service)new Table(
																	(Log)getService(ServiceNames.LOG),
																	(Config)getService(ServiceNames.CONFIG)
																);
		else if(serviceRequested == ServiceNames.THREAD_SERIAL) // les séries
		{
			if(serialManager == null)
				serialManager = new SerialManager(log);
			instanciedServices[serviceRequested.ordinal()] = 	(Service)serialManager.getSerial();
		}
		else if(serviceRequested == ServiceNames.SERIAL_WRAPPER)
			instanciedServices[serviceRequested.ordinal()] = 	(Service)new SerialWrapper(
																	(Config)getService(ServiceNames.CONFIG),
																	(Log)getService(ServiceNames.LOG),
																	(ThreadSerial)getService(ServiceNames.THREAD_SERIAL)
																);
		else if(serviceRequested == ServiceNames.HOOK_FACTORY)
			instanciedServices[serviceRequested.ordinal()] = 	(Service)new HookFactory(
																	(Config)getService(ServiceNames.CONFIG),
																	(Log)getService(ServiceNames.LOG),
																	(GameState)getService(ServiceNames.GAME_STATE)
																);
		else if(serviceRequested == ServiceNames.ROBOT)
			instanciedServices[serviceRequested.ordinal()] = 	(Service)new Robot(
																	(Locomotion)getService(ServiceNames.LOCOMOTION),
																	(Config)getService(ServiceNames.CONFIG),
																	(Log)getService(ServiceNames.LOG),
																	(SerialWrapper)getService(ServiceNames.SERIAL_WRAPPER)
																);		
        else if(serviceRequested == ServiceNames.LOCOMOTION)
            instanciedServices[serviceRequested.ordinal()] = 	(Service)new Locomotion(
            														(Log)getService(ServiceNames.LOG),
            														(Config)getService(ServiceNames.CONFIG),
            														(Table)getService(ServiceNames.TABLE),
            														(SerialWrapper)getService(ServiceNames.SERIAL_WRAPPER)
            													);
        else if(serviceRequested == ServiceNames.GAME_STATE)
            instanciedServices[serviceRequested.ordinal()] = 	(Service)new GameState(
            														(Config)getService(ServiceNames.CONFIG),
                                                             		(Log)getService(ServiceNames.LOG),
                                                             		(Table)getService(ServiceNames.TABLE),
                                                             		(Robot)getService(ServiceNames.ROBOT)
                                                             	);
		else if(serviceRequested == ServiceNames.SCRIPT_MANAGER)
			instanciedServices[serviceRequested.ordinal()] = 	(Service)new ScriptManager(
																	(HookFactory)getService(ServiceNames.HOOK_FACTORY),
																	(Config)getService(ServiceNames.CONFIG),
																	(Log)getService(ServiceNames.LOG)
																);
		else if(serviceRequested == ServiceNames.THREAD_TIMER)
			instanciedServices[serviceRequested.ordinal()] = 	(Service)threadManager.getThreadTimer(
																	(Table)getService(ServiceNames.TABLE),
																	(Robot)getService(ServiceNames.ROBOT),
																	(SerialWrapper)getService(ServiceNames.SERIAL_WRAPPER)
                                                                );
		else if(serviceRequested == ServiceNames.THREAD_EVENTS)
			instanciedServices[serviceRequested.ordinal()] = 	(Service)threadManager.getThreadEvents(
																	(Table)getService(ServiceNames.TABLE),
																	(Robot)getService(ServiceNames.ROBOT),
																	(ThreadSerial) getService(ServiceNames.THREAD_SERIAL)
                                                                );
		else if(serviceRequested == ServiceNames.THREAD_SENSOR)
			instanciedServices[serviceRequested.ordinal()] = 	(Service)threadManager.getThreadSensors(
																	(Table)getService(ServiceNames.TABLE),
																	(Robot)getService(ServiceNames.ROBOT),
																	(SerialWrapper)getService(ServiceNames.SERIAL_WRAPPER),
																	(ThreadSerial)getService(ServiceNames.THREAD_SERIAL)
																);


		else if(serviceRequested == ServiceNames.THREAD_INTERFACE)
			instanciedServices[serviceRequested.ordinal()] = 	(Service)threadManager.getThreadInterface(
																	(Config)getService(ServiceNames.CONFIG),
																	(Log)getService(ServiceNames.LOG),
																	(Table)getService(ServiceNames.TABLE),
					     											(Robot)getService(ServiceNames.ROBOT));

		
		// si le service demandé n'est pas connu, alors on log une erreur.
		else
		{
			log.critical("Erreur de getService pour le service (service inconnu): "+serviceRequested);
			throw new ContainerException();
		}
		
		// retourne le service en mémoire à l'utilisateur
		return instanciedServices[serviceRequested.ordinal()];
	}	
		
	/**
	 * Demande au thread manager de démarrer les threads instanciés 
	 * (ie ceux qui ont étés demandés à getService)
	 */
	public void startInstanciedThreads()
	{
		threadManager.startInstanciedThreads();
	}

	/**
	 * Demande au thread manager de démarrer tous les threads
	 */
	public void startAllThreads()
	{
		try {
			getService(ServiceNames.THREAD_SENSOR);
            getService(ServiceNames.THREAD_EVENTS);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			getService(ServiceNames.THREAD_TIMER);
			//getService(ServiceNames.THREAD_INTERFACE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		threadManager.startInstanciedThreads();
	}

	/**
	 * Demande au thread manager d'arrêter tout les threads
	 * Le thread principal (appellant cette méthode) continue son exécution
	 */
	public void stopAllThreads()
	{
		threadManager.stopAllThreads();
	}
	
}
