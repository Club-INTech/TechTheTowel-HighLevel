package smartMath;

/**
 * segment, coordonnees double
 * @author Etienne
 *
 */
public class Segment
{
	private Vec2 mPointA;
	private Vec2 mPointB;
	
	public Segment()
	{
		mPointA = new Vec2();
		mPointB = new Vec2();
	}
	
	public Segment(Vec2 pointA, Vec2 pointB)
	{
		mPointA = pointA;
		mPointB = pointB;
	}

	public Vec2 getA()
	{
		return mPointA;
	}
	
	public Vec2 getB()
	{
		return mPointB;
	}
	
	public void setA(Vec2 pointA)
	{
		mPointA = pointA;
	}
	
	public void setB(Vec2 pointB)
	{
		mPointB = pointB;
	}

	public static double squaredLength(Vec2 pointA, Vec2 pointB)
	{
		return (pointB.x - pointA.x)*(pointB.x - pointA.x) + (pointB.y - pointA.y)*(pointB.y - pointA.y);

	}
}
