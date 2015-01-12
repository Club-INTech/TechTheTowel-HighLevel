package exceptions;

/**
 * 
 * @author paul
 * exception lancee si on ne reussi pas a manger un plot, n'est pas sensee etre lancee hors du script GetPlot
 */
public class UnableToEatPlot extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -964578126117914433L;

	public UnableToEatPlot() {
		// TODO Auto-generated constructor stub
	}

	public UnableToEatPlot(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public UnableToEatPlot(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public UnableToEatPlot(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public UnableToEatPlot(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

}
