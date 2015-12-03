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
     * TODO L'améliorer
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
		//TODO Améliorer le graphe
		// On ajoute tous les nodes et on les relie ensemble en verifiant si on "intersect()" pas un obstacle
		nodes.add(new Node(new Vec2(-1100, 1400)));
		nodes.add(new Node(new Vec2(1100, 1400)));
		nodes.add(new Node(new Vec2(0, 400)));
		nodes.add(new Node(new Vec2(-750, 600)));
		nodes.add(new Node(new Vec2(750, 600)));
		nodes.add(new Node(new Vec2(-1150, 750)));
		nodes.add(new Node(new Vec2(1150, 750)));

		/*
		nodes.add(new Node(new Vec2(-1100, 1400)));
		nodes.add(new Node(new Vec2(1100, 1400)));
		nodes.add(new Node(new Vec2(0, 400)));
		nodes.add(new Node(new Vec2(-750, 600)));
		nodes.add(new Node(new Vec2(750, 600)));
		nodes.add(new Node(new Vec2(-1150, 750)));
		nodes.add(new Node(new Vec2(1150, 750)));*/


        addObstacleNodes();
		setAllLinks();
		//setAllLinksOptimised();
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
	 * @return -1 si inexistant , la position dans nodes sinon
     */
    public int alreadyContains(Node node)
    {
        for(int i=0 ; i<nodes.size() ; i++)
        {
            if(nodes.get(i).getPosition().x == node.getPosition().x && nodes.get(i).getPosition().y == node.getPosition().y)
            {
                return i;
            }
        }
        return -1;
    }

	/**
	 * Vérifie si le Vec2 indiqué n'est pas déjà dans la liste des noeuds
	 * @param point le lieu à vérifier
	 * @return -1 si inexistant , la position dans nodes sinon
	 */
	public int alreadyContains(Vec2 point)
	{
		for(int i=0 ; i<nodes.size() ; i++)
		{
			if(nodes.get(i).getPosition().x == point.x && nodes.get(i).getPosition().y == point.y)
			{
				return i;
			}
		}
		return -1;
	}

	/**
	 * Relie tous les noeuds ensemble en vérifiant s'il n'y a pas d'intersection avec un obstacle
	 */
	public synchronized void setAllLinks()
	{
		//On vide la liste des noeuds pour la reconstruire
		links.clear();
		
		for(int i=0 ; i < nodes.size() ; i++)
		{
			for(int j=0 ; j < nodes.size() ; j++)
			{
				if(j>i)
				{
					if(!isObstructed(nodes.get(i), nodes.get(j)) && (linkAlreadyExists(nodes.get(j), nodes.get(i))==-1))
					{
						links.add(new Link(nodes.get(j), nodes.get(i)));
					}
				}
			}
		}
	}

    /**
     * Créé les liens possibles entre le noeud donné et tous les autres noeuds du graphe
     * @param node le noeud
     */
    private synchronized void setLinks(Node node)
    {
        for(int i=0 ; i<nodes.size() ; i++)
        {
            if(!nodes.get(i).equals(node) && !isObstructed(nodes.get(i), node) && (linkAlreadyExists(nodes.get(i), node)==-1))
            {
                links.add(new Link(node, nodes.get(i)));
            }
        }
    }

    /**
     * Revoie la position dans links si un lien existe déjà entre ces noeuds
	 * Sinon renvoie -1
     */
    private int linkAlreadyExists(Node node1, Node node2)
    {
        for(int i=0 ; i<links.size() ; i++)
        {
            if(links.get(i).equals(node1, node2))
                return i;
        }
        return -1;
    }

    /**
     * Créé le noeud à partir d'un Vec2 et d'un but (calcul heuristique) ; le renvoie
     * @param point la position
     * @param goal le noeud d'arrivé (heuristique)
     * @return le noeud crée ou trouvé si déjà existant
     */
    public synchronized Node addNode(Vec2 point, Node goal)
    {
        Node node = addNode(point);
        node.computeHeuristic(goal);
        return node;
    }

    /**
     * Créé le noeud à partir d'un Vec2 ; le renvoie
     * @param point la position
     * @return le noeud crée ou trouvé si déjà existant
     */
    public synchronized Node addNode(Vec2 point)
    {
        // S'il n'existe pas, on le crée
		int pos = alreadyContains(point);
        if(pos == -1)
        {
            nodes.add(new Node(point));
            setLinks(nodes.get(nodes.size()-1));
            return nodes.get(nodes.size()-1);
        }

        //Sinon on le renvoie
		setLinks(nodes.get(pos)); //On refait ses liens car si l'arrivée et le départ sont visibles, on purra les relier
		return nodes.get(pos);
    }

	/**
	 * Relie tous les noeuds ensemble en vérifiant s'il n'y a pas d'intersection avec un obstacle ; méthode optimisée
	 */
	public synchronized void setAllLinksOptimised()
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
        if(node1.equals(node2))
        {
            return true;
        }

        //On récupère les différents obstacles
        ArrayList<ObstacleRectangular> rectangularObstacles = obstacleManager.getRectangles();

        //On vérifie l'intersection avec les rectangles ; effectué en premier car plus probable
        for(int k=0 ; k<rectangularObstacles.size() ; k++)
        {
            ArrayList<Segment> segments = rectangularObstacles.get(k).getSegments();
            if(rectangularObstacles.get(k).isInObstacle(node2.getPosition()) || rectangularObstacles.get(k).isInObstacle(node1.getPosition()))
            {
                return true;
            }
            for(int l=0 ; l<segments.size() ; l++)
            {
                if(Geometry.intersects(new Segment(node1.getPosition(), node2.getPosition()), segments.get(l)))
                {
                    return true;
                }
            }
        }

        ArrayList<ObstacleCircular> circleObstacles = obstacleManager.getFixedObstacles();

        //On vérifie l'intersection avec les cercles
        for(int k=0 ; k<circleObstacles.size() ; k++)
        {
            if(Geometry.intersects(new Segment(node1.getPosition(), node2.getPosition()), circleObstacles.get(k).toCircle()))
            {
                return true;
            }
        }

        ArrayList<Segment> lineObstacles = obstacleManager.getLines();

        //On vérifie l'intersection avec les lignes
        for(int k=0 ; k<lineObstacles.size() ; k++)
        {
            if(Geometry.intersects(new Segment(node1.getPosition(), node2.getPosition()), lineObstacles.get(k)))
            {
                return true;
            }
        }

        return false;
    }

	/**
	 * Ajoute les noeuds aux coins des obstacles ; permet d'optimiser le PathDingDing
     * On ajoute +1 ou -1 aux composantes pour être bien hors de l'obstacle
	 */
	public synchronized void addObstacleNodes()
	{
		ArrayList<ObstacleRectangular> rect = obstacleManager.getRectangles();
		ArrayList<ObstacleCircular> cir = obstacleManager.getFixedObstacles();
        Vec2 pos;
		/**
		 * Les noeuds sont placés aux coins des rectangles
		 */
		for(int i = 0 ; i < rect.size() ; i++)
        {
            ObstacleRectangular r = rect.get(i);

            pos = new Vec2(r.getPosition().x + (r.getSizeX()/2) +1, (r.getPosition().y - r.getSizeY()/2) -1);
            if(!isInObstacle(pos) && (alreadyContains(pos)==-1))
                nodes.add(new Node(pos));

            pos = new Vec2(r.getPosition().x + (r.getSizeX()/2) +1, (r.getPosition().y + r.getSizeY()/2) +1);
            if(!isInObstacle(pos) && (alreadyContains(pos)==-1))
                nodes.add(new Node(pos));

            pos = new Vec2(r.getPosition().x - (r.getSizeX()/2) -1, (r.getPosition().y - r.getSizeY()/2) -1);
            if(!isInObstacle(pos) && (alreadyContains(pos)==-1))
                nodes.add(new Node(pos));

            pos = new Vec2(r.getPosition().x - (r.getSizeX()/2) -1, (r.getPosition().y + r.getSizeY()/2) +1);
            if(!isInObstacle(pos) && (alreadyContains(pos)==-1))
                nodes.add(new Node(pos));
		}
		/**
		 * Les noeuds sont placés à (+/-)pi/4 et (+/-)3*pi/4 ; d'où le 0.707 qui provient de sqrt(2)/2 = arccos(pi/4) = arcsin (pi/4)
		 */
        for(int i = 0 ; i < cir.size() ; i++)
        {
            ObstacleCircular c = cir.get(i);

            pos = new Vec2(c.getPosition().x + (int)(c.getRadius()*0.707) +1, c.getPosition().y + (int)(c.getRadius()*0.707) +1);
            if(!isInObstacle(pos) && (alreadyContains(pos)==-1))
                nodes.add(new Node(pos));

            pos = new Vec2(c.getPosition().x + (int)(c.getRadius()*0.707) +1, c.getPosition().y - (int)(c.getRadius()*0.707) -1);
            if(!isInObstacle(pos) && (alreadyContains(pos)==-1))
                nodes.add(new Node(pos));

            pos = new Vec2(c.getPosition().x - (int)(c.getRadius()*0.707) -1, c.getPosition().y + (int)(c.getRadius()*0.707) +1);
            if(!isInObstacle(pos) && (alreadyContains(pos)==-1))
                nodes.add(new Node(pos));

            pos = new Vec2(c.getPosition().x - (int)(c.getRadius()*0.707) -1, c.getPosition().y - (int)(c.getRadius()*0.707) -1);
            if(!isInObstacle(pos) && (alreadyContains(pos)==-1))
                nodes.add(new Node(pos));
        }
	}

	/**
	 * Renvoie les nodes adjacents à tel node
	 * @param node le node a tester
	 */
	public ArrayList<Node> getRelatedNodes(Node node)
	{
		ArrayList<Node> related = new ArrayList<Node>();
		
		for(int i = 0 ; i < links.size() ; i++)
		{
			if(links.get(i).isRelated(node))
				related.add(links.get(i).getTheOtherNode(node));
		}
		
		return related;
		
	}

	/**
	 * Renvoie la liste des liens adjacents au noeud
	 */
	public ArrayList<Link> getRelatedLinks(Node node)
	{
		ArrayList<Link> related = new ArrayList<Link>();

		for(int i = 0 ; i < links.size() ; i++)
		{
			if(links.get(i).isRelated(node))
				related.add(links.get(i));
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

        ArrayList<ObstacleCircular> circularObstacles = obstacleManager.getFixedObstacles();
        for(int i=0 ; i<circularObstacles.size() ; i++)
        {
            if(circularObstacles.get(i).isInObstacle(point))
            {
                return true;
            }
        }

        return false;
    }

	
	/**
	 * Supprime un noeud du graphe
	 */
	public synchronized void removeNode(Vec2 pos)
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