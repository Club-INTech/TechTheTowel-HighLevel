package pathDingDing;

import container.Service;
import exceptions.PathNotFoundException;
import exceptions.PointInObstacleException;
import smartMath.Segment;
import smartMath.Vec2;
import table.Table;
import table.obstacles.ObstacleCircular;
import table.obstacles.ObstacleRectangular;
import utils.Log;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Classe de calcul de chemins utilisant l'algorithme A*
 * 
 * Le but de l'algorithme A* est de trouver le chemin le plus court en demarrant d'un
 * node et en parcourant les nodes alentours reliés. Le node suivant est choisi selon 2 critères :
 * 
 *  1- Le coût direct, c'est-a-dire le temps que va prendre le robot pour s'y rendre
 *  2- L'heuristique, cela peut prendre plusieures formes, ici c'est la distance à vol d'oiseau entre
 *     le noeud et l'arrivée, donc un noeud qui fait s'eloigner le robot de la destination finale aura
 *     une heuristique plus elevee qu'un noeud plus proche
 *     
 *  On choisit simplement le node avec la somme des deux la moins elevee. Ce node sera considere comme FERME
 *  
 *  Avant de choisir le node suivant, on parcourt les nodes adjacents et :
 *   - S'il est pratiquable et non ferme, on l'ajoute a la liste des nodes ouverts
 *   - Si il est deja dans la liste des ouverts, on recalcule son coût direct (l'heuristique ne changeant pas)
 *   - Sinon on l'ignore
 *  Le choix du node suivant (avec les critères enonces precedemment) se fait donc sur la liste ouverte
 *  
 *  La liste ouverte est triee par la somme des critères afin d'eviter un parcours complet de la liste
 *  a chaque etape de l'algo (optimisation du temps de calcul)
 *  
 *  A chaque node ferme, on specifie son parent afin de pouvoir remonter le chemin une fois l'arrivee trouvee
 *  
 *  Le code est commente a chaque etape, mais il est preferable de lire cet article pour une meilleure comprehension :
 *  http://www.gamedev.net/page/resources/_/technical/artificial-intelligence/a-pathfinding-for-beginners-r2003
 *  
 * @author discord
 *
 */
public class PathDingDing implements Service
{
	
	//Le graphe a parcourir
	private Graph graph;
	
	//Noeuds ouverts (a vider a chaque calcul de chemin) 
	private ArrayList<Node> openNodes;
	
	//Noeuds fermés (a vider a chaque calcul de chemin)
	private ArrayList<Node> closedNodes;
	
	//Le log
	private Log log;

    //La table
    private Table table;

	/**
	 * TODO Doc
	 */
	private static final int BEAM_LENGTH = 3;
	
	/**
	 * Constructeur du PathDIngDIng
	 * @param table La table de jeu
	 * @param log Le systeme de log qui affiche les erreurs et reussites
	 */
	public PathDingDing(Table table, Log log)
	{
		this.log = log;
		this.table = table;
		this.graph = new Graph(table, log);
		
		this.openNodes = new ArrayList<Node>();
		this.closedNodes = new ArrayList<Node>();
	}
	
	/**
	 * Calcule le chemin à parcourir à l'aide de l'algorithme A*
	 * @param start noeud de départ
	 * @param end noeud d'arrivée
	 * @return Liste de noeuds (Node) à parcourir ; exception si échec
	 */
	public ArrayList<Node> computePath(Vec2 start, Vec2 end) throws PointInObstacleException, PathNotFoundException
	{
		long time = System.currentTimeMillis();


		//On vide les listes de nodes pour un nouveau calcul
		this.initialise();
		
		// Verifie si le graphe n'a pas ete initialise vide (ALERTE AU GOGOLE!!)
		if(graph.isEmpty())
		{
			log.critical("GRAPHE DE PathDingDing VIDE !!");
			return new ArrayList<Node>();
		}
		
		// Si on demande un calcul trivial (ALERTE AU GOGOLE!!)
		if(end.equals(start))
        {
			log.critical("Appel pathDingDing avec arrivée=départ !");
			return new ArrayList<Node>();
		}

        if(!graph.isObstructed(end, start))
        {
            ArrayList<Node> path = new ArrayList<Node>();
            path.add(new Node(start));
            path.add(new Node(end));
            return path;
        }
		/**
		 * On copie les Vec2 afin d'éviter que leur composantes soient changées en dehors du PDD
		 * Si elles sont changés, les liens eux ne bougent pas, d'où la possibilité de se
		 * retrouver avec un lien qui coupe un obstacle
		 */
		Vec2 endCopy = end.makeCopy();
		Vec2 startCopy = start.makeCopy();

		// On ajoute les noeuds de départ et d'arrivée au graphe (ignorés s'ils existent déjà)
		Node endNode = 	graph.addNode(endCopy);
        Node startNode = graph.addNode(startCopy, endNode);

		
		//===========================================
		// DEBUT DE L'ALGORITHME A* - INITIALISATION
		//===========================================

		// On ajoute le noeud de départ à la liste des nodes fermés
		this.closedNodes.add(startNode);

		//Test d'isolation du point d'arrivée
		if(graph.isInObstacle(end))
		{
			log.critical("PDD : noeud d'arrivée isolé (obstacle ?)");
			throw new PointInObstacleException(endNode, graph.getObstacleManager());
		}
		
		// Idem test d'isolation du départ
		
		//======================================================================================================================
		// Sortie d'obstacle - Partie traitant le cas où l'on doit sortir d'un obstacle avant de pouvoir débuter l'A* proprement
		//======================================================================================================================
		
		if(graph.isInObstacle(start))
		{/*
			log.critical("PDD : noeud de départ isolé");
			
			throw new PointInObstacleException(startNode, graph.getObstacleManager());
		 */
			// Regarde quel est l'obstacle qui pose problème dans les différents obstacles possibles, ce qui est un peu lourd puisque Graph le fait déjà en amont
			// Du coup, pourrait-on étendre les capacités de réponse de la méthode isInObstacle de Graph ? Y a-t-il bien plus simple et je suis aveugle ?
			//TODO S'en sortir si le robot est hors de la table (repositionnement à faire)?
			// Dans les différents rectangles
			ArrayList<ObstacleRectangular> rectObs = graph.getObstacleManager().getRectangles();
			for (int i=0; i<rectObs.size(); i++)
			{
				// Si le point de départ coïncide avec un des éléments
				if (rectObs.get(i).isInObstacle(start))
				{
					// On récupère l'élément problématique
					ObstacleRectangular prob = new ObstacleRectangular(rectObs.get(i).getPosition(), rectObs.get(i).getSizeX(), rectObs.get(i).getSizeY());
					
					// On le supprime temporairement pour que le robot puisse en sortir
					graph.getObstacleManager().removeObstacle(prob);
					
					// Relance de l'A* sans l'obstacle problématique
					ArrayList<Node> path = this.computePath(start, end);
					
					// Rajout de l'obstacle en fin de calcul
					graph.getObstacleManager().addObstacle(prob);
					return path;
					
				}
			}
			
			//Dans les différents cercles
			ArrayList<ObstacleCircular> circObs = graph.getObstacleManager().getFixedObstacles();
			for (int i=0; i<circObs.size();i++)
			{
				// Si le point de départ coïncide avec une des éléments
				if (circObs.get(i).isInObstacle(start))
				{
					// On récupère l'élément problématique
					ObstacleCircular prob = new ObstacleCircular(circObs.get(i).getPosition(), circObs.get(i).getRadius());
					
					// On le supprime temporairemnt pour que le robot puisse en sortir
					graph.getObstacleManager().removeObstacle(prob);
					
					// Relance de l'A* sans l'obstacle problématique
					ArrayList<Node> path = this.computePath(start, end);
					
					// Rajout de l'obstacle en fin de calcul
					graph.getObstacleManager().addObstacle(prob);
					return path;
				}
			}
		}

		// D'abord, on ajoute les noeuds adjacents au depart dans la liste ouverte
		ArrayList<Node> related = this.graph.getRelatedNodes(startNode);
		
		for(int i=0 ; i < related.size() ; i++)
		{
			openNodes.add(related.get(i));
			
			// Cette ligne calcule le coût de déplacement et le met dans l'objet ; l'offset est à 0 car on débute le chemin
			// Ce que j'appelle l'offset c'est le coût du déplacement déjà effectué qui s'y ajoute
			openNodes.get(i).setMovementCost(openNodes.get(i).computeMovementCost(startNode, (double)0));
			openNodes.get(i).computeHeuristic(endNode);
			
			openNodes.get(i).setParent(startNode);
		}
		
		// On classe ces noeuds par coût croissant grâce au service Collections et la méthode compareTo() dont hérite Node
		Collections.sort(openNodes);
		
		// On ajoute le meilleur noeud (en premier dans openNodes) dans la liste fermée
		closedNodes.add(openNodes.get(0));
		openNodes.remove(0);

		while(openNodes.size() > BEAM_LENGTH)
			openNodes.remove(openNodes.size()-1);

		log.debug("Temps d'init. A* = "+(System.currentTimeMillis()-time)+" ms");
		//====================================================================
		// Boucle principale - Recherche de l'arrivée en parcourant le graphe
		//====================================================================
		
		while(!this.closedNodes.contains(endNode)) //Tant que le noeud de fin n'est pas dans la liste fermée, on continue
		{
			//On enregistre le dernier noeud fermé dans une variable (ça rends le code plus lisible)
			Node lastClosedNode = closedNodes.get(closedNodes.size()-1);
			
			//On prend les noeuds proches du dernier noeud fermé
			related = this.graph.getRelatedNodes(lastClosedNode);

            if(related.isEmpty())
            {
                log.critical("pas de lien pour "+lastClosedNode.getPosition());
            }
			
			//On vérifie si un de ces noeuds n'existe pas déjà dans la liste des noeuds ouverts (pas de doublons)
			// ou s'il est dans la liste des noeuds fermés
			for(int i=0 ; i < related.size() ; i++)
			{
				if(openNodes.contains(related.get(i)))
				{
					Node replicate = related.get(i);
					while(related.remove(replicate))
						i--;

					
					//Si il existe, on recalcule le coût de déplacement (l'heuristique ne changeant pas)
					//s'il est inférieur on change le noeud avec le nouveau coût, sinon on l'ignore
					double newCost = replicate.computeMovementCost(lastClosedNode, lastClosedNode.getMovementCost());
					if(newCost < replicate.getMovementCost())
					{
						replicate.setMovementCost(newCost);
						
						//Un fois modifié, on le reclasse dans la liste afin de la garder ordonnée
						//Ceci est fait en le supprimant et en l'ajoutant avant le premier noeud
						//ayant un coût plus grand que lui-même (la liste est triée)
						openNodes.remove(replicate);
						int compteur = 0;
						while(compteur < openNodes.size() && replicate.getCost() >  openNodes.get(compteur).getCost())
						{
							compteur++;
						}
						replicate.setParent(lastClosedNode);
						openNodes.add(compteur, replicate);
					}
				}
				else if(closedNodes.contains(related.get(i)))
				{
					Node replicate = related.get(i);
					while(related.remove(replicate))
						i--;
				}
			}
			
			
			//On place les noeuds restants dans la liste des noeuds ouverts, de manière à la garder triée
			for(int i=0 ; i < related.size() ; i++)
			{
				int compteur = 0;
                related.get(i).setParent(lastClosedNode);
				related.get(i).setMovementCost(related.get(i).computeMovementCost(lastClosedNode, lastClosedNode.getMovementCost()));
				related.get(i).computeHeuristic(endNode);
				while(compteur < openNodes.size() && (related.get(i).getCost() >  openNodes.get(compteur).getCost()))
				{
					compteur++;
				}
				openNodes.add(compteur, related.get(i));
			}
			
			//On ajoute le meilleur noeud dans la liste fermée en le supprimant de openNodes
			closedNodes.add(openNodes.get(0));
			openNodes.remove(0);
			
			//On vérifie que la liste des noeuds ouverts n'est pas vide
			//Si c'est le cas, il n'y a pas de chemin existant, ce noeud est inaccessible
			if(openNodes.isEmpty())
			{
				log.critical("pathDingDing : Le noeud demandé ("+endNode.getPosition().toString()+") est inacessible.");
				throw new PathNotFoundException();
			}

			while(openNodes.size() > BEAM_LENGTH)
				openNodes.remove(openNodes.size()-1);
			
			//ET ON RECOMMENCE !!!
		} 
		
		//==============================================
		// Recomposition du chemin - Arrivée --> Départ
		//==============================================
		
		//result est le chemin final à renvoyer ; on y met l'arrivée
		ArrayList<Node> result = new ArrayList<Node>();
		
		//On remonte le chemin en ajoutant le parent du dernier noeud ajouté à result
		//Il s'arrête quand il rencontre le noeud de départ
		Node currentNode = endNode;
		while(currentNode != startNode)
		{
			result.add(0, currentNode);
			currentNode = currentNode.getParent();
		}
        result.add(0, startNode);
		
		//Petite vérification
		if(result.isEmpty())
		{
			log.critical("erreur : pathDingDing sans résultat");
		}

		//==================================
		// On cherche à optimiser le chemin
		//==================================
		/*
		for(int i=0 ; i<(result.size()-2) ; i++)
		{
			for(int j=i+2; j<(result.size()) ; j++)
			{
				// Si le noeud i et j sont reliables, on les relie et on supprime les entres-deux
				if (!graph.isObstructed(result.get(i), result.get(j))) {
					for(int k=i+1 ; k<j ; k++)
					{
						result.remove(k);
						k--;
						j--;
					}
				}
			}
		}*/


        // ET C'EST FUCKING TERMINE !!!!
		return result;
	}
	
	/**
	 * Vide les listes ouvertes et fermees pour lancer un nouveau calcul
	 * et reconstruit le graphe au cas de nouveau obstacles sont apparus
	 */
	public void initialise()
	{
		if(!openNodes.isEmpty() || !closedNodes.isEmpty())
		{
			this.openNodes.clear();
			this.closedNodes.clear();
		}
		//TODO empêcher un calcul inutile du graphe
        //graph.setAllLinksOptimised();
	}



    /**
     * Déplace un obstacle à la postion donnée, effectue tous les changements necessaires, autant sur le graphe
     * de PathDingDing que sur la table.
     * @param obs l'obstacle en question
     * @param newPos la nouvelle position
     */
    public void moveObstacle(ObstacleCircular obs, Vec2 newPos)
    {


        // On supprime les noeuds du graphe désormais inutiles
        graph.removeNode(new Vec2((obs.getPosition().x - obs.getRadius()),(obs.getPosition().y)));
        graph.removeNode(new Vec2((obs.getPosition().x + obs.getRadius()),(obs.getPosition().y)));
        graph.removeNode(new Vec2((obs.getPosition().x),(obs.getPosition().y + obs.getRadius())));
        graph.removeNode(new Vec2((obs.getPosition().x),(obs.getPosition().y - obs.getRadius())));

        //on ajoute les nouveaux noeuds
        graph.addNode(new Vec2((newPos.x + obs.getRadius()),(newPos.y)));
        graph.addNode(new Vec2((newPos.x - obs.getRadius()),(newPos.y)));
        graph.addNode(new Vec2((newPos.x),(newPos.y + obs.getRadius())));
        graph.addNode(new Vec2((newPos.x),(newPos.y - obs.getRadius())));

        // On change les données du Vec2 au lieu de l'écraser par celui en argument
        // cela permet de changer la position en même temps dans la table
        obs.getPosition().x = newPos.x;
        obs.getPosition().y = newPos.y;

    }

    /**
     * Déplace un obstacle à la postion donnée, effectue tous les changements necessaires, autant sur le graphe
     * de PathDingDing que sur la table.
     * @param obs l'obstacle en question
     * @param newPos la nouvelle position
     */
    public void moveObstacle(ObstacleRectangular obs, Vec2 newPos)
    {


        // On supprime les noeuds du graphe désormais inutiles
        graph.removeNode(new Vec2((obs.getPosition().x - obs.getSizeX()),(obs.getPosition().y + obs.getSizeY())));
        graph.removeNode(new Vec2((obs.getPosition().x - obs.getSizeX()),(obs.getPosition().y - obs.getSizeY())));
        graph.removeNode(new Vec2((obs.getPosition().x + obs.getSizeX()),(obs.getPosition().y + obs.getSizeY())));
        graph.removeNode(new Vec2((obs.getPosition().x + obs.getSizeX()),(obs.getPosition().y - obs.getSizeY())));

        // On change les données du Vec2 au lieu de l'écraser par celui en argument
        // cela permet de changer la position en même temps dans la table
        obs.getPosition().x = newPos.x;
        obs.getPosition().y = newPos.y;

        //on ajoute les nouveaux noeuds
        graph.addNode(new Vec2((obs.getPosition().x - obs.getSizeX()),(obs.getPosition().y + obs.getSizeY())));
        graph.addNode(new Vec2((obs.getPosition().x - obs.getSizeX()),(obs.getPosition().y - obs.getSizeY())));
        graph.addNode(new Vec2((obs.getPosition().x + obs.getSizeX()),(obs.getPosition().y + obs.getSizeY())));
        graph.addNode(new Vec2((obs.getPosition().x + obs.getSizeX()),(obs.getPosition().y - obs.getSizeY())));

    }

    /**
     * Calcule le chemin à parcourir à l'aide de l'algorithme A*
     * @param start noeud de départ
     * @param end noeud d'arrivée
     * @return Liste de noeuds à parcourir (Vec2) ; exception si échec
     */
    public ArrayList<Vec2> computePathVec2(Vec2 start, Vec2 end) throws PointInObstacleException, PathNotFoundException {
        ArrayList<Node> path = computePath(start, end);
        ArrayList<Vec2> res = new ArrayList<Vec2>();

        for(int i=0; i<path.size() ; i++)
        {
            res.add(path.get(i).getPosition());
        }
        return res;
    }

	/**
	 * Renvoie le graphe
     */
	public Graph getGraph()
	{
		return graph;
	}

    @Override
	public void updateConfig() 
	{
		// TODO update the config
	}
}




