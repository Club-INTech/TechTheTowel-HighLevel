package table.obstacles;

import smartMath.Vec2;

// TODO: Auto-generated Javadoc
/**
 * Obstacles détectés par capteurs de proximité (ultrasons et infrarouges).
 *
 * @author pf, marsu
 */
class ObstacleProximity extends ObstacleCircular
{
	
	/**
	 * Instantiates a new obstacle proximity.
	 *
	 * @param position the position
	 * @param rad the rad
	 */
	public ObstacleProximity (Vec2 position, int rad)
	{
		super(position,rad);
	}
	
	/* (non-Javadoc)
	 * @see table.obstacles.ObstacleCircular#clone()
	 */
	public ObstacleProximity clone()
	{
		return new ObstacleProximity(position.clone(), getRadius());
	}
}
