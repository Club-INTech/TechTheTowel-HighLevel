package smartMath;

/**
 * definit un couple de reels
 * @author Etienne
 * 
 */
public class Point
{
	//TODO: doc
	
	
	public double x;
	public double y;
	
	public Point()
	{
		x = 0;
		y = 0;
	}

	public Point(double X, double Y)
	{
		x = X;
		y = Y;
	}
	
	public void setPosition(Point position)
	{
		x = position.x;
		y = position.y;
	}

	public void setPosition(double X, double Y)
	{
		x = X;
		y = Y;
	}
	
	public Vec2 toVec2()
	{
		return new Vec2((int)x, (int)y);
	}
}
