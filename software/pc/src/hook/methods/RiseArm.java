package hook.methods;


import enums.ActuatorOrder;
import exceptions.serial.SerialConnexionException;
import hook.Executable;
import robot.Robot;
import strategie.GameState;

// permet de lever le bras des poissons au dessus du filet
public class RiseArm implements Executable
{

    @Override
    public boolean execute(GameState<Robot> stateToConsider)
    {
        try {
            stateToConsider.robot.useActuator(ActuatorOrder.MIDDLE_POSITION, false);
        } catch (SerialConnexionException e) {
            e.printStackTrace();
        }
        return false;
    }
}