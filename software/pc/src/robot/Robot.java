package robot;

import container.Service;
import enums.*;
import exceptions.ConfigPropertyNotFoundException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialConnexionException;
import hook.Hook;
import robot.serial.SerialWrapper;
import smartMath.Circle;
import smartMath.Vec2;
import table.Table;
import utils.Config;
import utils.Log;
import utils.Sleep;

import java.util.ArrayList;

/**
 * Effectue le lien entre le code et la réalité (permet de parler aux actionneurs, d'interroger les capteurs, etc.)
 * @author pf, marsu
 *
 */
public class Robot implements Service
{

	/**  système de log sur lequel écrire. */
	protected Log log;

	/**  endroit ou lire la configuration du robot. */
	protected Config config;

	/**  la table est symétrisée si on est équipe jaune. */
	protected boolean symmetry;


	/**  vitesse du robot sur la table. */
	protected Speed speed;

	/**la position du robot*/
	protected Vec2 position;

	/** l'orientation du robot*/
	protected double orientation;

	/** Rayon du robot provenant du fichier de config */
	private int robotRay;

	/** chemin en court par le robot, utilise par l'interface graphique */
	public ArrayList<Vec2> cheminSuivi = new ArrayList<Vec2>();

	private float aimThresold = 15;

	/** Si le robot force dans ses mouvements*/
	protected boolean isForcing = false;

	private SerialWrapper serialWrapper;

	private SymmetrizedActuatorOrderMap mActuatorCorrespondenceMap = new SymmetrizedActuatorOrderMap();
	private SymmetrizedTurningStrategy mTurningStrategyCorrespondenceMap = new SymmetrizedTurningStrategy();
	private SymmetrizedSensorNamesMap mSensorNamesMap = new SymmetrizedSensorNamesMap();
	
	/** Système de locomotion a utiliser pour déplacer le robot */
	private Locomotion mLocomotion;
	
	
	/** Constructeur*/
	public Robot(Locomotion deplacements, Config config, Log log, SerialWrapper serialWrapper)
 	{
		this.config = config;
		this.log = log;
		this.serialWrapper = serialWrapper;
		this.mLocomotion = deplacements;
		updateConfig();
		speed = Speed.SLOW_ALL;
	}

	/**
	 * Met a jour la configuration de la classe via le fichier de configuration fourni par le sysème de container.
	 * et supprime les espaces (si si c'est utile)
	 */
	public void updateConfig()
	{
		try
		{
			symmetry = config.getProperty("couleur").replaceAll(" ","").equals("violet"); // TODO : modifier la couleur adverse
			robotRay = Integer.parseInt(config.getProperty("rayon_robot"));
			position = Table.entryPosition;
			orientation = Math.PI;
		}
		catch (ConfigPropertyNotFoundException e)
		{
			log.critical( e.logStack());
			log.debug("Revoir le code : impossible de trouver la propriété "+e.getPropertyNotFound());;
		}
	}

	public void useActuator(ActuatorOrder order, boolean waitForCompletion) throws SerialConnexionException
	{
		if(symmetry)
			order = mActuatorCorrespondenceMap.getSymmetrizedActuatorOrder(order);
		serialWrapper.useActuator(order);
		
		if(waitForCompletion)
		{
			sleep(order.getDuration());
		}
	}
	
	public boolean getContactSensorValue (ContactSensors sensor) throws SerialConnexionException
	{
		// si il n'y a pas de symétrie, on renvoie la valeur brute du bas niveau
				if(!symmetry) 
					return serialWrapper.getContactSensorValue(sensor);
				else
				{
					sensor = mSensorNamesMap.getSymmetrizedContactSensorName(sensor);
					
					/* attention si les capteurs sont en int[] il faut symétriser ce int[] */
					
					return serialWrapper.getContactSensorValue(sensor);
				}
	}

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
	public void moveLengthwise(int distance, ArrayList<Hook> hooksToConsider, boolean expectsWallImpact) throws UnableToMoveException
	{	
		log.debug("appel de Robot.moveLengthwise(" + distance + "," + hooksToConsider + "," + expectsWallImpact + ")");
		moveLengthwise(distance, hooksToConsider, expectsWallImpact, true);
	}


    public void setBasicDetection(boolean basicDetection)
    {
        mLocomotion.setBasicDetection(basicDetection);
    }

	public void setUSvalues(ArrayList<Integer> val)
	{
		mLocomotion.setUSvalues(val);
	}

    public void moveLengthwiseWithoutDetection(int distance, ArrayList<Hook> hooksToConsider, boolean expectsWallImpact) throws UnableToMoveException
	{	
		log.debug("appel de Robot.moveLengthwiseWithoutDetection(" + distance + "," + hooksToConsider + "," + expectsWallImpact + ")");
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
	 public void moveLengthwise(int distance, ArrayList<Hook> hooksToConsider, boolean expectsWallImpact, Boolean mustDetect) throws UnableToMoveException
	{	
		log.debug("appel de Robot.moveLengthwise(" + distance + "," + hooksToConsider + "," + expectsWallImpact + "," + mustDetect + ")");
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
	 public void moveLengthwise(int distance, ArrayList<Hook> hooksToConsider, boolean expectsWallImpact, Boolean mustDetect, Speed newSpeed) throws UnableToMoveException
	{	
		log.debug("appel de Robot.moveLengthwise(" + distance + "," + hooksToConsider + "," + expectsWallImpact + "," + mustDetect + "," + newSpeed + ")");
		Speed oldSpeed = speed;
		speed = newSpeed;
		mLocomotion.moveLengthwise(distance, hooksToConsider, expectsWallImpact, mustDetect);
		speed = oldSpeed;
	}

    public void moveLengthwiseWithoutDetection(int distance) throws UnableToMoveException
    {
        log.debug("appel de Robot.moveLengthwiseWithoutDetection(" + distance + ")");
        moveLengthwiseWithoutDetection(distance, null, false);
    }
    /**
     * Fait avancer le robot de la distance spécifiée. Le robot garde son orientation actuelle et va simplement avancer
     * Cette méthode est bloquante: son exécution ne se termine que lorsque le robot a atteint le point d'arrivée
     * @param distance en mm que le robot doit franchir
     * @param hooksToConsider hooks a considérer lors de ce déplacement. Le hook n'est déclenché que s'il est dans cette liste et que sa condition d'activation est remplie
     * @param speed la vitesse du robot lors de son parcours
     * @throws UnableToMoveException losrque quelque chose sur le chemin cloche et que le robot ne peut s'en défaire simplement: bloquage mécanique immobilisant le robot ou obstacle percu par les capteurs
     */
    public void moveLengthwise(int distance, ArrayList<Hook> hooksToConsider, Speed speed) throws UnableToMoveException
    {

        log.debug("appel de Robot.moveLengthwise(" + distance + "," + hooksToConsider + "," + speed + ")");
        moveLengthwise(distance, hooksToConsider, false, true, speed);
    }

    /**
     * Fait avancer le robot de la distance spécifiée. Le robot garde son orientation actuelle et va simplement avancer.
     * Attention, cette méthode suppose qu'il n'y a pas de hooks a considérer, et que l'on est pas sensé percuter un mur.
     * Cette méthode est bloquante: son exécution ne se termine que lorsque le robot a atteint le point d'arrivée
     * @param distance en mm que le robot doit franchir. Si cette distance est négative, le robot va reculer. Attention, en cas de distance négative, cette méthode ne vérifie pas s'il y a un système d'évitement a l'arrère du robot
     * @throws UnableToMoveException losrque quelque chose sur le chemin cloche et que le robot ne peut s'en défaire simplement: bloquage mécanique immobilisant le robot ou obstacle percu par les capteurs
     */
    public void moveLengthwise(int distance) throws UnableToMoveException
    {
        log.debug("appel de Robot.distance(" + distance + ")");
        moveLengthwise(distance, new ArrayList<Hook>(), false);
    }

    /**
     * Fait avancer le robot de la distance spécifiée. Le robot garde son orientation actuelle et va simplement avancer.
     * Attention, cette méthode suppose que l'on est pas sensé percuter un mur.
     * Cette méthode est bloquante: son exécution ne se termine que lorsque le robot a atteint le point d'arrivée
     *
     * @param distance en mm que le robot doit franchir. Si cette distance est négative, le robot va reculer. Attention, en cas de distance négative, cette méthode ne vérifie pas s'il y a un système d'évitement a l'arrère du robot
     * @param hooksToConsider les hooks déclenchables durant ce mouvement
     * @throws UnableToMoveException losrque quelque chose sur le chemin cloche et que le robot ne peut s'en défaire simplement: bloquage mécanique immobilisant le robot ou obstacle percu par les capteurs
     */
    public void moveLengthwise(int distance, ArrayList<Hook> hooksToConsider) throws UnableToMoveException
    {
        log.debug("appel de Robot.moveLengthwise(" + distance + "," + hooksToConsider + ")");
        moveLengthwise(distance, hooksToConsider, false);
    }

    /**
     * Fait avancer le robot de la distance spécifiée. Le robot garde son orientation actuelle et va simplement avancer.
     * Attention, cette méthode suppose qu'il n'y a pas de hooks a considérer, et que l'on est sensé percuter un mur. La vitesse du robor est alors réduite a Speed.INTO_WALL.
     * Cette méthode est bloquante: son exécution ne se termine que lorsque le robot a atteint le point d'arrivée
     * @param distance en mm que le robot doit franchir. Si cette distance est négative, le robot va reculer. Attention, en cas de distance négative, cette méthode ne vérifie pas s'il y a un système d'évitement a l'arrère du robot
     * @param hooksToConsider les hooks déclenchables durant ce mouvement
     * @throws UnableToMoveException losrque quelque chose sur le chemin cloche et que le robot ne peut s'en défaire simplement: bloquage mécanique immobilisant le robot ou obstacle percu par les capteurs
     */
    public void moveLengthwiseTowardWall(int distance, ArrayList<Hook> hooksToConsider) throws UnableToMoveException
    {

        log.debug("appel de Robot.moveLengthwiseTowardWall(" + distance + "," + hooksToConsider + ")");
        Speed oldSpeed = speed;
        setLocomotionSpeed(Speed.SLOW_ALL);
        moveLengthwise(distance, hooksToConsider, true, false);
        setLocomotionSpeed(oldSpeed);
    }

    /**
     * Déplace le robot vers un point en suivant un chemin qui évite les obstacles. (appel du pathfinding)
     * Cette méthode est bloquante: son exécution ne se termine que lorsque le robot a atteint le point d'arrivée
     *
     * @param aim le point de destination du mouvement
     * @param hooksToConsider les hooks déclenchables durant ce mouvement
     * @param table la table sur laquelle le robot se deplace
     * @throws UnableToMoveException losrque quelque chose sur le chemin cloche et que le robot ne peut s'en défaire simplement: bloquage mécanique immobilisant le robot ou obstacle percu par les capteurs
     */
    public void moveToLocation(Vec2 aim, ArrayList<Hook> hooksToConsider, Table table) throws  UnableToMoveException
    {
        log.debug("appel de Robot.moveToLocation(" + aim + "," + hooksToConsider + "," + table + ")");
        //On crée bêtement un cercle de rayon nul pour lancer moveToCircle, sachant que la position de ce cercle est extraite pour le pathDiniDing (et après on dit qu'à INTech on code comme des porcs...)
        moveToCircle(new Circle(aim), hooksToConsider, table);
    }

    /**
     * deplace le robot vers le point du cercle donnné le plus proche, en evitant les obstacles. (appel du pathfinding)
     * methode bloquante : l'execution ne se termine que lorsque le robot est arrive
     * @param aim le cercle ou l'on veut se rendre
     * @param hooksToConsider the hooks to consider
     * @param table la table sur laquell on est sensé se déplacer
     * @throws UnableToMoveException losrque quelque chose sur le chemin cloche et que le robot ne peut s'en défaire simplement: bloquage mécanique immobilisant le robot ou obstacle percu par les capteurs
     */
    public void moveToCircle(Circle aim, ArrayList<Hook> hooksToConsider, Table table) throws UnableToMoveException
    {
        // TODO : à coder après la création du PathFinding
    }

	/**
	 * ATTENTION, la valeur "mur" est ignorée
	 */
    public void turn(double angle, ArrayList<Hook> hooksToConsider, boolean expectsWallImpact, boolean isTurnRelative) throws UnableToMoveException
    {
		log.debug("appel de Robot.turn(" + angle + "," + hooksToConsider + "," + expectsWallImpact + "," + isTurnRelative + ")");
    	if (isTurnRelative)
    		angle += getOrientation();
        turn(angle, hooksToConsider);
    }

	/**
	 * Fait tourner le robot (méthode bloquante)
	 * Attention: le pivot sera fait en supposant qu'il n'y a pas de hook a vérifier, et qu'on ne s'attends pas a percuter un obstacle.
	 *
	 * @param angle : valeur relative en radiant de l'orientation que le robot doit avoir après cet appel
	 * @throws UnableToMoveException losrque quelque chose sur le chemin cloche et que le robot ne peut s'en défaire simplement: bloquage mécanique immobilisant le robot ou obstacle percu par les capteurs
	 */
	public void turnRelative(double angle) throws UnableToMoveException
	{
		log.debug("appel de Robot.turnRelative(" + angle + ")");
		turn(angle, null, false, true);
	}

	/**
	 * Fait tourner le robot (méthode bloquante)
	 * Attention: le pivot sera fait en supposant qu'il n'y a pas de hook a vérifier, et qu'on ne s'attends pas a percuter un obstacle.
	 *
	 * @param angle : valeur absolue en radiant de l'orientation que le robot doit avoir après cet appel
	 * @throws UnableToMoveException losrque quelque chose sur le chemin cloche et que le robot ne peut s'en défaire simplement: bloquage mécanique immobilisant le robot ou obstacle percu par les capteurs
	 */
	public void turn(double angle) throws UnableToMoveException
	{
		log.debug("appel de Robot.turn(" + angle + ")");
		turn(angle, null, false, false);
	}


    public void turnWithoutDetection(double angle, ArrayList<Hook> hooksToConsider) throws UnableToMoveException
    {
		log.debug("appel de Robot.turn(" + angle + "," + hooksToConsider + ")");
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

	public void setSmoothAcceleration(boolean state) throws SerialConnexionException
	{
		this.mLocomotion.setSmoothAcceleration(state);
	}

    public void turn(double angle, ArrayList<Hook> hooksToConsider, boolean expectsWallImpact) throws UnableToMoveException
    {
		log.debug("appel de Robot.turn(" + angle + "," + hooksToConsider + "," + expectsWallImpact + ")");
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
		log.debug("appel de Robot.turn(" + angle + "," + hooksToConsider + ")");
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

    /**
     * Fait tourner le robot (méthode bloquante)
     * L'orientation est modifiée si on est équipe jaune: Cette méthode n'adapte pas l'orientation en fonction de la couleur de l'équipe
     * Attention: le pivot sera fait en supposant qu'il n'y a pas de hook a vérifier, et qu'on ne s'attends pas a percuter un obstacle.
     *
     * @param angle : valeur absolue en radiant de l'orientation que le robot doit avoir après cet appel. L'orientation ne sera pas symétrisée, quelle que soit la couleur de l'équipe.
     * @throws UnableToMoveException losrque quelque chose sur le chemin cloche et que le robot ne peut s'en défaire simplement: bloquage mécanique immobilisant le robot ou obstacle percu par les capteurs
     */
    public void turnNoSymmetry(double angle) throws UnableToMoveException
    {

        log.debug("appel de Robot.turnNoSymmetry(" + angle + ")");
        // Fais la symétrie deux fois (symétrie de symétrie, c'est l'identité)
        if(symmetry)
            turn(Math.PI-angle, null, false, false);
        else
            turn(angle, null, false, false);
    }
    
    @SuppressWarnings("unchecked")
    public void followPath(ArrayList<Vec2> chemin, ArrayList<Hook> hooksToConsider) throws UnableToMoveException
    {
    	cheminSuivi = (ArrayList<Vec2>) chemin.clone();
        mLocomotion.followPath(chemin, hooksToConsider, DirectionStrategy.getDefaultStrategy());
    }
    

    @SuppressWarnings("unchecked")
    protected void followPath(ArrayList<Vec2> chemin, ArrayList<Hook> hooksToConsider, DirectionStrategy direction) throws UnableToMoveException
    {
    	cheminSuivi = (ArrayList<Vec2>) chemin.clone();
        mLocomotion.followPath(chemin, hooksToConsider, direction);
    }

    public void immobilise()
    {
		log.debug("appel de Robot.immobilise()");
        mLocomotion.immobilise();
    }
    
	public void enableRotationnalFeedbackLoop()
	{
		log.debug("appel de Robot.enableRotationnalFeedbackLoop()");
		try
		{
			mLocomotion.enableRotationnalFeedbackLoop();
		}
		catch (SerialConnexionException e)
		{
			log.critical( e.logStack());
		}
	}

	public void disableRotationnalFeedbackLoop()
	{
		log.debug("appel de Robot.disableRotationnalFeedbackLoop()");
		try
		{
			mLocomotion.disableRotationnalFeedbackLoop();
		}
		catch (SerialConnexionException e)
		{
			log.critical( e.logStack());
		}
	}
	
	public void enableFeedbackLoop() throws SerialConnexionException
	{
		mLocomotion.enableFeedbackLoop();		
	}

	public void disableFeedbackLoop() throws SerialConnexionException
	{
		mLocomotion.disableFeedbackLoop();
	}
	
	/* 
	 * GETTERS & SETTERS
	 */
	public void setPosition(Vec2 position)
	{
	    mLocomotion.setPosition(position);
	}
	
	public Vec2 getPosition()
	{
    	position = mLocomotion.getPosition();
	    return position;
	}

    /**
     * donne la dernière position connue du robot sur la table
     * cette methode est rapide et ne déclenche pas d'appel série
     * @return la dernière position connue du robot
     */
    public Vec2 getPositionFast()
    {
        return position;
    }

    public void setOrientation(double orientation)
	{
	    mLocomotion.setOrientation(orientation);
	}

    public double getOrientation()
    {
    	orientation =  mLocomotion.getOrientation();
        return orientation;
    }

    /**
     * Donne la derniere orientation connue du robot sur la table
     * Cette méthode est rapide et ne déclenche pas d'appel série
     * @return la derniere orientation connue du robot
     */
    public double getOrientationFast()
    {
        return orientation;
    }

	public boolean setTurningStrategy(TurningStrategy turning)
	{
        if(!(turning == TurningStrategy.FASTEST))
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
	
	public boolean setDirectionStrategy(DirectionStrategy motion)
	{
        if(!(motion == DirectionStrategy.FASTEST))
		{
			mLocomotion.setDirectionOrders(motion);
			return true;
		}
		return false;
	}

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

    public void setRobotRadius(int radius)
    {
        this.robotRay = radius;
    }

    public int getRobotRadius()
    {
        return this.robotRay;
    }
	

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
