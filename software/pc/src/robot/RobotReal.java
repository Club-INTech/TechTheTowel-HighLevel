package robot;

import enums.*;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialConnexionException;
import hook.Hook;
import pathDingDing.PathDingDing;
import robot.cardsWrappers.ActuatorCardWrapper;
import robot.cardsWrappers.SensorsCardWrapper;
import smartMath.Arc;
import smartMath.Vec2;
import utils.Config;
import utils.Log;
import utils.Sleep;

import java.util.ArrayList;

/**
 * Effectue le lien entre le code et la réalité (permet de parler aux actionneurs, d'interroger les capteurs, etc.)
 * @author pf, marsu
 *
 */
public class RobotReal extends Robot
{
	private ActuatorCardWrapper mActuatorCardWrapper;
	private SensorsCardWrapper mSensorsCardWrapper;
	
	private SymmetrizedActuatorOrderMap mActuatorCorrespondenceMap = new SymmetrizedActuatorOrderMap();
	private SymmetrizedTurningStrategy mTurningStrategyCorrespondenceMap = new SymmetrizedTurningStrategy();
	private SymmetrizedSensorNamesMap mSensorNamesMap = new SymmetrizedSensorNamesMap();
	/** Système de locomotion a utiliser pour déplacer le robot */
	private Locomotion mLocomotion;
	
	
	// Constructeur
	public RobotReal( Locomotion deplacements, ActuatorCardWrapper mActuatorCardWrapper, Config config, Log log, PathDingDing pathDingDing, SensorsCardWrapper mSensorsCardWrapper) {
        super(config, log, pathDingDing);
        this.mSensorsCardWrapper = mSensorsCardWrapper;
        this.mActuatorCardWrapper = mActuatorCardWrapper;
        this.mLocomotion = deplacements;
        updateConfig();
        speed = Speed.SLOW_ALL;
    }


	@Override
	public void useActuator(ActuatorOrder order, boolean waitForCompletion) throws SerialConnexionException
	{
		//redondance avec useActuator qui log.debug deja
		//log.debug("appel de RobotReal.useActuator(" + order + "," + waitForCompletion + ")", this);
        int door = (order == ActuatorOrder.OPEN_DOOR ? 2 : 0) + (order == ActuatorOrder.CLOSE_DOOR ? 1 : 0);
		if(symmetry)
			order = mActuatorCorrespondenceMap.getSymmetrizedActuatorOrder(order);
		mActuatorCardWrapper.useActuator(order);

        if(waitForCompletion && door == 1)
        {
            while(!getContactSensorValue(ContactSensors.DOOR_CLOSED) && !getContactSensorValue(ContactSensors.DOOR_BLOCKED))
			{
				try
                {
					Thread.sleep(100);
				}
				catch (Exception e)
				{

				}
			}
        }
        else if(waitForCompletion && door == 2)
        {
            long time = System.currentTimeMillis();
            while(!getContactSensorValue(ContactSensors.DOOR_OPENED) && !getContactSensorValue(ContactSensors.DOOR_BLOCKED))
			{
				try
				{
					Thread.sleep(100);
				}
				catch (Exception e)
				{

				}
			}
        }
		else if(waitForCompletion)
		{
			sleep(order.getDuration());
		}

		//=============================
		// Gestion pourrave des portes
		//=============================

		if(waitForCompletion && door != 0 && getContactSensorValue(ContactSensors.DOOR_OPENED))
			doorIsOpen = true;
		else if(waitForCompletion && door != 0 && getContactSensorValue(ContactSensors.DOOR_CLOSED))
			doorIsOpen = false;
		else if(waitForCompletion && door != 0)
			doorIsOpen = true; //Cas pourri où la porte s'est bloquée
		else if(!waitForCompletion && door != 0)
			doorIsOpen = (door == 2);

	}
	
	@Override
	public ArrayList<Integer> getUSSensorValue (USsensors sensor) throws SerialConnexionException
	{

		// si il n'y a pas de symétrie, on renvoie la valeur brute du bas niveau
		if(!symmetry) 
			return mSensorsCardWrapper.getUSSensorValue(sensor);
		else
		{
			//TODO symetriser le capteur gauche/droite
			
			/* attention si les capteurs sont en int[] il faut symétriser ce int[] */
			
			return mSensorsCardWrapper.getUSSensorValue(sensor);
		}
	}
	
	@Override
	public boolean getContactSensorValue (ContactSensors sensor) throws SerialConnexionException
	{
		// si il n'y a pas de symétrie, on renvoie la valeur brute du bas niveau
				if(!symmetry) 
					return mSensorsCardWrapper.getContactSensorValue(sensor);
				else
				{
					sensor = mSensorNamesMap.getSymmetrizedContactSensorName(sensor);
					
					/* attention si les capteurs sont en int[] il faut symétriser ce int[] */
					
					return mSensorsCardWrapper.getContactSensorValue(sensor);
				}
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
		log.debug("appel de RobotReal.moveLengthwise(" + distance + "," + hooksToConsider + "," + expectsWallImpact + ")");
		moveLengthwise(distance, hooksToConsider, expectsWallImpact, true);
	}

    /**
     * Ordonne le déplacement selon un arc
     * @param arc l'arc
     * @param hooks les hooks à gérer
     */
	public void moveArc(Arc arc, ArrayList<Hook> hooks) throws UnableToMoveException
	{
		mLocomotion.moveArc(arc,hooks, true, false);
	}

	@Override
	public void moveArcNoDetectionWhileTurning(Arc arc, ArrayList<Hook> hooks) throws UnableToMoveException {
		mLocomotion.moveArc(arc, hooks, false, false);
	}

	@Override
	public void moveArc(Arc arc, ArrayList<Hook> hooks, boolean expectedWallImpact) throws UnableToMoveException {
		mLocomotion.moveArc(arc,hooks, false, expectedWallImpact);

	}

	@Override
	public void moveArcNoDetectionWhileTurning(Arc arc, ArrayList<Hook> hooks, boolean expectedWallImpact) throws UnableToMoveException {
		mLocomotion.moveArc(arc,hooks, false, expectedWallImpact);
	}

	@Override
    public void setBasicDetection(boolean basicDetection)
    {
        mLocomotion.setBasicDetection(basicDetection);
    }

    @Override
	public void setUSvalues(ArrayList<Integer> val)
	{
		mLocomotion.setUSvalues(val);
	}

	@Override
    public void moveLengthwiseWithoutDetection(int distance, ArrayList<Hook> hooksToConsider, boolean expectsWallImpact) throws UnableToMoveException
	{	
		log.debug("appel de RobotReal.moveLengthwiseWithoutDetection(" + distance + "," + hooksToConsider + "," + expectsWallImpact + ")");
		Speed newSpeed = Speed.SLOW_ALL;
		/*
    	if (distance<150)
    		newSpeed = Speed.SLOW;
    	else if (distance <1000)
    		newSpeed = Speed.BETWEEN_SCRIPTS_SLOW;
    	else
    		newSpeed = Speed.BETWEEN_SCRIPTS;
    		*/
    	
		moveLengthwise(distance, hooksToConsider, expectsWallImpact, false, newSpeed);
	}	
	
	/**
	 * Fait avancer le robot de la distance spécifiée. Le robot garde son orientation actuelle et va simplement avancer
	 * Cette méthode est bloquante: son exécution ne se termine que lorsque le robot a atteint le point d'arrivée
	 * @param distance en mm que le robot doit franchir
	 * @param hooksToConsider hooks a considérer lors de ce déplacement. Le hook n'est déclenché que s'il est dans cette liste et que sa condition d'activation est remplie	 
	 * @param expectsWallImpact true si le robot doit s'attendre a percuter un mur au cours du déplacement. false si la route est sensée être dégagée.
	 * @param mustDetect vrai si le robot doit detecter les obstacles sur son chemin
	 * @throws UnableToMoveException losrque quelque chose sur le chemin cloche et que le robot ne peut s'en défaire simplement: bloquage mécanique immobilisant le robot ou obstacle percu par les capteurs
	 */
	@Override
	 public void moveLengthwise(int distance, ArrayList<Hook> hooksToConsider, boolean expectsWallImpact, Boolean mustDetect) throws UnableToMoveException
	{	
		log.debug("appel de RobotReal.moveLengthwise(" + distance + "," + hooksToConsider + "," + expectsWallImpact + "," + mustDetect + ")");
		Speed newSpeed = Speed.SLOW_ALL;
		/*
    	if (distance<150)
    		newSpeed = Speed.SLOW;
    	else if (distance <1000)
    		newSpeed = Speed.BETWEEN_SCRIPTS_SLOW;
    	else
    		newSpeed = Speed.BETWEEN_SCRIPTS;
    		*/
    	
		moveLengthwise(distance, hooksToConsider, expectsWallImpact, mustDetect, newSpeed);
	}	

	 
	/**
	 * Fait avancer le robot de la distance spécifiée. Le robot garde son orientation actuelle et va simplement avancer
	 * Cette méthode est bloquante: son exécution ne se termine que lorsque le robot a atteint le point d'arrivée
	 * @param distance en mm que le robot doit franchir
	 * @param hooksToConsider hooks a considérer lors de ce déplacement. Le hook n'est déclenché que s'il est dans cette liste et que sa condition d'activation est remplie	 
	 * @param expectsWallImpact true si le robot doit s'attendre a percuter un mur au cours du déplacement. false si la route est sensée être dégagée.
	 * @param mustDetect vrai si le robot doit detecter les obstacles sur son chemin
	 * @throws UnableToMoveException losrque quelque chose sur le chemin cloche et que le robot ne peut s'en défaire simplement: bloquage mécanique immobilisant le robot ou obstacle percu par les capteurs
	 */
	@Override
	 public void moveLengthwise(int distance, ArrayList<Hook> hooksToConsider, boolean expectsWallImpact, Boolean mustDetect, Speed newSpeed) throws UnableToMoveException
	{	
		log.debug("appel de RobotReal.moveLengthwise(" + distance + "," + hooksToConsider + "," + expectsWallImpact + "," + mustDetect + "," + newSpeed + ")");
		Speed oldSpeed = speed;
		speed = newSpeed;
		mLocomotion.moveLengthwise(distance, hooksToConsider, expectsWallImpact, mustDetect);
		speed = oldSpeed;
	}	

	/**
	 * ATTENTION, la valeur "mur" est ignorée
	 */
    @Override
    public void turn(double angle, ArrayList<Hook> hooksToConsider, boolean expectsWallImpact, boolean isTurnRelative) throws UnableToMoveException
    {
		log.debug("appel de RobotReal.turn(" + angle + "," + hooksToConsider + "," + expectsWallImpact + "," + isTurnRelative + ")");
    	if (isTurnRelative)
    		angle += getOrientation();
        turn(angle, hooksToConsider);
    }
    
    @Override
    public void turnWithoutDetection(double angle, ArrayList<Hook> hooksToConsider) throws UnableToMoveException
    {
		log.debug("appel de RobotReal.turn(" + angle + "," + hooksToConsider + ")");
    	try
    	{
    		mLocomotion.turn(angle, hooksToConsider, false);
    	}
    	catch (UnableToMoveException e)
    	{
			log.critical( e.logStack());
    		throw e;
    	}
    }

	@Override
	public void setForceMovement(boolean state)
	{
		try {
			mLocomotion.setForceMovement(state);
		} catch (SerialConnexionException e) {
			e.printStackTrace();
			log.critical("Erreur critique série : Forcing non changé !");
			return;
		}
		this.isForcing = true;
	}

	@Override
	public void setSmoothAcceleration(boolean state) throws SerialConnexionException
	{
		this.mLocomotion.setSmoothAcceleration(state);
	}

	@Override
    public void turn(double angle, ArrayList<Hook> hooksToConsider, boolean expectsWallImpact) throws UnableToMoveException
    {
		log.debug("appel de RobotReal.turn(" + angle + "," + hooksToConsider + "," + expectsWallImpact + ")");
    	try
    	{
    		turn(angle, hooksToConsider);
    	}
    	catch (UnableToMoveException e)
    	{
			log.critical( e.logStack());
            throw e;
    	}
    }
    
    public void turn(double angle, ArrayList<Hook> hooksToConsider) throws UnableToMoveException
    {
		log.debug("appel de RobotReal.turn(" + angle + "," + hooksToConsider + ")");
    	try
    	{
    		mLocomotion.turn(angle, hooksToConsider);
    	}
    	catch (UnableToMoveException e)
    	{
			log.critical( e.logStack());
            throw e;
    	}// le robot s'est arreté de tourner qu'il y ait catch ou non.
    }

    
    @SuppressWarnings("unchecked")
	@Override
    public void followPath(ArrayList<Vec2> chemin, ArrayList<Hook> hooksToConsider) throws UnableToMoveException
    {
    	cheminSuivi = (ArrayList<Vec2>) chemin.clone();
        mLocomotion.followPath(chemin, hooksToConsider, DirectionStrategy.getDefaultStrategy());
    }
    

    @SuppressWarnings("unchecked")
	@Override
    protected void followPath(ArrayList<Vec2> chemin, ArrayList<Hook> hooksToConsider, DirectionStrategy direction) throws UnableToMoveException
    {
    	cheminSuivi = (ArrayList<Vec2>) chemin.clone();
        mLocomotion.followPath(chemin, hooksToConsider, direction);
    }

    @Override
    public void immobilise()
    {
		log.debug("appel de RobotReal.immobilise()");
        mLocomotion.immobilise();
    }
    
	@Override
	public void enableRotationnalFeedbackLoop()
	{
		log.debug("appel de RobotReal.enableRotationnalFeedbackLoop()");
		try
		{
			mLocomotion.enableRotationnalFeedbackLoop();
		}
		catch (SerialConnexionException e)
		{
			log.critical( e.logStack());
		}
	}

	@Override
	public void disableRotationnalFeedbackLoop()
	{
		log.debug("appel de RobotReal.disableRotationnalFeedbackLoop()");
		try
		{
			mLocomotion.disableRotationnalFeedbackLoop();
		}
		catch (SerialConnexionException e)
		{
			log.critical( e.logStack());
		}
	}
	
	@Override
	public void enableFeedbackLoop() throws SerialConnexionException
	{
		mLocomotion.enableFeedbackLoop();		
	}

	@Override
	public void disableFeedbackLoop() throws SerialConnexionException
	{
		mLocomotion.disableFeedbackLoop();
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
    	position = mLocomotion.getPosition();
	    return position;
	}
	
	@Override
	public void setOrientation(double orientation)
	{
	    mLocomotion.setOrientation(orientation);
	}

    @Override
    public double getOrientation()
    {
    	orientation =  mLocomotion.getOrientation();
        return orientation;
    }

	@Override
	public boolean setTurningStrategy(TurningStrategy turning)
	{
        if(((getIsSandInside()||shellsOnBoard) && !(turning == TurningStrategy.FASTEST)) || (!getIsSandInside() && !shellsOnBoard))
        {
			if(symmetry)
			{
				mLocomotion.setTurningOrders(mTurningStrategyCorrespondenceMap.getSymmetrizedTurningStrategy(turning));
				return true;
			}
            mLocomotion.setTurningOrders(turning);
            return true;
        }
        return false;
	}
	
	@Override
	public boolean setDirectionStrategy(DirectionStrategy motion)
	{
        if(((getIsSandInside()||shellsOnBoard) && !(motion == DirectionStrategy.FASTEST)) || (!getIsSandInside() && !shellsOnBoard))
		{
			mLocomotion.setDirectionOrders(motion);
			return true;
		}
		return false;
	}

	@Override
	public void setLocomotionSpeed(Speed vitesse)
	{
        try
        {
			mLocomotion.setTranslationnalSpeed(vitesse.translationSpeed);
	        mLocomotion.setRotationnalSpeed(vitesse.rotationSpeed);
	        
	        speed = vitesse;
		} 
        catch (SerialConnexionException e)
        {
			log.critical( e.logStack());
		}
	}
	

	@Override
	public Speed getLocomotionSpeed()
	{
		return speed;
	}
	
	public boolean getIsRobotTurning()
	{
		return mLocomotion.isRobotTurning;
	}
	
	public boolean getIsRobotMovingForward()
	{
		return mLocomotion.isRobotMovingForward;
	}
	
	public boolean getIsRobotMovingBackward()
	{
		return mLocomotion.isRobotMovingBackward;
	}


}
