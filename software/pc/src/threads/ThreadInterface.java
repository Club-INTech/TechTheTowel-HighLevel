package threads;


import graphics.Window;
import robot.Robot;
import table.Table;
import utils.Config;
import utils.Log;


/**
 * Thread affichant l'interface graphique pour observer ce que le robot pense être vrai, ainsi que le chemin
 * qu'il va suivre.
 * @author discord
 *
 * TODO Lier à la stratégie
 */
public class ThreadInterface extends AbstractThread
{
    private Window win;
    private Log log;
    private Robot robot;

    public ThreadInterface(Config config, Log log, Table table, Robot robot)
    {
        super(config, log);
        Thread.currentThread().setPriority(3); // C'est le thread le moins prioritaire du lot
        this.log = log;
        this.robot = robot;
        this.win = new Window(table, robot);
    }



    @Override
    public void run() {
        while(true)
        {
            win.getPanel().drawArrayList(robot.cheminSuivi);
            win.getPanel().repaint();
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}