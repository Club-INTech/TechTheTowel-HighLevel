package hook.methods;

import enums.ActuatorOrder;
import exceptions.serial.SerialConnexionException;
import hook.Executable;
import robot.Robot;
import strategie.GameState;

/** permet de démarrer l'axe */
public class StartAxis implements Executable
{
	@Override
    public boolean execute(GameState<Robot> stateToConsider) 
	{
        try 
        {
            stateToConsider.robot.useActuator(ActuatorOrder.START_AXIS,false);
        } 
        catch (SerialConnexionException e) 
        {
            e.printStackTrace();
        }
        return false;
    }

}
