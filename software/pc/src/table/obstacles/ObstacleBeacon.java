package table.obstacles;
import smartMath.Vec2;

/**
 * Obstacles détectés par balise. On connaît leur vitesse.
 * @author pf
 *
 */
public class ObstacleBeacon extends ObstacleCircular 
{

	private Vec2 speed;
	
	public ObstacleBeacon (Vec2 position, int rad, Vec2 speed)
	{
		super(position,rad);
		this.speed = speed;
	}

	public void clone(ObstacleBeacon ob)
	{
		super.clone(ob);
		ob.speed = speed;
	}
}
