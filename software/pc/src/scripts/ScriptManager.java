package scripts;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import utils.Log;
import utils.Config;
import container.Service;
import exceptions.UnknownScriptException;

 /**
  * Classe enregistrée comme service qui fournira les scripts
  * @author pf, marsu
  */
 
public class ScriptManager implements Service
{
	
	private Log log;

	// pour retrouver un script a partir de son nom
	private Map<String,AbstractScript> instancesScripts = new Hashtable<String,AbstractScript>(); // ce commentaire est inutile

	// TODO : effacer ?
	@SuppressWarnings("unused")
	private ArrayList<String> scripts_robot;
	
	public ScriptManager(Config config, Log log)
	{
		this.log = log;
		scripts_robot = new ArrayList<String>();
	}
	
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
	
	public void updateConfig()
	{
	}

}