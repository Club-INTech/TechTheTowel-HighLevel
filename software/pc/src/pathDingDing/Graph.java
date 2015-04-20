package pathDingDing;

import smartMath.*;
import table.Table;

import java.util.ArrayList;
import java.util.EnumSet;

import enums.ObstacleGroups;

/**
 * graphe definissant les liens entre les noeuds
 * @author Etienne
 *
 */
public class Graph
{
	private ArrayList<Node> mNodes;
	private Node mStartNode;
	private Node mEndNode;
	public ArrayList<Area> mAreas; // TODO : private (utilise par graphics)
	private Table mTable;
	private EnumSet<ObstacleGroups> mObstaclesToConsider;
	
	public Graph(Table table, EnumSet<ObstacleGroups> obstaclesToConsider)
	{
		mNodes = new ArrayList<Node>();
		mAreas = new ArrayList<Area>();
		mTable = table;
		mObstaclesToConsider = obstaclesToConsider;
		buildGraph();
	}
	
	/**
	 * methode interne a la classe, mettant en place le graphe a sa construction
	 */
	private void buildGraph()
	{
		int robotRadius = Integer.parseInt(mTable.getConfig().getProperty("rayon_robot"));
		//ajout des noeuds fixes
		mNodes.add(new Node(-1100 + robotRadius, 1222 + robotRadius));//noeud 0
		mNodes.add(new Node(-1100 + robotRadius, 778 - robotRadius));//noeud 1
		mNodes.add(new Node(-300 - robotRadius, 100 + robotRadius));//noeud 2
		mNodes.add(new Node(300 + robotRadius, 100 + robotRadius));//noeud 3
		mNodes.add(new Node(1100 - robotRadius, 778 - robotRadius));//noeud 4
		mNodes.add(new Node(1100 - robotRadius, 1222 + robotRadius));//noeud 5
		mNodes.add(new Node(533 + robotRadius, 1420 - robotRadius));//noeud 6
		mNodes.add(new Node(-533 - robotRadius, 1420 - robotRadius));//noeud 7
		//noeuds additionnels
		//TODO : rajouter des noeuds intelligemment
		mNodes.add(new Node(-1100 + robotRadius, 896));// noeud 8
		mNodes.add(new Node(0, 100 + robotRadius));// noeud 9
		mNodes.add(new Node(1100 - robotRadius, 896));// noeud 10
		mNodes.add(new Node(0, 896));// noeud 11
		mNodes.add(new Node(-400, 896));// noeud 12
		mNodes.add(new Node(400, 896));// noeud 13
		mNodes.add(new Node(-1100 + robotRadius, 100 + robotRadius));//noeud 14
		mNodes.add(new Node(1100 - robotRadius, 100 + robotRadius));//noeud 15

		
		//obstacles circulaires
		ArrayList<Circle> circles = new ArrayList<Circle>();
		//ObstacleCircular oc = new ObstacleCircular(new Vec2(0, 1000), 200);
		//circles.add(oc.toCircle());
		
		//creation des liens du graphe
		//parcours des noeuds
		for(int i = 0; i < mNodes.size(); i++)
			for(int j = 0; j < mNodes.size(); j++)
				if(i != j)
				{
					//parcours des cercles, si le lien ne coupe aucun cercle, on le rajoute
					boolean intersects = false;
					for(int k = 0; k < circles.size(); k++)
						if(PathDingDing.intersects(new Segment(mNodes.get(i).toVec2(), mNodes.get(j).toVec2()), new Circle(circles.get(k).position, circles.get(k).radius + mTable.getObstacleManager().getRobotRadius())))
							intersects = true;
					if(!intersects)
						mNodes.get(i).addLink(mNodes.get(j));
				}
		
		Area area = new Area(-1100 + robotRadius, 1420 - robotRadius, 567 - 2*robotRadius, 510);//zone 0
		area.attachNode(mNodes.get(0));
		area.attachNode(mNodes.get(7));
		mAreas.add(area);
		
		area = new Area(-1500 + robotRadius, 1222 + robotRadius, 400, 708 - 2*robotRadius);//zone 1
		area.attachNode(mNodes.get(0));
		area.attachNode(mNodes.get(7));
		mAreas.add(area);
		
		area = new Area(-1500 + robotRadius, 0 + robotRadius, 400, 778 - 2*robotRadius);//zone 2
		area.attachNode(mNodes.get(1));
		area.attachNode(mNodes.get(2));
		mAreas.add(area);
		
		area = new Area(-1100 + robotRadius, 0 + robotRadius, 800 - 2*robotRadius, 100);//zone 3
		area.attachNode(mNodes.get(1));
		area.attachNode(mNodes.get(2));
		mAreas.add(area);
		
		area = new Area(300 + robotRadius, 0 + robotRadius, 800 - 2*robotRadius, 100);//zone 4
		area.attachNode(mNodes.get(3));
		area.attachNode(mNodes.get(4));
		mAreas.add(area);
		
		area = new Area(1100 - robotRadius, 0 + robotRadius, 400, 778 - 2*robotRadius);//zone 5
		area.attachNode(mNodes.get(3));
		area.attachNode(mNodes.get(4));
		mAreas.add(area);
		
		area = new Area(1100 - robotRadius, 1222 + robotRadius, 400, 708 - 2*robotRadius);//zone 6
		area.attachNode(mNodes.get(5));
		area.attachNode(mNodes.get(6));
		mAreas.add(area);
		
		area = new Area(533 + robotRadius, 1420 - robotRadius, 567 - 2*robotRadius, 510);//zone 7
		area.attachNode(mNodes.get(5));
		area.attachNode(mNodes.get(6));
		mAreas.add(area);
		
		area = new Area(-1100 + robotRadius, 100 + robotRadius, 2200 - 2*robotRadius, 1320 - 2*robotRadius);//zone 10
		area.attachNode(mNodes.get(0));
		area.attachNode(mNodes.get(1));
		area.attachNode(mNodes.get(2));
		area.attachNode(mNodes.get(3));
		area.attachNode(mNodes.get(4));
		area.attachNode(mNodes.get(5));
		area.attachNode(mNodes.get(6));
		area.attachNode(mNodes.get(7));
		area.attachNode(mNodes.get(8));
		area.attachNode(mNodes.get(9));
		area.attachNode(mNodes.get(10));
		area.attachNode(mNodes.get(11));
		area.attachNode(mNodes.get(12));
		area.attachNode(mNodes.get(13));
		area.attachNode(mNodes.get(14));
		area.attachNode(mNodes.get(15));
		mAreas.add(area);
	}
	
	public void setObstaclesToConsider(EnumSet<ObstacleGroups> obstaclesToConsider)
	{
		mObstaclesToConsider = obstaclesToConsider;
	}
	
	public ArrayList<Node> getNodes()
	{
		return mNodes;
	}
	
	public Node getStartNode()
	{
		return mStartNode;
	}
	
	public Node getEndNode()
	{
		return mEndNode;
	}
	
	//specifie le point de depart et le lie au graphe en fonction de sa zone
	public void setStartNode(Node startNode)
	{
		mStartNode = startNode;
		//on parcourt les zones pour trouver celle dans laquelle le noeud
		for(int i = 0; i < mAreas.size(); i++)
			//si on en trouve une zone dans laquelle il est
			if(mAreas.get(i).isInArea(startNode))
				//on le rattache a tous les points de la zone
				for(int j = 0; j < mAreas.get(i).attachedNodesNumber(); j++)
					mStartNode.addLink(mAreas.get(i).getAttachedNode(j));
	}
	
	//specifie le point d'arrivee et le lie au graphe en fonction de sa zone
	public void setEndNode(Node endNode)
	{
		mEndNode = endNode;
		//on parcourt les zones pour trouver celle dans laquelle le noeud
		for(int i = 0; i < mAreas.size(); i++)
		{
			//si on en trouve une zone dans laquelle il est
			if(mAreas.get(i).isInArea(mEndNode))
			{
				//on le rattache a tous les points de la zone
				for(int j = 0; j < mAreas.get(i).attachedNodesNumber(); j++)
				{
					mAreas.get(i).getAttachedNode(j).addLink(mEndNode);
				}
			}
		}
	}
	
	//detache un noeud du graphe
	public void unlinkNode(Node node)
	{
		//parcours des noeuds
		for(int i = 0; i< mNodes.size(); i++)
		{
			mNodes.get(i).deleteLink(node);
		}
	}
	
	//trie une liste de noeuds en fonction de leur heuristique
	// non utilisee dans l'algorithme
	public static ArrayList<Node> sort(ArrayList<Node> nodes)
	{
		for(int i = 1; i < nodes.size(); i++)
		{
			int j = 0;
			while(nodes.get(i).getHeuristicCost() < nodes.get(j).getHeuristicCost())
			{
				j++;
			}
			nodes.add(j, nodes.get(i));
			nodes.remove(i+1);
		}
		return nodes;
	}
	
	public ArrayList<Area> getAreas()
	{
		return mAreas;
	}
	
	public boolean isOnTable(Node node)
	{
		mObstaclesToConsider.add(ObstacleGroups.ENNEMY_ZONE);
		
		//conversion des obstacles circulaires en cercles
		ArrayList<Circle> circles = new ArrayList<Circle>();
		for(int i = 0; i < mTable.getObstacleManager().getMobileObstacles().size(); i++)
			//si l'obstacle est sp�cifi� par la liste d'obstacles � consid�rer
			if(mObstaclesToConsider.contains(mTable.getObstacleManager().getMobileObstacles().get(i).getObstacleGroup()))
				circles.add(new Circle(mTable.getObstacleManager().getMobileObstacles().get(i).getPosition(), mTable.getObstacleManager().getMobileObstacles().get(i).getRadius()));
		for(int i = 0; i < mTable.getObstacleManager().getFixedObstacles().size(); i++)
			//si l'obstacle est sp�cifi� par la liste d'obstacles � consid�rer
			if(mObstaclesToConsider.contains(mTable.getObstacleManager().getFixedObstacles().get(i).getObstacleGroup()))
				circles.add(new Circle(mTable.getObstacleManager().getFixedObstacles().get(i).getPosition(), mTable.getObstacleManager().getFixedObstacles().get(i).getRadius()));
		
		//si le noeud est dans un cercle, on retourne directement false
		for(int i = 0; i < circles.size(); i++)
			if((circles.get(i).position.x - node.x)*(circles.get(i).position.x - node.x) + (circles.get(i).position.y - node.y)*(circles.get(i).position.y - node.y) < (circles.get(i).radius + mTable.getObstacleManager().getRobotRadius())*(circles.get(i).radius + mTable.getObstacleManager().getRobotRadius()))
				return false;
		
		boolean isOnTable = false;
		//on parcourt les zones pour trouver celle dans laquelle le noeud
		for(int i = 0; i < mAreas.size(); i++)
		{
			//si on en trouve une zone dans laquelle il est
			if(mAreas.get(i).isInArea(node))
			{
				isOnTable = true;
			}
		}
		return isOnTable;
	}
	
    /**
     * 
     * @param position
     * @return les groupes d'obstacles dans lesquels est le point, en y ajoutant le rayon du robot
     */
    //TODO : trouver un meilleur nom?
    public EnumSet<ObstacleGroups> obstacleGroupsInPosition(Vec2 position)
    {
    	EnumSet<ObstacleGroups> obstacleGroups = EnumSet.noneOf(ObstacleGroups.class);
    	for(int i = 0; i < mTable.getObstacleManager().getMobileObstacles().size(); i++)
			if((position.x - mTable.getObstacleManager().getMobileObstacles().get(i).getPosition().x)*(position.x - mTable.getObstacleManager().getMobileObstacles().get(i).getPosition().x)
			 + (position.y - mTable.getObstacleManager().getMobileObstacles().get(i).getPosition().y)*(position.y - mTable.getObstacleManager().getMobileObstacles().get(i).getPosition().y)
			 < (mTable.getObstacleManager().getMobileObstacles().get(i).getRadius() + mTable.getObstacleManager().getRobotRadius())*(mTable.getObstacleManager().getMobileObstacles().get(i).getRadius() + mTable.getObstacleManager().getRobotRadius()))
			{
				obstacleGroups.add(mTable.getObstacleManager().getMobileObstacles().get(i).getObstacleGroup());
				break;
			}
		for(int i = 0; i < mTable.getObstacleManager().getFixedObstacles().size(); i++)
			if((position.x - mTable.getObstacleManager().getFixedObstacles().get(i).getPosition().x)*(position.x - mTable.getObstacleManager().getFixedObstacles().get(i).getPosition().x)
					 + (position.y - mTable.getObstacleManager().getFixedObstacles().get(i).getPosition().y)*(position.y - mTable.getObstacleManager().getFixedObstacles().get(i).getPosition().y)
					 < (mTable.getObstacleManager().getFixedObstacles().get(i).getRadius() + mTable.getObstacleManager().getRobotRadius())*(mTable.getObstacleManager().getFixedObstacles().get(i).getRadius() + mTable.getObstacleManager().getRobotRadius()))
				obstacleGroups.add(mTable.getObstacleManager().getFixedObstacles().get(i).getObstacleGroup());
    	return obstacleGroups;
    }
	
	//retourne le noeud correct (pas dans un obstacle) le plus proche de la position
	//TODO : cree un bug si TOUS les noeuds sont occupes par des obstacles
	public Node closestNode(Vec2 position)
	{
		int minSquaredDistance = 13000000;
		Node closestNode = new Node(0, 0);
		for(int i = 0; i < mNodes.size(); i++)
		{
			int distanceToNode = (mNodes.get(i).x - position.x)*(mNodes.get(i).x - position.x) + (mNodes.get(i).y - position.y)*(mNodes.get(i).y - position.y);
			if(distanceToNode < minSquaredDistance && isOnTable(mNodes.get(i)))
			{
				minSquaredDistance = distanceToNode;
				closestNode = mNodes.get(i);
			}
		}
		return closestNode;
	}
}
