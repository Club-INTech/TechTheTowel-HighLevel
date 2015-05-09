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
import exceptions.ConfigPropertyNotFoundException;
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
     * Position "bas niveau" du robot, celle du robot
     * La vraie.
     */
    private Vec2 lowLevelPosition = new Vec2();
    
    /**
     * Position "haut niveau" du robot, celle du robot
     * Celle qui commence toujours en vert
     */
    private Vec2 highLevelPosition = new Vec2();
    
    /**
     * la position visee au final par le deplacement
     */
    private Vec2 finalAim = new Vec2();
    
    /**
     * orientation réelle du robot (symetrisee)
     * non connue par les classes de plus haut niveau
     */
    private double lowLevelOrientation;
    
    /**
     * orientation réelle du robot non symetrisée
     */
    private double highLevelOrientation;
    
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
    private int maxLengthCorrectionThreeshold = 30;
    
    /**
     * 	L'orientation maximale pour une correction rotationelle 
     * 	La correction ne sera effectuée que si le robot est assez eloigné de son orientation souhaitée.
     */
    private double maxRotationCorrectionThreeshold = 0.05;

	
	/**Booleen explicitant si le robot est pret à tourner, utile pour le cercle de detection */
	public boolean isRobotTurning=false;	
	
	/** nombre d'essais maximum après une BlockedException*/
    private int maxRetriesIfBlocked=2;
    private int actualRetriesIfBlocked=0;
    
    /** Utile pour l'activation dees capteurs*/
    public boolean isRobotMovingForward;
    public boolean isRobotMovingBackward;


    
    
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
    	turn(angle, hooks, true);
    } 
    	    	
    /**
     * Fait tourner le robot (méthode bloquante)
     * Une manière de tourner qui réutilise le reste du code, car tourner
     * n'en devient plus qu'un cas particulier (celui où... on n'avance pas)
     * @param angle l'angle vise (en absolut)
     * @param hooks les potentiels hooks a prendre en compte (ne pas mettre null !)
     * @param mustDetect true si on veut detecter, false sinon.
     * @throws UnableToMoveException si le robot a un bloquage mecanique
     */
    public void turn(double angle, ArrayList<Hook> hooks, boolean mustDetect) throws UnableToMoveException
    {
        log.debug("Tourner de "+Double.toString(angle), this);

    	actualRetriesIfBlocked=0;
		updateCurrentPositionAndOrientation();

    	/**
    	 * calcul de la position visee du haut niveau
    	 *   on vise une position eloignee mais on ne s'y deplacera pas, le robot ne fera que tourner
    	 */
    	Vec2 aim = new Vec2(
        (int) (highLevelPosition.x + 1000*Math.cos(angle)),
        (int) (highLevelPosition.y + 1000*Math.sin(angle))
        );
    	finalAim = aim;

        isRobotMovingForward=true;
		moveToPointException(aim, hooks, true, false, true, mustDetect);
        isRobotMovingBackward=false;

    	actualRetriesIfBlocked=0;
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
    	moveLengthwise(distance, hooks, wall, true);
    }
    
    /**
     * Fait avancer le robot de "distance" (en mm).
     * @param distance la distance dont le robot doit se deplacer
     * @param hooks les potetniels hooks a prendre en compte (ne pas mettre null !)
     * @param wall vrai si on supppose qu'on vas se cogner dans un mur (et qu'il ne faut pas pousser dessus)
     * @param mustDetect true si on veut detecter, false sinon.
     * @param speed la vitesse que doit prendre le robot pedant le deplacement (a donner imperativement si on utilise un hook)
     * @throws UnableToMoveException si le robot a un bloquage mecanique
     */
    public void moveLengthwise(int distance, ArrayList<Hook> hooks, boolean wall, boolean mustDetect) throws UnableToMoveException
    {    

    	actualRetriesIfBlocked=0;
    	
		updateCurrentPositionAndOrientation();

        log.debug("Avancer de "+Integer.toString(distance), this);
        
        /**
         * aim est la visée du haut niveau, qui commence toujours à droite
         * TODO; trouver ce que veut dire ce commentaire
         */
        Vec2 aim = new Vec2(); 
        
        aim.x = (int) (highLevelPosition.x + distance*Math.cos(highLevelOrientation));
        aim.y = (int) (highLevelPosition.y + distance*Math.sin(highLevelOrientation));      
        finalAim = aim;
        // l'appel à cette méthode sous-entend que le robot ne tourne pas
        // il va donc en avant si la distance est positive, en arrière si elle est négative
        // si on est à 90°, on privilégie la marche avant
        
        // Ôur les capteurs
        if(distance>=0)
        	isRobotMovingForward=true;
        else 
        	isRobotMovingBackward=true;
		moveToPointException(aim, hooks, distance >= 0, wall, false, mustDetect);
		
		isRobotMovingForward=false;
    	isRobotMovingBackward=false;

		actualRetriesIfBlocked=0;// on reinitialise
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
        followPath(path, hooks, directionstrategy, true);// par defaut, on detecte
    }
    
    /**
     * Suit un chemin en ligne brisee
     * @param path le chemin a suivre (un arraylist de Vec2 qui sont les point de rotation du robot)
     * @param hooks les potentiels hooks a prendre en compte (ne pas mettre null !)
     * @param directionstrategy ce que la strategie choisit comme optimal (en avant, en arriere, au plus rapide)
     * @param mustDetect true si on veut detecter, false sinon.
     * @throws UnableToMoveException si le robot a un bloquage mecanique
     */
    public void followPath(ArrayList<Vec2> path, ArrayList<Hook> hooks, DirectionStrategy directionstrategy, boolean mustDetect) throws UnableToMoveException
    {
		updateCurrentPositionAndOrientation();

    	//si un singe a mie de pain null pour les hooks on le gere
    	if(hooks == null)
    		hooks = new ArrayList<Hook>();
    	
    	//un simple for (on vas au point 0 puis au point 1 etc.)
    	finalAim = path.get(path.size()-1);
  
    	path.remove(0);//On enleve le premier point, notre propre position
    	
    	for(int i = 0; i < path.size(); i++)
        {
            Vec2 aim = path.get(i);
			moveToPointForwardBackward(aim, hooks,
													/*on suppose qu'on ne se prends pas de mur (sinon la pathDingDing est a revoir)*/
													false, directionstrategy,
																			/*on veut avancer*/
																			false, mustDetect);
        }		
    }


    
    /**
     * Bloquant. Gère la marche arrière automatique selon la stratégie demandée.
     * @param aim le point visé sur la table (consigne donné par plus haut niveau donc non symetrise)
     * @param hooks les potentiels hooks a prendre en compte (ne pas mettre null !)
     * @param mur vrai si on suppose qu'on vas se cogner dans un mur (et qu'on veut s'arreter des qu'on cogne)
     * @param strategy ce que la strategie choisit comme optimal (en avant, en arriere, au plus rapide)
     * @param turnOnly vrai si on veut uniquement tourner (et pas avancer)
     * @param mustDetect true si on veut detecter, false sinon.
     * @throws UnableToMoveException si le robot a un bloquage mecanique
     */
    private void moveToPointForwardBackward(Vec2 aim, ArrayList<Hook> hooks, boolean mur, DirectionStrategy strategy, boolean turnOnly, boolean mustDetect) throws UnableToMoveException
    {
		actualRetriesIfBlocked=0;// on reinitialise

		updateCurrentPositionAndOrientation();

    	// on avance en fonction de ce que nous dit la strategie
    	if(strategy == DirectionStrategy.FORCE_BACK_MOTION)
    	{
            isRobotMovingForward=true;
            moveToPointException(aim, hooks, false, mur, turnOnly, mustDetect);
            isRobotMovingForward=false;
    	}
    	else if(strategy == DirectionStrategy.FORCE_FORWARD_MOTION)
    	{ 
            isRobotMovingBackward=true;
            moveToPointException(aim, hooks, true, mur, turnOnly, mustDetect);
            isRobotMovingForward=false;
    	}
    	else //if(strategy == DirectionStrategy.FASTEST)
    	{
    		// Calcul du moyen le plus rapide (on se sert d'un calcul de produit scalaire)
	        Vec2 delta = aim.clone();
	        delta.minus(lowLevelPosition);
	        // Le coeff 1000 vient du fait que Vec2 est constitué d'entiers
	        Vec2 orientationVec = new Vec2((int)(1000*Math.cos(lowLevelOrientation)), (int)(1000*Math.sin(lowLevelOrientation)));
	     
	        // On regarde le produit scalaire; si c'est positif, alors on est dans le bon sens, et inversement
	        boolean isFastestDirectionForward = delta.dot(orientationVec) >= 0;
	        
	        isRobotMovingForward = isFastestDirectionForward;
	        isRobotMovingBackward = !isFastestDirectionForward;
	        moveToPointException(aim, hooks, isFastestDirectionForward, mur, turnOnly, mustDetect);
	        isRobotMovingForward = false;
	        isRobotMovingBackward = false;
    	}
    	
    	log.debug("Arrivés en "+aim+" vraie position : "+lowLevelPosition, this);
    	
		actualRetriesIfBlocked=0;// on reinitialise

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
     * @param mustDetect true si on veut detecter, false sinon.
     * @throws UnableToMoveException si le robot a un bloquage mecanique
     */
    private void moveToPointException(Vec2 aim, ArrayList<Hook> hooks, boolean isMovementForward, boolean headingToWall, boolean turnOnly, boolean mustDetect) throws UnableToMoveException
    {
        //int maxTimeToWaitForEnemyToLeave = 600; // combien de temps attendre que l'ennemi parte avant d'abandonner
        int unexpectedWallImpactCounter = 2; // combien de fois on réessayer si on se prend un mur (si wall est a true alors les impacts sont attendus donc on s'en fout)
        boolean doItAgain;
        do 
        {
        	//si on a pas d'erreur on ne recommence pas
            doItAgain = false;
            try
            {
                moveToPointCorrectAngleAndDetectEnnemy(aim, hooks, isMovementForward, turnOnly, mustDetect);
            }
            catch (BlockedException e)
            {
    			log.critical( e.logStack(), this);
                log.critical("Haut : Catch de "+e+" dans moveToPointException", this);
                
                if (!headingToWall) // si on s'y attendaiq, on ne fais rien.
                {
	                if(maxRetriesIfBlocked!=0)
	                {
		                if(maxRetriesIfBlocked > actualRetriesIfBlocked)
		                {
		                	actualRetriesIfBlocked++;
		                    log.critical("Tentative "+actualRetriesIfBlocked+" de deplacement ", this);
		                    if(isMovementForward)
		                    	isRobotMovingForward=true;
		                    else 
		                    	isRobotMovingBackward=true;
		                	moveToPointException(aim, hooks, isMovementForward, headingToWall, turnOnly, mustDetect); // on rentente s'il a y eu un probleme
		                	isRobotMovingForward=false;
		                	isRobotMovingBackward=false;
		                }
		                else 
		                {
	                        log.warning("On arrive pas à se degager, nombre max d'essais depassé, lancement de UnableToMOveEXception", this);
	                        throw new UnableToMoveException(aim, UnableToMoveReason.PHYSICALLY_BLOCKED);
		                }

	                }
	                else
	                {
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
		                        	isRobotTurning=true;
		                        	
		                        	// TODO: les appels à déplacements sont non bloquants, il faut rajouter des sleeps
		                        	// on alterne rotation à gauche et à droite
		                        	if((unexpectedWallImpactCounter & 1) == 0)
		                        		deplacements.turn(lowLevelOrientation+angleToDisengage);
		                        	else
		                        		deplacements.turn(lowLevelOrientation-angleToDisengage);                        	
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
		            			log.critical( e1.logStack(), this);
		                        log.critical("On ne fait rien après ceci: Catch de "+e1+" dans moveToPointException", this);
		                    } 
		                    catch (BlockedException e1)
		                    {
		            			log.critical( e1.logStack(), this);
		                        log.critical("Catch de "+e1+" dans moveToPointException", this);
		                    	immobilise();                       
		                        log.critical("On n'arrive pas à se dégager", this);
		                        

			                    if(!doItAgain)
			                    {
			                        log.critical("Lancement de UnableToMoveException dans MoveToPointException, visant "+finalAim.x+" :: "+finalAim.y+" cause physique", this);
			                        throw new UnableToMoveException(finalAim, UnableToMoveReason.PHYSICALLY_BLOCKED);
			                    }
							}
		                }
		                else 
		                {
	                        log.warning("On s'attendait à ce mur.", this);
						}
	                }
                }
            }
            
            catch (UnexpectedObstacleOnPathException unexpectedObstacle)
            {
                log.critical("Ennemi detecté : Catch de "+unexpectedObstacle, this); 
    			log.critical( unexpectedObstacle.logStack(), this);
            	immobilise();
            	
                //long detectionTime = System.currentTimeMillis();
                /*
            	while(System.currentTimeMillis() - detectionTime < maxTimeToWaitForEnemyToLeave)
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
                        throw new UnableToMoveException(finalAim, UnableToMoveReason.OBSTACLE_DETECTED);
            		}
            	}*/

                if(!doItAgain)
                {
                    log.critical("UnableToMoveException dans MoveToPointException, visant "+finalAim.x+" :: "+finalAim.y+" cause : detection d'obstacle", this);
                    throw new UnableToMoveException(finalAim, UnableToMoveReason.OBSTACLE_DETECTED);
                }
			}
            catch(SerialConnexionException e)
            {
    			log.critical( e.logStack(), this);

            	// FIXME : gérer cette exception
            }

        } 
        while(doItAgain)
        	;     
        // on recommence tant qu'il le faut


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
     * @param mustDetect true si on veut detecter, false sinon.
     * @throws BlockedException si le robot a un bloquage mecanique
     * @throws UnexpectedObstacleOnPathException si le robot rencontre un obstacle innatendu sur son chemin (par les capteurs)
     */
    private void moveToPointCorrectAngleAndDetectEnnemy(Vec2 aim, ArrayList<Hook> hooks, boolean isMovementForward, boolean turnOnly, boolean mustDetect) throws UnexpectedObstacleOnPathException, BlockedException, SerialConnexionException
    {         	
    	//double time=System.currentTimeMillis();
        moveToPointSymmetry(aim, isMovementForward, mustDetect, turnOnly, false);
        do 
        { 	
            updateCurrentPositionAndOrientation();
            
        	// en cas de détection d'ennemi, une exception est levée
        	if(mustDetect)
        	{
        		//detectEnemyInFrontDisk(isMovementForward, turnOnly, aim);
        		detectEnemyAtDistance(70, aim);	// 85 mm est une bonne distance pour être safe.
        		
        		//si un ennemi est détecté à moins de 200, on diminue au minimum la vitesse
            	/*
        		try
            	{
            		if(mustDetect)
            			detectEnemyAtDistance(150, aim);
            	}
            	catch(UnexpectedObstacleOnPathException e)
            	{
            		//setTranslationnalSpeed(3);
            		
            		//debug
            		log.debug("diminution de la vitesse suite à la détection d'un ennemi proche", this);
            	}
            	*/
        	}
        	else 
        		log.debug("Pas de detection demandée", this); 

        	

            //on evalue les hooks (non null !)
            if(hooks != null)
	            for(Hook hook : hooks)
	                hook.evaluate();
                        
            // le fait de faire de nombreux appels permet de corriger la trajectoire
            correctAngle(aim, isMovementForward, mustDetect);
            
            //log.critical("Temps pour finir la boucle d'asservissement "+(System.currentTimeMillis()-time), this);
            //time=System.currentTimeMillis();
            
            // On sleep pour eviter le spam de la serie
            //Sleep.sleep(feedbackLoopDelay);

        } 
        while(!isMotionEnded())
        	;
        
    }


    /**
     * donne une consigne d'un nouvel angle a atteindre (pour corriger la trajectoire en cours de mouvement)
     * @param aim la point vise (non symetrisee)
     * @param isMovementForward vrai si on vas en avant et faux si on vas en arriere
     * @throws BlockedException si le robot a un bloquage mecanique
     * @throws UnexpectedObstacleOnPathException 
     */
    private void correctAngle(Vec2 aim, boolean isMovementForward, boolean mustDetect) throws BlockedException, UnexpectedObstacleOnPathException
    {
    	//envoi de la consigne avec turnOnly a false et a isCorrection a true (c'est bien une correction)
    	//la correction est toujours un turnOnly, on evite les doublons d'où le turnOnly à false.
    	moveToPointSymmetry(aim, isMovementForward, mustDetect, false, true);
    }

    /**
     * Non bloquant. 
     * Gère la symétrie et la marche arrière. (si on est en marche arriere le aim doit etre modifié pour que la consigne vers le bas niveau soit bonne)
     * @param aim la position visee sur la tab le (consigne donné par plus haut niveau donc non symetrise)
     * @param isMovementForward vrai si on vas en avant et faux si on vas en arriere
     * @param mustDetect si on autorise la detection pendant ce deplacement
     * @param turnOnly vrai si on veut uniquement tourner (et pas avancer)
     * @param isCorrection vrai si la consigne est une correction et pas un ordre de deplacement
     * @param isTurnRelative vrai si l'angle est relatif et pas absolut
     * @throws BlockedException si le robot rencontre un obstacle innatendu sur son chemin (par les capteurs)
     * @throws UnexpectedObstacleOnPathException 
     */
    private void moveToPointSymmetry(Vec2 aim, boolean isMovementForward, boolean mustDetect, boolean turnOnly,boolean isCorrection) throws BlockedException, UnexpectedObstacleOnPathException
    {
        updateCurrentPositionAndOrientation();
        
        // position donnée par le bas niveau avec un traitement dans UpdateCurrentPositionAndOrientation
        Vec2 givenPosition = highLevelPosition.clone();
        
        // Le point qu'on vise, donné par le haut niveau donc comme si on etais vert
        Vec2 aimSymmetrized = aim.clone();   
        
        if(symetry) // miroir des positions
        {
        	givenPosition.x  = -givenPosition.x;
        	aimSymmetrized.x = -aimSymmetrized.x;
        }
        Vec2 delta = aimSymmetrized.clone();
        
        delta.minus(givenPosition);
 
        
        //calcul de la nouvelle distance et du nouvel angle
        double distance = delta.length();
        double angle;
        /*
        if(symetry)
        	  angle = Math.atan2(-delta.y, delta.x);//Angle en absolu 
        else */
    	angle = Math.atan2(delta.y, delta.x);//Angle en absolu

        
        // si on a besoin de se retourner pour suivre la consigne de isMovementForward on le fait ici
        if(isMovementForward && distance < 0 || (!isMovementForward && distance > 0))
        {
            distance *= -1;
            angle += Math.PI;
        }
                
        // on annule la correction si on est trop proche de la destination
        if(isCorrection) 
        {
           Vec2 vectorTranslation = aimSymmetrized;
           vectorTranslation.minus( givenPosition );
           if( (  vectorTranslation.length() >  maxLengthCorrectionThreeshold )) 
	        	moveToPointSerialOrder(aimSymmetrized, givenPosition, angle, distance, mustDetect, turnOnly, isCorrection);
	        else 
	        	return;// Si on est trop proche, on ne fais rien.
        }
        else 
        	moveToPointSerialOrder(aimSymmetrized, givenPosition, angle, distance, mustDetect, turnOnly, isCorrection);
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
     * @throws UnexpectedObstacleOnPathException 
     */
    private void moveToPointSerialOrder(Vec2 symmetrisedAim, Vec2 givenPosition, double angle, double distance, boolean mustDetect,boolean turnOnly, boolean isCorrection) throws BlockedException, UnexpectedObstacleOnPathException
    {
    	boolean trajectoire_courbe = false;

    	// Ce code fait juste un modulo 2*pi, avec un résultat entre -PI et +PI
		double delta = (angle-lowLevelOrientation) % (2*Math.PI);
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
			//Si l'angle petit, alors on fait la correction en angle
			if((Math.abs(delta) < Math.PI/8))
			{
				//on active la correction (on attendra pas d'avoir fini de tourner (le robot) pour reprendre le programme)
				trajectoire_courbe = true;
			}
			else
			{
				log.debug("correction en abandon; delta :"+delta, this);
				return;
			}
		}

        try
        {
        	if(isCorrection && Math.abs(delta) > maxRotationCorrectionThreeshold)
        	{
        		isRobotTurning=true;// prochain ordre : on tourne
                
        		deplacements.turn(angle);  // On ne tourne que si on est assez loin de l'orientation voulu
              
//        		log.debug("Angle corrigé", this);
        	}
        	else if(!isCorrection)// Si ca n'est pas  une correction
        	{
        		if(Math.abs(delta)>maxRotationCorrectionThreeshold)
        		{// on ne tourne vraiment que si l'angle souhaité est vraiment different.
	        		isRobotTurning=true;// prochain ordre : on tourne
        		}
                
        		deplacements.turn(angle);
        	}
        	
            // sans virage : la première rotation est bloquante
            if(!trajectoire_courbe) 
            	// on attend la fin du mouvement
                while(!isMotionEnded()) 
                {
                	if(mustDetect)
                		detectEnemyInDisk(true, true, highLevelPosition);
                	//TODO : ce sleep ne pose-t-il pas de problèmes?
                    Sleep.sleep(feedbackLoopDelay);
                }
            
            isRobotTurning=false; // fin du turn
            
            if(!(turnOnly || isCorrection))
            	deplacements.moveLengthwise(distance);
        } 
        catch (SerialConnexionException e)
        {
            log.critical("Catch de "+e+" dans moveToPointSerialOrder", this);
			log.critical( e.logStack(), this);
            isRobotTurning=false; // Meme avec un catch, on a fini de tourner
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
        		if(infos[1])//si le robot patine, il est bloqué
        		{
                    log.critical("Robot bloqué, lancement de BlockedException dans isMotionEnded", this);
                    throw new BlockedException();
        		}
        		else
        		{
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
			log.critical( e.logStack(), this);
            return false;
        }
    }
    
    /**
     * fonction vérifiant que l'on ne va pas taper dans le robot adverse.
     * test si le cercle devant (ou derriere en fonction du mouvement) est vide d'obstacle
     * @param front vrai si on veut detecter a l'avant du robot (donc si on avance en marche avant)
     * @param isTurnOnly On detecte differement si on tourne ou translate
     * @param aim 
     * @throws UnexpectedObstacleOnPathException si obstacle sur le chemin
     */
    public void detectEnemyInDisk(boolean front, boolean isTurnOnly, Vec2 aim) throws UnexpectedObstacleOnPathException
    {
        int signe = -1;
        if(front)
            signe = 1;
        
        //rayon du cercle de detection
        int detectionRadius = robotLength/2 + detectionDistance;
        
        //centre du cercle de detection
        Vec2 detectionCenter = new Vec2((int)(signe * detectionRadius * Math.cos(highLevelOrientation)), 
        								(int)(signe * detectionRadius * Math.sin(highLevelOrientation))); //centre par rapport au centre de position du robot
        	
        detectionCenter.plus(highLevelPosition);

        // si on ne tourne pas, on regarde devant nous : sinon, on regarde autour de nous
        if(isTurnOnly || isRobotTurning)
        	detectionCenter=highLevelPosition;
        
        if(table.getObstacleManager().isDiscObstructed(detectionCenter, detectionDistance))
        {
            log.warning("Lancement de UnexpectedObstacleOnPathException dans detectEnemyInDisk", this);
            throw new UnexpectedObstacleOnPathException();
        }
    }
    

    /**
     * Lance une exception si un ennemi se trouve a une distance inférieure a celle spécifiée
     * @param distance distance jusqu'a un ennemi en mm en dessous de laquelle on doit abandonner le mouvement
     * @param movementDirection direction de mouvment du robot
     * @param isMovementForward vrai si on va en avant et faux si on va en arriere
     * @throws UnexpectedObstacleOnPathException si obstacle sur le chemin
     */
    public void detectEnemyAtDistance(int distance, Vec2 movementDirection) throws UnexpectedObstacleOnPathException
    {
    	
        if(table.getObstacleManager().distanceToClosestEnemy(highLevelPosition, movementDirection) <= distance)
        {
        	log.debug("DetectEnemyAtDistance voit un ennemi trop proche pour continuer le déplacement (distance de " 
        			 + table.getObstacleManager().distanceToClosestEnemy(highLevelPosition, movementDirection) +" mm)", this);
        	immobilise();
        	throw new UnexpectedObstacleOnPathException();
        }
    }

    /**
     * Met à jour position et orientation via la carte d'asservissement.
     * Donne la veritable positions du robot sur la table
     * @throws FinMatchException 
     * @throws SerialConnexionException
     */
    
    private void updateCurrentPositionAndOrientation()
    {
        try 
        {
            float[] infos = deplacements.getCurrentPositionAndOrientation();
            
            lowLevelPosition.x = (int)infos[0];            
            lowLevelPosition.y = (int)infos[1];
            lowLevelOrientation = infos[2]; // car getCurrentPositionAndOrientation renvoie des radians
            
            highLevelPosition=lowLevelPosition.clone();
            highLevelOrientation=lowLevelOrientation;
            
            if(symetry)
            {
            	highLevelPosition.x = -highLevelPosition.x;
            	highLevelOrientation=Math.PI-highLevelOrientation;
            }
            
        }
        catch(SerialConnexionException e)
        {
        	log.critical("Catch de "+e+" dans updateCurrentPositionAndOrientation", this);
			log.critical( e.logStack(), this);
        }
    }

    @Override
    public void updateConfig()
    {
    	try 
    	{
			
	    	detectionDistance = Integer.parseInt(config.getProperty("distance_detection"));
	        distanceToDisengage = Integer.parseInt(config.getProperty("distance_degagement_robot"));
	        feedbackLoopDelay = Integer.parseInt(config.getProperty("sleep_boucle_acquittement"));
	        angleToDisengage = Double.parseDouble(config.getProperty("angle_degagement_robot"));
			symetry = config.getProperty("couleur").replaceAll(" ","").equals("jaune");
			robotLength = Integer.parseInt(config.getProperty("longueur_robot").replaceAll(" ",""));
    	}
    	catch (ConfigPropertyNotFoundException e)
    	{
    		log.debug("Revoir le code : impossible de trouver la propriété "+e.getPropertyNotFound(), this);
			log.critical( e.logStack(), this);
    	}
    }

    /**
     * Arrête le robot.
     * @throws FinMatchException 
     */
    public void immobilise()
    {
        log.warning("Arrêt du robot en "+lowLevelPosition, this);
        try 
        {
            deplacements.immobilise();
        } 
        catch (SerialConnexionException e) 
        {
            log.critical("Catch de "+e+" dans immobilise", this);
			log.critical( e.logStack(), this);
        }           
    }

    /**
     * Met à jour la position. A ne faire qu'en début de match.
     * @param positionWanted
     * @throws SerialConnexionException 
     * @throws FinMatchException 
     */
    public void setPosition(Vec2 positionWanted)
    {
        this.lowLevelPosition = positionWanted.clone();
        if(symetry)
        	this.lowLevelPosition.x = -this.lowLevelPosition.x;// on lui met la vraie position
		try 
		{
			deplacements.setX(this.lowLevelPosition.x);
	        deplacements.setY(this.lowLevelPosition.y);
		} 
		catch (SerialConnexionException e)
		{
            log.critical("Catch de "+e+" dans setPosition", this);
			log.critical( e.logStack(), this);
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
        this.lowLevelOrientation = orientation;
        if(symetry)
        	this.lowLevelOrientation = Math.PI-this.lowLevelOrientation; // la vraie orientation
        try 
        {
    		deplacements.setOrientation(this.lowLevelOrientation);
        }
        catch (SerialConnexionException e) 
        {
            log.critical("Catch de "+e+" dans setOrientation", this);
			log.critical( e.logStack(), this);
        }
    }

    /**
     * @return la position du robot en debut de match
     */
    public Vec2 getPosition()
    {
        updateCurrentPositionAndOrientation();
        Vec2 out = highLevelPosition.clone();
        return out;
    }

    /**
     * 
     * @return l'orientation du robot en debut de match
     */
    public double getOrientation()
    {
        updateCurrentPositionAndOrientation();
        return highLevelOrientation;
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
			log.critical( e.logStack(), this);

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
