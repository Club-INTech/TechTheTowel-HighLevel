package pathDingDing;

import smartMath.*;
import table.Table;
import table.obstacles.*;

import java.util.ArrayList;

import exceptions.*;

/**
 * Classe de calcul de chemins
 * @author Etienne
 *
 */
public class PathDingDing
{
	private Table mTable;
	private Graph mGraph;
	
	/**
	 * constructeur
	 * @param table
	 */
	public PathDingDing(Table table)
	{
		mTable = table;
		mGraph = new Graph(mTable);
	}
	
	/**
	 * methode a appeler
	 * @param start le point de depart
	 * @param end le point d'arrivee
	 * @return un chemin optimise liant depart et arrivee
	 * @throws Exception pas encore implemente
	 */
	public ArrayList<Vec2> computePath(Vec2 start, Vec2 end) throws Exception
	{
		//le cas ou les points de depart et d'arrivee sont reliables en ligne droite est directement traite
		ArrayList<Vec2> directPath =  new ArrayList<Vec2>();
		directPath.add(start);
		directPath.add(end);
		if(isPathCorrect(directPath))
			return directPath;

		//ajout du noeud de départ au graphe
		mGraph.setStartNode(new Node(start.x, start.y));

		//ajout du noeud de fin au graphe
		mGraph.setEndNode(new Node(end.x, end.y));
		
		//calcul du chemin via computeGraph, convertion, et simplification.
		ArrayList<Vec2> pathVec2 = new ArrayList<Vec2>();
		ArrayList<Node> pathNode = computeGraph(mGraph);
		for(int i = 0; i < pathNode.size(); i++)
			pathVec2.add(pathNode.get(i).toVec2());
		simplify(pathVec2);
		return pathVec2;
	}
	
	/**
	 * renvoie un chemin sous forme de node, non optimise. methode interne a la classe.
	 * @param graph
	 * @return
	 */
	private ArrayList<Node> computeGraph(Graph graph)
	{
		//algo A star
		
		ArrayList<Node> openList = new ArrayList<Node>(); //liste ouverte des points, triee par heuristique croissante
		ArrayList<Node> closedList = new ArrayList<Node>(); //liste fermee des points, triee par cout croissant
		openList.add(graph.getStartNode());
		graph.getStartNode().setHeuristicCost(graph.getEndNode());
		//le noeud precedant le premier noeud est lui-même
		graph.getStartNode().setPrevious(graph.getStartNode());
		//tant que la liste ouverte n'est pas vide ou que l'on n'est pas encore arrivé
		while(!openList.isEmpty() && openList.get(0) != graph.getEndNode())
		{
			//on supprime le point a l'heuristique le plus faible a la liste ouverte
			Node current = openList.remove(0);
			
			//on ajoute tous les points adjacents a ce noeud dans la liste ouverte, en la laissant triee par cout total = cout heuristique + cout croissant.
			//i designe l'indice des noeuds adjacents au noeud actuel
			for(int i = 0; i < current.getLinkNumber(); i++)
			{
				//j designe l'indice permettant de placer chaque noeud adjacent au bon endroit dans la liste ouverte
				int j = 0;
				Node adjacent = current.getLink(i).getDestination();
				if((!openList.contains(adjacent) || adjacent.getCost() > adjacent.calculateCost(current))  && !closedList.contains(adjacent))
				{
					//le noeud actuel est designe comme predecesseur de son successeur
					adjacent.setPrevious(current);
					//calcul de l'heuristique
					adjacent.setHeuristicCost(graph.getEndNode());
					//calcul du cout
					adjacent.setCost(current.getLink(i).getDestination().getPrevious());
					while(j < openList.size() && openList.get(j).getHeuristicCost() + openList.get(j).getCost() < adjacent.getHeuristicCost() + adjacent.getCost())
						j++;
					//ajout du noeud suivant le noeud actuel dans la liste ouverte
					openList.add(j, adjacent);
				}
			}
			
			//ajout du point actuel a la liste fermee, tout en la maintenant triee par cout croissant
			int j = 0;
			while(j < closedList.size() && current.getCost() < closedList.get(j).getCost())
				j++;
			closedList.add(j, current);
		}
		ArrayList<Node> path = new ArrayList<Node>();
		//si on a vide la liste ouverte, c'est qu'aucun chemin ne peut etre trouve
		if(openList.isEmpty())
			System.out.println("aucun chemin trouve :(");
		//sinon, des chemins ont ete trouves, on determine le plus court
		else
		{
			path.add(graph.getEndNode());
			while(path.get(path.size() - 1) != graph.getStartNode())
			{
				path.add(path.get(path.size() - 1).getPrevious());
			}
		}
		return path;
	}

	/**
	 * 
	 * @param segment1
	 * @param segment2
	 * @return vrai si il y a intersection entre les deux segments, faux sinon (les extremites ne sont pas comptees comme intersection)
	 */
	public static boolean intersects(Segment segment1, Segment segment2)
	{
		// les points formant les segments 1 et 2 sont A1, B1, A2, B2
		// pour qu'il y ait intersection, il faut :
		// - les segments ne soient pas paralleles : (A1B1)^(A2B2) != 0
		// - les point d'intersection est entre A2 et B2 : (A1B1)^(A1B2) * (A1B1)^(A1A2) < 0
		// - les point d'intersection est entre A1 et B1 : (A2B2)^(A2B1) * (A2B2)^(A2A1) < 0
		// ^ = produit vectoriel
		return ((double)segment1.getB().x - (double)segment1.getA().x) * ((double)segment2.getB().y - (double)segment2.getA().y) - ((double)segment1.getB().y - (double)segment1.getA().y) * ((double)segment2.getB().x - (double)segment2.getA().x) != 0
				&& (((double)segment1.getB().x - (double)segment1.getA().x) * ((double)segment2.getB().y - (double)segment1.getA().y) - ((double)segment1.getB().y - (double)segment1.getA().y) * ((double)segment2.getB().x - (double)segment1.getA().x)) * (((double)segment1.getB().x - (double)segment1.getA().x) * ((double)segment2.getA().y - (double)segment1.getA().y) - ((double)segment1.getB().y - (double)segment1.getA().y) * ((double)segment2.getA().x - (double)segment1.getA().x)) < 0
				&& (((double)segment2.getB().x - (double)segment2.getA().x) * ((double)segment1.getB().y - (double)segment2.getA().y) - ((double)segment2.getB().y - (double)segment2.getA().y) * ((double)segment1.getB().x - (double)segment2.getA().x)) * (((double)segment2.getB().x - (double)segment2.getA().x) * ((double)segment1.getA().y - (double)segment2.getA().y) - ((double)segment2.getB().y - (double)segment2.getA().y) * ((double)segment1.getA().x - (double)segment2.getA().x)) < 0
				;
	}
	
	/**
	 * 
	 * @param segment
	 * @param circle
	 * @return vrai si il y a intersection entre le segment et le cercle, faux sinon
	 */
	public static boolean intersects(Segment segment, Circle circle)
	{
		// TODO : commenter
		double area = (circle.position.x - segment.getA().x)*(segment.getB().y - segment.getA().y) - (circle.position.y - segment.getA().y)*(segment.getB().x - segment.getA().x);
		double distA = (segment.getA().x - circle.position.x)*(segment.getA().x - circle.position.x) + (segment.getA().y - circle.position.y)*(segment.getA().y - circle.position.y);
		double distB = (segment.getB().x - circle.position.x)*(segment.getB().x - circle.position.x) + (segment.getB().y - circle.position.y)*(segment.getB().y - circle.position.y);
		if(distA >= circle.radius*circle.radius && distB < circle.radius*circle.radius || distA < circle.radius*circle.radius && distB >= circle.radius*circle.radius)
			return true;
		return distA >= circle.radius*circle.radius
			&& distB >= circle.radius*circle.radius
			&& area * area / ((segment.getB().x - segment.getA().x)*(segment.getB().x - segment.getA().x)+(segment.getB().y - segment.getA().y)*(segment.getB().y - segment.getA().y)) <= circle.radius * circle.radius
			&& (segment.getB().x - segment.getA().x)*(circle.position.x - segment.getA().x) + (segment.getB().y - segment.getA().y)*(circle.position.y - segment.getA().y) >= 0
			&& (segment.getA().x - segment.getB().x)*(circle.position.x - segment.getB().x) + (segment.getA().y - segment.getB().y)*(circle.position.y - segment.getB().y) >= 0;
	}
	
	/**
	 * 
	 * @param segment1
	 * @param segment2
	 * @return le point d'intersection des droites portees par les segments.
	 */
	public static Vec2 intersection(Segment segment1, Segment segment2)
	{
		// resolution du systeme associe aux deux segments
		double inter, k;
		
		if((segment2.getB().y - segment2.getA().y) != 0)
		{
			inter = (double)(segment2.getB().x - segment2.getA().x) / (double)(segment2.getB().y - segment2.getA().y);
			k = (segment1.getA().x - segment2.getA().x + inter * (double)(segment2.getA().y - segment1.getA().y)) / (double)(segment1.getB().x - segment1.getA().x - inter * (segment1.getB().y - segment1.getA().y));
		}
		else
			k = -(double)(segment2.getA().y - segment1.getA().y) / (double)(segment1.getB().y - segment1.getA().y);
		
		return new Vec2((int)(segment1.getA().x - k * (segment1.getB().x - segment1.getA().x)), (int)(segment1.getA().y - k * (segment1.getB().y - segment1.getA().y)));
	}
	
	/**
	 * 
	 * @param path
	 * @return vrai si le chemin est correct(ne rencontre pas d'obstacles), faux sinon
	 */
	public boolean isPathCorrect(ArrayList<Vec2> path)
	{
		boolean intersects = false;
		//parcours du chemin
		for(int i = 0; i < path.size() - 1; i++)
		{
			//test de collision avec chaque segment
			for(int j = 0; j < mTable.getObstacleManager().getLines().size(); j++)
			{
				//si les deux segments de coupent
				if(intersects(mTable.getObstacleManager().getLines().get(j), new Segment(path.get(i), path.get(i+1))))
				{
					intersects = true;
					//System.out.println("intersection avec la ligne "+j);
				}
			}
		}
		return !intersects;
	}
	
	/**
	 * simplifie un chemin en tenant compte des obstacles
	 * @param path
	 */
	public void simplify(ArrayList<Vec2> path)
	{
		//parcours du chemin
		for(int i = 0; i < path.size() - 2; i++)
		{
			ArrayList<Vec2> segment = new ArrayList<Vec2>();
			segment.add(path.get(i));
			segment.add(path.get(i+2));
			if(isPathCorrect(segment))
			{
				//si possible on retire un point inutile dans le chemin
				path.remove(i+1);
				i--;
			}
		}
	}
	
	public Graph getGraph()
	{
		return mGraph;
	}
}




