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
    Map<ContactSensors, ContactSensors> mContactCorrespondenceMap = new HashMap<ContactSensors, ContactSensors>();
    /** Map contenant un capteur pour clé, et son symétrique pour valeur */
    Map<USsensors, USsensors> mUltraSoundCorrespondenceMap = new HashMap<USsensors, USsensors>();
    
    /**
     * construit la map de correspondances
     */
    public SymmetrizedSensorNamesMap()
    {
    	//TODO associer un capteur a son symetrique sur le robot (gauche droit)
    	//exemple : mContactCorrespondenceMap.put(ContactSensors.DOOR_CLOSED, ContactSensors.DOOR_CLOSED_LEFT);
    }
    
    /**
     * 
     * @param sensorName le capteur à symétriser
     * @return le capteur symétrisé
     */
    public ContactSensors getSymmetrizedContactSensorName(ContactSensors contactSensors)
    {
    	return mContactCorrespondenceMap.get(contactSensors);
    }
    
    /**
     * 
     * @param sensorName le capteur à symétriser
     * @return le capteur symétrisé
     */
    public USsensors getSymmetrizedUltraSoundSensorName(USsensors ussensors)
    {
		return mUltraSoundCorrespondenceMap.get(ussensors);
    }
}
