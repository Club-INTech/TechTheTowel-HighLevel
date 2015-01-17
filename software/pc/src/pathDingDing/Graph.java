package pathDingDing;

import smartMath.*;
import table.Table;
import table.obstacles.*;

import java.util.ArrayList;

/**
 * graphe definissant les liens entre les noeuds
 * @author Etienne
 *
 */
public class Graph
{
	private ArrayList<Node> mNodes;
	private Node mStartNode;
	private Node mEndNode;
	private ArrayList<Area> mAreas;
	
	public Graph()
	{
		mNodes = new ArrayList<Node>();
		mAreas = new ArrayList<Area>();
		buildGraph();
	}
	
	/**
	 * methode interne a la classe, mettant en place le graphe a sa construction
	 */
	private void buildGraph()
	{
		mNodes.add(new Node(-1100, 1222));//noeud 0
		mNodes.add(new Node(-1100, 1200));//noeud 1
		mNodes.add(new Node(-1100, 800));//noeud 2
		mNodes.add(new Node(-1100, 778));//noeud 3
		mNodes.add(new Node(-300, 100));//noeud 4
		mNodes.add(new Node(300, 100));//noeud 5
		mNodes.add(new Node(1100, 778));//noeud 6
		mNodes.add(new Node(1100, 800));//noeud 7
		mNodes.add(new Node(1100, 1200));//noeud 8
		mNodes.add(new Node(1100, 1222));//noeud 9
		mNodes.add(new Node(533, 1420));//noeud 10
		mNodes.add(new Node(-533, 1420));//noeud 11
		
		for(int i = 0; i < mNodes.size(); i++)
			for(int j = 0; j < mNodes.size(); j++)
				if(i != j)
					mNodes.get(i).addLink(mNodes.get(j));
		
		Area area = new Area(-1300, 1222, 400, 800);//zone 0
		area.attachNode(mNodes.get(0));
		area.attachNode(mNodes.get(11));
		mAreas.add(area);
		
		area = new Area(-1300, 0, 400, 800);//zone 1
		area.attachNode(mNodes.get(3));
		area.attachNode(mNodes.get(4));
		mAreas.add(area);
		
		area = new Area(-700, 0, 800, 100);//zone 2
		area.attachNode(mNodes.get(3));
		area.attachNode(mNodes.get(4));
		mAreas.add(area);
		
		area = new Area(700, 0, 800, 100);//zone 3
		area.attachNode(mNodes.get(5));
		area.attachNode(mNodes.get(6));
		mAreas.add(area);
		
		area = new Area(1300, 0, 400, 800);//zone 4
		area.attachNode(mNodes.get(5));
		area.attachNode(mNodes.get(6));
		mAreas.add(area);
		
		area = new Area(1300, 1222, 400, 800);//zone 5
		area.attachNode(mNodes.get(9));
		area.attachNode(mNodes.get(10));
		mAreas.add(area);
		
		area = new Area(817, 1420, 568, 530);//zone 6
		area.attachNode(mNodes.get(9));
		area.attachNode(mNodes.get(10));
		mAreas.add(area);
		
		area = new Area(-817, 1420, 568, 530);//zone 7
		area.attachNode(mNodes.get(0));
		area.attachNode(mNodes.get(11));
		mAreas.add(area);
		
		area = new Area(0, 100, 2200, 1320);//zone 10
		area.attachNode(mNodes.get(0));
		area.attachNode(mNodes.get(1));
		area.attachNode(mNodes.get(2));
		area.attachNode(mNodes.get(3));
		area.attachNode(mNodes.get(4));
		area.attachNode(mNodes.get(5));
		area.attachNode(mNodes.get(6));
		area.attachNode(mNodes.get(7));
		area.attachNode(mNodes.get(8));
		area.attachNode(mNodes.get(9));
		area.attachNode(mNodes.get(10));
		area.attachNode(mNodes.get(11));
		mAreas.add(area);
	}
	
	public ArrayList<Node> getNodes()
	{
		return mNodes;
	}
	
	public Node getStartNode()
	{
		return mStartNode;
	}
	
	public Node getEndNode()
	{
		return mEndNode;
	}
	
	//specifie le point de depart et le lie au graphe en fonction de sa zone
	public void setStartNode(Node startNode)
	{
		mStartNode = startNode;
		//on parcourt les zones pour trouver celle dans laquelle le noeud
		for(int i = 0; i < mAreas.size(); i++)
		{
			//si on en trouve une zone dans laquelle il est
			if(mAreas.get(i).isInArea(startNode))
			{
				//on le rattache a tous les points de la zone
				for(int j = 0; j < mAreas.get(i).attachedNodesNumber(); j++)
				{
					mStartNode.addLink(mAreas.get(i).getAttachedNode(j));
				}
			}
		}
	}
	
	//specifie le point d'arrivee et le lie au graphe en fonction de sa zone
	public void setEndNode(Node endNode)
	{
		mEndNode = endNode;
		//on parcourt les zones pour trouver celle dans laquelle le noeud
		for(int i = 0; i < mAreas.size(); i++)
		{
			//si on en trouve une zone dans laquelle il est
			if(mAreas.get(i).isInArea(mEndNode))
			{
				//on le rattache a tous les points de la zone
				for(int j = 0; j < mAreas.get(i).attachedNodesNumber(); j++)
				{
					mAreas.get(i).getAttachedNode(j).addLink(mEndNode);
				}
			}
		}
	}
	
	//trie une liste de noeuds en fonction de leur heuristique
	// non utilisee dans l'algorithme
	public static ArrayList<Node> sort(ArrayList<Node> nodes)
	{
		for(int i = 1; i < nodes.size(); i++)
		{
			int j = 0;
			while(nodes.get(i).getHeuristicCost() < nodes.get(j).getHeuristicCost())
			{
				j++;
			}
			nodes.add(j, nodes.get(i));
			nodes.remove(i+1);
		}
		return nodes;
	}
	
	public ArrayList<Area> getAreas()
	{
		return mAreas;
	}
}
