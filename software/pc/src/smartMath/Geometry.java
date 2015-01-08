package smartMath;

/**
 * classe de calculs de geometrie
 * @author Etienne
 *
 */
public class Geometry
{
	/**
	 * retourne vrai si les points a, b, c sont orientés dans le sens trigonometrique, faux sinon.
	 * @param a
	 * @param b
	 * @param c
	 * @return
	 */
	public static boolean isCCWOriented(Point a, Point b, Point c)
	{
		return (b.x - a.x)*(c.y - a.y) >= (b.y - a.y)*(c.x - a.x);
	}
}
