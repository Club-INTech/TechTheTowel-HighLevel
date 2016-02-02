package hook.methods;

import enums.ActuatorOrder;
import exceptions.serial.SerialConnexionException;
import hook.Executable;
import robot.Robot;
import strategie.GameState;

/** permet de fermer la porte */
public class OpenDoor implements Executable
{
	@Override
    public boolean execute(GameState<Robot> stateToConsider) 
	{
        try 
        {
            stateToConsider.robot.useActuator(ActuatorOrder.OPEN_DOOR,false);
        } 
        catch (SerialConnexionException e) 
        {
            e.printStackTrace();
        }
        return false;
    }
}
