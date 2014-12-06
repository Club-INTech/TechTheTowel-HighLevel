package robot;

import smartMath.Vec2;
import utils.Log;
import utils.Config;
import utils.Sleep;
import hook.Hook;

import java.util.ArrayList;

import enums.Speed;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialConnexionException;

/**
 * Effectue le lien entre le code et la réalité (permet de parler aux actionneurs, d'interroger les capteurs, etc.)
 * @author pf, marsu
 *
 */

public class RobotReal extends Robot
{
	private Locomotion deplacements;

	// Constructeur
	public RobotReal( Locomotion deplacements, Config config, Log log)
 	{
		super(config, log);
		this.deplacements = deplacements;
		updateConfig();
		speed = Speed.BETWEEN_SCRIPTS;		
	}
	
	/*
	 * MÉTHODES PUBLIQUES
	 */
	
	public void updateConfig()
	{
		super.updateConfig();
	}
	
	
	public void enableRotationnalFeedbackLoop()
	{
		try
		{
			deplacements.getLocomotionCardWrapper().disableRotationnalFeedbackLoop();
		}
		catch (SerialConnexionException e)
		{
			e.printStackTrace();
		}
	}

	public void disableTranslationnalFeedbackLoop()
	{
		try
		{
			deplacements.getLocomotionCardWrapper().enableRotationnalFeedbackLoop();
		}
		catch (SerialConnexionException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Recale le robot pour qu'il sache ou il est sur la table et dans quel sens il se trouve.
	 * La méthode est de le faire pecuter contre les coins de la table, ce qui lui donne des repères.
	 */
	public void recaler()
	{
	    deplacements.readjust();
	}
	
	/**
	 * Fait avancer le robot de la distance spécifiée. Le robot garde son orientation actuelle et va simplement avancer
	 * Cette méthode est bloquante: son exécution ne se termine que lorsque le robot a atteint le point d'arrivée
	 * @param distance en mm que le robot doit franchir
	 * @param hooksToConsider hooks a considérer lors de ce déplacement. Le hook n'est déclenché que s'il est dans cette liste et que sa condition d'activation est remplie	 
	 * @param expectsWallImpact true si le robot doit s'attendre a percuter un mur au cours du déplacement. false si la route est sensée être dégagée.
	 * @throws UnableToMoveException losrque quelque chose sur le chemin cloche et que le robot ne peut s'en défaire simplement: bloquage mécanique immobilisant le robot ou obstacle percu par les capteurs
	 */
	@Override
    public void moveLengthwise(int distance, ArrayList<Hook> hooksToConsider, boolean expectsWallImpact) throws UnableToMoveException
	{
		deplacements.moveLengthwise(distance, hooksToConsider, expectsWallImpact);
	}	


	@Override	
	public void sleep(long duree)
	{
		Sleep.sleep(duree);
	}

    @Override
    public void immobilise()
    {
        deplacements.immobilise();
    }

    @Override
    public void turn(double angle, ArrayList<Hook> hooks, boolean mur) throws UnableToMoveException
    {
        deplacements.turn(angle, hooks, mur);
    }
    
    @Override
    public void followPath(ArrayList<Vec2> chemin, ArrayList<Hook> hooks) throws UnableToMoveException
    {
        deplacements.followPath(chemin, hooks);
    }

    public void copy(RobotChrono rc)
    {
    	// TODO
        getPositionFast().copy(rc.position);
        rc.orientation = getOrientationFast();
    }
    
	/*
	 * ACTIONNEURS
	 */
	
	
	// TODO: mettre les fonctions actionnants les actionneurs ici ( chaque méthode fera appel au wrapper de la carte actionneur)
	
	
	/* 
	 * GETTERS & SETTERS
	 */
	@Override
	public void setPosition(Vec2 position)
	{
	    deplacements.setPosition(position);
	}
	
    @Override
	public Vec2 getPosition()
	{
	    return deplacements.getPosition();
	}

	@Override
	public void setOrientation(double orientation)
	{
	    deplacements.setOrientation(orientation);
	}

    @Override
    public double getOrientation()
    {
        return deplacements.getOrientation();
    }

	/**
	 * Modifie la vitesse de translation
	 * @param Speed : l'une des vitesses indexées dans enums.
	 * 
	 */
	@Override
	public void set_vitesse(Speed vitesse)
	{
        deplacements.setTranslationnalSpeed(vitesse.PWMTranslation);
        deplacements.setRotationnalSpeed(vitesse.PWMRotation);
		log.debug("Modification de la vitesse: "+vitesse, this);
	}
    
	@Override
	public Vec2 getPositionFast()
	{
		return deplacements.getPositionFast();
	}

	@Override
	public double getOrientationFast()
	{
		return deplacements.getOrientationFast();
	}
}
