package hook.types;

import container.Service;
import exceptions.ConfigPropertyNotFoundException;
import hook.Hook;
import robot.RobotReal;
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
	private GameState<RobotReal> realState;
	
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
	public HookFactory(Config config, Log log, GameState<RobotReal> realState)
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
    public Hook newPositionHook(Vec2 position, float orientation, float tolerancyPos, float tolerancyOr)
	{
		return new HookIsPositionAndOrientationCorrect(config, log, realState, position, orientation, tolerancyPos, tolerancyOr);
	}
	
	/* ======================================================================
	 * 							Hooks d'abscisse (sur X)
	 * ======================================================================
	 */

    /** Hook déclenché pour un position en abscisse inférieure à celle donnée en argument
     * @param XValue argument */
	public Hook newXLesserHook(int XValue)
	{
		return new HookXLesser(config, log, realState, XValue);
	}
	
	/** Hook déclenché pour une position en abscisse supérieure à celle donnée en argument
	 * @param XValue argument en mm */
	public Hook newXGreaterHook(int XValue)
	{
		return new HookXGreater(config, log, realState, XValue);
	}
    
    

	/* ======================================================================
	 * 							Hook d'ordonnée (sur Y)
	 * ======================================================================
	 */
    
	/** Hook déclenché pour une position en ordonnée inférieure à celle donnée en argument
	 * @param Yvalue argument en mm */
	public Hook newYLesserHook(int Yvalue)
	{
		return new HookYLesser(config, log, realState, Yvalue);
	}
	
	/** Hook déclenché pour une position en ordonnée supérieure à celle donnée en argument
	 * @param Yvalue argument en mm */
	public Hook newYGreaterHook(int Yvalue)
	{
		return new HookYGreater(config, log, realState, Yvalue);
	}

    /* ======================================================================
   	 * 							Hooks de position et orientation
   	 * ======================================================================
   	 */
    
    /**s
     * 
     * @param point : point de declenchement du hook
     * @param orientation : orientation de decle,chement du hook 
     * @param tolerancyPoint : tolerance sur la distance au point : ne se declenche que si le robot est proche du point
     * @param tolerancyOrientation : tolerance sur l'orientation du robot, le hook ne se declence que si la difference entre l'orientation actuelle et l'orientation voulue est inferieure à cette toelrance
     * @return le hook
     */
    public Hook newHookIsPositionAndOrientationCorrect(Vec2 point, float orientation, float tolerancyPoint, float tolerancyOrientation) 
    {
    	return new HookIsPositionAndOrientationCorrect(config, log, realState, point, orientation , tolerancyPoint, tolerancyOrientation);
    }
    
    /** Hook déclenché pour une position donnée, avec une tolérance donnée
     * @param pos argument
     * @param tolerancy argument en mm*/
    public Hook newPositionCorrectHook(Vec2 pos, float tolerancy)
    {
    	return new HookPositionCorrect(config, log, realState, pos, tolerancy);
    }
    
    /** Hook déclenché pour une orientation donnée, avec une tolérance donnée
     * @param orientation argument en radians
     * @param tolerancy argument en radians*/
    public Hook newOrientationCorrectHook(float orientation, float tolerancy)
    {
    	return new HookOrientationCorrect(config, log, realState, orientation, tolerancy);
    }
    
    /** Hook déclenché pour une orientation inférieure à celle donnée en argument
     * @param orientation argument en radians*/
    public Hook newOrientationLesserHook(float orientation)
    {
    	return new HookOrientationLesser(config, log, realState, orientation);
    }
    
    /** Hook déclenché pour une orientation supérieure à celle donnée en argument
     * @param orientation argument en radians*/
    public Hook newOrientationGreaterHook(float orientation)
    {
    	return new HookOrientationGreater(config, log, realState, orientation);
    }
}
