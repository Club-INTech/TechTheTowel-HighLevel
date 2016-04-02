package enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Symétrisation des "TurningStrategy"
 * @author Discord
 *
 */
public class SymmetrizedTurningStrategy
{
    /** Map contenant une stratégie pour clé, et son symétrique pour valeur */
    Map<TurningStrategy, TurningStrategy> mCorrespondenceMap = new HashMap<TurningStrategy, TurningStrategy>();

    /**
     * construit la map de correspondances
     */
    public SymmetrizedTurningStrategy()
    {
        mCorrespondenceMap.put(TurningStrategy.FASTEST, TurningStrategy.FASTEST);
        mCorrespondenceMap.put(TurningStrategy.LEFT_ONLY, TurningStrategy.RIGHT_ONLY);
        mCorrespondenceMap.put(TurningStrategy.RIGHT_ONLY, TurningStrategy.LEFT_ONLY);
    }


    public TurningStrategy getSymmetrizedTurningStrategy(TurningStrategy order)
    {
        return mCorrespondenceMap.get(order);
    }
}
