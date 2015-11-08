package exceptions;

import java.io.PrintWriter;
import java.io.StringWriter;

import enums.Obstacles;
import pathDingDing.Node;
import table.obstacles.ObstacleCircular;
import table.obstacles.ObstacleManager;
import table.obstacles.ObstacleRectangular;

/**
 * Exception lancée si le point donné au pathDingDing est dans un obstacle
 * @author julian
 *
 */
public class PointInObstacleException extends Exception
{
	private static final long serialVersionUID = -7968975910907981869L;
	
	private Node node;
	private ObstacleManager obstacleManager;
	
	private ObstacleCircular obsCir = null;
	private ObstacleRectangular obsRec = null;
	
	/**
	 * Constructeur
	 * @param node le noeud problématique
	 * @param obs le gestionnaire d'obstacles du graphe
	 */
	public PointInObstacleException(Node node, ObstacleManager obs)
	{
		super();
		this.node = node;
		this.obstacleManager = obs;
		getObstacle();
	}
	
	public PointInObstacleException(Node node, ObstacleManager obs, String m)
	{
		super(m);
		this.node = node;
		this.obstacleManager = obs;
		getObstacle();
	}
	
	public String logStack()
	{
		StringWriter sw = new StringWriter();
		this.printStackTrace(new PrintWriter(sw));
		
		String exceptionAsString = sw.toString();	
		exceptionAsString = exceptionAsString.replaceAll("(\r\n|\n\r|\r|\n)", " -> ");
		
		return exceptionAsString;
	}
	
	/**
	 * Renvoie le node problématique
	 */
	public Node getNode()
	{
		return node;
	}
	
	/**
	 * Renvoie l'obstacle rectangulaire
	 * renvoie null s'il est circulaire
	 * pensez à utiliser getObstacleType() avant !
	 */
	public ObstacleRectangular getRectangularObstacle()
	{
		return obsRec;
	}
	
	/**
	 * Renvoie l'obstacle circulaire
	 * renvoie null s'il est rectangulaire
	 * pensez à utiliser getObstacleType() avant !
	 */
	public ObstacleCircular getCircularObstacle()
	{
		return obsCir;
	}
	
	/**
	 * Renvoie le type de l'obstacle
	 */
	public Obstacles getObstacleType()
	{
		if(obsRec == null)
		{
			return Obstacles.CIRCULAR;
		}
		return Obstacles.RECTANGULAR;
	}
	
	/**
	 * Cherche et stocke l'obstacle dans la variable appropriée
	 */
	private void getObstacle()
	{
		//TODO recherche de l'obstacle en question
	}
}
