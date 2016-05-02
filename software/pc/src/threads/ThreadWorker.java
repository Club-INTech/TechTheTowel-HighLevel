package threads;

import pathDingDing.PathDingDing;
import utils.Config;
import utils.Log;

/**
 * Thread permettant d'effectuer des tâches de second plan comme recalculer le graphe du PDD
 * @author discord
 */
public class ThreadWorker extends AbstractThread
{

    PathDingDing pdd;

    /**
     * False si le graphe du PDD doit être recalculé
     */
    private static boolean graphOK = true;


    public ThreadWorker(Config config, Log log, PathDingDing pdd)
    {
        super(config, log);
        this.pdd = pdd;
        Thread.currentThread().setPriority(1);
    }

    @Override
    public void run()
    {
        if(!graphOK)
        {
             pdd.recomputeGraph();
             ThreadWorker.graphOK = true;
        }

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void recalculateGraph()
    {
        ThreadWorker.graphOK = false;
    }

    public static boolean isGraphReady()
    {
        return ThreadWorker.graphOK;
    }
}
