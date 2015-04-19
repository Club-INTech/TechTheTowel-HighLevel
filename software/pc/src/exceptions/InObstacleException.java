package exceptions;

/**
 * exception levee par le pathFinding si le point d'arrivée est dans un obstacle / n'est pas sur la table
 * @author Etienne
 *
 */
public class InObstacleException extends Exception
{
	private static final long serialVersionUID = -7968975910907981869L;
	
	public InObstacleException()
	{
		super();
	}
	
	public InObstacleException(String m)
	{
		super(m);
	}
}
