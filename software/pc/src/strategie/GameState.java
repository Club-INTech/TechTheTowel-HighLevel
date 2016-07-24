package strategie;

import container.Service;
import robot.Robot;
import table.Table;
import threads.ThreadTimer;
import utils.Config;
import utils.Log;

/**
 * Classe indiquant l'ensemble des informations sur le match suceptibles d'évoluer durant le match.
 * Le game state contient toutes les informations à connaître pour la stratégie. Il contient:
 * - Robot (real ou chrono), qui apporte des informations sur le robot (position, orientation, ...)
 * - Table, qui apporte des informations sur les obstacles et les éléments de jeux
 * @author marsu
 *
 */

public class GameState implements Service
{    
    /*
     * Les attributs public sont en "final". Cela signifie que les objets
     * peuvent être modifiés mais pas ces références.
     */
	
    /** La table */
    public final Table table;
    
    /** Le robot que l'on fait agir sur cette table. Cela peut être un Robot ou un Robothrono */
    public final Robot robot;

    /** Temps écoulé depuis le début du match en ms */
    private long timeEllapsed;
    
	/** points marqués depus le debut du match */
    public int obtainedPoints;

    /**
     * Instancie un nouvel état de jeu. (il ne représente pas forcément la réalité, il peut être fictif)
     *
     * @param config inutilisé
     * @param log inutilisé
     * @param table l'état de la table a considérer
     * @param robot Le robot a considérer, soit un Robot, soit un Robothrono
     */
    public GameState(Config config, Log log, Table table, Robot robot)
    {
        this.table = table;
        this.robot = robot;
        
        // on n'a marqué aucun point en début de match
        obtainedPoints = 0;
    }

    /* (non-Javadoc)
     * @see container.Service#updateConfig()
     */
    
    @Override
    public void updateConfig()
    {
        table.updateConfig();
        robot.updateConfig();
    }
    

    
    /**
     * temps écoulé depuis le début du match en ms
     * 
	 * @return the time Ellapsed
	 */
	public long getTimeEllapsed()
	{
		timeEllapsed = ThreadTimer.ellapsedTimeSinceMatchStarted();
		return timeEllapsed;
	}

    /**
     * Change le rayon du robot et fait toutes les modifs necesssaires
     * A utiliser dans les scripts et la stratégie
     * @param newRad le nouveau rayon
     */
    public void changeRobotRadius(int newRad)
    {
        this.robot.setRobotRadius(newRad);
        this.table.getObstacleManager().updateObstacles(newRad);
    }
	
}
