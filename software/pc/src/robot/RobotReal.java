package robot;

import robot.cardsWrappers.ActuatorCardWrapper;
import robot.cardsWrappers.SensorsCardWrapper;
import smartMath.Vec2;
import utils.Log;
import utils.Config;
import pathDingDing.PathDingDing;
import utils.Sleep;
import hook.Hook;

import java.util.ArrayList;

import enums.ActuatorOrder;
import enums.SensorNames;
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
	private ActuatorCardWrapper mActuatorCardWrapper;
	private SensorsCardWrapper mSensorsCardWrapper;
	
	/** Système de locomotion a utiliser pour déplacer le robot */
	private Locomotion mLocomotion;

	// Constructeur
	public RobotReal( Locomotion deplacements, ActuatorCardWrapper mActuatorCardWrapper, Config config, Log log, PathDingDing pathDingDing, SensorsCardWrapper mSensorsCardWrapper)
 	{
		super(config, log, pathDingDing);
		this.mSensorsCardWrapper = mSensorsCardWrapper;
		this.mActuatorCardWrapper = mActuatorCardWrapper;
		this.mLocomotion = deplacements;
		updateConfig();
		speed = Speed.BETWEEN_SCRIPTS;		
	}
	
    public void copy(RobotChrono rc)
    {
    	// TODO: vérifier que la copie est faite sur tout ce qu'il y a besoin
        getPosition().copy(rc.position);
        rc.orientation = getOrientation();
    }
    

	@Override
	public void useActuator(ActuatorOrder order, boolean waitForCompletion) throws SerialConnexionException
	{
		mActuatorCardWrapper.useActuator(order);
		
		if(waitForCompletion)
			Sleep.sleep(order.getDuration());
		else 
			Sleep.sleep(100); // On attend 25 ms pour Sylvain T_T
		
	}
	
	@Override
	public Object getSensorValue (SensorNames captor) throws SerialConnexionException
	{
		return mSensorsCardWrapper.getSensorValue(captor);
	}

	
	@Override	
	public void sleep(long duree)
	{
		Sleep.sleep(duree);
	}
	
	/**
	 * Recale le robot pour qu'il sache ou il est sur la table et dans quel sens il se trouve.
	 * La méthode est de le faire pecuter contre les coins de la table, ce qui lui donne des repères.
	 */
	public void recaler()
	{
	    mLocomotion.readjust();
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
		mLocomotion.moveLengthwise(distance, hooksToConsider, expectsWallImpact);
	}	

	/**
	 * ATTENTION, la valeur "mur" est ignorée
	 */
    @Override
    public void turn(double angle, ArrayList<Hook> hooks, boolean mur, boolean isTurnRelative) throws UnableToMoveException
    {
    	if (isTurnRelative)
    		angle += getOrientation();
        mLocomotion.turn(angle, hooks);
    }
    
    @Override
    public void turn(double angle, ArrayList<Hook> hooks, boolean mur) throws UnableToMoveException
    {
        mLocomotion.turn(angle, hooks);
    }
    
    @Override
    public void followPath(ArrayList<Vec2> chemin, ArrayList<Hook> hooks) throws UnableToMoveException
    {
        mLocomotion.followPath(chemin, hooks, DirectionStrategy.getDefaultStrategy());
    }

    @Override
    protected void followPath(ArrayList<Vec2> chemin, ArrayList<Hook> hooks, DirectionStrategy direction) throws UnableToMoveException
    {
        mLocomotion.followPath(chemin, hooks, direction);
    }

    @Override
    public void immobilise()
    {
        mLocomotion.immobilise();
    }
    
	@Override
	public void enableRotationnalFeedbackLoop()
	{
		try
		{
			mLocomotion.enableRotationnalFeedbackLoop();
		}
		catch (SerialConnexionException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void disableTranslationnalFeedbackLoop()
	{
		try
		{
			mLocomotion.disableRotationnalFeedbackLoop();
		}
		catch (SerialConnexionException e)
		{
			e.printStackTrace();
		}
	}
	
	/* 
	 * GETTERS & SETTERS
	 */
	@Override
	public void setPosition(Vec2 position)
	{
	    mLocomotion.setPosition(position);
	}
	
    @Override
	public Vec2 getPosition()
	{
	    return mLocomotion.getPosition();
	}
	
	@Override
	public void setOrientation(double orientation)
	{
	    mLocomotion.setOrientation(orientation);
	}

    @Override
    public double getOrientation()
    {
        return mLocomotion.getOrientation();
    }

	@Override
	public void setLocomotionSpeed(Speed vitesse)
	{
        try {
			mLocomotion.setTranslationnalSpeed(vitesse.PWMTranslation);
	        mLocomotion.setRotationnalSpeed(vitesse.PWMRotation);
		} catch (SerialConnexionException e) {
			e.printStackTrace();
		}
	}

	
}
