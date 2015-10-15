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
	 * Valeur heuristique (distance eulerienne jusqu'au point d'arrivee)
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
	 * Cree un noeud sans valeur heuristique (mise à 0)
	 * /!\ NE PAS UTILISER DANS UN VRAI CALCUL DE CHEMIN
	 * @param pos position
	 */
	public PDDNode(Vec2 pos)
	{
		this.position = pos;
		heuristic=0;
	}
	
	
	/**
	 * Renvoie la postion du noeud
	 */
	public Vec2 getPosition()
	{
		return this.position;
	}
	
	private void computeHeuristic(PDDNode goal)
	{
		//TODO Calcul de la valeur heuristique
	}
}