package pathDingDing;

import smartMath.Vec2;

/**
 *  Noeud d'un graphe de pathDingDing
 * @author julian
 *
 */
public class Node implements Comparable<Node>
{
	
	/**
	 * Position du noeud (x,y)
	 */
	private Vec2 position;
	
	/**
	 * Valeur heuristique (distance eulerienne jusqu'au point d'arrivee), necessaire pour le A*
	 */
	private double heuristic;
	
	/**
	 * Cout en terme de temps pour s'y deplacer ; ATTTENTION c'est relatif au contexte de l'instanciation du noeud
	 * Vaut 0 par defaut, donc a changer par la methode setMovementCost()
	 */
	private double movementCost = 0;
	/**
	 * Noeud parent de celui ci, utile dans l'algorithme pour remonter le chemin de l'arrivee vers le depart
	 */
	private Node parent = null;
	
	/**
	 * Cree un noeud a partir de sa position et du point d'arrivee (pour calcul heuristique)
	 * @param pos position
	 * @param goal Point d'arrivee du chemin
	 */
	public Node(Vec2 pos, Node goal)
	{
		this.position = pos;
		computeHeuristic(goal);
	}
	
	/**
	 * Cree un noeud sans arrivée, donc sans heuristique
	 * Peut etre utile pour faire un Dijkstra simple
	 * /!\ NE PAS UTILISER DANS UN VRAI CALCUL DE CHEMIN A*
	 * @param pos position
	 */
	public Node(Vec2 pos)
	{
		this.position = pos;
		// Vu que l'on a pas d'arrivee fixee, on met l'heuristique à -1
		this.heuristic=-1;
	}
	
	
	/**
	 * Renvoie la postion du noeud
	 */
	public Vec2 getPosition()
	{
		return this.position;
	}
	
	/**
	 * Calcule la valeur heuristique du noeud ; l'arrivee ayant une heuristique nulle
	 * @param goal point d'arrivee du chemin recherché
	 */
	private void computeHeuristic(Node goal)
	{
		//Si ce noeud est l'arrivee, on met son heuristique à 0 (car il parait que la distance d'un point a lui même est nulle...)
		if(this == goal)
		{
			this.heuristic = 0;
		}
		else
		{
			// Calcul de la distance a vol d'oiseau (methode basique mais suffisante dans notre cas) 
			this.heuristic = Math.sqrt(Math.pow(Math.abs(this.position.x - goal.getPosition().x), 2) + Math.pow(Math.abs(this.position.y - goal.getPosition().y), 2));
		}
	}
	
	/**
	 * Donne le noeud parent a celui ci ; utile dans l'algorithme A*
	 * @param p le noeud parent
	 */
	public void setParent(Node p)
	{
		this.parent = p;
	}
	
	/**
	 * Revoie le noeud parent de ce noeud ; utilise pour remonter le chemin a la fin de l'algo
	 * Renvoie null si parent non donne auparavent
	 */
	public Node getParent()
	{
		return this.parent;
	}
	
	/**
	 * Calcule le cout en deplacement du noeud precedent specifie a celui-ci + un offset (déplacements précédents)
	 * LE CHEMIN DOIT ETRE SANS OBSTACLE ! VERIFIEZ AVANT DE L'APPELER
	 * Cette méthode n'enregistre pas le résultat dans l'objet, pour cela il faut appeler setMovementCost()
	 * @param start noeud precedent
	 * @return le coût de déplacement
	 */
	public double computeMovementCost(Node start, double offset)
	{
		//TODO calcul de cout
		return 0;
	}
	
	/**
	 * Enregistre la valeur donnée comme coût de déplacement
	 * Utilisez setMovementCost(computeMovementCost()) pour le calculer et définir
	 * @param cost le coût
	 */
	public void setMovementCost(double cost)
	{
		this.movementCost = cost;
	}
	
	/**
	 * Revoie le cout en deplacement du noeud
	 */
	public double getMovementCost()
	{
		return this.movementCost;
	}
	
	/**
	 * Revoie le coût total du noeud (heuristique + mouvement)
	 */
	public double getCost()
	{
		return (this.movementCost + this.heuristic);
	}

	
	
	/**
	 * Permet de comparer deux nodes (donc de les classer), ceci est fait par leur coût
	 * @param otherNode l'autre node à comparer avec celui-ci
	 * @return 0 si = ; 1 si > ; -1 si <
	 */
	@Override
	public int compareTo(Node otherNode) 
	{
		double thisCost = this.getCost();
		double otherCost = otherNode.getCost();
		
		if(thisCost == otherCost)
		{
			return 0;
		}
		else if(thisCost > otherCost)
		{
			return 1;
		}
		else
		{
			return -1;
		}
		
	}
}