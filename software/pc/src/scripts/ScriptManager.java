package scripts;

import container.Service;
import enums.ScriptNames;
import hook.types.HookFactory;
import utils.Config;
import utils.Log;

/**
  * Classe enregistrée comme service qui instancie puis fournira les scripts.
  *
  * @author pf, marsu
  */
 
public class ScriptManager implements Service
{
	
	/** système de log sur lequel écrire. */
	@SuppressWarnings("unused")
	private Log log;

	/** endroit ou lire la configuration du robot */
	@SuppressWarnings("unused")
	private Config config;

	/** Map contenant l'ensemble des scripts instanciés. Permet de retrouver un script via son nom */
	private AbstractScript[] instanciedScripts = new AbstractScript[ScriptNames.values().length];
	
	/**
	 * Instancie le scriptManager
	 * @param factory le générateur de hook a utiliser dans les scripts
	 * @param config the config endroit ou lire la configuration du robot
	 * @param log système de log sur lequel écrire
	 */
	public ScriptManager(HookFactory factory, Config config, Log log)
	{
		this.log = log;
		this.config = config;
		
		// exemple:
		//instanciedScripts[ScriptNames.CLOSE_CLAP.ordinal()] = new CloseClap(factory, config, log);
		instanciedScripts[ScriptNames.DROP_CARPET.ordinal()] = new DropCarpet(factory, config, log);
		instanciedScripts[ScriptNames.CLOSE_DOORS.ordinal()] = new CloseDoors(factory, config, log);
		instanciedScripts[ScriptNames.PARASOL.ordinal()] = new Parasol(factory, config, log);
		instanciedScripts[ScriptNames.FISHING.ordinal()] = new Fishing(factory, config, log);
		instanciedScripts[ScriptNames.PREQUEL.ordinal()] = new Prequel(factory, config, log);
		instanciedScripts[ScriptNames.TECH_THE_SAND.ordinal()] = new TechTheSand(factory, config, log);
		instanciedScripts[ScriptNames.DROP_THE_SAND.ordinal()] = new DropTheSand(factory, config, log);
		instanciedScripts[ScriptNames.SHELL_GETTER.ordinal()] = new ShellGetter(factory, config, log);

		//TODO instancier les scripts
	}
	
	/**
	 * Renvois le script spécifié via son nom
	 *
	 * @param nom le nom du script voulu
	 * @return le script voulu
	 * @throws UnknownScriptException si le script est inconnu.
	 */
	public AbstractScript getScript(ScriptNames nom)
	{
		AbstractScript script = instanciedScripts[nom.ordinal()];
		return script;
	}
	
	/* (non-Javadoc)
	 * @see container.Service#updateConfig()
	 */
	public void updateConfig()
	{
	}

}
