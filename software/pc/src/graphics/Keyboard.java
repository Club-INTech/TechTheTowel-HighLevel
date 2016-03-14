package graphics;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import enums.ActuatorOrder;
import enums.TurningStrategy;
import robot.RobotReal;


public class Keyboard implements KeyListener
{
	private RobotReal mRobot;
	private TurningStrategy turningStr = TurningStrategy.FASTEST;
	private boolean modeActual = false;
	
	public Keyboard(RobotReal robot)
	{
		mRobot= robot;
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
			//Bras
		}
		if(e.getKeyCode() == KeyEvent.VK_S)
		{
			turningStr = TurningStrategy.FASTEST;
		}
		if(e.getKeyCode() == KeyEvent.VK_I)
		{
			//Clap
		}
		if(e.getKeyCode() == KeyEvent.VK_A)
		{
            turningStr = TurningStrategy.RIGHT_ONLY;
		}
		if(e.getKeyCode() == KeyEvent.VK_K)
		{
			//Carpet
		}
		if(e.getKeyCode() == KeyEvent.VK_R || e.getKeyCode() == KeyEvent.VK_UP)
		{
			try
			{
				mRobot.useActuator(ActuatorOrder.MOVE_FORWARD, false);
			}
			catch(Exception exception)
			{
				System.out.println("ça marche pas bien trololo");
			}
		}
		if(e.getKeyCode() == KeyEvent.VK_F || e.getKeyCode() == KeyEvent.VK_DOWN)
		{
			try
			{
				mRobot.useActuator(ActuatorOrder.MOVE_BACKWARD, false);
			}
			catch(Exception exception)
			{
				System.out.println("ça marche pas bien trololo");
			}
		}
		if(e.getKeyCode() == KeyEvent.VK_G || e.getKeyCode() == KeyEvent.VK_RIGHT)
		{
			try
			{
				mRobot.useActuator(ActuatorOrder.TURN_RIGHT, false);
			}
			catch(Exception exception)
			{
				System.out.println("ça marche pas bien trololo");
			}
		}
		if(e.getKeyCode() == KeyEvent.VK_D || e.getKeyCode() == KeyEvent.VK_LEFT)
		{
			try
			{
				mRobot.useActuator(ActuatorOrder.TURN_LEFT, false);
			}
			catch(Exception exception)
			{
				System.out.println("ça marche pas bien trololo");
			}
		}
		if(e.getKeyCode() == KeyEvent.VK_P)
		{
			//Poweroff
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
				mRobot.useActuator(ActuatorOrder.STOP, false);
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
