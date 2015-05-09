package table.obstacles;

import com.sun.org.apache.bcel.internal.generic.INEG;

import smartMath.Vec2;
import enums.ObstacleGroups;

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
	
	private int lifetime;

	/**
	 * Nombre de fois détécté :
	 * Positif : on l'a vu plusieurs fois
	 * Negatif : si on ne l'a pas detecté alors qu'on aurais dû
	 */
	public int numberOfTimeDetected;

	private int maxNumberOfTimeNotDetected;
	
	private int maxNumberOfTimeDetected;

	
	/**
	 * Crée un nouvel obstacle détecté a proximité du robot.
	 * Ces obstacles sont supposés circulaires: on les définit par leur centre et leur rayon
	 * "a proximité du robot" signifie qu'il a été détecté par les capteurs de proximité, mais
	 * dans l'absolu, il n'y a pas de contrainte géométrique de proximité
	 *
	 * @param position position du centre du disque représentant l'obstacle circulaire
	 * @param radius rayon du disque représentant l'obstacle circulaire 
	 * @param obstacleGroup 
	 * @param lifetime la durée de vie (en ms) de l'objet a créer
	 */
	public ObstacleProximity (Vec2 position, int radius, ObstacleGroups obstacleGroup, int lifetime)
	{
		super(position,radius, obstacleGroup);
		
		this.lifetime = lifetime;
		mOutDatedTime = System.currentTimeMillis() + lifetime;// la date de peremption = temps actuel + temps de peremption de l'obstacle
		//TODO mettre dans le fichier de config le "tempsde peremption" de chaque obstacle 
		numberOfTimeDetected=0;
		maxNumberOfTimeNotDetected=0;
		maxNumberOfTimeDetected=3;
	}
	
	/* (non-Javadoc)
	 * @see table.obstacles.ObstacleCircular#clone()
	 */
	public ObstacleProximity clone()
	{
		return new ObstacleProximity(position.clone(), getRadius(), obstacleGroup, lifetime);
	}
	
	public long getOutDatedTime()
	{
		return mOutDatedTime;
	}
	
	public int getMaxNumberOfTimeNotDetected()
	{
		return maxNumberOfTimeNotDetected;
	}
	
	public int getMaxNumberOfTimeDetected()
	{
		return maxNumberOfTimeDetected;
	}
}
