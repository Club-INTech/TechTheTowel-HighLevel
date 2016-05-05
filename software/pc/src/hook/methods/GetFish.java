package hook.methods;

import enums.ActuatorOrder;
import exceptions.serial.SerialConnexionException;
import hook.Executable;
import robot.Robot;
import strategie.GameState;

/** permet de prendre les poissons */
public class GetFish implements Executable
{

    @Override
    public boolean execute(GameState<Robot> stateToConsider) {
        try {
            if(stateToConsider.robot.fishing == 0)
                stateToConsider.robot.useActuator(ActuatorOrder.FISHING_POSITION_LOW, false);
            else if(stateToConsider.robot.fishing == 1)
                stateToConsider.robot.useActuator(ActuatorOrder.FISHING_POSITION_MID, false);
            else
                stateToConsider.robot.useActuator(ActuatorOrder.FISHING_POSITION_HI, false);

        } catch (SerialConnexionException e) {
            e.printStackTrace();
        }
        return false;
    }
}