package exceptions.Locomotion;

import enums.UnableToMoveReason;
import smartMath.Vec2;

/**
 * Problème générique de déplacement du robot, que ce soit a cause d'un robot ennemi
 * (détecté par les capteurs) qui bloque le passage, ou d'un bloquage mécanique (type mur)
 * @author pf, marsu, theo
 *
 */
public class UnableToMoveException extends Exception
{

	/**
	 * La position où on voulais aller au moment de l'exception
	 */
	public Vec2 aim;
	
	/**
	 * La raison de l'exception
	 */
	public UnableToMoveReason reason;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8139322860107594266L;

	/**
	 * 
	 * @param aim  position où on voulais aller au moment de l'exception
	 * @param reason raison de l'exception
	 */
	public UnableToMoveException(Vec2 aim, UnableToMoveReason reason)
	{
		super();
		this.aim = aim;
		this.reason=reason;
	}
	
	/**
	 * @param aim  position où on voulais aller au moment de l'exception
	 * @param reason raison de l'exception
	 */
	public UnableToMoveException(String m, Vec2 aim, UnableToMoveReason reason)
	{
		super(m);
		this.aim = aim;
		this.reason=reason;
	}
	
}
 