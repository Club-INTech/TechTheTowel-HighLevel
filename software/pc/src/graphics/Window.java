package graphics;

import javax.swing.JFrame;

import table.Table;
import graphics.Mouse;
import robot.RobotReal;

/**
 * interface graphique de debugage
 * @author Etienne
 *
 */
public class Window extends JFrame
{
	/** numéro de serialisation	 */
	private static final long serialVersionUID = -3140220993568124763L;
	
	
	private Panel mPanel;
	private Mouse mMouse;
	
	public Window(Table table, RobotReal robot)
	{
		this.setVisible(true);
		this.setTitle("table");
	    this.setSize(600, 400);
	    this.setLocationRelativeTo(null);
	    
	    mPanel = new Panel(table, robot);
	    this.setContentPane(mPanel);
	    
	    mMouse = new Mouse(mPanel);
	    addMouseListener(mMouse);
	}
	
	public Window(Table table)
	{
		this.setVisible(true);
		this.setTitle("table");
	    this.setSize(600, 400);
	    this.setLocationRelativeTo(null);
	    
	    mPanel = new Panel(table);
	    this.setContentPane(mPanel);
	    
	    mMouse = new Mouse(mPanel);
	    addMouseListener(mMouse);
	}
	
	/**
	 * 
	 * @return le panneau
	 */
	public Panel getPanel()
	{
		return mPanel;
	}
	
	public Mouse getMouse()
	{
		return mMouse;
	}
}
