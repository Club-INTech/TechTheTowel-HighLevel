package container;

import java.io.IOException;

import hook.types.HookFactory;
import enums.ServiceNames;
import enums.ServiceNames.ServiceType;
import exceptions.ContainerException;
import exceptions.serial.SerialManagerException;
import utils.*;
import pathDingDing.PathDingDing;
import scripts.ScriptManager;
import strategie.GameState;
import strategie.Strategie;
import table.Table;
import threads.ThreadManager;
import robot.Locomotion;
import robot.RobotReal;
import robot.cards.laser.LaserFiltration;
import robot.cards.laser.LaserCardWrapper;
import robot.cardsWrappers.ActuatorCardWrapper;
import robot.cardsWrappers.LocomotionCardWrapper;
import robot.cardsWrappers.SensorsCardWrapper;
import robot.serial.SerialManager;
import robot.serial.SerialConnexion;

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
	
	/** TODO: Pourquoi ce manager est-il memebre de container ? */
	private SerialManager serialmanager = null;

	/** TODO: Pourquoi ce manager est-il memebre de container ? */
	private ThreadManager threadmanager;
	

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
		// stoppe les différents threads
		stopAllThreads();
		Sleep.sleep(700); // attends qu'ils soient bien tous arrètés
		
		// coupe les connexions séries
		if(serialmanager != null)
		{
			if(serialmanager.serieAsservissement != null)
				serialmanager.serieAsservissement.close();
		}
		
		// ferme le log
		log.close();
	}
	
	
	/**
	 * instancie le gestionnaire de dépendances et quelques services critiques
	 * Services instanciés:
	 * 		Config
	 * 		Log
	 * Instancie aussi le ThreadManager. // TODO: voir si l'on peut proprer cela ( ce n'est pa a priori le role de container puisque ThreadManager n'est pas un service) 
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
		threadmanager = new ThreadManager(config, log); //TODO: pourquoi ce manager est instancié ici alors que le manager des serial ne l'est qu'au premier appel ?
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
		else if(serviceRequested.getType() == ServiceType.SERIAL) // les séries
		{
			if(serialmanager == null)
				serialmanager = new SerialManager(log);
			instanciedServices[serviceRequested.ordinal()] = 	(Service)serialmanager.getSerial(serviceRequested);
		}
		else if(serviceRequested == ServiceNames.LOCOMOTION_CARD_WRAPPER)
			instanciedServices[serviceRequested.ordinal()] = 	(Service)new LocomotionCardWrapper(
																	(Log)getService(ServiceNames.LOG),
																	(SerialConnexion)getService(ServiceNames.STM_CARD)
																);
		else if(serviceRequested == ServiceNames.SENSORS_CARD_WRAPPER)
			instanciedServices[serviceRequested.ordinal()] = 	(Service)new SensorsCardWrapper(
																	(Config)getService(ServiceNames.CONFIG),
																	(Log)getService(ServiceNames.LOG),
																	(SerialConnexion)getService(ServiceNames.STM_CARD)
																);
		else if(serviceRequested == ServiceNames.ACTUATOR_CARD_WRAPPER)
			instanciedServices[serviceRequested.ordinal()] = 	(Service)new ActuatorCardWrapper(
																	(Config)getService(ServiceNames.CONFIG),
															 		(Log)getService(ServiceNames.LOG),
															 		(SerialConnexion)getService(ServiceNames.STM_CARD));
		else if(serviceRequested == ServiceNames.HOOK_FACTORY)
			instanciedServices[serviceRequested.ordinal()] = 	(Service)new HookFactory(
																	(Config)getService(ServiceNames.CONFIG),
																	(Log)getService(ServiceNames.LOG),
																	(GameState<RobotReal>)getService(ServiceNames.GAME_STATE)
																);
		else if(serviceRequested == ServiceNames.ROBOT_REAL)
			instanciedServices[serviceRequested.ordinal()] = 	(Service)new RobotReal(
																	(Locomotion)getService(ServiceNames.LOCOMOTION),
																	(ActuatorCardWrapper)getService(ServiceNames.ACTUATOR_CARD_WRAPPER),
																	(Config)getService(ServiceNames.CONFIG),
																	(Log)getService(ServiceNames.LOG),
																	(PathDingDing)getService(ServiceNames.PATHDINGDING),
																	(SensorsCardWrapper)getService(ServiceNames.SENSORS_CARD_WRAPPER)
																);		
        else if(serviceRequested == ServiceNames.LOCOMOTION)
            instanciedServices[serviceRequested.ordinal()] = 	(Service)new Locomotion(
            														(Log)getService(ServiceNames.LOG),
            														(Config)getService(ServiceNames.CONFIG),
            														(Table)getService(ServiceNames.TABLE),
            														(LocomotionCardWrapper)getService(ServiceNames.LOCOMOTION_CARD_WRAPPER)
            													);
        else if(serviceRequested == ServiceNames.GAME_STATE)
            instanciedServices[serviceRequested.ordinal()] = 	(Service)new GameState<RobotReal>(
            														(Config)getService(ServiceNames.CONFIG),
                                                             		(Log)getService(ServiceNames.LOG),
                                                             		(Table)getService(ServiceNames.TABLE),
                                                             		(RobotReal)getService(ServiceNames.ROBOT_REAL)
                                                             	);
		else if(serviceRequested == ServiceNames.SCRIPT_MANAGER)
			instanciedServices[serviceRequested.ordinal()] = 	(Service)new ScriptManager(
																	(HookFactory)getService(ServiceNames.HOOK_FACTORY),
																	(Config)getService(ServiceNames.CONFIG),
																	(Log)getService(ServiceNames.LOG)
																);
		else if(serviceRequested == ServiceNames.THREAD_TIMER)
			instanciedServices[serviceRequested.ordinal()] = 	(Service)threadmanager.getThreadTimer(
																	(Table)getService(ServiceNames.TABLE),
																	(RobotReal)getService(ServiceNames.ROBOT_REAL),
																	(SensorsCardWrapper)getService(ServiceNames.SENSORS_CARD_WRAPPER),
																	(LocomotionCardWrapper)getService(ServiceNames.LOCOMOTION_CARD_WRAPPER),
	                                                                (ActuatorCardWrapper)getService(ServiceNames.ACTUATOR_CARD_WRAPPER)
                                                                );
		else if(serviceRequested == ServiceNames.THREAD_SENSOR)
			instanciedServices[serviceRequested.ordinal()] = 	(Service)threadmanager.getThreadSensors(
																	(Table)getService(ServiceNames.TABLE),
																	(RobotReal)getService(ServiceNames.ROBOT_REAL),
																	(SensorsCardWrapper)getService(ServiceNames.SENSORS_CARD_WRAPPER)
																);
		else if(serviceRequested == ServiceNames.THREAD_LASER)
			instanciedServices[serviceRequested.ordinal()] = 	(Service)threadmanager.getThreadLaser(
																	(LaserCardWrapper)getService(ServiceNames.LASER),
																	(Table)getService(ServiceNames.TABLE),
																	(LaserFiltration)getService(ServiceNames.LASER_FILTRATION)
																);
		else if(serviceRequested == ServiceNames.LASER)
			instanciedServices[serviceRequested.ordinal()] = 	(Service)new LaserCardWrapper(
																	(Config)getService(ServiceNames.CONFIG),
																	(Log)getService(ServiceNames.LOG),
																	(SerialConnexion)getService(ServiceNames.STM_CARD),
																	(RobotReal)getService(ServiceNames.ROBOT_REAL)
																);
		else if(serviceRequested == ServiceNames.LASER_FILTRATION)
			instanciedServices[serviceRequested.ordinal()] = 	(Service)new LaserFiltration(
																	(Config)getService(ServiceNames.CONFIG),
																	(Log)getService(ServiceNames.LOG)
																);
		else if(serviceRequested == ServiceNames.STRATEGIE)
			instanciedServices[serviceRequested.ordinal()] = 	(Service)new Strategie(
																(Config)getService(ServiceNames.CONFIG),
																(Log)getService(ServiceNames.LOG),
																(GameState<RobotReal>)getService(ServiceNames.GAME_STATE), 
																(ScriptManager)getService(ServiceNames.SCRIPT_MANAGER), 
																(PathDingDing)getService(ServiceNames.PATHDINGDING)
																);
		else if(serviceRequested == ServiceNames.CHECK_UP)
			instanciedServices[serviceRequested.ordinal()] = 	(Service)new CheckUp(
																	(Log)getService(ServiceNames.LOG),
																	(RobotReal)getService(ServiceNames.ROBOT_REAL)
																);
		else if(serviceRequested == ServiceNames.PATHDINGDING)
			instanciedServices[serviceRequested.ordinal()] = 	(Service)new PathDingDing(
																	(Table)getService(ServiceNames.TABLE),
																	(Log)getService(ServiceNames.LOG)
																);
		
		// si le service demandé n'est pas connu, alors on log une erreur.
		else
		{
			log.critical("Erreur de getService pour le service (service inconnu): "+serviceRequested, this);
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
		threadmanager.startInstanciedThreads();
	}

	/**
	 * Demande au thread manager de démarrer tous les threads
	 */
	//TODO: gestion propre des exeptions
	public void startAllThreads()
	{
		// TODO: faire une gestion propre des exceptions
		try {
			//getService(ServiceNames.THREAD_LASER);
		} catch (Exception e) {	
			e.printStackTrace();
		}
		try {
			getService(ServiceNames.THREAD_SENSOR);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			getService(ServiceNames.THREAD_TIMER);
		} catch (Exception e) {
			e.printStackTrace();
		}
		threadmanager.startInstanciedThreads();
	}

	/**
	 * Demande au thread manager d'arrêter tout les threads
	 * Le thread principal (appellant cette méthode) continue son exécution
	 */
	public void stopAllThreads()
	{
		threadmanager.stopAllThreads();
	}
	
}
