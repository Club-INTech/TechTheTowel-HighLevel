package pathDingDing;

import java.util.ArrayList;

/**
 * Zone rectangulaire de la table
 * @author Etienne
 *
 */
public class Area
{
	//coordonnees et dimensions
	public int x; // centre de la zone
	public int y; // bas de la zone
	public int width;
	public int height;
	
	//noeuds fixes rattaches a la zone
	private ArrayList<Node> mAttachedNodes;
	
	/**
	 * constructeur
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public Area(int x, int y, int width, int height)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		
		mAttachedNodes = new ArrayList<Node>();
	}
	
	public void attachNode(Node attachedNode)
	{
		mAttachedNodes.add(attachedNode);
	}
	
	public Node getAttachedNode(int attachedNodesIndex)
	{
		return mAttachedNodes.get(attachedNodesIndex);
	}
	
	public int attachedNodesNumber()
	{
		return mAttachedNodes.size();
	}
	
	/**
	 * 
	 * @param node
	 * @return vrai si le noeud est dans la zone, faux sinon
	 */
	public boolean isInArea(Node node)
	{
		return node.x <= x + width/2 && node.x >= x - width/2 && node.y <= y + height && node.y >= y;
	}
}
