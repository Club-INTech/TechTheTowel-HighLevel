package table.obstacles;

import smartMath.*;

/**
 * Obstacle de forme circulaire.
 *
 * @author pf, marsu
 */
public class ObstacleCircular extends Obstacle
{
	
	/** rayon en mm de cet obstacle */
	protected int radius=0;
	
	/**
	 * crée un nouvel obstacle de forme circulaire a la position et a la taille spécifiée.
	 *
	 * @param position position du centre de l'obstacle a créer
	 * @param radius rayon de l'obstacle a créer 
	 * @param obstacleGroup 
	 */
	public ObstacleCircular(Vec2 position, int radius)
	{
		super(position);
		this.radius = radius;
	}
	
	/* (non-Javadoc)
	 * @see table.obstacles.Obstacle#clone()
	 */
	public ObstacleCircular clone()
	{
		return new ObstacleCircular(position.clone(), radius);
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
				);
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
	
	/**
	 * Convertit l'obstacle en cercle.
	 * 
	 * @return
	 */
	@SuppressWarnings("javadoc")
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
		System.out.println("Obstacle enlevé");
	}
	
	public void printObstacleMemory()
	{
		System.out.println("Obstacle en memoire");
	}
}
