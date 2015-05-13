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
import enums.SymmetrizedActuatorOrderMap;
import enums.SymmetrizedSensorNamesMap;
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
	
	private SymmetrizedActuatorOrderMap mActuatorCorrespondenceMap = new SymmetrizedActuatorOrderMap();
	private SymmetrizedSensorNamesMap mSensorCorrespondenceMap = new SymmetrizedSensorNamesMap();
	
	
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
		speed = Speed.SLOW;		
	}
	
    public void copy(RobotChrono rc)
    {
    	// TODO: vérifier que la copie est faite sur tout ce qu'il y a besoin
        getPosition().copy(rc.position);
        rc.speed=speed;
        rc.isBallStored=isBallStored;
        rc.isGlassStoredLeft=isGlassStoredLeft;
        rc.isGlassStoredRight=isGlassStoredRight;
        rc.orientation = getOrientation();
    }
    

	@Override
	public void useActuator(ActuatorOrder order, boolean waitForCompletion) throws SerialConnexionException
	{
		//redondance avec useActuator qui log.debug deja
		//log.debug("appel de RobotReal.useActuator(" + order + "," + waitForCompletion + ")", this);
		if(symmetry)
			order = mActuatorCorrespondenceMap.getSymmetrizedActuatorOrder(order);
		mActuatorCardWrapper.useActuator(order);
		
		if(waitForCompletion)
			sleep(order.getDuration());
	}
	
	@Override
	public Object getSensorValue (SensorNames sensor) throws SerialConnexionException
	{

		// si il n'y a pas de symétrie, on renvoie la valeur brute du bas niveau
		if(!symmetry) 
			return mSensorsCardWrapper.getSensorValue(sensor);
		else
		{
			// on regarde les capteurs symetrisés (ex : contacteur du bras droit / gauche)
			sensor = mSensorCorrespondenceMap.getSymmetrizedSensorName(sensor);

			// capteurs ultrasons
			// on échange les valeurs des ultrasons (l'ultrason droit devien l'ultrason gauche et réciproquement)
			if(sensor == SensorNames.ULTRASOUND_FRONT_SENSOR || sensor == SensorNames.ULTRASOUND_FRONT_SENSOR)
			{
				 // On intervertit droite et gauche pour les capteurs	
				int[] invertedOut = (int[])mSensorsCardWrapper.getSensorValue(sensor);
				int[] out = new int[2];
				out[0] = invertedOut[1];
				return out;					
			}
			else // contacteurs
			{
				return mSensorsCardWrapper.getSensorValue(sensor);
			}
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
		log.debug("appel de RobotReal.moveLengthwise(" + distance + "," + hooksToConsider + "," + expectsWallImpact + ")", this);
		moveLengthwise(distance, hooksToConsider, expectsWallImpact, true);
	}	
	
	@Override
    public void moveLengthwiseWithoutDetection(int distance, ArrayList<Hook> hooksToConsider, boolean expectsWallImpact) throws UnableToMoveException
	{	
		log.debug("appel de RobotReal.moveLengthwiseWithoutDetection(" + distance + "," + hooksToConsider + "," + expectsWallImpact + ")", this);
		Speed newSpeed = Speed.SLOW;
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
		log.debug("appel de RobotReal.moveLengthwise(" + distance + "," + hooksToConsider + "," + expectsWallImpact + "," + mustDetect + ")", this);
		Speed newSpeed = Speed.SLOW;
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
	 * @param speed la vitesse du robot lors de son parcours
	 * @throws UnableToMoveException losrque quelque chose sur le chemin cloche et que le robot ne peut s'en défaire simplement: bloquage mécanique immobilisant le robot ou obstacle percu par les capteurs
	 */
	@Override
	 public void moveLengthwise(int distance, ArrayList<Hook> hooksToConsider, boolean expectsWallImpact, Boolean mustDetect, Speed newSpeed) throws UnableToMoveException
	{	
		log.debug("appel de RobotReal.moveLengthwise(" + distance + "," + hooksToConsider + "," + expectsWallImpact + "," + mustDetect + "," + newSpeed + ")", this);
		Speed oldSpeed = speed;
		speed = newSpeed;
		mLocomotion.moveLengthwise(distance, hooksToConsider, expectsWallImpact, mustDetect);
		speed = oldSpeed;
	}	

	/* TODO nexiste pas ?
	@Override
    public void moveTowardEnnemy(int distance, ArrayList<Hook> hooksToConsider) throws UnableToMoveException, BlockedException, UnexpectedObstacleOnPathException
	{	
		
		mLocomotion.moveTowardEnnemy(distance, hooksToConsider);
	}
	*/
	


	/**
	 * ATTENTION, la valeur "mur" est ignorée
	 */
    @Override
    public void turn(double angle, ArrayList<Hook> hooksToConsider, boolean expectsWallImpact, boolean isTurnRelative) throws UnableToMoveException
    {
		log.debug("appel de RobotReal.turn(" + angle + "," + hooksToConsider + "," + expectsWallImpact + "," + isTurnRelative + ")", this);
    	if (isTurnRelative)
    		angle += getOrientation();
        turn(angle, hooksToConsider);
    }
    
    @Override
    public void turnWithoutDetection(double angle, ArrayList<Hook> hooksToConsider) throws UnableToMoveException
    {
		log.debug("appel de RobotReal.turn(" + angle + "," + hooksToConsider + ")", this);
    	try
    	{
    		mLocomotion.turn(angle, hooksToConsider, false);
    	}
    	catch (UnableToMoveException e)
    	{
			log.critical( e.logStack(), this);
    		throw e;
    	}
    }
    
    @Override
    public void turn(double angle, ArrayList<Hook> hooksToConsider, boolean expectsWallImpact) throws UnableToMoveException
    {
		log.debug("appel de RobotReal.turn(" + angle + "," + hooksToConsider + "," + expectsWallImpact + ")", this);
    	try
    	{
    		turn(angle, hooksToConsider);
    	}
    	catch (UnableToMoveException e)
    	{
			log.critical( e.logStack(), this);
            throw e;
    	}
    }
    
    public void turn(double angle, ArrayList<Hook> hooksToConsider) throws UnableToMoveException
    {
		log.debug("appel de RobotReal.turn(" + angle + "," + hooksToConsider + ")", this);
    	try
    	{
    		mLocomotion.turn(angle, hooksToConsider);
    	}
    	catch (UnableToMoveException e)
    	{
			log.critical( e.logStack(), this);
            throw e;
    	}// le robot s'est arreté de tourner qu'il y ait catch ou non.
    }

    
    @SuppressWarnings("unchecked")
	@Override
    public void followPath(ArrayList<Vec2> chemin, ArrayList<Hook> hooksToConsider) throws UnableToMoveException
    {
		log.debug("appel de RobotReal.followPath(" + chemin + "," + hooksToConsider + ")", this);
    	cheminSuivi=(ArrayList<Vec2>) chemin.clone();

        mLocomotion.followPath(chemin, hooksToConsider, DirectionStrategy.getDefaultStrategy());
    }
    

    @SuppressWarnings("unchecked")
	@Override
    protected void followPath(ArrayList<Vec2> chemin, ArrayList<Hook> hooksToConsider, DirectionStrategy direction) throws UnableToMoveException
    {
		log.debug("appel de RobotReal.followPath(" + chemin + "," + hooksToConsider + "," + direction + ")", this);
    	cheminSuivi=(ArrayList<Vec2>) chemin.clone();
        mLocomotion.followPath(chemin, hooksToConsider, direction);
    }

    @Override
    public void immobilise()
    {
		log.debug("appel de RobotReal.immobilise()", this);
        mLocomotion.immobilise();
    }
    
	@Override
	public void enableRotationnalFeedbackLoop()
	{
		log.debug("appel de RobotReal.enableRotationnalFeedbackLoop()", this);
		try
		{
			mLocomotion.enableRotationnalFeedbackLoop();
		}
		catch (SerialConnexionException e)
		{
			log.critical( e.logStack(), this);
		}
	}

	@Override
	public void disableRotationnalFeedbackLoop()
	{
		log.debug("appel de RobotReal.disableRotationnalFeedbackLoop()", this);
		try
		{
			mLocomotion.disableRotationnalFeedbackLoop();
		}
		catch (SerialConnexionException e)
		{
			log.critical( e.logStack(), this);
		}
	}
	
	@Override
	public void enableFeedbackLoop() throws SerialConnexionException
	{
		mLocomotion.enableFeedbackLoop();		
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
	public void setLocomotionSpeed(Speed vitesse)
	{
        try
        {
			mLocomotion.setTranslationnalSpeed(vitesse.PWMTranslation);
	        mLocomotion.setRotationnalSpeed(vitesse.PWMRotation);
	        
	        speed = vitesse;
		} 
        catch (SerialConnexionException e)
        {
			log.critical( e.logStack(), this);
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
