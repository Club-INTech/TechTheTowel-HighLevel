package hook;

import hook.methods.RiseArm;
import hook.types.HookIsPositionAndOrientationCorrect;
import robot.Robot;
import smartMath.Vec2;
import strategie.GameState;
import utils.Config;
import utils.Log;

public class HookFishing extends HookIsPositionAndOrientationCorrect
{

    public HookFishing(Config config, Log log, GameState<Robot> realState) {
        super(config, log, realState, new Vec2(565, 20), (float)Math.PI, 20, (float)0.05);
        this.addCallback(new Callback(new RiseArm(), false, realState));
    }

}