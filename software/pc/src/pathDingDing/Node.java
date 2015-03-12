package pathDingDing;

import smartMath.*;

import java.util.ArrayList;

/**
 * noeud composant le graphe, compos� de sa position, et des noeuds auquel il est rattache.
 * @author Etienne
 *
 */
public class Node
{
	public int x;
	public int y;
	// cout heuristique = distance au point d'arrivee ( a vol d'oiseau :) )
	private double mHeuristicCost;
	// cout = distance au noeud de depart, en suivant le chemin etabli
	private double mCost;
	private ArrayList<Link> mLinks;
	// noeud precedant lors de la recherche du chemin
	private Node mPrevious;
	
	public Node(int x, int y)
	{
		mHeuristicCost = 0;
		mCost = 0;
		this.x = x;
		this.y = y;
		mLinks = new ArrayList<Link>();
	}
	
	//ajoute une lien vers le noeud en argument
	public void addLink(Node node)
	{
		mLinks.add(new Link(this, node));
	}
	
	//supprime tous les liens vers le noeud en argument
	public void deleteLink(Node node)
	{
		for(int i = 0; i < mLinks.size(); i++)
			if(node == mLinks.get(i).getDestination())
				mLinks.remove(i);
	}
	
	public double distanceTo(Node destination)
	{
		return Math.sqrt((destination.x - x)*(destination.x - x) + (destination.y - y)*(destination.y - y));
	}
	
	public Link getLink(int linkIndex)
	{
		return mLinks.get(linkIndex);
	}
	
	public int getLinkNumber()
	{
		return mLinks.size();
	}
	
	public void setHeuristicCost(Node destination)
	{
		mHeuristicCost = Math.sqrt((destination.x - x)*(destination.x - x) + (destination.y - y)*(destination.y - y));
	}
	
	public double getHeuristicCost()
	{
		return mHeuristicCost;
	}
	
	public double getCost()
	{
		return mCost;
	}
	
	//calcule le cout en considerant un noeud precedant different
	public double calculateCost(Node previous)
	{
		return previous.getCost() + distanceTo(previous);
	}
	
	public void setCost(Node previous)
	{
		mCost = previous.getCost() + distanceTo(previous);
	}
	
	public void setPrevious(Node previous)
	{
		mPrevious = previous;
	}
	
	public Node getPrevious()
	{
		return mPrevious;
	}
	
	public Vec2 toVec2()
	{
		return new Vec2(x, y);
	}
	
	//lie le noeud aux zones auquel il appartient
	public void linkToArea(Graph graph)
	{
		for(int i = 0; i < graph.getAreas().size(); i++)
			if(graph.getAreas().get(i).isInArea(this))
				graph.getAreas().get(i).attachNode(this);
	}
}
