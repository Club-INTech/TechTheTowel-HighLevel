package pathDingDing;

/**
 * lien oriente entre deux noeuds
 * @author Etienne
 *
 */
public class Link
{
	private Node mDestination;
	private double mLenght;
	
	public Link(Node attached, Node destination)
	{
		mDestination = destination;
		mLenght = mDestination.distanceTo(attached);
	}
	
	public Node getDestination()
	{
		return mDestination;
	}
	
	public double getLength()
	{
		return mLenght;
	}
}
