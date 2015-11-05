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
	
	/**
	 * Constructeur du coquillage
	 * @param color, SHELL_NEUTRAL, SHELL_ENEMY, ou SHELL_ALLY de l'enum Elements
	 * @param position, la position du coquillage
	 */
	public Shell(Vec2 position, Color color)
	{
		super(position);
		this.color = color;
	}
	
	/**
	 * Renvoie la couleur du coquillage
	 */
	public Color getColor()
	{
		return this.color;
	}
}
