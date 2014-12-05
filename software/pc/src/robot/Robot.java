package robot;

import java.util.ArrayList;

import hook.Hook;
import smartMath.Vec2;
import container.Service;
import enums.Speed;
import exceptions.Locomotion.UnableToMoveException;
import utils.Log;
import utils.Config;

/**
 *  Classe abstraite du robot, dont héritent RobotVrai et RobotChrono
 *  Quand une action peut être faite soit pour en connaitre le temps d'exéction, soit pour la faire en vrai, c'est elle qu'il faut utiliser
 * @author PF, marsu
 */

public abstract class Robot implements Service 
{
	
	/* ==============================================
	 *            DÉPLACEMENT HAUT NIVEAU
	 * ==============================================
	 */
	
	/**
	 * Immobilise le robot.
	 * Après l'appel de cette fonction, le robot sera immobile sur la table
	 */
	public abstract void immobilise();
	

	/**
	 * Fait tourner le robot (méthode bloquante)
	 * @param angle : valeur absolue en radiant de l'orientation que le robot doit avoir après cet appel
	 * @param hooksToConsider hooks a considérer lors de ce déplacement. Le hook n'est déclenché que s'il est dans cette liste et que sa condition d'activation est remplie
	 * @param expectsWallImpact true si le robot doit s'attendre a percuter un mur au cours de la rotation. false si les alentours du robot sont sensés être dégagés.
	 * @throws UnableToMoveException losrque quelque chose sur le chemin cloche et que le robot ne peut s'en défaire simplement: bloquage mécanique immobilisant le robot ou obstacle percu par les capteurs
	 */
    public abstract void turn(double angle, ArrayList<Hook> hooksToConsider, boolean expectsWallImpact) throws UnableToMoveException;
    
	/**
	 * Fait avancer le robot de la distance spécifiée. Le robot garde son orientation actuelle et va simplement avancer)
	 * C'est la méthode que les utilisateurs (externes au développement du système de locomotion) vont utiliser
	 * Cette méthode est bloquante: son exécution ne se termine que lorsque le robot a atteint le point d'arrivée
	 * @param distance en mm que le robot doit franchir
	 * @param hooksToConsider hooks a considérer lors de ce déplacement. Le hook n'est déclenché que s'il est dans cette liste et que sa condition d'activation est remplie	 
	 * @param expectsWallImpact true si le robot doit s'attendre a percuter un mur au cours du déplacement. false si la route est sensée être dégagée.
	 * @throws UnableToMoveException losrque quelque chose sur le chemin cloche et que le robot ne peut s'en défaire simplement: bloquage mécanique immobilisant le robot ou obstacle percu par les capteurs
	 */
    public abstract void moveLengthwise(int distance, ArrayList<Hook> hooksToConsider, boolean expectsWallImpact) throws UnableToMoveException;
    

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
	public abstract void set_vitesse(Speed speed);
	
	/**
	 * Change dans l'asservissement la position du robot sur la table .
	 * Après appel de cette méthode, le robot considèrera qu'il se trouve sur la table aux coordonnées fournies.
	 * Cette fonction n'est pas instantannée, un petit délai (de 300ms) pour que la communication série se fasse est nécéssaire.
	 * @param position
	 */
	public abstract void setPosition(Vec2 position);
	
	/**
	 * Change dans l'asservissement l'orientation du robot sur la table .
	 * Après appel de cette méthode, le robot considèrera qu'il se trouve sur la table avec l'orientation fournie.
	 * Cette fonction n'est pas instantannée, un petit délai (de 300ms) pour que la communication série se fasse est nécéssaire.
	 * @param orientation
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
	 * Donne la position du robot sur la table.
	 * Cette méthode est rapide mais peu précise: elle ne déclenche pas d'appel a la série pour obtenir une position a jour.
	 * La position revoyée est celle mémorisée lors de sa dernière mise a jour (la date de la dernière mise a jour est inconnue).
	 * @return la dernière position mémorisée du robot sur la table
	 */
    public abstract Vec2 getPositionFast();
    
	/**
	 * Donne l'orientation du robot sur la table.
	 * Cette méthode est rapide mais peu précise: elle ne déclenche pas d'appel a la série pour obtenir une orientation a jour.
	 * L'orientation revoyée est celle mémorisée lors de sa dernière mise a jour (la date de la dernière mise a jour est inconnue).
	 * @return la dernière orientation mémorisée du robot sur la table
	 */
    public abstract double getOrientationFast();
    
    /**
     * Fais attendre le robot.
     * C'est a utiliser au lieu d'attendre via Sleep.sleep, car dans robotChrono, au lieu d'attendre, on incrémente le chronomètre de la valeur coresspondante.
     * @param delay temps que le robot doit passer a attendre
     */
    public abstract void sleep(long delay);
    
	// système de log sur lequel écrire
	protected Log log;
	
	//endroit ou lire la configuration du robot
	protected Config config;

	// la table est symétrisée si on est équipe jaune
	protected boolean symmetry;
	
	// vitesse du robot sur la table
	protected Speed speed;

	/**
	 * Instancie le robot.
	 * Appell� par le container
	 * @param log : la sortie de log à utiliser
	 * @param config : sur quel objet lire la configuration du match
	 */
	public Robot(Config config, Log log)
	{
		this.config = config;
		this.log = log;
		updateConfig();
	}
	
	/**
	 * Met a jour la configuration de la classe via le fichier de configuration fourni par le sysème de container
	 */
	public void updateConfig()
	{
		symmetry = config.get("couleur").equals("jaune");
	}
	
	/**
	 * Donne la vitesse courrante a laquelle le robot avance et tourne sur lui même sur la table
	 * @return la vitesse courrante
	 */
	public Speed getSpeed()
	{
		return speed;
	}
	
	/**
	 * Fait tourner le robot (méthode bloquante)
	 * Attention: le pivot sera fait en supposant qu'il n'y a pas de hook a vérifier, et qu'on ne s'attends pas a percuter un obstacle
	 * @param angle : valeur relative en radiant de l'orientation que le robot doit avoir après cet appel
	 * @throws UnableToMoveException losrque quelque chose sur le chemin cloche et que le robot ne peut s'en défaire simplement: bloquage mécanique immobilisant le robot ou obstacle percu par les capteurs
	 */
	public void turnRelative(double angle) throws UnableToMoveException
	{
		turn(getOrientation() + angle, null, false);
	}
	
	/**
	 * Fait tourner le robot (méthode bloquante)
	 * Attention: le pivot sera fait en supposant qu'il n'y a pas de hook a vérifier, et qu'on ne s'attends pas a percuter un obstacle
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
	 * Attention: le pivot sera fait en supposant qu'il n'y a pas de hook a vérifier, et qu'on ne s'attends pas a percuter un obstacle
	 * @param angle : valeur absolue en radiant de l'orientation que le robot doit avoir après cet appel. L'orientation ne sera pas symétrisée, quelle que soit la couleur de l'équipe.
	 * @throws UnableToMoveException losrque quelque chose sur le chemin cloche et que le robot ne peut s'en défaire simplement: bloquage mécanique immobilisant le robot ou obstacle percu par les capteurs
	 */
    public void turnNoSymmetry(double angle) throws UnableToMoveException
    {
        if(symmetry)
            turn(Math.PI-angle, null, false);
        else
            turn(angle, null, false);
    }

    
    
    
    
    
    
    
    
   
    
tododvfuysdbkflms!g:ksfjqgd wk,dùgbf h,sq;dkbmnojf :hdvnckmbhig!lf< jgeubqkjdvcpnglbcm;n,fbnvg+5n+6gh54,h45,6b4n6,4bn,bn






    public void moveLengthwise(int distance) throws UnableToMoveException
    {
        moveLengthwise(distance, null, false);
    }

    public void moveLengthwise(int distance, ArrayList<Hook> hooks) throws UnableToMoveException
    {
        moveLengthwise(distance, hooks, false);
    }

    public void moveLengthwiseTowardWall(int distance) throws UnableToMoveException
    {
        Speed sauv_vitesse = speed; 
        set_vitesse(Speed.INTO_WALL);
        moveLengthwise(distance, null, true);
        set_vitesse(sauv_vitesse);
    }
    
    /**
     * Va au point "arrivée" en utilisant le pathfinding.
     * @param arrivee
     * @throws PathfindingException
     * @throws UnableToMoveException
     */
    public boolean va_au_point_pathfinding(Vec2 arrivee) throws UnableToMoveException
    {
    	// TODO
    	
    	return 666 == 42;
    }

    public abstract void desactiver_asservissement_rotation();
    public abstract void activer_asservissement_rotation();
    
}
