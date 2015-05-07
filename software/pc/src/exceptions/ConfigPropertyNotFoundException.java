package exceptions;

/**
 * exception levee si aucun chemin n'est trouve par le pathfinding
 * @author Etienne
 *
 */
public class ConfigPropertyNotFoundException extends Exception
{
	private static final long serialVersionUID = -7968975910907981869L;
	private String nameOfProperty;
	
	public ConfigPropertyNotFoundException(String m)
	{
		super(m);
		nameOfProperty=m;
	}
	
	public String getPropertyNotFound()
	{
		return nameOfProperty;
	}
}
