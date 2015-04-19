package table.obstacles;

import enums.ObstacleGroups;
import smartMath.*;

/**
 * Obstacle de forme circulaire.
 *
 * @author pf, marsu
 */
public class ObstacleCircular extends Obstacle
{
	/** groupe d'obstacle auquel appartient l'obstacle*/
	protected ObstacleGroups obstacleGroup;
	
	/** rayon en mm de cet obstacle */
	protected int radius=0;
	
	/**
	 * crée un nouvel obstacle de forme circulaire a la position et a la taille spécifiée.
	 *
	 * @param position position du centre de l'obstacle a créer
	 * @param radius rayon de l'obstacle a créer 
	 */
	public ObstacleCircular(Vec2 position, int radius, ObstacleGroups obstacleGroup)
	{
		super(position);
		this.radius = radius;
		this.obstacleGroup = obstacleGroup;
	}
	
	/* (non-Javadoc)
	 * @see table.obstacles.Obstacle#clone()
	 */
	public ObstacleCircular clone()
	{
		return new ObstacleCircular(position.clone(), radius, obstacleGroup);
	}
	
	/**
	 * Verifie si a == b pour des obstacles circulaires
	 * @param otherObstacle b
	 * @return true si a == b
	 */
	public boolean equals(ObstacleCircular otherObstacle) 
	{
		return (
				this.radius == otherObstacle.radius
			&&  this.position.equals(otherObstacle.position)	
			&&	this.obstacleGroup.compareTo(otherObstacle.obstacleGroup)==0	
				);
	}
	
	/**
	 * Verifie si le nom de a == b pour des obstacles circulaires
	 * @param otherObstacle b
	 * @return true si nom de a == nom de b
	 */
	public boolean equalsGroups(ObstacleGroups obstacleGroup) 
	{
		return this.obstacleGroup.compareTo(obstacleGroup)==0;
	}

	/**
	 * Copie this dans other, sans modifier this
	 *
	 * @param other l'obstacle circulaire a modifier
	 */
	public void clone(ObstacleCircular other)
	{
		other.position = position;
		other.radius = radius;
	}

	/**
	 * Donne le rayon de cet obstacle circulaire.
	 *
	 * @return le rayon de cet obstacle circulaire.
	 */
	public int getRadius()
	{
		return radius;
	}
	
	public ObstacleGroups getObstacleGroup()
	{
		return obstacleGroup;
	}
	
	/**
	 * Convertit l'obstacle en cercle.
	 * 
	 * @return
	 */
	public Circle toCircle()
	{
		return new Circle(position, radius);
	}
	
	/* (non-Javadoc)
	 * @see table.obstacles.Obstacle#toString()
	 */
	public String toString()
	{
		return "Obstacle circulaire de centre " + position + " et de rayon: "+radius;
	}
	
	public void printObstacleDeleted()
	{
		System.out.println("Obstacle enlevé:"+obstacleGroup);
	}
	
	public void printObstacleMemory()
	{
		System.out.println("Obstacle en memoire : "+obstacleGroup);
	}
}
