package hook.methods;

import enums.ActuatorOrder;
import exceptions.serial.SerialConnexionException;
import hook.Executable;
import robot.Robot;
import strategie.GameState;


/**
 * Created by julia on 04/05/2016.
 */
public class StopDetect implements Executable
{
    @Override
    public boolean execute(GameState<Robot> stateToConsider)
    {
            stateToConsider.robot.setBasicDetection(false);

        return false;
    }
}

