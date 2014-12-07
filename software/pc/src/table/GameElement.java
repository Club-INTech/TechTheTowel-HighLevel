package table;

import smartMath.Vec2;

// TODO: Auto-generated Javadoc
/**
 * The Class GameElement.
 */
abstract class GameElement
{
	
	/** The position. */
	protected Vec2 position;
	
	/**
	 * Instantiates a new game element.
	 *
	 * @param position the position
	 */
	public GameElement(Vec2 position)
	{
		this.position = position;
	}
	
	/**
	 * Gets the position.
	 *
	 * @return the position
	 */
	public Vec2 getPosition()
	{
		return position;
	}
}
