package pathDingDing;

import smartMath.Geometry;
import smartMath.Segment;
import smartMath.Vec2;
import table.Table;
import table.obstacles.ObstacleCircular;
import table.obstacles.ObstacleManager;
import table.obstacles.ObstacleRectangular;
import utils.Log;

import java.util.ArrayList;

/**
 * Graphe de la table, utilise par le pathDingDing pour creer un chemin sur la table et le parcourir
 * @author julian
 *
 */
public class Graph
{
	/**
	 * Définit la distance maximale entre deux noeuds pour tenter le calcul d'un lien
	 * Permet d'éviter un calcul en O(n²)
     * Plus cette valeur est élevée, plus le calcul sera lent, mais plus le chemin sera optimisé
     * Une valeur trop petite peut rendre un noeud isolé s'il est trop éloigné des autres
     * Assimilable au clipping dans les moteurs 3D
	 */
	public static double IGNORE_DISTANCE = 5000;

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
		
		nodes.add(new Node(new Vec2(-1350, 1000)));
		nodes.add(new Node(new Vec2(-1050, 1750)));
		nodes.add(new Node(new Vec2(-1050, 1500)));
		nodes.add(new Node(new Vec2(-1050, 1000)));
		nodes.add(new Node(new Vec2(-1050, 400)));
		nodes.add(new Node(new Vec2(-800, 1500)));
		nodes.add(new Node(new Vec2(-800, 1000)));
		nodes.add(new Node(new Vec2(-800, 400)));
		nodes.add(new Node(new Vec2(-300, 1000)));
		
		int k = nodes.size();
		
		for(int i=0 ; i<k ; i++)
		{
			nodes.add(new Node(new Vec2((-1*nodes.get(i).getPosition().x),nodes.get(i).getPosition().y)));
		}
		
		nodes.add(new Node(new Vec2(0, 1500)));
		nodes.add(new Node(new Vec2(0, 200)));
		
		//========================
		// Fin du graphe à la con
		//========================
		setAllLinksOptimised();
	}
	
	/**
	 * Relie tous les noeuds ensemble en vérifiant s'il n'y a pas d'intersection avec un obstacle
	 */
	public void setAllLinks()
	{
		//On vide la liste des noeuds pour la reconstruire
		links.clear();
		
		for(int i=0 ; i < nodes.size() ; i++)
		{
			for(int j=0 ; j < nodes.size() ; j++)
			{
				if(j>i)
				{
					if(!isObstructed(nodes.get(i), nodes.get(j)))
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
		//S'il existe déjà, on sort de la fonction
		if(nodes.contains(node))
			return;
				
		for(int i=0 ; i<nodes.size() ; i++)
		{
			if(!isObstructed(node, nodes.get(i)))
			{
				links.add(new Link(node, nodes.get(i)));
			}
		}
		nodes.add(node);
	}

	/**
	 * Relie tous les noeuds ensemble en vérifiant s'il n'y a pas d'intersection avec un obstacle ; méthode optimisée
	 */
	public void setAllLinksOptimised()
	{
		//On vide la liste des noeuds pour la reconstruire
		links.clear();

		for(int i=0 ; i < nodes.size() ; i++)
		{
			for(int j=0 ; j < nodes.size() ; j++)
			{
				// On ne prend en compte que les noeuds proches
				if(j>i && Segment.squaredLength(nodes.get(i).getPosition(), nodes.get(j).getPosition()) <= IGNORE_DISTANCE)
				{
					if(!isObstructed(nodes.get(i), nodes.get(j)))
					{
						links.add(new Link(nodes.get(i), nodes.get(j)));
					}
				}
			}
		}
	}

	/**
	 * Ajoute un noeud et crée tous les liens qui le relient ; méthode optimisée
	 * @param node le noeud
	 */
	public void addNodeOptimised(Node node)
	{
		//S'il existe déjà, on sort de la fonction
		if(nodes.contains(node))
			return;

		for(int i=0 ; i<nodes.size() ; i++)
		{
			// On le prend en compte que les noeuds proches
			if(Segment.squaredLength(nodes.get(i).getPosition(), node.getPosition()) <= IGNORE_DISTANCE)
            {
				if (isObstructed(node, nodes.get(i)))
                {
					links.add(new Link(node, nodes.get(i)));
				}
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
	 * Revoie le gestionnaire d'obstacles, utile pour PointInObstacleException
	 */
	public ObstacleManager getObstacleManager()
	{
		return obstacleManager;
	}

    /**
     * Renvoie si un obstacle est sur le chemin entre les deux noeuds
     * @param node1 noeud 1
     * @param node2 noeud 2
     */
    public boolean isObstructed(Node node1, Node node2)
    {
        boolean ok = false;
        //On récupère les différents obstacles
        ArrayList<ObstacleRectangular> rectangularObstacles = obstacleManager.getRectangles();
        ArrayList<ObstacleCircular> circleObstacles = obstacleManager.getFixedObstacles();
        ArrayList<Segment> lineObstacles = obstacleManager.getLines();

        //On vérifie l'intersection avec les cercles
        for(int k=0 ; k<circleObstacles.size() ; k++)
        {
            if(Geometry.intersects(new Segment(node1.getPosition(), node2.getPosition()), circleObstacles.get(k).toCircle()))
            {
                ok = true;
            }
            if(ok)
                break;
        }
        if(ok)
            return ok;

        //On vérifie l'intersection avec les lignes
        for(int k=0 ; k<lineObstacles.size() ; k++)
        {
            if(Geometry.intersects(new Segment(node1.getPosition(), node2.getPosition()), lineObstacles.get(k)))
            {
                ok = true;
            }
            if(ok)
                break;
        }
        if(ok)
            return ok;

        //On vérifie l'intersection
        for(int k=0 ; k<rectangularObstacles.size() ; k++)
        {
            ArrayList<Segment> segments = rectangularObstacles.get(k).getSegments();
            for(int l=0 ; l<segments.size() ; l++)
            {
                if(Geometry.intersects(new Segment(node1.getPosition(), node2.getPosition()), segments.get(l)))
                {
                    ok = true;
                }
                if(ok)
                    break;
            }
            if(ok)
                break;
        }

        return ok;
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