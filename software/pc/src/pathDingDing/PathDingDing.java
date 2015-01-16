package pathDingDing;

import smartMath.*;
import table.Table;
import table.obstacles.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import exceptions.*;

/**
 * Classe encapsulant les calculs de pathfinding 
 * @author Marsya et Etienne
 *
 */
public class PathDingDing
{
	public static ArrayList<Vec2> computePath(Vec2 start, Vec2 end, Table table) throws Exception
	{
		Graph graph = new Graph();
		
		//ajout du noeud de départ au graphe
		graph.setStartNode(new Node(start.x, start.y));
		
		//ajout du noeud de fin au graphe
		graph.setEndNode(new Node(end.x, end.y));
		
		ArrayList<Vec2> pathVec2 = new ArrayList<Vec2>();
		ArrayList<Node> pathNode = computeGraph(graph);
		for(int i = 0; i < pathNode.size(); i++)
			pathVec2.add(pathNode.get(i).toVec2());
		return pathVec2;
	}
	
	public static ArrayList<Node> computeGraph(Graph graph)
	{
		//heuristicalCost = node.distanceTo(graph.getEndNode());
		ArrayList<Node> openList = new ArrayList<Node>(); //liste ouverte des points, triee par heuristique croissante
		ArrayList<Node> closedList = new ArrayList<Node>(); //liste fermee des points, triee par cout croissant
		openList.add(graph.getStartNode());
		graph.getStartNode().setHeuristicCost(graph.getEndNode());
		//le noeud precedant le premier noeud est lui-même
		graph.getStartNode().setPrevious(graph.getStartNode());
		System.out.println("on ajoute le point ("+graph.getStartNode().x+", "+graph.getStartNode().y+") a la liste ouverte");
		//tant que la liste ouverte n'est pas vide ou que l'on n'est pas encore arrivé
		while(!openList.isEmpty() && openList.get(0) != graph.getEndNode())
		{
			//on supprime le point a l'heuristique le plus faible a la liste ouverte
			Node actual = openList.remove(0);
			System.out.println("on retire le point ("+actual.x+", "+actual.y+") a la liste ouverte");
			
			//on ajoute tous les points adjacents a ce noeud dans la liste ouverte, en la laissant triee par cout heuristique croissant.
			//i designe l'indice des noeuds adjacents au noeud actuel
			for(int i = 0; i < actual.getLinkNumber(); i++)
			{
				//j designe l'indice permettant de placer chaque noeud adjacent au bon endroit dans la liste ouverte
				int j = 0;
				if(!openList.contains(actual.getLink(i).getDestination()) && !closedList.contains(actual.getLink(i).getDestination()))
				{
					//calcul de l'heuristique
					actual.getLink(i).getDestination().setHeuristicCost(graph.getEndNode());
					while(j < openList.size() && openList.get(j).getHeuristicCost() < actual.getLink(i).getDestination().getHeuristicCost())
						j++;
					//le noeud actuel est designe comme predecesseur de son successeur (oui, oui!)
					actual.getLink(i).getDestination().setPrevious(actual);
					//ajout du noeud suivant le noeud actuel dans la liste ouverte
					openList.add(j, actual.getLink(i).getDestination());
					System.out.println("on ajoute le point ("+actual.getLink(i).getDestination().x+", "+actual.getLink(i).getDestination().y+") a la liste ouverte");
				}
			}
			
			//ajout du point actuel a la liste fermee, tout en la maintenant triee par cout croissant
			int j = 0;
			//calcul du cout du point ajouté à la liste fermée
			actual.setCost(actual.getPrevious());
			while(j < closedList.size() && actual.getCost() < closedList.get(j).getCost())
				j++;
			closedList.add(j, actual);
			System.out.println("on ajoute le point ("+actual.x+", "+actual.y+") a la liste fermee");
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
}
