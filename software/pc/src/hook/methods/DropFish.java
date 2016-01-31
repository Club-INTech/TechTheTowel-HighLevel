package hook.methods;

import enums.ActuatorOrder;
import exceptions.serial.SerialConnexionException;
import hook.Executable;
import robot.Robot;
import strategie.GameState;

// permet de relâcher les poissons
public class DropFish implements Executable
{

    @Override
    public boolean execute(GameState<Robot> stateToConsider) {
        try {
            stateToConsider.robot.useActuator(ActuatorOrder.MAGNET_DOWN, true);
            stateToConsider.robot.useActuator(ActuatorOrder.FINGER_DOWN, true);
            stateToConsider.robot.useActuator(ActuatorOrder.MAGNET_UP, true);
            stateToConsider.robot.useActuator(ActuatorOrder.FINGER_UP, false);
        } catch (SerialConnexionException e) {
            e.printStackTrace();
        }
        return false;
    }
}