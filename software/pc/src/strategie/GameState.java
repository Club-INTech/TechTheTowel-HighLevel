package strategie;

import container.Service;
import robot.Robot;
import table.Table;
import utils.Log;
import utils.Config;

/**
 * Classe indiquant l'ensemble des informations sur le match suceptibles d'évoluer durant le match.
 * Le game state contient toutes les informations à connaître pour la stratégie. Il contient:
 * - Robot (real ou chrono), qui apporte des informations sur le robot (position, orientation, ...)
 * - Table, qui apporte des informations sur les obstacles et les éléments de jeux
 * @author marsu
 *
 * @param <R> R est soit un RobotReal, soit un RobotChrono, en fonction du but de cette instance de GameState: claculer des temps d'exécution dans un certain contexte, ou bien faire agir le vrai robot
 */

public class GameState<R extends Robot> implements Service
{    
    /*
     * Les attributs public sont en "final". Cela signifie que les objets
     * peuvent être modifiés mais pas ces références.
     */
	
    /** La table */
    public final Table table;
    
    /** The robot. */
    public final R robot;

    /** Temps écoulé depuis le début du match en ms */
    public long timeEllapsed;
    
    /** points marqués depus le debut du match */
    public int pointsObtenus;

    /**
     * Instancie un nouvel état de jeu. (il ne représente pas forcément la réalité, il peut être fictif)
     *
     * @param config inutilisé
     * @param log inutilisé
     * @param table l'état de la table a considérer
     * @param robot Le robot a considérer, soit un RobotReal, soit un RobotChrono
     */
    public GameState(Config config, Log log, Table table, R robot)
    {
        this.table = table;
        this.robot = robot;
        
        // on n'a marqué aucun point en début de match
        pointsObtenus = 0;
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
    
}
