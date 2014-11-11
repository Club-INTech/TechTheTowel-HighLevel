package PathFinding;
/**
 * d�finit un obstacle comme un segment, constitu� deux points A et B, et un ou deux points de passage
 * @author Etienne
 *
 */
public class ObstableSegm
{
	private Node m_A, m_B;
	private Node m_passagePoint1, m_passagePoint2;
	private int m_nbPassagePoint;

	public ObstableSegm(Node A, Node B, int nbPsgPt, Node psgPt1, Node psgPt2)
	{
		m_A = A;
		m_B = B;
		m_passagePoint1 = psgPt1;
		m_passagePoint2 = psgPt2;
		m_nbPassagePoint = nbPsgPt;
	}
	
	public Node getA()
	{
		return m_A;
	}
	
	public Node getB()
	{
		return m_B;
	}
	
	public int getNbPassagePoint()
	{
		return m_nbPassagePoint;
	}
	
	public Node getPassagePoint1()
	{
		return m_passagePoint1;
	}
	
	public Node getPassagePoint2()
	{
		return m_passagePoint2;
	}
}
