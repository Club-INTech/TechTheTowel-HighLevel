package pathDingDing;

import smartMath.Geometry;
import smartMath.Segment;
import smartMath.Vec2;
import table.Table;
import table.obstacles.Obstacle;
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
	public static final double IGNORE_DISTANCE = 1000000;

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
        addObstacleNodes();
		setAllLinksOptimised();
	}

	public void computeAllHeuristic(Node goal)
	{
		for(int i = 0 ; i< nodes.size() ; i++)
		{
			nodes.get(i).computeHeuristic(goal);
		}
	}

    /**
     * Vérifie si le noeud indiqué n'est pas déjà dans la liste des noeuds
     * @param node le noeud à vérifier
     */
    public boolean alreadyContains(Node node)
    {
        for(int i=0 ; i<nodes.size() ; i++)
        {
            if(nodes.get(i).getPosition().x == node.getPosition().x && nodes.get(i).getPosition().y == node.getPosition().y)
            {
                // on supprime l'ancien et on ajoute le nouveau
                nodes.remove(i);
                nodes.add(i, node);
                return true;
            }
        }
        return false;
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
    if(alreadyContains(node))
    {
        for(int i=0 ; i<nodes.size() ; i++)
        {
            if(!isObstructed(node, nodes.get(i)))
            {
                links.add(new Link(node, nodes.get(i)));
            }
        }
        return;
    }
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
		if(alreadyContains(node))
			return;

		for(int i=0 ; i<nodes.size() ; i++)
		{
			// On le prend en compte que les noeuds proches
			if(Segment.squaredLength(nodes.get(i).getPosition(), node.getPosition()) <= IGNORE_DISTANCE)
            {
				if (!isObstructed(node, nodes.get(i)))
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
     * Remets les parents des noeuds à null, sinon un conflit peut apparaître
     */
    public void voidAllParents()
    {
        for(int i=0 ; i<nodes.size() ; i++)
        {
            nodes.get(i).setParent(null);
        }
    }

    /**
     * Renvoie si un obstacle est sur le chemin entre les deux noeuds
     * @param node1 noeud 1
     * @param node2 noeud 2
     */
    public boolean isObstructed(Node node1, Node node2)
    {
        // On évite de créer des liens sur le même noeud
        if(node1 == node2)
        {
            return true;
        }

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
			if(rectangularObstacles.get(k).isInObstacle(node2.getPosition()) || rectangularObstacles.get(k).isInObstacle(node1.getPosition()))
            {
                ok = true;
                break;
            }
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
	 * Ajoute les noeuds aux coins des obstacles ; permet d'optimiser le PathDingDing
     * On ajoute +1 ou -1 aux composantes pour être bien hors de l'obstacle
	 */
	public void addObstacleNodes()
	{
		ArrayList<ObstacleRectangular> rect = obstacleManager.getRectangles();
		ArrayList<ObstacleCircular> cir = obstacleManager.getFixedObstacles();
        Vec2 pos;
		for(int i = 0 ; i < rect.size() ; i++)
        {
            ObstacleRectangular r = rect.get(i);

            pos = new Vec2(r.getPosition().x + (r.getSizeX()/2) +1, (r.getPosition().y - r.getSizeY()/2) -1);
            if(!isInObstacle(pos))
                nodes.add(new Node(pos));

            pos = new Vec2(r.getPosition().x + (r.getSizeX()/2) +1, (r.getPosition().y + r.getSizeY()/2) +1);
            if(!isInObstacle(pos))
                nodes.add(new Node(pos));

            pos = new Vec2(r.getPosition().x - (r.getSizeX()/2) -1, (r.getPosition().y - r.getSizeY()/2) -1);
            if(!isInObstacle(pos))
                nodes.add(new Node(pos));

            pos = new Vec2(r.getPosition().x - (r.getSizeX()/2) -1, (r.getPosition().y + r.getSizeY()/2) +1);
            if(!isInObstacle(pos))
                nodes.add(new Node(pos));
		}
        for(int i = 0 ; i < cir.size() ; i++)
        {
            ObstacleCircular c = cir.get(i);

            pos = new Vec2(c.getPosition().x, (c.getPosition().y - c.getRadius()) -1);
            if(!isInObstacle(pos))
                nodes.add(new Node(pos));

            pos = new Vec2(c.getPosition().x, (c.getPosition().y + c.getRadius()) +1);
            if(!isInObstacle(pos))
                nodes.add(new Node(pos));

            pos = new Vec2(c.getPosition().x - (c.getRadius()) -1, c.getPosition().y );
            if(!isInObstacle(pos))
                nodes.add(new Node(pos));

            pos = new Vec2(c.getPosition().x + (c.getRadius()) +1, c.getPosition().y );
            if(!isInObstacle(pos))
                nodes.add(new Node(pos));
        }
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
     * Vérifie si le point est dans un obstacle
     * @param point point en question
     */
    public boolean isInObstacle(Vec2 point)
    {
		// Si le point est en dehors de la table
		if(point.y > (2000 - obstacleManager.mRobotRadius)
			|| point.y < obstacleManager.mRobotRadius
			|| point.x > (1500 - obstacleManager.mRobotRadius)
			|| point.x < (-1500 + obstacleManager.mRobotRadius))
		{
			return true;
		}

        ArrayList<ObstacleRectangular> rectangularObstacles = obstacleManager.getRectangles();
        for(int i=0 ; i<rectangularObstacles.size() ; i++)
        {
            if(rectangularObstacles.get(i).isInObstacle(point))
            {
                return true;
            }
        }
        return false;
    }

	
	/**
	 * Supprime un noeud du graphe
	 */
	public void removeNode(Vec2 pos)
	{
		for(int i =0 ; i<nodes.size() ; i++)
		{
			if(nodes.get(i).getPosition().x == pos.x && nodes.get(i).getPosition().y == pos.y)
            {
                nodes.remove(i);
                break;
            }
		}
	}
	
	/**
	 * Renvoie si le graphe est vide ou non (des noeuds mais pas de lien ou l'inverse est considere vide)
	 */
	public boolean isEmpty()
	{
		return (nodes.isEmpty() || links.isEmpty());
	}
}