package enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Map contenant un capteur pour clé, et son symétrique pour valeur
 * @author Etienne
 *
 */
public class SymmetrizedSensorNamesMap
{
	/** Map contenant un capteur pour clé, et son symétrique pour valeur */
    Map<SensorNames, SensorNames> mCorrespondenceMap = new HashMap<SensorNames, SensorNames>();
    
    /**
     * construit la map de correspondances
     */
    public SymmetrizedSensorNamesMap()
    {
    	mCorrespondenceMap.put(SensorNames.JAW_SENSOR, SensorNames.JAW_SENSOR);
    	mCorrespondenceMap.put(SensorNames.LEFT_ZONE_SENSOR, SensorNames.RIGHT_ZONE_SENSOR);
    	mCorrespondenceMap.put(SensorNames.RIGHT_ZONE_SENSOR, SensorNames.LEFT_ZONE_SENSOR);
    	mCorrespondenceMap.put(SensorNames.ULTRASOUND_FRONT_SENSOR, SensorNames.ULTRASOUND_FRONT_SENSOR);
    	mCorrespondenceMap.put(SensorNames.ULTRASOUND_BACK_SENSOR, SensorNames.ULTRASOUND_BACK_SENSOR);
    }
    
    /**
     * 
     * @param sensorName le capteur à symétriser
     * @return le capteur symétrisé
     */
    public SensorNames getSymmetrizedSensorName(SensorNames sensorName)
    {
    	return mCorrespondenceMap.get(sensorName);
    }
}
