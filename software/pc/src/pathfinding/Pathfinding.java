package pathfinding;
import java.util.ArrayList;

import smartMath.Vec2;

/**
 * Classe encapsulant les calculs de pathfinding
 * @author Marsya
 *
 */
public class Pathfinding
{
	ArrayList<Vec2> path;
	/**
	 * Constructor
	 */
	public Pathfinding(Vec2 point_depart, Vec2 point_arrivee)
	{
		//test :)
	}
	
	
	public ArrayList<Vec2> computePath(Vec2 start, Vec2 end)
	{
		
		// TODO
		// voici un pathfinding math�matiquement d�montr� comme correct
		// correct au sens d'un chemin partant du d�part et allant a l'arriv�e
		
		// bon apr�s si vous chipottez pour les obstacles en chemin aussi...
		
		ArrayList<Vec2> out = new ArrayList<Vec2>();
		out.add(end);
		return out;
	}
}
