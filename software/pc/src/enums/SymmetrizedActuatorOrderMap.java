package enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Map contenant un actionneur pour clé, et son symétrique pour valeur
 * @author Etienne
 *
 */
public class SymmetrizedActuatorOrderMap
{
	/** Map contenant un actionneur pour clé, et son symétrique pour valeur */
    Map<ActuatorOrder, ActuatorOrder> mCorrespondenceMap = new HashMap<ActuatorOrder, ActuatorOrder>();
    
    /**
     * construit la map de correspondances
     */
    public SymmetrizedActuatorOrderMap()
    {
    	//TODO associer un actionneur a son symetrique sur le robot (gauche droit)
    	//exemple : mCorrespondenceMap.put(ActuatorOrder.LEFT_CARPET_DROP, ActuatorOrder.RIGHT_CARPET_DROP);

    }
    
    /**
     * 
     * @param order l'actionneur à symétriser
     * @return l'actionneur à symétrisé
     */
    public ActuatorOrder getSymmetrizedActuatorOrder(ActuatorOrder order)
    {
    	return mCorrespondenceMap.get(order);
    }
}
