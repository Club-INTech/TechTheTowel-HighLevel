package utils;

import java.io.FileWriter;
import java.io.PrintStream;
import java.util.Calendar;
import java.util.GregorianCalendar;

import container.Service;

/**
 * Service de log, affiche à l'écran et enregistre dans des fichiers de logs des informations avec différents niveaux de couleurs.
 *
 * @author pf, marsu
 */

public class Log implements Service
{
	/** fichier de configuration pour le match. */
	private Config config;

	/** Redirecteur de chaine de caractères vers le fichier de log. */
	FileWriter writer = null;

	/** Préfixe donnant la couleur en console des messages de debug */
	private String 	debugPrefix 	= "Dbg - \u001B[32m";

	/** Préfixe donnant la couleur en console des messages de warning */
	private String 	warningPrefix 	= "Warn - \u001B[33m";

	/** Préfixe donnant la couleur en console des messages critiques */
	private String 	criticalPrefix = "Critical - \u001B[31m";

	/** Vrai s'il faut afficher les messages sur la sortie standard (prend du temps CPU), faux sinon. */
	private boolean printLogs = true;
	
	/** Vrai s'il faut sauvegarder les logs dans un fichier. */
	private boolean saveLogs = false;

	
	/**
	 * Instancie un nouvveau service de log
	 *
	 * @param config fichier de configuration pour le match.
	 */
	public Log(Config config)
	{
		this.config = config;
		
		updateConfig();
		
		// crée le fichier de log si on spécifie d'écrire dans un fichcier les logs du robot
		if(saveLogs)
			try 
			{
				java.util.GregorianCalendar calendar = new GregorianCalendar();
				String heure = calendar.get(Calendar.HOUR)+":"+calendar.get(Calendar.MINUTE)+":"+calendar.get(Calendar.SECOND);
				writer = new FileWriter("logs/LOG-"+heure+".txt", true); 
			}
			catch(Exception e)
			{
				critical(e, this);
			}
		debug("Service de log démarré", this);
	
	}
		
	/**
	 * Méthode à appeler uniquement depuis une méthode statique.
	 *
	 * @param message the message
	 */
	public void appel_static(String message)
	{
		writeToLog("AppelStatic: "+message, debugPrefix, System.out);
	}
	
	
	/**
	 * Affichage de debug, en vert. User-friendly
	 *
	 * @param message message a logguer
	 * @param objet l'objet demandant le log
	 */
	public void debug(Object message, Object objet)
	{
		debug(message.toString(), objet);
	}
	
	/**
	 * Affichage de debug, en vert.
	 *
	 * @param message message a logguer
	 * @param objet l'objet demandant le log
	 */
	public void debug(String message, Object objet)
	{
		if(printLogs)
			writeToLog(objet.getClass().getName()+": "+message, debugPrefix, System.out);
	}

	/**
	 * Affichage de warnings, en orange. User-friendly
	 * 
	 * @param message message a logguer
	 * @param objet l'objet demandant le log
	 */
	public void warning(Object message, Object objet)
	{
		warning(message.toString(), objet);
	}

	/**
	 * Affichage de warnings, en orange.
	 * 
	 * @param message message a logguer
	 * @param objet l'objet demandant le log
	 */
	public void warning(String message, Object objet)
	{
		writeToLog(objet.getClass().getName()+": "+message, warningPrefix, System.out);
	}

	/**
	 * Affichage d'erreurs critiques, en rouge. User-friendly
	 *
	 * @param message message a logguer
	 * @param objet l'objet demandant le log
	 */
	public void critical(Object message, Object objet)
	{
		critical(message.toString(), objet);
	}
	
	/**
	 * Affichage d'erreurs critiques, en rouge.
	 *
	 * @param message message a logguer
	 * @param objet l'objet demandant le log
	 */
	public void critical(String message, Object objet)
	{
		writeToLog(objet.getClass().getName()+": "+message, criticalPrefix, System.err);
	}

	/**
	 * loggue pour de vrai le massage.
	 * APrès appele de cette méthode, le message été loggué en fonction de la configuration.
	 *
	 * @param message message a logguer
	 * @param prefix le préfixe a rajouter, avant que l'heure ne soit mise
	 * @param logPrinter ou afficher sur l'écran le log
	 */
	private void writeToLog(String message, String prefix, PrintStream logPrinter)
	{
		// trouve l'heure pour la rajouter dans le message de log
		java.util.GregorianCalendar calendar = new GregorianCalendar();
		String heure = calendar.get(Calendar.HOUR)+":"+calendar.get(Calendar.MINUTE)+":"+calendar.get(Calendar.SECOND)+","+calendar.get(Calendar.MILLISECOND);
		
		
		if(prefix != debugPrefix || printLogs)
			logPrinter.println(prefix+heure+" "+message+"\u001B[0m"); // suffixe en \u001B[0m pour que la prochiane ligne soit blanche si on ne spécifie rien
		if(saveLogs)
			writeToFile(prefix+heure+" "+message+"\u001B[0m"); // suffixe en \u001B[0m pour que la prochiane ligne soit blanche si on ne spécifie rien
	}
	
	/**
	 * Ecrit le message spécifié dans le fichier de log
	 *
	 * @param message le message a logguer
	 */
	private void writeToFile(String message)
	{
		// chaque message sur sa propre ligne
		message += "\n";
		
		try
		{
		     writer.write(message,0,message.length());
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
	}

	/**
	 * Sorte de destructeur, dans lequel le fichier est sauvegardé.
	 * // TODO: refuser les demande d'écriture sur le log si l'initialisation n'a pas été faite, ou si le destructeur a été appellé 
	 */
	public void close()
	{
		warning("Fin du log",this);
		
		if(saveLogs)
			try {
				debug("Sauvegarde du fichier de logs", this);
				if(writer != null)
					writer.close();
			}
			catch(Exception e)
			{
				System.out.println(e);
			}
	}
	
	/* (non-Javadoc)
	 * @see container.Service#updateConfig()
	 */
	@Override
	public void updateConfig()
	{
		try
		{
			// vérifie s'il faut afficher les logs a l'écran
			printLogs = Boolean.parseBoolean(this.config.getProperty("affiche_debug"));
		}
		catch(Exception e)
		{
			critical(e, this);
		}
		try
		{
			// vérifie s'il faut écrire les logs dans un fichier
			saveLogs = Boolean.parseBoolean(this.config.getProperty("sauvegarde_fichier"));
			// TODO: mettre ici ouverture/fermeture de fichier si la valeur de sauvegarde_fichier change
		}
		catch(Exception e)
		{
			critical(e, this);
		}
	}

}
