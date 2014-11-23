package smartMath;

/**
 * definit un couple de reels
 * @author Etienne
 * 
 */
public class Point
{
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
}
