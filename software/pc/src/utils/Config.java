package utils;

import container.Service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;


// TODO: Auto-generated Javadoc
/**
 * The Class Config.
 *
 * @author pf, marsu
 */
public class Config implements Service
{
	
	/** The name_local_file. */
	private String name_local_file = "local.ini";
	
	/** The name_config_file. */
	private String name_config_file = "config.ini";
	
	/** The path. */
	private String path;
	
	/** The config. */
	private Properties config = new Properties();
	
	/** The local. */
	private Properties local = new Properties();
	
    /** The e. */
    Enumeration<?> e = local.propertyNames();

	
	/**
	 * Instantiates a new config.
	 *
	 * @param path the path
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public Config(String path) throws IOException
	{
		this.path = path;
	//	log.debug("Loading config from current directory : " +  System.getProperty("user.dir"), this)
		try
		{
			this.config.load(new FileInputStream(this.path+this.name_config_file));
		}
		catch  (IOException e)
		{
			e.printStackTrace();
			throw new IOException("Erreur ouverture de config.ini");
		}
		
		try
		{
			this.config.load(new FileInputStream(this.path+this.name_local_file));
		}
		catch  (IOException e)
		{
			try
			{
				FileOutputStream fileOut = new FileOutputStream(this.path+this.name_local_file);
				this.local.store(fileOut, "Ce fichier est un fichier généré par le programme.\nVous pouvez redéfinir les variables de config.ini dans ce fichier dans un mode de votre choix.\nPS : SopalINT RULEZ !!!\n");
			}
			catch (IOException e2)
			{
				e2.printStackTrace();
				throw new IOException("Erreur création de local.ini");
			}	
			throw new IOException("Erreur ouverture de local.ini");
		}	
		affiche_tout();
	}
	
	/**
	 * Méthode de récupération des paramètres de configuration.
	 *
	 * @param nom the nom
	 * @return the string
	 */
	public String get(String nom)
	{
		String out = null;
		out = config.getProperty(nom);
		if(out == null)
		{
			System.out.println("Erreur config: "+nom+" introuvable.");
		}
		return out;
	}

	/**
	 * Méthode utilisée seulement par les tests.
	 *
	 * @param nom the nom
	 * @param value the value
	 */
	private void set(String nom, String value)
	{
		System.out.println(nom+" = "+value+" (ancienne valeur: "+config.getProperty(nom)+")");
		config.setProperty(nom, value);
	}
	
	/**
	 * Set en version user-friendly.
	 *
	 * @param nom the nom
	 * @param value the value
	 */
	public void set(String nom, Object value)
	{
		System.out.println(nom+" = "+value.toString()+" (ancienne valeur: "+config.getProperty(nom)+")");
		set(nom, value.toString());
	}

	// TODO private
	/**
	 * Affiche_tout.
	 */
	private void affiche_tout()
	{
		if(Boolean.parseBoolean(config.getProperty("affiche_debug")))
		{
			System.out.println("Configuration initiale");
			for(Object o: config.keySet())
				System.out.println(o+": "+config.get(o));
		}
	}
	
	/* (non-Javadoc)
	 * @see container.Service#updateConfig()
	 */
	public void updateConfig()
	{
	}
	
}
