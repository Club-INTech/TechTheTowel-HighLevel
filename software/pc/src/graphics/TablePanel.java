package graphics;

import java.awt.Graphics;

import javax.swing.JPanel;

import java.awt.Color; 
import java.util.ArrayList;

import table.Table;
import smartMath.*;
import pathDingDing.*;
import robot.*;
import table.obstacles.*;

/**
 * panneau sur lequel est dessine la table
 * @author Etienne
 *
 */
public class TablePanel extends JPanel
{	
	/** numéro pour la serialisation	 */
	private static final long serialVersionUID = -3033815690221481964L;
	
	private ArrayList<Vec2> mPath;
	private Table mTable;
	private Robot mRobot;
	private boolean showGraph;
	private Graph mGraph;
	private boolean isRobotPresent = true;
	
	public TablePanel(Table table, RobotReal robot)
	{
		mPath = new ArrayList<Vec2>();
		mTable = table;
		mRobot = robot;
		showGraph = false;
	}
	
	public TablePanel(Table table)
	{
		mPath = new ArrayList<Vec2>();
		mTable = table;
		showGraph = false;
		isRobotPresent = false;
	}
	
	public void paintComponent(Graphics g)
	{
		// Les bords de la table
		g.setColor(Color.black);
	    g.fillRect(0, 0, this.getWidth(), this.getHeight());
	    
	    
	    // Lignes des obstacles 
	    g.setColor(Color.darkGray);
	    ArrayList<Segment> lines = mTable.getObstacleManager().getLines();
	    for(int i = 0; i < lines.size(); i++)
	    {
	    	g.drawLine((int)((lines.get(i).getA().x + 1500) * this.getWidth() / 3000), 
	    			   (int)((-lines.get(i).getA().y) * this.getHeight() / 2000 + this.getHeight()),
	    			   (int)((lines.get(i).getB().x + 1500) * this.getWidth() / 3000),
	    			   (int)((-lines.get(i).getB().y) * this.getHeight() / 2000 + this.getHeight()));
	    }
	    
	    
	    // Obstacles rectangulaires
	    g.setColor(Color.white);
	    ArrayList<ObstacleRectangular> rects = mTable.getObstacleManager().getRectangles();
	    for(int i = 0; i < rects.size(); i++)
	    {
	    	g.fillRect((rects.get(i).getPosition().x - (rects.get(i).getSizeX() / 2) + 1500) * this.getWidth() / 3000, 
	    			  -(rects.get(i).getPosition().y + rects.get(i).getSizeY()) * this.getHeight() / 2000 + this.getHeight(), 
	    			  rects.get(i).getSizeX() * this.getWidth() / 3000, 
	    			  rects.get(i).getSizeY() * this.getHeight() / 2000);
	    }
	    
	    
	    //  Le graphe du pathdingding
	    if(showGraph)
	    {
	    	//parcours des noeuds
	    	for(int i = 0; i < mGraph.getNodes().size(); i++)
	    	{
	    		g.setColor(new Color(200, 120, 15));
		    	g.fillOval( (mGraph.getNodes().get(i).x + 1500) * this.getWidth() / 3000 - 5,
	    			    -mGraph.getNodes().get(i).y * this.getHeight() / 2000 + this.getHeight() - 5,
	    			     10,
	    			     10);
		    	g.setColor(new Color(100, 60, 5));
	    		//parcours des liens de chaque noeud
	    		for(int j = 0; j < mGraph.getNodes().get(i).getLinkNumber(); j++)
	    			g.drawLine((mGraph.getNodes().get(i).x + 1500) * this.getWidth() / 3000, -mGraph.getNodes().get(i).y * this.getHeight() / 2000 + this.getHeight(), (mGraph.getNodes().get(i).getLink(j).getDestination().x + 1500) * this.getWidth() / 3000, -mGraph.getNodes().get(i).getLink(j).getDestination().y * this.getHeight() / 2000 + this.getHeight());
	    	}
	    }
	    
	    
	    // Les obstacles fixes : plots, gobelets
	    g.setColor(Color.white);
	    ArrayList<ObstacleCircular> fixedObstacles = mTable.getObstacleManager().getFixedObstacles();
	    for(int i = 0; i < fixedObstacles.size(); i++)
	    {
	    	g.drawOval((fixedObstacles.get(i).getPosition().x - (fixedObstacles.get(i).getRadius() + mTable.getObstacleManager().getRobotRadius()) + 1500) * this.getWidth() / 3000, -(fixedObstacles.get(i).getPosition().y + fixedObstacles.get(i).getRadius() + mTable.getObstacleManager().getRobotRadius()) * this.getHeight() / 2000 + this.getHeight(), (2 * (fixedObstacles.get(i).getRadius() + mTable.getObstacleManager().getRobotRadius())) * this.getWidth() / 3000, (2 * (fixedObstacles.get(i).getRadius() + mTable.getObstacleManager().getRobotRadius())) * this.getHeight() / 2000);
	    }
	    
	    //les robots ennemis
	    g.setColor(Color.red);
	    ArrayList<ObstacleProximity> ennemyRobots = mTable.getObstacleManager().getMobileObstacles();
	    for(int i = 0; i < ennemyRobots.size(); i++)
		    g.drawOval((ennemyRobots.get(i).getPosition().x - ennemyRobots.get(i).getRadius() + 1500) * this.getWidth() / 3000,
		    		-(ennemyRobots.get(i).getPosition().y + ennemyRobots.get(i).getRadius()) * this.getHeight() / 2000 + this.getHeight(),
		    		(2 * ennemyRobots.get(i).getRadius()) * this.getWidth() / 3000,
					(2 * ennemyRobots.get(i).getRadius()) * this.getHeight() / 2000);
	    
	    //les robots ennemis non confirmés
	    g.setColor(new Color(0, 100, 100));
	    ennemyRobots = mTable.getObstacleManager().getUntestedArrayList();
	    for(int i = 0; i < ennemyRobots.size(); i++)
		    g.drawOval((ennemyRobots.get(i).getPosition().x - ennemyRobots.get(i).getRadius() + 1500) * this.getWidth() / 3000,
		    		-(ennemyRobots.get(i).getPosition().y + ennemyRobots.get(i).getRadius()) * this.getHeight() / 2000 + this.getHeight(),
		    		(2 * ennemyRobots.get(i).getRadius()) * this.getWidth() / 3000,
					(2 * ennemyRobots.get(i).getRadius()) * this.getHeight() / 2000);
	    
		// Notre robot
	    if(isRobotPresent)
	    {
		    g.setColor(Color.green);
		    Vec2 position = mRobot.getPosition();
		    double orientation = mRobot.getOrientation();
		    g.drawOval( (position.x - 100 + 1500) * this.getWidth() / 3000,
		    		   -(position.y + 100) * this.getHeight() / 2000 + this.getHeight(), 
		    		    (2 * 100) * this.getWidth() / 3000,
		    		    (2 * 100) * this.getHeight() / 2000);
		    g.drawLine((position.x + 1500) * this.getWidth() / 3000, 
		    			-position.y * this.getHeight() / 2000 + this.getHeight(),
		    			(int)((position.x + 200*Math.cos(orientation) + 1500) * this.getWidth() / 3000),
		    			(int)(-(position.y + 200*Math.sin(orientation)) * this.getHeight() / 2000 + this.getHeight()));
	    }
	    
	    g.setColor(Color.green);
	    //debug : zones
	    if(showGraph)
	    	for(int i=0; i<mGraph.mAreas.size(); i++)
	    	{
	    		g.drawRect((mGraph.mAreas.get(i).x + 1500) * this.getWidth() / 3000,
	    				  -(mGraph.mAreas.get(i).y + mGraph.mAreas.get(i).height) * this.getHeight() / 2000 + this.getHeight(),
	    				   mGraph.mAreas.get(i).width * this.getWidth() / 3000,
	    				   mGraph.mAreas.get(i).height * this.getHeight() / 2000);
	    	}
	    
	    // un chemin
	    g.setColor(Color.blue);
	    for(int i = 0; i+1 < mPath.size(); i++)
	    {
	    	g.drawLine( (mPath.get(i).x + 1500) * this.getWidth() / 3000, 
	    			    -mPath.get(i).y * this.getHeight() / 2000 + this.getHeight(),
	    			    (mPath.get(i+1).x + 1500) * this.getWidth() / 3000,
	    			    -mPath.get(i+1).y * this.getHeight() / 2000 + this.getHeight() );
	    }
	    
	    // les points du chemin
	    g.setColor(Color.cyan);
	    for(int i = 0; i < mPath.size(); i++)
	    {
	    	g.fillOval( (mPath.get(i).x + 1500) * this.getWidth() / 3000 - 3,
	    			    -mPath.get(i).y * this.getHeight() / 2000 + this.getHeight() - 3,
	    			     6,
	    			     6);
	    }
	    
	    // les coordonnées des points du chemin
	    g.setColor(Color.magenta);
	    for(int i = 0; i < mPath.size(); i++)
	    {
	    	g.drawString(mPath.get(i).x + ", " + mPath.get(i).y, 
	    			    (mPath.get(i).x + 1500) * this.getWidth() / 3000, 
	    			    -mPath.get(i).y * this.getHeight() / 2000 + this.getHeight());
	    }
	    
	    g.setColor(Color.yellow);
	    g.drawOval( (mTable.getObstacleManager().getDiscPosition().x- mTable.getObstacleManager().getDiscRadius() + 1500) * this.getWidth() / 3000,
	    		   -(mTable.getObstacleManager().getDiscPosition().y + mTable.getObstacleManager().getDiscRadius()) * this.getHeight() / 2000 + this.getHeight(), 
	    		    (2 * mTable.getObstacleManager().getDiscRadius()) * this.getWidth() / 3000,
	    		    (2 * mTable.getObstacleManager().getDiscRadius()) * this.getHeight() / 2000);
	}
	
	//permet d'afficher un chemin
	public void drawArrayList(ArrayList<Vec2> path)
	{
		mPath = path;
		repaint();
	}
	
	//permet d'afficher le graphe du pathdingding
	public void drawGraph(Graph graph)
	{
		mGraph = graph;
		showGraph = true;
	}
	
	public Table getTable()
	{
		return mTable;
	}
}
