package graphics;

import java.awt.Graphics;

import javax.swing.JPanel;

import java.awt.Color; 
import java.util.ArrayList;

import table.Table;
import smartMath.Vec2;

/**
 * affichage
 * @author Etienne
 *
 */
public class Panel extends JPanel
{
	ArrayList<Vec2> m_path = new ArrayList<Vec2>();
	
	public Panel()
	{
	}
	
	public void paintComponent(Graphics g)
	{
		g.setColor(Color.gray);
	    g.fillRect(0, 0, this.getWidth(), this.getHeight());
	    
	    g.setColor(Color.orange);
	    for(int i = 0; i+1 < m_path.size(); i++)
	    {
	    	g.drawLine(m_path.get(i).x, m_path.get(i).y, m_path.get(i+1).x, m_path.get(i+1).y);
	    }
	}
	
	public void drawArrayList(ArrayList<Vec2> path)
	{
		m_path = path;
		repaint();
	}
}
