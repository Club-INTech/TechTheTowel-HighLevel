package robot;

import container.Service;
import enums.ActuatorOrder;
import enums.ContactSensors;
import enums.Speed;
import enums.USsensors;
import exceptions.ConfigPropertyNotFoundException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.PathNotFoundException;
import exceptions.PointInObstacleException;
import exceptions.serial.SerialConnexionException;
import hook.Hook;
import pathDingDing.Node;
import pathDingDing.PathDingDing;
import smartMath.Circle;
import smartMath.Vec2;
import table.Table;
import utils.Config;
import utils.Log;

import java.util.ArrayList;

// TODO ajouter les capteurs au robot.
/**
 *  Classe abstraite du robot, dont héritent RobotVrai et RobotChrono
 *  Quand une action peut être faite soit pour en connaitre le temps d'exécution, soit pour la faire en vrai, c'est elle qu'il faut utiliser.
 *
 * @author PF, marsu
 */
public abstract class Robot implements Service 
{

	/**  système de log sur lequel écrire. */
	protected Log log;
	
	/**  endroit ou lire la configuration du robot. */
	protected Config config;
	
	/**  pathfinding du robot */
	protected PathDingDing pathDingDing;

	/**  la table est symétrisée si on est équipe jaune. */
	protected boolean symmetry;
	
	/**  vitesse du robot sur la table. */
	protected Speed speed;
	
	/**
	 * la position du robot
	 */
	protected Vec2 position;
	/**
	 * l'orientation du robot
	 */
	protected double orientation;
	
	/** Rayon du robot provenant du fichier de config */
	public int robotRay;

	/** chemin en court par le robot, utilise par l'interface graphique */
	public ArrayList<Vec2> cheminSuivi = new ArrayList<Vec2>();
	
	private float aimThresold = 15;
	

	/** état du parasol */
	public boolean stateParasol = false;

	/** Booléen indiquant si les poissons sont récupérés, sert pour les versions du script Fishing */
	public boolean AreFishesFished = false;
	
	/** Booléen indiquant la présence de poisson sur les actionneurs */
	private boolean AreFishesOnBoard = false;
	
	/** Booléen indiquant la présence de sable dans le robot */
	private boolean IsSandInside = false;

	
	
	/**
	 * Instancie le robot.
	 * Appell� par le container
	 *
	 * @param config : sur quel objet lire la configuration du match
	 * @param log : la sortie de log à utiliser
	 * @param pathDingDing l'instance de pathfinding a utiliser
	 */
	public Robot(Config config, Log log, PathDingDing pathDingDing)
	{
		this.config = config;
		this.log = log;
		this.pathDingDing = pathDingDing;
		updateConfig();
	}
	
	/**
	 * Met a jour la configuration de la classe via le fichier de configuration fourni par le sysème de container.
	 * et supprime les espaces (si si c'est utile)
	 */
	public void updateConfig()
	{
		try 
		{
			symmetry = config.getProperty("couleur").replaceAll(" ","").equals("jaune");
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

	/**
	 * Demande au robot d'utiliser un de ses actionneurs
	 * @param order l'ordre a exécuter
	 * @param waitForCompletion si vrai, cette méthode attendra que l'actionneur ait fini d'exécuter la consigne
	 * @throws SerialConnexionException  en cas de problème de communication avec la carte actionneurs
	 */
	public abstract void useActuator(ActuatorOrder order, boolean waitForCompletion) throws SerialConnexionException;
	
    /**
     * Fais attendre le robot.
     * C'est a utiliser au lieu d'attendre via Sleep.sleep, car dans robotChrono, au lieu d'attendre, on incrémente le chronomètre de la valeur coresspondante.
     * @param delay temps que le robot doit passer a attendre
     */
	
    public abstract void sleep(long delay);
    
	/**
	 * Donne la vitesse courrante a laquelle le robot avance et tourne sur lui même sur la table.
	 *
	 * @return la vitesse courrante
	 */
    
	public Speed getSpeed()
	{
		return speed;
	}
	
	/**
	 * Change la valeur du booléen AreFishesFished
	 * @param bool bouléen souhaité
	 */
	public void setAreFishesFished(boolean bool)
	{
		this.AreFishesFished = bool;
	}
	
	/**
	 * Indique si les poissons ont été pêchés ou pas
	 * @return Poisson pris ou non
	 */
	public boolean getAreFishesFished()
	{
		return AreFishesFished;
	}
	
	/**
	 * Change la valeur du booléen AreFishesOnBoard
	 * @param booléen souhaité
	 */
	public void setAreFishesOnBoard(boolean areFishesOnBoard) 
	{
		this.AreFishesOnBoard = areFishesOnBoard;
	}
	
	/**
	 * Indique si les poissons sont sur le bras ou pas
	 * @return Poisson sur bras ou non
	 */
	public boolean getAreFishesOnBoard() 
	{
		return AreFishesOnBoard;
	}
	
	/**
	 * Change la valeur du booléen IsSandInside
	 * @param booléen souhaité
	 */
	public void setIsSandInside(boolean isSandInside) 
	{
		this.IsSandInside = isSandInside;
	}
	
	/**
	 * Indique si le sable est dans le robot ou pas
	 * @return Sable pris ou non
	 */
	public boolean getIsSandInside() 
	{
		return IsSandInside;
	}
	
	/**
	 * Immobilise le robot.
	 * Après l'appel de cette fonction, le robot sera immobile sur la table
	 */
	public abstract void immobilise();

	/**
	 * Fait tourner le robot (méthode bloquante).
	 *
	 * @param angle : valeur absolue en radiant de l'orientation que le robot doit avoir après cet appel
	 * @param hooksToConsider hooks a considérer lors de ce déplacement. Le hook n'est déclenché que s'il est dans cette liste et que sa condition d'activation est remplie
	 * @param expectsWallImpact true si le robot doit s'attendre a percuter un mur au cours de la rotation. false si les alentours du robot sont sensés être dégagés.
	 * @param isTurnRelative vrai si l'angle est relatif et pas absolut
	 * @throws UnableToMoveException losrque quelque chose sur le chemin cloche et que le robot ne peut s'en défaire simplement: bloquage mécanique immobilisant le robot ou obstacle percu par les capteurs
	 */
    public abstract void turn(double angle, ArrayList<Hook> hooksToConsider, boolean expectsWallImpact, boolean isTurnRelative) throws UnableToMoveException;
    
    /**
	 * Fait tourner le robot (méthode bloquante).
	 *
	 * @param angle : valeur absolue en radiant de l'orientation que le robot doit avoir après cet appel
	 * @param hooksToConsider hooks a considérer lors de ce déplacement. Le hook n'est déclenché que s'il est dans cette liste et que sa condition d'activation est remplie
	 * @param expectsWallImpact true si le robot doit s'attendre a percuter un mur au cours de la rotation. false si les alentours du robot sont sensés être dégagés.
	 * @throws UnableToMoveException losrque quelque chose sur le chemin cloche et que le robot ne peut s'en défaire simplement: bloquage mécanique immobilisant le robot ou obstacle percu par les capteurs
	 */
    public abstract void turn(double angle, ArrayList<Hook> hooksToConsider, boolean expectsWallImpact) throws UnableToMoveException;
    
	/**
	 * Fait avancer le robot de la distanted PathDingDing pathDingDing;ce spécifiée. Le robot garde son orientation actuelle et va simplement avancer.
	 * C'est la méthode que les utilisateurs (externes au développement du système de locomotion) vont utiliser
	 * Cette méthode est bloquante: son exécution ne se termine que lorsque le robot a atteint le point d'arrivée
	 * @param distance en mm que le robot doit franchir. Si cette distance est négative, le robot va reculer. Attention, en cas de distance négative, cette méthode ne vérifie pas s'il y a un système d'évitement a l'arrère du robot
	 * @param hooksToConsider hooks a considérer lors de ce déplacement. Le hook n'est déclenché que s'il est dans cette liste et que sa condition d'activation est remplie	 
	 * @param expectsWallImpact true si le robot doit s'attendre a percuter un mur au cours du déplacement. false si la route est sensée être dégagée.
	 * @throws UnableToMoveException losrque quelque chose sur le chemin cloche et que le robot ne peut s'en défaire simplement: bloquage mécanique immobilisant le robot ou obstacle percu par les capteurs
	 */
    // TODO: ne pas utiliser cette méthode. il faut utiliser moveLengthwiseTowardWall pour foncer dans un mur.
    // à mettre en privé
    public abstract void moveLengthwise(int distance, ArrayList<Hook> hooksToConsider, boolean expectsWallImpact) throws UnableToMoveException;

    /**
     * Suit un chemin decrit par une liste de points
     * @param path le chemin a suivre, sous forme d'une liste de points 
     * @param hooks les  hooks a prendre en compte, à declencher suivant leurs conditions respectives
     * @param directionstrategy ce que la strategie choisit comme optimal (en avant, en arriere, au plus rapide)
     * @throws UnableToMoveException si le robot a un bloquage mecanique, ou un obstacle vu par un capteur
     */
    protected abstract void followPath(ArrayList<Vec2> chemin, ArrayList<Hook> hooks, DirectionStrategy direction) throws UnableToMoveException;


	/**
	 * Fais suivre un chemin au robot décrit par une liste de point.
	 * @param path liste des points sur la table a atteindre, dans l'ordre. Le robot parcourera une ligne brisée dont les sommets sont ces points.
	 * @param hooksToConsider hooks a considérer lors de ce déplacement. Le hook n'est déclenché que s'il est dans cette liste et que sa condition d'activation est remplie
	 * @throws UnableToMoveException losrque quelque chose sur le chemin cloche et que le robot ne peut s'en défaire simplement: bloquage mécanique immobilisant le robot ou obstacle percu par les capteurs
	 */
    public abstract void followPath(ArrayList<Vec2> path, ArrayList<Hook> hooksToConsider) throws UnableToMoveException;
    
	/**
	 * Change la vitesse a laquelle le robot avance et tourne sur lui-même.
	 * @param speed la vitesse désirée
	 */
	public abstract void setLocomotionSpeed(Speed speed);
	
	/**
	 * Donne la vitesse a laquelle le robot est configurée pour avancer et tourner sur lui-même.
	 * @return La vitesse du robot configurée actuellement
	 */
	public abstract Speed getLocomotionSpeed();
	
	/**
	 * Change dans l'asservissement la position du robot sur la table .
	 * Après appel de cette méthode, le robot considèrera qu'il se trouve sur la table aux coordonnées fournies.
	 * Cette fonction n'est pas instantannée, un petit délai (de 300ms) pour que la communication série se fasse est nécéssaire.
	 *
	 * @param position the new position
	 */
	public abstract void setPosition(Vec2 position);
	
	/**
	 * Change dans l'asservissement l'orientation du robot sur la table .
	 * Après appel de cette méthode, le robot considèrera qu'il se trouve sur la table avec l'orientation fournie.
	 * Cette fonction n'est pas instantannée, un petit délai (de 300ms) pour que la communication série se fasse est nécéssaire.
	 *
	 * @param orientation the new orientation
	 */
	public abstract void setOrientation(double orientation);
	
	/**
	 * Donne la position du robot sur la table.
	 * Cette méthode est lente mais très précise: elle déclenche un appel a la série pour obtenir une position a jour.
	 * @return la position courante du robot sur la table
	 */
    public abstract Vec2 getPosition();
    
    /**
     * donne la dernière position connue du robot sur la table
     * cette methode est rapide et ne déclenche pas d'appel série
     * @return la dernière position connue du robot
     */
    public Vec2 getPositionFast()
    {
    	return position;
    }
    
	/**
	 * Donne l'orientation du robot sur la table.
	 * Cette méthode est lente mais très précise: elle déclenche un appel a la série pour obtenir une orientation a jour.
	 * @return l'orientation en radiants courante du robot sur la table
	 */
    public abstract double getOrientation();
    
    /**
     * Donne la derniere orientation connue du robot sur la table
     * Cette méthode est rapide et ne déclenche pas d'appel série
     * @return la derniere orientation connue du robot
     */
    public double getOrientationFast() 
    {
		return orientation;
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
    
    public abstract void moveLengthwiseWithoutDetection(int distance, ArrayList<Hook> hooksToConsider, boolean expectsWallImpact) throws UnableToMoveException;

    
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
	 * @param expectsWallImpact true si le robot doit s'attendre a percuter un mur au cours du déplacement. false si la route est sensée être dégagée.
	 * @param mustDetect vrai si le robot doit detecter les obstacles sur son chemin
	 * @throws UnableToMoveException losrque quelque chose sur le chemin cloche et que le robot ne peut s'en défaire simplement: bloquage mécanique immobilisant le robot ou obstacle percu par les capteurs
	 */
	public abstract void moveLengthwise(int distance, ArrayList<Hook> hooksToConsider, boolean expectsWallImpact, Boolean mustDetect) throws UnableToMoveException;

	
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
	 public abstract void moveLengthwise(int distance, ArrayList<Hook> hooksToConsider, boolean expectsWallImpact, Boolean mustDetect, Speed speed) throws UnableToMoveException;
	
	 
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
        setLocomotionSpeed(Speed.SLOW);
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
     * @throws PathNotFoundException lorsque le pathdingding ne trouve pas de chemin 
     * @throws PointInObstacleException 
     */
    public void moveToLocation(Vec2 aim, ArrayList<Hook> hooksToConsider, Table table) throws  PathNotFoundException, UnableToMoveException, PointInObstacleException
    {

		log.debug("appel de Robot.moveToLocation(" + aim + "," + hooksToConsider + "," + table + ")");
		//On crée bêtement un cercle de rayon nul pour lancer moveToCircle, sachant que la position de ce cercle est extraite pour le pathDiniDing (et après on dit qu'à INTech on code comme des porcs...)
    	moveToCircle(new Circle(aim), hooksToConsider, table);
    }
    
    /**
     * deplace le robot vers le point du cercle donnné le plus proche, en evitant les obstacles. (appel du pathfinding)
     * methode bloquante : l'execution ne se termine que lorsque le robot est arrive
     * 
     * @param aim le cercle ou l'on veut se rendre
	 * @param hooksToConsider the hooks to consider
     * @param table la table sur laquell on est sensé se déplacer
     * 
     * @throws PathNotFoundException lorsque le pathdingding ne trouve pas de chemin 
     * @throws UnableToMoveException losrque quelque chose sur le chemin cloche et que le robot ne peut s'en défaire simplement: bloquage mécanique immobilisant le robot ou obstacle percu par les capteurs
     * @throws PointInObstacleException lorsque le point demandé est dans un obstacle ; utile pour un déplacement random / à éviter de jouer avec...
     */
    public void moveToCircle(Circle aim, ArrayList<Hook> hooksToConsider, Table table) throws PathNotFoundException, UnableToMoveException, PointInObstacleException
    {

    	PathDingDing pdd = new PathDingDing(table, log);
    	// On lance le calcul
    	ArrayList<Node> nodePath = pdd.computePath(position, aim.position);
    	
    	// On transforme les noeuds en Vec2
    	ArrayList<Vec2> path = new ArrayList<Vec2>();
    	for(int i=0 ; i<nodePath.size() ; i++)
    	{
    		path.add(nodePath.get(i).getPosition());
    	}
    	
		
    	//retire une distance egale au rayon du cercle au dernier point du chemin (le centre du cercle)
    	
    	//on retire le dernier point (le centre du cercle)
    	path.remove(path.size()-1);
    	
    	//le point precedent dans le path
    	Vec2 precedentPathPoint = new Vec2();
    	if (path.size()==0)
    	{
    		precedentPathPoint = getPosition();
    		if (symmetry)
    		{
    			precedentPathPoint.x *= -1;
    		}
    	}
    	else
    	{
    		precedentPathPoint = path.get(path.size()-1);
    	}

    	//le dernier vecteur deplacement
    	Vec2 movementVector = aim.position.minusNewVector(precedentPathPoint);
    	
    	
    	/* on ajoute le point sur le cercle B'=(B-A)*(L-r)/L+A
    	 * B le centre du cercle, r le rayon du cercle, A le point precedent dans le path et L la taille de B-A
    	 * ainsi le robot finira son chemin sur le point B'
    	 */
    	path.add(movementVector.dotFloat( (movementVector.length()-aim.radius)/movementVector.length() ).plusNewVector(precedentPathPoint));

    	//si on est trop proche du point d'arrivee on le retire
	    	if (path.get(1).distance(getPosition())<aimThresold)
	    		path.remove(1);

		followPath(path , hooksToConsider);
    }
    

	/**
	 * Informe la classe Robot que le parasol est déployé.
	 */
	public void parasolUnfolded()
	{
		stateParasol = true;
	}
    
    
    /**
     * Active tout l'asservissement
     * @throws SerialConnexionException 
     */
    public abstract void enableFeedbackLoop() throws SerialConnexionException;

	/**
	 * Active l'asservissement en rotation du robot.
	 */
    public abstract void enableRotationnalFeedbackLoop();
    
	/**
	 * Active l'asservissement en translation du robot.
	 */
    public abstract void disableRotationnalFeedbackLoop();

	/**
	 * le robot demande l'etat de ses capteurs ultrasons
	 * @param captor le nom du capteur dont on veut l'etat
	 * @return la valeur du capteur
	 * @throws SerialConnexionException si la connexion avec le capteur est interrompue
	 */
	public abstract int getUSSensorValue(USsensors captor) throws SerialConnexionException;

	/**
	 * le robot demande l'etat de ses capteurs de contact
	 * @param captor le nom du capteur dont on veut l'etat
	 * @return la valeur du capteur
	 * @throws SerialConnexionException si la connexion avec le capteur est interrompue
	 */
	public abstract boolean getContactSensorValue(ContactSensors captor) throws SerialConnexionException;
	
	public abstract void turnWithoutDetection(double angle, ArrayList<Hook> hooks) throws UnableToMoveException;

	public PathDingDing getPDD()
	{
		return pathDingDing;
	}

}
