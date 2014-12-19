package table.obstacles;

import smartMath.Vec2;

/**
 * Obstacle de forme circulaire.
 *
 * @author pf, marsu
 */
public class ObstacleCircular extends Obstacle
{
	/** position du centre du disque constituant cet obstacle */
	protected Vec2 position;
	
	/** rayon en mm de cet obstacle */
	protected int radius;
	
	/**
	 * crée un nouvel obstacle de forme circulaire a la position et a la taille spécifiée.
	 *
	 * @param position position du centre de l'obstacle a créer
	 * @param radius rayon de l'obstacle a créer 
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
	
	/* (non-Javadoc)
	 * @see table.obstacles.Obstacle#toString()
	 */
	public String toString()
	{
		return "Obstacle circulaire de centre " + position + " et de rayon: "+radius;
	}
}
