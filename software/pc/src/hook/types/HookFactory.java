package hook.types;

import container.Service;
import exceptions.ConfigPropertyNotFoundException;
import hook.Hook;
import hook.HookFishing;
import robot.Robot;
import smartMath.Vec2;
import strategie.GameState;
import utils.Config;
import utils.Log;

/**
 * Service fabriquant des hooks à la demande.
 * @author pf, marsu, théo
 *
 */
public class HookFactory implements Service
{
	
	/**  endroit ou lire la configuration du robot. */
	private Config config;

	/**  système de log a utiliser. */
	private Log log;
	
	/**  robot a surveiller pour le déclenchement des hooks. */
	private GameState<Robot> realState;
	
	/**  la valeur de 20 est en mm, elle est remplcée par la valeur spécifié dans le fichier de config s'il y en a une. */
	private int positionTolerancy = 20;
	
	/** spécifie de quelle couleur est le robot (vert ou violet). Uniquement donné par le fichier de config. */ // TODO: en faire une enum
	String color;
	
	
	
	/**
	 *  appellé uniquement par Container.
	 *  Initialise la factory de hooks.
	 * 
	 * @param config fichier de config du match
	 * @param log système de log
	 * @param realState état du jeu
	 */
	public HookFactory(Config config, Log log, GameState<Robot> realState)
	{
		this.config = config;
		this.log = log;
		this.realState = realState;
		updateConfig();
	}

	/*
	 * (non-Javadoc)
	 * @see container.Service#updateConfig()
	 */
	public void updateConfig()
	{
		try
		{
			
			// demande la couleur du robot pour ce match
			color = config.getProperty("couleur");
			
			// demande avec quelle tolérance sur la précision on déclenche les hooks
			positionTolerancy = Integer.parseInt(this.config.getProperty("hooks_tolerance_mm"));		
	
		}
		catch (ConfigPropertyNotFoundException e)
		{
    		log.debug("Revoir le code : impossible de trouver la propriété "+e.getPropertyNotFound());;
		}
	}
	
	/* ======================================================================
	 * 							Hooks de position
	 * ======================================================================
	 */
	
	//TODO Hooks
    public Hook fishingHook = new HookFishing(this.config, this.log, this.realState);
	

	
	/* ======================================================================
	 * 							Hooks d'abscisse (sur X)
	 * ======================================================================
	 */
	

    
    

	/* ======================================================================
	 * 							Hook d'ordonnée (sur Y)
	 * ======================================================================
	 */
    


    /* ======================================================================
   	 * 							Hooks de position et orientation
   	 * ======================================================================
   	 */
    
    /**s
     * 
     * @param point : point de declenchement du hook
     * @param orientation : orientation de decle,chement du hook 
     * @param tolerancyPoint : tolerance sur la distance au point : ne se declenche que si le robot est proche du point
     * @param tolerancyOrientation : tolerance sur l'orientation du robot, le hook ne se declence que si la difference entre l'orrientation actuelle et l'orientation voulue est inferieure à cette toelrance
     * @return le hook
     */
    
    public Hook newHookIsPositionAndOrientationCorrect(Vec2 point, float orientation, float tolerancyPoint, float tolerancyOrientation) 
    {
    	return new HookIsPositionAndOrientationCorrect(config, log, realState, point, orientation , tolerancyPoint, tolerancyOrientation);
    }
}
