package PathFinding;
import javax.swing.JFrame;

/**
 * affichage
 * @author Etienne
 *
 */
public class Fenetre extends JFrame
{
	private Panel m_panel;
	
	public Fenetre(Table table)
	{
		this.setVisible(true);
		this.setTitle("table");
	    this.setSize(400, 600);
	    this.setLocationRelativeTo(null);
	    
	    m_panel = new Panel(table);
	    this.setContentPane(m_panel);
	    
	    addMouseListener(new Mouse(table, this));
	}
	
	public void rafraichir()
	{
		m_panel.repaint();
	}
	
	public Panel getPanel()
	{
		return m_panel;
	}
}
