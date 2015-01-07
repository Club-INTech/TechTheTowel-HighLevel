package smartMath;

import java.util.ArrayList;

import smartMath.Point;

// TODO: doc
public class Path
{
	private ArrayList<Point> pointArray;
	
	public Path(ArrayList<Point> array)
	{
		pointArray = array;
	}
	
	public Path()
	{
		pointArray = new ArrayList<Point>();
	}
	
	public void add(Point point)
	{
		pointArray.add(point);
	}
	
	public void add(int index, Point point)
	{
		pointArray.add(index, point);
	}
	
	public void addAll(Path path)
	{
		pointArray.addAll(path.pointArray);
	}
	
	public void insert(int index, Path path)
	{
		pointArray.addAll(index, path.pointArray);
	}
	
	public void remove(int index)
	{
		pointArray.remove(index);
	}
	
	public int size()
	{
		return pointArray.size();
	}
	
	public Point get(int index)
	{
		return pointArray.get(index);
	}
	
	/**
	 * 
	 * @return la longueur en mm d'un chemin
	 */
	public double getLenght()
	{
		double lenght = 0;
		for(int i = 0; i < pointArray.size() - 1; i++)
		{
			lenght += Math.sqrt(Math.pow(pointArray.get(i).x - pointArray.get(i + 1).x, 2) + Math.pow(pointArray.get(i).y - pointArray.get(i + 1).y, 2));
		}
		return lenght;
	}
	
	/**
	 * 
	 * @return la dur�e n�cessaire pour parcourir le chemin, en secondes
	 */
	public double duration()
	{
		// TODO longueur/vitesse + nombrePoints * tempsPourTourner 
		return 42;
	}
	
	/**
	 * inverse un chemin
	 */
	public void invert()
	{
		for(int i = 0; i < pointArray.size() / 2; i++)
		{
			Point aux = pointArray.get(i);
			pointArray.set(i, pointArray.get(pointArray.size() - 1 - i));
			pointArray.set(pointArray.size() - 1 - i, aux);
		}
	}
	
	public ArrayList<Vec2> toVec2Array()
	{
		ArrayList<Vec2> Vec2Array = new ArrayList<Vec2>();
		for(int i = 0; i < pointArray.size(); i++)
			Vec2Array.add(pointArray.get(i).toVec2());
		return Vec2Array;
	}
}
