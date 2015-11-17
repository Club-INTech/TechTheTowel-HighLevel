package pathDingDing;

import container.Service;
import exceptions.PathNotFoundException;
import exceptions.PointInObstacleException;
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
 * @author julian
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
	 * @return Liste de noeuds à parcourir ; exception si échec
	 */
	public ArrayList<Node> computePath(Vec2 start, Vec2 end) throws PointInObstacleException, PathNotFoundException
	{
		
		//On vide les listes de nodes pour un nouveau calcul
		this.initialise();
		
		// Verifie si le graphe n'a pas ete initialise vide (ALERTE AU GOGOLE!!)
		if(graph.isEmpty())
		{
			log.critical("GRAPHE DE PathDingDing VIDE !!");
			return new ArrayList<Node>();
		}
		
		// Si on demande un calcul trivial (ALERTE AU GOGOLE!!)
		if(end == start)
		{
			log.critical("Appel pathDingDing avec arrivée=départ !");
			return new ArrayList<Node>();
		}
		
		// On ajoute les noeuds de départ et d'arrivée au graphe (ignorés s'ils existent déjà)
		ArrayList<Node> temp = addStartAndEnd(start, end);
		Node startNode = temp.get(0);
		Node endNode = temp.get(1);
		temp.clear();
		
		//===========================================
		// DEBUT DE L'ALGORITHME A* - INITIALISATION
		//===========================================

		//On calcule l'heuristique de chacun des noeuds
		graph.computeAllHeuristic(endNode);
		
		// On ajoute le noeud de départ à la liste des nodes fermés
		this.closedNodes.add(startNode);
		
		// Test d'isolation du point d'arrivée
		ArrayList<Node> related = this.graph.getRelatedNodes(endNode);
		if(related.isEmpty())
		{
			log.critical("PDD : noeud d'arrivée isolé (obstacle ?)");
			throw new PointInObstacleException(endNode, graph.getObstacleManager());
		}
		
		// D'abord, on ajoute les noeuds adjacents au depart dans la liste ouverte
		related = this.graph.getRelatedNodes(startNode);
		
		// Idem test d'isolation du départ
		// TODO Ajuster pour sortir de l'obstacle 
		if(related.isEmpty())
		{
			log.critical("PDD : noeud de départ isolé");
			
			throw new PointInObstacleException(startNode, graph.getObstacleManager());
		}
		
		for(int i=0 ; i < related.size() ; i++)
		{
			openNodes.add(related.get(i));
			
			// Cette ligne calcule le coût de déplacement et le met dans l'objet ; l'offset est à 0 car on débute le chemin
			// Ce que j'appelle l'offset c'est le coût du déplacement déjà effectué qui s'y ajoute
			double zero =0;
			openNodes.get(i).setMovementCost(openNodes.get(i).computeMovementCost(startNode, zero));
			
			openNodes.get(i).setParent(startNode);
		}
		
		// On vérifie que l'on est pas dans un cas de bloquage :
		// On etudie un seul point, et il n'y a rien autour.
		if(openNodes.isEmpty())
			return new ArrayList<Node>();
		
		// On classe ces noeuds par coût croissant grâce au service Collections et la méthode compareTo() dont hérite Node
		Collections.sort(openNodes);
		
		// On ajoute le meilleur noeud (en premier dans openNodes) dans la liste fermée
		closedNodes.add(openNodes.get(0));
		openNodes.remove(0);
		
		//====================================================================
		// Boucle principale - Recherche de l'arrivée en parcourant le graphe
		//====================================================================
		
		while(!this.closedNodes.contains(endNode)) //Tant que le noeud de fin n'est pas dans la liste fermée, on continue
		{
			//On enregistre le dernier noeud fermé dans une variable (ça rends le code plus lisible)
			Node lastClosedNode = closedNodes.get(closedNodes.size()-1);
			
			//On prend les noeuds proches du dernier noeud fermé
			related = this.graph.getRelatedNodes(lastClosedNode);
			
			//On vérifie si un de ces noeuds n'existe pas déjà dans la liste des noeuds ouverts (pas de doublons)
			// ou s'il est dans la liste des noeuds fermés
			for(int i=0 ; i < related.size() ; i++)
			{
				if(openNodes.contains(related.get(i)))
				{
					Node replicate = related.get(i);
					while(related.remove(replicate))
						i--;
					Node newParent = lastClosedNode;
					
					//Si il existe, on recalcule le coût de déplacement (l'heuristique ne changeant pas)
					//s'il est inférieur on change le noeud avec le nouveau coût, sinon on l'ignore
					double newCost = replicate.computeMovementCost(newParent, newParent.getMovementCost());
					if(newCost < replicate.getMovementCost())
					{
						replicate.setMovementCost(newCost);
						
						//Un fois modifié, on le reclasse dans la liste afin de la garder ordonnée
						//Ceci est fait en le supprimant et en l'ajoutant avant le premier noeud
						//ayant un coût plus grand que lui-même (la liste est triée)
						openNodes.remove(replicate);
						int compteur = 0;
						while(replicate.getCost() >  openNodes.get(compteur).getCost())
						{
							compteur++;
						}
						replicate.setParent(newParent);
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
				while(compteur < openNodes.size() && (related.get(i).getCost() >  openNodes.get(compteur).getCost()))
				{
					compteur++;
				}
				related.get(i).setParent(lastClosedNode);
				related.get(i).setMovementCost(related.get(i).computeMovementCost(lastClosedNode, lastClosedNode.getMovementCost()));
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
		
		//Petite vérification
		if(result.isEmpty())
		{
			log.critical("erreur : pathDingDing sans résultat");
		}

        //==================================
        // On cherche à optimiser le chemin
        //==================================
        for(int i=0 ; i<(result.size()-2) ; i++)
        {
            // Si le noeud i et i+2 sont reliables, on les relie et on supprime le i+1
            if(!graph.isObstructed(result.get(i), result.get(i+2)))
            {
                result.remove(i+1);
                i--;
            }
        }


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
			this.openNodes = new ArrayList<Node>();
			this.closedNodes = new ArrayList<Node>();
		}
        if(graph.getObstacleManager().hasBeenModified)
        {
            graph.setAllLinksOptimised();
        }
	}
	
	/**
	 * Ajoute le départ et l'arrivée au graphe, crée les liens et les renvoie
	 * @param start la position du noeud de départ
	 * @param end la postion du noeud d'arrivée
	 */
	public ArrayList<Node> addStartAndEnd(Vec2 start, Vec2 end)
	{
		Node endNode = new Node(end);
		Node startNode = new Node(start, endNode);
		
		graph.addNode(startNode);
		graph.addNode(endNode);
		
		ArrayList<Node> array = new ArrayList<Node>();
		array.add(startNode);
		array.add(endNode);
		return array;
	}


    /**
     * Déplace un obstacle à la postion donnée, effectue tous les changements necessaires, autant sur le graphe
     * de PathDingDing que sur la table.
     * @param obs l'obstacle en question
     * @param newPos la nouvelle position
     */
    public void moveObstacle(ObstacleCircular obs, Vec2 newPos)
    {
        // Pour informer qu'on a changé la table
        table.getObstacleManager().hasBeenModified = true;

        // On supprime les noeuds du graphe désormais inutiles
        graph.removeNode(new Vec2((obs.getPosition().x - obs.getRadius()),(obs.getPosition().y)));
        graph.removeNode(new Vec2((obs.getPosition().x + obs.getRadius()),(obs.getPosition().y)));
        graph.removeNode(new Vec2((obs.getPosition().x),(obs.getPosition().y + obs.getRadius())));
        graph.removeNode(new Vec2((obs.getPosition().x),(obs.getPosition().y - obs.getRadius())));

        //on ajoute les nouveaux noeuds
        graph.addNode(new Node(new Vec2((newPos.x + obs.getRadius()),(newPos.y))));
        graph.addNode(new Node(new Vec2((newPos.x - obs.getRadius()),(newPos.y))));
        graph.addNode(new Node(new Vec2((newPos.x),(newPos.y + obs.getRadius()))));
        graph.addNode(new Node(new Vec2((newPos.x),(newPos.y - obs.getRadius()))));

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
        // Pour informer qu'on a changé la table
        table.getObstacleManager().hasBeenModified = true;

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
        graph.addNode(new Node(new Vec2((obs.getPosition().x - obs.getSizeX()),(obs.getPosition().y + obs.getSizeY()))));
        graph.addNode(new Node(new Vec2((obs.getPosition().x - obs.getSizeX()),(obs.getPosition().y - obs.getSizeY()))));
        graph.addNode(new Node(new Vec2((obs.getPosition().x + obs.getSizeX()),(obs.getPosition().y + obs.getSizeY()))));
        graph.addNode(new Node(new Vec2((obs.getPosition().x + obs.getSizeX()),(obs.getPosition().y - obs.getSizeY()))));

    }



    @Override
	public void updateConfig() 
	{
		// TODO update the config
	}
}




