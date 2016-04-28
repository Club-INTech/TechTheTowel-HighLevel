package hook.methods;

import hook.Executable;
import robot.Robot;
import strategie.GameState;

/**
* Met le booleen du sable à true
 * */
public class SetSand implements Executable
{

    @Override
    public boolean execute(GameState<Robot> stateToConsider)
    {
        stateToConsider.robot.setIsSandInside(true);
        return false;
    }

}