package PathFinding;
import java.util.ArrayList;

public class Begin
{
	public static void main (String[] args)
	{
		Table table = new Table();
		new Fenetre(table);
		
		//PathFinder.findPath(new Node((int)(Math.random() * 2000), (int)(Math.random() * 3000)), new Node((int)(Math.random() * 2000), (int)(Math.random() * 3000)), table);
		//PathFinder.findPath(new Node(0, 0), new Node(100,100), table);
		/*
		Path path = new Path();
		path.addAll(PathFinder.findPath(new Node(1000, 200), new Node(100, 450), table));
		path.addAll(PathFinder.findPath(new Node(100, 450), new Node(100, 2550), table));
		path.addAll(PathFinder.findPath(new Node(100, 2550), new Node(1950, 2750), table));
		path.addAll(PathFinder.findPath(new Node(1950, 2750), new Node(1950, 2000), table));
		path.addAll(PathFinder.findPath(new Node(1950, 2000), new Node(1950, 1000), table));
		path.addAll(PathFinder.findPath(new Node(1950, 1000), new Node(1950, 250), table));
		path.addAll(PathFinder.findPath(new Node(1950, 250), new Node(1000, 300), table));
		table.getRobot().setPath(path);
		
		*/
		ArrayList<ObstacleRect> listeObst = new ArrayList<ObstacleRect>();
		listeObst.add(new ObstacleRect(778,0,22,400));
		listeObst.add(new ObstacleRect(800,0,400,70));
		listeObst.add(new ObstacleRect(1200,0,22,400));
		listeObst.add(new ObstacleRect(0,265,70,70));
		listeObst.add(new ObstacleRect(0,565,70,70));
		listeObst.add(new ObstacleRect(0,967,580,1066));
		listeObst.add(new ObstacleRect(1900,1200,100,600));
		listeObst.add(new ObstacleRect(0,2365,70,70));
		listeObst.add(new ObstacleRect(0,2665,70,70));
		listeObst.add(new ObstacleRect(778,2600,22,400));
		listeObst.add(new ObstacleRect(800,2930,400,70));
		listeObst.add(new ObstacleRect(1200,2600,22,400));
		
		
		double x1, x2, y1, y2;
		int tests = 0;
		long time = 0, start = 0, end = 0;
		for(int n = 1; n <= 10000; n++)
		{
			x1 = (int)(Math.random() * 2000);
			y1 = (int)(Math.random() * 3000);
			x2 = (int)(Math.random() * 2000);
			y2 = (int)(Math.random() * 3000);
			boolean isCorrect = true;
			for (int i = 0; i < listeObst.size(); i++)
			{
				if(listeObst.get(i).isInRect(x1, y1) || listeObst.get(i).isInRect(x2, y2))
					isCorrect = false;
			}
			if(isCorrect)
			{
				start = System.nanoTime();
				PathFinder.findPath(new Node(x1, y1), new Node(x2, y2), table);
				end = System.nanoTime();
				time += end - start;
				tests++;
			}
		}
	    System.out.println("Elapsed: "+(time) / tests+"ns");
	}
}