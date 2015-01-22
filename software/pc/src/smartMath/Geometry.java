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
		
		while (number > module || number<0 )
		{
			if (number<0)
				number += module;
			else
				number -= module;
		}
		return number;
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
}
