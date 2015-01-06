package pathDingDing;

import smartMath.*;
import table.Table;
import table.obstacles.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import exceptions.*;

/**
 * Classe encapsulant les calculs de pathfinding 
 * @author Marsya et Etienne
 *
 */
public class PathDingDing
{
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
		// TODO: expliciter en comentaire les formules utilisees. 
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
	 * @return le point d'intersection des droites port�es par les segments 1 et 2
	 */
	public static Point intersection(Point segment1A, Point segment1B, Point segment2A, Point segment2B)
	{
		// TODO: commenter pour expliquer a quoi sert ces variables, et les renommer pour refléter cette utilité
		double inter, k;
		// TODO: expliciter en comentaire les formules utilisés. 
		
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
	 * @param start point de d�part
	 * @param end point d'arriv�e
	 * @return un chemin entre le point de d�part et d'arriv�e
	 * @throws BlockedException 
	 */
	public static ArrayList<Vec2> computePath(Vec2 start, Vec2 end, Table table) throws PathNotFoundException
	{
		if(!isOnTable(start.toPoint(), table) || !isOnTable(end.toPoint(), table))
			throw new PathNotFoundException();
		Point DoubleStart = new Point(start.x, start.y), DoubleEnd = new Point(end.x, end.y);
		Path path = new Path();
		path.add(DoubleStart);
		path.addAll(dodgeStatic(DoubleStart, DoubleEnd, table));
		path.add(DoubleEnd);
		simplify(path, table);
		dodgeDynamic(path, table);
		return path.toVec2Array();
	}
	
	/**
	 * 
	 * @param start point de d�part
	 * @param end point d'arriv�e
	 * @return un chemin entre le point de d�part et d'arriv�e en evitant uniquement les obstacles fixes
	 * @throws BlockedException 
	 */
	private static Path dodgeStatic(Point start, Point end, Table table) throws PathNotFoundException
	{
		Path path = new Path();
		
		// cherche le point d'intersection avec les obstacles le plus proche de point de d�part
		double min = 13000000;
		int indiceDistMin = 0;
		Point node = new Point();
		boolean intersects = false;
		for(int ind_ligne = 0 ; ind_ligne < table.getObstacleManager().getLines().size() ; ind_ligne++)
    	{
	    	if( intersects(start, end, table.getObstacleManager().getLines().get(ind_ligne).getA(), table.getObstacleManager().getLines().get(ind_ligne).getB()))
	    	{
	    		node = intersection(start, end, table.getObstacleManager().getLines().get(ind_ligne).getA(), table.getObstacleManager().getLines().get(ind_ligne).getB());
	    		intersects = true;
	    		double dist = Math.pow(node.x - start.x, 2) + Math.pow(node.y - start.y, 2);
		    	if (dist <= min)
		    	{
		    		min = dist;
		    		indiceDistMin = ind_ligne;
		    	}
		    }
    	}
		//si il y a un point d'intersection, ajoute ce point au chemin, et recommence la recherche de chemin sur les chemins d�but -> point de passage du point d'intersection
		//                                                                                                                   point de passage du point d'intersection -> fin
		if(intersects)
		{
			//s'il n'y a qu'un seul point de passage sur l'obstacle
			if( table.getObstacleManager().getLines().get(indiceDistMin).getNbPassagePoint() == 1 )
			{
				path.addAll(dodgeStatic(start, table.getObstacleManager().getLines().get(indiceDistMin).getPassagePoint1(), table));
				path.add(table.getObstacleManager().getLines().get(indiceDistMin).getPassagePoint1());
				path.addAll(dodgeStatic(table.getObstacleManager().getLines().get(indiceDistMin).getPassagePoint1(), end, table));
			}
			//s'il y a deux points de passage sur l'obstacle, prend le point de passage le plus proche du point d'arriv�e.
			else
			{
				if(Math.pow(table.getObstacleManager().getLines().get(indiceDistMin).getPassagePoint1().x - end.x, 2) + Math.pow(table.getObstacleManager().getLines().get(indiceDistMin).getPassagePoint1().y - end.y, 2) <= Math.pow(table.getObstacleManager().getLines().get(indiceDistMin).getPassagePoint2().x - end.x, 2) + Math.pow(table.getObstacleManager().getLines().get(indiceDistMin).getPassagePoint2().y - end.y, 2))
				{
					path.addAll(dodgeStatic(start, table.getObstacleManager().getLines().get(indiceDistMin).getPassagePoint1(), table));
					path.add(table.getObstacleManager().getLines().get(indiceDistMin).getPassagePoint1());
					path.addAll(dodgeStatic(table.getObstacleManager().getLines().get(indiceDistMin).getPassagePoint1(), end, table));
				}
				else
				{
					path.addAll(dodgeStatic(start, table.getObstacleManager().getLines().get(indiceDistMin).getPassagePoint2(), table));
					path.add(table.getObstacleManager().getLines().get(indiceDistMin).getPassagePoint2());
					path.addAll(dodgeStatic(table.getObstacleManager().getLines().get(indiceDistMin).getPassagePoint2(), end, table));
				}
			}
		}
		return path;
	}
	
	/**
	 * 
	 * @param path
	 * @param table
	 * @return
	 * @throws PathNotFoundException
	 */
	private static Path dodgeDynamic(Path path, Table table) throws PathNotFoundException
	{
		ArrayList<ObstacleCircular> ennemy = table.getObstacleManager().getEnnemyRobot();
		//parcours des robots ennemis
		for(int robotsCount = 0; robotsCount < ennemy.size(); robotsCount++)
		{
			//parcours des points du chemin
			for(int pathCount = 0; pathCount < path.size(); pathCount++)
			{
				//si un point est dans un robot, on le supprime
				if((path.get(pathCount).x - ennemy.get(robotsCount).getPosition().x)*(path.get(pathCount).x - ennemy.get(robotsCount).getPosition().x) + (path.get(pathCount).y - ennemy.get(robotsCount).getPosition().y)*(path.get(pathCount).y - ennemy.get(robotsCount).getPosition().y) <= ennemy.get(robotsCount).getRadius()*ennemy.get(robotsCount).getRadius())
				{
					path.remove(pathCount);
					pathCount--;
				}
			}
			
			//parcours des points du chemin
			for(int pathCount = 0; pathCount < path.size() - 1; pathCount++)
			{
				//si un segment coupe le robot ennemi
				if(intersects(new Segment(path.get(pathCount), path.get(pathCount + 1)), ennemy.get(robotsCount)))
				{
					
				}
			}
		}
		return path;
	}
	
	public Path dodgeCircle(Point a, Point b, Circle circle)
	{
		Path path = new Path();
		path.add(a);
		
		path.add(b);
		return path;
	}
	
	public static List<Point> tangentPoints(Point point, ObstacleCircular circle)
	{
		double x1, x2, y1, y2;
		double a  = point.x - circle.getPosition().x;
		double b = point.y - circle.getPosition().y;
		double R2 = a*a + b*b - circle.getRadius()*circle.getRadius();
		double r2 = circle.getRadius()*circle.getRadius();
		double c = a*a + b*b - R2 + circle.getRadius()*circle.getRadius();
		a *= 2;
		b *= 2;
		double sqrtDelta = Math.sqrt(4*a*a*c*c - 4*(a*a+b*b)*(c*c-b*b*r2));
		//recherche des points d'intersection
		x1 = circle.getPosition().x + (2*a*c - sqrtDelta)/(2*(a*a + b*b));
		x2 = circle.getPosition().x + (2*a*c + sqrtDelta)/(2*(a*a + b*b));
		if (b == 0)
		{
			double inter = (2*c - a*a) / (2*a);
			inter = Math.sqrt(R2 - inter * inter);
			y1 = circle.getPosition().y + 0.5*b + inter;
			y2 = circle.getPosition().y + 0.5*b - inter;
		}
		else
		{ 
			y1 = circle.getPosition().y + (c - a*(x1-circle.getPosition().x)) / b;
			y2 = circle.getPosition().y + (c - a*(x2-circle.getPosition().x)) / b;
		}
		return Arrays.asList(new Point(x1, y1), new Point(x2, y2));
	}
	
	public static boolean intersects(Segment line, ObstacleCircular circle)
	{
		double aire = (circle.getPosition().x - line.getA().x)*(line.getB().y - line.getA().y) - (circle.getPosition().y - line.getA().y)*(line.getB().x - line.getA().x);
		return aire * aire / ((line.getB().x - line.getA().x)*(line.getB().x - line.getA().x)+(line.getB().y - line.getA().y)*(line.getB().y - line.getA().y)) <= circle.getRadius() * circle.getRadius()
			&& (line.getB().x - line.getA().x)*(circle.getPosition().x - line.getA().x) + (line.getB().y - line.getA().y)*(circle.getPosition().y - line.getA().y) >= 0
			&& (line.getA().x - line.getB().x)*(circle.getPosition().x - line.getB().x) + (line.getA().y - line.getB().y)*(circle.getPosition().y - line.getB().y) >= 0 ;
	}
	
	/**
	 * 
	 * @param point
	 * @param table
	 * @return vrai si le point est accessible, faux sinon
	 */
	public static boolean isOnTable(Point point, Table table)
	{
		//TODO : ajouter le rayon du robot
		if (point.x <= -1310 || point.x >= 1310 || point.y <= 190 || point.y >= 1740)
			return false;
		ArrayList<ObstacleRectangular> rects = table.getObstacleManager().getRects();
		for(int i = 0; i < rects.size(); i++)
		{
			if(point.x <= rects.get(i).getPosition().x + rects.get(i).getSizeX() / 2 + 190
			&& point.x >= rects.get(i).getPosition().x - rects.get(i).getSizeX() / 2 - 190
			&& point.y <= rects.get(i).getPosition().y + rects.get(i).getSizeY() + 190
			&& point.y >= rects.get(i).getPosition().y - 190)
				return false;
		}
		return true;
	}
	
	/**
	 * 
	 * @param path chemin a simplifier
	 * @return un chemin simplifie
	 */
	public static Path simplify(Path path, Table table)
	{
		
		// TODO: expliciter en commentaire l'algo
		
		for(int i = 0; i < path.size() - 2; i++)
		{
			boolean removable = true;
			for(int ind_ligne = 0 ; ind_ligne < table.getObstacleManager().getLines().size() ; ind_ligne++)
	    	{
		    	if( intersects(path.get(i), path.get(i+2), table.getObstacleManager().getLines().get(ind_ligne).getA(), table.getObstacleManager().getLines().get(ind_ligne).getB()));
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
