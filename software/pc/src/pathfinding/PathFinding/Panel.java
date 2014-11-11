package PathFinding;

import java.awt.Graphics;

import javax.swing.JPanel;

import java.awt.Color; 
import java.util.ArrayList;

/**
 * affichage
 * @author Etienne
 *
 */
public class Panel extends JPanel
{
	private Table m_table;
	
	public Panel(Table table)
	{
		m_table = table;
	}
	
	public void paintComponent(Graphics g)
	{
		g.setColor(Color.white);
	    g.fillRect(0, 0, this.getWidth(), this.getHeight());
	    
	    g.setColor(Color.black);
	    g.drawRect(0, 0, this.getWidth(), this.getHeight());
	    
	    ArrayList<ObstableSegm> lignes = m_table.getLignes();
	    g.setColor(Color.red);
	    for(int i = 0 ; i < lignes.size() ; i++)
	    {
	    	g.drawLine((int)(lignes.get(i).getA().getX() * this.getWidth() / 2000),
	    			   (int)(lignes.get(i).getA().getY() * this.getHeight() / 3000),
	    			   (int)(lignes.get(i).getB().getX() * this.getWidth() / 2000),
	    			   (int)(lignes.get(i).getB().getY() * this.getHeight() / 3000));
	    }
	    
	    Robot robot = m_table.getRobot();
	    g.setColor(Color.blue);
	    g.fillOval((int)((robot.getPosition().getX() - 25) * this.getWidth() / 2000),
	    			(int)((robot.getPosition().getY() - 25) * this.getHeight() / 3000),
	    			(int)(50 * this.getWidth() / 2000),
	    			(int)(50 * this.getHeight() / 3000));
	    g.setColor(Color.green);
	    g.fillOval(((int)(robot.getDestination().getX() - 25) * this.getWidth() / 2000),
    			(int)((robot.getDestination().getY() - 25) * this.getHeight() / 3000),
    			(int)(50 * this.getWidth() / 2000),
    			(int)(50 * this.getHeight() / 3000));
	    long start = System.nanoTime();
	    Path path = PathFinder.findPath(m_table.getRobot().getPosition(), m_table.getRobot().getDestination(), m_table);
	    long end = System.nanoTime();
	    System.out.println("Elapsed: "+(end-start)+"ns");
	    for(int i = 1 ; i < 
	    		path.size() ; i++)
	    {
	    	g.setColor(Color.orange);
	    	g.drawLine((int)(path.getPosition(i-1).getX() * this.getWidth() / 2000),
	    			   (int)(path.getPosition(i-1).getY() * this.getHeight() / 3000),
	    			   (int)(path.getPosition(i).getX() * this.getWidth() / 2000),
	    			   (int)(path.getPosition(i).getY() * this.getHeight() / 3000));
	    }
	    
	}
}
