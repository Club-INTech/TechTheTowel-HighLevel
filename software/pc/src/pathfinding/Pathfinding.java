package pathfinding;

import smartMath.Point;
import table.Table;
import smartMath.Path;
import smartMath.Vec2;

/**
 * Classe encapsulant les calculs de pathfinding (ou pas :p)
 * @author Marsya et Étienne
 *
 */
public class Pathfinding
{
	private Table m_table;
	
	public Pathfinding(Table table)
	{
		m_table = table;
	}
	
	/**
	 * 
	 * @param segment1A point A du premier segment
	 * @param segment1B point B du premier segment
	 * @param segment2A point A de second segment
	 * @param segment2B point B du second segment
	 * @return vrai si intersection, faux sinon
	 */
	public static boolean intersects(Point segment1A, Point segment1B, Point segment2A, Point segment2B)
	{
		return (segment1B.x - segment1A.x) * (segment2B.y - segment2A.y) - (segment1B.y - segment1A.y) * (segment2B.x - segment2A.x) != 0
			&& ((segment1B.x - segment1A.x) * (segment2B.y - segment1A.y) - (segment1B.y - segment1A.y) * (segment2B.x - segment1A.x)) * ((segment1B.x - segment1A.x) * (segment2A.y - segment1A.y) - (segment1B.y - segment1A.y) * (segment2A.x - segment1A.x)) <= 0
			&& ((segment2B.x - segment2A.x) * (segment1B.y - segment2A.y) - (segment2B.y - segment2A.y) * (segment1B.x - segment2A.x)) * ((segment2B.x - segment2A.x) * (segment1A.y - segment2A.y) - (segment2B.y - segment2A.y) * (segment1A.x - segment2A.x)) <= 0
			;
	}
	
	/**
	 * 
	 * @param segment1A point A du segment 1
	 * @param segment1B point B du segment 1
	 * @param segment2A point A du segment 2
	 * @param segment2B point B du segment 2
	 * @return le point d'intersection des droites portées par les segments 1 et 2
	 */
	public static Point intersection(Point segment1A, Point segment1B, Point segment2A, Point segment2B)
	{
		double inter, k;
		
		if((segment2B.y - segment2A.y) != 0)
		{
			inter = (segment2B.x - segment2A.x) / (segment2B.y - segment2A.y);
			k = (segment1A.x - segment2A.x + inter * (segment2A.y - segment1A.y)) / (segment1B.x - segment1A.x - inter * (segment1B.y - segment1A.y));
		}
		else
			k = -(segment2A.y - segment1A.y) / (segment1B.y - segment1A.y);
		
		return new Point(segment1A.x - k * (segment1B.x - segment1A.x), segment1A.y - k * (segment1B.y - segment1A.y));
	}
	
	/**
	 * 
	 * @param start point de départ
	 * @param end point d'arrivée
	 * @return un chemin entre le point de départ et d'arrivée
	 */
	public Path computePath(Vec2 start, Vec2 end)
	{
		Point DoubleStart = new Point(start.x, start.y), DoubleEnd = new Point(end.x, end.y);
		Path path = new Path();
		path.add(DoubleStart);
		path = dodgeStatic(DoubleStart, DoubleEnd);
		path.add(DoubleEnd);
		return path;
	}
	
	/**
	 * 
	 * @param start point de départ
	 * @param end point d'arrivée
	 * @return un chemin entre le point de départ et d'arrivée en evitant uniquement les obstacles fixes
	 */
	private Path dodgeStatic(Point start, Point end)
	{
		Path path = new Path();
		
		// cherche le point d'intersection avec les obstacles le plus proche de point de départ
		double min = 13000000;
		int indiceDistMin = 0;
		Point node = new Point();
		boolean intersects = false;
		for(int ind_ligne = 0 ; ind_ligne < m_table.getLines().size() ; ind_ligne++)
    	{
	    	if( intersects(start, end, m_table.getLines().get(ind_ligne).getA(), m_table.getLines().get(ind_ligne).getB()))
	    	{
	    		node = intersection(start, end, m_table.getLines().get(ind_ligne).getA(), m_table.getLines().get(ind_ligne).getB());
	    		intersects = true;
	    		double dist = Math.pow(node.x - start.x, 2) + Math.pow(node.y - start.y, 2);
		    	if (dist <= min)
		    	{
		    		min = dist;
		    		indiceDistMin = ind_ligne;
		    	}
		    }
    	}
		//si il y a un point d'intersection, ajoute ce point au chemin, et recommence la recherche de chemin sur les chemins début -> point de passage du point d'intersection
		//                                                                                                                   point de passage du point d'intersection -> fin
		if(intersects)
		{
			//s'il n'y a qu'un seul point de passage sur l'obstacle
			if( m_table.getLines().get(indiceDistMin).getNbPassagePoint() == 1 )
			{
				path.addAll(dodgeStatic(start, m_table.getLines().get(indiceDistMin).getPassagePoint1()));
				path.add(m_table.getLines().get(indiceDistMin).getPassagePoint1());
				path.addAll(dodgeStatic(m_table.getLines().get(indiceDistMin).getPassagePoint1(), end));
			}
			//s'il y a deux points de passage sur l'obstacle, prend le point de passage le plus proche du point d'arrivée.
			else
			{
				if(Math.pow(m_table.getLines().get(indiceDistMin).getPassagePoint1().x - end.x, 2) + Math.pow(m_table.getLines().get(indiceDistMin).getPassagePoint1().y - end.y, 2) <= Math.pow(m_table.getLines().get(indiceDistMin).getPassagePoint2().x - end.x, 2) + Math.pow(m_table.getLines().get(indiceDistMin).getPassagePoint2().y - end.y, 2))
				{
					path.addAll(dodgeStatic(start, m_table.getLines().get(indiceDistMin).getPassagePoint1()));
					path.add(m_table.getLines().get(indiceDistMin).getPassagePoint1());
					path.addAll(dodgeStatic(m_table.getLines().get(indiceDistMin).getPassagePoint1(), end));
				}
				else
				{
					path.addAll(dodgeStatic(start, m_table.getLines().get(indiceDistMin).getPassagePoint2()));
					path.add(m_table.getLines().get(indiceDistMin).getPassagePoint2());
					path.addAll(dodgeStatic(m_table.getLines().get(indiceDistMin).getPassagePoint2(), end));
				}
			}
		}
		return path;
	}
	
	/**
	 * 
	 * @param path chemin à simplifier
	 * @return un chemin simplifié
	 */
	public Path simplify(Path path)
	{
		for(int i = 0; i < path.size() - 2; i++)
		{
			boolean removable = true;
			for(int ind_ligne = 0 ; ind_ligne < m_table.getLines().size() ; ind_ligne++)
	    	{
		    	if( intersects(path.getPosition(i), path.getPosition(i+2), m_table.getLines().get(ind_ligne).getA(), m_table.getLines().get(ind_ligne).getB()));
		    	{
		    		removable = false;
		    	}
	    	}
			if(removable)
			{
				path.remove(i+1);
				i--;
			}
		}
		return path;
	}
}
