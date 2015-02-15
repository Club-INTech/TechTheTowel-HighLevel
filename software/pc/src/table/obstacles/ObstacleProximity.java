package table.obstacles;

import smartMath.Vec2;

/**
 * Obstacles détectés par capteurs de proximité (ultrasons et infrarouges).
 * Ces obstacles sont supposés circulaires
 *
 * @author pf, marsu
 */
public class ObstacleProximity extends ObstacleCircular
{
	/** temps ou l'obstacle sera perime en ms */
	private long mOutDatedTime;
	
	/**
	 * Crée un nouvel obstacle détecté a proximité du robot.
	 * Ces obstacles sont supposés circulaires: on les définit par leur centre et leur rayon
	 * "a proximité du robot" signifie qu'il a été détecté par les capteurs de proximité, mais
	 * dans l'absolu, il n'y a pas de contrainte géométrique de proximité
	 *
	 * @param position position du centre du disque représentant l'obstacle circulaire
	 * @param radius rayon du disque représentant l'obstacle circulaire 
	 */
	public ObstacleProximity (Vec2 position, int radius)
	{
		super(position,radius);
		mOutDatedTime = System.currentTimeMillis() + 5000;
	}
	
	/* (non-Javadoc)
	 * @see table.obstacles.ObstacleCircular#clone()
	 */
	public ObstacleProximity clone()
	{
		return new ObstacleProximity(position.clone(), getRadius());
	}
	
	public long getOutDatedTime()
	{
		return mOutDatedTime;
	}
}
