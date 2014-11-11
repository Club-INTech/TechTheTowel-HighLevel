package PathFinding;

/**
 * notre robot, mod�lis� comme un disque, avec sa position, son rayon
 * @author Etienne
 *
 */
public class Robot
{
	private double m_radius;
	private Node m_position;
	private Node m_destination;

	public Robot(Node position, double radius)
	{
		m_position = position;
		m_radius = radius;
		m_destination = new Node(10, 10);
	}
	
	public Node getPosition()
	{
		return m_position;
	}
	
	public Node getDestination()
	{
		return m_destination;
	}
	
	public double getRadius()
	{
		return m_radius;
	}

	public void setPosition(Node position)
	{
		m_position = position;
	}
	
	public void setDestination(Node destination)
	{
		m_destination = destination;
	}
}
