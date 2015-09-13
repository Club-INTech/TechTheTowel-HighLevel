//TODO : refactor

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
	
	
	private TablePanel mPanel;
	private SensorPanel mSensorPanel;
	private Mouse mMouse;
	private Keyboard mKeyboard;
	
	public Window(Table table, RobotReal robot)
	{
		this.setVisible(true);
		this.setTitle("table");
	    this.setSize(600, 400);
	    this.setLocationRelativeTo(null);
	    
	    mPanel = new TablePanel(table, robot);
	    this.setContentPane(mPanel);
	    
	    mMouse = new Mouse(mPanel);
	    addMouseListener(mMouse);
	    
	    mKeyboard = new Keyboard(robot);
	    addKeyListener(mKeyboard);
	}
	
	public Window(Table table)
	{
		this.setVisible(true);
		this.setTitle("table");
	    this.setSize(600, 400);
	    this.setLocationRelativeTo(null);
	    
	    mPanel = new TablePanel(table);
	    this.setContentPane(mPanel);
	    
	    mMouse = new Mouse(mPanel);
	    addMouseListener(mMouse);
	    
	    mMouse = new Mouse(mPanel);
	    addMouseListener(mMouse);
	}
	
	public Window()
	{
		this.setVisible(true);
		this.setTitle("sensorValues");
	    this.setSize(1200, 800);
	    this.setLocationRelativeTo(null);
	    
	    mSensorPanel = new SensorPanel();
	    this.setContentPane(mSensorPanel);
	}
	
	/**
	 * 
	 * @return le panneau
	 */
	public TablePanel getPanel()
	{
		return mPanel;
	}
	
	public void drawInt(int value1, int value2, int value3, int value4)
	{
		mSensorPanel.drawInteger(new Integer(value1), new Integer(value2), new Integer(value3), new Integer(value4));
	}
	
	public Mouse getMouse()
	{
		return mMouse;
	}
}
