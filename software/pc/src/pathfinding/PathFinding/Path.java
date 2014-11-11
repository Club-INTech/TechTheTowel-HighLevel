package PathFinding;

import java.util.ArrayList;

/**
 * d�finit un tableau de Nodes, correspondant � un chemin
 * @author Etienne
 *
 */
public class Path
{
	private ArrayList<Node> m_positions;
	
	public Path()
	{
		m_positions = new ArrayList<Node>();
	}
	
	public void add(Node node)
	{
		m_positions.add(node);
	}
	
	public void addAll(Path path)
	{
		m_positions.addAll(path.m_positions);
	}
	
	public double size()
	{
		return m_positions.size();
	}
	
	public Node getPosition(int index)
	{
		return m_positions.get(index);
	}
	
	public double getLenght()
	{
		double lenght = 0;
		for(int i = 0; i < m_positions.size() - 1; i++)
		{
			lenght += Math.sqrt(Math.pow(m_positions.get(i).getX() - m_positions.get(i + 1).getX(), 2) + Math.pow(m_positions.get(i).getY() - m_positions.get(i + 1).getY(), 2));
		}
		return lenght;
	}
	
	public void invert()
	{
		for(int i = 0; i < m_positions.size() / 2; i++)
		{
			Node aux = m_positions.get(i);
			m_positions.set(i, m_positions.get(m_positions.size() - 1 - i));
			m_positions.set(m_positions.size() - 1 - i, aux);
		}
	}
}
