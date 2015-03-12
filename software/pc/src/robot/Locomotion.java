package robot;

import hook.Hook;

import java.util.ArrayList;

import robot.cardsWrappers.LocomotionCardWrapper;
import smartMath.Vec2;
import table.Table;
import utils.Config;
import utils.Log;
import utils.Sleep;
import container.Service;
import enums.UnableToMoveReason;
import exceptions.Locomotion.BlockedException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.Locomotion.UnexpectedObstacleOnPathException;
import exceptions.serial.SerialConnexionException;

/**
 * Entre Deplacement (appels à la série) et RobotVrai (déplacements haut niveau), Locomotion
 * s'occupe de la position, de la symétrie, des hooks, des trajectoires courbes et des blocages.
 * Structure, du bas au haut niveau: symétrie, hook, trajectoire courbe et blocage.
 * Les méthodes "non-bloquantes" se finissent alors que le robot roule encore.
 * (les méthodes non-bloquantes s'exécutent très rapidement)
 * Les méthodes "bloquantes" se finissent alors que le robot est arrêté.
 * @author pf
 *
 */

public class Locomotion implements Service
{

	/**
	 * le log si on a des erreurs
	 */
    private Log log;
    /**
     * la config ...
     */
    private Config config;
    /**
     * la table sur laquelle evolue le robot
     */
    private Table table;
    /**
     * la carte de deplacement utilisee
     */
    private LocomotionCardWrapper deplacements;
    /**
     * la longueur du robot (taille dans la direction où le robot avance)
     */
    private int robotLength;
    
    /**
     * rayon du cercle place devant le robot qui sert pour la detection
     * 
     *la zone de détection d'obstacle est un disque comme suit:
     *     			          o  o
     *    			+----+ o        o		 Sens de déplacement du robot: ====>
     *    robot ->	|    |o          o
     *    			|    |o          o  <- Zone de vérification (ce disque est tangent au robot)
     *    			+----+ o        o 
     *   			          o  o
     */
    private int detectionDistance;
    
    /**
     * Position "haut niveau" du robot, celle du robot
     * qui commence toujours à droite de la table
     */
    private Vec2 position = new Vec2();
    
    /**
     * la position visee au final par le deplacement
     */
    private Vec2 finalAim = new Vec2();
    
    /**
     * orientation réelle du robot (symetrisee)
     * non connue par les classes de plus haut niveau
     */
    private double orientation;
    
    /**
     * vrai si on est a gauche de la table (x<0 et jaune)
     * faux sinon
     */
    private boolean symetry;
    
    /**
     * temps d'attente entre deux boucles d'acquitement
     */
    private int feedbackLoopDelay = 10;
    /**
     * la distance dont le robot vas avancer pour se degager en cas de bloquage mecanique
     */
    private int distanceToDisengage = 50;
    /**
     * l'angle dont le robot vas tourner pour se degager en cas de blouage mecanique
     */
    private double angleToDisengage;
    
    /**
     * 	La distance maximale pour une correction rotationelle 
     * 	La correction ne sera effectuée que si le robot est loin de son point d'arrivée.
     */
    private int maxLengthCorrectionThreeshold = 50;
    
    /**
     * 	L'orientation maximale pour une correction rotationelle 
     * 	La correction ne sera effectuée que si le robot est assez eloigné de son orientation souhaitée.
     */
    private double maxRotationCorrectionThreeshold = 0.05;

    
    
    public Locomotion(Log log, Config config, Table table, LocomotionCardWrapper deplacements)
    {
        this.log = log;
        this.config = config;
        this.deplacements = deplacements;
        this.table = table;
        updateConfig();
    }
    
    /**
     * recale le robot qui a perdu sa position acuelle, rentre dans les murs ou les obstacles fixes
     */
    public void readjust()
    {
    	// TODO
    	//se delacer vers le mur suppose le plus proche en abscisse
    	//des qu'on rentre dedans on met a jour netre position
    	//on fait la meme chose avec l'autre coordonnee
    }

    
    /**
     * Fait tourner le robot (méthode bloquante)
     * Une manière de tourner qui réutilise le reste du code, car tourner
     * n'en devient plus qu'un cas particulier (celui où... on n'avance pas)
     * @param angle l'angle vise (en absolut)
     * @param hooks les potentiels hooks a prendre en compte (ne pas mettre null !)
     * @throws UnableToMoveException si le robot a un bloquage mecanique
     */
    public void turn(double angle, ArrayList<Hook> hooks) throws UnableToMoveException
    {
    	/**
    	 * calcul de la position visee du haut niveau
    	 *   on vise une position eloignee mais on ne s'y deplacera pas, le robot ne fera que tourner
    	 */
    	Vec2 aim = new Vec2(

        (int) (position.x + 1000*Math.cos(angle)),
        (int) (position.y + 1000*Math.sin(angle))
        );
    	finalAim = aim;

		moveToPointException(aim, hooks, true, false, true, false);

    }
    /**
     * Fait tourner le robot (méthode bloquante)
     * Une manière de tourner qui réutilise le reste du code, car tourner
     * n'en devient plus qu'un cas particulier (celui où... on n'avance pas)
     * @param isTurnRelative vrai si l'angle vise est en relatif et pa sen absolut
     * @param angle l'angle vise (en absolut)
     * @param hooks les potentiels hooks a prendre en compte (ne pas mettre null !)
     * @throws UnableToMoveException si le robot a un bloquage mecanique
     */
    public void turn(double angle, ArrayList<Hook> hooks, boolean isTurnRelative) throws UnableToMoveException
    {
    	/**
    	 * calcul de la position visee du haut niveau
    	 * on vise une position eloignee mais on ne s'y deplacera pas, le robot ne fera que tourner
    	 */
    	Vec2 aim = new Vec2(

        (int) (position.x + 1000*Math.cos(angle)),
        (int) (position.y + 1000*Math.sin(angle))
        );
    	finalAim = aim;

		moveToPointException(aim, hooks, true, false, true, isTurnRelative);

    }
    
    
    /**
     * Fait avancer le robot de "distance" (en mm).
     * @param distance la distance dont le robot doit se deplacer
     * @param hooks les potetniels hooks a prendre en compte (ne pas mettre null !)
     * @param wall vrai si on supppose qu'on vas se cogner dans un mur (et qu'il ne faut pas pousser dessus)
     * @throws UnableToMoveException si le robot a un bloquage mecanique
     */
    public void moveLengthwise(int distance, ArrayList<Hook> hooks, boolean wall) throws UnableToMoveException
    {
        log.debug("Avancer de "+Integer.toString(distance), this);
        
        /**
         * aim est la visée du haut niveau, qui commence toujours à droite
         */
        Vec2 aim = new Vec2(); 
        aim.x = (int) (position.x + distance*Math.cos(orientation));
        aim.y = (int) (position.y + distance*Math.sin(orientation));      
        finalAim = aim;
        // l'appel à cette méthode sous-entend que le robot ne tourne pas
        // il va donc en avant si la distance est positive, en arrière si elle est négative
        // si on est à 90°, on privilégie la marche avant
		moveToPointException(aim, hooks, distance >= 0, wall, false, false);
    }
        
    /**
     * Suit un chemin en ligne brisee
     * @param path le chemin a suivre (un arraylist de Vec2 qui sont les point de rotation du robot)
     * @param hooks les potentiels hooks a prendre en compte (ne pas mettre null !)
     * @param directionstrategy ce que la strategie choisit comme optimal (en avant, en arriere, au plus rapide)
     * @throws UnableToMoveException si le robot a un bloquage mecanique
     */
    public void followPath(ArrayList<Vec2> path, ArrayList<Hook> hooks, DirectionStrategy directionstrategy) throws UnableToMoveException
    {
    	//si un singe a mie de pain null pour les hooks on le gere
    	if(hooks == null)
    		hooks = new ArrayList<Hook>();
    	
    	//un simple for (on vas au point 0 puis au point 1 etc.)
    	finalAim = path.get(path.size()-1);
    	for(int i = 0; i < path.size(); i++)
        {
            Vec2 aim = path.get(i);
			moveToPointForwardBackward(aim, hooks, /*on suppose q'on ne se prends pas de mur (sinon la pathDingDing est a revoir)*/false, directionstrategy, /*on veut avancer*/false);
        }
    	
		
    }


    /**
     * Bloquant. Gère la marche arrière automatique selon la stratégie demandée.
     * @param aim le point visé sur la table (consigne donné par plus haut niveau donc non symetrise)
     * @param hooks les potentiels hooks a prendre en compte (ne pas mettre null !)
     * @param mur vrai si on suppose qu'on vas se cogner dans un mur (et qu'on veut s'arreter des qu'on cogne)
     * @param strategy ce que la strategie choisit comme optimal (en avant, en arriere, au plus rapide)
     * @param turnOnly vrai si on veut uniquement tourner (et pas avancer)
     * @throws UnableToMoveException si le robot a un bloquage mecanique
     */
    private void moveToPointForwardBackward(Vec2 aim, ArrayList<Hook> hooks, boolean mur, DirectionStrategy strategy, boolean turnOnly) throws UnableToMoveException
    {
    	// on avance en fonction de ce que nous dit la strategie
    	if(strategy == DirectionStrategy.FORCE_BACK_MOTION)
    	{
            moveToPointException(aim, hooks, false, mur, turnOnly, false);
    	}
    	else if(strategy == DirectionStrategy.FORCE_FORWARD_MOTION)
    	{
            moveToPointException(aim, hooks, true, mur, turnOnly, false);
    	}
    	else //if(strategy == DirectionStrategy.FASTEST)
    	{
    		// Calcul du moyen le plus rapide (on se sert d'un calcul de produit scalaire)
	        Vec2 delta = aim.clone();
	        delta.minus(position);
	        // Le coeff 1000 vient du fait que Vec2 est constitué d'entiers
	        Vec2 orientationVec = new Vec2((int)(1000*Math.cos(orientation)), (int)(1000*Math.sin(orientation)));
	     
	        // On regarde le produit scalaire; si c'est positif, alors on est dans le bon sens, et inversement
	        boolean direction = delta.dot(orientationVec) >= 0;
	        
	        moveToPointException(aim, hooks, direction, mur, turnOnly, false);
    	}
    }
    
    /**
     * bloquant
     * Gère les exceptions, c'est-à-dire les rencontres avec l'ennemi et les câlins avec un mur.
     * @param aim la position visee sur la table (consigne donné par plus haut niveau donc non symetrise)
     * @param hooks les potentiels hooks a prendre en compte (ne pas mettre null !)
     * @param isMovementForward vrai si on vas en avant et faux si on vas en arriere
     * @param headingToWall vrai si on suppose qu'on vas se cogner dans un mur (et qu'on veut s'arreter des qu'on cogne)
     * @param isTurnRelative vrai si l'angle vise est relatif et pas absolut
     * @param turnOnly vrai si on veut uniquement tourner (et pas avancer)
     * @throws UnableToMoveException si le robot a un bloquage mecanique
     */
    private void moveToPointException(Vec2 aim, ArrayList<Hook> hooks, boolean isMovementForward, boolean headingToWall, boolean turnOnly, boolean isTurnRelative) throws UnableToMoveException
    {
        int maxTimeToWaitForEnemyToLeave = 600; // combien de temps attendre que l'ennemi parte avant d'abandonner
        int unexpectedWallImpactCounter = 2; // combien de fois on réessayer si on se prend un mur (si wall est a true alors les impacts sont attendus donc on s'en fout)
        boolean doItAgain;
        do 
        {
        	//si on a pas d'erreur on ne recommence pas
            doItAgain = false;
            try
            {
                moveToPointCorrectAngleAndDetectEnnemy(aim, hooks, isMovementForward, turnOnly, isTurnRelative);
            }
            catch (BlockedException e)
            {
                log.critical("Haut : Catch de "+e+" dans moveToPointException", this);

                unexpectedWallImpactCounter--;
                immobilise();
                /*
                 * En cas de blocage, on recule (si on allait tout droit) ou on avance.
                 */
                // Si on s'attendait à un mur, c'est juste normal de se le prendre.
                if(!headingToWall)
                {
                    try
                    {
                        log.warning("On n'arrive plus à avancer. On se dégage", this);
                        if(turnOnly)
                        {
                        	// TODO: les appels à déplacements sont non bloquants, il faut rajouter des sleeps
                        	// on alterne rotation à gauche et à droite
                        	if((unexpectedWallImpactCounter & 1) == 0)
                        		deplacements.turn(orientation+angleToDisengage);
                        	else
                        		deplacements.turn(orientation-angleToDisengage);
                        }
                        else if(isMovementForward)
                            deplacements.moveLengthwise(distanceToDisengage);
                        else
                            deplacements.moveLengthwise(-distanceToDisengage);
                        while(!isMotionEnded());
                    		doItAgain = true; // si on est arrivé ici c'est qu'aucune exception n'a été levée
                    } 
                    catch (SerialConnexionException e1)
                    {
                        log.critical("On ne fait rien après ceci: Catch de "+e1+" dans moveToPointException", this);
                    } 
                    catch (BlockedException e1)
                    {
                        log.critical("Catch de "+e1+" dans moveToPointException", this);
                    	immobilise();                       
                        log.critical("On n'arrive pas à se dégager", this);
					}
                    if(!doItAgain)
                    {
                        log.critical("Lancement de UnableToMoveException dans MoveToPointException, visant "+finalAim.x+" :: "+finalAim.y+" cause physique", this);
                        throw new UnableToMoveException(finalAim, UnableToMoveReason.PHYSICALLY_BLOCKED);
                    }
                }
            }
            
            catch (UnexpectedObstacleOnPathException unexpectedObstacle)
            {
                log.critical("Haut: Catch de "+unexpectedObstacle+" dans moveToPointException", this); 

            	immobilise();//FIXME  : le robot s'arrete en permanence 
            	long detectionTime = System.currentTimeMillis();
                log.critical("Détection d'un ennemi! Abandon du mouvement.", this);
            	while(System.currentTimeMillis() - detectionTime < maxTimeToWaitForEnemyToLeave)//TODO virer ?
            	{
            		try
            		{
            			detectEnemy(isMovementForward);
            			doItAgain = true; // si aucune détection
            			break;
            		}
            		catch(UnexpectedObstacleOnPathException e2)
            		{
                        log.critical("Catch de "+e2+" dans moveToPointException", this);
            		}
            	}

                if(!doItAgain)
                {
                    log.critical("UnableToMoveException dans MoveToPointException, visant "+finalAim.x+" :: "+finalAim.y+" cause : detection d'obstacle", this);
                    throw new UnableToMoveException(finalAim, UnableToMoveReason.OBSTACLE_DETECTED);
                }
			}

        } 
        while(doItAgain)
        	;     
        ; // on recommence tant qu'il le faut


    // Tout s'est bien passé

    }
    
    /**
     * Bloquant. 
     * Gère les hooks, la correction de trajectoire et la détection.
     * @param aim la position visee sur la table (consigne donné par plus haut niveau donc non symetrise)
     * @param hooks les potentiels hooks a prendre en compte (ne pas mettre null !)
     * @param isMovementForward vrai si on vas en avant et faux si on vas en arriere
     * @param turnOnly vrai si on veut uniquement tourner (et pas avancer)
     * @param isTurnRelative vrai si l'angle est relatif et pas absolut
     * @throws BlockedException si le robot a un bloquage mecanique
     * @throws UnexpectedObstacleOnPathException si le robot rencontre un obstacle innatendu sur son chemin (par les capteurs)
     */
    private void moveToPointCorrectAngleAndDetectEnnemy(Vec2 aim, ArrayList<Hook> hooks, boolean isMovementForward, boolean turnOnly, boolean isTurnRelative) throws UnexpectedObstacleOnPathException, BlockedException
    {         	
    	double time=System.currentTimeMillis();
        moveToPointSymmetry(aim, isMovementForward, turnOnly, false, isTurnRelative);
        do
        { 	
        	// en cas de détection d'ennemi, une exception est levée
            detectEnemy(isMovementForward);
            
            updateCurrentPositionAndOrientation();

            //on evalue les hooks (non null !)
            if(hooks != null)
	            for(Hook hook : hooks)
	                hook.evaluate();
            log.debug("logs tous evalues", this);
                        
            // le fait de faire de nombreux appels permet de corriger la trajectoire
            correctAngle(aim, isMovementForward);
            
            log.critical("Temps pour finir la boucle d'asservissement "+(System.currentTimeMillis()-time), this);
            time=System.currentTimeMillis();
            
            // On sleep pour eviter le spam de la serie
            Sleep.sleep(feedbackLoopDelay);

        } 
        while(!isMotionEnded());
    }


    /**
     * donne une consigne d'un nouvel angle a atteindre (pour corriger la trajectoire en cours de mouvement)
     * @param aim la point vise (non symetrisee)
     * @param isMovementForward vrai si on vas en avant et faux si on vas en arriere
     * @throws BlockedException si le robot a un bloquage mecanique
     */
    private void correctAngle(Vec2 aim, boolean isMovementForward) throws BlockedException
    {
    	//envoi de la consigne avec turnOnly a true et a isCorrection a true (c'est bien une correction et on ne veut que tourner)
    	moveToPointSymmetry(aim, isMovementForward, true, true, false);
    }

    /**
     * Non bloquant. 
     * Gère la symétrie et la marche arrière. (si on est en marche arriere le aim doit etre modifié pour que la consigne vers le bas niveau soit bonne)
     * @param aim la position visee sur la table (consigne donné par plus haut niveau donc non symetrise)
     * @param isMovementForward vrai si on vas en avant et faux si on vas en arriere
     * @param turnOnly vrai si on veut uniquement tourner (et pas avancer)
     * @param isCorrection vrai si la consigne est une correction et pas un ordre de deplacement
     * @param isTurnRelative vrai si l'angle est relatif et pas absolut
     * @throws BlockedException si le robot rencontre un obstacle innatendu sur son chemin (par les capteurs)
     */
    private void moveToPointSymmetry(Vec2 aim, boolean isMovementForward, boolean turnOnly, boolean isCorrection, boolean isTurnRelative) throws BlockedException
    {

        updateCurrentPositionAndOrientation();


        Vec2 givenPosition = position.clone();//Position est la position du bas niveau
        Vec2 aimSymmetrized = aim.clone();    // aim est celle du haut
        if(symetry)
        {
        	givenPosition.x=-givenPosition.x;
        	aimSymmetrized.x= -aimSymmetrized.x;
        }
        Vec2 delta = aimSymmetrized.clone();
        delta.minus(givenPosition);
//        log.debug("Distance directe: "+delta.length()+", differenceDistance: "+differenceDistance, this);
        //calcul de la nouvelle distance et du nouvel angle
        double distance = delta.length();
        double angle =  Math.atan2(delta.y, delta.x);//Angle en absolu 
        //if(symetry)
        //	angle = Math.PI- angle;
        
        // si on a besoin de se retourner pour suivre la consigne de isMovementForward on le fait ici
        if(isMovementForward && distance < 0 || (!isMovementForward && distance > 0))
        {
            distance *= -1;
            angle += Math.PI;
        }
        
        // On passe l'angle d'absolu à relatif : getOrientation se gere de la symetrie
       
        // on annule la correction si on est trop proche de la destination
        if(isCorrection) 
           if(aimSymmetrized.clone().minusNewVector( givenPosition ).length() <  maxLengthCorrectionThreeshold )
	        	moveToPointSerialOrder(aimSymmetrized, givenPosition, angle, distance, turnOnly, isCorrection,isTurnRelative);
	        else 
	        	return;// Si on est trop proche, on ne fais rien.
        else 
        	moveToPointSerialOrder(aimSymmetrized, givenPosition, angle, distance, turnOnly, isCorrection,isTurnRelative);
        

    }
    
    /**
     * 
     * Non bloquant. 
     * Avance, envoi a la serie
     * @param symmetrisedAim la position visee sur la table (symetrise)
     * @param givenPosition la position de depart du deplacement
     * @param angle l'angle dont il faut tourner (ordre pour la serie)
     * @param distance la distance dont il faut avancer (ordre pour la serie)
     * @param turnOnly vrai si on veut uniquement tourner (et pas avancer)
     * @param isCorrection vrai si la consigne est une correction et pas un ordre de deplacement
     * @throws BlockedException si le robot rencontre un obstacle innatendu sur son chemin (par les capteurs)
     */
    private void moveToPointSerialOrder(Vec2 symmetrisedAim, Vec2 givenPosition, double angle, double distance, boolean turnOnly, boolean isCorrection, boolean isTurnRelative) throws BlockedException
    {
    	boolean trajectoire_courbe = false;

    	// Ce code fait juste un modulo 2*pi, avec un résultat entre -PI et +PI
		double delta = (angle-orientation) % (2*Math.PI);
		if(delta > Math.PI)
			delta -= 2*Math.PI;
		else if(delta < -Math.PI)
			delta += 2*Math.PI;
		
		/**
		 * Si on fait une correction, il faut vérifier la distance à la consigne et la correction
		 * Si la distance est grande et l'angle petit, alors on fait la correction en angle sans s'arrêter
		 * Sinon on annule la correction 
		 * (par exemple, si le robot dépasse un peu la consigne, la correction le ferait se retourner ce qui n'est pas le résultat demandé)
		 */
		if(isCorrection)
		{
			//Si la distance est grande et l'angle petit, alors on fait la correction en angle
			if(givenPosition.squaredDistance(symmetrisedAim) > Math.pow(maxLengthCorrectionThreeshold,2) && Math.abs(delta) < Math.PI/4)
				//on active la correction (on attendra pas d'avoir fini de tourner (le robot) pour reprendre le programme)
				trajectoire_courbe = true;
			else
				return;
			
		}

        try
        {
        	if(isCorrection && Math.abs(delta) > maxRotationCorrectionThreeshold)
        	{
        		if (!isTurnRelative)
        		{
        			deplacements.turn(angle);  // On ne tourne que si on est assez loin de l'orientation voulue
        		}
        		else
        		{
        			deplacements.turnRelative(angle);
        		}
                log.debug("Angle corrigé", this);
        	}
        	else if(!isCorrection)// Si ca n'est pas  une correction
        	{
        		if (!isTurnRelative)
        		{
        			deplacements.turn(angle);
        		}
        		else
        		{
        			deplacements.turnRelative(angle);
        		}
        	}
        	

            // sans virage : la première rotation est bloquante
            if(!trajectoire_courbe) 
            	// on attend la fin du mouvement
                while(!isMotionEnded()) 
                    Sleep.sleep(feedbackLoopDelay);
            
            if(!turnOnly)
            	deplacements.moveLengthwise(distance);
            

        } 
        catch (SerialConnexionException e)
        {
            log.critical("Catch de "+e+" dans moveToPointSerialOrder", this);
        }
    }

    /**
     * Boucle d'acquittement générique. Retourne des valeurs spécifiques en cas d'arrêt anormal (blocage, capteur)
     *  	
     *  	false : si on roule
     *  	true : si on est arrivé à destination
     *  	exception : si patinage
     * 
     * 
     * @return oui si le robot est arrivé à destination, non si encore en mouvement
     * @throws BlockedException si patinage (donc bloquage mecanique)
     */
    private boolean isMotionEnded() throws BlockedException
    {
        try 
        {
        	// récupérations des informations d'acquittement
        	boolean[] infos=deplacements.isRobotMovingAndAbnormal();
        	// 0-false : le robot ne bouge pas
        	
        	//log.debug("Test deplacement : reponse "+ infos[0] +" :: "+ infos[1], this);
        	
        	if(!infos[0])//si le robot ne bouge plus
        	{
        		if(infos[1])//si le robot patine
        		{
                    log.critical("Robot bloqué, lancement de BlockedException dans isMotionEnded", this);
                    throw new BlockedException ();
        		}
        		else
        		{
            		log.debug("Arrivés a destination", this);
        			return !infos[0];//On est arrivés
        		}
        	}
        	else
        	{    
        		return !infos[0];//toujours pas arrivé
        	}
        } 
        catch (SerialConnexionException e) 
        {
            log.critical("Catch de "+e+" dans isMotionEnded", this);
            return false;
        }
    }
    
    public boolean isEnemyHere()
    {
		try 
		{
			detectEnemy(true);
			return false;
		}
		catch (UnexpectedObstacleOnPathException e)
		{
            log.critical("Catch de "+e+" dans isEnemyHere", this);
			return true;
		}
    }
    
    /**
     * fonction vérifiant que l'on ne va pas taper dans le robot adverse.
     * test si le cercle devant (ou derriere en fonction du mouvement) est vide d'obstacle
     * @param front vrai si on veut detecter a l'avant du robot (donc si on avance en marche avant)
     * @throws UnexpectedObstacleOnPathException si obstacle sur le chemin
     */
    private void detectEnemy(boolean front) throws UnexpectedObstacleOnPathException
    {
        int signe = -1;
        if(front)
            signe = 1;
        
        //rayon du cercle de detection
        int detectionRadius = robotLength/2 + detectionDistance;
        //centre du cercle de detection
        Vec2 detectionCenter = new Vec2((int)(signe * detectionRadius * Math.cos(orientation)), (int)(signe * detectionRadius * Math.sin(orientation))); //centre par rapport au cnetre de position du robot
        detectionCenter.plus(position);
        
        if(table.getObstacleManager().isDiscObstructed(detectionCenter, detectionDistance))
        {
            log.warning("Ennemi détecté en : " + detectionCenter, this);
            log.warning( "Lancement de UnexpectedObstacleOnPathException dans detectEnemy", this);
            throw new UnexpectedObstacleOnPathException();
        }
        else 
        {
			log.debug("Pas d'ennemi detecté", this);
		}

    }

    /**
     * Met à jour position et orientation via la carte d'asservissement.
     * @throws FinMatchException 
     * @throws SerialConnexionException
     */
    
    private void updateCurrentPositionAndOrientation()
    {
        try {
            float[] infos = deplacements.getCurrentPositionAndOrientation();
            position.x = (int)infos[0];
            position.y = (int)infos[1];
            orientation = infos[2]; // car getCurrentPositionAndOrientation renvoie des radians
            if(symetry)
            	orientation = Math.PI - orientation;
        }
        catch(SerialConnexionException e)
        {
            log.critical("Catch de "+e+" dans updateCurrentPositionAndOrientation", this);
        }
    }

    @Override
    public void updateConfig()
    {
    	detectionDistance = Integer.parseInt(config.getProperty("distance_detection"));
        distanceToDisengage = Integer.parseInt(config.getProperty("distance_degagement_robot"));
        feedbackLoopDelay = Integer.parseInt(config.getProperty("sleep_boucle_acquittement"));
        angleToDisengage = Double.parseDouble(config.getProperty("angle_degagement_robot"));
		symetry = config.getProperty("couleur").replaceAll(" ","").equals("jaune");
    }

    /**
     * Arrête le robot.
     * @throws FinMatchException 
     */
    public void immobilise()
    {
        log.warning("Arrêt du robot en "+position, this);
        try 
        {
            deplacements.immobilise();
        } 
        catch (SerialConnexionException e) 
        {
            log.critical("Catch de "+e+" dans immobilise", this);
        }           
    }

    /**
     * Met à jour la position. A ne faire qu'en début de match.
     * @param position
     * @throws SerialConnexionException 
     * @throws FinMatchException 
     */
    public void setPosition(Vec2 position)
    {
        this.position = position.clone();
        if(symetry)
        	this.position.x = -this.position.x;
		try 
		{
			deplacements.setX(this.position.x);
	        deplacements.setY(this.position.y);
		} 
		catch (SerialConnexionException e) 
		{
            log.critical("Catch de "+e+" dans setPosition", this);
		}
		Sleep.sleep(300);
    }

    /**
     * Met à jour l'orientation. A ne faire qu'en début de match.
     * @param orientation
     * @throws FinMatchException 
     */
    public void setOrientation(double orientation)
    {
        this.orientation = orientation;
        if(symetry)
        	this.orientation = Math.PI-this.orientation;
        try {
    		deplacements.setOrientation(orientation);
        } catch (SerialConnexionException e) {
            log.critical("Catch de "+e+" dans setOrientation", this);
        }
    }

    /**
     * 
     * @return la position du robot en debut de match
     */
    public Vec2 getPosition()
    {
        updateCurrentPositionAndOrientation();
        Vec2 out = position.clone();
        if(symetry)
        	out.x = -out.x;
        return out;
    }

    /**
     * 
     * @return l'orientation du robot en debut de match
     */
    public double getOrientation()
    {
        updateCurrentPositionAndOrientation();
        if(symetry)
        	return Math.PI-orientation;
        else
        	return orientation;
    }

    public void desasservit()
    {
        try
        {
            deplacements.disableRotationnalFeedbackLoop();
            deplacements.disableTranslationnalFeedbackLoop();
        } catch (SerialConnexionException e)
        {
            log.critical("Catch de "+e+" dans desasservit", this);

        }
    }

    public void setRotationnalSpeed(int pwm) throws SerialConnexionException
    {
        deplacements.setRotationnalSpeed(pwm);
    }

    public void setTranslationnalSpeed(int pwm) throws SerialConnexionException
    {
        deplacements.setTranslationnalSpeed(pwm);
    }

    
    public void asservit() throws SerialConnexionException
    {
        deplacements.enableRotationnalFeedbackLoop();
        deplacements.enableTranslationnalFeedbackLoop();
    }
    
    public void initialiser_deplacements()
    {}
    
    public void disableRotationnalFeedbackLoop() throws SerialConnexionException
    {
		deplacements.disableRotationnalFeedbackLoop();
    }

    public void enableRotationnalFeedbackLoop() throws SerialConnexionException
    {
		deplacements.enableRotationnalFeedbackLoop();
    }

	public void disableTranslationalFeedbackLoop() throws SerialConnexionException
	{
		deplacements.disableTranslationnalFeedbackLoop();
	}

	public void close()
	{
		deplacements.closeLocomotion();
	}
	
}
