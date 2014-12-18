package graphics;

import java.awt.Graphics;

import javax.swing.JPanel;

import java.awt.Color; 
import java.util.ArrayList;

import table.Table;
import smartMath.Vec2;

import table.obstacles.*;

/**
 * panneau sur lequel est dessine la table
 * @author Etienne
 *
 */
//TODO: Ce genre de classe n'a rien a voir avec le code de match du robot. Ce n'est pas un gros inconvéniant, mais que ca n'empèche pas de documenter le code et de statuer clairement a un endroit bien visible que ce code ne sert qu'au debug
public class Panel extends JPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 856331824502887046L;
	
	private ArrayList<Vec2> m_path = new ArrayList<Vec2>();
	private Table mTable;
	
	public Panel(Table table)
	{
		mTable = table;
	}
	
	public void paintComponent(Graphics g)
	{
		g.setColor(Color.white);
	    g.fillRect(0, 0, this.getWidth(), this.getHeight());
	    
	    g.setColor(Color.black);
	    
	    ArrayList<ObstacleRectangular> rects = mTable.getObstacleManager().getRects();
	    for(int i = 0; i < rects.size(); i++)
	    {
	    	g.drawRect((rects.get(i).getPosition().x - (rects.get(i).getSizeX() / 2) + 1500) * this.getWidth() / 3000, -(rects.get(i).getPosition().y + rects.get(i).getSizeY()) * this.getHeight() / 2000 + this.getHeight(), rects.get(i).getSizeX() * this.getWidth() / 3000, rects.get(i).getSizeY() * this.getHeight() / 2000);
	    }
	    
	    g.drawRect(0, 0, this.getWidth(), this.getHeight());
	    
	    g.setColor(Color.blue);
	    for(int i = 0; i+1 < m_path.size(); i++)
	    {
	    	g.drawLine((m_path.get(i).x + 1500) * this.getWidth() / 3000, -m_path.get(i).y * this.getHeight() / 2000 + this.getHeight(), (m_path.get(i+1).x + 1500) * this.getWidth() / 3000, -m_path.get(i+1).y * this.getHeight() / 2000 + this.getHeight());
	    }
	    g.setColor(Color.red);
	    for(int i = 0; i < m_path.size(); i++)
	    {
	    	g.drawString(m_path.get(i).x + ", " + m_path.get(i).y, (m_path.get(i).x + 1500) * this.getWidth() / 3000, -m_path.get(i).y * this.getHeight() / 2000 + this.getHeight());
	    }
	}
	
	public void drawArrayList(ArrayList<Vec2> path)
	{
		
		m_path = path;
		repaint();
	}
}
