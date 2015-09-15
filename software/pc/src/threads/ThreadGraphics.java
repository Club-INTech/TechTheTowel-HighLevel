//TODO : refactor

package threads;

import graphics.Window;
import robot.RobotReal;
import table.Table;

public class ThreadGraphics extends AbstractThread
{

	/** La table */
	private Table mTable;
	
	/** Le robot */
	private RobotReal mRobot;
	
	/** interface graphique */
	public Window window;
	
	public ThreadGraphics(Table table, RobotReal robot)
	{
		super(config, log);
		Thread.currentThread().setPriority(3);
		mTable = table;
		mRobot = robot;
		
		// DEBUG: interface graphique
		try
		{
			window = new Window(table, robot);
		}
		catch (Exception e)
		{
			log.debug("Affichage graphique non disponible");
		}
	}
	
	/* (non-Javadoc)
	 * @see threads.AbstractThread#run()
	 */
	@Override
	public void run()
	{
		log.debug("Lancement du thread de capteurs");
		updateConfig();
		
		while(true)
		{
			window.getPanel().repaint();
		}
	}
}
