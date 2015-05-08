package exceptions;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.EnumSet;

import enums.ObstacleGroups;

/**
 * exception levee par le pathFinding si le point d'arrivée est dans un obstacle / n'est pas sur la table
 * @author Etienne
 *
 */
public class InObstacleException extends Exception
{
	private static final long serialVersionUID = -7968975910907981869L;
	private EnumSet<ObstacleGroups> mObstacleGroups;
	
	public InObstacleException(EnumSet<ObstacleGroups> obstacleGroups)
	{
		super();
		mObstacleGroups = obstacleGroups;
	}
	
	public InObstacleException(String m, EnumSet<ObstacleGroups> obstacleGroups)
	{
		super(m);
		mObstacleGroups = obstacleGroups;
	}
	

	/**
	 * 
	 * @return les groupes d'obstacle auquel l'obstacle appartient
	 */
	public EnumSet<ObstacleGroups> getObstacleGroup()
	{
		return mObstacleGroups;
	}
	
	public String logStack()
	{
		StringWriter sw = new StringWriter();
		this.printStackTrace(new PrintWriter(sw));
		
		String exceptionAsString = sw.toString();	
		exceptionAsString = exceptionAsString.replaceAll("(\r\n|\n\r|\r|\n)", " -> ");
		
		return exceptionAsString;
	}
}
