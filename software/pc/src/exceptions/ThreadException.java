package exceptions;

/**
 * Exception levée par les threads
 * @author pf
 *
 */
public class ThreadException extends Exception
{

	private static final long serialVersionUID = 3551305502065045527L;
	
	/**
	 * Exception levée par les threads
	 * @author pf
	 *
	 */
	public ThreadException()
	{
		super();
	}
	
	public ThreadException(String m)
	{
		super(m);
	}
}
