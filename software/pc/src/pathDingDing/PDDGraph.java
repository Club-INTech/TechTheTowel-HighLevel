package pathDingDing;

import java.util.ArrayList;

/**
 * Graphe de la table, utilise par le pathDingDing
 * @author julian
 *
 */
public class PDDGraph
{
	private ArrayList<PDDNode> nodes;
	private ArrayList<PDDLink> links;
	
	public PDDGraph()
	{
		this.nodes = new ArrayList<PDDNode>();
		this.links = new ArrayList<PDDLink>();
		
		createGraph();
	}
	
	private void createGraph()
	{
		//TODO Creer le graphe (LONG)
	}
	
	private PDDNode getNode(int i)
	{
		return this.nodes.get(i);
	}
	
	private PDDLink getLink(int i)
	{
		return this.links.get(i);
	}
}