package graphics;

import javax.swing.JFrame;

import container.Container;
import enums.ServiceNames;
import table.Table;
import smartMath.Vec2;
import graphics.Mouse;

/**
 * interface graphique de debugage
 * @author Etienne
 *
 */
public class Window extends JFrame
{
	private Panel mPanel;
	private Mouse mMouse;
	
	public Window(Table table) throws Exception
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
