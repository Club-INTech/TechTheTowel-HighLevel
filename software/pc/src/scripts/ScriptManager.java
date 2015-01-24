package scripts;

import hook.types.HookFactory;
import utils.Log;
import utils.Config;
import container.Service;
import enums.ScriptNames;

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
		
		//TODO: instancier ici tout les scripts
		// exemple:
		//instanciedScripts[ScriptNames.CLOSE_CLAP.ordinal()] = new CloseClap(factory, config, log);
		
		instanciedScripts[ScriptNames.CLOSE_CLAP.ordinal()] = new CloseClap(factory, config, log);
		instanciedScripts[ScriptNames.DROP_CARPET.ordinal()] = new DropCarpet(factory, config, log);
		instanciedScripts[ScriptNames.EXIT_START_ZONE.ordinal()] = new ExitBeginZone(factory, config, log);
		instanciedScripts[ScriptNames.FREE_STACK.ordinal()] = new DropPile(factory, config, log);
		instanciedScripts[ScriptNames.GRAB_GLASS.ordinal()] = new GetGlass(factory, config, log);
		instanciedScripts[ScriptNames.GRAB_PLOT.ordinal()] = new GetPlot(factory, config, log);
		instanciedScripts[ScriptNames.TAKE_TENNIS_BALL.ordinal()] = new TakeTennisBall(factory, config, log);
		instanciedScripts[ScriptNames.DROP_GLASS.ordinal()] = new DropGlass(factory, config, log);
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
