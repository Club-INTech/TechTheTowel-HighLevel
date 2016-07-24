package hook;

import strategie.GameState;

/**
 * Classe de callback. Contient la fonction et ses arguments à appeler.
 * @author pf
 */

public class Callback
{

	/** L'évènement a-t-il été réalisé ? */
	private boolean isDone = false;
	
	/** L'évènement ne doit-t-il survenir qu'une unique fois ? */
	private boolean isUnique;
	
	/** le code à éxecuter lors de l'évènement */
	public Executable mExecutable;
	
	private GameState stateToConsider = null;

	/**
	 * Constructeur d'un callback avec 2 paramètres: la méthode et si elle doit être exécutée une seule fois
	 * @param methode
	 * @param unique
	 * @param stateToConsider 
	 */
	public Callback(Executable methode, boolean unique, GameState stateToConsider)
	{
		this.mExecutable = methode;
		this.isUnique = unique;
		this.stateToConsider = stateToConsider;
	}
	
	/**
	 * Constructeur d'un callback avec 1 paramètre, la méthode. Par défaut, celle-ci est exécutée une seule fois.
	 * @param methode
	 */
	public Callback(Executable methode)
	{
		this.mExecutable = methode;
		isUnique = true;
	}
	
	/**
	 * Le callback appelle la méthode, si elle n'est pas unique ou si elle n'est pas déjà faite
	 * @return vrai si le robot a été déplacé/ tourné, faux sinon
	 */
	public boolean call()
	{
		if(!(shouldBeDeleted()))
		{
            isDone = true;
			return mExecutable.execute(stateToConsider);
		}
		return false;
	}
	
	/**
	 * Explique si le Callback devrait être détruit
	 * @return true si le Callback devrait être détruit
	 */
	public boolean shouldBeDeleted()
	{
	    return isUnique && isDone;
	}
	
}
