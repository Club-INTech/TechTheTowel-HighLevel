package table.obstacles;
import smartMath.Vec2;

// TODO: Auto-generated Javadoc
/**
 * Obstacles détectés par balise. On connaît leur vitesse.
 * @author pf
 *
 */
public class ObstacleBeacon extends ObstacleCircular 
{

	/** The speed. */
	private Vec2 speed;
	
	/**
	 * Instantiates a new obstacle beacon.
	 *
	 * @param position the position
	 * @param rad the rad
	 * @param speed the speed
	 */
	public ObstacleBeacon (Vec2 position, int rad, Vec2 speed)
	{
		super(position,rad);
		this.speed = speed;
	}

	/**
	 * Clone.
	 *
	 * @param ob the ob
	 */
	public void clone(ObstacleBeacon ob)
	{
		super.clone(ob);
		ob.speed = speed;
	}
}
