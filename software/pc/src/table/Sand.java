package table;

import enums.Elements;
import smartMath.Vec2;

/**
 * Classe definissant les cubes/cylindres/cones de sable.
 * Cette classe herite de GameElement
 * @author julian
 */
public class Sand extends GameElement
{
	/** Type d'element de sable, enumeres dans enums/Elements.java 
	 *  Cone, cylindre, cube 									
	 */
	private Elements type;
	
	/**
	 * Taille en mm des elements de jeu :
	 * Hauteur d'un cube, cylindre, cone
	 */
	public int sandSize = 58;
	
	/** Niveau de l'element (z si vous preferez)
	 *  0 = sur la table
	 *  1 = sur un element
	 *  2 = sur deux elements
	 *  n = sur n elements
	 */
	private int level;
	
	/**
	 * Construit l'element de sable
	 * @param type son type (cube, cylindre ou cone)
	 * @param position sa position (x,y)
	 * @param level niveau de l'element (voir def)
	 */
	public Sand(Elements type, Vec2 position, int level)
	{
		super(position);
		this.level = level;
		this.type = type;
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
		return (((level*2)+1)*this.sandSize)/2;
	}
	
	/**
	 * Getter du type de l'element de jeu
	 * @return le type de l'element de jeu
	 */
	public Elements getType()
	{
		return this.type; 
	}
	
}
