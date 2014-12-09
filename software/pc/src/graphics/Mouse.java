package graphics;

import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;

import table.Table;

/**
 * gestion de la souris
 * @author Etienne
 *
 */
public class Mouse implements MouseListener
{
	private Window m_fen;
	
	public Mouse(Window fen)
	{
		m_fen = fen;
	}
	
    @Override
    public void mousePressed(MouseEvent e)
    {
        if (e.getButton()==MouseEvent.BUTTON1)
        {
        	m_fen.refresh();
        }
        if (e.getButton()==MouseEvent.BUTTON2)
        {
        	m_fen.refresh();
        }
        if (e.getButton()==MouseEvent.BUTTON3)
        {
        	m_fen.refresh();
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
}
