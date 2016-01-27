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
    	mCorrespondenceMap.put(ActuatorOrder.FISHING_POSITION, ActuatorOrder.FISHING_POSITION_RIGHT);
        mCorrespondenceMap.put(ActuatorOrder.MIDDLE_POSITION, ActuatorOrder.MIDDLE_POSITION_RIGHT);
        mCorrespondenceMap.put(ActuatorOrder.MAGNET_DOWN, ActuatorOrder.RIGHT_MAGNET_DOWN);
        mCorrespondenceMap.put(ActuatorOrder.MAGNET_UP, ActuatorOrder.RIGHT_MAGNET_UP);
        mCorrespondenceMap.put(ActuatorOrder.FINGER_DOWN, ActuatorOrder.RIGHT_FINGER_DOWN);
        mCorrespondenceMap.put(ActuatorOrder.FINGER_UP, ActuatorOrder.RIGHT_FINGER_UP);
        mCorrespondenceMap.put(ActuatorOrder.STOP, ActuatorOrder.STOP);
        mCorrespondenceMap.put(ActuatorOrder.ARM_INIT, ActuatorOrder.ARM_INIT);
        mCorrespondenceMap.put(ActuatorOrder.OPEN_DOOR, ActuatorOrder.OPEN_DOOR_LEFT);
        mCorrespondenceMap.put(ActuatorOrder.CLOSE_DOOR, ActuatorOrder.CLOSE_DOOR_RIGHT);
        mCorrespondenceMap.put(ActuatorOrder.START_AXIS, ActuatorOrder.START_AXIS_LEFT);
        mCorrespondenceMap.put(ActuatorOrder.STOP_AXIS, ActuatorOrder.STOP_AXIS_LEFT);
        mCorrespondenceMap.put(ActuatorOrder.STOP_DOOR, ActuatorOrder.STOP_DOOR_LEFT);
    }
    
    /**
     * 
     * @param order l'actionneur à symétriser
     * @return l'actionneur à symétriser
     */
    public ActuatorOrder getSymmetrizedActuatorOrder(ActuatorOrder order)
    {
    	return mCorrespondenceMap.get(order);
    }
}
