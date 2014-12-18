package graphics;

import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;


/**
 * gestion de la souris
 * @author Etienne
 *
 */
// Ce genre de classe n'a rien a voir avec le code de match du robot. Ce n'est pas un gros inconvéniant, mais que ca n'empèche pas de documenter le code et de statuer clairement a un endroit bien visible que ce code ne sert qu'au debug
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
        	m_fen.getPanel().repaint();
        }
        if (e.getButton()==MouseEvent.BUTTON2)
        {
        	m_fen.getPanel().repaint();
        }
        if (e.getButton()==MouseEvent.BUTTON3)
        {
        	m_fen.getPanel().repaint();
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
