package pathDingDing;

import enums.Direction;

/**
 * Liens reliant les nodes du pathDingDing
 * @author julian
 *
 */
public class Link
{
	/**
	 * Noeud de depart
	 */
	private Node startNode;
	
	/**
	 * Noeud d'arrivee
	 */
	private Node endNode;
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
	public Link(Node start, Node end, Direction direction)
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
	public Link(Node start, Node end)
	{
		this.startNode = start;
		this.endNode = end;
		this.direction = Direction.Both;
	}
	
	/**
	 * Renvoie le node de depart
	 */
	public Node getStartNode()
	{
		return this.startNode;
	}
	
	/**
	 * Renvoie le node d'arrivee
	 */
	public Node getEndNode()
	{
		return this.endNode;
	}
	
	/**
	 * Revoie si le lien est lié ou non au node donné
	 */
	public boolean isRelated(Node node)
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
	public Node getTheOtherNode(Node node)
	{
		if(endNode == node)
		{
			return startNode;
		}
		return endNode;
	}
}
