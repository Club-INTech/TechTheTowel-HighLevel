package scripts;

import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialConnexionException;
import hook.types.HookFactory;
import robot.Robot;
import smartMath.Vec2;
import strategie.GameState;
import utils.Config;
import utils.Log;

public class GetGlass extends AbstractScript 
{

	public GetGlass(HookFactory hookFactory, Config config, Log log) 
	{
		super(hookFactory, config, log);
		
		//TODO: tableau des versions
	}
	
	public void execute (int id_version, GameState<Robot> stateToConsider, boolean shouldRetryIfBlocke) throws UnableToMoveException, SerialConnexionException
	{
		//TODO le script en lui meme
		//on se tourne vers le goblet
		//on choisit le bras disponible (ici on montre avec le bras gauche)
		//si aucun bras disponible (logiquement l'IA ne devrai pas lancer le script (erreur ?)) on arrete le script
		//on avance en ouvrant le bras gauche (respectivement droit)
		//on se place proche du goblet pour le ramasser
		//on ferme lentement le bras gauche (respectivement droit) pour attraper le goblet
		//on demande si on a bien quelque chose a gauche (respectivement a droite)
		//si on a rien (et que l'autre bras n'est pas occupe) on recule, on ouvre l'autre bras (droit , repectivement gauche), on avance et on ferme le bras droit (respectivement gauche)
		//si on a toujours rien on arrete
		//si on a attrape quelque chose on le dit au robot ainsi que sa position (gauche / droite)
	}

	@Override
	public Vec2 entryPosition(int id)
	{
		//TODO les cinq ? versions
		// TODO un cercle autour du goblet
		return null;
	}

	@Override
	public int remainingScoreOfVersion(int id_version, GameState<?> state) 
	{
		//TODO si le robot est pein a droite et a gauche (on attends les capteurs de sylvain)
		//if (robot.fullRight) 
		//{
		//	if (robot.fullLeft)
		//	{
		//		return 0;
		//	}
		//}
		return 4;
	}

	@Override
	protected void finalise(GameState<?> state) 
	{
		// TODO fermer (ouvrir ?) le bras gauche et droit 
	}
	
	private void eatGlass (boolean isSecondTry , boolean isArmChosenLeft)
	{
		
	}

}
