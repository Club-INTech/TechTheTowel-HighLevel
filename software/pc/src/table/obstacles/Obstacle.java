package table.obstacles;
import smartMath.Vec2;

/**
 * classe abstraite pour les obstacles sur la table.
 * Les obstacles peuvent avoir différentes formes, et être soit fixes d'un match a l'autre, soit mobiles (un robot adverse est par exemple un obstacle mobile)
 * @author pf, marsu
 *
 */
public abstract class Obstacle
{

	/** Position de l'obstacle sur la table. En fonction de la forme de l'obstacle, il peut s'étendre plus ou moins loin de cette position dans diverses directions */
	protected Vec2 position;
	
	/**
	 * construit un nouvel obstacle a position donnée
	 *
	 * @param position position de l'obstacle a construire
	 */
	public Obstacle (Vec2 position)
	{
		this.position = position.clone();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public abstract Obstacle clone();

	/**
	 * Renvois la position de cet obstacle.
	 *
	 * @return the position
	 */
	public Vec2 getPosition()
	{
		return this.position;
	}
	
	public void setPosition(Vec2 position)
	{
		this.position = position;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		return "Obstacle en "+position;
	}
	
}
