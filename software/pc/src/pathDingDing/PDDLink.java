package pathDingDing;

import enums.Direction;

/**
 * Liens reliant les nodes du pathDingDing
 * @author julian
 *
 */
public class PDDLink
{
	/**
	 * Noeud de depart
	 */
	private PDDNode startNode;
	/**
	 * Noeud d'arrivee
	 */
	private PDDNode endNode;
	/**
	 * Sens de parcours du lien
	 * Direction.Forward  :  Depart ---> Arrivee
	 * Direction.Backward :  Depart <--- Arrivee
	 * Direction.Both     :  Depart <--> Arrivee
	 */
	private Direction direction;
	
	/**
	 * Instancie le lien
	 * @param start Noeud de depart
	 * @param end Noeud d'arrivee
	 * @param direction Sens de parcours (voir def)
	 */
	public PDDLink(PDDNode start, PDDNode end, Direction direction)
	{
		this.startNode = start;
		this.endNode = end;
		this.direction = direction;
	}
	
	/**
	 * Renvoie le node de depart
	 */
	public PDDNode getStartNode()
	{
		return this.startNode;
	}
	
	/**
	 * Renvoie le node d'arrivee
	 */
	public PDDNode getEndNode()
	{
		return this.endNode;
	}
	
	/**
	 * Renvoie le sens de parcours
	 */
	public Direction getDirection()
	{
		return this.direction;
	}
}