package hook.methods;

import enums.ActuatorOrder;
import exceptions.serial.SerialConnexionException;
import hook.Executable;
import robot.Robot;
import strategie.GameState;

/** permet d'arrêter l'axe */
public class StopAxis implements Executable
{
	@Override
    public boolean execute(GameState<Robot> stateToConsider) 
	{
        try 
        {
            stateToConsider.robot.useActuator(ActuatorOrder.STOP_AXIS, false);
        } 
        catch (SerialConnexionException e) 
        {
            e.printStackTrace();
        }
        return false;
    }
}
