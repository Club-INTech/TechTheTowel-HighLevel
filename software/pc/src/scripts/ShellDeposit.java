package scripts;


import enums.ActuatorOrder;
import enums.DirectionStrategy;
import enums.TurningStrategy;
import exceptions.BadVersionException;
import exceptions.ExecuteException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialFinallyException;
import hook.Hook;
import hook.types.HookFactory;
import robot.Robot;
import smartMath.Circle;
import smartMath.Vec2;
import strategie.GameState;
import table.Shell;
import table.Table;
import utils.Config;
import utils.Log;

import java.util.ArrayList;
import java.util.Map;

public class ShellDeposit extends AbstractScript
{



    /**
     * Constructeur a appeller lorsque un script héritant de la classe AbstractScript est instancié.
     * Le constructeur se charge de renseigner la hookFactory, le système de config et de log.
     *
     * @param hookFactory La factory a utiliser pour générer les hooks dont pourra avoir besoin le script
     * @param config      le fichier de config a partir duquel le script pourra se configurer
     * @param log         le système de log qu'utilisera le script
     */
    public ShellDeposit(HookFactory hookFactory, Config config, Log log) {
        super(hookFactory, config, log);
        versions = new Integer[]{0};
    }

    @Override
    public void execute(int versionToExecute, GameState<Robot> actualState, ArrayList<Hook> hooksToConsider) throws SerialFinallyException, ExecuteException {
        if(versionToExecute == 0)
        {
            try {

                actualState.robot.shellsOnBoard = false;

                actualState.robot.turn(Math.PI/2);

                actualState.robot.setDirectionStrategy(DirectionStrategy.FASTEST);

                actualState.robot.setTurningStrategy(TurningStrategy.FASTEST);

                actualState.robot.moveLengthwise(-300);

                actualState.robot.useActuator(ActuatorOrder.CLOSE_DOOR, true);

                actualState.robot.doorIsOpen = false;

                actualState.robot.setRobotRadius(TechTheSand.retractedRobotRadius);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public int remainingScoreOfVersion(int version, GameState<?> state) {
        return 0;
    }

    @Override
    public Circle entryPosition(int version, int ray, Vec2 robotPosition) throws BadVersionException {
        if (version == 0 )
        {
            return new Circle(new Vec2(Table.entryPosition.x-100, Table.entryPosition.y));
        }
        else
        {
            //TODO jetter une exception
            log.debug("erreur : mauvaise version de script");
            throw new BadVersionException();
        }
    }

    @Override
    public void finalize(GameState<?> state) throws UnableToMoveException, SerialFinallyException {

    }

    @Override
    public Integer[] getVersion(GameState<?> stateToConsider) {
        return new Integer[0];
    }
}