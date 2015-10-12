package table;

import enums.Elements;
import smartMath.Vec2;

/**
 * Classe definissant les coquillages.
 * @author julian
 */
public class Shell extends GameElement
{
	/** Couleur du coquillage, enumeres dans enums/Elements.java */
	private Elements color;
	
	public Shell(Elements color, Vec2 position)
	{
		super(position);
		this.color = color;
	}
}
