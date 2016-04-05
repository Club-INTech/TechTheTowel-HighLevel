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
        mCorrespondenceMap.put(ActuatorOrder.FISHING_POSITION_RIGHT, ActuatorOrder.FISHING_POSITION);
        mCorrespondenceMap.put(ActuatorOrder.MIDDLE_POSITION_RIGHT, ActuatorOrder.MIDDLE_POSITION);
        mCorrespondenceMap.put(ActuatorOrder.RIGHT_MAGNET_DOWN, ActuatorOrder.MAGNET_DOWN);
        mCorrespondenceMap.put(ActuatorOrder.RIGHT_MAGNET_UP, ActuatorOrder.MAGNET_UP);
        mCorrespondenceMap.put(ActuatorOrder.RIGHT_FINGER_DOWN, ActuatorOrder.FINGER_DOWN);
        mCorrespondenceMap.put(ActuatorOrder.RIGHT_FINGER_UP, ActuatorOrder.FINGER_UP);
        mCorrespondenceMap.put(ActuatorOrder.STOP, ActuatorOrder.STOP);
        mCorrespondenceMap.put(ActuatorOrder.ARM_INIT, ActuatorOrder.ARM_INIT);
        mCorrespondenceMap.put(ActuatorOrder.OPEN_DOOR, ActuatorOrder.OPEN_DOOR_LEFT);
        mCorrespondenceMap.put(ActuatorOrder.OPEN_DOOR_LEFT, ActuatorOrder.OPEN_DOOR);
        mCorrespondenceMap.put(ActuatorOrder.CLOSE_DOOR, ActuatorOrder.CLOSE_DOOR_LEFT);
        mCorrespondenceMap.put(ActuatorOrder.CLOSE_DOOR_LEFT, ActuatorOrder.CLOSE_DOOR);
        mCorrespondenceMap.put(ActuatorOrder.START_AXIS, ActuatorOrder.START_AXIS_LEFT);
        mCorrespondenceMap.put(ActuatorOrder.STOP_AXIS, ActuatorOrder.STOP_AXIS_LEFT);
        mCorrespondenceMap.put(ActuatorOrder.START_AXIS_LEFT, ActuatorOrder.START_AXIS);
        mCorrespondenceMap.put(ActuatorOrder.STOP_AXIS_LEFT, ActuatorOrder.STOP_AXIS);
        mCorrespondenceMap.put(ActuatorOrder.STOP_DOOR, ActuatorOrder.STOP_DOOR_LEFT);
        mCorrespondenceMap.put(ActuatorOrder.STOP_DOOR_LEFT, ActuatorOrder.STOP_DOOR);
        mCorrespondenceMap.put(ActuatorOrder.SSTOP, ActuatorOrder.SSTOP);
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
