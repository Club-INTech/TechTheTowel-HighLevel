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
	
	private int lifetime;

	/**
	 * Nombre de fois détécté :
	 * Positif : on l'a vu plusieurs fois
	 * Negatif : si on ne l'a pas detecté alors qu'on aurais dû
	 */
	public int numberOfTimeDetected;

	/*
	 * limite entre obstacle confirmé ou infirmé; x
	 * 
	 * 	    unconf   conf
	 * 	 0 |------|x|-----|y
	 * 
	 */
	private int thresholdConfirmedOrUnconfirmed;
	
	/*
	 *  Maximum d'incrementation de numberOfTimeDetected; y
	 * 
	 * 	    unconf   conf
	 * 	 0 |------|x|-----|y
	 * 
	 */
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
	public ObstacleProximity (Vec2 position, int radius, int lifetime)
	{
		super(position,radius);
		
		this.lifetime = lifetime;
		mOutDatedTime = System.currentTimeMillis() + lifetime;// la date de peremption = temps actuel + temps de peremption de l'obstacle
		//TODO mettre dans le fichier de config le "temps de peremption" de chaque obstacle 
		numberOfTimeDetected=1;
		
		
		thresholdConfirmedOrUnconfirmed=2;
		maxNumberOfTimeDetected=3;
	}
	
	/* (non-Javadoc)
	 * @see table.obstacles.ObstacleCircular#clone()
	 */
	public ObstacleProximity clone()
	{
		return new ObstacleProximity(position.clone(), getRadius(), lifetime);
	}
	
	public long getOutDatedTime()
	{
		return mOutDatedTime;
	}
	
	public int getThresholdConfirmedOrUnconfirmed()
	{
		return thresholdConfirmedOrUnconfirmed;
	}
	
	public int getMaxNumberOfTimeDetected()
	{
		return maxNumberOfTimeDetected;
	}
	
	/**
	 * nouveau du temps de vie pour l'obstacle
	 * @param time le nouveau temps de vie
	 */
	public void setLifeTime(int time) 
	{
		lifetime = time;
		mOutDatedTime = System.currentTimeMillis() + lifetime;
	}
}
