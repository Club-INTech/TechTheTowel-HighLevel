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
public class Panel extends JPanel
{	
	/** numéro pour la serialisation	 */
	private static final long serialVersionUID = -3033815690221481964L;
	
	private ArrayList<Vec2> mPath;
	private Table mTable;
	private Robot mRobot;
	private boolean showGraph;
	private Graph mGraph;
	
	
	public Panel(Table table/*, RobotReal robot*/)
	{
		mPath = new ArrayList<Vec2>();
		mTable = table;
		//mRobot = robot;
		showGraph = false;
	}
	
	public void paintComponent(Graphics g)
	{
		g.setColor(Color.black);
	    g.fillRect(0, 0, this.getWidth(), this.getHeight());
	    
	    g.setColor(Color.darkGray);
	    
	    ArrayList<Segment> lines = mTable.getObstacleManager().getLines();
	    for(int i = 0; i < lines.size(); i++)
	    {
	    	g.drawLine((int)((lines.get(i).getA().x + 1500) * this.getWidth() / 3000), (int)((-lines.get(i).getA().y) * this.getHeight() / 2000 + this.getHeight()), (int)((lines.get(i).getB().x + 1500) * this.getWidth() / 3000), (int)((-lines.get(i).getB().y) * this.getHeight() / 2000 + this.getHeight()));
	    }
	    
	    g.setColor(Color.white);
	    
	    ArrayList<ObstacleRectangular> rects = mTable.getObstacleManager().getRectangles();
	    for(int i = 0; i < rects.size(); i++)
	    {
	    	g.fillRect((rects.get(i).getPosition().x - (rects.get(i).getSizeX() / 2) + 1500) * this.getWidth() / 3000, -(rects.get(i).getPosition().y + rects.get(i).getSizeY()) * this.getHeight() / 2000 + this.getHeight(), rects.get(i).getSizeX() * this.getWidth() / 3000, rects.get(i).getSizeY() * this.getHeight() / 2000);
	    }
	    
	    g.setColor(new Color(100, 60, 5));
	    if(showGraph)
	    {
	    	//parcours des noeuds
	    	for(int i = 0; i < mGraph.getNodes().size(); i++)
	    		//parcours des liens de chaque noeud
	    		for(int j = 0; j < mGraph.getNodes().get(i).getLinkNumber(); j++)
	    			g.drawLine((mGraph.getNodes().get(i).x + 1500) * this.getWidth() / 3000, -mGraph.getNodes().get(i).y * this.getHeight() / 2000 + this.getHeight(), (mGraph.getNodes().get(i).getLink(j).getDestination().x + 1500) * this.getWidth() / 3000, -mGraph.getNodes().get(i).getLink(j).getDestination().y * this.getHeight() / 2000 + this.getHeight());
	    }
	    
	    g.setColor(Color.white);
	    
	    ArrayList<ObstacleCircular> mobileObstacles = mTable.getObstacleManager().getFixedObstacles();
	    for(int i = 0; i < mobileObstacles.size(); i++)
	    {
	    	g.drawOval((mobileObstacles.get(i).getPosition().x - mobileObstacles.get(i).getRadius() + 1500) * this.getWidth() / 3000, -(mobileObstacles.get(i).getPosition().y + mobileObstacles.get(i).getRadius()) * this.getHeight() / 2000 + this.getHeight(), (2 * mobileObstacles.get(i).getRadius()) * this.getWidth() / 3000, (2 * mobileObstacles.get(i).getRadius()) * this.getHeight() / 2000);
	    }
	    
	    //le premier robot ennemi
	    g.setColor(Color.red);
	    ArrayList<ObstacleProximity> plots = mTable.getObstacleManager().getMobileObstacles();
	    for(int i = 0; i < mTable.getObstacleManager().getMobileObstacles().size(); i++)
		    g.drawOval((mTable.getObstacleManager().getMobileObstacles().get(i).getPosition().x - mTable.getObstacleManager().getMobileObstacles().get(i).getRadius() + 1500) * this.getWidth() / 3000,
		    		-(mTable.getObstacleManager().getMobileObstacles().get(i).getPosition().y + mTable.getObstacleManager().getMobileObstacles().get(i).getRadius()) * this.getHeight() / 2000 + this.getHeight(),
		    		(2 * mTable.getObstacleManager().getMobileObstacles().get(i).getRadius()) * this.getWidth() / 3000,
		    		(2 * mTable.getObstacleManager().getMobileObstacles().get(i).getRadius()) * this.getHeight() / 2000);
	    
	    g.setColor(Color.green);
	    //g.drawOval((mRobot.getPosition().x - 100 + 1500) * this.getWidth() / 3000, -(mRobot.getPosition().y + 100) * this.getHeight() / 2000 + this.getHeight(), (2 * 100) * this.getWidth() / 3000, (2 * 100) * this.getHeight() / 2000);
	    //System.out.println("Graphique : position du robot : ("+mRobot.getPosition().x+", "+mRobot.getPosition().y+")");
	    
	    //debug : zones
	    for(int i=0; i<mGraph.mAreas.size(); i++)
	    	g.drawRect((mGraph.mAreas.get(i).x + 1500) * this.getWidth() / 3000, -(mGraph.mAreas.get(i).y + mGraph.mAreas.get(i).height) * this.getHeight() / 2000 + this.getHeight(), mGraph.mAreas.get(i).width * this.getWidth() / 3000, mGraph.mAreas.get(i).height * this.getHeight() / 2000);
	    
	    g.setColor(Color.blue);
	    for(int i = 0; i+1 < mPath.size(); i++)
	    {
	    	g.drawLine((mPath.get(i).x + 1500) * this.getWidth() / 3000, -mPath.get(i).y * this.getHeight() / 2000 + this.getHeight(), (mPath.get(i+1).x + 1500) * this.getWidth() / 3000, -mPath.get(i+1).y * this.getHeight() / 2000 + this.getHeight());
	    }
	    
	    g.setColor(Color.cyan);
	    for(int i = 0; i < mPath.size(); i++)
	    {
	    	g.fillOval((mPath.get(i).x + 1500) * this.getWidth() / 3000 - 3, -mPath.get(i).y * this.getHeight() / 2000 + this.getHeight() - 3, 6, 6);
	    }
	    
	    g.setColor(Color.magenta);
	    for(int i = 0; i < mPath.size(); i++)
	    {
	    	g.drawString(mPath.get(i).x + ", " + mPath.get(i).y, (mPath.get(i).x + 1500) * this.getWidth() / 3000, -mPath.get(i).y * this.getHeight() / 2000 + this.getHeight());
	    }
	}
	
	public void drawArrayList(ArrayList<Vec2> path)
	{
		mPath = path;
		repaint();
	}
	
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
