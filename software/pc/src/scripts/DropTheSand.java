package scripts;


import exceptions.ExecuteException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialFinallyException;
import hook.Hook;
import hook.types.HookFactory;
import pathDingDing.PathDingDing;
import robot.Robot;
import smartMath.Circle;
import smartMath.Vec2;
import strategie.GameState;
import utils.Config;
import utils.Log;

import java.util.ArrayList;

/**
 * Script de rapatriement du sable ; utilise le PDD
 * @author CF, discord
 */
public class DropTheSand extends AbstractScript
{
    PathDingDing pf;
    public DropTheSand(HookFactory hf, Config config, Log log)
    {
        super(hf, config, log);
        versions = new Integer[]{0};

    }

    /**
     * Execute le script de rapatriement du sable; nécessite un chemin calculé par le PDD
     * @param versionToExecute version à executer
     * @param actualState le GameState
     * @param hooksToConsider les hooks
     * @throws SerialFinallyException
     * @throws ExecuteException
     */
    @Override
    public void execute(int versionToExecute, GameState<Robot> actualState, ArrayList<Hook> hooksToConsider) throws SerialFinallyException, ExecuteException
    {
        pf = actualState.robot.getPDD();
        if(versionToExecute == 0)
        {
            //TODO appel au PDD pour un chemin, le traiter pour ne tourner que dans 1 sens et l'executer
        }

    }

    @Override
    public int remainingScoreOfVersion(int version, GameState<?> state) {
        return 0;
    }

    @Override
    public Circle entryPosition(int version, int ray, Vec2 robotPosition) {
        if(version == 0)
        {
            return new Circle(0, 1800, 0);
        }
        return new Circle(0,0,0);
    }

    @Override
    public void finalize(GameState<?> state) throws UnableToMoveException, SerialFinallyException {

    }

    @Override
    public Integer[] getVersion(GameState<?> stateToConsider) {
        return new Integer[0];
    }
}