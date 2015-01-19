package table.obstacles;

import smartMath.Point;

/**
 * classe non complètement rattachée au projet. Eventuellement à refaire...
 * TODO: la description de cette classe doit être compléter pour expliquer ce a quoi elle sert
 * @author Étienne
 *
 */
public class ObstacleLinear
{
	protected Point m_A, m_B;
	private Point m_passagePoint1, m_passagePoint2;
	private int m_nbPassagePoint;
	
	public ObstacleLinear(Point A, Point B, int nbPsgPt, Point psgPt1, Point psgPt2)
	{
		m_A = A;
		m_B = B;
		m_passagePoint1 = psgPt1;
		m_passagePoint2 = psgPt2;
		m_nbPassagePoint = nbPsgPt;
	}
	
	/*
	public ObstacleCircular clone()
	{
		return new ObstacleCircular(position.clone(), radius);
	}
	
	// Copie this dans oc, sans modifier this
	public void clone(ObstacleCircular oc)
	{
		oc.position = position;
		oc.radius = radius;
	}
	*/

	
	public Point getA()
	{
		return m_A;
	}
	
	public Point getB()
	{
		return m_B;
	}
	
	public int getNbPassagePoint()
	{
		return m_nbPassagePoint;
	}
	
	public Point getPassagePoint1()
	{
		return m_passagePoint1;
	}
	
	public Point getPassagePoint2()
	{
		return m_passagePoint2;
	}
}