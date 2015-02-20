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
     * la largeur du robot (taille dans la direction où le robot n'avance pas)
     */
    private int robotWidth;
    
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
     * position réelle du robot (symetrisee)
     * non connue par les classes de plus haut niveau
     * TODO quand est-elle mise a jour ?
     */
    private Vec2 position = new Vec2();
    
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
     * TODO a quoi ça sert ?
     */
    private boolean directionPrecedente;
    
    public Locomotion(Log log, Config config, Table table, LocomotionCardWrapper deplacements)
    {
        this.log = log;
        this.config = config;
        this.deplacements = deplacements;
        this.table = table;
        updateConfig();
    }
    
    public void readjust()
    {
    	// TODO
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
    	/*
    	 * clacul de la position visee 
    	 * on vise une position eloignee mais on ne s'y deplacera pas, le robot ne fera que tourner
    	 */
    	Vec2 aim = new Vec2(
        (int) (position.x + 1000*Math.cos(angle)),
        (int) (position.y + 1000*Math.sin(angle))
        );

		vaAuPointGestionExceptions(aim, position, hooks, true, false, true);
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
        
        //on calcul la position visee que l'on symetrisera ensuite (TODO peut se symplifier)
        Vec2 aim = new Vec2(), aimNoSymetry = new Vec2(); 
        aimNoSymetry.x = (int) (position.x + distance*Math.cos(orientation));
        aimNoSymetry.y = (int) (position.y + distance*Math.sin(orientation));        

        // En fait, ici on prend en compte que la symétrie va inverser la consigne...
        if(symetry)
        {
        	aim.x = -aimNoSymetry.x;
            aim.y = aimNoSymetry.y;
        }
        else
        {
        	aim.x = aimNoSymetry.x;
            aim.y = aimNoSymetry.y;
        }

        // l'appel à cette méthode sous-entend que le robot ne tourne pas
        // il va donc en avant si la distance est positive, en arrière si elle est négative
        // si on est à 90°, on privilégie la marche avant
		vaAuPointGestionExceptions(aim, position, hooks, distance >= 0, wall, false);
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
    	//si un singe a mit null pour les hooks on le gere
    	if(hooks == null)
    		hooks = new ArrayList<Hook>();
    	
    	//un simple for (on vas au point 0 puis au point 1 etc.)
    	int size = path.size();
    	for(int i = 0; i < size; i++)
        {
            Vec2 aim = path.get(i);
			vaAuPointGestionMarcheArriere(aim, position, hooks, /*on suppose q'on ne se prends pas de mur (sinon la pathDingDing est a revoir)*/false, directionstrategy, /*on veut avancer*/false);
        }
    }

    /**
     * Bloquant. Gère la marche arrière automatique selon la stratégie demandée.
     * @param aim le point visé sur la table (consigne donné par plus haut niveau donc non symetrise)
     * @param givenPosition la position de depart du deplacement //TODO relique de trajectoire courbe a suprimer ?
     * @param hooks les potentiels hooks a prendre en compte (ne pas mettre null !)
     * @param mur vrai si on suppose qu'on vas se cogner dans un mur (et qu'on veut s'arreter des qu'on cogne)
     * @param strategy ce que la strategie choisit comme optimal (en avant, en arriere, au plus rapide)
     * @param turnOnly vrai si on veut uniquement tourner (et pas avancer)
     * @throws UnableToMoveException si le robot a un bloquage mecanique
     */
    private void vaAuPointGestionMarcheArriere(Vec2 aim, Vec2 givenPosition, ArrayList<Hook> hooks, boolean mur, DirectionStrategy strategy, boolean turnOnly) throws UnableToMoveException
    {
    	// on avance en fonction de ce que nous dit la strategie
    	if(strategy == DirectionStrategy.FORCE_BACK_MOTION)
    	{
    		directionPrecedente = false;
            vaAuPointGestionExceptions(aim, givenPosition, hooks, false, mur, turnOnly);
    	}
    	else if(strategy == DirectionStrategy.FORCE_FORWARD_MOTION)
    	{
    		directionPrecedente = true;
            vaAuPointGestionExceptions(aim, givenPosition, hooks, true, mur, turnOnly);
    	}
    	else //if(strategy == DirectionStrategy.FASTEST)
    	{
    		// Calcul du moyen le plus rapide (on se sert d'un calcul de produit scalaire)
	        Vec2 delta = aim.clone();
	        //TODO pourquoi delta est symetrise ici ?
	        if(symetry)
	            delta.x *= -1;
	        delta.minus(position);
	        // Le coeff 1000 vient du fait que Vec2 est constitué d'entiers
	        Vec2 orientationVec = new Vec2((int)(1000*Math.cos(orientation)), (int)(1000*Math.sin(orientation)));
	        
	        // On regarde le produit scalaire; si c'est positif, alors on est dans le bon sens, et inversement
	        directionPrecedente = delta.dot(orientationVec) > 0;
	        
	        vaAuPointGestionExceptions(aim, givenPosition, hooks, directionPrecedente, mur, turnOnly);
    	}
    }
    
    /**
     * bloquant
     * Gère les exceptions, c'est-à-dire les rencontres avec l'ennemi et les câlins avec un mur.
     * @param aim la position visee sur la table (consigne donné par plus haut niveau donc non symetrise)
     * @param givenPosition la position de depart du deplacement //TODO relique de trajectoire courbe a suprimer ?
     * @param hooks les potentiels hooks a prendre en compte (ne pas mettre null !)
     * @param isMovementForward vrai si on vas en avant et faux si on vas en arriere
     * @param wall vrai si on suppose qu'on vas se cogner dans un mur (et qu'on veut s'arreter des qu'on cogne)
     * @param turnOnly vrai si on veut uniquement tourner (et pas avancer)
     * @throws UnableToMoveException si le robot a un bloquage mecanique
     */
    private void vaAuPointGestionExceptions(Vec2 aim, Vec2 givenPosition, ArrayList<Hook> hooks, boolean isMovementForward, boolean wall, boolean turnOnly) throws UnableToMoveException
    {
        int maxTimeToWaitForEnemyToLeave = 600; // combien de temps attendre que l'ennemi parte avant d'abandonner
        int unexpectedWallImpactCounter = 2; // combien de fois on réessayer si on se prend un mur (si wall est a true alors les impacts sont attendus donc on s'en fout)
        boolean doItAgain;
        do {
        	//si on a pas d'erreur on ne recommence pas
            doItAgain = false;
            try
            {
                vaAuPointGestionHookCorrectionEtDetection(aim, givenPosition, hooks, isMovementForward, turnOnly);
            } catch (BlockedException e)
            {
                unexpectedWallImpactCounter--;
                immobilise();
                /*
                 * En cas de blocage, on recule (si on allait tout droit) ou on avance.
                 */
                // Si on s'attendait à un mur, c'est juste normal de se le prendre.
                if(!wall)
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
                    } catch (SerialConnexionException e1)
                    {
                        e1.printStackTrace();
                    } catch (BlockedException e1) {
                    	immobilise();
                        log.critical("On n'arrive pas à se dégager.", this);
					}
                    if(!doItAgain)
                        throw new UnableToMoveException();
                }
            } catch (UnexpectedObstacleOnPathException e)
            {
            	immobilise();
            	long detectionTime = System.currentTimeMillis();
                log.critical("Détection d'un ennemi! Abandon du mouvement.", this);
            	while(System.currentTimeMillis() - detectionTime < maxTimeToWaitForEnemyToLeave)
            	{
            		try {
            			detectEnemy(isMovementForward);
            			doItAgain = true; // si aucune détection
            			break;
            		}
            		catch(UnexpectedObstacleOnPathException e2)
            		{}
            	}
                if(!doItAgain)
                    throw new UnableToMoveException();
			}

        } while(doItAgain); // on recommence tant qu'il le faut

    // Tout s'est bien passé
    }
    
    /**
     * Bloquant. 
     * Gère les hooks, la correction de trajectoire et la détection.
     * @param aim la position visee sur la table (consigne donné par plus haut niveau donc non symetrise)
     * @param givenPosition la position de depart du deplacement //TODO relique de trajectoire courbe a suprimer ?
     * @param hooks les potentiels hooks a prendre en compte (ne pas mettre null !)
     * @param isMovementForward vrai si on vas en avant et faux si on vas en arriere
     * @param turnOnly vrai si on veut uniquement tourner (et pas avancer)
     * @throws BlockedException si le robot a un bloquage mecanique
     * @throws UnexpectedObstacleOnPathException si le robot rencontre un obstacle innatendu sur son chemin (par les capteurs)
     */
    private void vaAuPointGestionHookCorrectionEtDetection(Vec2 aim, Vec2 givenPosition, ArrayList<Hook> hooks, boolean isMovementForward, boolean turnOnly) throws UnexpectedObstacleOnPathException, BlockedException
    {
    	
        vaAuPointGestionSymetrie(aim, givenPosition, isMovementForward, turnOnly, false);
        do
        {
            updateCurrentPositionAndOrientation();
            
            // en cas de détection d'ennemi, une exception est levée
            detectEnemy(isMovementForward);

            //on evalue les hooks (non null !)
            for(Hook hook : hooks)
                hook.evaluate();
            
            // le fait de faire de nombreux appels permet de corriger la trajectoire
            correctAngle(aim, isMovementForward);

//            TODO pas de delay ?
//            Sleep.sleep(feedbackLoopDelay);
        } while(!isMotionEnded());
        
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
    	vaAuPointGestionSymetrie(aim, position, isMovementForward, true, true);
    }

    /**
     * Non bloquant. 
     * Gère la symétrie et la marche arrière. (si on est en marche arriere le aim doit etre modifié pour que la consigne vers le bas niveau soit bonne)
     * @param aim la position visee sur la table (consigne donné par plus haut niveau donc non symetrise)
     * @param givenPosition la position de depart du deplacement //TODO relique de trajectoire courbe a suprimer ?
     * @param isMovementForward vrai si on vas en avant et faux si on vas en arriere
     * @param turnOnly vrai si on veut uniquement tourner (et pas avancer)
     * @param isCorrection vrai si la consigne est une correction et pas un ordre de deplacement
     * @throws BlockedException si le robot rencontre un obstacle innatendu sur son chemin (par les capteurs)
     */
    private void vaAuPointGestionSymetrie(Vec2 aim, Vec2 givenPosition, boolean isMovementForward, boolean turnOnly, boolean isCorrection) throws BlockedException
    {
    	//ici on gere la symetrie des x
        Vec2 delta = aim.clone();
        if(symetry)
        {
            delta.x = -delta.x;
            givenPosition.x = -givenPosition.x;
        }
        
        updateCurrentPositionAndOrientation();

        delta.minus(givenPosition);
//        log.debug("Distance directe: "+delta.length()+", differenceDistance: "+differenceDistance, this);
        //calcul de la nouvelle distance et du nouvel angle
        double distance = delta.length();
        double angle =  Math.atan2(delta.y, delta.x);
        
        // si on a besoin de se retourner pour suivre la consigne de isMovementForward on le fait ici
        if(isMovementForward && distance < 0 || (!isMovementForward && distance > 0))
        {
            distance *= -1;
            angle += Math.PI;
        }
        
        vaAuPointGestionCourbe(aim, givenPosition, angle, distance, false, turnOnly, isCorrection);
    }
    
    /**FIXME j'en suis là !!!
     * 
     * Non bloquant. Avance, envoi a la serie
     * @param symmetrisedAim la position visee sur la table (symetrise)
     * @param givenPosition la position de depart du deplacement //TODO relique de trajectoire courbe a suprimer ?
     * @param angle l'angle dont il faut tourner (ordre pour la serie)
     * @param distance la distance dont il faut avancer (ordre pour la serie)
     * @param trajectoire_courbe TODO heu.... a suprimer ?
     * @param turnOnly vrai si on veut uniquement tourner (et pas avancer)
     * @param isCorrection vrai si la consigne est une correction et pas un ordre de deplacement
     * @throws BlockedException si le robot rencontre un obstacle innatendu sur son chemin (par les capteurs)
     */
    private void vaAuPointGestionCourbe(Vec2 symmetrisedAim, Vec2 givenPosition, double angle, double distance, boolean trajectoire_courbe, boolean turnOnly, boolean isCorrection) throws BlockedException
    {
		double delta = (orientation-angle) % (2*Math.PI);
		if(delta > Math.PI)
			delta -= 2*Math.PI;
		else if(delta < -Math.PI)
			delta += 2*Math.PI;
		delta = Math.abs(delta);
		if(delta > Math.PI)
			delta = 2*Math.PI - delta;
		
		/**
		 * Si on fait une correction, il faut vérifier la distance à la consigne et la correction
		 * Si elles sont grandes, alors on fait la correction en angle sans s'arrêter
		 * Si une au moins est petite, on annule la correction (par exemple, si le robot
		 * dépasse un peu la consigne, la correction le ferait se retourner ce qui
		 * n'est pas le résultat demandé)
		 */
		if(isCorrection)
		{
			// 5 cm
			double deltaAngle = Math.abs((orientation-angle) % (2*Math.PI));
			if(deltaAngle > Math.PI)
				deltaAngle -= 2*Math.PI;
			else if(deltaAngle < -Math.PI)
				deltaAngle += 2*Math.PI;
			if(givenPosition.squaredDistance(symmetrisedAim) > 2500 && Math.abs(deltaAngle) < Math.PI/2)
//			if(delta < 3*Math.PI/180)
				trajectoire_courbe = true;
			else
				return;
		}
        try
        {
            deplacements.turn(angle);
            if(!trajectoire_courbe) // sans virage : la première rotation est bloquante
                while(!isMotionEnded()) // on attend la fin du mouvement
                    Sleep.sleep(feedbackLoopDelay);
            
/*            // TODO: passer en hook
            ObstacleRectangular obstacle = new ObstacleRectangular(position, consigne);
        	if(obstacle.isCollidingObstacleFixe())
        	{
        		log.debug("Le robot a demandé à avancer dans un obstacle. Ordre annulé.", this);
        		throw new WallCollisionDetectedException();
        	}
*/
            if(!turnOnly)
            	deplacements.moveLengthwise(distance);
        } catch (SerialConnexionException e) {
            e.printStackTrace();
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
     * @param detection_collision
     * @param sans_lever_exception
     * @return oui si le robot est arrivé à destination, non si encore en mouvement
     * @throws BlockedException
     * @throws FinMatchException 
     * @throws UnexpectedObstacleOnPathException
     */
    private boolean isMotionEnded() throws BlockedException
    {
    	// TODO: débugger!
        // récupérations des informations d'acquittement
        try {
        	
        	// met a jour: 	l'écart entre la position actuelle et la position sur laquelle on est asservi
        	//				la variation de l'écart a la position sur laquelle on est asservi
        	//				la puissance demandée par les moteurs 	
            deplacements.refreshFeedbackLoopStatistics();
            
            // lève une exeption de blocage si le robot patine (ie force sur ses moteurs sans bouger) 
            deplacements.raiseExeptionIfBlocked();
            
            // robot arrivé?
            return !deplacements.isRobotMoving();

        } 
        catch (SerialConnexionException e) 
        {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean isEnemyHere()
    {
		try {
			detectEnemy(true);
			return false;
		} catch (UnexpectedObstacleOnPathException e) {
			return true;
		}
    }
    
    /**
     * fonction vérifiant que l'on ne va pas taper dans le robot adverse. 
     * @param devant: fait la détection derrière le robot si l'on avance à reculons 
     * @throws UnexpectedObstacleOnPathException si obstacle sur le chemin
     */
    private void detectEnemy(boolean devant) throws UnexpectedObstacleOnPathException
    {
        int signe = -1;
        if(devant)
            signe = 1;
        
        int rayon_detection = robotWidth/2 + detectionDistance;
        Vec2 centre_detection = new Vec2((int)(signe * rayon_detection * Math.cos(orientation)), (int)(signe * rayon_detection * Math.sin(orientation)));
        centre_detection.plus(position);
        if(table.getObstacleManager().isDiscObstructed(centre_detection, detectionDistance))
        {
            log.warning("Ennemi détecté en : " + centre_detection, this);
            throw new UnexpectedObstacleOnPathException();
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
            double[] infos = deplacements.getCurrentPositionAndOrientation();
            position.x = (int)infos[0];
            position.y = (int)infos[1];
            orientation = infos[2]/1000; // car getCurrentPositionAndOrientation renvoie des milliradians
        }
        catch(SerialConnexionException e)
        {
            e.printStackTrace();
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
        log.debug("Arrêt du robot en "+position, this);
        try {
            deplacements.immobilise();
        } catch (SerialConnexionException e) {
            e.printStackTrace();
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
		try {
			deplacements.setX(this.position.x);
	        deplacements.setY(this.position.y);
		} catch (SerialConnexionException e) {
			e.printStackTrace();
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
    		deplacements.setOrientation(this.orientation);
        } catch (SerialConnexionException e) {
            e.printStackTrace();
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
            e.printStackTrace();
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
