package robot.cards.laser;

import exceptions.MatrixException;
import smartMath.Matrn;

/**
 * Filtrage mathématique. Classe en visibilité "friendly"
 * @author pf
 * @author clément
 */


class Kalman {
	/**
	 * Le filtrage de Kalman repose sur la postulat que le bruit s'ajoutant à l'observation est blanc gaussien
	 */
	//les attributs qui ont été mises en public sont utilisées dans FiltrageLaser.java
	public Matrn x;  // vecteur d'observation
	private Matrn p; //incertitude
	public Matrn f;  //vecteur de transition linéaire
	private Matrn h; //matrice d'observation
	private Matrn r; //
	private Matrn q; //
	private Matrn ident;
	public Kalman(Matrn x, Matrn p, Matrn f, Matrn h, Matrn r, Matrn q) 
		{
			
			this.x = x;
			this.p = p;
			this.f = f;
			this.h = h;
			this.r = r;
			this.q = q;
			this.ident = Matrn.identiter(this.x.size[0]);
			
		}
	
		void prediction(Matrn u)
		{
			if (u == null)
			{
				u = new Matrn(this.x.size[0], this.x.size[1], 0);
			}
			try {
				this.x = this.f.multiply(this.x).add(u);
				this.p = this.f.multiply(this.p).multiply(this.f.transpose()).add(this.q);
			} catch (MatrixException e) {
				e.printStackTrace();
			}
			//self.x = (self.F * self.x) + u
			
			/*self.P = self.F * self.P * self.F.transpose() + self.Q*/
			/*
			 * Il y a une histoire d'exception et j'ai la flemme de gérer ça. PF?
			 */
		}
		void prediction()
		{
			prediction(null);
		}
		void measurement(Matrn z)
		{
			Matrn y;
			try {
				y = z.substract(this.h.multiply(this.x));
				Matrn s = this.h.multiply(this.p).multiply(this.h.transpose()).add(this.r);
				Matrn k = this.p.multiply(this.h.transpose()).multiply(s.invert());
				//k permet de corriger l'état prédit : c'est le degré de sérieux à accorder à 
				this.x.add_equal(k.multiply(y));
				this.p = (this.ident.substract(k.multiply(this.h))).multiply(this.p);
			} catch (MatrixException e) {
				e.printStackTrace();
			}
		}
		void filter(Matrn z,Matrn u)
		{
			prediction(u);
			measurement(z);			
		}		
}
