package exceptions;

/**
 * exception levee si le robot se trouve dans un obstacle
 * @author Etienne
 *
 */
public class IsInObstacleException extends Exception
{
	private static final long serialVersionUID = -7968975910907981869L;
	
	public IsInObstacleException()
	{
		super();
	}
	
	public IsInObstacleException(String m)
	{
		super(m);
	}
}
