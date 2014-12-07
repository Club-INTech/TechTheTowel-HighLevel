package table.obstacles;

import smartMath.Vec2;

// TODO: Auto-generated Javadoc
/**
 * Obstacle circulaire.
 *
 * @author pf
 */
public class ObstacleCircular extends Obstacle
{
	// le Vec2 "position" indique le centre de l'obstacle
	
	// rayon de cet obstacle
	/** The radius. */
	protected int radius;
	
	/**
	 * Instantiates a new obstacle circular.
	 *
	 * @param position the position
	 * @param rad the rad
	 */
	public ObstacleCircular(Vec2 position, int rad)
	{
		super(position);
		this.radius = rad;
	}
	
	/* (non-Javadoc)
	 * @see table.obstacles.Obstacle#clone()
	 */
	public ObstacleCircular clone()
	{
		return new ObstacleCircular(position.clone(), radius);
	}

	// Copie this dans oc, sans modifier this
	/**
	 * Clone.
	 *
	 * @param oc the oc
	 */
	public void clone(ObstacleCircular oc)
	{
		oc.position = position;
		oc.radius = radius;
	}

	/**
	 * Gets the radius.
	 *
	 * @return the radius
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
		return super.toString()+", rayon: "+radius;
	}
}
