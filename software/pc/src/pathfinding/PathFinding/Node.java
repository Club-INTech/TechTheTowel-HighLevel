package PathFinding;
/**
 * definit un couple de reels
 * @author Etienne
 * 
 */
public class Node
{

	private double m_x;
	private double m_y;
	
	public Node(double x, double y)
	{
		m_x = x;
		m_y = y;
	}
	
	public double getX()
	{
		return m_x;
	}
	
	public double getY()
	{
		return m_y;
	}
	
	public void move(double x, double y)
	{
		m_x = x;
		m_y = y;
	}
}
