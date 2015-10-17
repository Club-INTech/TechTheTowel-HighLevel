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
	
	/**
	 * Renvoie les nodes adjacents à tel node
	 * @param node le node
	 * @return
	 */
	private ArrayList<PDDNode> getRelatedNodes(PDDNode node)
	{
		ArrayList<PDDNode> related = new ArrayList<PDDNode>();
		
		for(int i = 0 ; i < links.size() ; i++)
		{
			if(links.get(i).isRelated(node))
				related.add(links.get(i).getTheOtherNode(node));
		}
		
		return related;
		
	}
}