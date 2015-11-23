package smartMath;

/**
 * classe de calculs de geometrie
 * @author Etienne
 *
 */
public class Geometry
{
	/**
	 * retourne vrai si les points a, b, c sont orient�s dans le sens trigonometrique, faux sinon.
	 * @param a
	 * @param b
	 * @param c
	 * @return
	 */
	
	/**
	 * calcule le "vrai" modulo (entre 0 et module) contairement a % qui calcule entre -module et module
	 * @param number le nombre dont on veut calculer le modulo
	 * @param module le module pour le modulo
	 * @return number [module]
	 */
	public static double modulo(double number, double module)
	{
		double modulo = number%module;
		if (modulo<0)
			modulo += module;
		return modulo;
	}
	
	/**
	 * calcule la diference entre deux angles dans un cercle (prends en compte le fait que le cercle soit circulaire)
	 * @param angle1 le premier angle entre 0 et sizeOfCircle
	 * @param angle2 le deuxieme angle entre 0 et sizeOfCircle (on peut echanger angle1 et angle2 sans changer le retour)
	 * @param sizeOfCircle la taille du cercle (2Pi en Radiant 2000Pi en miliRadiant ou 360 en Degre par exemple)
	 * @return angle1 - angle2 dans un espace circulaire, forcement < a sizeOfCircle
	 */
	public static double minusAngle(double angle1, double angle2, double sizeOfCircle)
	{
		double angleMin = Math.min(angle1, angle2);
		double angleMax = Math.max(angle1, angle2);
		return Math.min(angleMax-angleMin, sizeOfCircle-angleMax+angleMin);
	}

	/**
	 * dit si l'angleEnd est plus loin que angleBegin sur le cercle trigo (prends en compte le fait que le cercle soit circulaire)
	 * @param angleBegin l'angle de position initiale (doit etre en Radiant pas milliRadiant)
	 * @param angleEnd l'angle dont on veut savoir si il est plus loin sur le cercle trigo (doit estre en Radiant et pas en milliRadiant)
	 * @return true si angleEnd est plus loin que angleBegin sur le cercle trigo
	 */
	public static boolean isFurtherInTrigoCircle(double angleBegin, double angleEnd) 
	{
		// si on ne passe pas par 2PI alors sera le plus petit (en abs)
		double possibility1 = modulo(angleEnd, 2*Math.PI)-modulo(angleBegin, 2*Math.PI);
		// si on passe par 2PI alors sera le plus petit (en abs)
		double possibility2 = modulo(angleEnd, 2*Math.PI)+2*Math.PI-modulo(angleBegin, 2*Math.PI);
		
		if (Math.abs(possibility1)<Math.abs(possibility2))
			//on ne passe pas par 2PI, on regarde si on tourne dans le sens trigo
			return possibility1>0;
		else
			//on passe par 2PI, on regarde si on tourne dans le sens trigo
			return possibility2>0;
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
}
