package robot;

import java.util.ArrayList;

import hook.Hook;
import smartMath.Vec2;
import table.Table;
import utils.Log;
import utils.Config;
import pathDingDing.PathDingDing;
import enums.ActuatorOrder;
import enums.SensorNames;
import enums.Speed;
import exceptions.Locomotion.UnableToMoveException;

/**
 * Robot virtuel ne faisant pas bouger le robot réel, mais détermine la durée des actions.
 * Utile pour l'IA pour savoir combien de temps prendrait une action
 * @author pf et marsu !
 */

public class RobotChrono extends Robot
{

	/** position du robot virtuel sur la table. Utile pour calculer le temps nécéssaire pour atteindre un autre point de la table */
	protected Vec2 position = new Vec2();
	

	/** orientation du robot virtuel sur la table. Utile pour calculer le temps nécéssaire pour atteindre un autre point de la table */
	protected double orientation;
	
	/** Chronomètre du robot en millisecondes */
	private int chrono = 0;
	
	/** valeur approchée du temps (en milisecondes) nécéssaire pour qu'une information que l'on envois a la série soit aquité */
	private int approximateSerialLatency = 50;

	/**
	 *  Fais un nouveau Robot Chrono
	 * @param config fichier de configuration ou lire la config du match
	 * @param log système de log a utiliser pour écrire
	 */
	public RobotChrono(Config config, Log log, PathDingDing pathDingDing)
	{
		super(config, log, pathDingDing);
	}

	/**
	 * Utilisé par les tests
	 * @param other l'autre robot chrono avec lequel comparer celui-ci
	 * @return vrai si les robotChronos sont égaux, faux sinon
	 */
	// TODO à compléter au fur et à mesure
	public boolean equals(RobotChrono other)
	{
		return 	position.equals(other.position) && 
				orientation == other.orientation;
	}

	@Override
	public void useActuator(ActuatorOrder order, boolean waitForCompletion)
	{
    	// prsise en considération de la latence de la liaison série
		this.chrono += approximateSerialLatency;
		
		if(waitForCompletion)
			this.chrono += order.getDuration();
	}

	@Override
    public void turn(double angle, ArrayList<Hook> hooksToConsider, boolean expectsWallImpact) throws UnableToMoveException
	{
		// symétrise la table si l'on est équipe jaune
        if(symmetry)
            angle = Math.PI-angle;

		// met a jour l'orientation du robot
		orientation = angle;
		
	    
        // angle duqel le robot doit tourner pour avoir l'orientation désirée
		double turnAmount = angle-orientation;
		
		// Met l'angle a parcourir dans ]-PI; PI]
		if(turnAmount < 0)
			turnAmount *= -1;
		while(turnAmount > 2*Math.PI)
			turnAmount -= 2*Math.PI;
		if(turnAmount > Math.PI)
			turnAmount = 2*(float)Math.PI - turnAmount;
		
		
		// incrémente le chronomètre du temps de trajet
		chrono += turnAmount*speed.invertedRotationnalSpeed;
		

    	// prsise en considération de la latence de la liaison série
		this.chrono += approximateSerialLatency;
	}

	@Override
    public void moveLengthwise(int distance, ArrayList<Hook> hooks, boolean mur) throws UnableToMoveException
	{

    	// prsise en considération de la latence de la liaison série
		this.chrono += approximateSerialLatency;
		
		// rajoute au compteur le temps de trajet
		chrono += Math.abs(distance)*speed.invertedTranslationnalSpeed;

		// déplace le robot virtuel
		position.plus( new Vec2( 	(int)(distance*Math.cos(orientation)),
									(int)(distance*Math.sin(orientation))  ) 
					 );
	}
	
	@SuppressWarnings("unused")
	@Override
    public void followPath(ArrayList<Vec2> path, ArrayList<Hook> hooksToConsider) throws UnableToMoveException
	{
		// va sucessivement a tout les points
		for(Vec2 point: path)
		{
			
			//TODO on utilise pas moveToLocation
			//moveToLocation vas au point en utilisant le pathfinding, followPath suit le chemin donne par moveToLocation
			//il faut calculer le temps pour parcourir le chemin donne : TempsPourTourner + distance/vitesse
			//moveToLocation(point, hooksToConsider);
		}
	}
	
	@Override
	public void moveToLocation(Vec2 point, ArrayList<Hook> hooksToConsider,Table table)
	{
		// symétrise la table si l'on est équipe jaune
		if(symmetry)
			point.x *= -1;

		// incrémente le chronomètre du temps de trajet
		//chrono += position.distance(point)*speed.invertedTranslationnalSpeed;
		// TODO: faire un appel au pathfinding ici, le compteur doit être incrémenté du temps de trajet en suivent le chemin dicté par le pathfinding
		// un appel a followPath serait avisé
		

    	// prsise en considération de la latence de la liaison série
		this.chrono += approximateSerialLatency;
		
		position = point.clone();
	}

	@Override
	public void setLocomotionSpeed(Speed vitesse)
	{
    	// prsise en considération de la latence de la liaison série
		this.chrono += approximateSerialLatency;
		
	    this.speed = vitesse;
	}

    @Override
    public void enableRotationnalFeedbackLoop()
    {
    	// prsise en considération de la latence de la liaison série
		this.chrono += approximateSerialLatency;
    }

    @Override
    public void disableTranslationnalFeedbackLoop()
    {
    	// prsise en considération de la latence de la liaison série
		this.chrono += approximateSerialLatency;
    }

	@Override
	public void sleep(long duree) 
	{
		this.chrono += duree;
	}
	
	@Override
    public void immobilise()
    {
    	// prsise en considération de la latence de la liaison série
		this.chrono += approximateSerialLatency;
    }


	/*
	 * 			SEETERS & Getters
	 */

	/**
	 * Remet le chronomètre a zéro
	 */
	public void resetChrono()
	{
			chrono = 0;
	}
	
	/**
	 * Donne la veleur courante du chronomètre
	 * @return valeur du chronomètre en milisecondes
	 */
	public int getCurrentChrono()
	{
		return chrono;
	}
	
	@Override
	public void setPosition(Vec2 position)
	{

    	// prsise en considération de la latence de la liaison série
		this.chrono += approximateSerialLatency;
		
		this.position = position;
	}
	
	@Override
	public void setOrientation(double orientation)
	{

    	// prsise en considération de la latence de la liaison série
		this.chrono += approximateSerialLatency;
		
		this.orientation = orientation;
	}

	    
    @Override
    public Vec2 getPosition()
    {
    	// prsise en considération de la latence de la liaison série
		this.chrono += approximateSerialLatency;
		
        return position.clone();
    }

    @Override
    public double getOrientation()
    {
    	// prsise en considération de la latence de la liaison série
		this.chrono += approximateSerialLatency;
		
        return orientation;
    }

	@Override
	public Vec2 getPositionFast()
	{
        return position.clone();
	}

	@Override
	public double getOrientationFast()
	{
        return orientation;
	}

	@Override
	public Object getSensorValue(SensorNames sensor) 
	{
		this.chrono += approximateSerialLatency;
		this.chrono += sensor.getAverageDuration();
		return sensor.getDefaultValue();
	}
}
