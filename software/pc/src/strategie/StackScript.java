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
	public ArrayList<AbstractScript> list;
	private ArrayList<Integer> versionList;
	public int points;
	public double time;
	
	public void add (AbstractScript script, int version, GameState<?> state)
	{
		points += script.remainingScoreOfVersion(version, state);
		time += script.getNoEnemyTime(state);
		list.add(script);
		versionList.add(version);
	}
	public void add (AbstractScript script, int version, GameState<?> state, double addedTime)
	{
		points += script.remainingScoreOfVersion(version, state);
		time += addedTime;
		list.add(script);
		versionList.add(version);
	}
	
	public void execute (GameState<Robot> state, ArrayList<Hook> hooksToConsider) throws UnableToMoveException, SerialConnexionException, PathNotFoundException, SerialFinallyException
	{
		for (int i = 0 ; i < list.size() ; i++)
		{
			list.get(i).goToThenExec(versionList.get(i), state, true, hooksToConsider);
		}
	}
}
