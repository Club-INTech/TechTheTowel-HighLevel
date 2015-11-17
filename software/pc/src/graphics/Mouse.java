package graphics;

import smartMath.Vec2;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * gestion de la souris
 * @author Etienne
 *
 */
public class Mouse implements MouseListener
{
	private Vec2 mRightClickPosition;
	private Vec2 mMiddleClickPosition;
	private Vec2 mLeftClickPosition;
	private boolean mHasClicked;
	private TablePanel mPanel;
	
	public Mouse(TablePanel pan)
	{
		mPanel = pan;
		mHasClicked = false;
		mRightClickPosition = new Vec2(0, 0);
		mMiddleClickPosition = new Vec2(0, 0);
		mLeftClickPosition = new Vec2(0, 0);
	}
	
    @Override
    public void mousePressed(MouseEvent e)
    {
    	mHasClicked = true;
        if (e.getButton()==MouseEvent.BUTTON1)
        {
        	mLeftClickPosition.x = (e.getX()/* - 8*/) * 3000 / mPanel.getWidth() - 1500; // mettre 0 au lieu de 8 sous linux
        	mLeftClickPosition.y = (-e.getY()/* + 31*/) * 2000 / mPanel.getHeight() + 2000; // mettre 0 au lieu de 31 sous windows
        }
        if (e.getButton()==MouseEvent.BUTTON2)
        {
        	mMiddleClickPosition.x = (e.getX()/* - 8*/) * 3000 / mPanel.getWidth() - 1500; // mettre 0 au lieu de 8 sous linux
        	mMiddleClickPosition.y = (-e.getY()/* + 31*/) * 2000 / mPanel.getHeight() + 2000; // mettre 0 au lieu de 31 sous windows
        }
        if (e.getButton()==MouseEvent.BUTTON3)
        {
        	mRightClickPosition.x = (e.getX()/* - 8*/) * 3000 / mPanel.getWidth() - 1500; // mettre 0 au lieu de 8 sous linux
        	mRightClickPosition.y = (-e.getY()/* + 31*/) * 2000 / mPanel.getHeight() + 2000; // mettre 0 au lieu de 31 sous windows
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}
    
	public Vec2 getRightClickPosition()
	{
		return mRightClickPosition;
	}
	
	public Vec2 getMiddleClickPosition()
	{
		return mMiddleClickPosition;
	}
	
	public Vec2 getLeftClickPosition()
	{
		return mLeftClickPosition;
	}
	
	public boolean hasClicked()
	{
		if(mHasClicked)
		{
			mHasClicked = false;
			return true;
		}
		return false;
	}
}
