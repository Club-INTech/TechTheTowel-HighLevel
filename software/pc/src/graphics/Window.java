package graphics;

import javax.swing.JFrame;

import table.Table;

/**
 * interface graphique de debugage
 * @author Etienne
 *
 */
//TODO: Ce genre de classe n'a rien a voir avec le code de match du robot. Ce n'est pas un gros inconvéniant, mais que ca n'empèche pas de documenter le code et de statuer clairement a un endroit bien visible que ce code ne sert qu'au debug et pas au match
public class Window extends JFrame
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1790274611904785158L;
	private Panel m_panel;
	
	public Window(Table table) throws Exception
	{
		this.setVisible(true);
		this.setTitle("table");
	    this.setSize(600, 400);
	    this.setLocationRelativeTo(null);
	    
	    m_panel = new Panel(table);
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
