package table;

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
	private Elements color;
	
	/**
	 * Constructeur du coquillage
	 * @param color, SHELL_NEUTRAL, SHELL_ENEMY, ou SHELL_ALLY de l'enum Elements
	 * @param position, la position du coquillage
	 */
	public Shell(Elements color, Vec2 position)
	{
		super(position);
		this.color = color;
	}
}
