package pathDingDing;

import java.util.ArrayList;

import smartMath.Geometry;
import smartMath.Segment;
import smartMath.Vec2;
import table.Table;
import table.obstacles.ObstacleCircular;
import table.obstacles.ObstacleManager;
import table.obstacles.ObstacleRectangular;
import utils.Log;

/**
 * Graphe de la table, utilise par le pathDingDing pour creer un chemin sur la table et le parcourir
 * @author julian
 *
 */
public class Graph
{
	/**
	 * Nodes statiques du graphe, c'est a dire permaments sur la tables (pas utilises pour l'evitement)
	 */
	private ArrayList<Node> nodes;
	
	/**
	 * Liens statiques, relient les nodes statiques pour creer des chemins
	 */
	private ArrayList<Link> links;
	
	/**
	 * La Table
	 */
	private Table table;
	
	/**
	 * Le log
	 */
	private Log log;
	
	/**
	 * Le Gestionnaire d'Obstacles
	 */
	private ObstacleManager obstacleManager;
	
	/**
	 * Instancie le graphe
	 * @param table 
	 */
	public Graph(Table table, Log log)
	{
		this.nodes = new ArrayList<Node>();
		this.links = new ArrayList<Link>();
		this.table = table;
		this.log = log;
		this.obstacleManager = table.getObstacleManager();
		
		createGraph();
	}
	
	/**
	 * Cree le graphe de nodes statiques, celui ci est hard codé en prenant compte de la largeur du robot 
	 */
	private void createGraph()
	{
		//TODO Creer le graphe (LONG)
		// On ajoute tous les nodes et on les relie ensemble en verifiant si on "intersect()" pas un obstacle
		
		//========================================
		// CE GRAPHE N'EST PAS LE FINAL !!!
		// IL NE SERT QUE DE TEST DU PATHDINGDING
		//========================================
		
		nodes.add(new Node(new Vec2(-1350, 1150)));
		nodes.add(new Node(new Vec2(-1050, 1800)));
		nodes.add(new Node(new Vec2(-1050, 1500)));
		nodes.add(new Node(new Vec2(-1050, 1150)));
		nodes.add(new Node(new Vec2(-1050, 400)));
		nodes.add(new Node(new Vec2(-800, 1500)));
		nodes.add(new Node(new Vec2(-800, 1150)));
		nodes.add(new Node(new Vec2(-800, 400)));
		nodes.add(new Node(new Vec2(-300, 1150)));
		
		for(int i=0 ; i<nodes.size() ; i++)
		{
			nodes.add(new Node(new Vec2((-1*nodes.get(i).getPosition().x),nodes.get(i).getPosition().y)));
		}
		
		nodes.add(new Node(new Vec2(0, 1500)));
		nodes.add(new Node(new Vec2(0, 200)));
		
		setAllLinks();
	}
	
	/**
	 * Relie tous les noeuds ensemble en vérifiant s'il n'y a pas d'intersection avec un obstacle
	 */
	public void setAllLinks()
	{
		//On récupère les différents obstacles
		ArrayList<ObstacleRectangular> rectangularObstacles = obstacleManager.getRectangles();
		ArrayList<ObstacleCircular> circleObstacles = obstacleManager.getFixedObstacles();
		ArrayList<Segment> lineObstacles = obstacleManager.getLines();
				
		//Booléen indiquant si la liaison est possible
		boolean ok;
		
		for(int i=0 ; i < nodes.size() ; i++)
		{
			for(int j=0 ; j < nodes.size() ; j++)
			{
				if(j>i)
				{
					
					ok = true;
					
					//On vérifie l'intersection avec les cercles
					for(int k=0 ; k<circleObstacles.size() ; k++)
					{
						if(!Geometry.intersects(new Segment(nodes.get(j).getPosition(), nodes.get(i).getPosition()), circleObstacles.get(k).toCircle()))
						{
							ok = false;
						}
					}
					
					//On vérifie l'intersection avec les lignes
					for(int k=0 ; k<lineObstacles.size() ; k++)
					{
						if(!Geometry.intersects(new Segment(nodes.get(j).getPosition(), nodes.get(i).getPosition()), lineObstacles.get(k)))
						{
							ok = false;
						}
					}
					
					//On vérifie l'intersection
					for(int k=0 ; k<rectangularObstacles.size() ; k++)
					{
						ArrayList<Segment> segments = rectangularObstacles.get(k).getSegments();
						for(int l=0 ; l<segments.size() ; l++)
						{
							if(!Geometry.intersects(new Segment(nodes.get(j).getPosition(), nodes.get(i).getPosition()), segments.get(l)))
							{
								ok = false;
							}
						}
					}
					
					if(ok)
					{
						links.add(new Link(nodes.get(j), nodes.get(i)));
					}
				}
			}
		}
	}
	
	/**
	 * Ajoute un noeud et crée tous les liens qui le relient
	 * @param node le noeud
	 */
	public void addNode(Node node)
	{
		//On récupère les différents obstacles
		ArrayList<ObstacleRectangular> rectangularObstacles = obstacleManager.getRectangles();
		ArrayList<ObstacleCircular> circleObstacles = obstacleManager.getFixedObstacles();
		ArrayList<Segment> lineObstacles = obstacleManager.getLines();
						
		//Booléen indiquant si la liaison est possible
		boolean ok;
				
		for(int i=0 ; i<nodes.size() ; i++)
		{
			
			ok = true;
			
			//On vérifie l'intersection avec les cercles
			for(int k=0 ; k<circleObstacles.size() ; k++)
			{
				if(!Geometry.intersects(new Segment(node.getPosition(), nodes.get(i).getPosition()), circleObstacles.get(k).toCircle()))
				{
					ok = false;
				}
			}
			
			//On vérifie l'intersection avec les lignes
			for(int k=0 ; k<lineObstacles.size() ; k++)
			{
				if(!Geometry.intersects(new Segment(node.getPosition(), nodes.get(i).getPosition()), lineObstacles.get(k)))
				{
					ok = false;
				}
			}
			
			//On vérifie l'intersection
			for(int k=0 ; k<rectangularObstacles.size() ; k++)
			{
				ArrayList<Segment> segments = rectangularObstacles.get(k).getSegments();
				for(int l=0 ; l<segments.size() ; l++)
				{
					if(!Geometry.intersects(new Segment(node.getPosition(), nodes.get(i).getPosition()), segments.get(l)))
					{
						ok = false;
					}
				}
			}
			
			if(ok)
			{
				links.add(new Link(node, nodes.get(i)));
			}
		}
		nodes.add(node);
	}
	
	/**
	 * Renvoie le noeud numero i
	 * @param i le numero du noeud
	 */
	public Node getNode(int i)
	{
		return this.nodes.get(i);
	}
	
	/**
	 * Renvoie le lien numero i
	 * @param i numero du lien
	 */
	public Link getLink(int i)
	{
		return this.links.get(i);
	}
	
	/**
	 * Renvoie les nodes adjacents à tel node
	 * @param node le node a tester
	 */
	public ArrayList<Node> getRelatedNodes(Node node)
	{
		//TODO Optimiser cette méthode pour réduire le temps de calcul
		ArrayList<Node> related = new ArrayList<Node>();
		
		for(int i = 0 ; i < links.size() ; i++)
		{
			if(links.get(i).isRelated(node))
				related.add(links.get(i).getTheOtherNode(node));
		}
		
		return related;
		
	}
	
	/**
	 * Supprime tous les liens dans l'optique de les reconstruire
	 */
	public void clearLinks()
	{
		links.clear();
	}
	
	/**
	 * Supprime un noeud du graphe
	 */
	public void removeNode(Node node)
	{
		nodes.remove(node);
	}
	
	/**
	 * Renvoie si le graphe est vide ou non (des noeuds mais pas de lien ou l'inverse est considere vide)
	 */
	public boolean isEmpty()
	{
		return (nodes.isEmpty() || links.isEmpty());
	}
}