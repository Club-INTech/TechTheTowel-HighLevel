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

    private Log log;
    private Config config;
    private Table table;
    private int largeur_robot;
    private int distance_detection;
    private Vec2 position = new Vec2();  // la position réelle du robot, pas la version qu'ont les robots
    
    private double orientation; // l'orientation réelle du robot, pas la version qu'ont les robots
    private LocomotionCardWrapper deplacements;
    private boolean symetrie;
    private int sleep_boucle_acquittement = 10;
    private int distance_degagement_robot = 50;
    private double angle_degagement_robot;
    
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
     * @param angle
     * @param hooks
     * @throws UnableToMoveException
     * @throws FinMatchException
     * @throws ScriptHookException
     */
    public void turn(double angle, ArrayList<Hook> hooks) throws UnableToMoveException
    {
    	Vec2 consigne = new Vec2(
        (int) (position.x + 1000*Math.cos(angle)),
        (int) (position.y + 1000*Math.sin(angle))
        );

		vaAuPointGestionExceptions(consigne, position, hooks, true, false, true);
    }
    
    /**
     * Fait avancer le robot de "distance" (en mm).
     * @param distance
     * @param hooks
     * @param insiste
     * @throws UnableToMoveException
     * @throws FinMatchException 
     * @throws ScriptHookException 
     */
    public void moveLengthwise(int distance, ArrayList<Hook> hooks, boolean mur) throws UnableToMoveException
    {
        log.debug("Avancer de "+Integer.toString(distance), this);
        
        Vec2 consigne = new Vec2(), consigneNonInversee = new Vec2(); 
        consigneNonInversee.x = (int) (position.x + distance*Math.cos(orientation));
        consigneNonInversee.y = (int) (position.y + distance*Math.sin(orientation));        

        // En fait, ici on prend en compte que la symétrie va inverser la consigne...
        if(symetrie)
        {
        	consigne.x = -consigneNonInversee.x;
            consigne.y = consigneNonInversee.y;
        }
        else
        {
        	consigne.x = consigneNonInversee.x;
            consigne.y = consigneNonInversee.y;
        }

        // l'appel à cette méthode sous-entend que le robot ne tourne pas
        // il va donc en avant si la distance est positive, en arrière si elle est négative
        // si on est à 90°, on privilégie la marche avant
		vaAuPointGestionExceptions(consigne, position, hooks, distance >= 0, mur, false);
    }
        
    /**
     * Suit un chemin. Crée les hooks de trajectoire courbe si besoin est.
     * @param chemin
     * @param hooks
     * @param insiste
     * @throws UnableToMoveException
     * @throws FinMatchException 
     * @throws ScriptHookException 
     */
    public void followPath(ArrayList<Vec2> chemin, ArrayList<Hook> hooks, DirectionStrategy directionstrategy) throws UnableToMoveException
    {
    	if(hooks == null)
    		hooks = new ArrayList<Hook>();
    	int size = chemin.size();
    	for(int i = 0; i < size; i++)
        {
            Vec2 consigne = chemin.get(i);
			vaAuPointGestionMarcheArriere(consigne, position, hooks, false, directionstrategy, false);
        }
    }

    /**
     * Bloquant. Gère la marche arrière automatique selon la stratégie demandée.
     * @param hooks
     * @param insiste
     * @throws UnableToMoveException
     * @throws FinMatchException 
     * @throws ScriptHookException 
     * @throws ChangeDirectionException 
     */
    private void vaAuPointGestionMarcheArriere(Vec2 consigne, Vec2 intermediaire, ArrayList<Hook> hooks, boolean mur, DirectionStrategy strategy, boolean seulementAngle) throws UnableToMoveException
    {
    	// Si on est en trajectoire courbe, on continue comme la fois précédente
    	if(strategy == DirectionStrategy.FORCE_BACK_MOTION)
    	{
    		directionPrecedente = false;
            vaAuPointGestionExceptions(consigne, intermediaire, hooks, false, mur, seulementAngle);
    	}
    	else if(strategy == DirectionStrategy.FORCE_FORWARD_MOTION)
    	{
    		directionPrecedente = true;
            vaAuPointGestionExceptions(consigne, intermediaire, hooks, true, mur, seulementAngle);
    	}
    	else //if(strategy == DirectionStrategy.FASTEST)
    	{
    		// Calcul du moyen le plus rapide
	        Vec2 delta = consigne.clone();
	        if(symetrie)
	            delta.x *= -1;
	        delta.minus(position);
	        // Le coeff 1000 vient du fait que Vec2 est constitué d'entiers
	        Vec2 orientationVec = new Vec2((int)(1000*Math.cos(orientation)), (int)(1000*Math.sin(orientation)));
	
	        directionPrecedente = delta.dot(orientationVec) > 0;
	        // On regarde le produit scalaire; si c'est positif, alors on est dans le bon sens, et inversement
	        vaAuPointGestionExceptions(consigne, intermediaire, hooks, directionPrecedente, mur, seulementAngle);
    	}
    }
    
    /**
     * Gère les exceptions, c'est-à-dire les rencontres avec l'ennemi et les câlins avec un mur.
     * @param hooks
     * @param trajectoire_courbe
     * @param marche_arriere
     * @param insiste
     * @throws UnableToMoveException 
     * @throws FinMatchException 
     * @throws ScriptHookException 
     * @throws ChangeDirectionException 
     */
    private void vaAuPointGestionExceptions(Vec2 consigne, Vec2 intermediaire, ArrayList<Hook> hooks, boolean marcheAvant, boolean mur, boolean seulementAngle) throws UnableToMoveException
    {
        int attente_ennemi_max = 600; // combien de temps attendre que l'ennemi parte avant d'abandonner
        int nb_iterations_deblocage = 2; // combien de fois on réessayer si on se prend un mur
        boolean recommence;
        do {
            recommence = false;
            try
            {
                vaAuPointGestionHookCorrectionEtDetection(consigne, intermediaire, hooks, marcheAvant, seulementAngle);
            } catch (BlockedException e)
            {
                nb_iterations_deblocage--;
                immobilise();
                /*
                 * En cas de blocage, on recule (si on allait tout droit) ou on avance.
                 */
                // Si on s'attendait à un mur, c'est juste normal de se le prendre.
                if(!mur)
                {
                    try
                    {
                        log.warning("On n'arrive plus à avancer. On se dégage", this);
                        if(seulementAngle)
                        {
                        	// TODO: les appels à déplacements sont non bloquants, il faut rajouter des sleeps
                        	// on alterne rotation à gauche et à droite
                        	if((nb_iterations_deblocage & 1) == 0)
                        		deplacements.turn(orientation+angle_degagement_robot);
                        	else
                        		deplacements.turn(orientation-angle_degagement_robot);
                        }
                        else if(marcheAvant)
                            deplacements.moveLengthwise(distance_degagement_robot);
                        else
                            deplacements.moveLengthwise(-distance_degagement_robot);
                        while(!isMotionEnded());
                    	recommence = true; // si on est arrivé ici c'est qu'aucune exception n'a été levée
                    } catch (SerialConnexionException e1)
                    {
                        e1.printStackTrace();
                    } catch (BlockedException e1) {
                    	immobilise();
                        log.critical("On n'arrive pas à se dégager.", this);
					}
                    if(!recommence)
                        throw new UnableToMoveException();
                }
            } catch (UnexpectedObstacleOnPathException e)
            {
            	immobilise();
            	long dateAvant = System.currentTimeMillis();
                log.critical("Détection d'un ennemi! Abandon du mouvement.", this);
            	while(System.currentTimeMillis() - dateAvant < attente_ennemi_max)
            	{
            		try {
            			detectEnemy(marcheAvant);
            			recommence = true; // si aucune détection
            			break;
            		}
            		catch(UnexpectedObstacleOnPathException e2)
            		{}
            	}
                if(!recommence)
                    throw new UnableToMoveException();
			}

        } while(recommence); // on recommence tant qu'il le faut

    // Tout s'est bien passé
    }
    
    /**
     * Bloquant. Gère les hooks, la correction de trajectoire et la détection.
     * @param point
     * @param hooks
     * @param trajectoire_courbe
     * @throws BlockedException 
     * @throws UnexpectedObstacleOnPathException 
     * @throws FinMatchException 
     * @throws ScriptHookException 
     * @throws WallCollisionDetectedException 
     * @throws ChangeDirectionException 
     */
    private void vaAuPointGestionHookCorrectionEtDetection(Vec2 consigne, Vec2 intermediaire, ArrayList<Hook> hooks, boolean marcheAvant, boolean seulementAngle) throws UnexpectedObstacleOnPathException, BlockedException
    {
        // le fait de faire de nombreux appels permet de corriger la trajectoire
        vaAuPointGestionSymetrie(consigne, intermediaire, marcheAvant, seulementAngle, false);
        do
        {
            updateCurrentPositionAndOrientation();
            
            // en cas de détection d'ennemi, une exception est levée
            detectEnemy(marcheAvant);

            for(Hook hook : hooks)
                hook.evaluate();

            corrigeAngle(consigne, marcheAvant);


//            Sleep.sleep(sleep_boucle_acquittement);
        } while(!isMotionEnded());
        
    }

    private void corrigeAngle(Vec2 consigne, boolean marcheAvant) throws BlockedException
    {
    	vaAuPointGestionSymetrie(consigne, position, marcheAvant, true, true);
    }

    /**
     * Non bloquant. Gère la symétrie et la marche arrière.
     * @param point
     * @param sans_lever_exception
     * @param trajectoire_courbe
     * @param marche_arriere
     * @throws BlockedException 
     * @throws FinMatchException 
     * @throws WallCollisionDetectedException 
     */
    private void vaAuPointGestionSymetrie(Vec2 consigne, Vec2 intermediaire, boolean marcheAvant, boolean seulementAngle, boolean correction) throws BlockedException
    {
        Vec2 delta = consigne.clone();
        if(symetrie)
        {
            delta.x = -delta.x;
            intermediaire.x = -intermediaire.x;
        }
        
        updateCurrentPositionAndOrientation();

        delta.minus(intermediaire);
//        log.debug("Distance directe: "+delta.length()+", differenceDistance: "+differenceDistance, this);
        double distance = delta.length();
        
        double angle =  Math.atan2(delta.y, delta.x);
        // on suit ce que demande le boolean marcheAvant, en se retournant si besoin
        if(marcheAvant && distance < 0 || (!marcheAvant && distance > 0))
        {
            distance *= -1;
            angle += Math.PI;
        }
        
        vaAuPointGestionCourbe(consigne, intermediaire, angle, distance, false, seulementAngle, correction);
    }
    
    /**
     * Non bloquant. Avance, de manière courbe ou non.
     * @param angle
     * @param distance
     * @param trajectoire_courbe
     * @throws BlockedException 
     * @throws FinMatchException 
     * @throws WallCollisionDetectedException 
     */
    private void vaAuPointGestionCourbe(Vec2 consigne, Vec2 intermediaire, double angle, double distance, boolean trajectoire_courbe, boolean seulementAngle, boolean correction) throws BlockedException
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
		if(correction)
		{
			// 5 cm
			double deltaAngle = Math.abs((orientation-angle) % (2*Math.PI));
			if(deltaAngle > Math.PI)
				deltaAngle -= 2*Math.PI;
			else if(deltaAngle < -Math.PI)
				deltaAngle += 2*Math.PI;
			if(intermediaire.squaredDistance(consigne) > 2500 && Math.abs(deltaAngle) < Math.PI/2)
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
                    Sleep.sleep(sleep_boucle_acquittement);
            
/*            // TODO: passer en hook
            ObstacleRectangular obstacle = new ObstacleRectangular(position, consigne);
        	if(obstacle.isCollidingObstacleFixe())
        	{
        		log.debug("Le robot a demandé à avancer dans un obstacle. Ordre annulé.", this);
        		throw new WallCollisionDetectedException();
        	}
*/
            if(!seulementAngle)
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
        
        int rayon_detection = largeur_robot/2 + distance_detection;
        Vec2 centre_detection = new Vec2((int)(signe * rayon_detection * Math.cos(orientation)), (int)(signe * rayon_detection * Math.sin(orientation)));
        centre_detection.plus(position);
        if(table.getObstacleManager().isDiscObstructed(centre_detection, distance_detection))
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
            float[] infos = deplacements.getCurrentPositionAndOrientation();
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
    	distance_detection = Integer.parseInt(config.getProperty("distance_detection"));
        distance_degagement_robot = Integer.parseInt(config.getProperty("distance_degagement_robot"));
        sleep_boucle_acquittement = Integer.parseInt(config.getProperty("sleep_boucle_acquittement"));
        angle_degagement_robot = Double.parseDouble(config.getProperty("angle_degagement_robot"));
		symetrie = config.getProperty("couleur").replaceAll(" ","").equals("jaune");
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
        if(symetrie)
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
        if(symetrie)
        	this.orientation = Math.PI-this.orientation;
        try {
    		deplacements.setOrientation(this.orientation);
        } catch (SerialConnexionException e) {
            e.printStackTrace();
        }
    }

    public Vec2 getPosition()
    {
        updateCurrentPositionAndOrientation();
        Vec2 out = position.clone();
        if(symetrie)
        	out.x = -out.x;
        return out;
    }

    public double getOrientation()
    {
        updateCurrentPositionAndOrientation();
        if(symetrie)
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
