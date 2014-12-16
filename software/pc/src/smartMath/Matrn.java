package smartMath;

import exceptions.MatrixException;
import Jama.Matrix;
// TODO: Auto-generated Javadoc

/**
 * Classe de calcul matriciel.
 *
 * @author pf
 * @author clément
 * @author marsu
 */

public class Matrn
{

	/** coefficients de la matrice */
	public double[][] data;
	
	/** couple de valeurs donnant la taille de la matrice. size[0] = nombre de lignes. size[1] = nombre de colones */
	public int[] size;

	/**
	 * Instantiates a new matrix.
	 *
	 * @param requestedDatas les coefficients de la matrice a créer
	 */
	public Matrn(double[][] requestedDatas)
	{
		data = requestedDatas;
		size = new int[2];
		size[0] = requestedDatas.length ;
		size[1] = requestedDatas[0].length;
	}
	
	/**
	 * Instantie une nouvelle matrice.
	 *
	 * @param n nombre de lignes et de colones que la nouvelle matrice aura
	 */
	public Matrn(int n)
	{
		data = new double[n][n];
		size = new int[2];
		size[0] = n;
		size[1] = n;
	}
	
	/**
	 * Instancie une nouvelle matrice.
	 *
	 * @param p nombre de lignes
	 * @param n nombre de colonnes
	 */
	public Matrn(int p,int n)
	{
		data = new double[p][n];
		size = new int[2];
		size[0] = n;
		size[1] = p;
	}
	
	/**
	 * Instancie une nouvelle matrice.
	 * la matrice aura une taille (p,n) et tous les éléments vaudront valeur.
	 *
	 * @param p nombre de lignes que la nouvelle matrice aura
	 * @param n nombre de collones que la nouvelle matrice aura
	 * @param value la valeur par défaut que tout les coefficients de la nouvelle matrice aura
	 */
	public Matrn(int p,int n, int value)
	{
		data = new double[p][n];
		size = new int[2];
		size[0] = n;
		size[1] = p;
		for(int i = 0; i< size[0]; i++)
		{
			for(int j = 0; j < size[1]; j++)
			{
				setCoeff(value ,i, j);
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
		data[i][j] = coeff;
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
		return data[i][j];
	}
	
	/**
	 * Gets the nb lignes.
	 *
	 * @return the nb lignes
	 */
	public int getNbLines()
	{
		return size[1];
	}

	/**
	 * Gets the nb colonnes.
	 *
	 * @return the nb colonnes
	 */
	public int getNbRows()
	{
		return size[0];
	}

	/**
	 * Additionner_egal.
	 *
	 * @param A la matrice qu'on veut additionner à l'objet
	 * @throws MatrixException the matrix exception
	 */
	public void add_equal (Matrn A) throws MatrixException
	{	
		if(size[0] != A.size[0] || size[1] != A.size[1])
			throw new MatrixException();
		for(int i = 0; i < size[0]; i++)
			for(int j = 0; j < size[1]; j++)
				 data[j][i]= data[j][i]+A.data[j][i];
	}
	
	/**
	 * Additionner.
	 *
	 * @param A une matrice
	 * @return the matrn
	 * @throws MatrixException the matrix exception
	 */
	public Matrn add (Matrn A) throws MatrixException
	{
		Matrn a = new Matrn(size[0],size[1]);
		if(size[0] != A.size[0] || size[1] != A.size[1])
			throw new MatrixException();
		for(int i = 0; i < size[0]; i++)
			for(int j = 0; j < size[1]; j++)
				 a.data[j][i]= data[j][i]+A.data[j][i];
		return a;
	}
	
	/**
	 * Soustraire.
	 *
	 * @param A une matrice
	 * @return the matrn
	 * @throws MatrixException the matrix exception
	 */
	public Matrn substract (Matrn A) throws MatrixException
	{	
		Matrn a = new Matrn(size[0],size[1]);
		if(size[0] != A.size[0] || size[1] != A.size[1])
			throw new MatrixException();
		for(int i = 0; i < size[0]; i++)
			for(int j = 0; j < size[1]; j++)
				 a.data[j][i]= data[j][i] - A.data[j][i];
		return a;
	}
	
	/**
	 * Soustraire_egal.
	 *
	 * @param A une matrice
	 * @throws MatrixException the matrix exception
	 */
	public void substract_equal (Matrn A) throws MatrixException
	{	
		if(size[0] != A.size[0] || size[1] != A.size[1])
			throw new MatrixException();
		for(int i = 0; i < size[0]; i++)
			for(int j = 0; j < size[1]; j++)
				 data[j][i]= data[j][i] - A.data[j][i];
	}
	
	/**
	 * Multiplier_egal.
	 *
	 * @param A une matrice
	 * @throws MatrixException the matrix exception
	 */
	public void multiply_equal(Matrn A) throws MatrixException
	{//multiplier this. avec A
		if( this.size[0] != A.size[1])
			throw new MatrixException();
		Matrn m = new Matrn(size[0], A.size[1]);
		for(int i = 0; i< size[0]; i++)
		{
			for(int j = 0;j < A.size[1];j++)
			{
				m.data[i][j] = 0;
				for(int k = 0; k < size[1];k++)
				{
					m.data[i][j] += data[i][k]*A.data[k][j];
				}
			}
		}
		this.data = m.data;
	}
	
	/**
	 * Multiplier.
	 *
	 * @param A the a
	 * @return the matrn
	 * @throws MatrixException the matrix exception
	 */
	public Matrn multiply(Matrn A) throws MatrixException
	{//multiplier this. avec A
		if( this.size[0] != A.size[1])
			throw new MatrixException();
		Matrn m = new Matrn(size[0], A.size[1]);
		for(int i = 0; i< size[0]; i++)
		{
			for(int j = 0;j < A.size[1];j++)
			{
				m.data[i][j] = 0;
				for(int k = 0; k < size[1];k++)
				{
					m.data[i][j] += data[i][k]*A.data[k][j];
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
	public void transpose_equal() throws MatrixException
	{
		if(size[0] != size[1])
			throw new MatrixException();
		for(int i = 0; i < size[0]; i++)
			for(int j = 0; j < i; j++)
			{
				double tmp = data[j][i];
				data[j][i] = data[i][j];
				data[i][j] = tmp;
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
		if(size[0] != size[1])
			throw new MatrixException();
		Matrn a = new Matrn(size[0], size[1]);
		for(int i = 0; i < size[0]; i++)
			for(int j = 0; j <= i; j++)
			{
				a.data[j][i] = data[i][j];
				a.data[i][j] = data[j][i];				
			}
		return a;
	}

	/**
	 * Inverser.
	 *
	 * @return the matrn
	 */
	public Matrn invert()
	{
		// TODO : check
		//Il faut impérativement que la matrice soit inversible !!! enfin je vais voir si je peux gérer le cas contraire
		Matrix a = new Matrix(data);
		a.inverse();
		Matrn b = new Matrn(a.getArrayCopy());
		return b;
	}
	
	/**
	 * Identiter.
	 * 
	 *
	 * @param n un entier 
	 * @return la matrice identité
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
	 * @param m une matrice
	 */
	public void clone(Matrn m)
	{
		size = m.size.clone();
		data = m.data.clone();		
	}
	
	/**
	 * Multiplier_scalaire.
	 *
	 * @param a un double
	 */
	public void multiply_scalar(double a)
	{
		for(int i = 0; i < size[0]; i++)
			for(int j = 0; j < size[1]; j++)
				 data[j][i]= a*data[j][i];
	}
	
}
	
