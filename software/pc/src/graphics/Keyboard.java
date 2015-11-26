package graphics;

import enums.ActuatorOrder;
import enums.TurningStrategy;
import robot.RobotReal;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


public class Keyboard implements KeyListener
{
	//private RobotReal mRobot;
	private boolean modeActual = false;
	private TurningStrategy turningStr = TurningStrategy.FASTEST;
	
	
	public Keyboard(/*RobotReal robot*/)
	{
		//mRobot= robot;
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		if(e.getKeyCode() == KeyEvent.VK_Q)
		{
			turningStr = TurningStrategy.LEFT_ONLY;
		}
		if(e.getKeyCode() == KeyEvent.VK_Z)
		{
			
		}
		if(e.getKeyCode() == KeyEvent.VK_S)
		{
			turningStr = TurningStrategy.FASTEST;
		}
		if(e.getKeyCode() == KeyEvent.VK_D)
		{
			turningStr = TurningStrategy.RIGHT_ONLY;
		}
		if(e.getKeyCode() == KeyEvent.VK_A)
		{
			
		}
		if(e.getKeyCode() == KeyEvent.VK_K)
		{
		
		}
		if(e.getKeyCode() == KeyEvent.VK_R || e.getKeyCode() == KeyEvent.VK_UP)
		{
			
		}
		if(e.getKeyCode() == KeyEvent.VK_F || e.getKeyCode() == KeyEvent.VK_DOWN)
		{
			
		}
		if(e.getKeyCode() == KeyEvent.VK_G || e.getKeyCode() == KeyEvent.VK_RIGHT)
		{
			
		}
		if(e.getKeyCode() == KeyEvent.VK_D || e.getKeyCode() == KeyEvent.VK_LEFT)
		{
			
		}
		if(e.getKeyCode() == KeyEvent.VK_P)
		{
			
		}
		if(e.getKeyCode() == KeyEvent.VK_O)
		{
			modeActual = true;
		}
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		if(e.getKeyCode() == KeyEvent.VK_D
		|| e.getKeyCode() == KeyEvent.VK_R
		|| e.getKeyCode() == KeyEvent.VK_F
		|| e.getKeyCode() == KeyEvent.VK_G
		|| e.getKeyCode() == KeyEvent.VK_LEFT
		|| e.getKeyCode() == KeyEvent.VK_RIGHT
		|| e.getKeyCode() == KeyEvent.VK_UP
		|| e.getKeyCode() == KeyEvent.VK_DOWN
		)
		{
			try
			{
				//mRobot.useActuator(ActuatorOrder.STOP, false);
			}
			catch(Exception exception)
			{
				System.out.println("ça marche pas bien trololo");
			}
		}
	}

	public boolean isModeActual()
	{
		return modeActual;
	}

	public void resetModeActual()
	{
		modeActual = false;
	}

	public TurningStrategy getTurningStrategy()
	{
		return turningStr;
	}

	@Override
	public void keyTyped(KeyEvent e)
	{
	}

}
