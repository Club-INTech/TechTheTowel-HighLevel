package Pathfinding;
import java.util.ArrayList;

import smartMath.Vec2;

/**
 * Classe encapsulant les calculs de pathfinding
 * @author Marsya
 *
 */
public class Pathfinding implements Service 
{
	private Table m_table;

	public Pathfinding(Table table)
	{
		m_table = new Table();
	}
	
	public ArrayList<Vec2> computePath(Vec2 start, Vec2 end)
	{
		
	}
}
