package strategie;

import container.Service;
import robot.*;
import table.Table;
import utils.Log;
import utils.Config;


/**
 *	Classe de l'IA
 * @author Théo
 */

public class Strategie implements Service
{
	/** système de log sur lequel écrire*/
	private Log log;
	
	/** endroit ou lire la configuration du robot */
	private Config config;
	
	/** La table sur laquelle le robot se déplace */
	private Table table;
	
	/** Les robots sur lequel on travaille :*/
	
	private RobotReal robotReal;
	private RobotChrono RobotChrono;
	
	
	/**Les differents gameState de chaque robot : */
	private  GameState<RobotReal> gameStateRobotReal;
	private  GameState<RobotChrono> gameStateRobotChrono;

	
	 public Strategie()
	 {
	 }


	@Override
	public void updateConfig() {
		table.updateConfig();
        robotReal.updateConfig();		
	}
}
