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
    }

    @Override
    public void run()
    {
        log.debug("ThreadWorker lancé");
        while(true)
        {
            if (!graphOK) {
                log.debug("recalcul du graphe");
                pdd.recomputeGraph();
                ThreadWorker.graphOK = true;
                log.debug("recalcul du graphe terminé");
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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
