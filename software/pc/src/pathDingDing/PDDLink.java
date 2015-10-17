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
	 * En principe pour le robot c'est inutile, c'est pour cela qu'il est defini par defaut sur Direction.Both
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
	 * Instancie le lien
	 * @param start Noeud de depart
	 * @param end Noeud d'arrivee
	 */
	public PDDLink(PDDNode start, PDDNode end)
	{
		this.startNode = start;
		this.endNode = end;
		this.direction = Direction.Both;
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
	 * Revoie si le lien est lié ou non au node donné
	 */
	public boolean isRelated(PDDNode node)
	{
		return (node == startNode)||(node == endNode);
	}
	
	/**
	 * Renvoie le sens de parcours
	 */
	public Direction getDirection()
	{
		return this.direction;
	}
	
	/**
	 * Revoie le node de depart si le node d'arrivee est donne, sinon il renvoie le node d'arrivee
	 */
	public PDDNode getTheOtherNode(PDDNode node)
	{
		if(endNode == node)
		{
			return startNode;
		}
		return endNode;
	}
}
