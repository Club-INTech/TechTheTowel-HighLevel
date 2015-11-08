package table;

import enums.Color;
import enums.Elements;
import smartMath.Vec2;

/**
 * Classe definissant les coquillages.
 * Cette classe herite de GameElement
 * @author julian
 */
public class Shell extends GameElement
{
	/** Couleur du coquillage, enumeres dans enums/Elements.java */
	private Color color;

	/** Niveau de l'element
	 *  0 = sur la table
	 *  1 = sur le premier palier dans les coins
	 *  2 = sur le deuxième palier dans les coins
	 */
	private int level;
	
	/** Hauteur en mm d'un coquillage*/
	public int shellHeight = 25;
	
	/** Diamètre en mm d'un coquillage */
	public float shellDiam = (float) 76.2;
	/**
	 * Constructeur du coquillage
	 * @param color, SHELL_NEUTRAL, SHELL_ENEMY, ou SHELL_ALLY de l'enum Elements
	 * @param position, la position du coquillage
	 * @param level, niveau de l'élément
	 */
	public Shell(Vec2 position, Color color, int level)
	{
		super(position);
		this.color = color;
		this.level = level;
	}
	
	/**
	 * Renvoie la couleur du coquillage
	 */
	public Color getColor()
	{
		return this.color;
	}
	
	/**
	 * Getter de la position de l'element de jeu
	 * @return la position en X de l'element de jeu
	 */
	public float getX()
	{
		return position.x;
	}
	
	/**
	 * Getter de la position de l'element de jeu
	 * @return la position en Y de l'element de jeu
	 */
	public float getY()
	{
		return position.y;
	}
	
	/**
	 * Getter de la position de l'element de jeu
	 * @return la position en Z de l'element de jeu
	 */
	public float getZ()
	{
		if (this.level == 0)
		{
			return ((float) 0);
		}
		else if (this.level == 1)
		{
			return ((float) 44+(shellHeight/2));
		}
		else 
		{
			return ((float) 44+22+(shellHeight/2));
		}
	}
	
}
