package strategie;

import hook.Hook;

import java.util.ArrayList;

import exceptions.PathNotFoundException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialConnexionException;
import exceptions.serial.SerialFinallyException;
import robot.Robot;
import scripts.AbstractScript;

class StackScript 
{
	/**
	 * la pile des scripts
	 */
	public ArrayList<AbstractScript> list;
	/**
	 * la pile des version des scripts
	 */
	private ArrayList<Integer> versionList;
	/**
	 * les points que rapportera cette pile
	 */
	public int points;
	/**
	 * le temps que prendra cette pile
	 */
	public double time;
	
	public StackScript()
	{
		list = new ArrayList<AbstractScript>();
		versionList = new ArrayList<Integer>();
		points = 0;
		time = 0;
	}
	public StackScript(int points, double time)
	{
		list = new ArrayList<AbstractScript>();
		versionList = new ArrayList<Integer>();
		this.points = points;
		this.time = time;
	}
	
	/**
	 * ajoute le script au stack, le temps ajouté est celui sans ennemi.
	 * @param script le script a ajouter
	 * @param version la version du script a ajouter (cette information sera aussi stockee pour etre utilisee plus tard
	 * @param state l'etat de la table dans lequel on veut executer le script
	 */
	public void add (AbstractScript script, int version, GameState<?> state)
	{
		points += script.remainingScoreOfVersion(version, state);
		time += script.getNoEnemyTime(state, 0);
		list.add(script);
		versionList.add(version);
	}
	
	/**
	 * ajoute le script au stack (temps choisit par l'IA
	 * @param script le script a ajouter
	 * @param version la version du script a ajouter (cette information sera aussi stockee pour etre utilisee plus tard
	 * @param state l'etat de la table dans lequel on veut executer le script
	 * @param addedTime le temps que predra le script a s'executer
	 */
	public void add (AbstractScript script, int version, GameState<?> state, double addedTime)
	{
		points += script.remainingScoreOfVersion(version, state);
		time += addedTime;
		list.add(script);
		versionList.add(version);
	}
	/**
	 * ajoute un temps a la pile, utile pour ajouter le temps de deplacement
	 * @param time le temps a ajouter a la pile de scripts
	 */
	public void addTime(double time)
	{
		this.time += time;
	}
	/**
	 * ajoute des points a la pile, utile si on active des hooks lors du deplacement
	 * @param points les points a ajouter
	 */
	public void addPoints(int points)
	{
		this.points += points;
	}
	
	/**
	 * execute la pile de scripts
	 * @param state l'etat de la table dans lequel on execute les scripts
	 * @param hooksToConsider les hooks a executer
	 * @throws UnableToMoveException si le robot est incapable de bouger (bloque)
	 * @throws SerialConnexionException si on arrive pas a communiquer avec l'ARM
	 * @throws PathNotFoundException si le pathfiding ne trouve pas de chemin (surement suite a une  detection de l'ennemi en cours de deplacement)
	 * @throws SerialFinallyException si un des scripts n'a pas reussi a executer son finally
	 */
	public void execute (GameState<Robot> state, ArrayList<Hook> hooksToConsider) throws UnableToMoveException, SerialConnexionException, PathNotFoundException, SerialFinallyException
	{
		for (int i = 0 ; i < list.size() ; i++)
		{
			list.get(i).goToThenExec(versionList.get(i), state, true, hooksToConsider);
		}
	}
}
