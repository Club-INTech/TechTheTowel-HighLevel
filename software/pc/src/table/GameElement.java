package table;

import smartMath.Vec2;

/**
 * Element de jeu
 */
abstract class GameElement
{
	
	/** La position de l'élément sur la table */
	protected Vec2 position;
	
	/** Booléen qui précise si l'élement est dans le robot ou non */
	public boolean inRobot;
	
	/**
	 * Crée un nouvel élément a l'endroit de la table spécifié
	 *
	 * @param position position à laquelle instancier l'élément de jeu
	 */
	public GameElement(Vec2 position)
	{
		this.position = position;
	}
	
	/**
	 * Renvoie la position courante de l'élément de jeu
	 *
	 * @return la position courante de l'élément de jeu
	 */
	public Vec2 getPosition()
	{
		return position;
	}
	
	/** Permet d'annoncer si l'élément est dans le robot
	 * 
	 * @param inRobot le booléen associé
	 */
	public void setInRobot(boolean inRobot)
	{
		this.inRobot = inRobot;
	}
	
	/** Renvoie si l'élément est dans le robot */
	public boolean getInRobot()
	{
		return inRobot;
	}
	
}
