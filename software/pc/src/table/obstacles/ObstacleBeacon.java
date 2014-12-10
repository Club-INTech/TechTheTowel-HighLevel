package table.obstacles;
import smartMath.Vec2;

/**
 * Obstacles mobiles détectés par la balise laser.
 * La vitesse de déplacment de cet obstacle sur la table est, congtrairement aux autres types d'obstacle, renseingée par la balise elle-meme
 * @author pf, marsu
 *
 */
public class ObstacleBeacon extends ObstacleCircular 
{

	/** Vitesse de l'obstacle sur la table. */
	private Vec2 speed;
	
	/**
	 * Crée un nouvel obstacle détecté par la balise laser.
	 *
	 * @param position endroit de la table ou faire cet obstacle
	 * @param rad orientation de l'obstacle, même si cet obstacle est supposé de forme circulaire, son orientation est mémorisé pour en déduire son vecteur vitesse
	 * @param speed vitesse d'évolution de cet obstacle sur la table
	 */
	public ObstacleBeacon (Vec2 position, int rad, Vec2 speed)
	{
		super(position,rad);
		this.speed = speed;
	}

	/**
	 * Rends l'obstacle fourni explicitement en argument égal a celui-ci 
	 *
	 * @param other l'obstacle de balise laser a modifier
	 */
	public void clone(ObstacleBeacon other)
	{
		super.clone(other);
		other.speed = speed;
	}
}
