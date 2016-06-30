/* ============================================================================
 * 				Vec2 class
 * ============================================================================
 * 
 * Bi-dimentionnal vector 2 class. Simple-precision members.
 * Author : Dede
 * Refactoring : Martial
 */

package smartMath;

/**
 * Classe de calcul de vecteurs de dimension 2.
 *
 * @author martial
 * @author pf
 */

public class Vec2
{

	/** The x. */
	public int x;
	
	/** The y. */
	public int y;
	
	/**
	 * Instantiates a new vec2.
	 */
	public Vec2()
	{
		x = 0;
		y = 0;
	}

	/**
	 * Instantiates a new vec2.
	 *
	 * @param requestedX the requested x
	 * @param requestedY the requested y
	 */
	public Vec2(int requestedX, int requestedY)
	{
		x = requestedX;
		y = requestedY;
	}
	
	// Do not square a length, use squared length directly
	// to increase performances
	/**
	 * Squared length.
	 *
	 * @return the int
	 */
	public int squaredLength()
	{
		return x*x + y*y;
	}

	// Returns this vec2's magnitude
	/**
	 * Length.
	 * sqrt is applied
	 * @return the float
	 */
	public float length()
	{
		return (float) Math.sqrt(squaredLength());
	}
	
	// dot product
	/**
	 * le produit scalaire
	 *
	 * @param other the other
	 * @return the int
	 */
	public int dot(Vec2 other)
	{
		return x*other.x + y*other.y;
	}
	

	// build a new Vec2 by summing the calling Vec2 and the one in args
	/**
	 * Plus new vector.
	 *
	 * @param other the other
	 * @return the vec2
	 */
	public Vec2 plusNewVector(Vec2 other)
	{
		return new Vec2(x + other.x, y + other.y);
	}
	
	// build a new Vec2 with the value obtained by decrementing the
	// calling Vec2 by the provided Vec2 in args
	/**
	 * Minus new vector.
	 *
	 * @param other the other
	 * @return the vec2
	 */
	public Vec2 minusNewVector(Vec2 other)
	{
		return new Vec2(x - other.x, y - other.y);
	}

	/**
	 * Plus.
	 *
	 * @param other the other
	 */
	public void plus(Vec2 other)
	{
		x += other.x;
		y += other.y;
	}
	
	/**
	 * Minus.
	 *
	 * @param other the other
	 */
	public void minus(Vec2 other)
	{
		x -= other.x;
		y -= other.y;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public Vec2 clone()
	{
		return new Vec2(this.x, this.y);
	}
	
	/**
	 * Squared distance.
	 *
	 * @param other the other
	 * @return the float
	 */
	public float squaredDistance(Vec2 other)
	{
		return (x-other.x)*(x-other.x) + (y-other.y)*(y-other.y);
	}

	/**
	 * Distance.
	 *
	 * @param other the other
	 * @return the float
	 */
	public float distance(Vec2 other)
	{
		return (float) Math.sqrt(squaredDistance(other));
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		return "("+x+","+y+")";
	}
	
	/**
	 * transforme un point en un cercle de rayon nul
	 * @return un cercle de centre ce Vec2 et de rayon nul.
	 */
	public Circle toCircle()
	{
		return new Circle (this,0);
	}
	
	/**
	 * Equals.
	 *
	 * @param other the other
	 * @return true, if successful
	 */
	public boolean equals(Vec2 other)
	{
		return x == other.x && y == other.y;
	}
	
	/**
	 * Dot float.
	 *
	 * @param a the a
	 * @return the vec2
	 */
	public Vec2 dotFloat(int a)
	{
		return new Vec2(x*a,y*a);
	}
	
	/**
	 * Dot float.
	 *
	 * @param a the scalar to multiply the Vec2 with
	 * @return the vec2 multiplied by a
	 */
	public Vec2 dotFloat(double a)
	{
		return new Vec2((int)(x*a),(int)(y*a));
	}
	
	/**
	 * Sets the.
	 *
	 * @param other the other
	 */
	public void set(Vec2 other)
	{
		x = other.x;
		y = other.y;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}
	
	/**
	 * Make copy.
	 *
	 * @return the vec2
	 */
	public Vec2 makeCopy()
	{
		return new Vec2(x, y);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		else if (obj == null)
			return false;
		else if (!(obj instanceof Vec2))
			return false;
		Vec2 other = (Vec2) obj;
		if (x != other.x)
			return false;
		else if (y != other.y)
			return false;
		return true;
	}

	/**
	 * Manhattan_distance.
	 *
	 * @param other the other
	 * @return the int
	 */
	public int manhattan_distance(Vec2 other)
	{
		return Math.abs(x - other.x) + Math.abs(y - other.y); 
	}

	/**
	 * Copie this dans other.
	 *
	 * @param other the other
	 */
	public void copy(Vec2 other)
	{
	    other.x = x;
	    other.y = y;
	}

	/**
	 * Angle du vecteur par rapport à l'abscisse
	 * @return l'angle en radians
     */
	public double angle()
	{
		int signe=0;
		if(this.squaredLength() == 0)
			return 0;

		if(this.y<0)
			signe=-2;

		return Math.abs(signe*Math.PI+Math.acos(this.x / this.length()));
	}

    /**
     * Tourne le vecteur d'un angle donné et le renvoie sous forme d'un nouveau vecteur (original inchangé)
     * @param angle l'angle en radians
     * @return le nouveau vecteur
     */
    public Vec2 turnNewVector(double angle)
    {
        return new Vec2((int)(this.x * Math.cos(angle) - this.y * Math.sin(angle)),
                (int)(this.x * Math.sin(angle) + this.y * Math.cos(angle)));
    }

	
}

