package table;


import container.Service;
import enums.Color;
import enums.Elements;
import exceptions.ConfigPropertyNotFoundException;
import smartMath.Circle;
import smartMath.Vec2;
import table.obstacles.Obstacle;
import table.obstacles.ObstacleCircular;
import table.obstacles.ObstacleManager;
import table.obstacles.ObstacleRectangular;
import utils.Config;
import utils.Log;

import java.util.ArrayList;

/* Positions :
 * 			_______________________________________________________
 * 			|-1500,1000         	0,1000		         1500,1000|
 * 			|           		      							  |
 * 			|           		     							  |
 * 			|           	 		  							  |
 * 			|           	 		  							  |
 * 			|           	 		  							  |
 * 			|-1500,0           		 0,0       				1500,0|
 *          -------------------------------------------------------
 *          
 *  
 */

/**
 * Stocke toute les informations liées a la table (muables et immuables) au cours d'un match.
 * 
 * @author julian
 */
public class Table implements Service
{
	/** Le gestionnaire d'obstacle. */
	private ObstacleManager mObstacleManager;
	
	/** système de log sur lequel écrire. */
	private Log log;

	/** endroit ou lire la configuration du robot */
	private Config config;
	
	/** configuration pour les différents arrangements des coquillages, vaut au choix entre 1 et 5*/
	public int configShell = 1;
	//==================================
	// Definition des elements de sable
	//==================================
	
	/** taille des cubes/cylindres */
	public static int sandSize = 58;
	
	/** taille des coquillages */
	public final static int shellSize = 77;
	
	/** Cubes de sable centraux */
	public ArrayList<Sand> centerCubes = new ArrayList<Sand>(16);
	
	/** Cubes de sable annexes devant la dune, de notre cote */
	public ArrayList<Sand> ourDuneCubes = new ArrayList<Sand>(8);
	
	/** Cubes de sable annexes devant la dune, cote adversaire */
	public ArrayList<Sand> theirDuneCubes = new ArrayList<Sand>(8);
	
	/** Cubes de sable annexes devant notre serviette */
	public ArrayList<Sand> ourTowelCubes = new ArrayList<Sand>(4);
	
	/** Cubes de sable annexes devant la serviette adverse */
	public ArrayList<Sand> theirTowelCubes = new ArrayList<Sand>(4);
	
	/** Cylindre de sable centraux */
	public ArrayList<Sand> centerCylinders = new ArrayList<Sand>(16);
	
	/** Cylindre annexe devant notre serviette */
	public Sand ourTowelCylinders = new Sand(Elements.SAND_CYLINDER, new Vec2(850 ,1100), 1);
	
	/** Cylindre annexe devant leur serviette */
	public Sand theirTowelCylinders = new Sand(Elements.SAND_CYLINDER, new Vec2(-850 ,1100), 1);
	
	/** Cylindre annexe devant la dune, de notre cote */
	public Sand ourDuneCylinders = new Sand(Elements.SAND_CYLINDER, new Vec2(678-sandSize, 2000-sandSize), 2);
	
	/** Cylindre annexe devant la dune, cote adversaire */
	public Sand theirDuneCylinders = new Sand(Elements.SAND_CYLINDER, new Vec2(sandSize-678, 2000-sandSize), 2);
	
	/** Cones de sable centraux */
	public ArrayList<Sand> centerCones = new ArrayList<Sand>(5);
	
	/** Cone annexe devant notre serviette */
	public Sand ourTowelCones = new Sand(Elements.SAND_CONE, new Vec2(850 ,1100), 2);
	
	/** Cone annexe devant leur serviette */
	public Sand theirTowelCones = new Sand(Elements.SAND_CONE, new Vec2(-850 ,1100), 2);
	
	/** Cone annexe devant la dune, de notre cote */
	public Sand ourDuneCones = new Sand(Elements.SAND_CONE, new Vec2(678-sandSize, 2000-sandSize), 3);
	
	/** Cone annexe devant la dune, cote adversaire */
	public Sand theirDuneCones = new Sand(Elements.SAND_CONE, new Vec2(sandSize-678, 2000-sandSize), 3);
	
	//==================================
	// Definition des coquillages
	//==================================
	
	/** Hauteur en mm d'un coquillage*/
	public static int shellHeight = 25;
	
	/** Diamètre en mm d'un coquillage */
	public static float shellDiam = (float) 76.2;
	
	/** Nos coquillages */
	public static ArrayList<Shell> ourShells = new ArrayList<Shell>(5);
			
	/** Coquillages ennemis */
	public static ArrayList<Shell> theirShells = new ArrayList<Shell>(5);
			
	/**Coquillages neutres*/
	public static ArrayList<Shell> neutralShells = new ArrayList<Shell>(6);

    /** Spéciaux, ils sont à différencier du reste */
    public ArrayList<Shell> specialShells = new ArrayList<>(2);
	
	//==========
	// Objectifs
	//==========
	
	/** poissons */
	public ArrayList<Fish> ourFish = new ArrayList<Fish>(4);
	
	/** entier indiquant le nombre de poissons dans le filet */
	public int fishesFished=0;

	/** nombre de coquillages sur notre tapis */
	public int shellsObtained=0;
	
	/** portes fermées ou non */
	public boolean extDoorClosed = false;
	public boolean intDoorClosed = false;

	
	/** point de depart du match a modifier a chaque base roulante */
	public static final Vec2 entryPosition = new Vec2(1500-180,1215); //TODO position entree
	
	/**
	 * Instancie une nouvelle table
	 *
	 * @param log le système de log sur lequel écrire.
	 * @param config l'endroit ou lire la configuration du robot
	 */
	public Table(Log log, Config config)
	{
		this.log = log;
		this.config = config;
		this.mObstacleManager = new ObstacleManager(log, config);
		
		initialise();
	}
	
	public void initialise() // initialise la table du debut du jeu
	{

		//==================================================================================
		// Instanciation des elements de sable centraux (VOIR LES REGLES POUR LES POSITIONS)
		// L'ordre est : les niveaux du sol au ciel,
		//               puis les lignes du fond a l'avant à chaque niveau, 
		//               et enfin les cubes de l'ennemi à nous à chaque ligne
		//==================================================================================
		
		//Ligne de cubes au sol, au fond, de l'ennemi à nous
		for(int i = 0 ; i < 9 ; i++)
		{
			Vec2 pos = new Vec2(-(4*sandSize)+(i*sandSize),  2000-(sandSize/2));
			//mObstacleManager.addRectangle(new ObstacleRectangular(pos, sandSize, sandSize));
			centerCubes.add(new Sand(Elements.SAND_CUBE, pos, 0));
		}
		
		//Ligne de cubes au sol, avant, de l'ennemi à nous
		for(int i = 0 ; i < 3 ; i++)
		{
			Vec2 pos = new Vec2(-sandSize+(i*sandSize), 2000-((3*sandSize)/2));
			//mObstacleManager.addRectangle(new ObstacleRectangular(pos, sandSize, sandSize));
			centerCubes.add(new Sand(Elements.SAND_CUBE, pos , 0));
		}
		
		//Ligne de cubes niveau 1, au fond, de l'ennemi à nous
		for(int i = 0 ; i < 3 ; i++)
		{
			Vec2 pos = new Vec2(-sandSize+(i*sandSize), 2000-(sandSize/2));
			//mObstacleManager.addRectangle(new ObstacleRectangular(pos, sandSize, sandSize));
			centerCubes.add(new Sand(Elements.SAND_CUBE, pos, 1));
		}
		
		//Cube au niveau 1, avant centré
		Vec2 pos = new Vec2(0, 2000-((3*sandSize)/2));
		//mObstacleManager.addRectangle(new ObstacleRectangular(pos, sandSize, sandSize));
		centerCubes.add(new Sand(Elements.SAND_CUBE, pos, 1));
		
		//Cylindre au sol tout à l'avant
		pos = new Vec2(0, 2000-((5*sandSize)/2));
		//mObstacleManager.addCircular(new ObstacleCircular(pos,sandSize/2));
		centerCylinders.add(new Sand(Elements.SAND_CYLINDER, pos, 0));
		
		//Cylindres au niveau 1 (C'est le bordel, la flemme de mettre un commentaire pour chacun)
		
		//Cylindres formant les flancs de la montagne du côté ennemi
		for(int i = 0 ; i < 2 ; i++)
		{
			pos = new Vec2(-(sandSize*(2+i)), 2000-(sandSize/2));
			//mObstacleManager.addCircular(new ObstacleCircular(pos,sandSize/2));
			centerCylinders.add(new Sand(Elements.SAND_CYLINDER, pos, 1));
		}
		
		//Cylindres formant les flancs de la montagne de notre côté
		for(int i = 0 ; i < 2 ; i++)
		{
			pos = new Vec2((sandSize*(2+i)), 2000-(sandSize/2));
			//mObstacleManager.addCircular(new ObstacleCircular(pos,sandSize/2));
			centerCylinders.add(new Sand(Elements.SAND_CYLINDER, pos, 1));
		}
		
		// Cylindres à l'avant de la montagne 
		for(int i = -1 ; i < 2 ; i+=2)
		{
			pos = new Vec2(i*sandSize, 2000-((3*sandSize)/2));
			//mObstacleManager.addCircular(new ObstacleCircular(pos,sandSize/2));
			centerCylinders.add(new Sand(Elements.SAND_CYLINDER, pos, 1));
		}
		
		//Cylindres au niveau 2, au fond
		for(int i=0; i < 5 ; i++)
		{
			pos = new Vec2(-(2*sandSize)+(i*sandSize), 2000-(sandSize/2));
			//mObstacleManager.addCircular(new ObstacleCircular(pos,sandSize/2));
			centerCylinders.add(new Sand(Elements.SAND_CYLINDER, pos, 2));
		}
		
		//Cylindre niveau 2 centré
		centerCylinders.add(new Sand(Elements.SAND_CYLINDER, new Vec2(0, 2000-((3*sandSize)/2)), 2));
		
		//Cylindres niveau 3
		for(int i=0; i<3 ; i++)
		{
			centerCylinders.add(new Sand(Elements.SAND_CYLINDER, new Vec2(-(sandSize)+(i*sandSize), 2000-(sandSize/2)), 3));
		}
		
		//Cones niveau 3
		centerCones.add(new Sand(Elements.SAND_CONE, new Vec2(-2*sandSize, 2000-(sandSize/2)), 3));
		centerCones.add(new Sand(Elements.SAND_CONE, new Vec2(2*sandSize, 2000-(sandSize/2)), 3));
		
		//Cones niveau 4
		for(int i=0; i<3 ; i++)
		{
			centerCones.add(new Sand(Elements.SAND_CONE, new Vec2(-sandSize+(i*sandSize), 2000-(sandSize/2)), 4));
		}
		
		//==================================================================================
		// Instanciation des elements de sable annexes (VOIR LES REGLES POUR LES POSITIONS)
		// L'ordre est : les niveaux du sol au ciel,
		//               puis les lignes du fond a l'avant à chaque niveau, 
		//               et enfin les cubes de l'ennemi à nous à chaque ligne
		//==================================================================================


		
		//Cubes devant notre serviette
		pos = new Vec2(850-(sandSize/2), 1100+(sandSize/2));
		//mObstacleManager.addRectangle(new ObstacleRectangular(pos, sandSize, sandSize));
		ourTowelCubes.add(new Sand(Elements.SAND_CUBE, pos, 0));
		
		pos = new Vec2(850+(sandSize/2), 1100+(sandSize/2));
		//mObstacleManager.addRectangle(new ObstacleRectangular(pos, sandSize, sandSize));
		ourTowelCubes.add(new Sand(Elements.SAND_CUBE, pos, 0));
		
		pos = new Vec2(850-(sandSize/2), 1100-(sandSize/2));
		//mObstacleManager.addRectangle(new ObstacleRectangular(pos, sandSize, sandSize));
		ourTowelCubes.add(new Sand(Elements.SAND_CUBE, pos, 0));
		
		pos = new Vec2(850+(sandSize/2), 1100-(sandSize/2));
		//mObstacleManager.addRectangle(new ObstacleRectangular(pos, sandSize, sandSize));
		ourTowelCubes.add(new Sand(Elements.SAND_CUBE, pos, 0));

		//Cubes devant leur serviette
		pos = new Vec2(-850-(sandSize/2), 1100+(sandSize/2));
		//mObstacleManager.addRectangle(new ObstacleRectangular(pos, sandSize, sandSize));
		theirTowelCubes.add(new Sand(Elements.SAND_CUBE, pos, 0));
		
		pos = new Vec2(-850+(sandSize/2), 1100+(sandSize/2));
		//mObstacleManager.addRectangle(new ObstacleRectangular(pos, sandSize, sandSize));
		theirTowelCubes.add(new Sand(Elements.SAND_CUBE, pos, 0));
		
		pos = new Vec2(-850-(sandSize/2), 1100-(sandSize/2));
		//mObstacleManager.addRectangle(new ObstacleRectangular(pos, sandSize, sandSize));
		theirTowelCubes.add(new Sand(Elements.SAND_CUBE, pos, 0));
		
		pos = new Vec2(-850+(sandSize/2), 1100-(sandSize/2));
		//mObstacleManager.addRectangle(new ObstacleRectangular(pos, sandSize, sandSize));
		theirTowelCubes.add(new Sand(Elements.SAND_CUBE, pos, 0));
		
		//Nos cubes devant la dune
		for(int i=0 ; i<2 ;i++)
		{
			pos = new Vec2(678-((3*sandSize)/2), 2000-(sandSize/2));
			//mObstacleManager.addRectangle(new ObstacleRectangular(pos, sandSize, sandSize));
			ourDuneCubes.add(new Sand(Elements.SAND_CUBE, pos, i));
			
			pos = new Vec2(678-(sandSize/2), 2000-(sandSize/2));
			//mObstacleManager.addRectangle(new ObstacleRectangular(pos, sandSize, sandSize));
			ourDuneCubes.add(new Sand(Elements.SAND_CUBE, pos, i));
			
			pos = new Vec2(678-((3*sandSize)/2), 2000-((3*sandSize)/2));
			//mObstacleManager.addRectangle(new ObstacleRectangular(pos, sandSize, sandSize));
			ourDuneCubes.add(new Sand(Elements.SAND_CUBE, pos, i));
			
			pos = new Vec2(678-(sandSize/2), 2000-((3*sandSize/2)));
			//mObstacleManager.addRectangle(new ObstacleRectangular(pos, sandSize, sandSize));
			ourDuneCubes.add(new Sand(Elements.SAND_CUBE, pos, i));
		}
		
		//Leur cubes devant la dune
		for(int i=0 ; i<2 ;i++)
		{
			pos = new Vec2(-678+((3*sandSize)/2), 2000-(sandSize/2));
			//mObstacleManager.addRectangle(new ObstacleRectangular(pos, sandSize, sandSize));
			theirDuneCubes.add(new Sand(Elements.SAND_CUBE, pos, i));
			
			pos = new Vec2(-678+(sandSize/2), 2000-(sandSize/2));
			//mObstacleManager.addRectangle(new ObstacleRectangular(pos, sandSize, sandSize));
			theirDuneCubes.add(new Sand(Elements.SAND_CUBE, pos, i));
			
			pos = new Vec2(-678+((3*sandSize)/2), 2000-((3*sandSize)/2));
			//mObstacleManager.addRectangle(new ObstacleRectangular(pos, sandSize, sandSize));
			theirDuneCubes.add(new Sand(Elements.SAND_CUBE, pos, i));
			
			pos = new Vec2(-678+(sandSize/2), 2000-((3*sandSize/2)));
			//mObstacleManager.addRectangle(new ObstacleRectangular(pos, sandSize, sandSize));
			theirDuneCubes.add(new Sand(Elements.SAND_CUBE, pos, i));
		}
		
		//=======================================================================
		// Instanciation des coquillages selon la valeur de configShell 
		// L'ordre est : les niveaux du sol au ciel,
		//               de l'ennemi à nous,
		//               du fond à l'avant,
		//               d'abord les notres, puis les leurs et enfin les neutres
		//=======================================================================

		if (configShell == 1)
		{
			pos = new Vec2(-300,350);
			mObstacleManager.addObstacle(new ObstacleCircular(pos, shellSize/2 + mObstacleManager.mRobotRadius));
			ourShells.add(new Shell(pos,Color.ALLY, new Circle(pos, 200)));
			
			pos = new Vec2(600,550);
			mObstacleManager.addObstacle(new ObstacleCircular(pos, shellSize/2 + mObstacleManager.mRobotRadius));
			ourShells.add(new Shell(pos,Color.ALLY, new Circle(pos, 200)));
			
			pos = new Vec2(-600,550);
			mObstacleManager.addObstacle(new ObstacleCircular(pos, shellSize/2 + mObstacleManager.mRobotRadius));
			theirShells.add(new Shell(pos,Color.ENNEMY, new Circle(pos, 200)));
			
			pos = new Vec2(300,350);
			mObstacleManager.addObstacle(new ObstacleCircular(pos, shellSize/2 + mObstacleManager.mRobotRadius));
			theirShells.add(new Shell(pos,Color.ENNEMY, new Circle(pos, 200)));
			
			pos = new Vec2(-1425,200);
			mObstacleManager.addObstacle(new ObstacleCircular(pos, shellSize/2 + mObstacleManager.mRobotRadius));
			theirShells.add(new Shell(pos,Color.ENNEMY, new Circle(pos, 200)));
			
			pos = new Vec2(-1300,75);
			mObstacleManager.addObstacle(new ObstacleCircular(pos, shellSize/2 + mObstacleManager.mRobotRadius));
			theirShells.add(new Shell(pos,Color.ENNEMY, new Circle(pos, 200)));
			
			pos = new Vec2(-1425,75);
			mObstacleManager.addObstacle(new ObstacleCircular(pos, shellSize/2 + mObstacleManager.mRobotRadius));
			theirShells.add(new Shell(pos,Color.ENNEMY, new Circle(pos, 200)));
			
			pos = new Vec2(-1300,750);
			mObstacleManager.addObstacle(new ObstacleCircular(pos, shellSize/2 + mObstacleManager.mRobotRadius));
			neutralShells.add(new Shell(pos,Color.NEUTRAL, new Circle(pos, 200)));
			
			pos = new Vec2(-1300,450);
			mObstacleManager.addObstacle(new ObstacleCircular(pos, shellSize/2 + mObstacleManager.mRobotRadius));
			neutralShells.add(new Shell(pos,Color.NEUTRAL, new Circle(pos, 200)));
			
			pos = new Vec2(0,450);
			mObstacleManager.addObstacle(new ObstacleCircular(pos, shellSize/2 + mObstacleManager.mRobotRadius));
			neutralShells.add(new Shell(pos,Color.NEUTRAL, new Vec2(pos.x+200, pos.y-100)));
			
			pos = new Vec2(0,150);
			mObstacleManager.addObstacle(new ObstacleCircular(pos, shellSize/2 + mObstacleManager.mRobotRadius));
			neutralShells.add(new Shell(pos,Color.NEUTRAL, new Vec2(pos.x+200, pos.y+100)));
			
			pos = new Vec2(1300,750);
			mObstacleManager.addObstacle(new ObstacleCircular(pos, shellSize/2 + mObstacleManager.mRobotRadius));
            specialShells.add(new Shell(pos,Color.NEUTRAL, new Circle(pos, 200)));
			
			pos = new Vec2(1300,450);
			mObstacleManager.addObstacle(new ObstacleCircular(pos, shellSize/2 + mObstacleManager.mRobotRadius));
            specialShells.add(new Shell(pos,Color.NEUTRAL, new Circle(pos, 200)));
		}
		
		else if (configShell == 2) {
            pos = new Vec2(300, 350);
            mObstacleManager.addObstacle(new ObstacleCircular(pos, shellSize/2));
            ourShells.add(new Shell(pos, Color.ALLY, new Circle(pos, 200)));

            pos = new Vec2(600, 550);
            mObstacleManager.addObstacle(new ObstacleCircular(pos, shellSize/2));
            ourShells.add(new Shell(pos, Color.ALLY, new Circle(pos, 200)));

            pos = new Vec2(1300, 750);
            mObstacleManager.addObstacle(new ObstacleCircular(pos, shellSize/2));
            specialShells.add(new Shell(pos, Color.ALLY, new Circle(pos, 200)));

            pos = new Vec2(-1300, 750);
            mObstacleManager.addObstacle(new ObstacleCircular(pos, shellSize/2));
            theirShells.add(new Shell(pos, Color.ENNEMY, new Circle(pos, 200)));

            pos = new Vec2(-600, 550);
            mObstacleManager.addObstacle(new ObstacleCircular(pos, shellSize/2));
            theirShells.add(new Shell(pos, Color.ENNEMY, new Circle(pos, 200)));

            pos = new Vec2(-300, 350);
            mObstacleManager.addObstacle(new ObstacleCircular(pos, shellSize/2));
            theirShells.add(new Shell(pos, Color.ENNEMY, new Circle(pos, 200)));

            pos = new Vec2(-1300, 450);
            mObstacleManager.addObstacle(new ObstacleCircular(pos, shellSize/2));
            neutralShells.add(new Shell(pos, Color.NEUTRAL, new Circle(pos, 200)));

            pos = new Vec2(0, 450);
            mObstacleManager.addObstacle(new ObstacleCircular(pos, shellSize/2));
            neutralShells.add(new Shell(pos, Color.NEUTRAL, new Vec2(pos.x+200, pos.y-100)));

            pos = new Vec2(0, 150);
            mObstacleManager.addObstacle(new ObstacleCircular(pos, shellSize/2));
            neutralShells.add(new Shell(pos, Color.NEUTRAL, new Vec2(pos.x+200, pos.y+100)));

            pos = new Vec2(1300, 450);
            mObstacleManager.addObstacle(new ObstacleCircular(pos, shellSize/2));
            specialShells.add(new Shell(pos, Color.NEUTRAL, new Circle(pos, 200)));
        }
		else if (configShell == 3)
		{
			pos = new Vec2(300,350);
			mObstacleManager.addObstacle(new ObstacleCircular(pos, shellSize/2));
			ourShells.add(new Shell(pos,Color.ALLY, new Circle(pos, 200)));
			
			pos = new Vec2(900,750);
			mObstacleManager.addObstacle(new ObstacleCircular(pos, shellSize/2));
			ourShells.add(new Shell(pos,Color.ALLY, new Circle(pos, 200)));
			
			pos = new Vec2(1300,750);
			mObstacleManager.addObstacle(new ObstacleCircular(pos, shellSize/2));
            specialShells.add(new Shell(pos,Color.ALLY, new Circle(pos, 200)));
			
			pos = new Vec2(-1300,750);
			mObstacleManager.addObstacle(new ObstacleCircular(pos, shellSize/2));
			theirShells.add(new Shell(pos,Color.ENNEMY, new Circle(pos, 200)));
			
			pos = new Vec2(-900,750);
			mObstacleManager.addObstacle(new ObstacleCircular(pos, shellSize/2));
			theirShells.add(new Shell(pos,Color.ENNEMY, new Circle(pos, 200)));
			
			pos = new Vec2(-300,350);
			mObstacleManager.addObstacle(new ObstacleCircular(pos, shellSize/2));
			theirShells.add(new Shell(pos,Color.ENNEMY, new Circle(pos, 200)));

			pos = new Vec2(-1300,450);
			mObstacleManager.addObstacle(new ObstacleCircular(pos, shellSize/2));
			neutralShells.add(new Shell(pos,Color.NEUTRAL, new Circle(pos, 200)));
			
			pos = new Vec2(-900,450);
			mObstacleManager.addObstacle(new ObstacleCircular(pos, shellSize/2));
			neutralShells.add(new Shell(pos,Color.NEUTRAL, new Circle(pos, 200)));
			
			pos = new Vec2(900,450);
			mObstacleManager.addObstacle(new ObstacleCircular(pos, shellSize/2));
			neutralShells.add(new Shell(pos,Color.NEUTRAL, new Circle(pos, 200)));
			
			pos = new Vec2(1300,450);
			mObstacleManager.addObstacle(new ObstacleCircular(pos, shellSize/2));
            specialShells.add(new Shell(pos,Color.NEUTRAL, new Circle(pos, 200)));
		}
		
		else if (configShell == 4)
		{
			pos = new Vec2(900,750);
			mObstacleManager.addObstacle(new ObstacleCircular(pos, shellSize/2));
			ourShells.add(new Shell(pos,Color.ALLY, new Circle(pos, 200)));
			
			pos = new Vec2(1300,750);
			mObstacleManager.addObstacle(new ObstacleCircular(pos, shellSize/2));
            specialShells.add(new Shell(pos,Color.ALLY, new Circle(pos, 200)));
			
			pos = new Vec2(1300,450);
			mObstacleManager.addObstacle(new ObstacleCircular(pos, shellSize/2));
            specialShells.add(new Shell(pos,Color.ALLY, new Circle(pos, 200)));
			
			pos = new Vec2(-1300,750);
			mObstacleManager.addObstacle(new ObstacleCircular(pos, shellSize/2));
			theirShells.add(new Shell(pos,Color.ENNEMY, new Circle(pos, 200)));
			
			pos = new Vec2(-1300,450);
			mObstacleManager.addObstacle(new ObstacleCircular(pos, shellSize/2));
			theirShells.add(new Shell(pos,Color.ENNEMY, new Circle(pos, 200)));
			
			pos = new Vec2(-900,750);
			mObstacleManager.addObstacle(new ObstacleCircular(pos, shellSize/2));
			theirShells.add(new Shell(pos,Color.ENNEMY, new Circle(pos, 200)));
			
			pos = new Vec2(-900,450);
			mObstacleManager.addObstacle(new ObstacleCircular(pos, shellSize/2));
			neutralShells.add(new Shell(pos,Color.NEUTRAL, new Circle(pos, 200)));
			
			pos  = new Vec2(-300,350);
			mObstacleManager.addObstacle(new ObstacleCircular(pos, shellSize/2));
			neutralShells.add(new Shell(pos,Color.NEUTRAL, new Circle(pos, 200)));
			
			pos = new Vec2(300,350);
			mObstacleManager.addObstacle(new ObstacleCircular(pos, shellSize/2));
			neutralShells.add(new Shell(pos,Color.NEUTRAL, new Circle(pos, 200)));
			
			pos = new Vec2(900,450);
			mObstacleManager.addObstacle(new ObstacleCircular(pos, shellSize/2));
			neutralShells.add(new Shell(pos,Color.NEUTRAL, new Circle(pos, 200)));
		}
		
		else if (configShell == 5)
		{
			pos = new Vec2(-900,450);
			mObstacleManager.addObstacle(new ObstacleCircular(pos, shellSize/2));
			ourShells.add(new Shell(pos,Color.ALLY, new Circle(pos, 200)));
			
			pos = new Vec2(900,750);
			mObstacleManager.addObstacle(new ObstacleCircular(pos, shellSize/2));
			ourShells.add(new Shell(pos,Color.ALLY, new Circle(pos, 200)));
			
			pos = new Vec2(1300,750);
			mObstacleManager.addObstacle(new ObstacleCircular(pos, shellSize/2));
            specialShells.add(new Shell(pos,Color.ALLY, new Circle(pos, 200)));
			
			pos = new Vec2(1300,450);
			mObstacleManager.addObstacle(new ObstacleCircular(pos, shellSize/2));
            specialShells.add(new Shell(pos,Color.ALLY, new Circle(pos, 200)));

			pos = new Vec2(-1300,750);
			mObstacleManager.addObstacle(new ObstacleCircular(pos, shellSize/2));
			theirShells.add(new Shell(pos,Color.ENNEMY, new Circle(pos, 200)));
			
			pos = new Vec2(-1300,450);
			mObstacleManager.addObstacle(new ObstacleCircular(pos, shellSize/2));
			theirShells.add(new Shell(pos,Color.ENNEMY, new Circle(pos, 200)));
			
			pos = new Vec2(-900,750);
			mObstacleManager.addObstacle(new ObstacleCircular(pos, shellSize/2));
			theirShells.add(new Shell(pos,Color.ENNEMY, new Circle(pos, 200)));
			
			pos = new Vec2(900,450);
			mObstacleManager.addObstacle(new ObstacleCircular(pos, shellSize/2));
			theirShells.add(new Shell(pos,Color.ENNEMY, new Circle(pos, 200)));
			
			pos = new Vec2(-900,150);
			mObstacleManager.addObstacle(new ObstacleCircular(pos, shellSize/2));
			neutralShells.add(new Shell(pos,Color.NEUTRAL, new Circle(pos, 200)));
			
			pos = new Vec2(900,150);
			mObstacleManager.addObstacle(new ObstacleCircular(pos, shellSize/2));
			neutralShells.add(new Shell(pos,Color.NEUTRAL, new Circle(pos, 200)));

		}

		
		//=======================================================================
		// Instanciation des Poissons
		//=======================================================================
		
		// Ajout des quatre poissons qu'on récupère
		for(int i=0; i<4 ; i++)
		{
			ourFish.add(new Fish());
		}
		
	}

	public ObstacleManager getObstacleManager()
	{
		return mObstacleManager;
	}

    /**
     * Supprime tous les coquillages alliés et neutres de l'obstacle manager, les renvoie
     * @return les obstacles supprimés
     */
	public ArrayList<ObstacleCircular> deleteAllTheShells()
    {
        ArrayList<ObstacleCircular> deleted = new ArrayList<>(); //Les obstacles que l'on a delete
        ArrayList<Obstacle> temp; // Liste temporaire pour la vérif que l'on a bien delete un coquillage
        ArrayList<Shell> copy = ourShells; // On copie la liste de nos coquillages
        copy.addAll(neutralShells); // On y ajoute les neutres
        copy.addAll(theirShells);
		copy.addAll(specialShells);

        for(Shell i : copy) // On la parcourt
        {
            temp = mObstacleManager.freePoint(i.position);
            for(Obstacle j : temp)
            {
                if (j instanceof ObstacleCircular) //Si c'est bien un coquillage
                {
                    deleted.add((ObstacleCircular) j);
                }
                else //Sinon on le replace dans la table
                {
                    mObstacleManager.addObstacle((ObstacleRectangular) j);
                }
            }
        }

        return deleted;
    }

	/* (non-Javadoc)
	 * @see container.Service#updateConfig()
	 */
	@Override
	public void updateConfig()
	{
		// TODO update config
		try {
			configShell = Integer.parseInt(config.getProperty("config_shell"));
		} catch (ConfigPropertyNotFoundException e) {
			e.printStackTrace();
		}
	}
}

