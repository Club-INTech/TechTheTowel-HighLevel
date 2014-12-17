package exceptions;

/**
 * exception levee si aucun chemin n'est trouve par le pathfinding
 * @author Etienne
 *
 */
public class PathNotFoundException extends Exception
{
	private static final long serialVersionUID = -7968975910907981869L;
	
	public PathNotFoundException()
	{
		super();
	}
	
	public PathNotFoundException(String m)
	{
		super(m);
	}
}
