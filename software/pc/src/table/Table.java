package table;


import smartMath.Vec2;
import table.obstacles.*;

import java.util.ArrayList;

import container.Service;
import enums.Elements;
import utils.*;

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
	
	//==================================
	// Definition des elements de sable
	//==================================
	
	/** taille des cubes/cylindres */
	public static int sandSize = 58;
	
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
	
	//==========
	// Objectifs
	//==========
	
	/** poissons */
	public ArrayList<Fish> ourFish = new ArrayList<Fish>();
	
	/** portes fermées ou non */
	public boolean extDoorClosed = false;
	public boolean intDoorClosed = false;

	
	/** point de depart du match a modifier a chaque base roulante */
	public static final Vec2 entryPosition = new Vec2(1300, 1200); //TODO position entree
	
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
		//TODO initialiser la table (instancier les elems de jeu, etc...)
		
		//==================================================================================
		// Instanciation des elements de sable centraux (VOIR LES REGLES POUR LES POSITIONS)
		// L'ordre est : les niveaux du sol au ciel,
		//               puis les lignes du fond a l'avant à chaque niveau, 
		//               et enfin les cubes de l'ennemi à nous à chaque ligne
		//==================================================================================
		
		//Ligne de cubes au sol, au fond, de l'ennemi à nous
		for(int i = 0 ; i < 9 ; i++)
		{
			centerCubes.add(new Sand(Elements.SAND_CUBE, new Vec2(-(4*sandSize)+(i*sandSize), 2000-(sandSize/2)), 0));
		}
		
		//Ligne de cubes au sol, avant, de l'ennemi à nous
		for(int i = 0 ; i < 3 ; i++)
		{
			centerCubes.add(new Sand(Elements.SAND_CUBE, new Vec2(-sandSize+(i*sandSize), 2000-((3*sandSize)/2)), 0));
		}
		
		//Ligne de cubes niveau 1, au fond, de l'ennemi à nous
		for(int i = 0 ; i < 3 ; i++)
		{
			centerCubes.add(new Sand(Elements.SAND_CUBE, new Vec2(-sandSize+(i*sandSize), 2000-(sandSize/2)), 1));
		}
		
		//Cube au niveau 1, avant centré
		centerCubes.add(new Sand(Elements.SAND_CUBE, new Vec2(0, 2000-((3*sandSize)/2)), 1));
		
		//Cylindre au sol tout à l'avant
		centerCylinders.add(new Sand(Elements.SAND_CYLINDER, new Vec2(0, 2000-((5*sandSize)/2)), 0));
		
		//Cylindres au niveau 1 (C'est le bordel, la flemme de mettre un commentaire pour chacun)
		
		//Cylindres formant les flancs de la montagne du côté ennemi
		centerCylinders.add(new Sand(Elements.SAND_CYLINDER, new Vec2(-(sandSize*3), 2000-(sandSize/2)), 1));
		centerCylinders.add(new Sand(Elements.SAND_CYLINDER, new Vec2(-(sandSize*2), 2000-(sandSize/2)), 1));
		
		//Cylindres formant les flancs de la montagne de notre côté
		centerCylinders.add(new Sand(Elements.SAND_CYLINDER, new Vec2((sandSize*2), 2000-(sandSize/2)), 1));
		centerCylinders.add(new Sand(Elements.SAND_CYLINDER, new Vec2((sandSize*3), 2000-(sandSize/2)), 1));
		
		// Cylindres à l'avant de la montagne 
		centerCylinders.add(new Sand(Elements.SAND_CYLINDER, new Vec2(-sandSize, 2000-((3*sandSize)/2)), 1));
		centerCylinders.add(new Sand(Elements.SAND_CYLINDER, new Vec2(sandSize, 2000-((3*sandSize)/2)), 1));
		
		//Cylindres au niveau 2, au fond
		for(int i=0; i < 5 ; i++)
		{
			centerCylinders.add(new Sand(Elements.SAND_CYLINDER, new Vec2(-(2*sandSize)+(i*sandSize), 2000-(sandSize/2)), 2));
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
		
		//TODO elements annexes
		
		//Cubes devant notre serviette
		ourTowelCubes.add(new Sand(Elements.SAND_CUBE, new Vec2(850-(sandSize/2), 1100+(sandSize/2)), 0));
		ourTowelCubes.add(new Sand(Elements.SAND_CUBE, new Vec2(850+(sandSize/2), 1100+(sandSize/2)), 0));
		ourTowelCubes.add(new Sand(Elements.SAND_CUBE, new Vec2(850-(sandSize/2), 1100-(sandSize/2)), 0));
		ourTowelCubes.add(new Sand(Elements.SAND_CUBE, new Vec2(850+(sandSize/2), 1100-(sandSize/2)), 0));

		//Cubes devant leur serviette
		theirTowelCubes.add(new Sand(Elements.SAND_CUBE, new Vec2(-850-(sandSize/2), 1100+(sandSize/2)), 0));
		theirTowelCubes.add(new Sand(Elements.SAND_CUBE, new Vec2(-850+(sandSize/2), 1100+(sandSize/2)), 0));
		theirTowelCubes.add(new Sand(Elements.SAND_CUBE, new Vec2(-850-(sandSize/2), 1100-(sandSize/2)), 0));
		theirTowelCubes.add(new Sand(Elements.SAND_CUBE, new Vec2(-850+(sandSize/2), 1100-(sandSize/2)), 0));
		
		//Nos cubes devant la dune
		for(int i=0 ; i<2 ;i++)
		{
			ourDuneCubes.add(new Sand(Elements.SAND_CUBE, new Vec2(678-((3*sandSize)/2), 2000-(sandSize/2)), i));
			ourDuneCubes.add(new Sand(Elements.SAND_CUBE, new Vec2(678-(sandSize/2), 2000-(sandSize/2)), i));
			ourDuneCubes.add(new Sand(Elements.SAND_CUBE, new Vec2(678-((3*sandSize)/2), 2000-((3*sandSize)/2)), i));
			ourDuneCubes.add(new Sand(Elements.SAND_CUBE, new Vec2(678-(sandSize/2), 2000-((3*sandSize/2))), i));
		}
		
		//Leur cubes devant la dune
		for(int i=0 ; i<2 ;i++)
		{
			theirDuneCubes.add(new Sand(Elements.SAND_CUBE, new Vec2(-678+((3*sandSize)/2), 2000-(sandSize/2)), i));
			theirDuneCubes.add(new Sand(Elements.SAND_CUBE, new Vec2(-678+(sandSize/2), 2000-(sandSize/2)), i));
			theirDuneCubes.add(new Sand(Elements.SAND_CUBE, new Vec2(-678+((3*sandSize)/2), 2000-((3*sandSize)/2)), i));
			theirDuneCubes.add(new Sand(Elements.SAND_CUBE, new Vec2(-678+(sandSize/2), 2000-((3*sandSize/2))), i));
		}
		
		
		
	}
	
	public ObstacleManager getObstacleManager()
	{
		return mObstacleManager;
	}

	/* (non-Javadoc)
	 * @see container.Service#updateConfig()
	 */
	@Override
	public void updateConfig()
	{
		// TODO update config
	}
}

