package exceptions;


/**
 * Un capteur est bloqué, bloquant le script
 */
public class BlockedActuatorException extends Exception
{
    public BlockedActuatorException()
    {
        super();
    }

    public BlockedActuatorException(String m)
    {
        super(m);
    }
}