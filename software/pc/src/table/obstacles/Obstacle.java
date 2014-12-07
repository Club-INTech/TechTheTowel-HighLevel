package table.obstacles;
import smartMath.Vec2;

// TODO: Auto-generated Javadoc
/**
 * Superclasse abstraite des obstacles.
 * @author pf, marsu
 *
 */
public abstract class Obstacle
{

	/** The position. */
	protected Vec2 position;
	
	/**
	 * Instantiates a new obstacle.
	 *
	 * @param position the position
	 */
	public Obstacle (Vec2 position)
	{
		this.position = position;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public abstract Obstacle clone();

	/**
	 * Gets the position.
	 *
	 * @return the position
	 */
	public Vec2 getPosition()
	{
		return this.position;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		return "Obstacle en "+position;
	}
	
}
