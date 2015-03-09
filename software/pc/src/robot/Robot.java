package robot;

import java.util.ArrayList;
import java.util.EnumSet;

import pathDingDing.PathDingDing;
import hook.Hook;
import smartMath.Circle;
import smartMath.Vec2;
import table.Table;
import container.Service;
import enums.ActuatorOrder;
import enums.ObstacleGroups;
import enums.SensorNames;
import enums.Speed;
import enums.UnableToMoveReason;
import exceptions.PathNotFoundException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialConnexionException;
import utils.Log;
import utils.Config;

// TODO: Auto-generated Javadoc
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

	/** nombre de plots stockes dans le robot (mis a jour et utlisé par les scripts) */
	public int storedPlotCount;
	
	/** si le robot stocke une balle de tennis (mis a jour et utilise par les scripts) */
	public boolean isBallStored;

	/** si le robot a un verre dans la zone gauche (mis a jour et utilise par les scripts) */
	public boolean isGlassStoredLeft;
	
	/** si le robot a un verre dans la zone droit (mis a jour et utilise par les scripts) */
	public boolean isGlassStoredRight;
	
	/** Rayon du robot provenant du fichier de config */
	public int robotRay;
	
	/**
	 * Instancie le robot.
	 * Appell� par le container
	 *
	 * @param config : sur quel objet lire la configuration du match
	 * @param log : la sortie de log à utiliser
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
		symmetry = config.getProperty("couleur").replaceAll(" ","").equals("jaune");
        robotRay = Integer.parseInt(config.getProperty("rayon_robot"));
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
	 * @throws UnableToMoveException losrque quelque chose sur le chemin cloche et que le robot ne peut s'en défaire simplement: bloquage mécanique immobilisant le robot ou obstacle percu par les capteurs
	 */
    public abstract void turn(double angle, ArrayList<Hook> hooksToConsider, boolean expectsWallImpact) throws UnableToMoveException;
    
	/**
	 * Fait avancer le robot de la distance spécifiée. Le robot garde son orientation actuelle et va simplement avancer.
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

    // TODO: documenter
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
	 * Donne l'orientation du robot sur la table.
	 * Cette méthode est lente mais très précise: elle déclenche un appel a la série pour obtenir une orientation a jour.
	 * @return l'orientation en radiants courante du robot sur la table
	 */
    public abstract double getOrientation();
    
	/**
	 * Fait tourner le robot (méthode bloquante)
	 * Attention: le pivot sera fait en supposant qu'il n'y a pas de hook a vérifier, et qu'on ne s'attends pas a percuter un obstacle.
	 *
	 * @param angle : valeur relative en radiant de l'orientation que le robot doit avoir après cet appel
	 * @throws UnableToMoveException losrque quelque chose sur le chemin cloche et que le robot ne peut s'en défaire simplement: bloquage mécanique immobilisant le robot ou obstacle percu par les capteurs
	 */
	public void turnRelative(double angle) throws UnableToMoveException
	{
		turn(getOrientation() + angle, null, false);
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
        turn(angle, null, false);
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
    	
    	// Fais la symétrie deux fois (symétrie de symétrie, c'est l'identité)
        if(symmetry)
            turn(Math.PI-angle, null, false);
        else
            turn(angle, null, false);
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
        moveLengthwise(distance, null, false);
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
        moveLengthwise(distance, hooksToConsider, false);
    }

	/**
	 * Fait avancer le robot de la distance spécifiée. Le robot garde son orientation actuelle et va simplement avancer.
	 * Attention, cette méthode suppose qu'il n'y a pas de hooks a considérer, et que l'on est sensé percuter un mur. La vitesse du robor est alors réduite a Speed.INTO_WALL.
	 * Cette méthode est bloquante: son exécution ne se termine que lorsque le robot a atteint le point d'arrivée
	 * @param distance en mm que le robot doit franchir. Si cette distance est négative, le robot va reculer. Attention, en cas de distance négative, cette méthode ne vérifie pas s'il y a un système d'évitement a l'arrère du robot
	 * @throws UnableToMoveException losrque quelque chose sur le chemin cloche et que le robot ne peut s'en défaire simplement: bloquage mécanique immobilisant le robot ou obstacle percu par les capteurs
	 */
    public void moveLengthwiseTowardWall(int distance /* TODO: ajouter hook en argument */ ) throws UnableToMoveException
    {
        Speed oldSpeed = speed; 
        setLocomotionSpeed(Speed.INTO_WALL);
        moveLengthwise(distance, null, true);
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
     */
    public void moveToLocation(Vec2 aim, ArrayList<Hook> hooksToConsider, Table table) throws  PathNotFoundException, UnableToMoveException
    {
    	//TODO : preciser les obstacles a eviter
    	ArrayList<Vec2> path = pathDingDing.computePath(getPosition(),aim, EnumSet.noneOf(ObstacleGroups.class));
    	
		try 
		{
			followPath(path , hooksToConsider);
		} 
		catch (UnableToMoveException e) 
		{
			//si le chemin est bloque par un robot ennemi on appel a nouveau le pathdingding pour qu'il calcul un autre chemin
			if (e.reason.compareTo(UnableToMoveReason.OBSTACLE_DETECTED)==0)
			{
				ArrayList<Vec2> newPath = pathDingDing.computePath(getPosition(),aim, EnumSet.noneOf(ObstacleGroups.class));
				followPath(newPath , hooksToConsider);
			}
		}
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
     */
    public void moveToCircle(Circle aim, ArrayList<Hook> hooksToConsider, Table table) throws PathNotFoundException, UnableToMoveException
    {
    	//TODO : preciser les obstacles a eviter
    	ArrayList<Vec2> path = pathDingDing.computePath(getPosition(),aim.toVec2(),EnumSet.noneOf(ObstacleGroups.class)); // TODO ATTENTION a changer 
    	
    	
    	//retire une distance egale au rayon du cercle au dernier point du chemin (le centre du cercle)
    	
    	//on enleve le premier point (notre position)
    	//TODO : refaire ca propre dans PDD
    	path.remove(0);
    	
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
    	
    	
    	/* on ajoute le point du cercle B'=(B-A)*(L-r)/L+A
    	 * B le centre du cercle, r le rayon du cercle, A le point precedent dans le path et L la taille du dernier vecteur deplacement
    	 */
    	path.add(movementVector.dotFloat( (movementVector.length()-aim.radius)/movementVector.length() ).plusNewVector(precedentPathPoint));

    	try 
    	{
			followPath(path , hooksToConsider);
		} 
    	catch (UnableToMoveException e) 
    	{
    		//si le chemin est bloque par un robot ennemi on recalcule le chemin par un autre appel au pathdingding
			if (e.reason.compareTo(UnableToMoveReason.OBSTACLE_DETECTED)==0)
			{
				moveToCircle(new Circle(e.aim, 0), hooksToConsider, table);
			}
		}
    }

	/**
	 * Active l'asservissement en rotation du robot.
	 */
    public abstract void enableRotationnalFeedbackLoop();
    
	/**
	 * Active l'asservissement en translation du robot.
	 */
    public abstract void disableTranslationnalFeedbackLoop();

	
	public boolean getSymmetry()
	{
		return symmetry;
	}

	/**
	 * le robot demande l'etat de ses capteurs
	 * @param captor le nom du capteur dont on veut l'etat
	 * @return la valeur du capteur
	 * @throws SerialConnexionException si la connexion avec le capteur est interrompue
	 */
	public abstract Object getSensorValue(SensorNames captor) throws SerialConnexionException;

}
