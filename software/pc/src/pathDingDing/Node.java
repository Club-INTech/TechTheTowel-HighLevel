package pathDingDing;

import smartMath.Vec2;

/**
 *  Noeud d'un graphe de pathDingDing
 * @author julian
 *
 */
public class Node
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
}