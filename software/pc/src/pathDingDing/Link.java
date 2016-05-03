package pathDingDing;

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
	 * Instancie le lien
	 * @param start Noeud de depart
	 * @param end Noeud d'arrivee
	 */
	public Link(Node start, Node end)
	{
		this.startNode = start;
		this.endNode = end;


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

	public boolean equals(Link other)
	{
		return other.isRelated(this.endNode) && other.isRelated(this.startNode);
	}

	public boolean equals(Node a, Node b)
	{
		return this.isRelated(a) && this.isRelated(b);
	}
    
}
