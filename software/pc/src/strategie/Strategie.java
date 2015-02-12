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
	
	/** Endroit où on peut lire la configuration du robot */
	private Config config;
	
	/** La table sur laquelle le robot se déplace */
	private Table table;
	
	/** Les robots sur lequel on travaille :*/
	
	private RobotReal robotReal;
	private RobotChrono robotChrono;
	
	/** Les differents gameState de chaque robot : */
	private GameState<RobotReal> gameStateRobotReal;
	private GameState<RobotChrono> gameStateRobotChrono;
	
	/** Le nombre de points maximal que le robot est capable de faire en un temps infini */
	int maxPointsPossible;
	
	/** Le temps maximum autorisé pour certaines actions : on ne fais pas tel ou tel script si un certain temps est passé*/
	int maxTimeForTakingPlots=60;
	int maxTimeForTakingGlass=70;
	
	/**
     * Crée la strategie, l'IA decisionnelle
     */
	public Strategie(Config config, Log log, Table table, RobotReal robotReal, RobotChrono robotChrono)
	{
		this.config=config;
		this.log=log;
        this.table = table;
        this.robotReal = robotReal;
        this.robotChrono = robotChrono;		
	}

	@Override
	public void updateConfig() 
	{
		table.updateConfig();
        robotReal.updateConfig();
        robotChrono.updateConfig();
	}
	
	public void IA()
	{		
		//tant qu'il reeste des points et que le match n'est pas fini, on prend des decisions :
		while(( gameStateRobotReal.obtainedPoints <  maxPointsPossible ) &&
			  ( gameStateRobotReal.timeEllapsed   <  Integer.parseInt(config.getProperty("temps_match")) )  )
		{
			takeDecision(gameStateRobotReal.timeEllapsed);
		}
	}
	
	/** Fonction principale : prend une decision en prenantt tout en compte */
	public void takeDecision(long timeEllapsed)
	{
		//Gestion 
		if( timeEllapsed > maxTimeForTakingPlots )
		{
			if( timeEllapsed > maxTimeForTakingGlass )
			{
				
				
			}
		}
		else if( timeEllapsed > maxTimeForTakingGlass )
		{
			
		}
	}
}
