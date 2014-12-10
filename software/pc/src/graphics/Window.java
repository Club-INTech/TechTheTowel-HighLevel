package graphics;

import javax.swing.JFrame;

import table.Table;
import smartMath.Vec2;

/**
 * interface graphique de debugage
 * @author Etienne
 *
 */
public class Window extends JFrame
{
	private Panel m_panel;
	
	public Window()
	{
		this.setVisible(true);
		this.setTitle("table");
	    this.setSize(600, 400);
	    this.setLocationRelativeTo(null);
	    
	    m_panel = new Panel();
	    this.setContentPane(m_panel);
	    
	    addMouseListener(new Mouse(this));
	}
	
	/**
	 * 
	 * @return le panneau
	 */
	public Panel getPanel()
	{
		return m_panel;
	}
}
