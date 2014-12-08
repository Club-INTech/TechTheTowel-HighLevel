package table;

import smartMath.Vec2;

/**
 * Element de jeu
 */
abstract class GameElement
{
	
	/** La position de l'élément sur la table */
	protected Vec2 position;
	
	/**
	 * Crée un nouvel élément a l'endroit de la table spécifié
	 *
	 * @param position position a laquelle instancier l'élément de jeu
	 */
	public GameElement(Vec2 position)
	{
		this.position = position;
	}
	
	/**
	 * Renvois la position courrante de l'élément de jeu
	 *
	 * @return la position courrante de l'élément de jeu
	 */
	public Vec2 getPosition()
	{
		return position;
	}
}
