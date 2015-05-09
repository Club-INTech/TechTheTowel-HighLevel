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
    	mCorrespondenceMap.put(ActuatorOrder.LEFT_CARPET_DROP, ActuatorOrder.RIGHT_CARPET_DROP);
    	mCorrespondenceMap.put(ActuatorOrder.RIGHT_CARPET_DROP, ActuatorOrder.LEFT_CARPET_DROP);
    	mCorrespondenceMap.put(ActuatorOrder.LEFT_CARPET_FOLDUP, ActuatorOrder.RIGHT_CARPET_FOLDUP);
    	mCorrespondenceMap.put(ActuatorOrder.RIGHT_CARPET_FOLDUP, ActuatorOrder.LEFT_CARPET_FOLDUP);
    	mCorrespondenceMap.put(ActuatorOrder.HIGH_LEFT_CLAP, ActuatorOrder.HIGH_RIGHT_CLAP);
    	mCorrespondenceMap.put(ActuatorOrder.HIGH_RIGHT_CLAP, ActuatorOrder.HIGH_LEFT_CLAP);
    	mCorrespondenceMap.put(ActuatorOrder.MID_LEFT_CLAP, ActuatorOrder.MID_RIGHT_CLAP);
    	mCorrespondenceMap.put(ActuatorOrder.MID_RIGHT_CLAP, ActuatorOrder.MID_LEFT_CLAP);
    	mCorrespondenceMap.put(ActuatorOrder.LOW_LEFT_CLAP, ActuatorOrder.LOW_RIGHT_CLAP);
    	mCorrespondenceMap.put(ActuatorOrder.LOW_RIGHT_CLAP, ActuatorOrder.LOW_LEFT_CLAP);
    	mCorrespondenceMap.put(ActuatorOrder.OPEN_RIGHT_GUIDE, ActuatorOrder.OPEN_LEFT_GUIDE);
    	mCorrespondenceMap.put(ActuatorOrder.OPEN_LEFT_GUIDE, ActuatorOrder.OPEN_RIGHT_GUIDE);
    	mCorrespondenceMap.put(ActuatorOrder.MID_RIGHT_GUIDE, ActuatorOrder.MID_LEFT_GUIDE);
    	mCorrespondenceMap.put(ActuatorOrder.MID_LEFT_GUIDE, ActuatorOrder.MID_RIGHT_GUIDE);
    	mCorrespondenceMap.put(ActuatorOrder.CLOSE_RIGHT_GUIDE, ActuatorOrder.CLOSE_RIGHT_GUIDE);
    	mCorrespondenceMap.put(ActuatorOrder.CLOSE_LEFT_GUIDE, ActuatorOrder.CLOSE_LEFT_GUIDE);
    	mCorrespondenceMap.put(ActuatorOrder.ELEVATOR_HIGH, ActuatorOrder.ELEVATOR_HIGH);
    	mCorrespondenceMap.put(ActuatorOrder.ELEVATOR_STAGE, ActuatorOrder.ELEVATOR_STAGE);
    	mCorrespondenceMap.put(ActuatorOrder.ELEVATOR_ON_STAGE, ActuatorOrder.ELEVATOR_ON_STAGE);
    	mCorrespondenceMap.put(ActuatorOrder.ELEVATOR_LOW, ActuatorOrder.ELEVATOR_LOW);
    	mCorrespondenceMap.put(ActuatorOrder.ELEVATOR_GROUND, ActuatorOrder.ELEVATOR_GROUND);
    	mCorrespondenceMap.put(ActuatorOrder.ELEVATOR_OPEN_JAW, ActuatorOrder.ELEVATOR_OPEN_JAW);
    	mCorrespondenceMap.put(ActuatorOrder.ELEVATOR_CLOSE_JAW, ActuatorOrder.ELEVATOR_CLOSE_JAW);
    	mCorrespondenceMap.put(ActuatorOrder.ELEVATOR_OPEN_JAW_RIGHT, ActuatorOrder.ELEVATOR_OPEN_JAW_LEFT);
    	mCorrespondenceMap.put(ActuatorOrder.ELEVATOR_OPEN_JAW_LEFT, ActuatorOrder.ELEVATOR_OPEN_JAW_RIGHT);
    	mCorrespondenceMap.put(ActuatorOrder.ELEVATOR_CLOSE_JAW_RIGHT, ActuatorOrder.ELEVATOR_CLOSE_JAW_LEFT);
    	mCorrespondenceMap.put(ActuatorOrder.ELEVATOR_CLOSE_JAW_LEFT, ActuatorOrder.ELEVATOR_CLOSE_JAW_RIGHT);
    	mCorrespondenceMap.put(ActuatorOrder.ARM_RIGHT_OPEN, ActuatorOrder.ARM_LEFT_OPEN);
    	mCorrespondenceMap.put(ActuatorOrder.ARM_LEFT_OPEN, ActuatorOrder.ARM_RIGHT_OPEN);
    	mCorrespondenceMap.put(ActuatorOrder.ARM_RIGHT_CLOSE, ActuatorOrder.ARM_LEFT_CLOSE);
    	mCorrespondenceMap.put(ActuatorOrder.ARM_LEFT_CLOSE, ActuatorOrder.ARM_RIGHT_CLOSE);
    	mCorrespondenceMap.put(ActuatorOrder.ARM_RIGHT_OPEN_SLOW, ActuatorOrder.ARM_LEFT_OPEN_SLOW);
    	mCorrespondenceMap.put(ActuatorOrder.ARM_LEFT_OPEN_SLOW, ActuatorOrder.ARM_RIGHT_OPEN_SLOW);
    	mCorrespondenceMap.put(ActuatorOrder.ARM_RIGHT_CLOSE_SLOW, ActuatorOrder.ARM_LEFT_CLOSE_SLOW);
    	mCorrespondenceMap.put(ActuatorOrder.ARM_LEFT_CLOSE_SLOW, ActuatorOrder.ARM_RIGHT_CLOSE_SLOW);
    	mCorrespondenceMap.put(ActuatorOrder.ARM_RIGHT_MIDDLE, ActuatorOrder.ARM_LEFT_MIDDLE);
    	mCorrespondenceMap.put(ActuatorOrder.ARM_LEFT_MIDDLE, ActuatorOrder.ARM_RIGHT_MIDDLE);
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
