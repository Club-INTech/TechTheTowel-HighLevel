package PathFinding;
/**
 * obstacle rectangulaire, d�finit par ses coordonn�es, sa longueur, sa largeur
 * @author Etienne
 *
 */
public class ObstacleRect
{
	private double m_x, m_y, m_width, m_length;
	
	public ObstacleRect(double x, double y, double width, double length)
	{
		m_x = x;
		m_y = y;
		m_width = width;
		m_length = length;
	}
	
	public double getX()
	{
		return m_x;
	}
	
	public double getY()
	{
		return m_y;
	}
	
	public double getWidth()
	{
		return m_width;
	}
	
	public double getLength()
	{
		return m_length;
	}
	
	public boolean isInRect(double x, double y)
	{
		return x <= m_x + m_width && x >= m_x && y <= m_y + m_length && y >= m_y;
	}
}
