package PathFinding;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;

/**
 * gestion de la souris
 * @author Etienne
 *
 */
public class Mouse implements MouseListener
{
	private Table m_table;
	private Fenetre m_fen;
	
	public Mouse(Table table, Fenetre fen)
	{
		m_table = table;
		m_fen = fen;
	}
	
    @Override
    public void mouseClicked(MouseEvent e)
    {
        if (e.getButton()==MouseEvent.BUTTON1)
        {
        	m_table.getRobot().setPosition(new Node((e.getX()) * 2000 / m_fen.getPanel().getWidth(), (e.getY()) * 3000 / m_fen.getPanel().getHeight()));
        	m_fen.rafraichir();
        }
        if (e.getButton()==MouseEvent.BUTTON3)
        {
        	m_table.getRobot().setDestination(new Node((e.getX()) * 2000 / m_fen.getPanel().getWidth(), (e.getY()) * 3000 / m_fen.getPanel().getHeight()));
        	m_fen.rafraichir();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}
}
