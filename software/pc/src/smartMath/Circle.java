package smartMath;

/**
 * 
 * @author paul
 *Classe des cercles, utile pour les points d'entree des scripts et peut-etre pour le pathDingDing
 */
public class Circle {

	Vec2 center;
	double ray;
	
	/**
	 * construit un cercle
	 * @param center le centre en mm  pas de virgule
	 * @param ray le rayon en mm, avec virgule
	 */
	public Circle(Vec2 center, double ray) 
	{
		this.center=center;
		this.ray=ray;
	}

	/**
	 * construit un cercle
	 * dans ce constructeur on suppose le rayon nul
	 *
	 * @param center le centre en mm, pas de virgule
	 */
	public Circle(Vec2 center)
	{
		this.center=center;
		this.ray=0;
	}

	/**
	 * 
	 * @return le centre du cercle (position en mm)
	 */
	public Vec2 toVec2()
	{
		return this.center;
	}

	/**
	 * test si le Vec2 est dans le disque
	 * @param point un vec2 a tester
	 * @return vrai si le point est a l'interieur du cercle ou dessus
	 */
	public boolean containDisk(Vec2 point)
	{
		return (point.distance(this.center)<=this.ray);
	}
	
	/**
	 * test si le point appartient au cercle
	 * @param point un Point a tester
	 * @return vrai si le point est sur le cercle
	 */
	public boolean containCircle(Point point)
	{
		double dx=point.x-this.center.x;
		double dy=point.y-this.center.y;
		return (dx*dx+dy*dy)==(ray*ray);
	}
	
	/**
	 * test si le Vec2 appartient au cercle
	 * @param point un Vec2 a tester
	 * @return vrai si le point est sur le cercle
	 */
	public boolean containCircle(Vec2 point)
	{
		double dx=point.x-this.center.x;
		double dy=point.y-this.center.y;
		return (dx*dx+dy*dy)==(ray*ray);
	}
}
