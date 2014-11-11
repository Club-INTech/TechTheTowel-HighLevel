package PathFinding;

/**
 * recherche de chemin pour les obstacles de la table
 * @author Etienne
 *
 */
public class PathFinder
{
	/**
	 * teste l'intersection de deux segments, 1 et 2, d'extr�mit�s A et B
	 * @param segment1A point A du segment 1
	 * @param segment1B point B du segment 1
	 * @param segment2A point A du segment 2
	 * @param segment2B point B du segment 2
	 * @param intersection point d'intersection des deux segments (modifi� par la fonction)
	 * @return true si les deux segments se coupent
	 */
	public static boolean intersects(Node segment1A, Node segment1B, Node segment2A, Node segment2B, Node intersection)
	{
		//si les segments sont parall�les / de longueur nulle
		if ((segment1B.getX() - segment1A.getX()) * (segment2B.getY() - segment2A.getY()) == (segment1B.getY() - segment1A.getY()) * (segment2B.getX() - segment2A.getX()))
			return false;
		
		//calcul du point d'intersection
		double inter, k;
		
		if((segment2B.getY() - segment2A.getY()) != 0)
		{
			inter = (segment2B.getX() - segment2A.getX()) / (segment2B.getY() - segment2A.getY());
			k = (segment1A.getX() - segment2A.getX() + inter * (segment2A.getY() - segment1A.getY())) / (segment1B.getX() - segment1A.getX() - inter * (segment1B.getY() - segment1A.getY()));
		}
		else
			k = -(segment2A.getY() - segment1A.getY()) / (segment1B.getY() - segment1A.getY());
		
		intersection.move(segment1A.getX() - k * (segment1B.getX() - segment1A.getX()), segment1A.getY() - k * (segment1B.getY() - segment1A.getY()));
		/*
		 * permet de tester l'intersection sans calculer le point d'intersection
		 * 
		return (segment1B.getX() - segment1A.getX()) * (segment2B.getY() - segment2A.getY()) - (segment1B.getY() - segment1A.getY()) * (segment2B.getX() - segment2A.getX()) != 0
			 && ((segment1B.getX() - segment1A.getX()) * (segment2B.getY() - segment1A.getY()) - (segment1B.getY() - segment1A.getY()) * (segment2B.getX() - segment1A.getX())) * ((segment1B.getX() - segment1A.getX()) * (segment2A.getY() - segment1A.getY()) - (segment1B.getY() - segment1A.getY()) * (segment2A.getX() - segment1A.getX())) <= 0
			 && ((segment2B.getX() - segment2A.getX()) * (segment1B.getY() - segment2A.getY()) - (segment2B.getY() - segment2A.getY()) * (segment1B.getX() - segment2A.getX())) * ((segment2B.getX() - segment2A.getX()) * (segment1A.getY() - segment2A.getY()) - (segment2B.getY() - segment2A.getY()) * (segment1A.getX() - segment2A.getX())) <= 0
			 ;
		*/
		
		//teste l'intersection des deux segments
		return Math.abs(2 * intersection.getX() - (segment1A.getX() + segment1B.getX())) <= Math.abs(segment1B.getX() - segment1A.getX())
			&& Math.abs(2 * intersection.getY() - (segment1A.getY() + segment1B.getY())) <= Math.abs(segment1B.getY() - segment1A.getY())
			&& Math.abs(2 * intersection.getX() - (segment2A.getX() + segment2B.getX())) <= Math.abs(segment2B.getX() - segment2A.getX())
			&& Math.abs(2 * intersection.getY() - (segment2A.getY() + segment2B.getY())) <= Math.abs(segment2B.getY() - segment2A.getY())
			;
	}
	
	/**
	 * cherche un chemin entre un point de d�part et d'arriv�e
	 * @param start point de d�part
	 * @param end point d'arriv�e
	 * @param table 
	 * @return un chemin dans le sens d�part -> arriv�e
	 */
	public static Path findPath(Node start, Node end, Table table)
	{
		//cr�e deux chemins, respectivement du d�but et de la fin
		Path path1 = new Path(), path2 = new Path();
		path1.add(start);
		path2.add(end);
		
		//cherche le point d'intersection le plus proche du d�but et de la fin, respectivement pour les chemins 1 et 2
		double min = 13000000, max = 0;
		int indiceDistMin = 0, indiceDistMax = 0;
		Node node = new Node(0, 0);
		boolean intersects = false;
		for(int ind_ligne = 0 ; ind_ligne < table.getLignes().size() ; ind_ligne++)
    	{
	    	if( PathFinder.intersects(start,
	    							  end,
	    							  table.getLignes().get(ind_ligne).getA(),
	    							  table.getLignes().get(ind_ligne).getB(),
	    							  node))
	    	{
	    		intersects = true;
	    		double dist = Math.pow(node.getX() - start.getX(), 2) + Math.pow(node.getY() - start.getY(), 2);
		    	if (dist <= min)
		    	{
		    		min = dist;
		    		indiceDistMin = ind_ligne;
		    	}
		    	if (dist >= max)
		    	{
		    		max = dist;
		    		indiceDistMax = ind_ligne;
		    	}
		    }
    	}
		//si il y a un point d'intersection, ajoute ce point au chemin, et recommence la recherche de chemin sur les chemins d�but -> point de passage du point d'intersection
		//                                                                                                                   point de passage du point d'intersection -> fin
		if(intersects)
		{
			if( table.getLignes().get(indiceDistMin).getNbPassagePoint() == 1 )
			{
				path1.addAll(findPath(start, table.getLignes().get(indiceDistMin).getPassagePoint1(), table));
				path1.addAll(findPath(table.getLignes().get(indiceDistMin).getPassagePoint1(), end, table));
				
			}
			else
			{
				if(Math.pow(table.getLignes().get(indiceDistMin).getPassagePoint1().getX() - end.getX(), 2) + Math.pow(table.getLignes().get(indiceDistMin).getPassagePoint1().getY() - end.getY(), 2) <= Math.pow(table.getLignes().get(indiceDistMin).getPassagePoint2().getX() - end.getX(), 2) + Math.pow(table.getLignes().get(indiceDistMin).getPassagePoint2().getY() - end.getY(), 2))
				{
					path1.addAll(findPath(start, table.getLignes().get(indiceDistMin).getPassagePoint1(), table));
					path1.addAll(findPath(table.getLignes().get(indiceDistMin).getPassagePoint1(), end, table));
				}
				else
				{
					path1.addAll(findPath(start, table.getLignes().get(indiceDistMin).getPassagePoint2(), table));
					path1.addAll(findPath(table.getLignes().get(indiceDistMin).getPassagePoint2(), end, table));
				}
			}
			
			if( table.getLignes().get(indiceDistMax).getNbPassagePoint() == 1 )
			{
				path2.addAll(findPath(end, table.getLignes().get(indiceDistMax).getPassagePoint1(), table));
				path2.addAll(findPath(table.getLignes().get(indiceDistMax).getPassagePoint1(), start, table));
			}
			else
			{
				if(Math.pow(table.getLignes().get(indiceDistMax).getPassagePoint1().getX() - start.getX(), 2) + Math.pow(table.getLignes().get(indiceDistMax).getPassagePoint1().getY() - start.getY(), 2) <= Math.pow(table.getLignes().get(indiceDistMax).getPassagePoint2().getX() - start.getX(), 2) + Math.pow(table.getLignes().get(indiceDistMax).getPassagePoint2().getY() - start.getY(), 2))
				{
					path2.addAll(findPath(end, table.getLignes().get(indiceDistMax).getPassagePoint1(), table));
					path2.addAll(findPath(table.getLignes().get(indiceDistMax).getPassagePoint1(), start, table));
				}
				else
				{
					path2.addAll(findPath(end, table.getLignes().get(indiceDistMax).getPassagePoint2(), table));
					path2.addAll(findPath(table.getLignes().get(indiceDistMax).getPassagePoint2(), start, table));
				}
			}
		}
		//si il n'y a pas de point d'intersection, lie directement le d�but � la fin
		else
		{
			path1.add(end);
			path2.add(start);
		}
		
		//cherche quel chemin est le plus court, et le retourne
		if(path1.getLenght() <= path2.getLenght())
			return path1;
		path2.invert();
		return path2;
	}
}
