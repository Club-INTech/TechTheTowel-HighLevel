package table.obstacles;

import smartMath.Vec2;

/**
 * Obstacle rectangulaire sont les bords sont alignés avec les axes X et Y (pas de possibilité de faire un rectangle en biais).
 * 
 * @author pf, marsu
 */
public class ObstacleRectangular extends Obstacle
{
	/** Positon du centre du rectangle représentant l'obstacle (intersection des 2 diagonales)*/
	protected Vec2 positon;
	
	/** taille du rectangle en mm selon l'axe X */
	protected int sizeX;
	
	/** taille du rectangle en mm selon l'axe Y */
	protected int sizeY;
	
	/**
	 *	crée un nouvel obstacle rectangulaire sur la table a la position désirée.
	 *
	 * @param position Positon désirée du centre du rectangle représentant l'obstacle (intersection des 2 diagonales)
	 * @param sizeX taille voulue du rectangle représentant l'obstacle en mm selon l'axe X
	 * @param sizeY taille voulue du rectangle représentant l'obstacle en mm selon l'axe Y
	 */
	public ObstacleRectangular(Vec2 position, int sizeX, int sizeY)
	{
		super(position);
		this.sizeY = sizeY;
		this.sizeX = sizeX;
	}

	/* (non-Javadoc)
	 * @see table.obstacles.Obstacle#clone()
	 */
	public ObstacleRectangular clone()
	{
		return new ObstacleRectangular(position.clone(), sizeX, sizeY);
	}
	
	/* (non-Javadoc)
	 * @see table.obstacles.Obstacle#toString()
	 */
	public String toString()
	{
		return "ObstacleRectangulaire";
	}
	
	/**
	 * Renvoit la taille du rectangle en mm selon l'axe Y
	 *
	 * @return the size y
	 */
	public int getSizeY()
	{
		return this.sizeY;
	}
	
	/**
	 *  Renvoit la taille du rectangle en mm selon l'axe X
	 *
	 * @return the size x
	 */
	public int getSizeX()
	{
		return this.sizeX;
	}
	
	/**
	 * Fourni la plus petite distance entre le point fourni et l'obstacle.
	 *
	 * @param point point a considérer
	 * @return la plus petite distance entre le point fourni et l'obstacle.
	 */
	public float distance(Vec2 point)
	{
		return (float) Math.sqrt(SquaredDistance(point));
	}
	
	/**
	 * Fourni la plus petite distance au carré entre le point fourni et l'obstacle.
	 *
	 * @param in  point a considérer
	 * @return la plus petite distance au carré entre le point fourni et l'obstacle
	 */
	public float SquaredDistance(Vec2 in)
	{
		
		/*		
		 *  Schéma de la situation :
		 *
		 * 		 												  y
		 * 			4	|		3		|		2					    ^
		 * 				|				|								|
		 * 		____________________________________				    |
		 * 				|				|								-----> x
		 * 				|				|
		 * 			5	|	obstacle	|		1
		 * 				|				|
		 * 		____________________________________
		 * 		
		 * 			6	|		7		|		8
		 * 				|				|
		 */		
		
		// calcul des positions des coins
		Vec2 coinBasGauche = position.plusNewVector((new Vec2(0,-sizeY)));
		Vec2 coinHautGauche = position.plusNewVector((new Vec2(0,0)));
		Vec2 coinBasDroite = position.plusNewVector((new Vec2(sizeX,-sizeY)));
		Vec2 coinHautDroite = position.plusNewVector((new Vec2(sizeX,0)));
		
		// si le point fourni est dans les quarts-de-plans n°2,4,6 ou 8
		if(in.x < coinBasGauche.x && in.y < coinBasGauche.y)
			return in.squaredDistance(coinBasGauche);
		
		else if(in.x < coinHautGauche.x && in.y > coinHautGauche.y)
			return in.squaredDistance(coinHautGauche);
		
		else if(in.x > coinBasDroite.x && in.y < coinBasDroite.y)
			return in.squaredDistance(coinBasDroite);

		else if(in.x > coinHautDroite.x && in.y > coinHautDroite.y)
			return in.squaredDistance(coinHautDroite);

		// Si le point fourni est dans les demi-bandes n°1,3,5,ou 7
		if(in.x > coinHautDroite.x)
			return (in.x - coinHautDroite.x)*(in.x - coinHautDroite.x);
		
		else if(in.x < coinBasGauche.x)
			return (in.x - coinBasGauche.x)*(in.x - coinBasGauche.x);

		else if(in.y > coinHautDroite.y)
			return (in.y - coinHautDroite.y)*(in.y - coinHautDroite.y);
		
		else if(in.y < coinBasGauche.y)
			return (in.y - coinBasGauche.y)*(in.y - coinBasGauche.y);

		// Sinon, on est dans l'obstacle
		return 0f;
	}
	
}
