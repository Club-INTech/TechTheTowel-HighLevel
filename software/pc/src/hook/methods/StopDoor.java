package hook.methods;

import enums.ActuatorOrder;
import exceptions.serial.SerialConnexionException;
import hook.Executable;
import robot.Robot;
import strategie.GameState;

/** permet de stopper la porte */
public class StopDoor implements Executable
{
	@Override
    public boolean execute(GameState<Robot> stateToConsider) 
	{
        try 
        {
            stateToConsider.robot.useActuator(ActuatorOrder.STOP_DOOR, true);
        } 
        catch (SerialConnexionException e) 
        {
            e.printStackTrace();
        }
        return false;
    }
}
