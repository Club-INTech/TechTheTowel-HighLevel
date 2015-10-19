package pathDingDing;

import smartMath.*;
import table.Table;
import utils.Log;

import java.util.ArrayList;
import container.Service;

/**
 * Classe de calcul de chemins utilisant l'algorithme A*
 * 
 * Le but de l'algorithme A* est de trouver le chemin le plus court en demarrant d'un
 * node et en parcourant les nodes alentours reliés. Le node suivant est choisi selon 2 critères :
 *  1- Le coût direct, c'est-a-dire le temps que va prendre le robot pour s'y rendre
 *  2- L'heuristique, cela peut prendre plusieures formes, ici c'est la distance à vol d'oiseau entre
 *     le noeud et l'arrivée, donc un noeud qui fait s'eloigner le robot de la destination finale aura
 *     une heuristique plus elevee qu'un noeud plus proche
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
 * @author Etienne, julian
 *
 */
public class PathDingDing implements Service
{
	//La table de jeu
	private Table table;
	
	//Le graphe a parcourir
	private PDDGraph graph;
	
	//Noeuds ouverts (a vider a chaque calcul de chemin) 
	private ArrayList<PDDNode> openNodes;
	
	//Noeuds fermés (a vider a chaque calcul de chemin)
	private ArrayList<PDDNode> closedNodes;
	
	//Le log
	private Log log;
	
	public PathDingDing(Table table, Log log)
	{
		//TODO constructeur pathfinding
		this.table = table;
		this.log = log;
		
		this.graph = new PDDGraph();
		
		this.openNodes = new ArrayList<PDDNode>();
		this.closedNodes = new ArrayList<PDDNode>();
	}
	
	
	public ArrayList<Vec2> computePath()
	{
		//TODO pathfinding
		this.initialise();
		return null;
	}
	
	/**
	 * Vide les listes ouverte et fermee pour lancer un nouveau calcul
	 */
	public void initialise()
	{
		if(!openNodes.isEmpty() || !closedNodes.isEmpty())
		{
			this.openNodes = new ArrayList<PDDNode>();
			this.closedNodes = new ArrayList<PDDNode>();
		}
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
		// - le point d'intersection est entre A2 et B2 : (A1B1)^(A1B2) * (A1B1)^(A1A2) < 0
		// - le point d'intersection est entre A1 et B1 : (A2B2)^(A2B1) * (A2B2)^(A2A1) < 0
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
		// TODO : expliquer l'algo (TOO MANY CASTS EXCEPTION)
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

	@Override
	public void updateConfig() 
	{
		// TODO update the config
	}
}




