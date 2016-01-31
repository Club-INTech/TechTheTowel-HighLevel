package hook.methods;

import enums.ActuatorOrder;
import exceptions.serial.SerialConnexionException;
import hook.Executable;
import robot.Robot;
import strategie.GameState;

// permet de prendre les poissons
public class GetFish implements Executable
{

    @Override
    public boolean execute(GameState<Robot> stateToConsider) {
        try {
            stateToConsider.robot.useActuator(ActuatorOrder.FISHING_POSITION, true);

        } catch (SerialConnexionException e) {
            e.printStackTrace();
        }
        return false;
    }
}