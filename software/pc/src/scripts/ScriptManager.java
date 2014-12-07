package scripts;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import utils.Log;
import utils.Config;
import container.Service;
import exceptions.UnknownScriptException;

 // TODO: Auto-generated Javadoc
/**
  * Classe enregistrée comme service qui fournira les scripts.
  *
  * @author pf, marsu
  */
 
public class ScriptManager implements Service
{
	
	/** système de log sur lequel écrire. */
	private Log log;

	// pour retrouver un script a partir de son nom
	/** The instances scripts. */
	private Map<String,AbstractScript> instancesScripts = new Hashtable<String,AbstractScript>(); // ce commentaire est inutile

	// TODO : effacer ?
	/** The scripts_robot. */
	@SuppressWarnings("unused")
	private ArrayList<String> scripts_robot;
	
	/**
	 * Instantiates a new script manager.
	 *
	 * @param config the config
	 * @param log the log
	 */
	public ScriptManager(Config config, Log log)
	{
		this.log = log;
		scripts_robot = new ArrayList<String>();
	}
	
	/**
	 * Renvois le script spécifié par son nom
	 *
	 * @param nom the nom
	 * @return the script
	 * @throws UnknownScriptException the unknown script exception
	 */
	public AbstractScript getScript(String nom) throws UnknownScriptException
	{
		AbstractScript script = instancesScripts.get(nom);
		if(script == null)
		{
			log.warning("Script inconnu: "+nom, this);
			throw new UnknownScriptException();
		}
		return script;
	}
	
	/* (non-Javadoc)
	 * @see container.Service#updateConfig()
	 */
	public void updateConfig()
	{
	}

}