package pathDingDing;

import smartMath.*;
import table.Table;
import utils.Log;

import java.util.ArrayList;
import container.Service;

/**
 * Classe de calcul de chemins utilisant l'algorithme A*
 * @author Etienne, julian
 *
 */
public class PathDingDing implements Service
{
	//La table
	private Table table;
	
	//Le graphe
	
	//Noeuds ouverts
	private ArrayList<PDDNode> openNodes;
	
	//Noeuds fermés
	private ArrayList<PDDNode> closedNodes;
	
	//Le log
	private Log log;
	
	public PathDingDing(Table table, Log log)
	{
		//TODO constructeur pathfinding
		this.table = table;
		this.log = log;
		
		this.openNodes = new ArrayList<PDDNode>();
		this.closedNodes = new ArrayList<PDDNode>();
	}
	
	
	public ArrayList<Vec2> computePath()
	{
		//TODO pathfinding
		return null;
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




