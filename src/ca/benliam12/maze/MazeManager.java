package ca.benliam12.maze;

public class MazeManager 
{
	private static MazeManager mazeManager = new MazeManager();
	
	public static MazeManager getInstance()
	{
		return mazeManager;
	}
	
}
