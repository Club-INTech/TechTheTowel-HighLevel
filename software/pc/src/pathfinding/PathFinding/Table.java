package PathFinding;

import java.util.ArrayList;

/**
 * la table, contenant le robot, et les diff�rents obstacles
 * @author Etienne
 *
 */
public class Table
{
	private ArrayList<ObstableSegm> m_lignes;
	private Robot m_robot;

	public Table()
	{
		m_lignes = new ArrayList<ObstableSegm>();
		
		//1 + nodes
		m_lignes.add(new ObstableSegm(new Node(778, 0), new Node(778, 400), 1, new Node(778, 405), new Node(0, 0)));
		m_lignes.add(new ObstableSegm(new Node(778, 400), new Node(800, 400), 2, new Node(778, 405), new Node(800, 405)));
		m_lignes.add(new ObstableSegm(new Node(800, 400), new Node(800, 0), 1, new Node(800, 405), new Node(0, 0)));
		
		//2 + nodes
 		m_lignes.add(new ObstableSegm(new Node(800, 0), new Node(800, 70), 1, new Node(800, 405), new Node(0, 0)));
		m_lignes.add(new ObstableSegm(new Node(800, 70), new Node(1200, 70), 2, new Node(800, 405), new Node(1200, 405)));
		m_lignes.add(new ObstableSegm(new Node(1200, 70), new Node(1200, 0), 1, new Node(1200, 405), new Node(0, 0)));
		
		//3 + nodes
		m_lignes.add(new ObstableSegm(new Node(1200, 0), new Node(1200, 400), 1, new Node(1200, 405), new Node(0, 0)));
		m_lignes.add(new ObstableSegm(new Node(1200, 400), new Node(1222, 400), 2, new Node(1200, 405), new Node(1222, 405)));
		m_lignes.add(new ObstableSegm(new Node(1222, 400), new Node(1222, 0), 1, new Node(1222, 405), new Node(0, 0)));
		
		//4 + nodes
		m_lignes.add(new ObstableSegm(new Node(0, 335), new Node(70, 335), 1, new Node(75, 335), new Node(0, 0)));
		m_lignes.add(new ObstableSegm(new Node(70, 335), new Node(70, 265), 2, new Node(75, 335), new Node(75, 265)));
		m_lignes.add(new ObstableSegm(new Node(70, 265), new Node(0, 265), 1, new Node(75, 265), new Node(0, 0)));
		
		//5 + nodes
		m_lignes.add(new ObstableSegm(new Node(0, 635), new Node(70, 635), 1, new Node(75, 635), new Node(0, 0)));
		m_lignes.add(new ObstableSegm(new Node(70, 635), new Node(70, 565), 2, new Node(75, 635), new Node(75, 565)));
		m_lignes.add(new ObstableSegm(new Node(70, 565), new Node(0, 565), 1, new Node(75, 565), new Node(0, 0)));
		
		//6 + nodes
		m_lignes.add(new ObstableSegm(new Node(0, 2033), new Node(580, 2033), 1, new Node(585, 2038), new Node(0, 0)));
		m_lignes.add(new ObstableSegm(new Node(580, 2033), new Node(580, 967), 2, new Node(585, 2038), new Node(585, 962)));
		m_lignes.add(new ObstableSegm(new Node(580, 967), new Node(0, 967), 1, new Node(585, 962), new Node(0, 0)));
		
		//7 + nodes
		m_lignes.add(new ObstableSegm(new Node(2000, 1200), new Node(1900, 1200), 1, new Node(1895, 1200), new Node(0, 0)));
		m_lignes.add(new ObstableSegm(new Node(1900, 1200), new Node(1900, 1800), 2, new Node(1895, 1200), new Node(1895, 1800)));
		m_lignes.add(new ObstableSegm(new Node(1900, 1800), new Node(2000, 1800), 1, new Node(1895, 1800), new Node(0, 0)));
		
		//8 + nodes
		m_lignes.add(new ObstableSegm(new Node(0, 2435), new Node(70, 2435), 1, new Node(75, 2435), new Node(0, 0)));
		m_lignes.add(new ObstableSegm(new Node(70, 2435), new Node(70, 2365), 2, new Node(75, 2435), new Node(75, 2365)));
		m_lignes.add(new ObstableSegm(new Node(70, 2365), new Node(0, 2365), 1, new Node(75, 2365), new Node(0, 0)));
		
		//9 + nodes
		m_lignes.add(new ObstableSegm(new Node(0, 2735), new Node(70, 2735), 1, new Node(75, 2735), new Node(0, 0)));
		m_lignes.add(new ObstableSegm(new Node(70, 2735), new Node(70, 2665), 2, new Node(75, 2735), new Node(75, 2665)));
		m_lignes.add(new ObstableSegm(new Node(70, 2665), new Node(0, 2665), 1, new Node(75, 2665), new Node(0, 0)));
		
		//10 + nodes
		m_lignes.add(new ObstableSegm(new Node(778, 3000), new Node(778, 2600), 1, new Node(778, 2595), new Node(0, 0)));
		m_lignes.add(new ObstableSegm(new Node(778, 2600), new Node(800, 2600), 2, new Node(778, 2595), new Node(800, 2595)));
		m_lignes.add(new ObstableSegm(new Node(800, 2600), new Node(800, 3000), 1, new Node(800, 2595), new Node(0, 0)));
				
		//11 + nodes
		m_lignes.add(new ObstableSegm(new Node(800, 3000), new Node(800, 2930), 1, new Node(800, 2595), new Node(0, 0)));
		m_lignes.add(new ObstableSegm(new Node(800, 2930), new Node(1200, 2930), 2, new Node(800, 2595), new Node(1200, 2595)));
		m_lignes.add(new ObstableSegm(new Node(1200, 2930), new Node(1200, 3000), 1, new Node(1200, 2595), new Node(0, 0)));
				
		//12 + nodes
		m_lignes.add(new ObstableSegm(new Node(1200, 3000), new Node(1200, 2600), 1, new Node(1200, 2595), new Node(0, 0)));
		m_lignes.add(new ObstableSegm(new Node(1200, 2600), new Node(1222, 2600), 2, new Node(1200, 2595), new Node(1222, 2595)));
		m_lignes.add(new ObstableSegm(new Node(1222, 2600), new Node(1222, 3000), 1, new Node(1222, 2595), new Node(0, 0)));
		
		m_robot = new Robot(new Node(1000, 300), 200);
	}
	
	public ArrayList<ObstableSegm> getLignes()
	{
		return m_lignes;
	}
	
	public Robot getRobot()
	{
		return m_robot;
	}
	
	public boolean findPath(int x, int y)
	{
		
		return true;
	}
}
