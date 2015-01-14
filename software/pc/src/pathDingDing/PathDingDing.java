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
		//dodgeDynamic(path, table);
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
	/*
	private static void dodgeDynamic(Path path, Table table) throws PathNotFoundException
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
				//si un segment du chemin coupe le robot ennemi
				if(intersects(new Segment(path.get(pathCount), path.get(pathCount + 1)), ennemy.get(robotsCount)))
				{
					//si le chemin le plus court semble etre dans le sens direct
					if(Geometry.isCCWOriented(path.get(pathCount), path.get(pathCount + 1), ennemy.get(robotsCount).getPosition().toPoint()))
					{
						int precision = 0;
						Path dodgeCircle = dodgeCircle(path.get(pathCount), path.get(pathCount + 1), new Circle(ennemy.get(robotsCount).getPosition(), ennemy.get(robotsCount).getRadius()), true, precision);
						//on teste le chemin calcule
						Path toTest = new Path();
						toTest.add(path.get(pathCount));
						toTest.insert(toTest.size(), dodgeCircle);
						toTest.add(path.get(pathCount + 1));
						if(isPathCorrect(toTest, table))
							path.insert(pathCount + 1, dodgeCircle);
						else //sinon on essaye dans l'autre sens
						{
							dodgeCircle = dodgeCircle(path.get(pathCount), path.get(pathCount + 1), new Circle(ennemy.get(robotsCount).getPosition(), ennemy.get(robotsCount).getRadius()), false, precision);
							//on teste le chemin calcule
							toTest = new Path();
							toTest.add(path.get(pathCount));
							toTest.insert(toTest.size(), dodgeCircle);
							toTest.add(path.get(pathCount + 1));
							if(isPathCorrect(toTest, table))
								path.insert(pathCount + 1, dodgeCircle);
						}
					}
					else
					{
						int precision = 0;
						Path dodgeCircle = dodgeCircle(path.get(pathCount), path.get(pathCount + 1), new Circle(ennemy.get(robotsCount).getPosition(), ennemy.get(robotsCount).getRadius()), false, precision);
						//on teste le chemin calcule
						Path toTest = new Path();
						toTest.add(path.get(pathCount));
						toTest.insert(toTest.size(), dodgeCircle);
						toTest.add(path.get(pathCount + 1));
						if(isPathCorrect(toTest, table))
							path.insert(pathCount + 1, dodgeCircle);
						else //sinon on essaye dans l'autre sens
						{
							dodgeCircle = dodgeCircle(path.get(pathCount), path.get(pathCount + 1), new Circle(ennemy.get(robotsCount).getPosition(), ennemy.get(robotsCount).getRadius()), true, precision);
							//on teste le chemin calcule
							toTest = new Path();
							toTest.add(path.get(pathCount));
							toTest.insert(toTest.size(), dodgeCircle);
							toTest.add(path.get(pathCount + 1));
							if(isPathCorrect(toTest, table))
								path.insert(pathCount + 1, dodgeCircle);
						}
					}
					pathCount++;
				}
			}
		}
	}
	
	public static Path dodgeCircle(Point a, Point b, Circle circle, boolean CCW, int precision)
	{
		Path path = new Path();
		Point firstTangentPoint, secondTangentPoint;
		//a->b
		List<Point> tangentPoints = tangentPoints(a, circle);
		if(Geometry.isCCWOriented(a, tangentPoints.get(0), circle.center.toPoint()) == CCW)
			firstTangentPoint = tangentPoints.get(0);
		else
			firstTangentPoint = tangentPoints.get(1);
		//b->a
		tangentPoints = tangentPoints(b, circle);
		if(Geometry.isCCWOriented(tangentPoints.get(0), b, circle.center.toPoint()) == CCW)
			secondTangentPoint = tangentPoints.get(0);
		else
			secondTangentPoint = tangentPoints.get(1);


		path.add(intersection(a, firstTangentPoint, secondTangentPoint, b));
		
		return path;
	}
	
	public static List<Point> tangentPoints(Point point, Circle circle)
	{
		double x1, x2, y1, y2;
		double a  = point.x - circle.center.x;
		double b = point.y - circle.center.y;
		double R2 = a*a + b*b - circle.ray*circle.ray;
		double r2 = circle.ray*circle.ray;
		double c = a*a + b*b - R2 + circle.ray*circle.ray;
		a *= 2;
		b *= 2;
		double sqrtDelta = Math.sqrt(4*a*a*c*c - 4*(a*a+b*b)*(c*c-b*b*r2));
		//recherche des points d'intersection
		x1 = circle.center.x + (2*a*c - sqrtDelta)/(2*(a*a + b*b));
		x2 = circle.center.x + (2*a*c + sqrtDelta)/(2*(a*a + b*b));
		if (b == 0)
		{
			double inter = (2*c - a*a) / (2*a);
			inter = Math.sqrt(R2 - inter * inter);
			y1 = circle.center.y + 0.5*b + inter;
			y2 = circle.center.y + 0.5*b - inter;
		}
		else
		{
			y1 = circle.center.y + (c - a*(x1-circle.center.x)) / b;
			y2 = circle.center.y + (c - a*(x2-circle.center.x)) / b;
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
	*/
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
	 * si le chemin est considere comme correct pour la recherche de chemin statique
	 * @param path
	 * @param table
	 * @return
	 */
	public static boolean isPathCorrect(Path path, Table table)
	{
		if(!isOnTable(path.get(0), table))
			return false;
		for(int ind_path = 0; ind_path < path.size() - 1; ind_path++)
		{
			if(!isOnTable(path.get(ind_path + 1), table))
				return false;
			for(int ind_ligne = 0 ; ind_ligne < table.getObstacleManager().getLines().size() ; ind_ligne++)
		    	if(intersects(path.get(ind_path), path.get(ind_path + 1), table.getObstacleManager().getLines().get(ind_ligne).getA(), table.getObstacleManager().getLines().get(ind_ligne).getB()))
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
