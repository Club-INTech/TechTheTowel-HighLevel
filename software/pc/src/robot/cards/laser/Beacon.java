package robot.cards.laser;

/**
 * Classe des balises, utilisée par la classe Laser
 * @author pf
 *
 */

public class Beacon {

	public int id;
	public boolean active;
	
	public Beacon(int id, boolean active) {
		this.id = id;
		this.active = active;
	}
	
}
