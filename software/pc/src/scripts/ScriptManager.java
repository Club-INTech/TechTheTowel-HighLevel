package scripts;

import utils.Log;
import utils.Config;
import container.Service;
import enums.ScriptNames;
import exceptions.UnknownScriptException;

/**
  * Classe enregistrée comme service qui instancie puis fournira les scripts.
  *
  * @author pf, marsu
  */
 
public class ScriptManager implements Service
{
	
	/** système de log sur lequel écrire. */
	private Log log;

	/** endroit ou lire la configuration du robot */
	@SuppressWarnings("unused")
	private Config config;

	// TODO: faire une enum des scripts plutot que de demander le nom en tant que string
	/** Map contenant l'ensemble des scripts instanciés. Permet de retrouver un script via son nom */
	private AbstractScript[] instanciedScripts = new AbstractScript[ScriptNames.values().length];
	
	/**
	 * Instancie le scriptManager
	 *
	 * @param config the config endroit ou lire la configuration du robot
	 * @param log système de log sur lequel écrire
	 */
	public ScriptManager(Config config, Log log)
	{
		this.log = log;
		this.config = config;
		
		//TODO: instancier ici tout les scripts
		// exemple:
//		AbstractScript[ScriptNames.SCRIPT_PLOT.ordinal()] = new ScriptPlot();
	}
	
	/**
	 * Renvois le script spécifié via son nom
	 *
	 * @param nom le nom du script voulu
	 * @return le script voulu
	 * @throws UnknownScriptException si le script est inconnu.
	 */
	public AbstractScript getScript(ScriptNames nom) throws UnknownScriptException
	{
		AbstractScript script = instanciedScripts[nom.ordinal()];
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