package graphics;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import enums.ActuatorOrder;
import robot.RobotReal;


public class Keyboard implements KeyListener
{
	private RobotReal mRobot;
	private boolean mIsRightArmOpen = false;
	private boolean mIsLeftArmOpen = false;
	private boolean mIsRightClapMiddle = false;
	private boolean mIsRightClapGoingDown = false;
	private boolean mIsLeftClapMiddle = false;
	private boolean mIsLeftClapGoingDown = false;
	private boolean mIsRightCarpetOpen = false;
	private boolean mIsLeftCarpetOpen = false;
	private boolean mIsMovingForward = false;
	private boolean mIsMovingBackward = false;
	private boolean mIsTurningRight = false;
	private boolean mIsTurningLeft = false;
	
	
	public Keyboard(RobotReal robot)
	{
		mRobot= robot;
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		if(e.getKeyCode() == KeyEvent.VK_Q)
		{
			try
			{
				if(!mIsRightArmOpen && !mIsLeftArmOpen)
				{
					mRobot.useActuator(ActuatorOrder.ARM_RIGHT_OPEN, false);
					mIsRightArmOpen = true;
				}
				else
				{
					mRobot.useActuator(ActuatorOrder.ARM_RIGHT_CLOSE_SLOW, false);
					mIsRightArmOpen = false;
				}
			}
			catch(Exception exception)
			{
				System.out.println("ça marche pas bien trololo");
			}
		}
		if(e.getKeyCode() == KeyEvent.VK_Z)
		{
			try
			{
				if(!mIsLeftArmOpen && !mIsRightArmOpen)
				{
					mRobot.useActuator(ActuatorOrder.ARM_LEFT_OPEN, false);
					mIsLeftArmOpen = true;
				}
				else
				{
					mRobot.useActuator(ActuatorOrder.ARM_LEFT_CLOSE_SLOW, false);
					mIsLeftArmOpen = false;
				}
			}
			catch(Exception exception)
			{
				System.out.println("ça marche pas bien trololo");
			}
		}
		if(e.getKeyCode() == KeyEvent.VK_S)
		{
			try
			{
				if(mIsRightClapMiddle)
				{
					if(mIsRightClapGoingDown)
						mRobot.useActuator(ActuatorOrder.LOW_RIGHT_CLAP, false);
					else
						mRobot.useActuator(ActuatorOrder.HIGH_RIGHT_CLAP, false);
					mIsRightClapGoingDown = !mIsRightClapGoingDown;
				}
				else
					mRobot.useActuator(ActuatorOrder.MID_RIGHT_CLAP, false);
				mIsRightClapMiddle = !mIsRightClapMiddle;
			}
			catch(Exception exception)
			{
				System.out.println("ça marche pas bien trololo");
			}
		}
		if(e.getKeyCode() == KeyEvent.VK_I)
		{
			try
			{
				if(mIsLeftClapMiddle)
				{
					if(mIsLeftClapGoingDown)
						mRobot.useActuator(ActuatorOrder.LOW_LEFT_CLAP, false);
					else
						mRobot.useActuator(ActuatorOrder.HIGH_LEFT_CLAP, false);
					mIsLeftClapGoingDown = !mIsLeftClapGoingDown;
				}
				else
					mRobot.useActuator(ActuatorOrder.MID_LEFT_CLAP, false);
				mIsLeftClapMiddle = !mIsLeftClapMiddle;
			}
			catch(Exception exception)
			{
				System.out.println("ça marche pas bien trololo");
			}
		}
		if(e.getKeyCode() == KeyEvent.VK_A)
		{
			try
			{
				if(!mIsRightCarpetOpen)
					mRobot.useActuator(ActuatorOrder.RIGHT_CARPET_DROP, false);
				else
					mRobot.useActuator(ActuatorOrder.RIGHT_CARPET_FOLDUP, false);
				mIsRightCarpetOpen = !mIsRightCarpetOpen;
			}
			catch(Exception exception)
			{
				System.out.println("ça marche pas bien trololo");
			}
		}
		if(e.getKeyCode() == KeyEvent.VK_K)
		{
			try
			{
				if(!mIsLeftCarpetOpen)
					mRobot.useActuator(ActuatorOrder.LEFT_CARPET_DROP, false);
				else
					mRobot.useActuator(ActuatorOrder.LEFT_CARPET_FOLDUP, false);
				mIsLeftCarpetOpen = !mIsLeftCarpetOpen;
			}
			catch(Exception exception)
			{
				System.out.println("ça marche pas bien trololo");
			}
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
			try
			{
				mRobot.useActuator(ActuatorOrder.POWEROFF, false);
			}
			catch(Exception exception)
			{
				System.out.println("ça marche pas bien trololo");
			}
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

	@Override
	public void keyTyped(KeyEvent e)
	{
	}

}
