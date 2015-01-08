package smartMath;

/**
 * segment, coordonnees double
 * @author Etienne
 *
 */
public class Segment
{
	private Point mA;
	private Point mB;
	
	public Segment()
	{
		mA = new Point(0, 0);
		mB = new Point(0, 0);
	}
	
	public Segment(Point a, Point b)
	{
		mA = a;
		mB = b;
	}

	public Point getA()
	{
		return mA;
	}
	
	public Point getB()
	{
		return mB;
	}
	
	public void setA(Point a)
	{
		mA = a;
	}
	
	public void setB(Point b)
	{
		mB = b;
	}
}
