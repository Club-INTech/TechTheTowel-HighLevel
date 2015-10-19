package pathDingDing;

import smartMath.Vec2;

/**
 *  Noeud d'un graphe de pathDingDing
 * @author julian
 *
 */
public class PDDNode
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
	public PDDNode(Vec2 pos, PDDNode goal)
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
	public PDDNode(Vec2 pos)
	{
		this.position = pos;
		// Vu que l'on a pas d'arrivee fixee, on met l'heuristique à 0
		this.heuristic=0;
	}
	
	
	/**
	 * Renvoie la postion du noeud
	 */
	public Vec2 getPosition()
	{
		return this.position;
	}
	
	/**
	 * Calcule la valeur heuristique du noeud
	 * @param goal point d'arrivee du chemin recherché
	 */
	private void computeHeuristic(PDDNode goal)
	{
		// Calcul de la distance a vol d'oiseau
		this.heuristic = Math.sqrt(Math.pow(Math.abs(this.position.x - goal.getPosition().x), 2) + Math.pow(Math.abs(this.position.y - goal.getPosition().y), 2));
	}
}