package table;

import enums.Color;
import smartMath.Circle;
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
	
	/** Hauteur en mm d'un coquillage*/
	public double shellHeight = 25;

	public Circle entryPosition;
	

	/**
	 * Constructeur du coquillage
	 * @param color, SHELL_NEUTRAL, SHELL_ENEMY, ou SHELL_ALLY de l'enum Elements
	 * @param position, la position du coquillage
	 * @param entry, point d'entrée pour récupération
	 */
	public Shell(Vec2 position, Color color, Vec2 entry)
	{
		super(position);
		this.color = color;
		this.entryPosition = new Circle(entry,0);
	}

    /**
     * Constructeur du coquillage
     * @param color, SHELL_NEUTRAL, SHELL_ENEMY, ou SHELL_ALLY de l'enum Elements
     * @param position, la position du coquillage
     * @param entry, point d'entrée pour récupération
     */
    public Shell(Vec2 position, Color color, Circle entry)
    {
        super(position);
        this.color = color;
        this.entryPosition = entry;
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
	

	
}
