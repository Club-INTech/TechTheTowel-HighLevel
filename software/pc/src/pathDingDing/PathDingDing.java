package pathDingDing;

import smartMath.*;
import table.Table;
import table.obstacles.*;
import utils.Log;

import java.util.ArrayList;
import java.util.EnumSet;

import container.Service;
import exceptions.*;
import enums.ObstacleGroups;

/**
 * Classe de calcul de chemins
 * @author Etienne
 *
 */
public class PathDingDing implements Service
{
	private Table mTable;
	private Graph mGraph;
	private EnumSet<ObstacleGroups> mObstaclesToConsider;
	
	/**
	 * constructeur
	 * @param table
	 */
	public PathDingDing(Table table)
	{
		mTable = table;
		mObstaclesToConsider = EnumSet.noneOf(ObstacleGroups.class);
		mGraph = new Graph(mTable, mObstaclesToConsider);
	}
	
	/**
	 * methode a appeler, avec la liste des obstacles � considerer
	 * @param start le point de depart
	 * @param end le point d'arrivee
	 * @return un chemin optimise liant depart et arrivee
	 * @throws Exception pas encore implemente
	 */
	public ArrayList<Vec2> computePath(Vec2 start, Vec2 end, EnumSet<ObstacleGroups> obstaclesToConsider) throws PathNotFoundException
	{
		this.mObstaclesToConsider = obstaclesToConsider;
		mGraph.setObstaclesToConsider(mObstaclesToConsider);
		
		//le cas ou les points de depart et d'arrivee sont reliables en ligne droite est directement traite
		ArrayList<Vec2> directPath =  new ArrayList<Vec2>();
		directPath.add(start);
		directPath.add(end);
		if(isPathCorrect(directPath))
			return directPath;
		
		ArrayList<Node> pathNode = new ArrayList<Node>();
		ArrayList<Vec2> pathVec2 = new ArrayList<Vec2>();
		
		Node startNode = new Node(start.x, start.y);
		//si le noeud de depart n'est pas sur la table, on le lie au point le plus proche
		if(!mGraph.isOnTable(startNode))
		{
			pathVec2.add(start);
			Node closestNode = mGraph.closestNode(startNode.toVec2());
			mGraph.setStartNode(new Node(closestNode.x, closestNode.y));
		}
		else
			//ajout du noeud de depart au graphe
			mGraph.setStartNode(startNode);

		//ajout du noeud de fin au graphe
		Node endNode = new Node(end.x, end.y);
		mGraph.setEndNode(endNode);
		
		//calcul du chemin via computeGraph, convertion, et simplification.
		try
		{
			pathNode = computeGraph(mGraph);
		}
		catch(PathNotFoundException e)
		{
			//on detache le dernier noeud du graphe
			mGraph.unlinkNode(endNode);
			throw new PathNotFoundException();
		}
		//recopie de pathNode dans pathVec2, avec inversion du sens
		for(int i = pathNode.size() - 1; i >= 0; i--)
			pathVec2.add(pathNode.get(i).toVec2());
		simplify(pathVec2);
		//on detache le dernier noeud du graphe
		mGraph.unlinkNode(endNode);
		return pathVec2;
	}
	
	/**
	 * renvoie un chemin sous forme de node, non optimise. methode interne a la classe.
	 * @param graph
	 * @return
	 */
	private ArrayList<Node> computeGraph(Graph graph) throws PathNotFoundException
	{
		//algo A star
		
		ArrayList<Node> openList = new ArrayList<Node>(); //liste ouverte des points, triee par heuristique croissante
		ArrayList<Node> closedList = new ArrayList<Node>(); //liste fermee des points, triee par cout croissant
		openList.add(graph.getStartNode());
		graph.getStartNode().setHeuristicCost(graph.getEndNode());
		//le noeud precedant le premier noeud est lui-meme
		graph.getStartNode().setPrevious(graph.getStartNode());
		//tant que la liste ouverte n'est pas vide ou que l'on n'est pas encore arrive
		while(!openList.isEmpty() && openList.get(0) != graph.getEndNode())
		{
			//on supprime le point a l'heuristique le plus faible a la liste ouverte
			Node current = openList.remove(0);
			
			//on ajoute tous les points adjacents a ce noeud dans la liste ouverte, en la laissant triee par cout total = cout heuristique + cout croissant.
			//i designe l'indice des noeuds adjacents au noeud actuel
			for(int i = 0; i < current.getLinkNumber(); i++)
			{
				Node adjacent = current.getLink(i).getDestination();
				//si le noeud adjacent est visible (pas d'obstacle)
				ArrayList<Vec2> segment = new ArrayList<Vec2>();
				segment.add(current.toVec2());
				segment.add(adjacent.toVec2());
				if(isPathCorrect(segment))
				{
					//j designe l'indice permettant de placer chaque noeud adjacent au bon endroit dans la liste ouverte
					int j = 0;
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
			throw new PathNotFoundException();
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
		// TODO : expliquer l'algo
		double area = ((double)circle.position.x - (double)segment.getA().x)*((double)segment.getB().y - (double)segment.getA().y) - ((double)circle.position.y - (double)segment.getA().y)*((double)segment.getB().x - (double)segment.getA().x);
		double distA = ((double)segment.getA().x - (double)circle.position.x)*((double)segment.getA().x - (double)circle.position.x) + ((double)segment.getA().y - (double)circle.position.y)*((double)segment.getA().y - (double)circle.position.y);
		double distB = ((double)segment.getB().x - (double)circle.position.x)*((double)segment.getB().x - (double)circle.position.x) + ((double)segment.getB().y - (double)circle.position.y)*((double)segment.getB().y - (double)circle.position.y);
		if(distA >= (double)circle.radius*(double)circle.radius && distB < (double)circle.radius*(double)circle.radius || distA < (double)circle.radius*(double)circle.radius && distB >= (double)circle.radius*(double)circle.radius)
			return true;
		return distA >= (double)circle.radius*(double)circle.radius
			&& distB >= (double)circle.radius*(double)circle.radius
			&& area * area / (((double)segment.getB().x - (double)segment.getA().x)*((double)segment.getB().x - (double)segment.getA().x)+((double)segment.getB().y - (double)segment.getA().y)*((double)segment.getB().y - (double)segment.getA().y)) <= (double)circle.radius * (double)circle.radius
			&& ((double)segment.getB().x - (double)segment.getA().x)*((double)circle.position.x - (double)segment.getA().x) + ((double)segment.getB().y - (double)segment.getA().y)*((double)circle.position.y - (double)segment.getA().y) >= 0
			&& ((double)segment.getA().x - (double)segment.getB().x)*((double)circle.position.x - (double)segment.getB().x) + ((double)segment.getA().y - (double)segment.getB().y)*((double)circle.position.y - (double)segment.getB().y) >= 0;
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
		//si un des points est en dehors de la table, on retourne directement false
		boolean pathOnTable = true;
		for(int i = 0; i < path.size(); i++)
			if(!mGraph.isOnTable((new Node(path.get(i).x, path.get(i).y))))
				pathOnTable = false;
		if(!pathOnTable)
			return false;
			
		//conversion des obstacles circulaires en cercles
		
		ArrayList<Circle> circles = new ArrayList<Circle>();
		if(mObstaclesToConsider.contains(ObstacleGroups.ENNEMY_ROBOTS))
			for(int i = 0; i < mTable.getObstacleManager().getMobileObstacles().size(); i++)
				circles.add(new Circle(mTable.getObstacleManager().getMobileObstacles().get(i).getPosition(), mTable.getObstacleManager().getMobileObstacles().get(i).getRadius()));
		
		if(mObstaclesToConsider.contains(ObstacleGroups.YELLOW_PLOTS))
			//parcours des plots jaunes
			for(int i = 0; i < 8; i++)
				circles.add(new Circle(mTable.getObstacleManager().getFixedObstacles().get(i).getPosition(), mTable.getObstacleManager().getFixedObstacles().get(i).getRadius()));
		if(mObstaclesToConsider.contains(ObstacleGroups.GREEN_PLOTS))
			//parcours des plots verts
			for(int i = 8; i < 16; i++)
				circles.add(new Circle(mTable.getObstacleManager().getFixedObstacles().get(i).getPosition(), mTable.getObstacleManager().getFixedObstacles().get(i).getRadius()));
		if(mObstaclesToConsider.contains(ObstacleGroups.GOBLETS))
			//parcours des gobelets
			for(int i = 16; i < 21; i++)
				circles.add(new Circle(mTable.getObstacleManager().getFixedObstacles().get(i).getPosition(), mTable.getObstacleManager().getFixedObstacles().get(i).getRadius()));
		
		boolean intersects = false;
		//parcours du chemin
		for(int i = 0; i < path.size() - 1; i++)
		{
			//test de collision avec chaque segment
			for(int j = 0; j < mTable.getObstacleManager().getLines().size(); j++)
				//si les deux segments de coupent
				if(intersects(mTable.getObstacleManager().getLines().get(j), new Segment(path.get(i), path.get(i+1))))
				{
					System.out.println("intersection PathDingDing avec l'obstacle " +j);
					intersects = true;
				}
			//test de collision avec chaque cercle
			for(int j = 0; j < circles.size(); j++)
			{
				//si le segment et le cercle se coupent
				if(intersects(new Segment(path.get(i), path.get(i+1)), circles.get(j)))
				{
					System.out.println("intersection PathDingDing avec l'obstacle circulaire " +j);
					intersects = true;
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
	
	/* (non-Javadoc)
	 * @see container.Service#updateConfig()
	 */
	public void updateConfig()
	{
	}
}




