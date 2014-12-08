package utils;

import container.Service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Service de configuration du robot.
 * Cette classe lit le fichier  /pc/config/config.ini pour en extraire les informations de configuration et les redistribuer a qui les demandera
 *
 * @author pf, marsu
 */
public class Config implements Service
{
	// TODO: trouver a quoi sert ce fichier
	/** Nom du fichier local. */
	private String localFileName = "local.ini";
	
	/** Nom du fichier de configuration a charger. */
	private String configFileName = "config.ini";
	
	/** chemin relatif au chemin d'exécution d'ou charger le fichier de config */
	private String path;
	
	/** Le fichier de configuration, une fois parsé par le classe builltin de java. */
	private Properties configProperties = new Properties();

	/** Le fichier local, une fois parsé par le classe builltin de java. */
	private Properties localProperties = new Properties();
	

	
	/**
	 * fait un nouveau gestionnaire de configuration
	 *
	 * @param path endroit ou trouver le fichier de configuration
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public Config(String path) throws IOException
	{
		this.path = path;
		System.out.println("Loading config from current directory : " +  System.getProperty("user.dir") + path);
		try
		{
			this.configProperties.load(new FileInputStream(this.path+this.configFileName));
		}
		catch  (IOException e)
		{
			e.printStackTrace();
			throw new IOException("Erreur ouverture de config.ini Le chemin d'exécution du programme est-il bien dans /pc ?");
		}
		
	
		try
		{
			this.configProperties.load(new FileInputStream(this.path+this.localFileName));
		}
		catch  (IOException e)
		{
			try
			{
				FileOutputStream fileOut = new FileOutputStream(this.path+this.localFileName);
				this.localProperties.store(fileOut, "Ce fichier est un fichier généré par le programme.\nVous pouvez redéfinir les variables de config.ini dans ce fichier dans un mode de votre choix.\nPS : SopalINT RULEZ !!!\n");
			}
			catch (IOException e2)
			{
				e2.printStackTrace();
				throw new IOException("Erreur création de local.ini");
			}	
			throw new IOException("Erreur ouverture de local.ini");
		}	
		printConfigFile();
	}
	
	// TODO: configPropertyNotFoundException pour prévenir l'utilisateur quand il essaye d'obtenir une propriété du fichier de config qui n'existe pas
	// TODO: encore mieux, faire une enummération de tout ce qui peut être appelleé comme property, et changer le type du paramètre de getProperty en cette enum
	/**
	 * Méthode que tout le monde utilise de récupération des paramètres de configuration.
	 *
	 * @param nom nom de la propriété a récupérer
	 * @return Leparamètre coresspondant a ce nom
	 */
	public String getProperty(String nom)
	{
		// Pour la correction de Deboc
		if(nom == "debocIntegralCorrectionBehavior")
				return System.getProperty("user.name").startsWith("kar") ? "disabled" : "default";
		
		String out = null;
		out = configProperties.getProperty(nom);
		if(out == null)
		{
			System.out.println("Erreur config, la propriété nommée '"+nom+"' est introuvable.");
		}
		return out;
	}

	/**
	 * Change la valeur d'un parmaètre de configuration.
	 * Le changement sera perdu si le programme redémarre.
	 * Méthode utilisée seulement par les tests.
	 *
	 * @param nom nom de la propriété a modifier
	 * @param value la nouvelle valeur a lui donner
	 */
	public void set(String nom, String value)
	{
		System.out.println("Changement de config: " +nom+" = "+value+" (ancienne valeur: "+configProperties.getProperty(nom)+")");
		configProperties.setProperty(nom, value);
	}

	/**
	 * Affiche tout le fichier de config.
	 * Si la valeur de config affiche_debug est a false, le fichier de config ne sera pas affiché, même si cette méthode est appellée
	 */
	private void printConfigFile()
	{
		if(Boolean.parseBoolean(configProperties.getProperty("affiche_debug")))
		{
			System.out.println("Configuration initiale");
			
			// imprime chaque propriété
			for(Object o: configProperties.keySet())
				System.out.println(o+": "+configProperties.get(o));
		}
	}
	
	/* (non-Javadoc)
	 * @see container.Service#updateConfig()
	 */
	public void updateConfig()
	{
	}
	
}
