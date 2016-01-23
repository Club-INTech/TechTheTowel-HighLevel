package exceptions;


/**
 * Utilisée par la stratégie pour savoir qu'on a récupéré tous les coquillages disponibles de notre côté
 */
public class BadVersionException extends Exception
{
    /**
     * Indique s'il s'agit d'un problème bénin de coquillages (aucun disponible)
     */
    private boolean shellProblem=false;

    public BadVersionException()
    {

    }

    public BadVersionException(boolean shells)
    {
        this.shellProblem = true;
    }


    public boolean isShellProblem() {
        return shellProblem;
    }
}