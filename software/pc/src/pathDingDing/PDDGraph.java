package pathDingDing;

import java.util.ArrayList;

import table.Table;

/**
 * Graphe de la table, utilise par le pathDingDing pour creer un chemin sur la table et le parcourir
 * @author julian
 *
 */
public class PDDGraph
{
	/**
	 * Nodes statiques du graphe, c'est a dire permaments sur la tables (pas utilises pour l'evitement)
	 */
	private ArrayList<PDDNode> nodes;
	
	/**
	 * Liens statiques, relient les nodes statiques pour creer des chemins
	 */
	private ArrayList<PDDLink> links;
	
	/**
	 * Instancie le graphe
	 */
	public PDDGraph()
	{
		this.nodes = new ArrayList<PDDNode>();
		this.links = new ArrayList<PDDLink>();
		
		createGraph();
	}
	
	/**
	 * Cree le graphe de nodes statiques, celui ci est hard codé en prenant compte de la largeur du robot 
	 */
	private void createGraph()
	{
		//TODO Creer le graphe (LONG)
		// On ajoute tous les nodes et on les relie ensemble en verifiant si on "intersect()" pas un obstacle
		
		//Node du point de depart du robot
		this.nodes.add(new PDDNode(Table.entryPosition));
	}
	
	/**
	 * Renvoie le noeud numero i
	 * @param i le numero du noeud
	 */
	private PDDNode getNode(int i)
	{
		return this.nodes.get(i);
	}
	
	/**
	 * Renvoie le lien numero i
	 * @param i numero du lien
	 */
	private PDDLink getLink(int i)
	{
		return this.links.get(i);
	}
	
	/**
	 * Renvoie les nodes adjacents à tel node
	 * @param node le node a tester
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