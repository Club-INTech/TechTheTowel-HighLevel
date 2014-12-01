package smartMath;

import java.util.ArrayList;

import smartMath.Point;

public class Path
{
	private ArrayList<Point> PointArray;
	
	public Path(ArrayList<Point> array)
	{
		PointArray = array;
	}
	
	public Path()
	{
		PointArray = new ArrayList<Point>();
	}
	
	public void add(Point point)
	{
		PointArray.add(point);
	}
	
	public void addAll(Path path)
	{
		PointArray.addAll(path.PointArray);
	}
	
	public void insert(int index, Path path)
	{
		PointArray.addAll(index, path.PointArray);
	}
	
	public void remove(int index)
	{
		PointArray.remove(index);
	}
	
	public double size()
	{
		return PointArray.size();
	}
	
	public Point getPosition(int index)
	{
		return PointArray.get(index);
	}
	
	/**
	 * 
	 * @return la longueur en mm d'un chemin
	 */
	public double getLenght()
	{
		double lenght = 0;
		for(int i = 0; i < PointArray.size() - 1; i++)
		{
			lenght += Math.sqrt(Math.pow(PointArray.get(i).x - PointArray.get(i + 1).x, 2) + Math.pow(PointArray.get(i).y - PointArray.get(i + 1).y, 2));
		}
		return lenght;
	}
	
	/**
	 * 
	 * @return la durée nécessaire pour parcourir le chemin, en secondes
	 */
	public double duration()
	{
		// TODO
		return 42;
	}
	
	/**
	 * inverse un chemin
	 */
	public void invert()
	{
		for(int i = 0; i < PointArray.size() / 2; i++)
		{
			Point aux = PointArray.get(i);
			PointArray.set(i, PointArray.get(PointArray.size() - 1 - i));
			PointArray.set(PointArray.size() - 1 - i, aux);
		}
	}
}
