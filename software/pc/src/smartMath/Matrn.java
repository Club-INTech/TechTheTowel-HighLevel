package smartMath;

import exceptions.MatrixException;
import Jama.Matrix;
// TODO: Auto-generated Javadoc

/**
 * Classe de calcul matriciel.
 *
 * @author pf
 * @author clément
 */

public class Matrn {
	
	/** The matrice. */
	public double[][] matrice;
	
	/** The taille. */
	public int[] taille;

	/**
	 * Instantiates a new matrn.
	 *
	 * @param t the t
	 */
	public Matrn(double[][] t)
	{
		matrice = t;
		taille = new int[2];
		taille[0] = t.length ;
		taille[1] = t[0].length;
	}
	
	/**
	 * Instantiates a new matrn.
	 *
	 * @param n the n
	 */
	public Matrn(int n)
	{
		matrice = new double[n][n];
		taille = new int[2];
		taille[0] = n;
		taille[1] = n;
	}
	
	/**
	 * Instantiates a new matrn.
	 *
	 * @param p nombre de lignes
	 * @param n nombre de colonnes
	 */
	public Matrn(int p,int n)
	{
		matrice = new double[p][n];
		taille = new int[2];
		taille[0] = n;
		taille[1] = p;
	}
	
	/**
	 * la matrice aura une taille (p,n) et tous les éléments vaudront valeur.
	 *
	 * @param p the p
	 * @param n the n
	 * @param valeur : la valeur par défaut
	 */
	public Matrn(int p,int n, int valeur)
	{
		matrice = new double[p][n];
		taille = new int[2];
		taille[0] = n;
		taille[1] = p;
		for(int i = 0; i< taille[0]; i++)
		{
			for(int j = 0; j < taille[1]; j++)
			{
				setCoeff(valeur ,i, j);
			}
		}		
	}
	
	/**
	 * Modifie le coeff en (i,j).
	 *
	 * @param coeff the coeff
	 * @param i la ligne
	 * @param j la colonne
	 */
	public void setCoeff(double coeff, int i, int j)
	{
		matrice[i][j] = coeff;
	}
	
	/**
	 * Récupère le coeff de (i,j).
	 *
	 * @param i la ligne
	 * @param j la colonne
	 * @return the coeff
	 */
	public double getCoeff(int i, int j)
	{
		return matrice[i][j];
	}
	
	/**
	 * Gets the nb lignes.
	 *
	 * @return the nb lignes
	 */
	public int getNbLignes()
	{
		return taille[1];
	}

	/**
	 * Gets the nb colonnes.
	 *
	 * @return the nb colonnes
	 */
	public int getNbColonnes()
	{
		return taille[0];
	}

	/**
	 * Additionner_egal.
	 *
	 * @param A the a
	 * @throws MatrixException the matrix exception
	 */
	public void additionner_egal (Matrn A) throws MatrixException
	{	
		if(taille[0] != A.taille[0] || taille[1] != A.taille[1])
			throw new MatrixException();
		for(int i = 0; i < taille[0]; i++)
			for(int j = 0; j < taille[1]; j++)
				 matrice[j][i]= matrice[j][i]+A.matrice[j][i];
	}
	
	/**
	 * Additionner.
	 *
	 * @param A the a
	 * @return the matrn
	 * @throws MatrixException the matrix exception
	 */
	public Matrn additionner (Matrn A) throws MatrixException
	{
		Matrn a = new Matrn(taille[0],taille[1]);
		if(taille[0] != A.taille[0] || taille[1] != A.taille[1])
			throw new MatrixException();
		for(int i = 0; i < taille[0]; i++)
			for(int j = 0; j < taille[1]; j++)
				 a.matrice[j][i]= matrice[j][i]+A.matrice[j][i];
		return a;
	}
	
	/**
	 * Soustraire.
	 *
	 * @param A the a
	 * @return the matrn
	 * @throws MatrixException the matrix exception
	 */
	public Matrn soustraire (Matrn A) throws MatrixException
	{	
		Matrn a = new Matrn(taille[0],taille[1]);
		if(taille[0] != A.taille[0] || taille[1] != A.taille[1])
			throw new MatrixException();
		for(int i = 0; i < taille[0]; i++)
			for(int j = 0; j < taille[1]; j++)
				 a.matrice[j][i]= matrice[j][i] - A.matrice[j][i];
		return a;
	}
	
	/**
	 * Soustraire_egal.
	 *
	 * @param A the a
	 * @throws MatrixException the matrix exception
	 */
	public void soustraire_egal (Matrn A) throws MatrixException
	{	
		if(taille[0] != A.taille[0] || taille[1] != A.taille[1])
			throw new MatrixException();
		for(int i = 0; i < taille[0]; i++)
			for(int j = 0; j < taille[1]; j++)
				 matrice[j][i]= matrice[j][i] - A.matrice[j][i];
	}
	
	/**
	 * Multiplier_egal.
	 *
	 * @param A the a
	 * @throws MatrixException the matrix exception
	 */
	public void multiplier_egal(Matrn A) throws MatrixException
	{//multiplier this. avec A
		if( this.taille[0] != A.taille[1])
			throw new MatrixException();
		Matrn m = new Matrn(taille[0], A.taille[1]);
		for(int i = 0; i< taille[0]; i++)
		{
			for(int j = 0;j < A.taille[1];j++)
			{
				m.matrice[i][j] = 0;
				for(int k = 0; k < taille[1];k++)
				{
					m.matrice[i][j] += matrice[i][k]*A.matrice[k][j];
				}
			}
		}
		this.matrice = m.matrice;
	}
	
	/**
	 * Multiplier.
	 *
	 * @param A the a
	 * @return the matrn
	 * @throws MatrixException the matrix exception
	 */
	public Matrn multiplier(Matrn A) throws MatrixException
	{//multiplier this. avec A
		if( this.taille[0] != A.taille[1])
			throw new MatrixException();
		Matrn m = new Matrn(taille[0], A.taille[1]);
		for(int i = 0; i< taille[0]; i++)
		{
			for(int j = 0;j < A.taille[1];j++)
			{
				m.matrice[i][j] = 0;
				for(int k = 0; k < taille[1];k++)
				{
					m.matrice[i][j] += matrice[i][k]*A.matrice[k][j];
				}
			}
		}
		return m;
	}
	
	
	
	/**
	 * Transpose_egal.
	 *
	 * @throws MatrixException the matrix exception
	 */
	public void transpose_egal() throws MatrixException
	{
		if(taille[0] != taille[1])
			throw new MatrixException();
		for(int i = 0; i < taille[0]; i++)
			for(int j = 0; j < i; j++)
			{
				double tmp = matrice[j][i];
				matrice[j][i] = matrice[i][j];
				matrice[i][j] = tmp;
			}
	}

	/**
	 * Transpose.
	 *
	 * @return the matrn
	 * @throws MatrixException the matrix exception
	 */
	public Matrn transpose() throws MatrixException
	{		
		if(taille[0] != taille[1])
			throw new MatrixException();
		Matrn a = new Matrn(taille[0], taille[1]);
		for(int i = 0; i < taille[0]; i++)
			for(int j = 0; j <= i; j++)
			{
				a.matrice[j][i] = matrice[i][j];
				a.matrice[i][j] = matrice[j][i];				
			}
		return a;
	}

	/**
	 * Inverser.
	 *
	 * @return the matrn
	 */
	public Matrn inverser()
	{
		// TODO : check
		//Il faut impérativement que la matrice soit inversible !!! enfin je vais voir si je peux gérer le cas contraire
		Matrix a = new Matrix(matrice);
		a.inverse();
		Matrn b = new Matrn(a.getArrayCopy());
		return b;
	}
	
	/**
	 * Identiter.
	 *
	 * @param n the n
	 * @return the matrn
	 */
	static public Matrn identiter(int n)
	{
		// TODO : doc
		//nom de la méthode on français, mais ça explique qu'on construit une matice identité de taille n
		
		Matrn ident= new Matrn(n,n,0);
		for(int i = 0; i <n; i++)
		{
			ident.setCoeff(1, i, i);
		}
		return ident;
	}
	
	/**
	 * Clone.
	 *
	 * @param m the m
	 */
	public void clone(Matrn m)
	{
		taille = m.taille.clone();
		matrice = m.matrice.clone();		
	}
	
	/**
	 * Multiplier_scalaire.
	 *
	 * @param a the a
	 */
	public void multiplier_scalaire(double a)
	{
		for(int i = 0; i < taille[0]; i++)
			for(int j = 0; j < taille[1]; j++)
				 matrice[j][i]= a*matrice[j][i];
	}
	
}
	
