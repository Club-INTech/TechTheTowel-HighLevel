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
    	//TODO associer un capteur a son symetrique sur le robot (gauche droit)
    	//exemple : mCorrespondenceMap.put(SensorNames.LEFT_ZONE_SENSOR, SensorNames.RIGHT_ZONE_SENSOR);
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
